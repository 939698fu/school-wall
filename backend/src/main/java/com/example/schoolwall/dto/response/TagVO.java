package com.example.schoolwall.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 话题标签视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "话题标签信息")
public class TagVO {

    @Schema(description = "标签名称", example = "校园生活")
    private String name;

    @Schema(description = "标签颜色", example = "blue")
    private String color;

    @Schema(description = "使用次数", example = "100")
    private Integer count;
}