package com.heb.guitar.controller;

import com.heb.guitar.entity.SysRole;
import com.heb.guitar.service.RoleService;
import com.heb.guitar.utils.DataResult;
import com.heb.guitar.vo.req.RoleAddReqVO;
import com.heb.guitar.vo.req.RolePageReqVO;
import com.heb.guitar.vo.req.RoleUpdateReqVO;
import com.heb.guitar.vo.resp.PageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;

@RequestMapping("/api")
@RestController
@Api(tags = "组织模块-角色管理")
public class RoleController {

    @Resource
    private RoleService roleService;

    @PostMapping("/roles")
    @ApiOperation(value = "分页获取角色信息接口")
    @RequiresPermissions("sys:role:list")
    public DataResult pageInfo(@RequestBody RolePageReqVO vo){
        DataResult result= DataResult.success();
        PageVO pageVO = new PageVO();
        pageVO.setTotalRows(roleService.roleCount(vo));
        pageVO.setList(roleService.selectAll(vo));
        result.setData(pageVO);
        return result;
    }

    @PostMapping("/role")
    @ApiOperation(value = "新增角色接口")
    @RequiresPermissions("sys:role:add")
    public DataResult<SysRole> addRole(@RequestBody @Valid RoleAddReqVO vo) throws InvocationTargetException, IllegalAccessException {
        DataResult<SysRole> result=DataResult.success();
        result.setData(roleService.addRole(vo));
        return result;
    }

    @GetMapping("/role/{id}")
    @ApiOperation(value = "查询角色详情接口")
    @RequiresPermissions("sys:role:detail")
    public DataResult<SysRole> detailInfo(@PathVariable("id") String id){
        DataResult<SysRole> result=DataResult.success();
        result.setData(roleService.detailInfo(id));
        return result;
    }

    @PutMapping("/role")
    @ApiOperation(value = "更新角色信息接口")
    @RequiresPermissions("sys:role:update")
    public DataResult updateRole(@RequestBody @Valid RoleUpdateReqVO vo){
        roleService.updateRole(vo);
        return DataResult.success();
    }

    @DeleteMapping("/role/{id}")
    @ApiOperation(value = "删除角色接口")
    @RequiresPermissions("sys:role:delete")
    public DataResult deleted(@PathVariable("id") String id){
        roleService.deletedRole(id);
        return DataResult.success();
    }

}
