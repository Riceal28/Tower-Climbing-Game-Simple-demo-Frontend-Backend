package com.szm.demo.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.RedisException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Redis 工具类
 * 封装常用的 Redis 操作
 *
 */
@Component
public class RedisUtil {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

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

    /**
     * 自定义过期时缓存
     *
     * @param key      键
     * @param value    值
     * @param timeout  时间长度
     * @param timeUnit 单位
     */
    public void set(String key, Object value, long timeout, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
            logger.debug("Redis set with expire: key={}, timeout={}{}", key, timeout, timeUnit);
        } catch (RedisException e) {
            logger.error("Redis set with expire error: key={}", key, e);
            throw new RuntimeException("Redis:设置限时缓存失败", e);
        }
    }

    /**
     * 获取值
     *
     * @param key 键
     * @return Object类型值
     */
    public Object get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (RedisException e) {
            logger.error("Redis get error: key={}", key, e);
            throw new RuntimeException("Redis:获取值失败", e);
        }
    }

    /**
     * 获取值
     *
     * @param key   键
     * @param clazz 要反序列化的类
     * @return clazz
     */
    public <T> T get(String key, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            return value == null ? null : objectMapper.convertValue(value, clazz);
        } catch (RedisException e) {
            logger.error("Redis get error: key={}", key, e);
            return null;
        }
    }

    /**
     * 删除缓存
     *
     * @param key 键
     */
    public void delete(String key) {
        try {
            redisTemplate.delete(key);
            logger.debug("Redis delete success: key={}", key);
        } catch (RedisException e) {
            logger.error("Redis delete error: key={}", key, e);
            throw new RuntimeException("Redis:删除键值失败", e);
        }
    }

    /**
     * 批量删除缓存
     *
     * @param keys 一组键
     */
    public void delete(Collection<String> keys) {
        try {
            redisTemplate.delete(keys);
            logger.debug("Redis delete success: keys={}", keys);
        } catch (RedisException e) {
            logger.error("Redis delete error: keys={}", keys, e);
            throw new RuntimeException("Redis:删除键值失败", e);
        }
    }

    /**
     * key是否存在
     *
     * @param key 键
     * @return true/false
     */
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (RedisException e) {
            logger.error("Redis hasKey error: key={}", key, e);
            return false;
        }
    }

    /**
     * 设置缓存过期时间
     *
     * @param key      键
     * @param timeUnit 时间长度
     * @param timeout  时间单位
     * @return true/false
     */
    public boolean expire(String key, long timeout, TimeUnit timeUnit) {
        try {
            return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, timeUnit));
        } catch (RedisException e) {
            logger.error("Redis expire error: key={}", key, e);
            return false;
        }
    }

    /**
     * 获取缓存剩余过期时间
     *
     * @param key      键
     * @param timeUnit 时间单位
     * @return 剩余时间长度
     */
    public Long getExpire(String key, TimeUnit timeUnit) {
        try {
            return redisTemplate.getExpire(key, timeUnit);
        } catch (RedisException e) {
            logger.error("Redis getExpire error: key={}", key, e);
            return null;
        }
    }

    //========================== HASH ============================//

    /**
     * Hash 结构设置单个字段
     *
     * @param key     键
     * @param hashKey 哈希键
     * @param value   哈希键的值
     */
    public void hashPut(String key, String hashKey, Object value) {
        try {
            redisTemplate.opsForHash().put(key, hashKey, value);
            logger.debug("Redis hashSet success: key={}, hashKey={}", key, hashKey);
        } catch (RedisException e) {
            logger.error("Redis hashSet error: key={}, hashKey={}", key, hashKey, e);
            throw new RuntimeException("Redis:哈希缓存设置失败", e);
        }
    }

    /**
     * Hash 获取单个字段
     *
     * @param key     键
     * @param hashKey 哈希键
     * @return Object
     */
    public Object hashGet(String key, String hashKey) {
        try {
            return redisTemplate.opsForHash().get(key, hashKey);
        } catch (Exception e) {
            logger.error("Redis hashGet error: key={}, hashKey={}", key, hashKey, e);
            return null;
        }
    }

    /**
     * Hash 获取单个字段
     *
     * @param key     键
     * @param hashKey 哈希键
     * @param clazz   要反序列化的类
     * @return clazz
     */
    public <T> T hashGet(String key, String hashKey, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForHash().get(key, hashKey);
            return value == null ? null : objectMapper.convertValue(value, clazz);
        } catch (Exception e) {
            logger.error("Redis hashGet error: key={}, hashKey={}", key, hashKey, e);
            return null;
        }
    }

    /**
     * 设置整个 Hash 结构
     *
     * @param key 键
     * @param map 哈希结构
     */
    public void hashPutAll(String key, Map<String, Object> map) {
        if (CollectionUtils.isEmpty(map)) {
            logger.debug("Redis hashPutAll fail, empty Map");
            return;
        }
        try {
            redisTemplate.opsForHash().putAll(key, map);
            logger.debug("Redis hashPutAll success: key={}", key);
        } catch (RedisException e) {
            logger.error("Redis hashPutAll error: key={}", key, e);
            throw new RuntimeException("Redis:哈希批量设置失败", e);
        }
    }

    /**
     * 获取整个 Hash 结构
     *
     * @param key 键
     * @return Map<Object, Object>
     */
    public Map<Object, Object> hashEntries(String key) {
        try {
            return redisTemplate.opsForHash().entries(key);
        } catch (RedisException e) {
            logger.error("Redis hashEntries error: key={}", key, e);
            return Collections.emptyMap();
        }
    }

    /**
     * 获取整个 Hash 结构
     *
     * @param key   键
     * @param clazz 要反序列化的类
     * @return Map<String, clazz>
     */
    public <T> Map<String, T> hashEntries(String key, Class<T> clazz) {
        try {
            Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
            if (CollectionUtils.isEmpty(map)) {
                return Collections.emptyMap();
            }
            return map.entrySet()
                    .stream()
                    .filter(e -> e.getKey() != null && e.getValue() != null)
                    .collect(Collectors.toMap(
                            e -> String.valueOf(e.getKey()),
                            e -> objectMapper.convertValue(e.getValue(), clazz)
                    ));
        } catch (RedisException e) {
            logger.error("Redis hashEntries error: key={}", key, e);
            return Collections.emptyMap();
        }
    }

    /**
     * 删除 Hash 中的指定字段
     *
     * @param key      键
     * @param hashKeys 哈希键
     */
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

    /**
     * 从左侧推入元素（栈模式）
     *
     * @param key   键
     * @param value 值
     */
    public void leftPush(String key, Object value) {
        try {
            redisTemplate.opsForList().leftPush(key, value);
            logger.debug("Redis leftPush success: key={}", key);
        } catch (RedisException e) {
            logger.error("Redis leftPush error: key={}", key, e);
            throw new RuntimeException("Redis:List左侧推入失败", e);
        }
    }

    /**
     * 从右侧推入元素（栈模式）
     *
     * @param key   键
     * @param value 值
     */
    public void rightPush(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            logger.debug("Redis rightPush success: key={}", key);
        } catch (RedisException e) {
            logger.error("Redis rightPush error: key={}", key, e);
            throw new RuntimeException("Redis:List右侧推入失败", e);
        }
    }

    /**
     * 左侧弹出元素
     *
     * @param key 键
     * @return Object
     */
    public Object leftPop(String key) {
        try {
            return redisTemplate.opsForList().leftPop(key);
        } catch (RedisException e) {
            logger.error("Redis leftPop error: key={}", key, e);
            return null;
        }
    }

    /**
     * 左侧弹出元素
     *
     * @param key   键
     * @param clazz 要反序列化的类
     * @return clazz
     */
    public <T> T leftPop(String key, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForList().leftPop(key);
            return value == null ? null : objectMapper.convertValue(value, clazz);
        } catch (RedisException e) {
            logger.error("Redis leftPop error: key={}", key, e);
            return null;
        }
    }

    /**
     * 右侧弹出元素
     *
     * @param key 键
     * @return Object
     */
    public Object rightPop(String key) {
        try {
            return redisTemplate.opsForList().rightPop(key);
        } catch (RedisException e) {
            logger.error("Redis rightPop error: key={}", key, e);
            return null;
        }
    }

    /**
     * 右侧弹出元素
     *
     * @param key   键
     * @param clazz 要反序列化的类
     * @return clazz
     */
    public <T> T rightPop(String key, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForList().rightPop(key);
            return value == null ? null : objectMapper.convertValue(value, clazz);
        } catch (RedisException e) {
            logger.error("Redis rightPop error: key={}", key, e);
            return null;
        }
    }

    /**
     * 获取列表区间数据(左右闭区间)
     *
     * @param key   键
     * @param start 起点索引
     * @param end   终点索引
     * @return List<Object>
     */
    public List<Object> range(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (RedisException e) {
            logger.error("Redis range error: key={}", key, e);
            return Collections.emptyList();
        }
    }

    /**
     * 获取列表区间数据(左右闭区间)
     *
     * @param key   键
     * @param start 起点索引
     * @param end   终点索引
     * @param clazz 要反序列化的类
     * @return List<clazz>
     */
    public <T> List<T> range(String key, long start, long end, Class<T> clazz) {
        try {
            List<Object> list = redisTemplate.opsForList().range(key, start, end);
            if (CollectionUtils.isEmpty(list)) {
                return Collections.emptyList();
            }

            return list.stream()
                    .filter(Objects::nonNull)
                    .map(obj -> objectMapper.convertValue(obj, clazz))
                    .collect(Collectors.toList());
        } catch (RedisException e) {
            logger.error("Redis range error: key={}", key, e);
            return Collections.emptyList();
        }
    }

    //========================== SET ============================//

    /**
     * Set 集合添加元素
     *
     * @param key    键
     * @param values 值
     */
    public void setAdd(String key, Object... values) {
        try {
            redisTemplate.opsForSet().add(key, values);
            logger.debug("Redis setAdd success: key={}", key);
        } catch (Exception e) {
            logger.error("Redis setAdd error: key={}", key, e);
            throw new RuntimeException("Redis:SET添加失败", e);
        }
    }

    /**
     * 获取 Set 全部元素
     *
     * @param key 键
     * @return Set<Object>
     */
    public Set<Object> setMembers(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (RedisException e) {
            logger.error("Redis setMembers error: key={}", key, e);
            return Collections.emptySet();
        }
    }

    /**
     * 获取 Set 全部元素
     *
     * @param key   键
     * @param clazz 要反序列化的类
     * @return Set<clazz>
     */
    public <T> Set<T> setMembers(String key, Class<T> clazz) {
        try {
            Set<Object> set = redisTemplate.opsForSet().members(key);
            if (CollectionUtils.isEmpty(set)) {
                return Collections.emptySet();
            }
            return set.stream()
                    .filter(Objects::nonNull)
                    .map(obj -> objectMapper.convertValue(obj, clazz))
                    .collect(Collectors.toSet());
        } catch (RedisException e) {
            logger.error("Redis setMembers error: key={}", key, e);
            return Collections.emptySet();
        }
    }

    /**
     * 判断元素是否在 Set 中
     *
     * @param key   键
     * @param value 值
     * @return true/false
     */
    public boolean isMember(String key, Object value) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
        } catch (Exception e) {
            logger.error("Redis isMember error: key={}", key, e);
            return false;
        }
    }

    //========================== 分布式锁 ============================//

    /**
     * 加分布式锁
     *
     * @param key      键
     * @param value    值
     * @param timeout  时间长度
     * @param timeUnit 时间单位
     * @return 加锁成功与否true/false
     */
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

    /**
     * 解锁（验证值再删，防止误删别人的锁）
     *
     * @param key   键
     * @param value 值
     * @return 解锁成功与否true/false
     */
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
