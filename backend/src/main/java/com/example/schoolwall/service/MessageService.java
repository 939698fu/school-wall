package com.example.schoolwall.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.schoolwall.dto.request.SendMessageRequest;
import com.example.schoolwall.dto.response.ConversationVO;
import com.example.schoolwall.dto.response.MessageVO;
import com.example.schoolwall.dto.response.SearchContactVO;
import com.example.schoolwall.entity.Message;

import java.util.List;

/**
 * 消息服务接口
 */
public interface MessageService extends IService<Message> {

    /**
     * 获取会话列表
     * @param userId 当前用户ID
     * @return 会话列表
     */
    List<ConversationVO> getConversations(Long userId);

    /**
     * 获取聊天记录
     * @param userId 当前用户ID
     * @param targetUserId 目标用户ID
     * @param page 页码
     * @param size 每页数量
     * @return 分页消息列表
     */
    IPage<MessageVO> getChatHistory(Long userId, Long targetUserId, Integer page, Integer size);

    /**
     * 发送消息
     * @param request 发送请求
     * @param userId 当前用户ID
     * @return 发送的消息
     */
    MessageVO sendMessage(SendMessageRequest request, Long userId);

    /**
     * 标记消息已读
     * @param userId 当前用户ID
     * @param targetUserId 目标用户ID
     */
    void markAsRead(Long userId, Long targetUserId);

    /**
     * 搜索联系人
     * @param userId 当前用户ID
     * @param keyword 搜索关键字
     * @return 匹配的联系人列表
     */
    List<SearchContactVO> searchContacts(Long userId, String keyword);
}