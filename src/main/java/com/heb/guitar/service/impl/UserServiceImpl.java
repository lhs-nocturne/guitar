package com.heb.guitar.service.impl;

import com.heb.guitar.constants.Constant;
import com.heb.guitar.entity.SysRole;
import com.heb.guitar.entity.SysUser;
import com.heb.guitar.exception.BusinessException;
import com.heb.guitar.exception.code.BaseResponseCode;
import com.heb.guitar.mapper.SysUserMapper;
import com.heb.guitar.service.*;
import com.heb.guitar.utils.JwtTokenUtil;
import com.heb.guitar.utils.PasswordUtils;
import com.heb.guitar.utils.TokenSettings;
import com.heb.guitar.vo.req.*;
import com.heb.guitar.vo.resp.LoginRespVO;
import com.heb.guitar.vo.resp.UserOwnRoleRespVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
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
 * Date: 2021/1/30
 * Time: 17:04
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private RedisService redisService;
    @Resource
    private UserRoleService userRoleService;
    @Resource
    private RoleService roleService;
    @Resource
    private TokenSettings tokenSettings;
    @Resource
    private PermissionService permissionService;

    @Override
    public LoginRespVO login(LoginReqVO vo) {
        SysUser sysUser=sysUserMapper.getUserInfoByName(vo.getUsername());
        if (null==sysUser){
            throw new BusinessException(BaseResponseCode.NOT_ACCOUNT);
        }
        if (sysUser.getStatus()==2){
            throw new BusinessException(BaseResponseCode.USER_LOCK);
        }
        if(!PasswordUtils.matches(sysUser.getSalt(),vo.getPassword(),sysUser.getPassword())){
            throw new BusinessException(BaseResponseCode.PASSWORD_ERROR);
        }
        LoginRespVO respVO=new LoginRespVO();
        BeanUtils.copyProperties(sysUser,respVO);
        Map<String,Object> claims=new HashMap<>();
        claims.put(Constant.JWT_PERMISSIONS_KEY,getPermissionsByUserId(sysUser.getId()));
        claims.put(Constant.JWT_ROLES_KEY,getRolesByUserId(sysUser.getId()));
        claims.put(Constant.JWT_USER_NAME,sysUser.getUsername());
        String access_token= JwtTokenUtil.getAccessToken(sysUser.getId(),claims);
        Map<String,Object> refreshClaims=new HashMap<>();
        refreshClaims.put(Constant.JWT_USER_NAME,sysUser.getUsername());
        String refresh_token;
        if(vo.getType().equals("1")){
            refresh_token=JwtTokenUtil.getRefreshToken(sysUser.getId(),refreshClaims);
        }else {
            refresh_token=JwtTokenUtil.getRefreshAppToken(sysUser.getId(),refreshClaims);
        }
        respVO.setAccessToken(access_token);
        respVO.setRefreshToken(refresh_token);
        return respVO;
    }

    @Override
    public void logout(String accessToken, String refreshToken) {
        if(StringUtils.isEmpty(accessToken)||StringUtils.isEmpty(refreshToken)){
            throw new BusinessException(BaseResponseCode.DATA_ERROR);
        }
        Subject subject = SecurityUtils.getSubject();
        log.info("subject.getPrincipals()={}",subject.getPrincipals());
        if (subject.isAuthenticated()) {
            subject.logout();
        }
        String userId=JwtTokenUtil.getUserId(accessToken);
        /**
         * 把token 加入黑名单 禁止再登录
         */
        redisService.set(Constant.JWT_ACCESS_TOKEN_BLACKLIST+accessToken,userId,JwtTokenUtil.getRemainingTime(accessToken), TimeUnit.MILLISECONDS);
        /**
         * 把 refreshToken 加入黑名单 禁止再拿来刷新token
         */
        redisService.set(Constant.JWT_REFRESH_TOKEN_BLACKLIST+refreshToken,userId,JwtTokenUtil.getRemainingTime(refreshToken),TimeUnit.MILLISECONDS);
    }

    /**
     * 分页查询用户信息
     */
    @Override
    public List<SysUser> getUserList(UserReqVO vo) {
        List<SysUser> sysUsers = sysUserMapper.selectAll(vo);
        return sysUsers;
    }

    @Override
    public long userCount(UserReqVO vo) {
        long count = sysUserMapper.userCount(vo);
        return count;
    }

    @Override
    public void addUser(UserAddReqVO vo) {
        SysUser sysUser=new SysUser();
        //BeanUtils.copyProperties(vo,sysUser);
        sysUser.setPhone(vo.getPhone());
        sysUser.setStatus(vo.getStatus());
        sysUser.setCreateWhere(vo.getCreateWhere());
        sysUser.setDeptId(vo.getDeptId());
        sysUser.setUsername(vo.getUsername());
        sysUser.setSex(vo.getSex());
        sysUser.setSalt(PasswordUtils.getSalt());
        String encode = PasswordUtils.encode(vo.getPassword(), sysUser.getSalt());
        sysUser.setPassword(encode);
        sysUser.setId(UUID.randomUUID().toString());
        sysUser.setCreateTime(new Date());
        int i = sysUserMapper.insertSelective(sysUser);
        if (i!=1){
            throw new BusinessException(BaseResponseCode.OPERATION_ERROR);
        }
    }

    @Override
    public UserOwnRoleRespVO getUserOwnRole(String userId) {
        List<String> roleIdsByUserId = userRoleService.getRoleIdsByUserId(userId);
        List<SysRole> list = roleService.selectAllRoles();
        UserOwnRoleRespVO vo=new UserOwnRoleRespVO();
        vo.setAllRole(list);
        vo.setOwnRoles(roleIdsByUserId);
        return vo;
    }

    @Override
    public void setUserOwnRole(UserRoleOperationReqVO vo) {
        userRoleService.addUserRoleInfo(vo);
        /**
         * 标记用户 要主动去刷新
         */
        redisService.set(Constant.JWT_REFRESH_KEY+vo.getUserId(),vo.getUserId(),tokenSettings.getAccessTokenExpireTime().toMillis(),TimeUnit.MILLISECONDS);
        /**
         * 清楚用户授权数据缓存
         */
        redisService.delete(Constant.IDENTIFY_CACHE_KEY+vo.getUserId());
    }

    @Override
    public String refreshToken(String refreshToken) {
        /**
         * refreshToken 它是否过期
         * 它是否被加如了黑名
         */
        if(!JwtTokenUtil.validateToken(refreshToken)||redisService.hasKey(Constant.JWT_REFRESH_TOKEN_BLACKLIST+refreshToken)){
            throw new BusinessException(BaseResponseCode.TOKEN_ERROR);
        }
        String userId = JwtTokenUtil.getUserId(refreshToken);
        log.info("userId={}",userId);
        Map<String,Object> claims=null;
        /**
         * 用户主动去刷新
         * 更新角色/权限信息
         */
        if(redisService.hasKey(Constant.JWT_REFRESH_KEY+userId)){
            claims=new HashMap<>();
            claims.put(Constant.JWT_ROLES_KEY,getRolesByUserId(userId));
            claims.put(Constant.JWT_PERMISSIONS_KEY,getPermissionsByUserId(userId));
        }
        String newAccessToken = JwtTokenUtil.refreshToken(refreshToken,claims);
        /**
         * 如果是主动去刷新着 redis 标记新的access_token
         * 过期时间为 key=Constant.JWT_REFRESH_KEY+userId 的剩余过期时间
        if(redisService.hasKey(Constant.JWT_REFRESH_KEY+userId)){
            redisService.set(Constant.JWT_REFRESH_IDENTIFICATION+newAccessToken,userId,redisService.getExpire(Constant.JWT_REFRESH_KEY+userId,TimeUnit.MILLISECONDS),TimeUnit.MILLISECONDS);
        }*/
        return newAccessToken;
    }

    @Override
    public void updateUserInfo(UserUpdateReqVO vo, String operationId) {
        SysUser sysUser=sysUserMapper.selectByPrimaryKey(vo.getId());
        if (null==sysUser){
            log.error("传入 的 id:{}不合法",vo.getId());
            throw new BusinessException(BaseResponseCode.DATA_ERROR);
        }
        BeanUtils.copyProperties(vo,sysUser);
        sysUser.setUpdateTime(new Date());
        if(!StringUtils.isEmpty(vo.getPassword())){
            String newPassword = PasswordUtils.encode(vo.getPassword(),sysUser.getSalt());
            sysUser.setPassword(newPassword);
        }else {
            sysUser.setPassword(null);
        }
        sysUser.setUpdateId(operationId);
        int count= sysUserMapper.updateByPrimaryKeySelective(sysUser);
        if (count!=1){
            throw new BusinessException(BaseResponseCode.OPERATION_ERROR);
        }
        /**
         * 说明用户状态有改变
         * 加入变成禁用，之前
         * 签发的token 都要失效
         */
        if(vo.getStatus()==2){
            redisService.set(Constant.ACCOUNT_LOCK_KEY+sysUser.getId(),sysUser.getId());
        }else {
            redisService.delete(Constant.ACCOUNT_LOCK_KEY+sysUser.getId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletedUsers(String id, String operationId) {
        SysUser sysUser=new SysUser();
        sysUser.setId(id);
        sysUser.setUpdateId(operationId);
        sysUser.setUpdateTime(new Date());
        sysUser.setDeleted(0);
        sysUserMapper.updateByPrimaryKeySelective(sysUser); //删除操作，执行逻辑删除，将DELETED值置为0
        redisService.set(Constant.DELETED_USER_KEY+id,id,tokenSettings.getRefreshTokenExpireAppTime().toMillis(),TimeUnit.MILLISECONDS);
        /**
         * 清楚用户授权数据缓存
         */
        redisService.delete(Constant.IDENTIFY_CACHE_KEY+id);
    }

    @Override
    public SysUser detailInfo(String userId) {
        return sysUserMapper.selectByPrimaryKey(userId);
    }

    @Override
    public void userUpdateDetailInfo(UserUpdateDetailInfoReqVO vo, String userId) {
        SysUser sysUser=new SysUser();
        BeanUtils.copyProperties(vo,sysUser);
        sysUser.setId(userId);
        sysUser.setUpdateTime(new Date());
        sysUser.setUpdateId(userId);
        int count= sysUserMapper.updateByPrimaryKeySelective(sysUser);
        if (count!=1){
            throw new BusinessException(BaseResponseCode.OPERATION_ERROR);
        }
    }

    @Override
    public void updatePwd(UpdatePasswordReqVO vo, String userId, String accessToken, String refreshToken) {
        SysUser sysUser=sysUserMapper.selectByPrimaryKey(userId);
        if(sysUser==null){
            throw new BusinessException(BaseResponseCode.DATA_ERROR);
        }
        if(!PasswordUtils.matches(sysUser.getSalt(), vo.getOldPwd(), sysUser.getPassword())){
            throw new BusinessException(BaseResponseCode.OLD_PASSWORD_ERROR);
        }
        sysUser.setUpdateTime(new Date());
        sysUser.setPassword(PasswordUtils.encode(vo.getNewPwd(), sysUser.getSalt()));
        int i = sysUserMapper.updateByPrimaryKeySelective(sysUser);
        if(i!=1){
            throw new BusinessException(BaseResponseCode.OPERATION_ERROR);
        }
        /**
         * 把token 加入黑名单 禁止再登录
         */
        redisService.set(Constant.JWT_REFRESH_TOKEN_BLACKLIST+accessToken,userId,JwtTokenUtil.getRemainingTime(accessToken),TimeUnit.MILLISECONDS);
        /**
         * 把 refreshToken 加入黑名单 禁止再拿来刷新token
         */
        redisService.set(Constant.JWT_REFRESH_TOKEN_BLACKLIST+refreshToken,userId,JwtTokenUtil.getRemainingTime(refreshToken),TimeUnit.MILLISECONDS);
        /**
         * 清楚用户授权数据缓存
         */
        redisService.delete(Constant.IDENTIFY_CACHE_KEY+userId);
    }


    /**
     * 获取用户的角色
     */
    private List<String> getRolesByUserId(String userId){
        return roleService.getRoleCodes(userId);
    }

    /**
     * 获取用户的权限
     */
    private Set<String>getPermissionsByUserId(String userId){
        return permissionService.getPermissionsByUserId(userId);
    }

}
