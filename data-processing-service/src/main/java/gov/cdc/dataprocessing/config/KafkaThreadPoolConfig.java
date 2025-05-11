package gov.cdc.dataprocessing.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class KafkaThreadPoolConfig {
    @Value("${feature.thread-pool-size}")
    private Integer poolSize = 1;
    @Value("${feature.thread-queue-size}")
    private Integer queueSize = 1;
//    @Bean
//    public ExecutorService processingExecutor() {
//        int cores = Runtime.getRuntime().availableProcessors();
//        int poolSize = 100;//cores * 2; // or higher if needed
//        int queueSize = 100;
//
//        return new ThreadPoolExecutor(
//                poolSize,
//                poolSize,
//                60L, TimeUnit.SECONDS, // threads will be cleaned up after 60s idle
//                new LinkedBlockingQueue<>(queueSize),
//                Executors.defaultThreadFactory(),
//                new ThreadPoolExecutor.CallerRunsPolicy()
//        );
//    }


    @Bean
    public ExecutorService processingExecutor() {

//        int availableCores = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                poolSize,
                poolSize * 2,
                60L, TimeUnit.SECONDS, // allow idle cleanup
                new LinkedBlockingQueue<>(queueSize),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy() // back-pressure
        );
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }
}

