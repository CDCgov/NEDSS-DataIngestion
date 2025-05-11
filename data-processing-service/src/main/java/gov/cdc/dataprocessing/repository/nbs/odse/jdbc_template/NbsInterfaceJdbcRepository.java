package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.msgoute.model.NbsInterfaceModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import static gov.cdc.dataprocessing.constant.query.NbsInterfaceQuery.SELECT_NBS_INTERFACE_BY_UID;

@Component
public class NbsInterfaceJdbcRepository {
    private final NamedParameterJdbcTemplate jdbcTemplateOdse;

    public NbsInterfaceJdbcRepository(@Qualifier("msgouteNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplateOdse) {
        this.jdbcTemplateOdse = jdbcTemplateOdse;
    }

    public NbsInterfaceModel getNbsInterfaceByUid(Integer uid) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("nbsInterfaceUid", uid);

        return jdbcTemplateOdse.queryForObject(
                SELECT_NBS_INTERFACE_BY_UID,
                params,
                new BeanPropertyRowMapper<>(NbsInterfaceModel.class)
        );
    }


}
