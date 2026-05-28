package com.example.schoolwall.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.schoolwall.common.Result;
import com.example.schoolwall.dto.request.LoginRequest;
import com.example.schoolwall.dto.request.RegisterRequest;
import com.example.schoolwall.dto.request.UpdateUserRequest;
import com.example.schoolwall.dto.request.WxLoginRequest;
import com.example.schoolwall.dto.response.FollowUserVO;
import com.example.schoolwall.dto.response.LoginResponse;
import com.example.schoolwall.dto.response.UserVO;
import com.example.schoolwall.entity.FollowRecord;
import com.example.schoolwall.entity.User;
import com.example.schoolwall.common.BusinessException;
import com.example.schoolwall.mapper.FollowRecordMapper;
import com.example.schoolwall.mapper.UserMapper;
import com.example.schoolwall.service.AuthService;
import com.example.schoolwall.service.FollowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
@Tag(name = "用户模块", description = "用户登录、注册、信息管理")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private final AuthService authService;

    @Autowired
    private final UserMapper userMapper;

    @Autowired
    private final FollowRecordMapper followRecordMapper;

    @Autowired
    private final FollowService followService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "注册新用户")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return Result.success();
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户名密码登录")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return Result.success("登录成功", response);
    }

    /**
     * 微信小程序登录
     */
    @PostMapping("/wx-login")
    @Operation(summary = "微信登录", description = "使用 uni.login 获取的 code 登录，自动注册新用户")
    public Result<LoginResponse> wxLogin(@Valid @RequestBody WxLoginRequest request) {
        LoginResponse response = authService.wxLogin(request);
        return Result.success("登录成功", response);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    public Result<UserVO> getCurrentUser(@RequestAttribute("userId") Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw BusinessException.notFound("用户不存在");
        }
        return Result.success(buildUserVO(user, null));
    }

    /**
     * 获取指定用户信息
     */
    @GetMapping("/{userId}")
    @Operation(summary = "获取指定用户信息", description = "获取其他用户的公开信息（完善版：包含统计数据）")
    public Result<UserVO> getUserInfo(
            @PathVariable Long userId,
            @RequestAttribute(value = "userId", required = false) Long currentUserId) {

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw BusinessException.notFound("用户不存在");
        }

        Boolean isFollowed = null;
        if (currentUserId != null) {
            LambdaQueryWrapper<FollowRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(FollowRecord::getUserId, currentUserId)
                   .eq(FollowRecord::getFollowedId, userId);
            isFollowed = followRecordMapper.selectOne(wrapper) != null;
        }

        return Result.success(buildUserVO(user, isFollowed));
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/info")
    @Operation(summary = "更新用户信息", description = "更新当前登录用户的个人信息")
    public Result<UserVO> updateUserInfo(
            @RequestAttribute("userId") Long userId,
            @Valid @RequestBody UpdateUserRequest request) {

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw BusinessException.notFound("用户不存在");
        }

        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getSchool() != null) {
            user.setSchool(request.getSchool());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }

        userMapper.updateById(user);
        log.info("用户信息更新成功: userId={}", userId);

        return Result.success("更新成功", buildUserVO(user, null));
    }

    /**
     * 关注用户
     */
    @PostMapping("/{userId}/follow")
    @Operation(summary = "关注用户", description = "关注指定用户")
    @org.springframework.transaction.annotation.Transactional
    public Result<Void> follow(
            @RequestAttribute("userId") Long currentUserId,
            @PathVariable Long userId) {

        if (currentUserId.equals(userId)) {
            throw BusinessException.badRequest("不能关注自己");
        }

        User followed = userMapper.selectById(userId);
        if (followed == null) {
            throw BusinessException.notFound("用户不存在");
        }

        LambdaQueryWrapper<FollowRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FollowRecord::getUserId, currentUserId)
               .eq(FollowRecord::getFollowedId, userId);
        if (followRecordMapper.selectOne(wrapper) != null) {
            throw BusinessException.badRequest("已关注该用户");
        }

        FollowRecord followRecord = new FollowRecord();
        followRecord.setUserId(currentUserId);
        followRecord.setFollowedId(userId);
        followRecordMapper.insert(followRecord);

        // 同步统计字段
        User follower = userMapper.selectById(currentUserId);
        if (follower != null) {
            follower.setFollowingCount(safeIncrement(follower.getFollowingCount()));
            userMapper.updateById(follower);
        }
        followed.setFollowerCount(safeIncrement(followed.getFollowerCount()));
        userMapper.updateById(followed);

        return Result.success();
    }

    /**
     * 取消关注
     */
    @DeleteMapping("/{userId}/follow")
    @Operation(summary = "取消关注", description = "取消关注指定用户")
    @org.springframework.transaction.annotation.Transactional
    public Result<Void> unfollow(
            @RequestAttribute("userId") Long currentUserId,
            @PathVariable Long userId) {

        LambdaQueryWrapper<FollowRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FollowRecord::getUserId, currentUserId)
               .eq(FollowRecord::getFollowedId, userId);

        int deleted = followRecordMapper.delete(wrapper);
        if (deleted == 0) {
            throw BusinessException.badRequest("未关注该用户");
        }

        // 同步统计字段
        User follower = userMapper.selectById(currentUserId);
        if (follower != null) {
            follower.setFollowingCount(safeDecrement(follower.getFollowingCount()));
            userMapper.updateById(follower);
        }
        User followed = userMapper.selectById(userId);
        if (followed != null) {
            followed.setFollowerCount(safeDecrement(followed.getFollowerCount()));
            userMapper.updateById(followed);
        }

        return Result.success();
    }

    private int safeIncrement(Integer current) {
        return (current == null ? 0 : current) + 1;
    }

    private int safeDecrement(Integer current) {
        return Math.max(0, (current == null ? 0 : current) - 1);
    }

    /**
     * 获取粉丝列表
     */
    @GetMapping("/{userId}/followers")
    @Operation(summary = "获取粉丝列表", description = "获取指定用户的粉丝列表")
    public Result<List<FollowUserVO>> getFollowers(
            @PathVariable Long userId,
            @RequestAttribute(value = "userId", required = false) Long currentUserId) {

        if (userMapper.selectById(userId) == null) {
            throw BusinessException.notFound("用户不存在");
        }

        List<FollowUserVO> result = followService.getFollowers(userId, currentUserId);
        return Result.success(result);
    }

    /**
     * 获取关注列表
     */
    @GetMapping("/{userId}/following")
    @Operation(summary = "获取关注列表", description = "获取指定用户关注的用户列表")
    public Result<List<FollowUserVO>> getFollowing(
            @PathVariable Long userId,
            @RequestAttribute(value = "userId", required = false) Long currentUserId) {

        if (userMapper.selectById(userId) == null) {
            throw BusinessException.notFound("用户不存在");
        }

        List<FollowUserVO> result = followService.getFollowing(userId, currentUserId);
        return Result.success(result);
    }

    /**
     * 搜索用户
     */
    @GetMapping("/search")
    @Operation(summary = "搜索用户", description = "搜索用户名、昵称、学校")
    public Result<List<FollowUserVO>> searchUsers(
            @RequestAttribute(value = "userId", required = false) Long currentUserId,
            @Parameter(description = "搜索关键字") @RequestParam String keyword) {

        if (keyword == null || keyword.trim().isEmpty()) {
            return Result.success(List.of());
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.like(User::getNickname, keyword)
                          .or().like(User::getUsername, keyword)
                          .or().like(User::getSchool, keyword))
               .last("LIMIT 20");

        List<User> users = userMapper.selectList(wrapper);

        Set<Long> currentUserFollowing = null;
        if (currentUserId != null) {
            LambdaQueryWrapper<FollowRecord> followingWrapper = new LambdaQueryWrapper<>();
            followingWrapper.eq(FollowRecord::getUserId, currentUserId);
            List<FollowRecord> followingRecords = followRecordMapper.selectList(followingWrapper);
            currentUserFollowing = followingRecords.stream()
                    .map(FollowRecord::getFollowedId)
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
                        .followerCount(user.getFollowerCount())
                        .followingCount(user.getFollowingCount())
                        .postCount(user.getPostCount())
                        .likeCount(user.getLikeCount())
                        .isFollowed(followingSet != null && followingSet.contains(user.getId()))
                        .createTime(user.getCreateTime())
                        .build())
                .collect(Collectors.toList());

        return Result.success(result);
    }

    private UserVO buildUserVO(User user, Boolean isFollowed) {
        return UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .school(user.getSchool())
                .bio(user.getBio())
                .postCount(user.getPostCount())
                .likeCount(user.getLikeCount())
                .collectCount(user.getCollectCount())
                .followerCount(user.getFollowerCount())
                .followingCount(user.getFollowingCount())
                .isFollowed(isFollowed)
                .createTime(user.getCreateTime())
                .build();
    }
}