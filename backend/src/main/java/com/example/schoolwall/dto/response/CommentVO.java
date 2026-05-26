package com.example.schoolwall.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 评论视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "评论信息")
public class CommentVO {

    @Schema(description = "评论ID", example = "1")
    private Long id;

    @Schema(description = "帖子ID", example = "1")
    private Long postId;

    @Schema(description = "作者名称", example = "饿了么同学")
    private String author;

    @Schema(description = "作者头像", example = "🦊")
    private String authorAvatar;

    @Schema(description = "作者ID", example = "201")
    private Long authorId;

    @Schema(description = "评论内容", example = "已经约好舍友明天中午去！")
    private String content;

    @Schema(description = "相对时间", example = "12:40")
    private String time;

    @Schema(description = "完整时间", example = "2024-06-10 12:40:00")
    private String fullTime;

    @Schema(description = "点赞数", example = "23")
    private Integer likes;

    @Schema(description = "是否已点赞", example = "false")
    private Boolean liked;

    @Schema(description = "是否是帖子作者", example = "false")
    private Boolean isAuthor;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
