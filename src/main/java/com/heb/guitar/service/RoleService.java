package com.heb.guitar.service;

import com.heb.guitar.entity.SysRole;
import com.heb.guitar.vo.req.RoleAddReqVO;
import com.heb.guitar.vo.req.RolePageReqVO;
import com.heb.guitar.vo.req.RoleUpdateReqVO;

import java.util.List;

public interface RoleService {

    List<SysRole> selectAll(RolePageReqVO vo);

    long roleCount(RolePageReqVO vo);

    SysRole addRole(RoleAddReqVO vo);
    //获取所有角色接口
    List<SysRole> selectAllRoles();

    SysRole detailInfo(String id);

    void updateRole(RoleUpdateReqVO vo);

    void deletedRole(String id);

    List<String> getRoleNames(String userId);

    List<SysRole> getRoleInfoByUserId(String userId);

    List<String> getRoleCodes(String userId);
}
