package com.example.schoolwall.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 帖子视图对象
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "帖子信息")
public class PostVO {

    @Schema(description = "帖子ID", example = "1")
    private Long id;

    @Schema(description = "帖子标题", example = "北区食堂新出的麻辣香锅真的绝了！！")
    private String title;

    @Schema(description = "帖子内容", example = "今天中午去北区食堂吃饭...")
    private String content;

    @Schema(description = "图片URL列表")
    private List<String> images;

    @Schema(description = "话题标签", example = "美食")
    private String tag;

    @Schema(description = "标签颜色", example = "orange")
    private String tagColor;

    @Schema(description = "是否匿名", example = "true")
    private Boolean isAnon;

    @Schema(description = "作者名称", example = "匿名用户")
    private String author;

    @Schema(description = "作者头像", example = "🐼")
    private String authorAvatar;

    @Schema(description = "作者ID", example = "101")
    private Long authorId;

    @Schema(description = "点赞数", example = "128")
    private Integer likes;

    @Schema(description = "是否已点赞", example = "false")
    private Boolean liked;

    @Schema(description = "是否已收藏", example = "false")
    private Boolean collected;

    @Schema(description = "评论数", example = "34")
    private Integer commentCount;

    @Schema(description = "相对时间", example = "10分钟前")
    private String time;

    @Schema(description = "完整时间", example = "2024-06-10 12:34:00")
    private String fullTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
