package com.example.schoolwall.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 点赞记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("like_record")
public class LikeRecord extends BaseEntity {

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 帖子ID
     */
    @TableField("post_id")
    private Long postId;

    /**
     * 评论ID
     */
    @TableField("comment_id")
    private Long commentId;
}
