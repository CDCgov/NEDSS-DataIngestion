package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.Role;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static gov.cdc.dataprocessing.constant.data_field.*;
import static gov.cdc.dataprocessing.constant.query.PersonQuery.*;

@Component
public class RoleJdbcRepository {
    private final NamedParameterJdbcTemplate jdbcTemplateOdse;

    public RoleJdbcRepository(@Qualifier("odseNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplateOdse) {
        this.jdbcTemplateOdse = jdbcTemplateOdse;
    }
    public void createRole(Role role) {
        jdbcTemplateOdse.update(INSERT_SQL_ROLE, new MapSqlParameterSource()
                .addValue(SUBJECT_ENTITY_UID_DB, role.getSubjectEntityUid())
                .addValue("cd", role.getCode())
                .addValue("role_seq", role.getRoleSeq())
                .addValue(ADD_REASON_CD_DB, role.getAddReasonCode())
                .addValue(ADD_TIME_DB, role.getAddTime())
                .addValue(ADD_USER_ID_DB, role.getAddUserId())
                .addValue("cd_desc_txt", role.getCodeDescription())
                .addValue("effective_duration_amt", role.getEffectiveDurationAmount())
                .addValue("effective_duration_unit_cd", role.getEffectiveDurationUnitCode())
                .addValue("effective_from_time", role.getEffectiveFromTime())
                .addValue("effective_to_time", role.getEffectiveToTime())
                .addValue(LAST_CHG_REASON_CD_DB, role.getLastChangeReasonCode())
                .addValue(LAST_CHG_TIME_DB, role.getLastChangeTime())
                .addValue(LAST_CHG_USER_ID_DB, role.getLastChangeUserId())
                .addValue(RECORD_STATUS_CD_DB, role.getRecordStatusCode())
                .addValue(RECORD_STATUS_TIME_DB, role.getRecordStatusTime())
                .addValue("scoping_class_cd", role.getScopingClassCode())
                .addValue("scoping_entity_uid", role.getScopingEntityUid())
                .addValue("scoping_role_cd", role.getScopingRoleCode())
                .addValue("scoping_role_seq", role.getScopingRoleSeq())
                .addValue(STATUS_CD_DB, role.getStatusCode())
                .addValue(STATUS_TIME_DB, role.getStatusTime())
                .addValue("subject_class_cd", role.getSubjectClassCode())
                .addValue(USER_AFFILIATION_TXT_DB, role.getUserAffiliationText())
        );
    }


    public List<Role> findRolesByParentUid(Long subjectEntityUid) {
        MapSqlParameterSource params = new MapSqlParameterSource(SUBJECT_ENTITY_UID_DB, subjectEntityUid);

        return jdbcTemplateOdse.query(SELECT_ROLE_BY_SUBJECT_ENTITY_UID, params, new BeanPropertyRowMapper<>(Role.class));
    }

    public void updateRole(Role role) {
        jdbcTemplateOdse.update(UPDATE_ROLE_BY_UID_AND_SEQ, buildParams(role));
    }

    public List<Role> findActiveBySubjectEntityUid(Long subjectEntityUid) {
        MapSqlParameterSource params = new MapSqlParameterSource(SUBJECT_ENTITY_UID_JAVA, subjectEntityUid);
        return jdbcTemplateOdse.query(SELECT_ACTIVE_ROLES_BY_ENTITY_UID, params, new BeanPropertyRowMapper<>(Role.class));
    }

    private MapSqlParameterSource buildParams(Role r) {
        return new MapSqlParameterSource()
                .addValue(SUBJECT_ENTITY_UID_JAVA, r.getSubjectEntityUid())
                .addValue("code", r.getCode())
                .addValue("roleSeq", r.getRoleSeq())
                .addValue("addReasonCode", r.getAddReasonCode())
                .addValue(ADD_TIME_JAVA, r.getAddTime())
                .addValue(ADD_USER_ID_JAVA, r.getAddUserId())
                .addValue("codeDescription", r.getCodeDescription())
                .addValue("effectiveDurationAmount", r.getEffectiveDurationAmount())
                .addValue("effectiveDurationUnitCode", r.getEffectiveDurationUnitCode())
                .addValue("effectiveFromTime", r.getEffectiveFromTime())
                .addValue("effectiveToTime", r.getEffectiveToTime())
                .addValue("lastChangeReasonCode", r.getLastChangeReasonCode())
                .addValue("lastChangeTime", r.getLastChangeTime())
                .addValue("lastChangeUserId", r.getLastChangeUserId())
                .addValue("recordStatusCode", r.getRecordStatusCode())
                .addValue(RECORD_STATUS_TIME_JAVA, r.getRecordStatusTime())
                .addValue("scopingClassCode", r.getScopingClassCode())
                .addValue("scopingEntityUid", r.getScopingEntityUid())
                .addValue("scopingRoleCode", r.getScopingRoleCode())
                .addValue("scopingRoleSeq", r.getScopingRoleSeq())
                .addValue("statusCode", r.getStatusCode())
                .addValue(STATUS_TIME_JAVA, r.getStatusTime())
                .addValue("subjectClassCode", r.getSubjectClassCode())
                .addValue("userAffiliationText", r.getUserAffiliationText());
    }
}
