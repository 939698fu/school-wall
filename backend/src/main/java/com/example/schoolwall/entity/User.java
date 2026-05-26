package com.example.schoolwall.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user")
public class User extends BaseEntity {

    /**
     * 用户名（登录用）
     */
    @TableField("username")
    private String username;

    /**
     * 密码（加密存储）
     */
    @TableField("password")
    private String password;

    /**
     * 微信OpenID
     */
    @TableField("open_id")
    private String openId;

    /**
     * 用户昵称
     */
    @TableField("nickname")
    private String nickname;

    /**
     * 头像URL
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 学校名称
     */
    @TableField("school")
    private String school;

    /**
     * 个人简介
     */
    @TableField("bio")
    private String bio;

    /**
     * 帖子数
     */
    @TableField("post_count")
    private Integer postCount;

    /**
     * 获赞数
     */
    @TableField("like_count")
    private Integer likeCount;

    /**
     * 收藏数
     */
    @TableField("collect_count")
    private Integer collectCount;

    /**
     * 粉丝数
     */
    @TableField("follower_count")
    private Integer followerCount;

    /**
     * 关注数
     */
    @TableField("following_count")
    private Integer followingCount;
}