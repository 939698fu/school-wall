package com.example.schoolwall.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 搜索用户视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "搜索用户信息")
public class SearchUserVO {

    @Schema(description = "用户ID", example = "2")
    private Long id;

    @Schema(description = "昵称", example = "数学学长")
    private String nickname;

    @Schema(description = "头像", example = "🦁")
    private String avatar;

    @Schema(description = "学校", example = "某某大学")
    private String school;

    @Schema(description = "帖子数", example = "25")
    private Integer postCount;
}
