package com.heb.guitar.shiro;

import com.heb.guitar.service.RedisService;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import javax.annotation.Resource;

/**
 * 注解
 * User: sai
 * Date: 2020/7/1
 * Time: 20:11
 */
public class ShiroCacheManager implements CacheManager {

    @Resource
    private RedisService redisService;

    @Override
    public <K, V> Cache<K, V> getCache(String s) throws CacheException {
        return new RedisCache<>(redisService);
    }
}
