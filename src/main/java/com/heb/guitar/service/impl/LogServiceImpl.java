package com.heb.guitar.service.impl;

import com.heb.guitar.entity.SysLog;
import com.heb.guitar.mapper.SysLogMapper;
import com.heb.guitar.service.LogService;
import com.heb.guitar.vo.req.SysLogPageReqVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class LogServiceImpl implements LogService {

    @Resource
    private SysLogMapper sysLogMapper;

    @Override
    public List<SysLog> selectAll(SysLogPageReqVO sysLogPageReqVO) {
        return sysLogMapper.selectAll(sysLogPageReqVO);
    }

    @Override
    public long logCount(SysLogPageReqVO sysLogPageReqVO) {
        return sysLogMapper.logCount(sysLogPageReqVO);
    }

    @Override
    public int deleted(List<String> logIds) {
        return sysLogMapper.batchDeletedLog(logIds);
    }
}
