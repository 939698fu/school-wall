package com.example.schoolwall.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新用户信息请求DTO
 */
@Data
@Schema(description = "更新用户信息请求")
public class UpdateUserRequest {

    @Size(min = 2, max = 20, message = "昵称长度必须在2-20个字符之间")
    @Schema(description = "用户昵称", example = "新昵称")
    private String nickname;

    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;

    @Size(max = 100, message = "学校名称长度不能超过100个字符")
    @Schema(description = "学校名称", example = "新学校")
    private String school;

    @Size(max = 100, message = "个人简介长度不能超过100个字符")
    @Schema(description = "个人简介", example = "新简介")
    private String bio;
}
