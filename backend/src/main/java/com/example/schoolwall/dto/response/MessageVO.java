package com.example.schoolwall.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "消息信息")
public class MessageVO {

    @Schema(description = "消息ID", example = "1")
    private Long id;

    @Schema(description = "发送者ID", example = "201")
    private Long fromId;

    @Schema(description = "接收者ID", example = "1")
    private Long toId;

    @Schema(description = "消息内容", example = "你好，我看了你发的那道数分题")
    private String content;

    @Schema(description = "消息类型：text(文本)、image(图片)", example = "text")
    private String type;

    @Schema(description = "图片URL（图片消息）", example = "/api/upload/messages/2024/01/15/abc123.jpg")
    private String fileUrl;

    @Schema(description = "相对时间", example = "12:20")
    private String time;

    @Schema(description = "完整时间", example = "2024-06-10 12:20:00")
    private String fullTime;

    @Schema(description = "是否是自己发送的", example = "false")
    private Boolean fromMe;
}