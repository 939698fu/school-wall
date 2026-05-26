package com.example.schoolwall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.schoolwall.dto.response.SearchPostVO;
import com.example.schoolwall.dto.response.SearchResponse;
import com.example.schoolwall.dto.response.SearchUserVO;
import com.example.schoolwall.entity.Post;
import com.example.schoolwall.entity.User;
import com.example.schoolwall.mapper.PostMapper;
import com.example.schoolwall.mapper.UserMapper;
import com.example.schoolwall.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 搜索服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final UserMapper userMapper;
    private final PostMapper postMapper;

    @Override
    public SearchResponse search(String keyword, Integer page, Integer size) {
        // 搜索用户（按昵称、用户名、学校搜索）
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.and(w -> w.like(User::getNickname, keyword)
                .or().like(User::getUsername, keyword)
                .or().like(User::getSchool, keyword))
                .orderByDesc(User::getCreateTime);
        
        List<User> users = userMapper.selectList(userWrapper);
        long userTotal = users.size();
        
        // 分页处理用户列表
        int userStart = (page - 1) * size;
        int userEnd = Math.min(userStart + size, users.size());
        List<User> pagedUsers = userStart < users.size() ? users.subList(userStart, userEnd) : List.of();

        // 搜索帖子（按标题、内容搜索）
        LambdaQueryWrapper<Post> postWrapper = new LambdaQueryWrapper<>();
        postWrapper.and(w -> w.like(Post::getTitle, keyword)
                .or().like(Post::getContent, keyword))
                .and(w -> w.eq(Post::getIsPrivate, 0).or().isNull(Post::getIsPrivate))
                .orderByDesc(Post::getCreateTime);
        
        List<Post> posts = postMapper.selectList(postWrapper);
        long postTotal = posts.size();
        
        // 分页处理帖子列表（从剩余空间开始）
        int userCount = pagedUsers.size();
        int remainingSize = size - userCount;
        int postStart = 0;
        int postEnd = Math.min(remainingSize, posts.size());
        List<Post> pagedPosts = postStart < posts.size() ? posts.subList(postStart, postEnd) : List.of();

        // 转换为VO
        List<SearchUserVO> userVOs = pagedUsers.stream()
                .map(this::convertToUserVO)
                .collect(Collectors.toList());

        List<SearchPostVO> postVOs = pagedPosts.stream()
                .map(this::convertToPostVO)
                .collect(Collectors.toList());

        return SearchResponse.builder()
                .users(userVOs)
                .posts(postVOs)
                .userTotal((int) userTotal)
                .postTotal((int) postTotal)
                .build();
    }

    /**
     * 转换用户实体为VO
     */
    private SearchUserVO convertToUserVO(User user) {
        return SearchUserVO.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .school(user.getSchool())
                .postCount(user.getPostCount())
                .build();
    }

    /**
     * 转换帖子实体为VO
     */
    private SearchPostVO convertToPostVO(Post post) {
        // 获取作者信息
        User author = userMapper.selectById(post.getUserId());
        String authorName = post.getIsAnon() == 1 ? "匿名用户" : (author != null ? author.getNickname() : "未知用户");

        // 截断内容（最多显示50个字符）
        String content = post.getContent();
        if (content != null && content.length() > 50) {
            content = content.substring(0, 50) + "...";
        }

        // 计算相对时间
        String relativeTime = calculateRelativeTime(post.getCreateTime());

        return SearchPostVO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(content)
                .author(authorName)
                .likes(post.getLikes())
                .commentCount(post.getComments())
                .time(relativeTime)
                .build();
    }

    /**
     * 计算相对时间
     */
    private String calculateRelativeTime(LocalDateTime createTime) {
        if (createTime == null) {
            return "未知";
        }

        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(createTime, now);

        if (minutes < 1) {
            return "刚刚";
        } else if (minutes < 60) {
            return minutes + "分钟前";
        } else if (minutes < 1440) { // 24小时
            return (minutes / 60) + "小时前";
        } else if (minutes < 10080) { // 7天
            return (minutes / 1440) + "天前";
        } else {
            return createTime.format(java.time.format.DateTimeFormatter.ofPattern("MM-dd"));
        }
    }
}
