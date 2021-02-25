package com.heb.guitar.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.time.Duration;

/**
 * 注解
 * User: sai
 * Date: 2021/1/30
 * Time: 16:24
 */
@Configuration
@ConfigurationProperties(prefix = "jwt")
@Data
public class TokenSettings {
    private String secretKey;
    private Duration accessTokenExpireTime;
    private Duration refreshTokenExpireTime;
    private Duration refreshTokenExpireAppTime;
    private String issuer;
}
