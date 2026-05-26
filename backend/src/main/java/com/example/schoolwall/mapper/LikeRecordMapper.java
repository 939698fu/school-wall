package com.example.schoolwall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.schoolwall.entity.LikeRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 点赞记录Mapper
 */
@Mapper
public interface LikeRecordMapper extends BaseMapper<LikeRecord> {
}
