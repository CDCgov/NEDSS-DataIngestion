package gov.cdc.dataprocessing.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCacheNames(Arrays.asList("srte")); // Add your cache names here
        cacheManager.setCaffeine(caffeineConfig());
        return cacheManager;
    }

    protected Caffeine caffeineConfig() {
        return Caffeine.newBuilder()
//                .maximumSize(500)
                .expireAfterAccess(60, TimeUnit.MINUTES); // Adjust expiration settings as needed
    }
}