package gov.cdc.dataprocessing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class KafkaThreadPoolConfig {

    @Bean
    public ExecutorService processingExecutor() {
        int cores = Runtime.getRuntime().availableProcessors();
        int poolSize = 5;//cores * 2; // or higher if needed
        int queueSize = 5;

        return new ThreadPoolExecutor(
                poolSize,
                poolSize,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(queueSize),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}

