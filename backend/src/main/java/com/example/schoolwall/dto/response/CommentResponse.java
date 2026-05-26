package com.example.schoolwall.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 发布评论响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "评论响应")
public class CommentResponse {

    @Schema(description = "评论ID", example = "100")
    private Long id;

    @Schema(description = "帖子ID", example = "1")
    private Long postId;

    @Schema(description = "评论内容", example = "新评论")
    private String content;

    @Schema(description = "相对时间", example = "刚刚")
    private String time;
}
