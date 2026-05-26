package com.example.schoolwall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.schoolwall.entity.CollectRecord;
import com.example.schoolwall.mapper.CollectRecordMapper;
import com.example.schoolwall.service.CollectRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 收藏记录服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CollectRecordServiceImpl extends ServiceImpl<CollectRecordMapper, CollectRecord> implements CollectRecordService {
}
