package com.heb.guitar.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * 注解
 * User: sai
 * Date: 2021/1/30
 * Time: 21:42
 */
public class CustomUsernamePasswordToken extends UsernamePasswordToken {

    private String token;

    public CustomUsernamePasswordToken(String token) {
        this.token = token;
    }
    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials(){
        return token;
    }
}
