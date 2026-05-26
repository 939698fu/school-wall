package com.example.schoolwall.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.schoolwall.common.Result;
import com.example.schoolwall.dto.request.CommentRequest;
import com.example.schoolwall.dto.response.CommentResponse;
import com.example.schoolwall.dto.response.CommentVO;
import com.example.schoolwall.dto.response.LikeResponse;
import com.example.schoolwall.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 评论控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/comments")
@Tag(name = "评论模块", description = "评论管理")
@RequiredArgsConstructor
public class CommentController {

    @Autowired
    private final CommentService commentService;

    /**
     * 获取评论列表
     */
    @GetMapping
    @Operation(summary = "获取评论列表", description = "获取帖子的评论列表")
    public Result<IPage<CommentVO>> getCommentList(
            @Parameter(description = "帖子ID", required = true) @RequestParam Long postId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") Integer size,
            @RequestAttribute(value = "userId", required = false) Long userId) {
        
        IPage<CommentVO> result = commentService.getCommentList(postId, page, size, userId);
        return Result.success(result);
    }

    /**
     * 发布评论
     */
    @PostMapping
    @Operation(summary = "发布评论", description = "发布新评论")
    public Result<CommentResponse> createComment(
            @Valid @RequestBody CommentRequest request,
            @RequestAttribute("userId") Long userId) {
        
        CommentResponse response = commentService.createComment(request, userId);
        return Result.success("评论成功", response);
    }

    /**
     * 点赞评论
     */
    @PostMapping("/{commentId}/like")
    @Operation(summary = "点赞评论", description = "点赞/取消点赞评论")
    public Result<LikeResponse> toggleLike(
            @Parameter(description = "评论ID") @PathVariable Long commentId,
            @RequestAttribute("userId") Long userId) {
        
        LikeResponse response = commentService.toggleLike(commentId, userId);
        String message = response.getLiked() ? "点赞成功" : "取消点赞成功";
        return Result.success(message, response);
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/{commentId}")
    @Operation(summary = "删除评论", description = "删除评论（仅限自己的评论或帖子作者）")
    public Result<Void> deleteComment(
            @Parameter(description = "评论ID") @PathVariable Long commentId,
            @RequestAttribute("userId") Long userId) {
        
        commentService.deleteComment(commentId, userId);
        return Result.success();
    }
}
