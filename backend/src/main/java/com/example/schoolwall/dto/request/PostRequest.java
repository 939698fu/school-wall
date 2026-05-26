package com.example.schoolwall.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 发布帖子请求DTO
 */
@Data
@Schema(description = "发布帖子请求")
public class PostRequest {

    @NotBlank(message = "标题不能为空")
    @Size(min = 2, max = 50, message = "标题长度必须在2-50个字符之间")
    @Schema(description = "帖子标题", example = "北区食堂新出的麻辣香锅真的绝了！！")
    private String title;

    @NotBlank(message = "内容不能为空")
    @Size(min = 10, max = 2000, message = "内容长度必须在10-2000个字符之间")
    @Schema(description = "帖子内容", example = "今天中午去北区食堂吃饭，偶然发现新开了个麻辣香锅窗口...")
    private String content;

    @Schema(description = "图片URL列表", example = "[\"https://example.com/img1.jpg\", \"https://example.com/img2.jpg\"]")
    private List<String> images;

    @Size(max = 20, message = "标签长度不能超过20个字符")
    @Schema(description = "话题标签", example = "美食")
    private String tag;

    @Schema(description = "标签颜色", example = "orange")
    private String tagColor;

    @Schema(description = "是否匿名", example = "false")
    private Boolean isAnon = false;

    @Schema(description = "是否仅自己可见", example = "false")
    private Boolean isPrivate = false;
}
