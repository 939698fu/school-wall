package com.example.schoolwall.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 微信小程序登录请求
 */
@Data
@Schema(description = "微信登录请求")
public class WxLoginRequest {

    @NotBlank(message = "微信登录凭证不能为空")
    @Schema(description = "uni.login / wx.login 返回的 code")
    private String code;

    @Schema(description = "用户昵称（可选，来自 getUserProfile）")
    private String nickname;

    @Schema(description = "用户头像 URL（可选，来自 getUserProfile）")
    private String avatar;
}
