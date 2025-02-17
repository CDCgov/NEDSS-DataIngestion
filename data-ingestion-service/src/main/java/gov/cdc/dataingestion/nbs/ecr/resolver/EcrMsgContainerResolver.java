package gov.cdc.dataingestion.nbs.ecr.resolver;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import gov.cdc.dataingestion.nbs.ecr.resolver.mapper.EcrContainerMapper;
import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgContainerDto;

@Component
public class EcrMsgContainerResolver {

    private final NamedParameterJdbcTemplate template;
    private final EcrContainerMapper rowMapper = new EcrContainerMapper();

    public EcrMsgContainerResolver(@Qualifier("nbsTemplate") NamedParameterJdbcTemplate template) {
        this.template = template;
    }

    private static final String QUERY = """
            SELECT
                TOP (:count) MSG_CONTAINER.MSG_CONTAINER_UID msgContainerUid,
                INV_LOCAL_ID invLocalId,
                NBS_INTERFACE_UID nbsInterfaceUid,
                RECEIVING_SYSTEM receivingSystem,
                MSG_CONTAINER.ONGOING_CASE ongoingCase,
                MSG_CONTAINER.VERSION_CTRL_NBR versionCtrlNbr,
                DATA_MIGRATION_STATUS dataMigrationStatus
            FROM
                MSG_CONTAINER
                INNER JOIN MSG_CASE_INVESTIGATION ON MSG_CASE_INVESTIGATION.MSG_CONTAINER_UID = MSG_CONTAINER.MSG_CONTAINER_UID
            WHERE
                DATA_MIGRATION_STATUS IN (-1,-2);
                                            """;

    public List<EcrMsgContainerDto> resolve(int count) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("count", count);
        return template.query(QUERY, params, rowMapper);
    }

}
