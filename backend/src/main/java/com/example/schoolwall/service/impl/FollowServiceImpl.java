package com.example.schoolwall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.schoolwall.dto.response.FollowUserVO;
import com.example.schoolwall.entity.FollowRecord;
import com.example.schoolwall.entity.User;
import com.example.schoolwall.mapper.FollowRecordMapper;
import com.example.schoolwall.mapper.UserMapper;
import com.example.schoolwall.service.FollowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 关注服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FollowServiceImpl extends ServiceImpl<FollowRecordMapper, FollowRecord> implements FollowService {

    private final UserMapper userMapper;

    @Override
    public List<FollowUserVO> getFollowers(Long userId, Long currentUserId) {
        LambdaQueryWrapper<FollowRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FollowRecord::getFollowedId, userId);

        List<FollowRecord> records = baseMapper.selectList(wrapper);

        if (records.isEmpty()) {
            return List.of();
        }

        List<Long> followerIds = records.stream()
                .map(FollowRecord::getUserId)
                .collect(Collectors.toList());

        List<User> users = userMapper.selectBatchIds(followerIds);
        Map<Long, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        Set<Long> currentUserFollowing = null;
        if (currentUserId != null) {
            LambdaQueryWrapper<FollowRecord> followingWrapper = new LambdaQueryWrapper<>();
            followingWrapper.eq(FollowRecord::getUserId, currentUserId);
            List<FollowRecord> followingRecords = baseMapper.selectList(followingWrapper);
            currentUserFollowing = followingRecords.stream()
                    .map(FollowRecord::getFollowedId)
                    .collect(Collectors.toSet());
        }

        final Set<Long> followingSet = currentUserFollowing;
        return followerIds.stream()
                .map(followerId -> {
                    User user = userMap.get(followerId);
                    if (user == null) {
                        return null;
                    }
                    return buildFollowUserVO(user,
                            followingSet != null && followingSet.contains(followerId));
                })
                .filter(v -> v != null)
                .collect(Collectors.toList());
    }

    @Override
    public List<FollowUserVO> getFollowing(Long userId, Long currentUserId) {
        LambdaQueryWrapper<FollowRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FollowRecord::getUserId, userId);

        List<FollowRecord> records = baseMapper.selectList(wrapper);

        if (records.isEmpty()) {
            return List.of();
        }

        List<Long> followingIds = records.stream()
                .map(FollowRecord::getFollowedId)
                .collect(Collectors.toList());

        List<User> users = userMapper.selectBatchIds(followingIds);
        Map<Long, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        Set<Long> currentUserFollowing = null;
        if (currentUserId != null) {
            LambdaQueryWrapper<FollowRecord> followingWrapper = new LambdaQueryWrapper<>();
            followingWrapper.eq(FollowRecord::getUserId, currentUserId);
            List<FollowRecord> followingRecords = baseMapper.selectList(followingWrapper);
            currentUserFollowing = followingRecords.stream()
                    .map(FollowRecord::getFollowedId)
                    .collect(Collectors.toSet());
        }

        final Set<Long> followingSet2 = currentUserFollowing;
        return followingIds.stream()
                .map(followingId -> {
                    User user = userMap.get(followingId);
                    if (user == null) {
                        return null;
                    }
                    return buildFollowUserVO(user,
                            followingSet2 != null && followingSet2.contains(followingId));
                })
                .filter(v -> v != null)
                .collect(Collectors.toList());
    }

    private FollowUserVO buildFollowUserVO(User user, Boolean isFollowed) {
        return FollowUserVO.builder()
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
                .isFollowed(isFollowed)
                .createTime(user.getCreateTime())
                .build();
    }
}