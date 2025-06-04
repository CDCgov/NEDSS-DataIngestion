package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsActEntity;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsActEntityHist;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static gov.cdc.dataprocessing.constant.query.NbsActQuery.*;

@Component
public class NbsActJdbcRepository {
    private final NamedParameterJdbcTemplate jdbcTemplateOdse;

    public NbsActJdbcRepository(@Qualifier("odseNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplateOdse) {
        this.jdbcTemplateOdse = jdbcTemplateOdse;
    }

    public void mergeNbsActEntity(NbsActEntity nbsActEntity) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("actUid", nbsActEntity.getActUid());
        params.addValue("addTime", nbsActEntity.getAddTime());
        params.addValue("addUserId", nbsActEntity.getAddUserId());
        params.addValue("entityUid", nbsActEntity.getEntityUid());
        params.addValue("entityVersionCtrlNbr", nbsActEntity.getEntityVersionCtrlNbr());
        params.addValue("lastChgTime", nbsActEntity.getLastChgTime());
        params.addValue("lastChgUserId", nbsActEntity.getLastChgUserId());
        params.addValue("recordStatusCd", nbsActEntity.getRecordStatusCd());
        params.addValue("recordStatusTime", nbsActEntity.getRecordStatusTime());
        params.addValue("typeCd", nbsActEntity.getTypeCd());

        jdbcTemplateOdse.update(MERGE_NBS_ACT_ENTITY, params);
    }

    public void mergeNbsActEntityHist(NbsActEntityHist hist) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("nbsActEntityUid", hist.getNbsActEntityUid());
        params.addValue("actUid", hist.getActUid());
        params.addValue("addTime", hist.getAddTime());
        params.addValue("addUserId", hist.getAddUserId());
        params.addValue("entityUid", hist.getEntityUid());
        params.addValue("entityVersionCtrlNbr", hist.getEntityVersionCtrlNbr());
        params.addValue("lastChgTime", hist.getLastChgTime());
        params.addValue("lastChgUserId", hist.getLastChgUserId());
        params.addValue("recordStatusCd", hist.getRecordStatusCd());
        params.addValue("recordStatusTime", hist.getRecordStatusTime());
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
