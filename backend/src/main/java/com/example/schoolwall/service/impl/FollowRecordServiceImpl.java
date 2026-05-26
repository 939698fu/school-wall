package com.example.schoolwall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.schoolwall.entity.FollowRecord;
import com.example.schoolwall.mapper.FollowRecordMapper;
import com.example.schoolwall.service.FollowRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 关注记录服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FollowRecordServiceImpl extends ServiceImpl<FollowRecordMapper, FollowRecord> implements FollowRecordService {
}
