package com.heb.guitar.service.impl;

import com.heb.guitar.entity.SysRolePermission;
import com.heb.guitar.exception.BusinessException;
import com.heb.guitar.exception.code.BaseResponseCode;
import com.heb.guitar.mapper.SysRolePermissionMapper;
import com.heb.guitar.service.RolePermissionService;
import com.heb.guitar.vo.req.RolePermissionOperationReqVO;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class RolePermissionServiceImpl implements RolePermissionService {

    @Resource
    private SysRolePermissionMapper sysRolePermissionMapper;

    @Override
    public void addRolePermission(RolePermissionOperationReqVO vo) {
        sysRolePermissionMapper.removeByRoleId(vo.getRoleId());
        if(vo.getPermissionIds()==null||vo.getPermissionIds().isEmpty()){
            return;
        }
        Date createTime=new Date();
        List<SysRolePermission> list=new ArrayList<>();
        for (String permissionId:vo.getPermissionIds()){
            SysRolePermission sysRolePermission=new SysRolePermission();
            sysRolePermission.setId(UUID.randomUUID().toString());
            sysRolePermission.setCreateTime(createTime);
            sysRolePermission.setPermissionId(permissionId);
            sysRolePermission.setRoleId(vo.getRoleId());
            list.add(sysRolePermission);
        }
        int count=sysRolePermissionMapper.batchRolePermission(list);
        if (count==0){
            throw new BusinessException(BaseResponseCode.OPERATION_ERROR);
        }
    }

    @Override
    public List<String> getRoleIdsByPermissionId(String permissionId) {
        return sysRolePermissionMapper.getRoleIdsByPermissionId(permissionId);
    }

    @Override
    public int removeByPermissionId(String permissionId) {
        return sysRolePermissionMapper.removeByPermissionId(permissionId);
    }

    @Override
    public List<String> getPermissionIdsByRoleId(String roleId) {
        return sysRolePermissionMapper.getPermissionIdsByRoleId(roleId);
    }

    @Override
    public int removeByRoleId(String roleId) {
        return sysRolePermissionMapper.removeByRoleId(roleId);
    }

    @Override
    public List<String> getPermissionIdsByRoles(List<String> roleIds) {
        return sysRolePermissionMapper.getPermissionIdsByRoles(roleIds);
    }


}
