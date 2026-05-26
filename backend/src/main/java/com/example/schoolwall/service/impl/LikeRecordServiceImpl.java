package com.example.schoolwall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.schoolwall.entity.LikeRecord;
import com.example.schoolwall.mapper.LikeRecordMapper;
import com.example.schoolwall.service.LikeRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 点赞记录服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LikeRecordServiceImpl extends ServiceImpl<LikeRecordMapper, LikeRecord> implements LikeRecordService {
}
