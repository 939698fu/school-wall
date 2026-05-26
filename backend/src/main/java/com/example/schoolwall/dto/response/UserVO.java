package com.example.schoolwall.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户信息")
public class UserVO {

    @Schema(description = "用户ID", example = "1")
    private Long id;

    @Schema(description = "用户名", example = "zhangsan")
    private String username;

    @Schema(description = "昵称", example = "张三")
    private String nickname;

    @Schema(description = "头像", example = "🍊")
    private String avatar;

    @Schema(description = "学校", example = "某某大学")
    private String school;

    @Schema(description = "个人简介", example = "喜欢学习")
    private String bio;

    @Schema(description = "帖子数", example = "12")
    private Integer postCount;

    @Schema(description = "获赞数", example = "286")
    private Integer likeCount;

    @Schema(description = "收藏数", example = "43")
    private Integer collectCount;

    @Schema(description = "粉丝数", example = "100")
    private Integer followerCount;

    @Schema(description = "关注数", example = "50")
    private Integer followingCount;

    @Schema(description = "是否已关注", example = "false")
    private Boolean isFollowed;

    @Schema(description = "创建时间", example = "2024-01-01 10:00:00")
    private LocalDateTime createTime;
}
