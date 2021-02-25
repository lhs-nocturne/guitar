package com.heb.guitar.controller;


import com.heb.guitar.aop.annotation.MyLog;
import com.heb.guitar.entity.SysDept;
import com.heb.guitar.service.DeptService;
import com.heb.guitar.utils.DataResult;
import com.heb.guitar.vo.req.DeptAddReqVO;
import com.heb.guitar.vo.req.DeptUpdateReqVO;
import com.heb.guitar.vo.resp.DeptRespNodeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RequestMapping("/api")
@RestController
@Api(tags = "组织模块-机构管理")
public class DeptController {
    @Resource
    private DeptService deptService;

    @GetMapping("/depts")
    @ApiOperation(value = "获取部门列表接口")
    @MyLog(title = "部门管理",action = "获取部门数据列表")
    @RequiresPermissions("sys:dept:list")
    public DataResult getDeptAll(){
        DataResult<List<SysDept>> result=DataResult.success();
        List<SysDept> list = deptService.selectAll();
        result.setData(list);
        return result;
    }

    @GetMapping("/dept/tree")
    @ApiOperation(value = "部门树形结构列表接口")
    @RequiresPermissions(value = {"sys:user:update","sys:user:add","sys:dept:add","sys:dept:update"},logical = Logical.OR)
    public DataResult<List<DeptRespNodeVO>> getDeptTree(@RequestParam(required = false) String deptId){
        DataResult result=DataResult.success();
        result.setData(deptService.deptTreeList(deptId));
        return result;
    }

    @PostMapping("/dept")
    @ApiOperation(value = "新增部门接口")
    @RequiresPermissions("sys:dept:add")
    public DataResult<SysDept> addDept(@RequestBody @Valid DeptAddReqVO vo){
        DataResult<SysDept> result=DataResult.success();
        SysDept sysDept = deptService.addDept(vo);
        result.setData(sysDept);
        return result;
    }

    @PutMapping("/dept")
    @ApiOperation(value = "更新部门信息接口")
    @RequiresPermissions("sys:dept:update")
    public DataResult updateDept(@RequestBody @Valid DeptUpdateReqVO vo){
        deptService.updateDept(vo);
        return DataResult.success();
    }

    @DeleteMapping("/dept/{id}")
    @RequiresPermissions("sys:dept:delete")
    @ApiOperation(value = "删除部门接口")
    public DataResult deleted(@PathVariable("id") String id){
        deptService.deleted(id);
        return DataResult.success();
    }

}
