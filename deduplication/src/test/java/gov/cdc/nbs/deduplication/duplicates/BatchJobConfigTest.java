package gov.cdc.nbs.deduplication.duplicates;

import gov.cdc.nbs.deduplication.duplicates.step.DuplicatesProcessor;
import gov.cdc.nbs.deduplication.duplicates.step.UnprocessedPersonReader;
import gov.cdc.nbs.deduplication.duplicates.step.MatchCandidateWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.transaction.PlatformTransactionManager;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BatchJobConfigTest {

  @Mock
  private UnprocessedPersonReader duplicatesReader;

  @Mock
  private DuplicatesProcessor deduplicationProcessor;

  @Mock
  private MatchCandidateWriter writer;

  @Mock
  private JobRepository jobRepository;

  @Mock
  private PlatformTransactionManager transactionManager;

  @Test
  void buildsValidConfig() {
    BatchJobConfig config = new BatchJobConfig(duplicatesReader, deduplicationProcessor, writer);
    assertThat(config).isNotNull();

    Job deduplicationJob = config.deduplicationJob(jobRepository, null);
    assertThat(deduplicationJob).isNotNull();
  }

  @Test
  void deduplicationStep() {
    BatchJobConfig config = new BatchJobConfig(duplicatesReader, deduplicationProcessor, writer);

    Step step1 = config.step1(jobRepository, transactionManager);
    assertThat(step1).isNotNull();
  }
}
