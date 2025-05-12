package gov.cdc.nbs.deduplication.batch;

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

  private final UnprocessedPersonReader personReader;
  private final DuplicatesProcessor deduplicationProcessor;
  private final MatchCandidateWriter writer;

  @Value("${deduplication.batch.processing.chunk:100}")
  private int chunkSize;

  public BatchJobConfig(
      final UnprocessedPersonReader personReader,
      final DuplicatesProcessor deduplicationProcessor,
      final MatchCandidateWriter writer) {
    this.personReader = personReader;
    this.deduplicationProcessor = deduplicationProcessor;
    this.writer = writer;
  }

  @Bean("deduplicationStep")
  public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    return new StepBuilder("step1", jobRepository)
        .<String, MatchCandidate>chunk(chunkSize, transactionManager) // Use externalized chunk size
        .reader(personReader) // Read unprocessed MPI records
        .processor(deduplicationProcessor) // Process records to find possible duplicates
        .writer(writer) // Write match candidates and update status
        .build();
  }

  @Bean("deduplicationJob")
  public Job deduplicationJob(JobRepository jobRepository, @Qualifier("deduplicationStep") Step step1) {
    return new JobBuilder("deduplication job", jobRepository)
        .start(step1)
        .build();
  }
}
