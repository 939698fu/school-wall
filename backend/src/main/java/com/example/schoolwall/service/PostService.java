package com.example.schoolwall.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.schoolwall.dto.request.PostRequest;
import com.example.schoolwall.dto.response.*;
import com.example.schoolwall.entity.Post;

import java.util.List;

/**
 * 帖子服务接口
 */
public interface PostService extends IService<Post> {

    /**
     * 获取当前用户关注的人列表
     * @param userId 当前用户ID
     * @return 关注的人列表
     */
    List<FollowUserVO> getFollowingUsers(Long userId);

    /**
     * 获取帖子列表
     * @param page 页码
     * @param size 每页数量
     * @param type 排序类型：latest(最新)、hot(热门)、hole(树洞)、love(表白)
     * @param tag 话题标签筛选
     * @param userId 当前用户ID（可选）
     * @return 分页帖子列表
     */
    IPage<PostVO> getPostList(Integer page, Integer size, String type, String tag, Long userId);

    /**
     * 获取帖子列表（游标分页 - 用于无限滚动）
     * @param cursor 游标时间戳（上一页最后一条帖子创建时间），首次请求为null
     * @param size 每页数量
     * @param type 排序类型：latest(最新)、hot(热门)、hole(树洞)、love(表白)
     * @param tag 话题标签筛选
     * @param userId 当前用户ID（可选）
     * @return 游标分页结果
     */
    CursorPageVO<PostVO> getPostListByCursor(Long cursor, Integer size, String type, String tag, Long userId);

    /**
     * 获取帖子详情
     * @param postId 帖子ID
     * @param userId 当前用户ID（可选）
     * @return 帖子详情
     */
    PostDetailVO getPostDetail(Long postId, Long userId);

    /**
     * 发布帖子
     * @param request 发布请求
     * @param userId 当前用户ID
     * @return 发布的帖子信息
     */
    PostVO createPost(PostRequest request, Long userId);

    /**
     * 点赞/取消点赞帖子
     * @param postId 帖子ID
     * @param userId 当前用户ID
     * @return 点赞状态
     */
    LikeResponse toggleLike(Long postId, Long userId);

    /**
     * 收藏/取消收藏帖子
     * @param postId 帖子ID
     * @param userId 当前用户ID
     * @return 收藏状态
     */
    CollectResponse toggleCollect(Long postId, Long userId);

    /**
     * 删除帖子
     * @param postId 帖子ID
     * @param userId 当前用户ID
     */
    void deletePost(Long postId, Long userId);

    /**
     * 获取我的帖子
     * @param userId 当前用户ID
     * @param page 页码
     * @param size 每页数量
     * @return 分页帖子列表
     */
    IPage<PostVO> getMyPosts(Long userId, Integer page, Integer size);

    /**
     * 获取我的收藏
     * @param userId 当前用户ID
     * @param page 页码
     * @param size 每页数量
     * @return 分页帖子列表
     */
    IPage<PostVO> getMyCollections(Long userId, Integer page, Integer size);
}
