package com.heb.guitar.service.impl;

import com.heb.guitar.constants.Constant;
import com.heb.guitar.entity.SysPermission;
import com.heb.guitar.exception.BusinessException;
import com.heb.guitar.exception.code.BaseResponseCode;
import com.heb.guitar.mapper.SysPermissionMapper;
import com.heb.guitar.service.*;
import com.heb.guitar.utils.MenuTreeBuildUtil;
import com.heb.guitar.utils.TokenSettings;
import com.heb.guitar.vo.req.PermissionAddReqVO;
import com.heb.guitar.vo.req.PermissionUpdateReqVO;
import com.heb.guitar.vo.resp.MenuInfoRespVO;
import com.heb.guitar.vo.resp.PermissionRespNodeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 注解
 * User: sai
 * Date: 2021/2/2
 * Time: 13:44
 */
@Slf4j
@Service
public class PermissionServiceImpl implements PermissionService {

    @Resource
    private SysPermissionMapper sysPermissionMapper;
    @Resource
    private RolePermissionService rolePermissionService;
    @Resource
    private UserRoleService userRoleService;
    @Resource
    private TokenSettings tokenSettings;
    @Resource
    private RedisService redisService;

    @Override
    public List<SysPermission> selectAll() {
        List<SysPermission> result =sysPermissionMapper.selectAll();
        if(!result.isEmpty()){
            for(SysPermission sysPermission:result){
                SysPermission parent = sysPermissionMapper.selectByPrimaryKey(sysPermission.getPid());
                if(parent!=null){
                    sysPermission.setPidName(parent.getName());
                }
            }
        }
        return result;
    }

    @Override
    public List<PermissionRespNodeVO> selectAllMenuByTree() {
        List<SysPermission> list=selectAll();
        List<PermissionRespNodeVO> result=new ArrayList<>();
        //新增顶级目录是为了方便添加一级目录
        PermissionRespNodeVO respNode=new PermissionRespNodeVO();
        respNode.setId("0");
        respNode.setTitle("根目录");
        respNode.setSpread(true);
        respNode.setChildren(getTree(list,true));
        result.add(respNode);
        return result;
    }

    @Override
    public SysPermission addPermission(PermissionAddReqVO vo) {
        SysPermission sysPermission=new SysPermission();
        BeanUtils.copyProperties(vo,sysPermission);
        verifyForm(sysPermission);
        sysPermission.setId(UUID.randomUUID().toString());
        sysPermission.setCreateTime(new Date());
        int count=sysPermissionMapper.insertSelective(sysPermission);
        if (count!=1){
            throw new BusinessException(BaseResponseCode.OPERATION_ERROR);
        }
        return sysPermission;
    }

