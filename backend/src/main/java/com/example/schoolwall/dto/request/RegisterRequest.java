package com.example.schoolwall.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 注册请求DTO
 */
@Data
@Schema(description = "注册请求")
public class RegisterRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 20, message = "用户名长度必须在2-20个字符之间")
    @Schema(description = "用户名", example = "zhangsan")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 32, message = "密码长度必须在6-32个字符之间")
    @Schema(description = "密码", example = "123456")
    private String password;

    @NotBlank(message = "昵称不能为空")
    @Size(min = 2, max = 50, message = "昵称长度必须在2-50个字符之间")
    @Schema(description = "昵称", example = "张三")
    private String nickname;

    @Size(max = 100, message = "学校名称长度不能超过100个字符")
    @Schema(description = "学校名称", example = "某某大学")
    private String school;

    @Size(max = 100, message = "个人简介长度不能超过100个字符")
    @Schema(description = "个人简介", example = "喜欢学习，热爱生活")
    private String bio;
}
