package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import static gov.cdc.dataprocessing.constant.query.EntityQuery.INSERT_SQL_ENTITY_ID;

@Component
public class EntityIdJdbcRepository {
    private final NamedParameterJdbcTemplate jdbcTemplateOdse;

    public EntityIdJdbcRepository(@Qualifier("odseNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplateOdse) {
        this.jdbcTemplateOdse = jdbcTemplateOdse;
    }

    public void createEntityId(EntityId entityId) {
        jdbcTemplateOdse.update(INSERT_SQL_ENTITY_ID, new MapSqlParameterSource()
                .addValue("entity_uid", entityId.getEntityUid())
                .addValue("entity_id_seq", entityId.getEntityIdSeq())
                .addValue("add_reason_cd", entityId.getAddReasonCode())
                .addValue("add_time", entityId.getAddTime())
                .addValue("add_user_id", entityId.getAddUserId())
                .addValue("assigning_authority_cd", entityId.getAssigningAuthorityCode())
                .addValue("assigning_authority_desc_txt", entityId.getAssigningAuthorityDescription())
                .addValue("duration_amt", entityId.getDurationAmount())
                .addValue("duration_unit_cd", entityId.getDurationUnitCode())
                .addValue("effective_from_time", entityId.getEffectiveFromTime())
                .addValue("effective_to_time", entityId.getEffectiveToTime())
                .addValue("last_chg_reason_cd", entityId.getLastChangeReasonCode())
                .addValue("last_chg_time", entityId.getLastChangeTime())
                .addValue("last_chg_user_id", entityId.getLastChangeUserId())
                .addValue("record_status_cd", entityId.getRecordStatusCode())
                .addValue("record_status_time", entityId.getRecordStatusTime())
                .addValue("root_extension_txt", entityId.getRootExtensionText())
                .addValue("status_cd", entityId.getStatusCode())
                .addValue("status_time", entityId.getStatusTime())
                .addValue("type_cd", entityId.getTypeCode())
                .addValue("type_desc_txt", entityId.getTypeDescriptionText())
                .addValue("user_affiliation_txt", entityId.getUserAffiliationText())
                .addValue("valid_from_time", entityId.getValidFromTime())
                .addValue("valid_to_time", entityId.getValidToTime())
                .addValue("as_of_date", entityId.getAsOfDate())
                .addValue("assigning_authority_id_type", entityId.getAssigningAuthorityIdType())
        );
    }
}
