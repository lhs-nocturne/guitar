package com.heb.guitar.mapper;

import com.heb.guitar.entity.SysUser;
import com.heb.guitar.vo.req.UserReqVO;
import java.util.List;

public interface SysUserMapper {
    int deleteByPrimaryKey(String id);

    int insert(SysUser record);

    int insertSelective(SysUser record);

    SysUser selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(SysUser record);

    int updateByPrimaryKey(SysUser record);

    SysUser getUserInfoByName(String username);

    List<SysUser> selectAll(UserReqVO vo);

    long userCount(UserReqVO vo);

    //根据部门id集合查找用户
    List<SysUser> selectUserInfoByDeptIds (List<String> deptIds);

    SysUser getUserByUsername(String username);

}