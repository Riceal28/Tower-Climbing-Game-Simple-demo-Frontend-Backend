package com.szm.demo.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisConfig {
    @Bean(name="redisTemplate")
    public RedisTemplate<String,Object> getRedisTemplate(RedisConnectionFactory connectionFactory){
        RedisTemplate<String,Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        // 使用 Jackson2JsonRedisSerializer 作为 value 序列化器
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = createJacksonSerializer();
        // 使用 StringRedisSerializer 作为 key 的序列化器
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // 设置 key 和 hash key 的序列化器
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        // 设置 value 和 hash value 的序列化器
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        // 启用默认的类型转换
        redisTemplate.afterPropertiesSet();
        return  redisTemplate;
    }

    /**
     * 创建 Jackson 序列化器
     */
    private Jackson2JsonRedisSerializer createJacksonSerializer(){

        ObjectMapper objectMapper = new ObjectMapper();
        // 所有属性可见
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        // 启用默认类型(存储类型信息)
//        objectMapper.activateDefaultTyping(
//                LaissezFaireSubTypeValidator.instance,ObjectMapper.DefaultTyping.NON_FINAL
//        );
        // 注册Java8时间模块(LocalDateTime等)
        objectMapper.registerModule(new JavaTimeModule());

        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer =
                new Jackson2JsonRedisSerializer<>(objectMapper,Object.class);

        return  jackson2JsonRedisSerializer;
    }
    /**
     * 配置 RedisCacheManager（用于 Spring Cache 注解）
     *
     * @param connectionFactory Redis 连接工厂
     * @return RedisCacheManager
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory){
        // 默认配置：使用 JSON 序列化，过期时间 1 小时
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))//key序列化器
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()))//value序列化器
                .disableCachingNullValues()//不缓存null
                .entryTtl(Duration.ofHours(1));//默认过期时间1h
        Map<String,RedisCacheConfiguration> cacheConfigurationMap = new HashMap<>();
        // 用户缓存：30分钟
        cacheConfigurationMap.put("user", defaultConfig.entryTtl(Duration.ofMinutes(30)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurationMap)
                .transactionAware()//支持事务
                .build();
    }

}
