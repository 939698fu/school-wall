package com.example.schoolwall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.schoolwall.entity.Post;
import org.apache.ibatis.annotations.Mapper;

/**
 * 帖子Mapper
 */
@Mapper
public interface PostMapper extends BaseMapper<Post> {
}
