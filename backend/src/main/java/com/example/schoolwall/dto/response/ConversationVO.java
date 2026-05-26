package com.example.schoolwall.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 会话视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "会话信息")
public class ConversationVO {

    @Schema(description = "会话ID", example = "1")
    private Long id;

    @Schema(description = "对方用户ID", example = "201")
    private Long userId;

    @Schema(description = "对方昵称", example = "数学学长")
    private String name;

    @Schema(description = "对方头像", example = "🦁")
    private String avatar;

    @Schema(description = "最后一条消息", example = "可以，明天下午图书馆见！")
    private String lastMsg;

    @Schema(description = "最后消息时间", example = "刚刚")
    private String lastTime;

    @Schema(description = "未读消息数", example = "2")
    private Integer unread;
}
