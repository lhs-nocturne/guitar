package com.heb.guitar.service;

import com.heb.guitar.entity.SysPermission;
import com.heb.guitar.vo.req.PermissionAddReqVO;
import com.heb.guitar.vo.req.PermissionUpdateReqVO;
import com.heb.guitar.vo.resp.MenuInfoRespVO;
import com.heb.guitar.vo.resp.PermissionRespNodeVO;

import java.util.List;
import java.util.Set;

/**
 * 注解
 * User: sai
 * Date: 2021/2/2
 * Time: 13:44
 */
public interface PermissionService {

    List<SysPermission> selectAll();

    List<PermissionRespNodeVO> selectAllMenuByTree();

    SysPermission addPermission(PermissionAddReqVO vo);

    List<MenuInfoRespVO> permissionTreeList(String userId);

    void delPermissionById(String id);

    List<PermissionRespNodeVO> selectAllByTree();

    void updatePermission(PermissionUpdateReqVO vo);

    List<SysPermission> getPermission(String userId);

    Set<String> getPermissionsByUserId(String userId);

}
