package gov.cdc.nbs.deduplication.batch;

import gov.cdc.nbs.deduplication.batch.step.UnprocessedPreviousDayPersonReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.transaction.PlatformTransactionManager;

import gov.cdc.nbs.deduplication.batch.step.DuplicatesProcessor;
import gov.cdc.nbs.deduplication.batch.step.MatchCandidateWriter;
import gov.cdc.nbs.deduplication.batch.step.UnprocessedPersonReader;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
class BatchJobConfigTest {

  @Mock
  private UnprocessedPreviousDayPersonReader previousDayReader;

  @Mock
  private UnprocessedPersonReader unprocessedPersonReader;

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
    BatchJobConfig config = new BatchJobConfig(
        previousDayReader,
        unprocessedPersonReader,
        deduplicationProcessor,
        writer);

    assertThat(config).isNotNull();

    Job job = config.deduplicationJob(jobRepository, null, null);
    assertThat(job).isNotNull();
  }

  @Test
  void previousDayStep_isConfiguredCorrectly() {
    BatchJobConfig config = new BatchJobConfig(
        previousDayReader,
        unprocessedPersonReader,
        deduplicationProcessor,
        writer);

    Step step = config.previousDayStep(jobRepository, transactionManager);
    assertThat(step).isNotNull();
  }

  @Test
  void olderThanPreviousDayStep_isConfiguredCorrectly() {
    BatchJobConfig config = new BatchJobConfig(
        previousDayReader,
        unprocessedPersonReader,
        deduplicationProcessor,
        writer);

    Step step = config.olderThanPreviousDayStep(jobRepository, transactionManager);
    assertThat(step).isNotNull();
  }

  @Test
  void deduplicationJob_hasTwoSteps() {
    BatchJobConfig config = new BatchJobConfig(
        previousDayReader,
        unprocessedPersonReader,
        deduplicationProcessor,
        writer);

    Step previousDayStep = config.previousDayStep(jobRepository, transactionManager);
    Step olderDayStep = config.olderThanPreviousDayStep(jobRepository, transactionManager);

    Job job = config.deduplicationJob(jobRepository, previousDayStep, olderDayStep);
    assertThat(job).isNotNull();
  }
}
