package com.example.schoolwall.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 搜索响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "搜索响应")
public class SearchResponse {

    @Schema(description = "用户列表")
    private List<SearchUserVO> users;

    @Schema(description = "帖子列表")
    private List<SearchPostVO> posts;

    @Schema(description = "用户总数", example = "5")
    private Integer userTotal;

    @Schema(description = "帖子总数", example = "23")
    private Integer postTotal;
}
