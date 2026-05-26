package com.example.schoolwall.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.schoolwall.dto.request.CommentRequest;
import com.example.schoolwall.dto.response.CommentResponse;
import com.example.schoolwall.dto.response.CommentVO;
import com.example.schoolwall.dto.response.LikeResponse;
import com.example.schoolwall.entity.Comment;

/**
 * 评论服务接口
 */
public interface CommentService extends IService<Comment> {

    /**
     * 获取评论列表
     * @param postId 帖子ID
     * @param page 页码
     * @param size 每页数量
     * @param userId 当前用户ID（可选）
     * @return 分页评论列表
     */
    IPage<CommentVO> getCommentList(Long postId, Integer page, Integer size, Long userId);

    /**
     * 发布评论
     * @param request 评论请求
     * @param userId 当前用户ID
     * @return 评论信息
     */
    CommentResponse createComment(CommentRequest request, Long userId);

    /**
     * 点赞/取消点赞评论
     * @param commentId 评论ID
     * @param userId 当前用户ID
     * @return 点赞状态
     */
    LikeResponse toggleLike(Long commentId, Long userId);

    /**
     * 删除评论
     * @param commentId 评论ID
     * @param userId 当前用户ID
     */
    void deleteComment(Long commentId, Long userId);
}
