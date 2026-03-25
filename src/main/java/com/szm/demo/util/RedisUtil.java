package com.szm.demo.util;

import io.lettuce.core.RedisException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis 工具类
 * 封装常用的 Redis 操作
 *
 */
@Component
public class RedisUtil {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 设置缓存
     *
     * @param key   键
     * @param value 值
     */
    public void set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            logger.debug("Redis set success: key={}", key);
        } catch (RedisException e) {
            logger.error("Redis set error: key={}", key, e);
            throw new RuntimeException("Redis:设置缓存失败", e);
        }
    }

    public void set(String key, Object value, long timeout, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
            logger.debug("Redis set with expire: key={}, timeout={}{}", key, timeout, timeUnit);
        } catch (RedisException e) {
            logger.error("Redis set with expire error: key={}", key, e);
            throw new RuntimeException("Redis:设置限时缓存失败", e);
        }
    }

    public Object get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (RedisException e) {
            logger.error("Redis get error: key={}", key, e);
            throw new RuntimeException("Redis:获取值失败", e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            return value == null ? null : (T) value;
        } catch (RedisException e) {
            logger.error("Redis get error: key={}", key, e);
            return null;
        }
    }

    public void delete(String key) {
        try {
            redisTemplate.delete(key);
            logger.debug("Redis delete success: key={}", key);
        } catch (RedisException e) {
            logger.error("Redis delete error: key={}", key, e);
            throw new RuntimeException("Redis:删除键值失败", e);
        }
    }

    public void delete(Collection<String> keys) {
        try {
            redisTemplate.delete(keys);
            logger.debug("Redis delete success: keys={}", keys);
        } catch (RedisException e) {
            logger.error("Redis delete error: keys={}", keys, e);
            throw new RuntimeException("Redis:删除键值失败", e);
        }
    }

    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (RedisException e) {
            logger.error("Redis hasKey error: key={}", key, e);
            return false;
        }
    }

    public boolean expire(String key, long timeout, TimeUnit timeUnit) {
        try {
            return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, timeUnit));
        } catch (RedisException e) {
            logger.error("Redis expire error: key={}", key, e);
            return false;
        }
    }

    public Long getExpire(String key, TimeUnit timeUnit) {
        try {
            return redisTemplate.getExpire(key, timeUnit);
        } catch (RedisException e) {
            logger.error("Redis getExpire error: key={}", key, e);
            return null;
        }
    }

    //========================== HASH ============================//
    public void hashSet(String key, String hashKey, Object value) {
        try {
            redisTemplate.opsForHash().put(key, hashKey, value);
            logger.debug("Redis hashSet success: key={}, hashKey={}", key, hashKey);
        } catch (RedisException e) {
            logger.error("Redis hashSet error: key={}, hashKey={}", key, hashKey, e);
            throw new RuntimeException("Redis:哈希缓存设置失败", e);
        }
    }

    public Object hashGet(String key, String hashKey) {
        try {
            return redisTemplate.opsForHash().get(key, hashKey);
        } catch (Exception e) {
            logger.error("Redis hashGet error: key={}, hashKey={}", key, hashKey, e);
            return null;
        }
    }

    public Map<Object, Object> hashEntries(String key) {
        try {
            return redisTemplate.opsForHash().entries(key);
        } catch (RedisException e) {
            logger.error("Redis hashEntries error: key={}", key, e);
            return Collections.emptyMap();
        }
    }

    public void hashDelete(String key, Object... hashKeys) {
        try {
            redisTemplate.opsForHash().delete(key, hashKeys);
            logger.debug("Redis hashDelete success: key={}, hashKeys={}", key, hashKeys);
        } catch (RedisException e) {
            logger.error("Redis hashDelete error: key={}", key, e);
            throw new RuntimeException("Redis:删除哈希键值组失败", e);
        }
    }

    //========================== LIST ============================//
    public void leftPush(String key, Object value) {
        try {
            redisTemplate.opsForList().leftPush(key, value);
            logger.debug("Redis leftPush success: key={}", key);
        } catch (RedisException e) {
            logger.error("Redis leftPush error: key={}", key, e);
            throw new RuntimeException("Redis:List左侧推入失败", e);
        }
    }

    public void rightPush(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            logger.debug("Redis rightPush success: key={}", key);
        } catch (RedisException e) {
            logger.error("Redis rightPush error: key={}", key, e);
            throw new RuntimeException("Redis:List右侧推入失败", e);
        }
    }

    public Object leftPop(String key) {
        try {
            return redisTemplate.opsForList().leftPop(key);
        } catch (RedisException e) {
            logger.error("Redis leftPop error: key={}", key, e);
            return null;
        }
    }

    public Object rightPop(String key) {
        try {
            return redisTemplate.opsForList().rightPop(key);
        } catch (RedisException e) {
            logger.error("Redis rightPop error: key={}", key, e);
            return null;
        }
    }

    public List<Object> range(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (RedisException e) {
            logger.error("Redis range error: key={}", key, e);
            return Collections.emptyList();
        }
    }

    //========================== SET ============================//
    public void setAdd(String key, Object... values) {
        try {
            redisTemplate.opsForSet().add(key, values);
            logger.debug("Redis setAdd success: key={}", key);
        } catch (Exception e) {
            logger.error("Redis setAdd error: key={}", key, e);
            throw new RuntimeException("Redis:SET添加失败", e);
        }
    }

    public Set<Object> setMembers(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (RedisException e) {
            logger.error("Redis setMembers error: key={}", key, e);
            return Collections.emptySet();
        }
    }

    public boolean isMember(String key, Object value) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
        } catch (Exception e) {
            logger.error("Redis isMember error: key={}", key, e);
            return false;
        }
    }

    //========================== 分布式锁 ============================//
    public boolean lock(String key, Object value, long timeout, TimeUnit timeUnit) {
        try {
            Boolean result = redisTemplate.opsForValue()
                    .setIfAbsent(key, value, timeout, timeUnit);
            logger.debug("Redis lock success: key={}", key);
            return Boolean.TRUE.equals(result);
        } catch (RedisException e) {
            logger.error("Redis lock error: key={}", key, e);
            return false;
        }
    }

    public boolean unlock(String key, Object value) {
        try {
            Object currentValue = redisTemplate.opsForValue().get(key);
            if (currentValue != null && currentValue.equals(value)) {
                logger.debug("Redis unlock success: key={}", key);
                return Boolean.TRUE.equals(redisTemplate.delete(key));
            }
            logger.debug("Redis unlock fail: key={}", key);
            return false;
        } catch (RedisException e) {
            logger.error("Redis unlock error: key={}", key, e);
            throw new RuntimeException(e);
        }
    }
}
