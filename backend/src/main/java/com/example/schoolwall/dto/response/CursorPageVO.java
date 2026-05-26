package com.example.schoolwall.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 游标分页响应DTO（用于无限滚动）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "游标分页响应")
public class CursorPageVO<T> {

    @Schema(description = "数据列表")
    private List<T> records;

    @Schema(description = "下一页游标（时间戳），为空表示没有更多数据")
    private Long nextCursor;

    @Schema(description = "是否还有更多数据")
    private Boolean hasMore;

    @Schema(description = "本次返回的数据数量")
    private Integer count;
}
