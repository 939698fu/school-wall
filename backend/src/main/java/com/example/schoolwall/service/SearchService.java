package com.example.schoolwall.service;

import com.example.schoolwall.dto.response.SearchResponse;

/**
 * 搜索服务接口
 */
public interface SearchService {

    /**
     * 搜索用户和帖子
     * @param keyword 搜索关键词
     * @param page 页码
     * @param size 每页数量
     * @return 搜索结果
     */
    SearchResponse search(String keyword, Integer page, Integer size);
}
