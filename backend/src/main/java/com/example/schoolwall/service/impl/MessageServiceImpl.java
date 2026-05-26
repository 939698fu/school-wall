package com.example.schoolwall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.schoolwall.dto.request.SendMessageRequest;
import com.example.schoolwall.dto.response.ConversationVO;
import com.example.schoolwall.dto.response.MessageVO;
import com.example.schoolwall.dto.response.SearchContactVO;
import com.example.schoolwall.entity.Message;
import com.example.schoolwall.entity.User;
import com.example.schoolwall.common.BusinessException;
import com.example.schoolwall.mapper.MessageMapper;
import com.example.schoolwall.mapper.UserMapper;
import com.example.schoolwall.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 消息服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    private final UserMapper userMapper;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public List<ConversationVO> getConversations(Long userId) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(Message::getFromId, userId).or().eq(Message::getToId, userId))
               .orderByDesc(Message::getCreateTime);

        List<Message> messages = baseMapper.selectList(wrapper);

        Map<Long, Message> lastMessages = new HashMap<>();
        Map<Long, Integer> unreadCounts = new HashMap<>();

        for (Message message : messages) {
            Long targetUserId = message.getFromId().equals(userId) ? message.getToId() : message.getFromId();

            Message existing = lastMessages.get(targetUserId);
            if (existing == null || message.getCreateTime().isAfter(existing.getCreateTime())) {
                lastMessages.put(targetUserId, message);
            }

            if (message.getToId().equals(userId) && message.getIsRead() == 0) {
                unreadCounts.merge(targetUserId, 1, Integer::sum);
            }
        }

        List<Long> targetUserIds = new ArrayList<>(lastMessages.keySet());
        List<User> users = userMapper.selectBatchIds(targetUserIds);
        Map<Long, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        List<ConversationVO> conversations = new ArrayList<>();
        for (Map.Entry<Long, Message> entry : lastMessages.entrySet()) {
            Long targetUserId = entry.getKey();
            Message lastMsg = entry.getValue();
            User user = userMap.get(targetUserId);

            conversations.add(ConversationVO.builder()
                    .id(targetUserId)
                    .userId(targetUserId)
                    .name(user != null ? user.getNickname() : "未知用户")
                    .avatar(user != null ? user.getAvatar() : "👤")
                    .lastMsg(truncateContent(lastMsg))
                    .lastTime(calculateRelativeTime(lastMsg.getCreateTime()))
                    .unread(unreadCounts.getOrDefault(targetUserId, 0))
                    .build());
        }

        conversations.sort((a, b) -> b.getLastTime().compareTo(a.getLastTime()));

        return conversations;
    }

    @Override
    public IPage<MessageVO> getChatHistory(Long userId, Long targetUserId, Integer page, Integer size) {
        User targetUser = userMapper.selectById(targetUserId);
        if (targetUser == null) {
            throw BusinessException.notFound("用户不存在");
        }

        Page<Message> pageParam = new Page<>(page, size);

        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(Message::getFromId, userId).eq(Message::getToId, targetUserId)
                        .or().eq(Message::getFromId, targetUserId).eq(Message::getToId, userId))
               .orderByDesc(Message::getCreateTime);

        IPage<Message> messagePage = baseMapper.selectPage(pageParam, wrapper);

        IPage<MessageVO> resultPage = new Page<>(page, size);
        resultPage.setTotal(messagePage.getTotal());
        resultPage.setRecords(messagePage.getRecords().stream()
                .map(message -> convertToVO(message, userId))
                .collect(Collectors.toList()));

        markAsRead(userId, targetUserId);

        return resultPage;
    }

    @Override
    @Transactional
    public MessageVO sendMessage(SendMessageRequest request, Long userId) {
        User targetUser = userMapper.selectById(request.getToId());
        if (targetUser == null) {
            throw BusinessException.notFound("接收用户不存在");
        }

        String type = request.getType() != null ? request.getType() : "text";

        if ("image".equals(type)) {
            if (request.getFileUrl() == null || request.getFileUrl().isEmpty()) {
                throw BusinessException.badRequest("图片消息必须提供图片URL");
            }
        } else {
            if (request.getContent() == null || request.getContent().isEmpty()) {
                throw BusinessException.badRequest("文本消息必须提供内容");
            }
        }

        Message message = new Message();
        message.setFromId(userId);
        message.setToId(request.getToId());
        message.setContent(request.getContent());
        message.setType(type);
        message.setFileUrl(request.getFileUrl());
        message.setIsRead(0);

        baseMapper.insert(message);

        log.info("消息发送成功: fromId={}, toId={}, type={}", userId, request.getToId(), type);

        return MessageVO.builder()
                .id(message.getId())
                .fromId(userId)
                .toId(request.getToId())
                .content(request.getContent())
                .type(type)
                .fileUrl(request.getFileUrl())
                .time("刚刚")
                .fullTime(message.getCreateTime() != null ? message.getCreateTime().format(FORMATTER) : null)
                .fromMe(true)
                .build();
    }

    @Override
    @Transactional
    public void markAsRead(Long userId, Long targetUserId) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getFromId, targetUserId)
               .eq(Message::getToId, userId)
               .eq(Message::getIsRead, 0);

        Message updateMsg = new Message();
        updateMsg.setIsRead(1);

        baseMapper.update(updateMsg, wrapper);

        log.info("消息已读标记: userId={}, targetUserId={}", userId, targetUserId);
    }

    @Override
    public List<SearchContactVO> searchContacts(Long userId, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String likeKeyword = "%" + keyword.trim() + "%";

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.like(User::getNickname, keyword)
                          .or().like(User::getUsername, keyword)
                          .or().like(User::getSchool, keyword))
               .ne(User::getId, userId)
               .last("LIMIT 20");

        List<User> users = userMapper.selectList(wrapper);

        Map<Long, Message> lastMessages = new HashMap<>();
        Map<Long, Integer> unreadCounts = new HashMap<>();

        for (User user : users) {
            LambdaQueryWrapper<Message> msgWrapper = new LambdaQueryWrapper<>();
            msgWrapper.and(w -> w.eq(Message::getFromId, userId).eq(Message::getToId, user.getId())
                            .or().eq(Message::getFromId, user.getId()).eq(Message::getToId, userId))
                   .orderByDesc(Message::getCreateTime)
                   .last("LIMIT 1");

            Message lastMsg = baseMapper.selectOne(msgWrapper);
            if (lastMsg != null) {
                lastMessages.put(user.getId(), lastMsg);

                LambdaQueryWrapper<Message> unreadWrapper = new LambdaQueryWrapper<>();
                unreadWrapper.eq(Message::getFromId, user.getId())
                       .eq(Message::getToId, userId)
                       .eq(Message::getIsRead, 0);
                unreadCounts.put(user.getId(), Math.toIntExact(baseMapper.selectCount(unreadWrapper)));
            }
        }

        return users.stream().map(user -> {
            Message lastMsg = lastMessages.get(user.getId());
            return SearchContactVO.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .nickname(user.getNickname())
                    .avatar(user.getAvatar())
                    .school(user.getSchool())
                    .bio(user.getBio())
                    .lastMessage(lastMsg != null ? truncateContent(lastMsg) : null)
                    .lastMessageTime(lastMsg != null ? calculateRelativeTime(lastMsg.getCreateTime()) : null)
                    .unreadCount(unreadCounts.getOrDefault(user.getId(), 0))
                    .build();
        }).collect(Collectors.toList());
    }

    private MessageVO convertToVO(Message message, Long userId) {
        return MessageVO.builder()
                .id(message.getId())
                .fromId(message.getFromId())
                .toId(message.getToId())
                .content(message.getContent())
                .type(message.getType())
                .fileUrl(message.getFileUrl())
                .time(message.getCreateTime() != null ? message.getCreateTime().format(TIME_FORMATTER) : null)
                .fullTime(message.getCreateTime() != null ? message.getCreateTime().format(FORMATTER) : null)
                .fromMe(message.getFromId().equals(userId))
                .build();
    }

    private String truncateContent(Message message) {
        if ("image".equals(message.getType())) {
            return "[图片]";
        }
        String content = message.getContent();
        if (content != null && content.length() > 30) {
            return content.substring(0, 30) + "...";
        }
        return content;
    }

    private String calculateRelativeTime(LocalDateTime createTime) {
        if (createTime == null) {
            return "未知";
        }

        LocalDateTime now = LocalDateTime.now();
        long minutes = java.time.Duration.between(createTime, now).toMinutes();

        if (minutes < 1) {
            return "刚刚";
        } else if (minutes < 60) {
            return minutes + "分钟前";
        } else if (minutes < 1440) {
            return (minutes / 60) + "小时前";
        } else if (minutes < 10080) {
            return (minutes / 1440) + "天前";
        } else {
            return createTime.format(FORMATTER);
        }
    }
}