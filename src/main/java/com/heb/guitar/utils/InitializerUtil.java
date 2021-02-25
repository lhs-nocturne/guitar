package com.heb.guitar.utils;

import org.springframework.stereotype.Component;

/**
 * 注解
 * User: sai
 * Date: 2021/1/30
 * Time: 16:26
 */
@Component
public class InitializerUtil {
    private TokenSettings tokenSettings;
    public InitializerUtil(TokenSettings tokenSettings) {
        JwtTokenUtil.setTokenSettings(tokenSettings);
    }
}
