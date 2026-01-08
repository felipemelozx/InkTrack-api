package com.inktrack.infrastructure.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

  public static final String GOOGLE_BOOKS_CACHE = "googleBooks";

  @Bean
  public CacheManager cacheManager(GoogleBooksConfig config) {
    CaffeineCacheManager cacheManager = new CaffeineCacheManager(GOOGLE_BOOKS_CACHE);
    cacheManager.setCaffeine(
        Caffeine.newBuilder()
            .maximumSize(config.getCacheMaxSize())
            .expireAfterWrite(config.getCacheExpirationMinutes(), TimeUnit.MINUTES)
            .recordStats()
    );
    return cacheManager;
  }
}
