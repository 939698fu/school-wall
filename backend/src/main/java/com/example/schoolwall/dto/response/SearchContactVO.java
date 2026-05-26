package com.example.schoolwall.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 搜索联系人视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "搜索联系人信息")
public class SearchContactVO {

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

    @Schema(description = "最后消息内容", example = "你好啊！")
    private String lastMessage;

    @Schema(description = "最后消息时间")
    private String lastMessageTime;

    @Schema(description = "未读消息数", example = "0")
    private Integer unreadCount;
}