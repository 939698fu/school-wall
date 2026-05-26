package com.example.schoolwall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.schoolwall.dto.response.FollowUserVO;
import com.example.schoolwall.entity.FollowRecord;

import java.util.List;

/**
 * 关注服务接口
 */
public interface FollowService extends IService<FollowRecord> {

    /**
     * 获取粉丝列表
     * @param userId 用户ID
     * @param currentUserId 当前登录用户ID
     * @return 粉丝列表
     */
    List<FollowUserVO> getFollowers(Long userId, Long currentUserId);

    /**
     * 获取关注列表
     * @param userId 用户ID
     * @param currentUserId 当前登录用户ID
     * @return 关注列表
     */
    List<FollowUserVO> getFollowing(Long userId, Long currentUserId);
}