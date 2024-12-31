package gov.cdc.nbs.deduplication.seed.step;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

import gov.cdc.nbs.deduplication.seed.mapper.MpiPersonMapper;
import gov.cdc.nbs.deduplication.seed.model.NbsPerson;
import gov.cdc.nbs.deduplication.seed.model.SeedRequest.Cluster;
import gov.cdc.nbs.deduplication.seed.model.SeedRequest.MpiPerson;
import gov.cdc.nbs.deduplication.seed.model.SeedRequest.Name;

@ExtendWith(MockitoExtension.class)
class PersonToClusterProcessorTest {

  @Mock
  private JdbcTemplate template;

  @InjectMocks
  private PersonToClusterProcessor processor;

  @Test
  void clustersPersons() {
    // Arrange
    when(template.query(
            Mockito.anyString(),
            Mockito.any(PreparedStatementSetter.class),
            Mockito.any(MpiPersonMapper.class)))
            .thenReturn(List.of(new MpiPerson(
                    "person_uid",
                    "1990-12-13",
                    "F",
                    "mrn",
                    null,
                    List.of(new Name(List.of("first", "middle"), "family", List.of())),
                    null,
                    null,
                    null,
                    null,
                    null)));

    // Act
    Cluster cluster = processor.process(new NbsPerson(1L, 2L));

    // Assert
    assertThat(cluster.external_person_id()).isEqualTo("2");
    assertThat(cluster.records()).hasSize(1);
    assertThat(cluster.records().get(0).external_id()).isEqualTo("person_uid");
  }

  @Test
  void skipsAlreadyProcessedPersons() {
    // Arrange
    NbsPerson alreadyProcessedPerson = new NbsPerson(1L, 2L);
    processor.process(alreadyProcessedPerson); // First processing

    // Act
    Cluster result = processor.process(alreadyProcessedPerson); // Try processing again

    // Assert
    assertThat(result).isNull(); // Should skip the second processing
    verifyNoInteractions(template); // Ensure no query is made
  }

  @Test
  void processesMultipleUniquePersons() {
    // Arrange
    NbsPerson person1 = new NbsPerson(1L, 2L);
    NbsPerson person2 = new NbsPerson(3L, 4L);

    when(template.query(
            Mockito.anyString(),
            Mockito.any(PreparedStatementSetter.class),
            Mockito.any(MpiPersonMapper.class)))
            .thenReturn(List.of(new MpiPerson(
                    "person_uid",
                    "1990-12-13",
                    "F",
                    "mrn",
                    null,
                    List.of(new Name(List.of("first", "middle"), "family", List.of())),
                    null,
                    null,
                    null,
                    null,
                    null)));

    // Act
    Cluster cluster1 = processor.process(person1);
    Cluster cluster2 = processor.process(person2);

    // Assert
    assertThat(cluster1).isNotNull();
    assertThat(cluster2).isNotNull();
    assertThat(cluster1.external_person_id()).isEqualTo("2");
    assertThat(cluster2.external_person_id()).isEqualTo("4");
    verify(template, times(2)).query(anyString(), any(), any(MpiPersonMapper.class)); // Two queries executed
  }
}