    @Override
    public List<MenuInfoRespVO> permissionTreeList(String userId) {
        List<SysPermission> list = getPermission(userId);
        return MenuTreeBuildUtil.getMenuTree(list);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delPermissionById(String permissionId) {
        SysPermission sysPermission = sysPermissionMapper.selectByPrimaryKey(permissionId);
        if(null==sysPermission){
            log.error("传入 的 id:{}不合法",permissionId);
            throw new BusinessException(BaseResponseCode.DATA_ERROR);
        }
        List<SysPermission> childs = sysPermissionMapper.selectChild(permissionId);
        if(!childs.isEmpty()){
            throw new BusinessException(BaseResponseCode.ROLE_PERMISSION_RELATION);
        }
        sysPermission.setDeleted(0);
        sysPermission.setUpdateTime(new Date());
        int count=sysPermissionMapper.updateByPrimaryKeySelective(sysPermission);
        if(count!=1){
            throw new BusinessException(BaseResponseCode.OPERATION_ERROR);
        }
        /**
         * 删除和角色关联
         */
        rolePermissionService.removeByPermissionId(permissionId);
        List<String> roleIds = rolePermissionService.getRoleIdsByPermissionId(permissionId);
        if(!roleIds.isEmpty()){
            List<String> userIds = userRoleService.getUserIdsByRoleIds(roleIds);
            if(!userIds.isEmpty()){
                for (String userId:userIds){
                    redisService.set(Constant.JWT_REFRESH_KEY+userId,userId,tokenSettings.getAccessTokenExpireTime().toMillis(), TimeUnit.MILLISECONDS);
                    /**
                     * 清楚用户授权数据缓存
                     */
                    redisService.delete(Constant.IDENTIFY_CACHE_KEY+userId);
                }
            }
        }
    }

    @Override
    public List<PermissionRespNodeVO> selectAllByTree() {
        List<SysPermission> list=selectAll();
        return getTree(list,false);
    }


    @Override
    public void updatePermission(PermissionUpdateReqVO vo) {
        SysPermission sysPermission = sysPermissionMapper.selectByPrimaryKey(vo.getId());
        if(sysPermission==null){
            log.info("传入的id不存在{}",vo.getId());
            throw new BusinessException(BaseResponseCode.DATA_ERROR);
        }
        SysPermission update = new SysPermission();
        BeanUtils.copyProperties(vo,update);
        //校验父级类型
        verifyForm(update);
        if(!sysPermission.getPid().equals(vo.getPid())){
            //如果该菜单权限关联了子集叶子节点的话我们就不让操作
            List<SysPermission> sysPermissions = sysPermissionMapper.selectChild(vo.getId());
            if(!sysPermissions.isEmpty()){
                throw new BusinessException(BaseResponseCode.OPERATION_MENU_PERMISSION_UPDATE);
            }
        }
        update.setUpdateTime(new Date());
        int i = sysPermissionMapper.updateByPrimaryKeySelective(update);
        if(i!=1){
            throw new BusinessException(BaseResponseCode.OPERATION_ERROR);
        }
        if(!vo.getPerms().equals(sysPermission.getPerms())){
            List<String> roleIdsByPermissionId = rolePermissionService.getRoleIdsByPermissionId(vo.getId());
            if(!roleIdsByPermissionId.isEmpty()){
                List<String> userIdsByRoleIds = userRoleService.getUserIdsByRoleIds(roleIdsByPermissionId);
                if(!userIdsByRoleIds.isEmpty()){
                    for (String userId: userIdsByRoleIds) {
                        redisService.set(Constant.JWT_REFRESH_KEY+userId,userId,tokenSettings.getAccessTokenExpireTime().toMillis(), TimeUnit.MILLISECONDS);
                        /**
                         * 清楚用户授权数据缓存
                         */
                        redisService.delete(Constant.IDENTIFY_CACHE_KEY+userId);
                    }
                }
            }
        }
    }

    @Override
    public List<SysPermission> getPermission(String userId) {
        List<String> roleIds = userRoleService.getRoleIdsByUserId(userId);
        if(roleIds.isEmpty()){
            return null;
        }
        List<String> permissionIds = rolePermissionService.getPermissionIdsByRoles(roleIds);
        if (permissionIds.isEmpty()){
            return null;
        }
        List<SysPermission> result = sysPermissionMapper.selectInfoByIds(permissionIds);
        return result;
    }

    @Override
    public Set<String> getPermissionsByUserId(String userId) {
        List<SysPermission> list=getPermission(userId);
        Set<String> permissions=new HashSet<>();
        if (null==list||list.isEmpty()){
            return null;
        }
        for (SysPermission sysPermission:list){
            if(!StringUtils.isEmpty(sysPermission.getPerms())){
                permissions.add(sysPermission.getPerms());
            }
        }
        return permissions;
    }


    /**
     * 递归获取表格菜单树,菜单权限树递归方法，新增一个参数type：true(只查询目录和菜单) false(查询目录、菜单、按钮权限)
     * @param all
     * @return
     */
    private List<PermissionRespNodeVO> getTree(List<SysPermission> all, boolean type){
        List<PermissionRespNodeVO> list=new ArrayList<>();
        if (all==null||all.isEmpty()){
            return list;
        }
        for(SysPermission sysPermission:all){
            if(sysPermission.getPid().equals("0")){
                PermissionRespNodeVO permissionRespNode=new PermissionRespNodeVO();
                BeanUtils.copyProperties(sysPermission,permissionRespNode);
                permissionRespNode.setTitle(sysPermission.getName());
                if(type){
                    permissionRespNode.setChildren(getChildExcBtn(sysPermission.getId(),all));
                }else {
                    permissionRespNode.setChildren(getChildAll(sysPermission.getId(),all));
                }

                list.add(permissionRespNode);
            }
        }
        return list;
    }

    /**
     * 递归遍历所有
     * @param id
     * @param all
     * @return
     */
    private List<PermissionRespNodeVO>getChildAll(String id,List<SysPermission> all){
        List<PermissionRespNodeVO> list=new ArrayList<>();
        for(SysPermission sysPermission:all){
            if(sysPermission.getPid().equals(id)){
                PermissionRespNodeVO permissionRespNode=new PermissionRespNodeVO();
                BeanUtils.copyProperties(sysPermission,permissionRespNode);
                permissionRespNode.setTitle(sysPermission.getName());
                permissionRespNode.setChildren(getChildAll(sysPermission.getId(),all));
                list.add(permissionRespNode);
            }
        }
        return list;
    }

    /**
     * 只递归获取目录和菜单
     * @param id
     * @param all
     * @return
     */
    private List<PermissionRespNodeVO> getChildExcBtn(String id,List<SysPermission> all){
        List<PermissionRespNodeVO> list=new ArrayList<>();
        for(SysPermission sysPermission:all){
            if(sysPermission.getPid().equals(id)&&sysPermission.getType()!=3){
                PermissionRespNodeVO permissionRespNode=new PermissionRespNodeVO();
                BeanUtils.copyProperties(sysPermission,permissionRespNode);
                permissionRespNode.setTitle(sysPermission.getName());
                permissionRespNode.setChildren(getChildExcBtn(sysPermission.getId(),all));
                list.add(permissionRespNode);
            }
        }
        return list;
    }



    /**
     * 操作后的菜单类型是目录的时候 父级必须为目录
     * 操作后的菜单类型是菜单的时候，父类必须为目录类型
     * 操作后的菜单类型是按钮的时候 父类必须为菜单类型
     */
    private void verifyForm(SysPermission sysPermission){
        SysPermission parent=sysPermissionMapper.selectByPrimaryKey(sysPermission.getPid());
        switch (sysPermission.getType()){
            case 1:
                if(parent!=null){
                    if(parent.getType()!=1){
                        throw new BusinessException(BaseResponseCode.OPERATION_MENU_PERMISSION_CATALOG_ERROR);
                    }
                }else if(!sysPermission.getPid().equals("0")){
                    throw new BusinessException(BaseResponseCode.OPERATION_MENU_PERMISSION_CATALOG_ERROR);
                }
                break;
            case 2:
                if(parent==null||parent.getType()!=1){
                    throw new BusinessException(BaseResponseCode.OPERATION_MENU_PERMISSION_MENU_ERROR);
                }
                if(StringUtils.isEmpty(sysPermission.getUrl())){
                    throw new BusinessException(BaseResponseCode.OPERATION_MENU_PERMISSION_URL_NOT_NULL);
                }
                break;
            case 3:
                if(parent==null||parent.getType()!=2){
                    throw new BusinessException(BaseResponseCode.OPERATION_MENU_PERMISSION_BTN_ERROR);
                }
                if(StringUtils.isEmpty(sysPermission.getPerms())){
                    throw new BusinessException(BaseResponseCode.OPERATION_MENU_PERMISSION_URL_PERMS_NULL);
                }
                if(StringUtils.isEmpty(sysPermission.getUrl())){
                    throw new BusinessException(BaseResponseCode.OPERATION_MENU_PERMISSION_URL_NOT_NULL);
                }
                if(StringUtils.isEmpty(sysPermission.getMethod())){
                    throw new BusinessException(BaseResponseCode.OPERATION_MENU_PERMISSION_URL_METHOD_NULL);
                }
                break;
        }
    }




}
