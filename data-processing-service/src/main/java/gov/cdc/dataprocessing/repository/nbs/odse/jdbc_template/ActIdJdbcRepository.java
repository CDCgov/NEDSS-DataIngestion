package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static gov.cdc.dataprocessing.constant.data_field.*;
import static gov.cdc.dataprocessing.constant.query.ActIdQuery.MERGE_SQL_ACT_ID;
import static gov.cdc.dataprocessing.constant.query.ActIdQuery.SELECT_BY_ACT_UID_SQL;

@Component
public class ActIdJdbcRepository {
    private final NamedParameterJdbcTemplate jdbcTemplateOdse;

    public ActIdJdbcRepository(@Qualifier("odseNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplateOdse) {
        this.jdbcTemplateOdse = jdbcTemplateOdse;
    }

    public void mergeActId(ActId a) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(ACT_UID_DB, a.getActUid())
                .addValue("act_id_seq", a.getActIdSeq())
                .addValue(ADD_REASON_CD_DB, a.getAddReasonCd())
                .addValue(ADD_TIME_DB, a.getAddTime())
                .addValue(ADD_USER_ID_DB, a.getAddUserId())
                .addValue("assigning_authority_cd", a.getAssigningAuthorityCd())
                .addValue("assigning_authority_desc_txt", a.getAssigningAuthorityDescTxt())
                .addValue("duration_amt", a.getDurationAmt())
                .addValue("duration_unit_cd", a.getDurationUnitCd())
                .addValue(LAST_CHG_REASON_CD_DB, a.getLastChgReasonCd())
                .addValue(LAST_CHG_TIME_DB, a.getLastChgTime())
                .addValue(LAST_CHG_USER_ID_DB, a.getLastChgUserId())
                .addValue(RECORD_STATUS_CD_DB, a.getRecordStatusCd())
                .addValue(RECORD_STATUS_TIME_DB, a.getRecordStatusTime())
                .addValue("root_extension_txt", a.getRootExtensionTxt())
                .addValue(STATUS_CD_DB, a.getStatusCd())
                .addValue(STATUS_TIME_DB, a.getStatusTime())
                .addValue("type_cd", a.getTypeCd())
                .addValue("type_desc_txt", a.getTypeDescTxt())
                .addValue(USER_AFFILIATION_TXT_DB, a.getUserAffiliationTxt())
                .addValue("valid_from_time", a.getValidFromTime())
                .addValue("valid_to_time", a.getValidToTime());

        jdbcTemplateOdse.update(MERGE_SQL_ACT_ID, params);
    }

    public List<ActId> findRecordsByActUid(Long actUid) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(ACT_UID_DB, actUid);
        return jdbcTemplateOdse.query(
                SELECT_BY_ACT_UID_SQL,
                params,
                new BeanPropertyRowMapper<>(ActId.class)
        );
    }
}
