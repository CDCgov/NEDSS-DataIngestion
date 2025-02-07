package gov.cdc.nbs.deduplication.seed;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.transaction.PlatformTransactionManager;

import gov.cdc.nbs.deduplication.seed.step.DeduplicationWriter;
import gov.cdc.nbs.deduplication.seed.step.MpiReader;
import gov.cdc.nbs.deduplication.seed.step.PersonReader;
import gov.cdc.nbs.deduplication.seed.step.SeedWriter;
import gov.cdc.nbs.deduplication.seed.listener.LastProcessedIdListener;


@ExtendWith(MockitoExtension.class)
class JobConfigTest {
  @Mock
  private PersonReader personReader;
  @Mock
  private SeedWriter seedWriter;
  @Mock
  private MpiReader mpiReader;
  @Mock
  private DeduplicationWriter deduplicationWriter;
  @Mock
  private JobRepository jobRepository;
  @Mock
  private PlatformTransactionManager transactionManager;
  @Mock
  private LastProcessedIdListener listener;

  @Test
  void buildsValidConfig() {
    JobConfig config = new JobConfig(personReader, seedWriter, mpiReader, deduplicationWriter);
    assertThat(config).isNotNull();

    Job seedJob = config.seedJob(jobRepository, null, null, listener);
    assertThat(seedJob).isNotNull();

  }

  @Test
  void step1() {
    JobConfig config = new JobConfig(personReader, seedWriter, mpiReader, deduplicationWriter);

    Step step1 = config.step1(jobRepository, transactionManager);
    assertThat(step1).isNotNull();
  }

  @Test
  void step2() {
    JobConfig config = new JobConfig(personReader, seedWriter, mpiReader, deduplicationWriter);
    Step step2 = config.step2(jobRepository, transactionManager);
    assertThat(step2).isNotNull();
  }

}
