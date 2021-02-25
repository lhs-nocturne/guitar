package com.heb.guitar.service;


import com.heb.guitar.entity.SysUserRole;
import com.heb.guitar.vo.req.UserRoleOperationReqVO;
import java.util.List;

public interface UserRoleService {

    List<String> getRoleIdsByUserId(String userId);

    void addUserRoleInfo(UserRoleOperationReqVO vo);

    List<String> getUserIdsByRoleIds(List<String> roleIds);

}
