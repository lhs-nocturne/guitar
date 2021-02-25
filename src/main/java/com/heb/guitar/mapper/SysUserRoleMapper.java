package com.heb.guitar.mapper;

import com.heb.guitar.entity.SysUserRole;

import java.util.List;

public interface SysUserRoleMapper {
    int deleteByPrimaryKey(String id);

    int insert(SysUserRole record);

    int insertSelective(SysUserRole record);

    SysUserRole selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(SysUserRole record);

    int updateByPrimaryKey(SysUserRole record);

    /**
     * 通过用户查询关联的角色id集合
     * @param userId
     * @return
     */
    List<String> getRoleIdsByUserId(String userId);
    //根据用户id 删除和该用户关联的角色关联表数据
    int removeByUserId(String userId);
    //批量插入用户和角色关联数据
    int batchUserRole(List<SysUserRole> list);
    //根据角色id集合获取所有关联用户di集合
    List<String> getUserIdsByRoleIds(List<String> roleIds);
    //通过角色id 获取跟该角色关联的用户id
    List<String> getInfoByUserIdByRoleId( String roleId);
    //根据角色id 删除该角色关联菜单权限所有数据
    int removeByRoleId(String roleId);

}