package com.example.schoolwall.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 点赞响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "点赞响应")
public class LikeResponse {

    @Schema(description = "是否已点赞", example = "true")
    private Boolean liked;

    @Schema(description = "点赞数", example = "129")
    private Integer likes;
}
