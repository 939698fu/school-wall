package com.example.schoolwall.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 搜索帖子视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "搜索帖子信息")
public class SearchPostVO {

    @Schema(description = "帖子ID", example = "5")
    private Long id;

    @Schema(description = "帖子标题", example = "分享一个超好用的备考资料整理网站")
    private String title;

    @Schema(description = "帖子内容", example = "期末季到了...")
    private String content;

    @Schema(description = "作者名称", example = "学习委员")
    private String author;

    @Schema(description = "点赞数", example = "430")
    private Integer likes;

    @Schema(description = "评论数", example = "62")
    private Integer commentCount;

    @Schema(description = "相对时间", example = "3小时前")
    private String time;
}
