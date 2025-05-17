package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.Role;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static gov.cdc.dataprocessing.constant.query.PersonQuery.INSERT_SQL_ROLE;
import static gov.cdc.dataprocessing.constant.query.PersonQuery.SELECT_ROLE_BY_SUBJECT_ENTITY_UID;

@Component
public class RoleJdbcRepository {
    private final NamedParameterJdbcTemplate jdbcTemplateOdse;

    public RoleJdbcRepository(@Qualifier("odseNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplateOdse) {
        this.jdbcTemplateOdse = jdbcTemplateOdse;
    }
    public void createRole(Role role) {
        jdbcTemplateOdse.update(INSERT_SQL_ROLE, new MapSqlParameterSource()
                .addValue("subject_entity_uid", role.getSubjectEntityUid())
                .addValue("cd", role.getCode())
                .addValue("role_seq", role.getRoleSeq())
                .addValue("add_reason_cd", role.getAddReasonCode())
                .addValue("add_time", role.getAddTime())
                .addValue("add_user_id", role.getAddUserId())
                .addValue("cd_desc_txt", role.getCodeDescription())
                .addValue("effective_duration_amt", role.getEffectiveDurationAmount())
                .addValue("effective_duration_unit_cd", role.getEffectiveDurationUnitCode())
                .addValue("effective_from_time", role.getEffectiveFromTime())
                .addValue("effective_to_time", role.getEffectiveToTime())
                .addValue("last_chg_reason_cd", role.getLastChangeReasonCode())
                .addValue("last_chg_time", role.getLastChangeTime())
                .addValue("last_chg_user_id", role.getLastChangeUserId())
                .addValue("record_status_cd", role.getRecordStatusCode())
                .addValue("record_status_time", role.getRecordStatusTime())
                .addValue("scoping_class_cd", role.getScopingClassCode())
                .addValue("scoping_entity_uid", role.getScopingEntityUid())
                .addValue("scoping_role_cd", role.getScopingRoleCode())
                .addValue("scoping_role_seq", role.getScopingRoleSeq())
                .addValue("status_cd", role.getStatusCode())
                .addValue("status_time", role.getStatusTime())
                .addValue("subject_class_cd", role.getSubjectClassCode())
                .addValue("user_affiliation_txt", role.getUserAffiliationText())
        );
    }


    public List<Role> findRolesByParentUid(Long subjectEntityUid) {
        MapSqlParameterSource params = new MapSqlParameterSource("subject_entity_uid", subjectEntityUid);

        return jdbcTemplateOdse.query(SELECT_ROLE_BY_SUBJECT_ENTITY_UID, params, new BeanPropertyRowMapper<>(Role.class));
    }
}
