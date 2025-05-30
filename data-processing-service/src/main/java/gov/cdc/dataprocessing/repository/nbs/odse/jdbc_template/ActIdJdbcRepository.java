package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

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
                .addValue("act_uid", a.getActUid())
                .addValue("act_id_seq", a.getActIdSeq())
                .addValue("add_reason_cd", a.getAddReasonCd())
                .addValue("add_time", a.getAddTime())
                .addValue("add_user_id", a.getAddUserId())
                .addValue("assigning_authority_cd", a.getAssigningAuthorityCd())
                .addValue("assigning_authority_desc_txt", a.getAssigningAuthorityDescTxt())
                .addValue("duration_amt", a.getDurationAmt())
                .addValue("duration_unit_cd", a.getDurationUnitCd())
                .addValue("last_chg_reason_cd", a.getLastChgReasonCd())
                .addValue("last_chg_time", a.getLastChgTime())
                .addValue("last_chg_user_id", a.getLastChgUserId())
                .addValue("record_status_cd", a.getRecordStatusCd())
                .addValue("record_status_time", a.getRecordStatusTime())
                .addValue("root_extension_txt", a.getRootExtensionTxt())
                .addValue("status_cd", a.getStatusCd())
                .addValue("status_time", a.getStatusTime())
                .addValue("type_cd", a.getTypeCd())
                .addValue("type_desc_txt", a.getTypeDescTxt())
                .addValue("user_affiliation_txt", a.getUserAffiliationTxt())
                .addValue("valid_from_time", a.getValidFromTime())
                .addValue("valid_to_time", a.getValidToTime());

        jdbcTemplateOdse.update(MERGE_SQL_ACT_ID, params);
    }

    public List<ActId> findRecordsByActUid(Long actUid) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("act_uid", actUid);
        return jdbcTemplateOdse.query(
                SELECT_BY_ACT_UID_SQL,
                params,
                new BeanPropertyRowMapper<>(ActId.class)
        );
    }
}
