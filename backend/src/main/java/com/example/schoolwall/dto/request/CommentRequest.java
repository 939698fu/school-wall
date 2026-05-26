package com.example.schoolwall.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 发布评论请求DTO
 */
@Data
@Schema(description = "发布评论请求")
public class CommentRequest {

    @NotNull(message = "帖子ID不能为空")
    @Schema(description = "帖子ID", example = "1")
    private Long postId;

    @NotBlank(message = "评论内容不能为空")
    @Size(min = 1, max = 200, message = "评论内容长度必须在1-200个字符之间")
    @Schema(description = "评论内容", example = "已经约好舍友明天中午去！")
    private String content;
}
