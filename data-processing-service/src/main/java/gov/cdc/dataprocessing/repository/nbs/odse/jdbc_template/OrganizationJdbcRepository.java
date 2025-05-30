package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;


import gov.cdc.dataprocessing.repository.nbs.odse.model.organization.Organization;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import static gov.cdc.dataprocessing.constant.query.OrganizationQuery.*;

@Component
public class OrganizationJdbcRepository {
    private final NamedParameterJdbcTemplate jdbcTemplateOdse;

    public OrganizationJdbcRepository(@Qualifier("odseNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplateOdse) {
        this.jdbcTemplateOdse = jdbcTemplateOdse;
    }

    public void insertOrganization(Organization org) {
        jdbcTemplateOdse.update(INSERT_ORGANIZATION, buildParams(org));
    }

    public void updateOrganization(Organization org) {
        jdbcTemplateOdse.update(UPDATE_ORGANIZATION, buildParams(org));
    }

    public Organization findById(Long organizationUid) {
        MapSqlParameterSource params = new MapSqlParameterSource("organizationUid", organizationUid);

        var result = jdbcTemplateOdse.query(
                SELECT_ORGANIZATION_BY_ID,
                params,
                new BeanPropertyRowMapper<>(Organization.class)
        );

        return result.isEmpty() ? null : result.getFirst();
    }

    private MapSqlParameterSource buildParams(Organization org) {
        return new MapSqlParameterSource()
                .addValue("organizationUid", org.getOrganizationUid())
                .addValue("addReasonCode", org.getAddReasonCode())
                .addValue("addTime", org.getAddTime())
                .addValue("addUserId", org.getAddUserId())
                .addValue("code", org.getCode())
                .addValue("codeDescTxt", org.getCodeDescTxt())
                .addValue("description", org.getDescription())
                .addValue("durationAmt", org.getDurationAmt())
                .addValue("durationUnitCd", org.getDurationUnitCd())
                .addValue("fromTime", org.getFromTime())
                .addValue("lastChgReasonCd", org.getLastChgReasonCd())
                .addValue("lastChgTime", org.getLastChgTime())
                .addValue("lastChgUserId", org.getLastChgUserId())
                .addValue("localId", org.getLocalId())
                .addValue("recordStatusCd", org.getRecordStatusCd())
                .addValue("recordStatusTime", org.getRecordStatusTime())
                .addValue("standardIndustryClassCd", org.getStandardIndustryClassCd())
                .addValue("standardIndustryDescTxt", org.getStandardIndustryDescTxt())
                .addValue("statusCd", org.getStatusCd())
                .addValue("statusTime", org.getStatusTime())
                .addValue("toTime", org.getToTime())
                .addValue("userAffiliationTxt", org.getUserAffiliationTxt())
                .addValue("displayNm", org.getDisplayNm())
                .addValue("streetAddr1", org.getStreetAddr1())
                .addValue("streetAddr2", org.getStreetAddr2())
                .addValue("cityCd", org.getCityCd())
                .addValue("cityDescTxt", org.getCityDescTxt())
                .addValue("stateCd", org.getStateCd())
                .addValue("cntyCd", org.getCntyCd())
                .addValue("cntryCd", org.getCntryCd())
                .addValue("zipCd", org.getZipCd())
                .addValue("phoneNbr", org.getPhoneNbr())
                .addValue("phoneCntryCd", org.getPhoneCntryCd())
                .addValue("versionCtrlNbr", org.getVersionCtrlNbr())
                .addValue("electronicInd", org.getElectronicInd())
                .addValue("edxInd", org.getEdxInd());
    }
}