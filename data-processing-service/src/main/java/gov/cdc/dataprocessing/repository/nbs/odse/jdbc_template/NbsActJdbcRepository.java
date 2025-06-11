package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsActEntity;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsActEntityHist;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static gov.cdc.dataprocessing.constant.data_field.*;
import static gov.cdc.dataprocessing.constant.query.NbsActQuery.*;

@Component
public class NbsActJdbcRepository {
    private final NamedParameterJdbcTemplate jdbcTemplateOdse;

    public NbsActJdbcRepository(@Qualifier("odseNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplateOdse) {
        this.jdbcTemplateOdse = jdbcTemplateOdse;
    }

    public void mergeNbsActEntity(NbsActEntity nbsActEntity) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(ACT_UID_JAVA, nbsActEntity.getActUid());
        params.addValue(ADD_TIME_JAVA, nbsActEntity.getAddTime());
        params.addValue(ADD_USER_ID_JAVA, nbsActEntity.getAddUserId());
        params.addValue(ENTITY_UID_JAVA, nbsActEntity.getEntityUid());
        params.addValue("entityVersionCtrlNbr", nbsActEntity.getEntityVersionCtrlNbr());
        params.addValue(LAST_CHG_TIME_JAVA, nbsActEntity.getLastChgTime());
        params.addValue(LAST_CHG_USER_ID_JAVA, nbsActEntity.getLastChgUserId());
        params.addValue(RECORD_STATUS_CD_JAVA, nbsActEntity.getRecordStatusCd());
        params.addValue(RECORD_STATUS_TIME_JAVA, nbsActEntity.getRecordStatusTime());
        params.addValue("typeCd", nbsActEntity.getTypeCd());

        jdbcTemplateOdse.update(MERGE_NBS_ACT_ENTITY, params);
    }

    public void mergeNbsActEntityHist(NbsActEntityHist hist) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("nbsActEntityUid", hist.getNbsActEntityUid());
        params.addValue(ACT_UID_JAVA, hist.getActUid());
        params.addValue(ADD_TIME_JAVA, hist.getAddTime());
        params.addValue(ADD_USER_ID_JAVA, hist.getAddUserId());
        params.addValue(ENTITY_UID_JAVA, hist.getEntityUid());
        params.addValue("entityVersionCtrlNbr", hist.getEntityVersionCtrlNbr());
        params.addValue(LAST_CHG_TIME_JAVA, hist.getLastChgTime());
        params.addValue(LAST_CHG_USER_ID_JAVA, hist.getLastChgUserId());
        params.addValue(RECORD_STATUS_CD_JAVA, hist.getRecordStatusCd());
        params.addValue(RECORD_STATUS_TIME_JAVA, hist.getRecordStatusTime());
        params.addValue("typeCd", hist.getTypeCd());

        jdbcTemplateOdse.update(MERGE_NBS_ACT_ENTITY_HIST, params);
    }

    public void deleteNbsEntityAct(Long nbsActEntityUid) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("nbsActEntityUid", nbsActEntityUid);
        jdbcTemplateOdse.update(DELETE_NBS_ACT_ENTITY_BY_UID, params);
    }

    public List<NbsActEntity> getNbsActEntitiesByActUid(Long uid) {
        MapSqlParameterSource params = new MapSqlParameterSource("uid", uid);
        return jdbcTemplateOdse.query(
                SELECT_NBS_ACT_ENTITIES_BY_ACT_UID,
                params,
                new BeanPropertyRowMapper<>(NbsActEntity.class)
        );
    }



}
