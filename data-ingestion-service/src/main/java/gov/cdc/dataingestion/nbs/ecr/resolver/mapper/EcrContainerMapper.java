package gov.cdc.dataingestion.nbs.ecr.resolver.mapper;

import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgContainerDto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EcrContainerMapper implements RowMapper<EcrMsgContainerDto> {

    @Override
    public EcrMsgContainerDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new EcrMsgContainerDto(
                rs.getInt("msgContainerUid"),
                rs.getString("invLocalId"),
                rs.getInt("nbsInterfaceUid"),
                rs.getString("receivingSystem"),
                rs.getString("ongoingCase"),
                rs.getInt("versionCtrlNbr"),
                rs.getInt("dataMigrationStatus"));
    }
}
