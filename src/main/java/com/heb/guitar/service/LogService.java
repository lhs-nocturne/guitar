package com.heb.guitar.service;

import com.heb.guitar.entity.SysLog;
import com.heb.guitar.vo.req.SysLogPageReqVO;
import java.util.List;

public interface LogService {

    List<SysLog> selectAll(SysLogPageReqVO sysLogPageReqVO);

    long logCount(SysLogPageReqVO sysLogPageReqVO);

    int deleted(List<String> logIds);

}
