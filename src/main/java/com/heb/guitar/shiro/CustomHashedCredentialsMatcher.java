package com.heb.guitar.shiro;

import com.heb.guitar.constants.Constant;
import com.heb.guitar.exception.BusinessException;
import com.heb.guitar.exception.code.BaseResponseCode;
import com.heb.guitar.service.RedisService;
import com.heb.guitar.utils.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 注解
 * User: sai
 * Date: 2021/1/30
 * Time: 20:05
 */
@Slf4j
public class CustomHashedCredentialsMatcher extends HashedCredentialsMatcher {

    @Resource
    private RedisService redisService;

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        CustomUsernamePasswordToken customUsernamePasswordToken = (CustomUsernamePasswordToken) token;
        String accessToken = (String) customUsernamePasswordToken.getCredentials();
        String userId = JwtTokenUtil.getUserId(accessToken);
        //判断是否删除
        if(redisService.hasKey(Constant.DELETED_USER_KEY+userId)){
            throw new BusinessException(BaseResponseCode.ACCOUNT_HAS_DELETED_ERROR);
        }
        //判断是否被锁定
        if(redisService.hasKey(Constant.ACCOUNT_USER_KEY+userId)){
            throw new BusinessException(BaseResponseCode.USER_LOCK);
        }
        //校验token
        if(!JwtTokenUtil.validateToken(accessToken)){
            throw new BusinessException(BaseResponseCode.TOKEN_PASS_DUE);
        }
        /**
         * 判断token 是否主动登出
         */
        if(redisService.hasKey(Constant.JWT_ACCESS_TOKEN_BLACKLIST+accessToken)){
            throw new BusinessException(BaseResponseCode.TOKEN_NOT_NULL);
        }
        if(redisService.hasKey(Constant.JWT_REFRESH_KEY+userId)){
            /**
             * 通过剩余的过期时间比较如果token的剩余过期时间大与标记key的剩余过期时间
             * 就说明这个token是在这个标记key之后生成的
             */
            if(redisService.getExpire(Constant.JWT_REFRESH_KEY+userId, TimeUnit.MILLISECONDS)>JwtTokenUtil.getRemainingTime(accessToken)){
                throw new BusinessException(BaseResponseCode.TOKEN_PASS_DUE);
            }
        }
        return true;
    }
}
