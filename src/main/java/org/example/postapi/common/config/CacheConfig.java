package org.example.postapi.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.example.postapi.common.GlobalConstants.SHORT_LIVE_CACHE_NAME;

/**
 * @author rival
 * @since 2025-01-23
 */

@Configuration
@EnableCaching
public class CacheConfig {

    @Value("${spring.data.redis.entry-ttl-minutes}")
    private int ENTRY_TTL_MINUTES;

    @Value("${spring.data.redis.cache-prefix}")
    private String CACHE_PREFIX;

    @Bean
    public RedisSerializer<?> redisSerializer(){
        return new JdkSerializationRedisSerializer();
    }


    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put(SHORT_LIVE_CACHE_NAME, shortLiveCacheConfiguration());

        return RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(defaultCacheConfiguration()) // 기본 TTL 5분
            .withInitialCacheConfigurations(cacheConfigurations) // 캐시별 TTL 적용
            .build();
    }


    @Bean
    public RedisCacheConfiguration defaultCacheConfiguration(){
        return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(ENTRY_TTL_MINUTES))
            .disableCachingNullValues()
            .prefixCacheNameWith(CACHE_PREFIX)
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer())
            );
    }

    @Bean
    public RedisCacheConfiguration shortLiveCacheConfiguration(){
        return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofSeconds(30)) // 30초 TTL
            .disableCachingNullValues()
            .prefixCacheNameWith(CACHE_PREFIX)
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer())
            );
    }


}
