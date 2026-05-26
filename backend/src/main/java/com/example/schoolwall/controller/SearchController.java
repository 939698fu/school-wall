package com.example.schoolwall.controller;

import com.example.schoolwall.common.Result;
import com.example.schoolwall.dto.response.SearchResponse;
import com.example.schoolwall.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 搜索控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/search")
@Tag(name = "搜索模块", description = "搜索功能")
@RequiredArgsConstructor
@Validated
public class SearchController {

    @Autowired
    private final SearchService searchService;

    /**
     * 搜索
     */
    @GetMapping
    @Operation(summary = "搜索", description = "搜索用户和帖子")
    public Result<SearchResponse> search(
            @Parameter(description = "搜索关键词", required = true) @RequestParam @NotBlank(message = "搜索关键词不能为空") String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size) {
        
        SearchResponse result = searchService.search(keyword, page, size);
        return Result.success(result);
    }
}
