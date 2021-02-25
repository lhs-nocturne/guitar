package com.heb.guitar.shiro;


import com.heb.guitar.constants.Constant;
import com.heb.guitar.service.PermissionService;
import com.heb.guitar.service.RedisService;
import com.heb.guitar.service.RoleService;
import com.heb.guitar.utils.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;


/**
 * 注解
 * User: sai
 * Date: 2020/6/30
 * Time: 20:18
 */
public class CustomRealm extends AuthorizingRealm {

    @Resource
    private RoleService roleService;
    @Resource
    private PermissionService permissionService;
    @Resource
    private RedisService redisService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof CustomUsernamePasswordToken;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String accessToken = (String) principalCollection.getPrimaryPrincipal();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        String userId=JwtTokenUtil.getUserId(accessToken);
        /**
         * 通过剩余的过期时间比较如果token的剩余过期时间大与标记key的剩余过期时间
         * 就说明这个token是在这个标记key之后生成的
         */
        if(redisService.hasKey(Constant.JWT_REFRESH_KEY+userId)&&redisService.getExpire(Constant.JWT_REFRESH_KEY+userId, TimeUnit.MILLISECONDS)>JwtTokenUtil.getRemainingTime(accessToken)){
            List<String> roleCodes = roleService.getRoleCodes(userId);
            if(roleCodes!=null&&!roleCodes.isEmpty()){
                info.addRoles(roleCodes);
            }
            Set<String> permissions=permissionService.getPermissionsByUserId(userId);
            if(permissions!=null){
                info.addStringPermissions(permissions);
            }
        }else {
            Claims claims = JwtTokenUtil.getClaimsFromToken(accessToken);
            /**
             * 返回该用户的权限信息给授权器
             */
            if(claims.get(Constant.JWT_PERMISSIONS_KEY)!=null){
                info.addStringPermissions((Collection<String>) claims.get(Constant.JWT_PERMISSIONS_KEY));
            }
            /**
             * 返回该用户的角色信息给授权器
             */
            if(claims.get(Constant.JWT_ROLES_KEY)!=null){
                info.addRoles((Collection<String>) claims.get(Constant.JWT_ROLES_KEY));
            }
        }
        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        CustomUsernamePasswordToken customUsernamePasswordToken = (CustomUsernamePasswordToken) authenticationToken;
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(customUsernamePasswordToken.getPrincipal(),
                customUsernamePasswordToken.getCredentials(),CustomRealm.class.getName());
        return info;
    }


}
