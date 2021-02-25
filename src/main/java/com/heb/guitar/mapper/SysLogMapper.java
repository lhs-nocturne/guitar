package com.heb.guitar.mapper;

import com.heb.guitar.entity.SysLog;
import com.heb.guitar.vo.req.SysLogPageReqVO;
import java.util.List;

public interface SysLogMapper {

    int deleteByPrimaryKey(String id);

    int insert(SysLog record);

    int insertSelective(SysLog record);

    SysLog selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(SysLog record);

    int updateByPrimaryKey(SysLog record);

    List<SysLog> selectAll(SysLogPageReqVO vo);

    long logCount(SysLogPageReqVO vo);

    int batchDeletedLog(List<String> logIds);
}