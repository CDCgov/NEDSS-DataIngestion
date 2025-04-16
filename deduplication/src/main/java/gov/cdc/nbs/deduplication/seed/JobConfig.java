package gov.cdc.nbs.deduplication.seed;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import gov.cdc.nbs.deduplication.seed.model.DeduplicationEntry;
import gov.cdc.nbs.deduplication.seed.model.NbsPerson;
import gov.cdc.nbs.deduplication.seed.step.SeedWriter;
import gov.cdc.nbs.deduplication.seed.step.DeduplicationWriter;
import gov.cdc.nbs.deduplication.seed.step.MpiReader;
import gov.cdc.nbs.deduplication.seed.step.PersonReader;

@Configuration
public class JobConfig {

  final PersonReader personReader;
  final SeedWriter seedWriter;

  final MpiReader mpiReader;
  final DeduplicationWriter deduplicationWriter;

  @Value("${batch.chunk.size.readNbsWriteToMpi:100}")
  private int step1ChunkSize;

  @Value("${batch.chunk.size.readMpiWriteDeduplication:1000}")
  private int step2ChunkSize;

  public JobConfig(
      final PersonReader personReader,
      final SeedWriter seedWriter,
      final MpiReader mpiReader,
      final DeduplicationWriter deduplicationWriter) {
    this.personReader = personReader;
    this.seedWriter = seedWriter;
    this.mpiReader = mpiReader;
    this.deduplicationWriter = deduplicationWriter;
  }

  @Bean("readNbsWriteToMpi")
  public Step step1(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager
  ) {
    return new StepBuilder("readNbsWriteToMpi", jobRepository)
        .<NbsPerson, NbsPerson>chunk(step1ChunkSize, transactionManager)
        .reader(personReader)
        .writer(seedWriter)
        .faultTolerant()
        .noSkip(RuntimeException.class)
        .build();
  }

  @Bean("readMpiWriteDeduplication")
  public Step step2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    return new StepBuilder("Read and transform NBS data", jobRepository)
        .<DeduplicationEntry, DeduplicationEntry>chunk(step2ChunkSize, transactionManager)
        .reader(mpiReader) // page UUID <-> NBS id data from MPI
        .writer(deduplicationWriter) // insert mapping and status into deduplication database
        .build();
  }

  @Bean("seedJob")
  public Job seedJob(JobRepository jobRepository,
      @Qualifier("readNbsWriteToMpi") Step step1,
      @Qualifier("readMpiWriteDeduplication") Step step2) {
    return new JobBuilder("Seed MPI", jobRepository)
        .start(step1)
        .next(step2)
        .build();
  }

}
