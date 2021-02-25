package com.heb.guitar.mapper;

import com.heb.guitar.entity.SysPermission;

import java.util.List;

public interface SysPermissionMapper {
    int deleteByPrimaryKey(String id);

    int insert(SysPermission record);

    int insertSelective(SysPermission record);

    SysPermission selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(SysPermission record);

    int updateByPrimaryKey(SysPermission record);

    List<SysPermission> selectAll();
    //查询关联子类
    List<SysPermission> selectChild(String pid);

    List<SysPermission> selectInfoByIds (List<String> ids);

}