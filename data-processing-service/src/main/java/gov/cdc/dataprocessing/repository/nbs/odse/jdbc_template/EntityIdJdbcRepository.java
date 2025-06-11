package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static gov.cdc.dataprocessing.constant.data_field.*;
import static gov.cdc.dataprocessing.constant.query.EntityQuery.*;

@Component
public class EntityIdJdbcRepository {
    private final NamedParameterJdbcTemplate jdbcTemplateOdse;

    public EntityIdJdbcRepository(@Qualifier("odseNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplateOdse) {
        this.jdbcTemplateOdse = jdbcTemplateOdse;
    }

    public void createEntityId(EntityId entityId) {
        jdbcTemplateOdse.update(INSERT_SQL_ENTITY_ID, new MapSqlParameterSource()
                .addValue(ENTITY_UID_DB, entityId.getEntityUid())
                .addValue("entity_id_seq", entityId.getEntityIdSeq())
                .addValue(ADD_REASON_CD_DB, entityId.getAddReasonCode())
                .addValue(ADD_TIME_DB, entityId.getAddTime())
                .addValue(ADD_USER_ID_DB, entityId.getAddUserId())
                .addValue("assigning_authority_cd", entityId.getAssigningAuthorityCode())
                .addValue("assigning_authority_desc_txt", entityId.getAssigningAuthorityDescription())
                .addValue("duration_amt", entityId.getDurationAmount())
                .addValue("duration_unit_cd", entityId.getDurationUnitCode())
                .addValue("effective_from_time", entityId.getEffectiveFromTime())
                .addValue("effective_to_time", entityId.getEffectiveToTime())
                .addValue(LAST_CHG_REASON_CD_DB, entityId.getLastChangeReasonCode())
                .addValue(LAST_CHG_TIME_DB, entityId.getLastChangeTime())
                .addValue(LAST_CHG_USER_ID_DB, entityId.getLastChangeUserId())
                .addValue(RECORD_STATUS_CD_DB, entityId.getRecordStatusCode())
                .addValue(RECORD_STATUS_TIME_DB, entityId.getRecordStatusTime())
                .addValue("root_extension_txt", entityId.getRootExtensionText())
                .addValue(STATUS_CD_DB, entityId.getStatusCode())
                .addValue(STATUS_TIME_DB, entityId.getStatusTime())
                .addValue("type_cd", entityId.getTypeCode())
                .addValue("type_desc_txt", entityId.getTypeDescriptionText())
                .addValue(USER_AFFILIATION_TXT_DB, entityId.getUserAffiliationText())
                .addValue("valid_from_time", entityId.getValidFromTime())
                .addValue("valid_to_time", entityId.getValidToTime())
                .addValue("as_of_date", entityId.getAsOfDate())
                .addValue("assigning_authority_id_type", entityId.getAssigningAuthorityIdType())
        );
    }

    public void batchCreateEntityIds(List<EntityId> entityIds) {
        List<MapSqlParameterSource> parameters = entityIds.stream()
                .map(e -> new MapSqlParameterSource()
                        .addValue(ENTITY_UID_DB, e.getEntityUid())
                        .addValue("entity_id_seq", e.getEntityIdSeq())
                        .addValue(ADD_REASON_CD_DB, e.getAddReasonCode())
                        .addValue(ADD_TIME_DB, e.getAddTime())
                        .addValue(ADD_USER_ID_DB, e.getAddUserId())
                        .addValue("assigning_authority_cd", e.getAssigningAuthorityCode())
                        .addValue("assigning_authority_desc_txt", e.getAssigningAuthorityDescription())
                        .addValue("duration_amt", e.getDurationAmount())
                        .addValue("duration_unit_cd", e.getDurationUnitCode())
                        .addValue("effective_from_time", e.getEffectiveFromTime())
                        .addValue("effective_to_time", e.getEffectiveToTime())
                        .addValue(LAST_CHG_REASON_CD_DB, e.getLastChangeReasonCode())
                        .addValue(LAST_CHG_TIME_DB, e.getLastChangeTime())
                        .addValue(LAST_CHG_USER_ID_DB, e.getLastChangeUserId())
                        .addValue(RECORD_STATUS_CD_DB, e.getRecordStatusCode())
                        .addValue(RECORD_STATUS_TIME_DB, e.getRecordStatusTime())
                        .addValue("root_extension_txt", e.getRootExtensionText())
                        .addValue(STATUS_CD_DB, e.getStatusCode())
                        .addValue(STATUS_TIME_DB, e.getStatusTime())
                        .addValue("type_cd", e.getTypeCode())
                        .addValue("type_desc_txt", e.getTypeDescriptionText())
                        .addValue(USER_AFFILIATION_TXT_DB, e.getUserAffiliationText())
                        .addValue("valid_from_time", e.getValidFromTime())
                        .addValue("valid_to_time", e.getValidToTime())
                        .addValue("as_of_date", e.getAsOfDate())
                        .addValue("assigning_authority_id_type", e.getAssigningAuthorityIdType()))
                .toList();

        jdbcTemplateOdse.batchUpdate(INSERT_SQL_ENTITY_ID,
                parameters.toArray(new MapSqlParameterSource[0]));
    }

    public void mergeEntityId(EntityId entityId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(ENTITY_UID_JAVA, entityId.getEntityUid())
                .addValue("entityIdSeq", entityId.getEntityIdSeq())
                .addValue("addReasonCode", entityId.getAddReasonCode())
                .addValue(ADD_TIME_JAVA, entityId.getAddTime())
                .addValue(ADD_USER_ID_JAVA, entityId.getAddUserId())
                .addValue("assigningAuthorityCode", entityId.getAssigningAuthorityCode())
                .addValue("assigningAuthorityDescription", entityId.getAssigningAuthorityDescription())
                .addValue("durationAmount", entityId.getDurationAmount())
                .addValue("durationUnitCode", entityId.getDurationUnitCode())
                .addValue("effectiveFromTime", entityId.getEffectiveFromTime())
                .addValue("effectiveToTime", entityId.getEffectiveToTime())
                .addValue("lastChangeReasonCode", entityId.getLastChangeReasonCode())
                .addValue("lastChangeTime", entityId.getLastChangeTime())
                .addValue("lastChangeUserId", entityId.getLastChangeUserId())
                .addValue("recordStatusCode", entityId.getRecordStatusCode())
                .addValue(RECORD_STATUS_TIME_JAVA, entityId.getRecordStatusTime())
                .addValue("rootExtensionText", entityId.getRootExtensionText())
                .addValue("statusCode", entityId.getStatusCode())
                .addValue(STATUS_TIME_JAVA, entityId.getStatusTime())
                .addValue("typeCode", entityId.getTypeCode())
                .addValue("typeDescriptionText", entityId.getTypeDescriptionText())
                .addValue("userAffiliationText", entityId.getUserAffiliationText())
                .addValue("validFromTime", entityId.getValidFromTime())
                .addValue("validToTime", entityId.getValidToTime())
                .addValue("asOfDate", entityId.getAsOfDate())
                .addValue("assigningAuthorityIdType", entityId.getAssigningAuthorityIdType());

        jdbcTemplateOdse.update(MERGE_ENTITY_ID, params);
    }


    public List<EntityId> findEntityIds(Long entityUid) {
        MapSqlParameterSource params = new MapSqlParameterSource(ENTITY_UID_DB, entityUid);

        return jdbcTemplateOdse.query(SELECT_ENTITY_ID_BY_ENTITY_ID, params, new BeanPropertyRowMapper<>(EntityId.class));
    }

    public List<EntityId> findEntityIdsActive(Long entityUid) {
        MapSqlParameterSource params = new MapSqlParameterSource(ENTITY_UID_DB, entityUid);

        return jdbcTemplateOdse.query(SELECT_ENTITY_ID_BY_ENTITY_ID_ACTIVE, params, new BeanPropertyRowMapper<>(EntityId.class));
    }

}
