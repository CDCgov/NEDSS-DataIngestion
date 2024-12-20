package gov.cdc.nbs.deduplication.seed;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import gov.cdc.nbs.deduplication.config.container.UseTestContainers;

@SpringBootTest
@ActiveProfiles("test")
@UseTestContainers
class SeedingTest {

  @Test
  void seedMpiTest() {
    // call seed endpoint

    // wait on process to be completed

    // query to check counts

    // verify a patient's content in MPI
  }

}
