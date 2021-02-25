package com.heb.guitar.mapper;

import com.heb.guitar.entity.SysRole;
import com.heb.guitar.vo.req.RoleAddReqVO;
import com.heb.guitar.vo.req.RolePageReqVO;

import java.util.List;

public interface SysRoleMapper {
    int deleteByPrimaryKey(String id);

    int insert(SysRole record);

    int insertSelective(SysRole record);

    SysRole selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(SysRole record);

    int updateByPrimaryKey(SysRole record);

    List<SysRole> selectAll(RolePageReqVO vo);

    long roleCount(RolePageReqVO vo);

    List<SysRole> getRoleInfoByIds(List<String> ids);

}