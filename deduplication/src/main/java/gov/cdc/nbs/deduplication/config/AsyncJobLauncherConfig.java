package gov.cdc.nbs.deduplication.config;

import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncJobLauncherConfig {

  @Bean("asyncJobLauncher")
  public TaskExecutorJobLauncher asyncJobLauncher(JobRepository jobRepository) throws Exception {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(3);
    executor.setMaxPoolSize(10);
    executor.setQueueCapacity(15);
    executor.initialize();

    TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
    jobLauncher.setJobRepository(jobRepository);
    jobLauncher.setTaskExecutor(executor);
    jobLauncher.afterPropertiesSet();
    return jobLauncher;
  }
}
