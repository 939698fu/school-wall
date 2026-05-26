package com.example.schoolwall.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 关注用户视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "关注用户信息")
public class FollowUserVO {

    @Schema(description = "用户ID", example = "1")
    private Long id;

    @Schema(description = "用户名", example = "chengzi")
    private String username;

    @Schema(description = "昵称", example = "橙子不甜")
    private String nickname;

    @Schema(description = "头像", example = "🍊")
    private String avatar;

    @Schema(description = "学校", example = "清华大学")
    private String school;

    @Schema(description = "个人简介", example = "一个热爱学习的同学")
    private String bio;

    @Schema(description = "粉丝数", example = "100")
    private Integer followerCount;

    @Schema(description = "关注数", example = "50")
    private Integer followingCount;

    @Schema(description = "发帖数", example = "20")
    private Integer postCount;

    @Schema(description = "获赞数", example = "500")
    private Integer likeCount;

    @Schema(description = "是否已关注", example = "true")
    private Boolean isFollowed;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}