package com.heb.guitar.service.impl;

import com.heb.guitar.constants.Constant;
import com.heb.guitar.entity.SysRole;
import com.heb.guitar.exception.BusinessException;
import com.heb.guitar.exception.code.BaseResponseCode;
import com.heb.guitar.mapper.SysRoleMapper;
import com.heb.guitar.mapper.SysUserRoleMapper;
import com.heb.guitar.service.*;
import com.heb.guitar.utils.TokenSettings;
import com.heb.guitar.vo.req.RoleAddReqVO;
import com.heb.guitar.vo.req.RolePageReqVO;
import com.heb.guitar.vo.req.RolePermissionOperationReqVO;
import com.heb.guitar.vo.req.RoleUpdateReqVO;
import com.heb.guitar.vo.resp.PermissionRespNodeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RoleServiceImpl implements RoleService {

    @Resource
    private SysRoleMapper sysRoleMapper;
    @Resource
    private RolePermissionService rolePermissionService;
    @Resource
    private PermissionService permissionService;
    @Resource
    private SysUserRoleMapper sysUserRoleMapper;
    @Resource
    private RedisService redisService;
    @Resource
    private TokenSettings tokenSettings;
    @Resource
    private UserRoleService userRoleService;

    @Override
    public List<SysRole> selectAll(RolePageReqVO vo) {
        return sysRoleMapper.selectAll(vo);
    }

    @Override
    public long roleCount(RolePageReqVO vo) {
        return sysRoleMapper.roleCount(vo);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public SysRole addRole(RoleAddReqVO vo){
        SysRole sysRole=new SysRole();
        sysRole.setName(vo.getName());
        sysRole.setDescription(vo.getDescription());
        sysRole.setStatus(vo.getStatus());
        sysRole.setId(UUID.randomUUID().toString());
        sysRole.setCreateTime(new Date());
        int count= sysRoleMapper.insertSelective(sysRole);
        if(count!=1){
            throw new BusinessException(BaseResponseCode.OPERATION_ERROR);
        }
        if(null!=vo.getPermissions()&&!vo.getPermissions().isEmpty()){
            RolePermissionOperationReqVO reqVO=new RolePermissionOperationReqVO();
            reqVO.setRoleId(sysRole.getId());
            reqVO.setPermissionIds(vo.getPermissions());
            rolePermissionService.addRolePermission(reqVO);
        }
        return sysRole;
    }

    @Override
    public List<SysRole> selectAllRoles() {
        return sysRoleMapper.selectAll(new RolePageReqVO());
    }

    @Override
    public SysRole detailInfo(String id) {
        SysRole sysRole = sysRoleMapper.selectByPrimaryKey(id);
        if(sysRole==null){
            log.error("传入 的 id:{}不合法",id);
            throw new BusinessException(BaseResponseCode.DATA_ERROR);
        }
        List<PermissionRespNodeVO> permissionRespNodes = permissionService.selectAllByTree();
        Set<String> checkList=new HashSet<>(rolePermissionService.getPermissionIdsByRoleId(sysRole.getId()));
        setChecked(permissionRespNodes,checkList);
        sysRole.setPermissionRespNode(permissionRespNodes);
        return sysRole;
    }

    @Override
    public void updateRole(RoleUpdateReqVO vo) {
        SysRole sysRole=sysRoleMapper.selectByPrimaryKey(vo.getId());
        if (null==sysRole){
            log.error("传入 的 id:{}不合法",vo.getId());
            throw new BusinessException(BaseResponseCode.DATA_ERROR);
        }
        BeanUtils.copyProperties(vo,sysRole);
        sysRole.setUpdateTime(new Date());
        int count=sysRoleMapper.updateByPrimaryKeySelective(sysRole);
        if(count!=1){
            throw new BusinessException(BaseResponseCode.OPERATION_ERROR);
        }
        RolePermissionOperationReqVO reqVO=new RolePermissionOperationReqVO();
        reqVO.setRoleId(sysRole.getId());
        reqVO.setPermissionIds(vo.getPermissions());
        rolePermissionService.addRolePermission(reqVO);
        List<String> userIds = sysUserRoleMapper.getInfoByUserIdByRoleId(vo.getId());
        /**
         * 修改了角色关联的菜单权限后 要主动去刷新token
         * 因为用户所拥有的菜单权限是通过角色去关联的
         * 所以要把跟这个角色关联的用户 都要重新刷新token
         */
        if(!userIds.isEmpty()){
            for (String userId:userIds){
                /**
                 * 标记用户 在用户认证的时候判断这个是否主动刷过
                 */
                redisService.set(Constant.JWT_REFRESH_KEY+userId,userId,tokenSettings.getAccessTokenExpireTime().toMillis(), TimeUnit.MILLISECONDS);
                /**
                 * 清楚用户授权数据缓存
                 */
                redisService.delete(Constant.IDENTIFY_CACHE_KEY+userId);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deletedRole(String id) {
        SysRole sysRole=new SysRole();
        sysRole.setId(id);
        sysRole.setUpdateTime(new Date());
        sysRole.setDeleted(0);
        int count=sysRoleMapper.updateByPrimaryKeySelective(sysRole);
        if (count!=1){
            throw new BusinessException(BaseResponseCode.OPERATION_ERROR);
        }
        List<String> userIds=sysUserRoleMapper.getInfoByUserIdByRoleId(id);
        sysUserRoleMapper.removeByRoleId(id);
        rolePermissionService.removeByRoleId(id);
        /**
         * 刪除角色后 要主动去刷新跟該角色有关联用户的token
         * 因为用户所拥有的菜单权限是通过角色去关联的
         * 所以要把跟这个角色关联的用户 都要重新刷新token
         */
        if(!userIds.isEmpty()){
            for (String userId:userIds){
                /**
                 * 标记用户 在用户认证的时候判断这个是否主动刷过
                 */
                redisService.set(Constant.JWT_REFRESH_KEY+userId,userId,tokenSettings.getAccessTokenExpireTime().toMillis(), TimeUnit.MILLISECONDS);
                /**
                 * 清楚用户授权数据缓存
                 */
                redisService.delete(Constant.IDENTIFY_CACHE_KEY+userId);
            }
        }
    }

    @Override
    public List<String> getRoleNames(String userId) {
        List<SysRole> sysRoles=getRoleInfoByUserId(userId);
        if (null==sysRoles||sysRoles.isEmpty()){
            return null;
        }
        List<String> list=new ArrayList<>();
        for (SysRole sysRole:sysRoles){
            list.add(sysRole.getName());
        }
        return list;
    }

    @Override
    public List<SysRole> getRoleInfoByUserId(String userId) {
        List<String> roleIds=userRoleService.getRoleIdsByUserId(userId);
        if (roleIds.isEmpty()){
            return null;
        }
        return sysRoleMapper.getRoleInfoByIds(roleIds);
    }

    private void setChecked(List<PermissionRespNodeVO> list, Set<String> checkList){
        for(PermissionRespNodeVO node:list){
            /**
             * 子集选中从它往上到跟目录都被选中，父级选中从它到它所有的叶子节点都会被选中
             * 这样我们就直接遍历最底层及就可以了
             */
            if(checkList.contains(node.getId())&& (node.getChildren()==null||node.getChildren().isEmpty())){
                node.setChecked(true);
            }
            setChecked((List<PermissionRespNodeVO>) node.getChildren(),checkList);
        }
    }


}
