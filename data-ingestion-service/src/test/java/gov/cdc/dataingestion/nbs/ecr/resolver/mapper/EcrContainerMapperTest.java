package gov.cdc.dataingestion.nbs.ecr.resolver.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgContainerDto;

class EcrContainerMapperTest {

  private final EcrContainerMapper mapper = new EcrContainerMapper();

  @Test
  void shouldMapFields() throws SQLException {
    ResultSet rs = Mockito.mock(ResultSet.class);
    when(rs.getInt("msgContainerUid")).thenReturn(1);
    when(rs.getString("invLocalId")).thenReturn("invLocalId");
    when(rs.getInt("nbsInterfaceUid")).thenReturn(2);
    when(rs.getString("receivingSystem")).thenReturn("receivingSystem");
    when(rs.getString("ongoingCase")).thenReturn("ongoingCase");
    when(rs.getInt("versionCtrlNbr")).thenReturn(3);
    when(rs.getInt("dataMigrationStatus")).thenReturn(4);
    EcrMsgContainerDto actual = mapper.mapRow(rs, 0);

    assertThat(actual.msgContainerUid()).isEqualTo(1);
    assertThat(actual.invLocalId()).isEqualTo("invLocalId");
    assertThat(actual.nbsInterfaceUid()).isEqualTo(2);
    assertThat(actual.receivingSystem()).isEqualTo("receivingSystem");
    assertThat(actual.ongoingCase()).isEqualTo("ongoingCase");
    assertThat(actual.versionCtrlNbr()).isEqualTo(3);
    assertThat(actual.dataMigrationStatus()).isEqualTo(4);

  }
}
