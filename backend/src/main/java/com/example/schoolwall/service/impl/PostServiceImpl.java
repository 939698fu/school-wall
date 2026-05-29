package com.example.schoolwall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.schoolwall.dto.request.PostRequest;
import com.example.schoolwall.dto.response.*;
import com.example.schoolwall.entity.*;
import com.example.schoolwall.common.BusinessException;
import com.example.schoolwall.mapper.*;
import com.example.schoolwall.service.PostService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 帖子服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    private final UserMapper userMapper;
    private final CommentMapper commentMapper;
    private final LikeRecordMapper likeRecordMapper;
    private final CollectRecordMapper collectRecordMapper;
    private final FollowRecordMapper followRecordMapper;
    private final ObjectMapper objectMapper;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public List<FollowUserVO> getFollowingUsers(Long userId) {
        if (userId == null) {
            return List.of();
        }

        LambdaQueryWrapper<FollowRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FollowRecord::getUserId, userId);

        List<FollowRecord> records = followRecordMapper.selectList(wrapper);

        if (records.isEmpty()) {
            return List.of();
        }

        List<Long> followingIds = records.stream()
                .map(FollowRecord::getFollowedId)
                .collect(Collectors.toList());

        List<User> users = userMapper.selectBatchIds(followingIds);
        Map<Long, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        return followingIds.stream()
                .map(followedId -> {
                    User user = userMap.get(followedId);
                    if (user == null) {
                        return null;
                    }
                    return FollowUserVO.builder()
                            .id(user.getId())
                            .username(user.getUsername())
                            .nickname(user.getNickname())
                            .avatar(user.getAvatar())
                            .school(user.getSchool())
                            .bio(user.getBio())
                            .isFollowed(true)
                            .build();
                })
                .filter(v -> v != null)
                .collect(Collectors.toList());
    }

    @Override
    public IPage<PostVO> getPostList(Integer page, Integer size, String type, String tag, Long userId) {
        Page<Post> pageParam = new Page<>(page, size);
        
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        appendVisibilityCondition(wrapper, userId);
        
        // 标签筛选
        if (tag != null && !tag.isEmpty()) {
            wrapper.eq(Post::getTag, tag);
        }
        
        // 排序类型
        switch (type != null ? type : "latest") {
            case "hot":
                wrapper.orderByDesc(Post::getLikes);
                break;
            case "hole":
                wrapper.eq(Post::getIsAnon, 1).orderByDesc(Post::getCreateTime);
                break;
            case "love":
                wrapper.eq(Post::getTag, "表白").orderByDesc(Post::getCreateTime);
                break;
            default: // latest
                wrapper.orderByDesc(Post::getCreateTime);
        }
        
        IPage<Post> postPage = baseMapper.selectPage(pageParam, wrapper);

        // 转换为VO
        return postPage.convert(post -> convertToVO(post, userId));
    }

    @Override
    public CursorPageVO<PostVO> getPostListByCursor(Long cursor, Integer size, String type, String tag, Long userId) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        appendVisibilityCondition(wrapper, userId);

        // 标签筛选
        if (tag != null && !tag.isEmpty()) {
            wrapper.eq(Post::getTag, tag);
        }

        String sortType = type != null ? type : "latest";

        // 游标条件与排序
        if ("hot".equals(sortType)) {
            wrapper.orderByDesc(Post::getLikes).orderByDesc(Post::getId);
            if (cursor != null) {
                // 热门排序暂时使用 offset 模拟游标（cursor 传入已加载的数量）
                wrapper.last("LIMIT " + cursor + ", " + (size + 1));
            } else {
                wrapper.last("LIMIT " + (size + 1));
            }
        } else {
            if ("hole".equals(sortType)) {
                wrapper.eq(Post::getIsAnon, 1);
            } else if ("love".equals(sortType)) {
                wrapper.eq(Post::getTag, "表白");
            }
            
            if (cursor != null) {
                wrapper.lt(Post::getCreateTime, java.time.LocalDateTime.ofEpochSecond(cursor, 0, java.time.ZoneOffset.UTC));
            }
            wrapper.orderByDesc(Post::getCreateTime).orderByDesc(Post::getId);
            wrapper.last("LIMIT " + (size + 1));
        }

        List<Post> posts = baseMapper.selectList(wrapper);

        boolean hasMore = posts.size() > size;
        if (hasMore) {
            posts = posts.subList(0, size);
        }

        List<PostVO> voList = posts.stream()
                .map(post -> convertToVO(post, userId))
                .collect(Collectors.toList());

        Long nextCursor = null;
        if (hasMore && !posts.isEmpty()) {
            if ("hot".equals(sortType)) {
                // 热门排序下，nextCursor 为当前已加载的总数
                nextCursor = (cursor != null ? cursor : 0) + size;
            } else {
                Post lastPost = posts.get(posts.size() - 1);
                if (lastPost.getCreateTime() != null) {
                    nextCursor = lastPost.getCreateTime().toEpochSecond(java.time.ZoneOffset.UTC);
                }
            }
        }

        return CursorPageVO.<PostVO>builder()
                .records(voList)
                .nextCursor(nextCursor)
                .hasMore(hasMore)
                .count(voList.size())
                .build();
    }

    @Override
    public PostDetailVO getPostDetail(Long postId, Long userId) {
        Post post = baseMapper.selectById(postId);
        if (post == null) {
            throw BusinessException.notFound("帖子不存在");
        }
        validatePostAccessible(post, userId);
        
        // 获取评论列表
        List<Comment> comments = commentMapper.selectList(
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getPostId, postId)
                        .orderByDesc(Comment::getCreateTime)
        );
        
        List<CommentVO> commentVOs = comments.stream()
                .map(comment -> convertToCommentVO(comment, userId, post.getUserId()))
                .collect(Collectors.toList());
        
        // 转换为VO
        PostVO baseVO = convertToVO(post, userId);
        
        // 构建详情VO
        return PostDetailVO.builder()
                .id(baseVO.getId())
                .title(baseVO.getTitle())
                .content(baseVO.getContent())
                .images(baseVO.getImages())
                .tag(baseVO.getTag())
                .tagColor(baseVO.getTagColor())
                .isAnon(baseVO.getIsAnon())
                .author(baseVO.getAuthor())
                .authorAvatar(baseVO.getAuthorAvatar())
                .authorId(baseVO.getAuthorId())
                .likes(baseVO.getLikes())
                .liked(baseVO.getLiked())
                .collected(baseVO.getCollected())
                .commentCount(baseVO.getCommentCount())
                .time(baseVO.getTime())
                .fullTime(baseVO.getFullTime())
                .createTime(baseVO.getCreateTime())
                .comments(commentVOs)
                .build();
    }

    @Override
    @Transactional
    public PostVO createPost(PostRequest request, Long userId) {
        // 获取用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw BusinessException.unauthorized("用户不存在");
        }
        
        // 创建帖子
        Post post = new Post();
        post.setUserId(userId);
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        
        // 处理图片
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            try {
                post.setImages(objectMapper.writeValueAsString(request.getImages()));
            } catch (JsonProcessingException e) {
                log.error("图片列表序列化失败", e);
                throw BusinessException.badRequest("图片数据格式错误");
            }
        }
        
        // 标签处理
        post.setTag(request.getTag() != null ? request.getTag() : "校园生活");
        post.setTagColor(request.getTagColor() != null ? request.getTagColor() : "gray");
        post.setIsAnon(request.getIsAnon() != null && request.getIsAnon() ? 1 : 0);
        post.setIsPrivate(request.getIsPrivate() != null && request.getIsPrivate() ? 1 : 0);
        post.setLikes(0);
        post.setComments(0);
        
        baseMapper.insert(post);
        
        // 更新用户帖子数
        user.setPostCount((user.getPostCount() == null ? 0 : user.getPostCount()) + 1);
        userMapper.updateById(user);
        
        log.info("帖子发布成功: userId={}, postId={}", userId, post.getId());
        
        return convertToVO(post, userId);
    }

    @Override
    @Transactional
    public LikeResponse toggleLike(Long postId, Long userId) {
        Post post = baseMapper.selectById(postId);
        if (post == null) {
            throw BusinessException.notFound("帖子不存在");
        }
        validatePostAccessible(post, userId);
        
        // 检查是否已点赞
        LambdaQueryWrapper<LikeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LikeRecord::getPostId, postId)
               .eq(LikeRecord::getUserId, userId);
        LikeRecord existing = likeRecordMapper.selectOne(wrapper);
        
        boolean liked;
        int likes;
        
        if (existing != null) {
            // 取消点赞
            likeRecordMapper.deleteById(existing.getId());
            likes = Math.max(0, post.getLikes() - 1);
            liked = false;
        } else {
            // 添加点赞
            LikeRecord record = new LikeRecord();
            record.setUserId(userId);
            record.setPostId(postId);
            likeRecordMapper.insert(record);
            likes = post.getLikes() + 1;
            liked = true;
        }
        
        // 更新帖子点赞数
        post.setLikes(likes);
        baseMapper.updateById(post);

        // 同步帖子作者的获赞数（不给自己加/减）
        if (!post.getUserId().equals(userId)) {
            User author = userMapper.selectById(post.getUserId());
            if (author != null) {
                int current = author.getLikeCount() == null ? 0 : author.getLikeCount();
                author.setLikeCount(liked ? current + 1 : Math.max(0, current - 1));
                userMapper.updateById(author);
            }
        }

        return LikeResponse.builder()
                .liked(liked)
                .likes(likes)
                .build();
    }

    @Override
    @Transactional
    public CollectResponse toggleCollect(Long postId, Long userId) {
        Post post = baseMapper.selectById(postId);
        if (post == null) {
            throw BusinessException.notFound("帖子不存在");
        }
        validatePostAccessible(post, userId);
        
        // 检查是否已收藏
        LambdaQueryWrapper<CollectRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CollectRecord::getPostId, postId)
               .eq(CollectRecord::getUserId, userId);
        CollectRecord existing = collectRecordMapper.selectOne(wrapper);
        
        boolean collected;
        
        if (existing != null) {
            // 取消收藏
            collectRecordMapper.deleteById(existing.getId());
            collected = false;
        } else {
            // 添加收藏
            CollectRecord record = new CollectRecord();
            record.setUserId(userId);
            record.setPostId(postId);
            collectRecordMapper.insert(record);
            collected = true;
        }
        
        return CollectResponse.builder()
                .collected(collected)
                .build();
    }

    @Override
    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = baseMapper.selectById(postId);
        if (post == null) {
            throw BusinessException.notFound("帖子不存在");
        }
        
        // 检查权限
        if (!post.getUserId().equals(userId)) {
            throw BusinessException.forbidden("无权删除该帖子");
        }
        
        // 删除帖子
        baseMapper.deleteById(postId);

        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setPostCount(Math.max(0, (user.getPostCount() == null ? 0 : user.getPostCount()) - 1));
            userMapper.updateById(user);
        }
        
        // 删除相关评论、点赞、收藏记录
        commentMapper.delete(new LambdaQueryWrapper<Comment>().eq(Comment::getPostId, postId));
        likeRecordMapper.delete(new LambdaQueryWrapper<LikeRecord>().eq(LikeRecord::getPostId, postId));
        collectRecordMapper.delete(new LambdaQueryWrapper<CollectRecord>().eq(CollectRecord::getPostId, postId));
        
        log.info("帖子删除成功: userId={}, postId={}", userId, postId);
    }

    @Override
    public IPage<PostVO> getMyPosts(Long userId, Integer page, Integer size) {
        Page<Post> pageParam = new Page<>(page, size);
        
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getUserId, userId)
               .orderByDesc(Post::getCreateTime);
        
        IPage<Post> postPage = baseMapper.selectPage(pageParam, wrapper);
        
        return postPage.convert(post -> convertToVO(post, userId));
    }

    @Override
    public IPage<PostVO> getUserPosts(Long targetUserId, Long currentUserId, Integer page, Integer size) {
        Page<Post> pageParam = new Page<>(page, size);

        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getUserId, targetUserId);
        appendVisibilityCondition(wrapper, currentUserId);
        wrapper.orderByDesc(Post::getCreateTime);

        IPage<Post> postPage = baseMapper.selectPage(pageParam, wrapper);
        return postPage.convert(post -> convertToVO(post, currentUserId));
    }

    @Override
    public IPage<PostVO> getMyCollections(Long userId, Integer page, Integer size) {
        Page<CollectRecord> pageParam = new Page<>(page, size);
        
        LambdaQueryWrapper<CollectRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CollectRecord::getUserId, userId)
               .orderByDesc(CollectRecord::getCreateTime);
        
        IPage<CollectRecord> collectPage = collectRecordMapper.selectPage(pageParam, wrapper);
        
        // 获取所有帖子ID
        List<Long> postIds = collectPage.getRecords().stream()
                .map(CollectRecord::getPostId)
                .collect(Collectors.toList());
        
        if (postIds.isEmpty()) {
            return new Page<>(page, size);
        }
        
        // 批量获取帖子
        List<Post> posts = baseMapper.selectBatchIds(postIds);
        
        // 建立帖子ID到帖子的映射
        Map<Long, Post> postMap = posts.stream()
                .collect(Collectors.toMap(Post::getId, post -> post));
        
        // 转换为VO
        List<PostVO> voList = collectPage.getRecords().stream()
                .map(record -> {
                    Post post = postMap.get(record.getPostId());
                    return post != null ? convertToVO(post, userId) : null;
                })
                .filter(v -> v != null)
                .collect(Collectors.toList());
        
        IPage<PostVO> result = new Page<>(page, size);
        result.setRecords(voList);
        result.setTotal(collectPage.getTotal());
        
        return result;
    }

    /**
     * 转换帖子实体为VO
     */
    private PostVO convertToVO(Post post, Long userId) {
        User user = userMapper.selectById(post.getUserId());
        
        // 处理匿名
        String author = post.getIsAnon() == 1 ? "匿名用户" : (user != null ? user.getNickname() : "未知用户");
        String authorAvatar = post.getIsAnon() == 1 ? "👤" : (user != null ? user.getAvatar() : "👤");
        
        // 解析图片列表
        List<String> images = new ArrayList<>();
        if (post.getImages() != null && !post.getImages().isEmpty()) {
            try {
                images = objectMapper.readValue(post.getImages(), new TypeReference<List<String>>() {});
            } catch (JsonProcessingException e) {
                log.warn("图片列表解析失败: {}", e.getMessage());
            }
        }
        
        // 检查是否已点赞
        Boolean liked = false;
        if (userId != null) {
            LambdaQueryWrapper<LikeRecord> likeWrapper = new LambdaQueryWrapper<>();
            likeWrapper.eq(LikeRecord::getPostId, post.getId())
                       .eq(LikeRecord::getUserId, userId);
            liked = likeRecordMapper.selectOne(likeWrapper) != null;
        }
        
        // 检查是否已收藏
        Boolean collected = false;
        if (userId != null) {
            LambdaQueryWrapper<CollectRecord> collectWrapper = new LambdaQueryWrapper<>();
            collectWrapper.eq(CollectRecord::getPostId, post.getId())
                         .eq(CollectRecord::getUserId, userId);
            collected = collectRecordMapper.selectOne(collectWrapper) != null;
        }
        
        // 计算相对时间
        String relativeTime = calculateRelativeTime(post.getCreateTime());
        
        return PostVO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .images(images)
                .tag(post.getTag())
                .tagColor(post.getTagColor())
                .isAnon(post.getIsAnon() == 1)
                .author(author)
                .authorAvatar(authorAvatar)
                .authorId(post.getUserId())
                .likes(post.getLikes())
                .liked(liked)
                .collected(collected)
                .commentCount(post.getComments())
                .time(relativeTime)
                .fullTime(post.getCreateTime() != null ? post.getCreateTime().format(FORMATTER) : null)
                .createTime(post.getCreateTime())
                .build();
    }

    private void appendVisibilityCondition(LambdaQueryWrapper<Post> wrapper, Long userId) {
        if (userId == null) {
            wrapper.and(w -> w.eq(Post::getIsPrivate, 0).or().isNull(Post::getIsPrivate));
            return;
        }
        wrapper.and(w -> w.eq(Post::getIsPrivate, 0)
                .or().isNull(Post::getIsPrivate)
                .or(q -> q.eq(Post::getIsPrivate, 1).eq(Post::getUserId, userId)));
    }

    private void validatePostAccessible(Post post, Long userId) {
        if (post.getIsPrivate() != null && post.getIsPrivate() == 1 && !post.getUserId().equals(userId)) {
            throw BusinessException.forbidden("该帖子仅作者自己可见");
        }
    }

    /**
     * 转换评论实体为VO
     */
    private CommentVO convertToCommentVO(Comment comment, Long userId, Long postAuthorId) {
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

    /**
     * 计算相对时间
     */
    private String calculateRelativeTime(LocalDateTime createTime) {
        if (createTime == null) {
            return "未知";
        }
        
        LocalDateTime now = LocalDateTime.now();
        long minutes = java.time.Duration.between(createTime, now).toMinutes();
        
        if (minutes < 1) {
            return "刚刚";
        } else if (minutes < 60) {
            return minutes + "分钟前";
        } else if (minutes < 1440) { // 24小时
            return (minutes / 60) + "小时前";
        } else if (minutes < 10080) { // 7天
            return (minutes / 1440) + "天前";
        } else {
            return createTime.format(FORMATTER);
        }
    }
}
