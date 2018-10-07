package com.imooc.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RedisLock {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 对key加锁，value为过期时间，单位是毫秒。时间到期后，key/value值还在redis中，但是会被其它线程重新设置value值。
     *
     * @param key   对key加锁
     * @param value 加锁时的时间 + 超时时间
     * @return 是否成功加锁
     */
    public boolean lock(String key, String value) {
        // 如果可以setnx，说明redis中还没有这个key, 成功对key加锁，返回true
        if (redisTemplate.opsForValue().setIfAbsent(key, value)) {
            //被锁定
            return true;
        }

        // 以下代码为了避免业务执行过程中抛出异常而没有正确的释放锁导致出现阻塞死锁情况。解决方法是判断当前key的过期时间是否到了。

        String currentValue = redisTemplate.opsForValue().get(key);
        // 如果锁过期(value(加锁时的时间 + 超时时间)小于当前时间，则锁过期. 可以进入这个条件判断，有可能return true，解开了死锁)
        if (!StringUtils.isEmpty(currentValue) && Long.parseLong(currentValue) < System.currentTimeMillis()) {
            // 这里是为了防止多个线程同时进入到这里然后都获得锁的情况。下面的代码是控制只有一个线程获得锁
            String oldValue = redisTemplate.opsForValue().getAndSet(key, value);
            if (!StringUtils.isEmpty(oldValue) && oldValue.equals(currentValue)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 解锁。删除Redis中对应的key/value，传过来的键值对必须是Redis中正确存储的键值对。
     *
     * @param key
     * @param value
     */
    public void unlock(String key, String value) {
        try {
            String currentValue = redisTemplate.opsForValue().get(key);
            if (!StringUtils.isEmpty(currentValue) && currentValue.equals(value)) {
                redisTemplate.opsForValue().getOperations().delete(key);
            }
        } catch (Exception e) {
            log.error("【Redis分布式锁】解锁异常");
        }
    }
}
