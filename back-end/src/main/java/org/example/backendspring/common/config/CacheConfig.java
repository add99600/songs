package org.example.backendspring.common.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Caffeine 캐시 설정.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * 캐시 매니저를 생성한다.
     * - lyrics: 가사 캐시 (최대 1200건, 1시간 TTL)
     * - searchResults: 검색 결과 캐시 (최대 1200건, 1시간 TTL)
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("lyrics", "searchResults");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(1200)
                .expireAfterWrite(1, TimeUnit.HOURS));
        return cacheManager;
    }
}
