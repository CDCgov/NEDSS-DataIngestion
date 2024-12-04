package gov.cdc.nbs.deduplication.seed;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import gov.cdc.nbs.deduplication.seed.model.NbsPerson;
import gov.cdc.nbs.deduplication.seed.model.SeedRequest.Cluster;
import gov.cdc.nbs.deduplication.seed.step.DeduplicationWriter;
import gov.cdc.nbs.deduplication.seed.step.PersonReader;
import gov.cdc.nbs.deduplication.seed.step.PersonToClusterProcessor;

@Configuration
public class JobConfig {

  final PersonReader reader;
  final PersonToClusterProcessor processor;
  final DeduplicationWriter deduplicationWriter;

  public JobConfig(
      final PersonReader reader,
      final PersonToClusterProcessor processor,
      final DeduplicationWriter deduplicationWriter) {
    this.reader = reader;
    this.processor = processor;
    this.deduplicationWriter = deduplicationWriter;
  }

  @Bean("readWriteToMpi")
  public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    return new StepBuilder("Read and transform NBS data", jobRepository)
        .<NbsPerson, Cluster>chunk(10, transactionManager)
        .reader(reader) // page ids to be processed from NBS
        .processor(processor) // query data from NBS, send to MPI and pass response
        .writer(deduplicationWriter) // insert into dedupe db
        .build();
  }

  @Bean("seedJob")
  public Job seedJob(JobRepository jobRepository,
      @Qualifier("readWriteToMpi") Step step1) {
    return new JobBuilder("MPI Seed", jobRepository)
        .flow(step1)
        .end()
        .build();
  }

}
