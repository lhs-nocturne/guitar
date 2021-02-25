package com.heb.guitar.controller;


import com.heb.guitar.aop.annotation.MyLog;
import com.heb.guitar.service.LogService;
import com.heb.guitar.utils.DataResult;
import com.heb.guitar.vo.req.SysLogPageReqVO;
import com.heb.guitar.vo.resp.PageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;


@RequestMapping("/api")
@Api(tags = "系统模块-系统操作日志管理")
@RestController
public class SysLogController {

    @Resource
    private LogService logService;

    @PostMapping("/logs")
    @ApiOperation(value = "分页查询系统操作日志接口")
    @RequiresPermissions("sys:log:list")
    public DataResult pageInfo(@RequestBody SysLogPageReqVO vo){
        DataResult result= DataResult.success();
        PageVO pageVO = new PageVO();
        pageVO.setTotalRows(logService.logCount(vo));
        pageVO.setList(logService.selectAll(vo));
        result.setData(pageVO);
        return result;
    }

    @DeleteMapping("/logs")
    @ApiOperation(value = "删除日志接口")
    @MyLog(title = "系统操作日志管理",action = "删除系统操作日志")
    @RequiresPermissions("sys:log:delete")
    public DataResult deleted(@RequestBody List<String> logIds){
        DataResult result=DataResult.success();
        result.setData(logService.deleted(logIds));
        return result;
    }

}
