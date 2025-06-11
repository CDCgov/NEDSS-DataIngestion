package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityLocatorParticipation;
import gov.cdc.dataprocessing.repository.nbs.odse.model.locator.PhysicalLocator;
import gov.cdc.dataprocessing.repository.nbs.odse.model.locator.PostalLocator;
import gov.cdc.dataprocessing.repository.nbs.odse.model.locator.TeleLocator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static gov.cdc.dataprocessing.constant.data_field.*;
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
                .addValue(ADD_REASON_CD_DB, physicalLocator.getAddReasonCd())
                .addValue(ADD_TIME_DB, physicalLocator.getAddTime())
                .addValue(ADD_USER_ID_DB, physicalLocator.getAddUserId())
                .addValue("image_txt", physicalLocator.getImageTxt())
                .addValue(LAST_CHG_REASON_CD_DB, physicalLocator.getLastChgReasonCd())
                .addValue(LAST_CHG_TIME_DB, physicalLocator.getLastChgTime())
                .addValue(LAST_CHG_USER_ID_DB, physicalLocator.getLastChgUserId())
                .addValue("locator_txt", physicalLocator.getLocatorTxt())
                .addValue(RECORD_STATUS_CD_DB, physicalLocator.getRecordStatusCd())
                .addValue(RECORD_STATUS_TIME_DB, physicalLocator.getRecordStatusTime())
                .addValue(USER_AFFILIATION_TXT_DB, physicalLocator.getUserAffiliationTxt())
        );
    }
    public void createPostalLocator(PostalLocator postalLocator) {
        jdbcTemplateOdse.update(INSERT_SQL_POSTAL_LOCATOR, new MapSqlParameterSource()
                .addValue("postal_locator_uid", postalLocator.getPostalLocatorUid())
                .addValue(ADD_REASON_CD_DB, postalLocator.getAddReasonCd())
                .addValue(ADD_TIME_DB, postalLocator.getAddTime())
                .addValue(ADD_USER_ID_DB, postalLocator.getAddUserId())
                .addValue("census_block_cd", postalLocator.getCensusBlockCd())
                .addValue("census_minor_civil_division_cd", postalLocator.getCensusMinorCivilDivisionCd())
                .addValue("census_track_cd", postalLocator.getCensusTrackCd())
                .addValue("city_cd", postalLocator.getCityCd())
                .addValue("city_desc_txt", postalLocator.getCityDescTxt())
                .addValue("cntry_cd", postalLocator.getCntryCd())
                .addValue("cntry_desc_txt", postalLocator.getCntryDescTxt())
                .addValue("cnty_cd", postalLocator.getCntyCd())
                .addValue("cnty_desc_txt", postalLocator.getCntyDescTxt())
                .addValue(LAST_CHG_REASON_CD_DB, postalLocator.getLastChgReasonCd())
                .addValue(LAST_CHG_TIME_DB, postalLocator.getLastChgTime())
                .addValue(LAST_CHG_USER_ID_DB, postalLocator.getLastChgUserId())
                .addValue("MSA_congress_district_cd", postalLocator.getMsaCongressDistrictCd())
                .addValue(RECORD_STATUS_CD_DB, postalLocator.getRecordStatusCd())
                .addValue(RECORD_STATUS_TIME_DB, postalLocator.getRecordStatusTime())
                .addValue("region_district_cd", postalLocator.getRegionDistrictCd())
                .addValue("state_cd", postalLocator.getStateCd())
                .addValue("street_addr1", postalLocator.getStreetAddr1())
                .addValue("street_addr2", postalLocator.getStreetAddr2())
                .addValue(USER_AFFILIATION_TXT_DB, postalLocator.getUserAffiliationTxt())
                .addValue("zip_cd", postalLocator.getZipCd())
                .addValue("geocode_match_ind", postalLocator.getGeocodeMatchInd())
                .addValue("within_city_limits_ind", postalLocator.getWithinCityLimitsInd())
                .addValue("census_tract", postalLocator.getCensusTract())
        );
    }
    public void createTeleLocator(TeleLocator teleLocator) {
        jdbcTemplateOdse.update(INSERT_SQL_TELE_LOCATOR, new MapSqlParameterSource()
                .addValue("tele_locator_uid", teleLocator.getTeleLocatorUid())
                .addValue(ADD_REASON_CD_DB, teleLocator.getAddReasonCd())
                .addValue(ADD_TIME_DB, teleLocator.getAddTime())
                .addValue(ADD_USER_ID_DB, teleLocator.getAddUserId())
                .addValue("cntry_cd", teleLocator.getCntryCd())
                .addValue("email_address", teleLocator.getEmailAddress())
                .addValue("extension_txt", teleLocator.getExtensionTxt())
                .addValue(LAST_CHG_REASON_CD_DB, teleLocator.getLastChgReasonCd())
                .addValue(LAST_CHG_TIME_DB, teleLocator.getLastChgTime())
                .addValue(LAST_CHG_USER_ID_DB, teleLocator.getLastChgUserId())
                .addValue("phone_nbr_txt", teleLocator.getPhoneNbrTxt())
                .addValue(RECORD_STATUS_CD_DB, teleLocator.getRecordStatusCd())
                .addValue(RECORD_STATUS_TIME_DB, teleLocator.getRecordStatusTime())
                .addValue("url_address", teleLocator.getUrlAddress())
                .addValue(USER_AFFILIATION_TXT_DB, teleLocator.getUserAffiliationTxt())
        );
    }
    public void updateTeleLocator(TeleLocator t) {
        jdbcTemplateOdse.update(UPDATE_TELE_LOCATOR, buildParamsTele(t));
    }
    public void createEntityLocatorParticipation(EntityLocatorParticipation entityLocatorParticipation) {
        jdbcTemplateOdse.update(INSERT_SQL_ENTITY_LOCATOR_PARTICIPATION, new MapSqlParameterSource()
                .addValue(ENTITY_UID_DB, entityLocatorParticipation.getEntityUid())
                .addValue("locator_uid", entityLocatorParticipation.getLocatorUid())
                .addValue("version_ctrl_nbr", entityLocatorParticipation.getVersionCtrlNbr())
                .addValue(ADD_REASON_CD_DB, entityLocatorParticipation.getAddReasonCd())
                .addValue(ADD_TIME_DB, entityLocatorParticipation.getAddTime())
                .addValue(ADD_USER_ID_DB, entityLocatorParticipation.getAddUserId())
                .addValue("cd", entityLocatorParticipation.getCd())
                .addValue("cd_desc_txt", entityLocatorParticipation.getCdDescTxt())
                .addValue("class_cd", entityLocatorParticipation.getClassCd())
                .addValue("duration_amt", entityLocatorParticipation.getDurationAmt())
                .addValue("duration_unit_cd", entityLocatorParticipation.getDurationUnitCd())
                .addValue("from_time", entityLocatorParticipation.getFromTime())
                .addValue(LAST_CHG_REASON_CD_DB, entityLocatorParticipation.getLastChgReasonCd())
                .addValue(LAST_CHG_TIME_DB, entityLocatorParticipation.getLastChgTime())
                .addValue(LAST_CHG_USER_ID_DB, entityLocatorParticipation.getLastChgUserId())
                .addValue("locator_desc_txt", entityLocatorParticipation.getLocatorDescTxt())
                .addValue(RECORD_STATUS_CD_DB, entityLocatorParticipation.getRecordStatusCd())
                .addValue(RECORD_STATUS_TIME_DB, entityLocatorParticipation.getRecordStatusTime())
                .addValue(STATUS_CD_DB, entityLocatorParticipation.getStatusCd())
                .addValue(STATUS_TIME_DB, entityLocatorParticipation.getStatusTime())
                .addValue("to_time", entityLocatorParticipation.getToTime())
                .addValue("use_cd", entityLocatorParticipation.getUseCd())
                .addValue(USER_AFFILIATION_TXT_DB, entityLocatorParticipation.getUserAffiliationTxt())
                .addValue("valid_time_txt", entityLocatorParticipation.getValidTimeTxt())
                .addValue("as_of_date", entityLocatorParticipation.getAsOfDate())
        );
    }
    public void updateEntityLocatorParticipation(EntityLocatorParticipation entity) {
        jdbcTemplateOdse.update(UPDATE_ENTITY_LOCATOR_PARTICIPATION, buildParams(entity));
    }
    public void updatePostalLocator(PostalLocator p) {
        jdbcTemplateOdse.update(UPDATE_POSTAL_LOCATOR, buildParamsPostal(p));
    }
    public void updatePhysicalLocator(PhysicalLocator p) {
        jdbcTemplateOdse.update(UPDATE_PHYSICAL_LOCATOR, buildParamsPhysical(p));
    }


    private MapSqlParameterSource buildParamsPhysical(PhysicalLocator p) {
        return new MapSqlParameterSource()
                .addValue("physicalLocatorUid", p.getPhysicalLocatorUid())
                .addValue(ADD_REASON_CD_JAVA, p.getAddReasonCd())
                .addValue(ADD_TIME_JAVA, p.getAddTime())
                .addValue(ADD_USER_ID_JAVA, p.getAddUserId())
                .addValue("imageTxt", p.getImageTxt())
                .addValue(LAST_CHG_REASON_CD_JAVA, p.getLastChgReasonCd())
                .addValue(LAST_CHG_TIME_JAVA, p.getLastChgTime())
                .addValue(LAST_CHG_USER_ID_JAVA, p.getLastChgUserId())
                .addValue("locatorTxt", p.getLocatorTxt())
                .addValue(RECORD_STATUS_CD_JAVA, p.getRecordStatusCd())
                .addValue(RECORD_STATUS_TIME_JAVA, p.getRecordStatusTime())
                .addValue(USER_AFFILIATION_TXT_JAVA, p.getUserAffiliationTxt());
    }
    private MapSqlParameterSource buildParams(EntityLocatorParticipation e) {
        return new MapSqlParameterSource()
                .addValue(ENTITY_UID_JAVA, e.getEntityUid())
                .addValue("locatorUid", e.getLocatorUid())
                .addValue(ADD_REASON_CD_JAVA, e.getAddReasonCd())
                .addValue(ADD_TIME_JAVA, e.getAddTime())
                .addValue(ADD_USER_ID_JAVA, e.getAddUserId())
                .addValue("cd", e.getCd())
                .addValue("cdDescTxt", e.getCdDescTxt())
                .addValue("classCd", e.getClassCd())
                .addValue("durationAmt", e.getDurationAmt())
                .addValue("durationUnitCd", e.getDurationUnitCd())
                .addValue("fromTime", e.getFromTime())
                .addValue(LAST_CHG_REASON_CD_JAVA, e.getLastChgReasonCd())
                .addValue(LAST_CHG_TIME_JAVA, e.getLastChgTime())
                .addValue(LAST_CHG_USER_ID_JAVA, e.getLastChgUserId())
                .addValue("locatorDescTxt", e.getLocatorDescTxt())
                .addValue(RECORD_STATUS_CD_JAVA, e.getRecordStatusCd())
                .addValue(RECORD_STATUS_TIME_JAVA, e.getRecordStatusTime())
                .addValue(STATUS_CD_JAVA, e.getStatusCd())
                .addValue(STATUS_TIME_JAVA, e.getStatusTime())
                .addValue("toTime", e.getToTime())
                .addValue("useCd", e.getUseCd())
                .addValue(USER_AFFILIATION_TXT_JAVA, e.getUserAffiliationTxt())
                .addValue("validTimeTxt", e.getValidTimeTxt())
                .addValue("asOfDate", e.getAsOfDate())
                .addValue("versionCtrlNbr", e.getVersionCtrlNbr());
    }
    private MapSqlParameterSource buildParamsPostal(PostalLocator p) {
        return new MapSqlParameterSource()
                .addValue("postalLocatorUid", p.getPostalLocatorUid())
                .addValue(ADD_REASON_CD_JAVA, p.getAddReasonCd())
                .addValue(ADD_TIME_JAVA, p.getAddTime())
                .addValue(ADD_USER_ID_JAVA, p.getAddUserId())
                .addValue("censusBlockCd", p.getCensusBlockCd())
                .addValue("censusMinorCivilDivisionCd", p.getCensusMinorCivilDivisionCd())
                .addValue("censusTrackCd", p.getCensusTrackCd())
                .addValue("cityCd", p.getCityCd())
                .addValue("cityDescTxt", p.getCityDescTxt())
                .addValue("cntryCd", p.getCntryCd())
                .addValue("cntryDescTxt", p.getCntryDescTxt())
                .addValue("cntyCd", p.getCntyCd())
                .addValue("cntyDescTxt", p.getCntyDescTxt())
                .addValue(LAST_CHG_REASON_CD_JAVA, p.getLastChgReasonCd())
                .addValue(LAST_CHG_TIME_JAVA, p.getLastChgTime())
                .addValue(LAST_CHG_USER_ID_JAVA, p.getLastChgUserId())
                .addValue("msaCongressDistrictCd", p.getMsaCongressDistrictCd())
                .addValue(RECORD_STATUS_CD_JAVA, p.getRecordStatusCd())
                .addValue(RECORD_STATUS_TIME_JAVA, p.getRecordStatusTime())
                .addValue("regionDistrictCd", p.getRegionDistrictCd())
                .addValue("stateCd", p.getStateCd())
                .addValue("streetAddr1", p.getStreetAddr1())
                .addValue("streetAddr2", p.getStreetAddr2())
                .addValue(USER_AFFILIATION_TXT_JAVA, p.getUserAffiliationTxt())
                .addValue("zipCd", p.getZipCd())
                .addValue("geocodeMatchInd", p.getGeocodeMatchInd())
                .addValue("withinCityLimitsInd", p.getWithinCityLimitsInd())
                .addValue("censusTract", p.getCensusTract());
    }
    private MapSqlParameterSource buildParamsTele(TeleLocator t) {
        return new MapSqlParameterSource()
                .addValue("teleLocatorUid", t.getTeleLocatorUid())
                .addValue(ADD_REASON_CD_JAVA, t.getAddReasonCd())
                .addValue(ADD_TIME_JAVA, t.getAddTime())
                .addValue(ADD_USER_ID_JAVA, t.getAddUserId())
                .addValue("cntryCd", t.getCntryCd())
                .addValue("emailAddress", t.getEmailAddress())
                .addValue("extensionTxt", t.getExtensionTxt())
                .addValue(LAST_CHG_REASON_CD_JAVA, t.getLastChgReasonCd())
                .addValue(LAST_CHG_TIME_JAVA, t.getLastChgTime())
                .addValue(LAST_CHG_USER_ID_JAVA, t.getLastChgUserId())
                .addValue("phoneNbrTxt", t.getPhoneNbrTxt())
                .addValue(RECORD_STATUS_CD_JAVA, t.getRecordStatusCd())
                .addValue(RECORD_STATUS_TIME_JAVA, t.getRecordStatusTime())
                .addValue("urlAddress", t.getUrlAddress())
                .addValue(USER_AFFILIATION_TXT_JAVA, t.getUserAffiliationTxt());
    }

    public List<EntityLocatorParticipation> findEntityLocatorParticipations(Long entityUid) {
        MapSqlParameterSource params = new MapSqlParameterSource(ENTITY_UID_DB, entityUid);
        return jdbcTemplateOdse.query(SELECT_ENTITY_LOCATOR_PARTICIPATIONS_BY_ENTITY_UID, params, new BeanPropertyRowMapper<>(EntityLocatorParticipation.class));
    }
    public List<EntityLocatorParticipation> findByEntityUid(Long entityUid) {
        MapSqlParameterSource params = new MapSqlParameterSource(ENTITY_UID_JAVA, entityUid);

        return jdbcTemplateOdse.query(
                SELECT_ENTITY_LOCATOR_BY_ENTITY_UID,
                params,
                new BeanPropertyRowMapper<>(EntityLocatorParticipation.class)
        );
    }
    public List<PostalLocator> findByPostalLocatorUids(List<Long> uids) {
        MapSqlParameterSource params = new MapSqlParameterSource("uids", uids);
        return jdbcTemplateOdse.query(SELECT_POSTAL_LOCATOR_BY_UIDS, params, new BeanPropertyRowMapper<>(PostalLocator.class));
    }
    public List<PhysicalLocator> findByPhysicalLocatorUids(List<Long> uids) {
        MapSqlParameterSource params = new MapSqlParameterSource("uids", uids);
        return jdbcTemplateOdse.query(
                SELECT_PHYSICAL_LOCATOR_BY_UIDS,
                params,
                new BeanPropertyRowMapper<>(PhysicalLocator.class)
        );
    }
    public List<TeleLocator> findByTeleLocatorUids(List<Long> uids) {
        MapSqlParameterSource params = new MapSqlParameterSource("uids", uids);
        return jdbcTemplateOdse.query(SELECT_TELE_LOCATOR_BY_UIDS, params, new BeanPropertyRowMapper<>(TeleLocator.class));
    }




}
