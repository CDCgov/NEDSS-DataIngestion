package gov.cdc.dataprocessing.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class CacheConfigTest {

    private AnnotationConfigApplicationContext context;

    @BeforeEach
    void setUp() {
        context = new AnnotationConfigApplicationContext(CacheConfig.class);
    }

    @Test
    void testCacheManager() {
        CacheManager cacheManager = context.getBean(CacheManager.class);
        assertNotNull(cacheManager);
        assertEquals(CaffeineCacheManager.class, cacheManager.getClass());

        CaffeineCacheManager caffeineCacheManager = (CaffeineCacheManager) cacheManager;
        assertNotEquals(List.of("srte"), caffeineCacheManager.getCacheNames());
    }

    @Test
    void testCaffeineConfig() {
        CacheConfig cacheConfig = context.getBean(CacheConfig.class);
        Caffeine caffeine = cacheConfig.caffeineConfig();
        assertNotNull(caffeine);

        Caffeine<Object, Object> expectedCaffeine = Caffeine.newBuilder()
                .expireAfterAccess(60, TimeUnit.MINUTES);

        assertEquals(expectedCaffeine.toString(), caffeine.toString());
    }
}
