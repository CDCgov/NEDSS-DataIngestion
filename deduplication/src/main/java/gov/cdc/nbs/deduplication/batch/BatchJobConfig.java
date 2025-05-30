package gov.cdc.nbs.deduplication.batch;

import gov.cdc.nbs.deduplication.batch.step.UnprocessedPreviousDayPersonReader;
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

import gov.cdc.nbs.deduplication.batch.model.MatchCandidate;
import gov.cdc.nbs.deduplication.batch.step.DuplicatesProcessor;
import gov.cdc.nbs.deduplication.batch.step.MatchCandidateWriter;
import gov.cdc.nbs.deduplication.batch.step.UnprocessedPersonReader;


@Configuration
public class BatchJobConfig {

  private final UnprocessedPreviousDayPersonReader previousDayReader;
  private final UnprocessedPersonReader unprocessedPersonReader;
  private final DuplicatesProcessor deduplicationProcessor;
  private final MatchCandidateWriter writer;

  @Value("${deduplication.batch.processing.chunk:100}")
  private int chunkSize;

  public BatchJobConfig(
      UnprocessedPreviousDayPersonReader previousDayReader,
      UnprocessedPersonReader unprocessedPersonReader,
      DuplicatesProcessor deduplicationProcessor,
      MatchCandidateWriter writer) {
    this.previousDayReader = previousDayReader;
    this.unprocessedPersonReader = unprocessedPersonReader;
    this.deduplicationProcessor = deduplicationProcessor;
    this.writer = writer;
  }

  @Bean("previousDayStep")
  public Step previousDayStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    return new StepBuilder("previousDayStep", jobRepository)
        .<String, MatchCandidate>chunk(chunkSize, transactionManager)
        .reader(previousDayReader)
        .processor(deduplicationProcessor)
        .writer(writer)
        .build();
  }

  @Bean("olderThanPreviousDayStep")
  public Step olderThanPreviousDayStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    return new StepBuilder("olderThanPreviousDayStep", jobRepository)
        .<String, MatchCandidate>chunk(chunkSize, transactionManager)
        .reader(unprocessedPersonReader)
        .processor(deduplicationProcessor)
        .writer(writer)
        .build();
  }

  @Bean("deduplicationJob")
  public Job deduplicationJob(JobRepository jobRepository,
      @Qualifier("previousDayStep") Step previousDayStep,
      @Qualifier("olderThanPreviousDayStep") Step olderDayStep) {
    return new JobBuilder("deduplicationJob", jobRepository)
        .start(previousDayStep)
        .next(olderDayStep)
        .build();
  }
}
