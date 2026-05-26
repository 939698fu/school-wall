package com.example.schoolwall.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 发送消息请求DTO
 */
@Data
@Schema(description = "发送消息请求")
public class SendMessageRequest {

    @NotNull(message = "接收用户ID不能为空")
    @Schema(description = "接收用户ID", example = "201")
    private Long toId;

    @Schema(description = "消息内容（文本消息必填）", example = "你好，我看了你发的那道数分题")
    private String content;

    @Schema(description = "消息类型：text(文本)、image(图片)", example = "text")
    private String type = "text";

    @Schema(description = "图片URL（图片消息必填）", example = "/api/upload/messages/2024/01/15/abc123.jpg")
    private String fileUrl;
}