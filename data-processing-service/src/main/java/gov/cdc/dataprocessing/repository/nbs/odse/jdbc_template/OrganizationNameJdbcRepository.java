package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;


import gov.cdc.dataprocessing.repository.nbs.odse.model.organization.OrganizationName;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static gov.cdc.dataprocessing.constant.query.OrganizationNameQuery.*;

@Component
public class OrganizationNameJdbcRepository {
    private final NamedParameterJdbcTemplate jdbcTemplateOdse;

    public OrganizationNameJdbcRepository(@Qualifier("odseNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplateOdse) {
        this.jdbcTemplateOdse = jdbcTemplateOdse;
    }

    public void insertOrganizationName(OrganizationName name) {
        jdbcTemplateOdse.update(INSERT_ORGANIZATION_NAME, buildParams(name));
    }

    public void updateOrganizationName(OrganizationName name) {
        jdbcTemplateOdse.update(UPDATE_ORGANIZATION_NAME, buildParams(name));
    }

    public List<OrganizationName> findByOrganizationUid(Long organizationUid) {
        MapSqlParameterSource params = new MapSqlParameterSource("organizationUid", organizationUid);
        return jdbcTemplateOdse.query(
                SELECT_ORGANIZATION_NAMES_BY_ORG_UID,
                params,
                new BeanPropertyRowMapper<>(OrganizationName.class)
        );
    }

    private MapSqlParameterSource buildParams(OrganizationName name) {
        return new MapSqlParameterSource()
                .addValue("organizationUid", name.getOrganizationUid())
                .addValue("organizationNameSeq", name.getOrganizationNameSeq())
                .addValue("nameText", name.getNameText())
                .addValue("nameUseCode", name.getNameUseCode())
                .addValue("recordStatusCode", name.getRecordStatusCode())
                .addValue("defaultNameIndicator", name.getDefaultNameIndicator());
    }
}