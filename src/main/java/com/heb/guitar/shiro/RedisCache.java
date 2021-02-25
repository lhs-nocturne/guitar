package com.heb.guitar.shiro;

import com.alibaba.fastjson.JSON;
import com.heb.guitar.constants.Constant;
import com.heb.guitar.service.RedisService;
import com.heb.guitar.utils.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.util.CollectionUtils;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 注解
 * User: sai
 * Date: 2020/7/1
 * Time: 20:10
 */
@Slf4j
public class RedisCache <K,V> implements Cache <K,V> {

    private RedisService redisService;
    private long expire = 24;
    private String cacheKey;

    public RedisCache(RedisService redisService){
        this.cacheKey = Constant.IDENTIFY_CACHE_KEY;
        this.redisService = redisService;
    }

    @Override
    public V get(K key) throws CacheException {
        log.info("Shiro从缓存中获取数据KEY值[{}]",key);
        if (key == null) {
            return null;
        }
        try {
            String redisCacheKey = getCacheKey(key);
            Object rawValue = redisService.get(redisCacheKey);
            if (rawValue == null) {
                return null;
            }
            SimpleAuthorizationInfo simpleAuthenticationInfo=
                    JSON.parseObject(rawValue.toString(),SimpleAuthorizationInfo.class);
            V value = (V) simpleAuthenticationInfo;
            return value;
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public V put(K key, V value) throws CacheException {
        log.info("put key [{}]",key);
        if (key == null) {
            log.warn("Saving a null key is meaningless, return value directly without call Redis.");
            return value;
        }
        try {
            String redisCacheKey = getCacheKey(key);
            redisService.set(redisCacheKey, value != null ? value : null, expire, TimeUnit.HOURS);
            return value;
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public V remove(K key) throws CacheException {
        log.info("remove key [{}]",key);
        if (key == null) {
            return null;
        }
        try {
            String redisCacheKey = getCacheKey(key);
            Object rawValue = redisService.get(redisCacheKey);
            V previous = (V) rawValue;
            redisService.delete(redisCacheKey);
            return previous;
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public void clear() throws CacheException {
        log.debug("clear cache");
        Set<String> keys = null;
        try {
            keys = redisService.keys(this.cacheKey + "*");
        } catch (Exception e) {
            log.error("get keys error", e);
        }
        if (keys == null || keys.size() == 0) {
            return;
        }
        for (String key: keys) {
            redisService.delete(key);
        }
    }

    @Override
    public int size() {
        int result = 0;
        try {
            result = redisService.keys(this.cacheKey + "*").size();
        } catch (Exception e) {
            log.error("get keys error", e);
        }
        return result;
    }

    @Override
    public Set<K> keys() {
        Set<String> keys = null;
        try {
            keys = redisService.keys(this.cacheKey + "*");
        } catch (Exception e) {
            log.error("get keys error", e);
            return Collections.emptySet();
        }
        if (CollectionUtils.isEmpty(keys)) {
            return Collections.emptySet();
        }
        Set<K> convertedKeys = new HashSet<>();
        for (String key:keys) {
            try {
                convertedKeys.add((K) key);
            } catch (Exception e) {
                log.error("deserialize keys error", e);
            }
        }
        return convertedKeys;
    }

    @Override
    public Collection<V> values() {
        Set<String> keys = null;
        try {
            keys = redisService.keys(this.cacheKey + "*");
        } catch (Exception e) {
            log.error("get values error", e);
            return Collections.emptySet();
        }
        if (CollectionUtils.isEmpty(keys)) {
            return Collections.emptySet();
        }
        List<V> values = new ArrayList<V>(keys.size());
        for (String key : keys) {
            V value = null;
            try {
                value = (V) redisService.get(key);
            } catch (Exception e) {
                log.error("deserialize values= error", e);
            }
            if (value != null) {
                values.add(value);
            }
        }
        return Collections.unmodifiableList(values);
    }

    private String getCacheKey(K token){
        if(null == token){
            return null;
        }else {
            return this.cacheKey+JwtTokenUtil.getUserId(token.toString());
        }
    }


}
