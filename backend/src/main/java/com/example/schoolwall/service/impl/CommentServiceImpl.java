package com.example.schoolwall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.schoolwall.dto.request.CommentRequest;
import com.example.schoolwall.dto.response.CommentResponse;
import com.example.schoolwall.dto.response.CommentVO;
import com.example.schoolwall.dto.response.LikeResponse;
import com.example.schoolwall.entity.Comment;
import com.example.schoolwall.entity.LikeRecord;
import com.example.schoolwall.entity.Post;
import com.example.schoolwall.entity.User;
import com.example.schoolwall.common.BusinessException;
import com.example.schoolwall.mapper.CommentMapper;
import com.example.schoolwall.mapper.LikeRecordMapper;
import com.example.schoolwall.mapper.PostMapper;
import com.example.schoolwall.mapper.UserMapper;
import com.example.schoolwall.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 评论服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    private final UserMapper userMapper;
    private final PostMapper postMapper;
    private final LikeRecordMapper likeRecordMapper;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public IPage<CommentVO> getCommentList(Long postId, Integer page, Integer size, Long userId) {
        // 检查帖子是否存在
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw BusinessException.notFound("帖子不存在");
        }

        Page<Comment> pageParam = new Page<>(page, size);
        
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getPostId, postId)
               .orderByDesc(Comment::getCreateTime);
        
        IPage<Comment> commentPage = baseMapper.selectPage(pageParam, wrapper);
        
        // 转换为VO
        return commentPage.convert(comment -> convertToVO(comment, userId, post.getUserId()));
    }

    @Override
    @Transactional
    public CommentResponse createComment(CommentRequest request, Long userId) {
        // 检查帖子是否存在
        Post post = postMapper.selectById(request.getPostId());
        if (post == null) {
            throw BusinessException.notFound("帖子不存在");
        }

        // 获取用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw BusinessException.unauthorized("用户不存在");
        }

        // 创建评论
        Comment comment = new Comment();
        comment.setPostId(request.getPostId());
        comment.setUserId(userId);
        comment.setContent(request.getContent());
        comment.setLikes(0);
        
        baseMapper.insert(comment);

        // 更新帖子评论数
        post.setComments(post.getComments() + 1);
        postMapper.updateById(post);

        log.info("评论发布成功: userId={}, postId={}, commentId={}", userId, request.getPostId(), comment.getId());

        return CommentResponse.builder()
                .id(comment.getId())
                .postId(request.getPostId())
                .content(request.getContent())
                .time("刚刚")
                .build();
    }

    @Override
    @Transactional
    public LikeResponse toggleLike(Long commentId, Long userId) {
        Comment comment = baseMapper.selectById(commentId);
        if (comment == null) {
            throw BusinessException.notFound("评论不存在");
        }

        // 检查是否已点赞
        LambdaQueryWrapper<LikeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LikeRecord::getCommentId, commentId)
               .eq(LikeRecord::getUserId, userId);
        LikeRecord existing = likeRecordMapper.selectOne(wrapper);

        boolean liked;
        int likes;

        if (existing != null) {
            // 取消点赞
            likeRecordMapper.deleteById(existing.getId());
            likes = comment.getLikes() - 1;
            liked = false;
        } else {
            // 添加点赞
            LikeRecord record = new LikeRecord();
            record.setUserId(userId);
            record.setCommentId(commentId);
            likeRecordMapper.insert(record);
            likes = comment.getLikes() + 1;
            liked = true;
        }

        // 更新评论点赞数
        comment.setLikes(likes);
        baseMapper.updateById(comment);

        return LikeResponse.builder()
                .liked(liked)
                .likes(likes)
                .build();
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = baseMapper.selectById(commentId);
        if (comment == null) {
            throw BusinessException.notFound("评论不存在");
        }

        // 检查权限：必须是自己的评论或帖子作者
        Post post = postMapper.selectById(comment.getPostId());
        if (post == null) {
            throw BusinessException.notFound("帖子不存在");
        }

        boolean isOwner = comment.getUserId().equals(userId);
        boolean isPostAuthor = post.getUserId().equals(userId);

        if (!isOwner && !isPostAuthor) {
            throw BusinessException.forbidden("无权删除该评论");
        }

        // 删除评论
        baseMapper.deleteById(commentId);

        // 删除相关点赞记录
        likeRecordMapper.delete(new LambdaQueryWrapper<LikeRecord>()
                .eq(LikeRecord::getCommentId, commentId));

        // 更新帖子评论数
        post.setComments(Math.max(0, post.getComments() - 1));
        postMapper.updateById(post);

        log.info("评论删除成功: userId={}, commentId={}", userId, commentId);
    }

    /**
     * 转换评论实体为VO
     */
    private CommentVO convertToVO(Comment comment, Long userId, Long postAuthorId) {
        User user = userMapper.selectById(comment.getUserId());
        String author = user != null ? user.getNickname() : "未知用户";
        String avatar = user != null ? user.getAvatar() : "👤";

        // 检查是否已点赞
        Boolean liked = false;
        if (userId != null) {
            LambdaQueryWrapper<LikeRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(LikeRecord::getCommentId, comment.getId())
                   .eq(LikeRecord::getUserId, userId);
            liked = likeRecordMapper.selectOne(wrapper) != null;
        }

        // 是否是帖子作者
        Boolean isAuthor = comment.getUserId().equals(postAuthorId);

        return CommentVO.builder()
                .id(comment.getId())
                .postId(comment.getPostId())
                .author(author)
                .authorAvatar(avatar)
                .authorId(comment.getUserId())
                .content(comment.getContent())
                .time(comment.getCreateTime() != null ? comment.getCreateTime().format(TIME_FORMATTER) : null)
                .fullTime(comment.getCreateTime() != null ? comment.getCreateTime().format(FORMATTER) : null)
                .likes(comment.getLikes())
                .liked(liked)
                .isAuthor(isAuthor)
                .createTime(comment.getCreateTime())
                .build();
    }
}
