package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityId;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.PersonEthnicGroup;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static gov.cdc.dataprocessing.constant.query.EntityQuery.INSERT_SQL_ENTITY_ID;
import static gov.cdc.dataprocessing.constant.query.EntityQuery.SELECT_ENTITY_ID_BY_ENTITY_ID;

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

    public void batchCreateEntityIds(List<EntityId> entityIds) {
        List<MapSqlParameterSource> parameters = entityIds.stream()
                .map(e -> new MapSqlParameterSource()
                        .addValue("entity_uid", e.getEntityUid())
                        .addValue("entity_id_seq", e.getEntityIdSeq())
                        .addValue("add_reason_cd", e.getAddReasonCode())
                        .addValue("add_time", e.getAddTime())
                        .addValue("add_user_id", e.getAddUserId())
                        .addValue("assigning_authority_cd", e.getAssigningAuthorityCode())
                        .addValue("assigning_authority_desc_txt", e.getAssigningAuthorityDescription())
                        .addValue("duration_amt", e.getDurationAmount())
                        .addValue("duration_unit_cd", e.getDurationUnitCode())
                        .addValue("effective_from_time", e.getEffectiveFromTime())
                        .addValue("effective_to_time", e.getEffectiveToTime())
                        .addValue("last_chg_reason_cd", e.getLastChangeReasonCode())
                        .addValue("last_chg_time", e.getLastChangeTime())
                        .addValue("last_chg_user_id", e.getLastChangeUserId())
                        .addValue("record_status_cd", e.getRecordStatusCode())
                        .addValue("record_status_time", e.getRecordStatusTime())
                        .addValue("root_extension_txt", e.getRootExtensionText())
                        .addValue("status_cd", e.getStatusCode())
                        .addValue("status_time", e.getStatusTime())
                        .addValue("type_cd", e.getTypeCode())
                        .addValue("type_desc_txt", e.getTypeDescriptionText())
                        .addValue("user_affiliation_txt", e.getUserAffiliationText())
                        .addValue("valid_from_time", e.getValidFromTime())
                        .addValue("valid_to_time", e.getValidToTime())
                        .addValue("as_of_date", e.getAsOfDate())
                        .addValue("assigning_authority_id_type", e.getAssigningAuthorityIdType()))
                .toList();

        jdbcTemplateOdse.batchUpdate(INSERT_SQL_ENTITY_ID,
                parameters.toArray(new MapSqlParameterSource[0]));
    }



    public List<EntityId> findEntityIds(Long entityUid) {
        MapSqlParameterSource params = new MapSqlParameterSource("entity_uid", entityUid);

        return jdbcTemplateOdse.query(SELECT_ENTITY_ID_BY_ENTITY_ID, params, new BeanPropertyRowMapper<>(EntityId.class));
    }

}
