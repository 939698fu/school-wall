package com.example.schoolwall.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 帖子实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("post")
public class Post extends BaseEntity {

    /**
     * 作者ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 帖子标题
     */
    @TableField("title")
    private String title;

    /**
     * 帖子内容
     */
    @TableField("content")
    private String content;

    /**
     * 图片URL列表，JSON格式
     */
    @TableField("images")
    private String images;

    /**
     * 话题标签
     */
    @TableField("tag")
    private String tag;

    /**
     * 标签颜色
     */
    @TableField("tag_color")
    private String tagColor;

    /**
     * 是否匿名(0-否,1-是)
     */
    @TableField("is_anon")
    private Integer isAnon;

    /**
     * 是否仅自己可见(0-否,1-是)
     */
    @TableField("is_private")
    private Integer isPrivate;

    /**
     * 点赞数
     */
    @TableField("likes")
    private Integer likes;

    /**
     * 评论数
     */
    @TableField("comments")
    private Integer comments;
}
