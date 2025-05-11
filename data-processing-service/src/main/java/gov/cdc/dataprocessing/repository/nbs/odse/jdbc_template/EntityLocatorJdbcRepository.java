package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityId;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityLocatorParticipation;
import gov.cdc.dataprocessing.repository.nbs.odse.model.locator.PhysicalLocator;
import gov.cdc.dataprocessing.repository.nbs.odse.model.locator.PostalLocator;
import gov.cdc.dataprocessing.repository.nbs.odse.model.locator.TeleLocator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static gov.cdc.dataprocessing.constant.query.EntityLocatorQuery.*;

@Component
public class EntityLocatorJdbcRepository {
    private final NamedParameterJdbcTemplate jdbcTemplateOdse;

    public EntityLocatorJdbcRepository(@Qualifier("odseNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplateOdse) {
        this.jdbcTemplateOdse = jdbcTemplateOdse;
    }

    public void createPhysicalLocator(PhysicalLocator physicalLocator) {
        jdbcTemplateOdse.update(INSERT_SQL_PHYSICAL_LOCATOR, new MapSqlParameterSource()
                .addValue("physical_locator_uid", physicalLocator.getPhysicalLocatorUid())
                .addValue("add_reason_cd", physicalLocator.getAddReasonCd())
                .addValue("add_time", physicalLocator.getAddTime())
                .addValue("add_user_id", physicalLocator.getAddUserId())
                .addValue("image_txt", physicalLocator.getImageTxt())
                .addValue("last_chg_reason_cd", physicalLocator.getLastChgReasonCd())
                .addValue("last_chg_time", physicalLocator.getLastChgTime())
                .addValue("last_chg_user_id", physicalLocator.getLastChgUserId())
                .addValue("locator_txt", physicalLocator.getLocatorTxt())
                .addValue("record_status_cd", physicalLocator.getRecordStatusCd())
                .addValue("record_status_time", physicalLocator.getRecordStatusTime())
                .addValue("user_affiliation_txt", physicalLocator.getUserAffiliationTxt())
        );
    }

    public void createPostalLocator(PostalLocator postalLocator) {
        jdbcTemplateOdse.update(INSERT_SQL_POSTAL_LOCATOR, new MapSqlParameterSource()
                .addValue("postal_locator_uid", postalLocator.getPostalLocatorUid())
                .addValue("add_reason_cd", postalLocator.getAddReasonCd())
                .addValue("add_time", postalLocator.getAddTime())
                .addValue("add_user_id", postalLocator.getAddUserId())
                .addValue("census_block_cd", postalLocator.getCensusBlockCd())
                .addValue("census_minor_civil_division_cd", postalLocator.getCensusMinorCivilDivisionCd())
                .addValue("census_track_cd", postalLocator.getCensusTrackCd())
                .addValue("city_cd", postalLocator.getCityCd())
                .addValue("city_desc_txt", postalLocator.getCityDescTxt())
                .addValue("cntry_cd", postalLocator.getCntryCd())
                .addValue("cntry_desc_txt", postalLocator.getCntryDescTxt())
                .addValue("cnty_cd", postalLocator.getCntyCd())
                .addValue("cnty_desc_txt", postalLocator.getCntyDescTxt())
                .addValue("last_chg_reason_cd", postalLocator.getLastChgReasonCd())
                .addValue("last_chg_time", postalLocator.getLastChgTime())
                .addValue("last_chg_user_id", postalLocator.getLastChgUserId())
                .addValue("MSA_congress_district_cd", postalLocator.getMsaCongressDistrictCd())
                .addValue("record_status_cd", postalLocator.getRecordStatusCd())
                .addValue("record_status_time", postalLocator.getRecordStatusTime())
                .addValue("region_district_cd", postalLocator.getRegionDistrictCd())
                .addValue("state_cd", postalLocator.getStateCd())
                .addValue("street_addr1", postalLocator.getStreetAddr1())
                .addValue("street_addr2", postalLocator.getStreetAddr2())
                .addValue("user_affiliation_txt", postalLocator.getUserAffiliationTxt())
                .addValue("zip_cd", postalLocator.getZipCd())
                .addValue("geocode_match_ind", postalLocator.getGeocodeMatchInd())
                .addValue("within_city_limits_ind", postalLocator.getWithinCityLimitsInd())
                .addValue("census_tract", postalLocator.getCensusTract())
        );
    }

    public void createTeleLocator(TeleLocator teleLocator) {
        jdbcTemplateOdse.update(INSERT_SQL_TELE_LOCATOR, new MapSqlParameterSource()
                .addValue("tele_locator_uid", teleLocator.getTeleLocatorUid())
                .addValue("add_reason_cd", teleLocator.getAddReasonCd())
                .addValue("add_time", teleLocator.getAddTime())
                .addValue("add_user_id", teleLocator.getAddUserId())
                .addValue("cntry_cd", teleLocator.getCntryCd())
                .addValue("email_address", teleLocator.getEmailAddress())
                .addValue("extension_txt", teleLocator.getExtensionTxt())
                .addValue("last_chg_reason_cd", teleLocator.getLastChgReasonCd())
                .addValue("last_chg_time", teleLocator.getLastChgTime())
                .addValue("last_chg_user_id", teleLocator.getLastChgUserId())
                .addValue("phone_nbr_txt", teleLocator.getPhoneNbrTxt())
                .addValue("record_status_cd", teleLocator.getRecordStatusCd())
                .addValue("record_status_time", teleLocator.getRecordStatusTime())
                .addValue("url_address", teleLocator.getUrlAddress())
                .addValue("user_affiliation_txt", teleLocator.getUserAffiliationTxt())
        );
    }

    public void createEntityLocatorParticipation(EntityLocatorParticipation entityLocatorParticipation) {
        jdbcTemplateOdse.update(INSERT_SQL_ENTITY_LOCATOR_PARTICIPATION, new MapSqlParameterSource()
                .addValue("entity_uid", entityLocatorParticipation.getEntityUid())
                .addValue("locator_uid", entityLocatorParticipation.getLocatorUid())
                .addValue("version_ctrl_nbr", entityLocatorParticipation.getVersionCtrlNbr())
                .addValue("add_reason_cd", entityLocatorParticipation.getAddReasonCd())
                .addValue("add_time", entityLocatorParticipation.getAddTime())
                .addValue("add_user_id", entityLocatorParticipation.getAddUserId())
                .addValue("cd", entityLocatorParticipation.getCd())
                .addValue("cd_desc_txt", entityLocatorParticipation.getCdDescTxt())
                .addValue("class_cd", entityLocatorParticipation.getClassCd())
                .addValue("duration_amt", entityLocatorParticipation.getDurationAmt())
                .addValue("duration_unit_cd", entityLocatorParticipation.getDurationUnitCd())
                .addValue("from_time", entityLocatorParticipation.getFromTime())
                .addValue("last_chg_reason_cd", entityLocatorParticipation.getLastChgReasonCd())
                .addValue("last_chg_time", entityLocatorParticipation.getLastChgTime())
                .addValue("last_chg_user_id", entityLocatorParticipation.getLastChgUserId())
                .addValue("locator_desc_txt", entityLocatorParticipation.getLocatorDescTxt())
                .addValue("record_status_cd", entityLocatorParticipation.getRecordStatusCd())
                .addValue("record_status_time", entityLocatorParticipation.getRecordStatusTime())
                .addValue("status_cd", entityLocatorParticipation.getStatusCd())
                .addValue("status_time", entityLocatorParticipation.getStatusTime())
                .addValue("to_time", entityLocatorParticipation.getToTime())
                .addValue("use_cd", entityLocatorParticipation.getUseCd())
                .addValue("user_affiliation_txt", entityLocatorParticipation.getUserAffiliationTxt())
                .addValue("valid_time_txt", entityLocatorParticipation.getValidTimeTxt())
                .addValue("as_of_date", entityLocatorParticipation.getAsOfDate())
        );
    }

    public List<EntityLocatorParticipation> findEntityLocatorParticipations(Long entityUid) {
        MapSqlParameterSource params = new MapSqlParameterSource("entity_uid", entityUid);
        return jdbcTemplateOdse.query(SELECT_ENTITY_LOCATOR_PARTICIPATIONS_BY_ENTITY_UID, params, new BeanPropertyRowMapper<>(EntityLocatorParticipation.class));
    }

}
