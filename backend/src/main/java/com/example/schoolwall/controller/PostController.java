package com.example.schoolwall.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.schoolwall.common.BusinessException;
import com.example.schoolwall.common.Result;
import com.example.schoolwall.dto.request.PostRequest;
import com.example.schoolwall.dto.response.*;
import com.example.schoolwall.entity.Post;
import com.example.schoolwall.entity.User;
import com.example.schoolwall.mapper.PostMapper;
import com.example.schoolwall.mapper.UserMapper;
import com.example.schoolwall.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 帖子控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/posts")
@Tag(name = "帖子模块", description = "帖子管理")
@RequiredArgsConstructor
public class PostController {

    @Autowired
    private final PostService postService;

    @Autowired
    private final PostMapper postMapper;

    @Autowired
    private final UserMapper userMapper;

    /**
     * 获取帖子列表
     */
    @GetMapping
    @Operation(summary = "获取帖子列表", description = "获取帖子列表（支持分页和分类筛选）")
    public Result<IPage<PostVO>> getPostList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "排序类型：latest(最新)、hot(热门)、hole(树洞)、love(表白)") @RequestParam(defaultValue = "latest") String type,
            @Parameter(description = "话题标签筛选") @RequestParam(required = false) String tag,
            @RequestAttribute(value = "userId", required = false) Long userId) {

        IPage<PostVO> result = postService.getPostList(page, size, type, tag, userId);
        return Result.success(result);
    }

    /**
     * 获取帖子列表（游标分页 - 用于无限滚动）
     */
    @GetMapping("/cursor")
    @Operation(summary = "获取帖子列表(游标分页)", description = "用于无限滚动的游标分页接口，首次请求cursor传null，之后传入返回的nextCursor")
    public Result<CursorPageVO<PostVO>> getPostListByCursor(
            @Parameter(description = "游标时间戳（首次请求为空，之后传入返回的nextCursor）") @RequestParam(required = false) String cursor,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "排序类型：latest(最新)、hot(热门)、hole(树洞)、love(表白)") @RequestParam(defaultValue = "latest") String type,
            @Parameter(description = "话题标签筛选") @RequestParam(required = false) String tag,
            @RequestAttribute(value = "userId", required = false) Long userId) {

        Long parsedCursor = parseCursor(cursor);
        CursorPageVO<PostVO> result = postService.getPostListByCursor(parsedCursor, size, type, tag, userId);
        return Result.success(result);
    }

    private Long parseCursor(String cursor) {
        if (cursor == null) {
            return null;
        }
        String value = cursor.trim();
        if (value.isEmpty() || "null".equalsIgnoreCase(value) || "undefined".equalsIgnoreCase(value)) {
            return null;
        }
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException ex) {
            throw BusinessException.badRequest("cursor参数格式错误");
        }
    }

    /**
     * 获取帖子详情
     */
    @GetMapping("/{postId}")
    @Operation(summary = "获取帖子详情", description = "获取帖子详情及评论列表")
    public Result<PostDetailVO> getPostDetail(
            @Parameter(description = "帖子ID") @PathVariable Long postId,
            @RequestAttribute(value = "userId", required = false) Long userId) {

        PostDetailVO detail = postService.getPostDetail(postId, userId);
        return Result.success(detail);
    }

    /**
     * 发布帖子
     */
    @PostMapping
    @Operation(summary = "发布帖子", description = "发布新帖子")
    public Result<PostVO> createPost(
            @Valid @RequestBody PostRequest request,
            @RequestAttribute(value = "userId", required = false) Long userId) {

        if (userId == null) {
            return Result.unauthorized("请先登录", (PostVO) null);
        }

        PostVO post = postService.createPost(request, userId);
        return Result.success("发布成功", post);
    }

    /**
     * 点赞帖子
     */
    @PostMapping("/{postId}/like")
    @Operation(summary = "点赞帖子", description = "点赞/取消点赞帖子")
    public Result<LikeResponse> toggleLike(
            @Parameter(description = "帖子ID") @PathVariable Long postId,
            @RequestAttribute(value = "userId", required = false) Long userId) {

        if (userId == null) {
            return Result.unauthorized("请先登录", (LikeResponse) null);
        }

        LikeResponse response = postService.toggleLike(postId, userId);
        String message = response.getLiked() ? "点赞成功" : "取消点赞成功";
        return Result.success(message, response);
    }

    /**
     * 收藏帖子
     */
    @PostMapping("/{postId}/collect")
    @Operation(summary = "收藏帖子", description = "收藏/取消收藏帖子")
    public Result<CollectResponse> toggleCollect(
            @Parameter(description = "帖子ID") @PathVariable Long postId,
            @RequestAttribute(value = "userId", required = false) Long userId) {

        if (userId == null) {
            return Result.unauthorized("请先登录", (CollectResponse) null);
        }

        CollectResponse response = postService.toggleCollect(postId, userId);
        String message = response.getCollected() ? "收藏成功" : "取消收藏成功";
        return Result.success(message, response);
    }

    /**
     * 删除帖子
     */
    @DeleteMapping("/{postId}")
    @Operation(summary = "删除帖子", description = "删除帖子（仅限自己的帖子）")
    public Result<Void> deletePost(
            @Parameter(description = "帖子ID") @PathVariable Long postId,
            @RequestAttribute(value = "userId", required = false) Long userId) {

        if (userId == null) {
            return Result.unauthorized("请先登录");
        }

        postService.deletePost(postId, userId);
        return Result.success();
    }

    /**
     * 获取我的帖子
     */
    @GetMapping("/mine")
    @Operation(summary = "获取我的帖子", description = "获取当前用户发布的帖子")
    public Result<IPage<PostVO>> getMyPosts(
            @RequestAttribute(value = "userId", required = false) Long userId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size) {

        if (userId == null) {
            return Result.unauthorized("请先登录", (IPage<PostVO>) null);
        }

        IPage<PostVO> result = postService.getMyPosts(userId, page, size);
        return Result.success(result);
    }

    /**
     * 获取指定用户的帖子
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "获取指定用户的帖子", description = "获取指定用户的公开帖子列表")
    public Result<IPage<PostVO>> getUserPosts(
            @PathVariable Long userId,
            @RequestAttribute(value = "userId", required = false) Long currentUserId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size) {

        if (userMapper.selectById(userId) == null) {
            throw BusinessException.notFound("用户不存在");
        }

        IPage<PostVO> result = postService.getUserPosts(userId, currentUserId, page, size);
        return Result.success(result);
    }

    /**
     * 获取我的收藏
     */
    @GetMapping("/collections")
    @Operation(summary = "获取我的收藏", description = "获取当前用户收藏的帖子")
    public Result<IPage<PostVO>> getMyCollections(
            @RequestAttribute(value = "userId", required = false) Long userId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size) {

        if (userId == null) {
            return Result.unauthorized("请先登录", (IPage<PostVO>) null);
        }

        IPage<PostVO> result = postService.getMyCollections(userId, page, size);
        return Result.success(result);
    }

    /**
     * 获取热门话题标签
     */
    @GetMapping("/tags")
    @Operation(summary = "获取热门话题标签", description = "获取使用次数最多的标签列表")
    public Result<List<TagVO>> getHotTags(
            @Parameter(description = "返回数量") @RequestParam(defaultValue = "10") Integer limit) {

        List<Post> posts = postMapper.selectList(null);

        Map<String, Integer> tagCountMap = new HashMap<>();
        for (Post post : posts) {
            if (post.getTag() != null && !post.getTag().isEmpty()) {
                tagCountMap.merge(post.getTag(), 1, Integer::sum);
            }
        }

        List<TagVO> tags = tagCountMap.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> TagVO.builder()
                        .name(entry.getKey())
                        .color(getTagColor(entry.getKey()))
                        .count(entry.getValue())
                        .build())
                .collect(Collectors.toList());

        return Result.success(tags);
    }

    /**
     * 搜索可@的用户
     */
    @GetMapping("/mentioned")
    @Operation(summary = "搜索可@的用户", description = "搜索可被@的用户列表")
    public Result<List<FollowUserVO>> searchMentionedUsers(
            @RequestAttribute(value = "userId", required = false) Long currentUserId,
            @Parameter(description = "搜索关键字") @RequestParam String keyword) {

        if (keyword == null || keyword.trim().isEmpty()) {
            return Result.success(List.of());
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.like(User::getNickname, keyword)
                          .or().like(User::getUsername, keyword))
               .last("LIMIT 10");

        List<User> users = userMapper.selectList(wrapper);

        Set<Long> currentUserFollowing = null;
        if (currentUserId != null) {
            List<FollowUserVO> following = postService.getFollowingUsers(currentUserId);
            currentUserFollowing = following.stream()
                    .map(FollowUserVO::getId)
                    .collect(Collectors.toSet());
        }

        final Set<Long> followingSet = currentUserFollowing;
        List<FollowUserVO> result = users.stream()
                .map(user -> FollowUserVO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .nickname(user.getNickname())
                        .avatar(user.getAvatar())
                        .school(user.getSchool())
                        .bio(user.getBio())
                        .isFollowed(followingSet != null && followingSet.contains(user.getId()))
                        .build())
                .collect(Collectors.toList());

        return Result.success(result);
    }

    private String getTagColor(String tag) {
        Map<String, String> colorMap = new HashMap<>();
        colorMap.put("校园生活", "blue");
        colorMap.put("学习", "green");
        colorMap.put("美食", "orange");
        colorMap.put("运动", "cyan");
        colorMap.put("音乐", "purple");
        colorMap.put("表白", "red");
        colorMap.put("吐槽", "gray");
        colorMap.put("资源", "gold");
        colorMap.put("问答", "geekblue");
        colorMap.put("树洞", "magenta");
        return colorMap.getOrDefault(tag, "blue");
    }
}
