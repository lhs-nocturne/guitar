package com.heb.guitar.mapper;

import com.heb.guitar.entity.SysRolePermission;

import java.util.List;

public interface SysRolePermissionMapper {
    int deleteByPrimaryKey(String id);

    int insert(SysRolePermission record);

    int insertSelective(SysRolePermission record);

    SysRolePermission selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(SysRolePermission record);

    int updateByPrimaryKey(SysRolePermission record);

    //根据角色id移除和菜单权限关联接口
    int removeByRoleId(String roleId);
    //批量插入角色和菜单权限关联
    int batchRolePermission(List<SysRolePermission> list);
    //根据菜单权限id获取关联的角色id集合
    List<String> getRoleIdsByPermissionId(String permissionId);
    //根据permissionId 删除角色和菜单权限关联表相关数据
    int removeByPermissionId(String permissionId);
    //根据角色id获取该角色关联的菜单权限id集合
    List<String> getPermissionIdsByRoleId(String roleId);

    List<String> getPermissionIdsByRoles(List<String> roleIds);

}