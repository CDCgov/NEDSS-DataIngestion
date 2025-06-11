package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.notification.Notification;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import static gov.cdc.dataprocessing.constant.data_field.*;
import static gov.cdc.dataprocessing.constant.query.NotificationQuery.*;

@Component
public class NotificationJdbcRepository {
    private final NamedParameterJdbcTemplate jdbcTemplateOdse;

    public NotificationJdbcRepository(@Qualifier("odseNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplateOdse) {
        this.jdbcTemplateOdse = jdbcTemplateOdse;
    }

    public void insertNotification(Notification notification) {
        jdbcTemplateOdse.update(INSERT_SQL_NOTIFICATION, buildParamMap(notification));
    }

    public void updateNotification(Notification notification) {
        jdbcTemplateOdse.update(UPDATE_SQL_NOTIFICATION, buildParamMap(notification));
    }

    public Notification findById(Long notificationUid) {
        MapSqlParameterSource params = new MapSqlParameterSource("notificationUid", notificationUid);

        var results = jdbcTemplateOdse.query(
                SELECT_NOTIFICATION_BY_ID,
                params,
                new BeanPropertyRowMapper<>(Notification.class)
        );

        return results.isEmpty() ? null : results.getFirst();
    }

    private MapSqlParameterSource buildParamMap(Notification notification) {
        return new MapSqlParameterSource()
                .addValue("notification_uid", notification.getNotificationUid())
                .addValue("activity_duration_amt", notification.getActivityDurationAmt())
                .addValue("activity_duration_unit_cd", notification.getActivityDurationUnitCd())
                .addValue("activity_from_time", notification.getActivityFromTime())
                .addValue("activity_to_time", notification.getActivityToTime())
                .addValue(ADD_REASON_CD_DB, notification.getAddReasonCd())
                .addValue(ADD_TIME_DB, notification.getAddTime())
                .addValue(ADD_USER_ID_DB, notification.getAddUserId())
                .addValue("case_class_cd", notification.getCaseClassCd())
                .addValue("case_condition_cd", notification.getCaseConditionCd())
                .addValue("cd", notification.getCd())
                .addValue("cd_desc_txt", notification.getCdDescTxt())
                .addValue("confidentiality_cd", notification.getConfidentialityCd())
                .addValue("confidentiality_desc_txt", notification.getConfidentialityDescTxt())
                .addValue("confirmation_method_cd", notification.getConfirmationMethodCd())
                .addValue("effective_duration_amt", notification.getEffectiveDurationAmt())
                .addValue("effective_duration_unit_cd", notification.getEffectiveDurationUnitCd())
                .addValue("effective_from_time", notification.getEffectiveFromTime())
                .addValue("effective_to_time", notification.getEffectiveToTime())
                .addValue("jurisdiction_cd", notification.getJurisdictionCd())
                .addValue(LAST_CHG_REASON_CD_DB, notification.getLastChgReasonCd())
                .addValue(LAST_CHG_TIME_DB, notification.getLastChgTime())
                .addValue(LAST_CHG_USER_ID_DB, notification.getLastChgUserId())
                .addValue("local_id", notification.getLocalId())
                .addValue("message_txt", notification.getMessageTxt())
                .addValue("method_cd", notification.getMethodCd())
                .addValue("method_desc_txt", notification.getMethodDescTxt())
                .addValue("mmwr_week", notification.getMmwrWeek())
                .addValue("mmwr_year", notification.getMmwrYear())
                .addValue("nedss_version_nbr", notification.getNedssVersionNbr())
                .addValue("prog_area_cd", notification.getProgAreaCd())
                .addValue("reason_cd", notification.getReasonCd())
                .addValue("reason_desc_txt", notification.getReasonDescTxt())
                .addValue("record_count", notification.getRecordCount())
                .addValue(RECORD_STATUS_CD_DB, notification.getRecordStatusCd())
                .addValue(RECORD_STATUS_TIME_DB, notification.getRecordStatusTime())
                .addValue("repeat_nbr", notification.getRepeatNbr())
                .addValue("rpt_sent_time", notification.getRptSentTime())
                .addValue("rpt_source_cd", notification.getRptSourceCd())
                .addValue("rpt_source_type_cd", notification.getRptSourceTypeCd())
                .addValue(STATUS_CD_DB, notification.getStatusCd())
                .addValue(STATUS_TIME_DB, notification.getStatusTime())
                .addValue("txt", notification.getTxt())
                .addValue(USER_AFFILIATION_TXT_DB, notification.getUserAffiliationTxt())
                .addValue("program_jurisdiction_oid", notification.getProgramJurisdictionOid())
                .addValue("shared_ind", notification.getSharedInd())
                .addValue("version_ctrl_nbr", notification.getVersionCtrlNbr())
                .addValue("auto_resend_ind", notification.getAutoResendInd())
                .addValue("export_receiving_facility_uid", notification.getExportReceivingFacilityUid())
                .addValue("nbs_interface_uid", notification.getNbsInterfaceUid());
    }

}
