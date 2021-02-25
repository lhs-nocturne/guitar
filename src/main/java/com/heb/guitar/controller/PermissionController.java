package com.heb.guitar.controller;

import com.heb.guitar.entity.SysPermission;
import com.heb.guitar.service.PermissionService;
import com.heb.guitar.utils.DataResult;
import com.heb.guitar.vo.req.PermissionAddReqVO;
import com.heb.guitar.vo.req.PermissionUpdateReqVO;
import com.heb.guitar.vo.resp.PageVO;
import com.heb.guitar.vo.resp.PermissionRespNodeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 注解
 * User: sai
 * Date: 2021/2/2
 * Time: 13:52
 */
@RequestMapping("/api")
@RestController
@Api(tags = "系统模块-菜单权限管理")
public class PermissionController {
    @Resource
    private PermissionService permissionService;

    @GetMapping("/permissions")
    @ApiOperation(value = "获取所有菜单权限接口")
    @RequiresPermissions("sys:permission:list")
    public DataResult<List<SysPermission>> getAllMenusPermission(){
        List<SysPermission> list = permissionService.selectAll();
        DataResult<List<SysPermission>> result= DataResult.success(list);
        return result;
    }

    @GetMapping("/permission/tree")
    @ApiOperation(value = "获取所有目录菜单树接口-查到到目录")
    @RequiresPermissions(value = {"sys:permission:update","sys:permission:add"},logical = Logical.OR)
    public DataResult<List<PermissionRespNodeVO>> getAllMenusPermissionTree(){
        DataResult<List<PermissionRespNodeVO>> result=DataResult.success();
        result.setData(permissionService.selectAllMenuByTree());
        return result;
    }

    @PostMapping("/permission")
    @ApiOperation(value = "新增菜单权限接口")
    @RequiresPermissions("sys:permission:add")
    public DataResult<SysPermission> addPermission(@RequestBody @Valid PermissionAddReqVO vo) {
        DataResult<SysPermission> result=DataResult.success();
        result.setData(permissionService.addPermission(vo));
        return result;
    }

    @DeleteMapping("/permission/{permissionId}")
    @ApiOperation(value = "删除菜单权限接口")
    @RequiresPermissions("sys:permission:delete")
    public DataResult delPermission(@PathVariable("permissionId") String permissionId) {
        DataResult<SysPermission> result=DataResult.success();
        permissionService.delPermissionById(permissionId);
        return result;
    }

    @GetMapping("/permission/tree/all")
    @ApiOperation(value = "获取所有目录菜单树接口-查询到按钮")
    @RequiresPermissions(value = {"sys:role:update","sys:role:add"},logical = Logical.OR)
    public DataResult<List<PermissionRespNodeVO>> getAllPermissionTree(){
        DataResult<List<PermissionRespNodeVO>> result=DataResult.success();
        List<PermissionRespNodeVO> list = permissionService.selectAllByTree();
        result.setData(list);
        return result;
    }

    @PutMapping("/permission")
    @ApiOperation(value = "更新菜单权限接口")
    @RequiresPermissions("sys:permission:update")
    public DataResult updatePermission(@RequestBody @Valid PermissionUpdateReqVO vo){
        DataResult result=DataResult.success();
        permissionService.updatePermission(vo);
        return result;
    }


}
