package gov.cdc.nbs.deduplication.duplicates.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import gov.cdc.nbs.deduplication.seed.mapper.MpiPersonMapper;
import gov.cdc.nbs.deduplication.seed.model.MpiPerson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class PatientRecordServiceTest {

  @Mock
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @InjectMocks
  private PatientRecordService patientRecordService;


  @Test
  void fetchPatientDetailsReturnsMpiPerson() {
    String personParentUid = "123";
    MpiPerson expectedPerson = new MpiPerson(null, null, null, null, null,
        null, null, null, null, null);
    List<MpiPerson> mpiPersons = Collections.singletonList(expectedPerson);

    when(namedParameterJdbcTemplate.query(any(String.class), any(MapSqlParameterSource.class),
        any(MpiPersonMapper.class)))
        .thenReturn(mpiPersons);

    MpiPerson actualPerson = patientRecordService.fetchPatientDetails(personParentUid);

    assertThat(actualPerson).isEqualTo(expectedPerson);
  }


  @Test
  void fetchPatientDetailsReturnsNullWhenNoRecordsFound() {
    String personParentUid = "123";
    List<MpiPerson> mpiPersons = Collections.emptyList();

    when(namedParameterJdbcTemplate.query(any(String.class), any(MapSqlParameterSource.class),
        any(MpiPersonMapper.class)))
        .thenReturn(mpiPersons);

    MpiPerson actualPerson = patientRecordService.fetchPatientDetails(personParentUid);
    assertThat(actualPerson).isNull();
  }
}
