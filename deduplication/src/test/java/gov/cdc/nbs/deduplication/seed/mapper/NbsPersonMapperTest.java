package gov.cdc.nbs.deduplication.seed.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import gov.cdc.nbs.deduplication.seed.model.NbsPerson;

@SuppressWarnings("null")
class NbsPersonMapperTest {
  NbsPersonMapper mapper = new NbsPersonMapper();

  @Test
  void testParse() throws SQLException {
    ResultSet resultSet = Mockito.mock(ResultSet.class);
    when(resultSet.getString("person_uid")).thenReturn("1");
    when(resultSet.getString("person_parent_uid")).thenReturn("2");

    NbsPerson entry = mapper.mapRow(resultSet, 0);

    assertThat(entry).isNotNull();
    assertThat(entry.personUid()).isEqualTo("1");
    assertThat(entry.personParentUid()).isEqualTo("2");

  }

}
