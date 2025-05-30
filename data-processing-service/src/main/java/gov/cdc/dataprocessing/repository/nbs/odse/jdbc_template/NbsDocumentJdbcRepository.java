package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsDocument;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsDocumentHist;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import static gov.cdc.dataprocessing.constant.query.NbsDocumentQuery.MERGE_NBS_DOC;
import static gov.cdc.dataprocessing.constant.query.NbsDocumentQuery.MERGE_NBS_DOC_HIST;

@Component
public class NbsDocumentJdbcRepository {
    private final NamedParameterJdbcTemplate jdbcTemplateOdse;

    public NbsDocumentJdbcRepository(@Qualifier("odseNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplateOdse) {
        this.jdbcTemplateOdse = jdbcTemplateOdse;
    }

    public void mergeNbsDocument(NbsDocument doc) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("nbs_document_uid", doc.getNbsDocumentUid())
                .addValue("doc_payload", doc.getDocPayload())
                .addValue("doc_type_cd", doc.getDocTypeCd())
                .addValue("local_id", doc.getLocalId())
                .addValue("record_status_cd", doc.getRecordStatusCd())
                .addValue("record_status_time", doc.getRecordStatusTime())
                .addValue("add_user_id", doc.getAddUserId())
                .addValue("add_time", doc.getAddTime())
                .addValue("prog_area_cd", doc.getProgAreaCd())
                .addValue("jurisdiction_cd", doc.getJurisdictionCd())
                .addValue("txt", doc.getTxt())
                .addValue("program_jurisdiction_oid", doc.getProgramJurisdictionOid())
                .addValue("shared_ind", doc.getSharedInd())
                .addValue("version_ctrl_nbr", doc.getVersionCtrlNbr())
                .addValue("cd", doc.getCd())
                .addValue("last_chg_time", doc.getLastChgTime())
                .addValue("last_chg_user_id", doc.getLastChgUserId())
                .addValue("doc_purpose_cd", doc.getDocPurposeCd())
                .addValue("doc_status_cd", doc.getDocStatusCd())
                .addValue("cd_desc_txt", doc.getCdDescTxt())
                .addValue("sending_facility_nm", doc.getSendingFacilityNm())
                .addValue("nbs_interface_uid", doc.getNbsInterfaceUid())
                .addValue("sending_app_event_id", doc.getSendingAppEventId())
                .addValue("sending_app_patient_id", doc.getSendingAppPatientId())
                .addValue("phdc_doc_derived", doc.getPhdcDocDerived())
                .addValue("payload_view_ind_cd", doc.getPayloadViewIndCd())
                .addValue("external_version_ctrl_nbr", doc.getExternalVersionCtrlNbr())
                .addValue("processing_decision_txt", doc.getProcessingDecisionTxt())
                .addValue("processing_decision_cd", doc.getProcessingDecisionCd());

        jdbcTemplateOdse.update(MERGE_NBS_DOC, params);
    }

    public void mergeNbsDocumentHist(NbsDocumentHist hist) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("nbs_document_hist_uid", hist.getNbsDocumentHistUid())
                .addValue("doc_payload", hist.getDocPayload())
                .addValue("doc_type_cd", hist.getDocTypeCd())
                .addValue("local_id", hist.getLocalId())
                .addValue("record_status_cd", hist.getRecordStatusCd())
                .addValue("record_status_time", hist.getRecordStatusTime())
                .addValue("add_user_id", hist.getAddUserId())
                .addValue("add_time", hist.getAddTime())
                .addValue("prog_area_cd", hist.getProgAreaCd())
                .addValue("jurisdiction_cd", hist.getJurisdictionCd())
                .addValue("txt", hist.getTxt())
                .addValue("program_jurisdiction_oid", hist.getProgramJurisdictionOid())
                .addValue("shared_ind", hist.getSharedInd())
                .addValue("version_ctrl_nbr", hist.getVersionCtrlNbr())
                .addValue("cd", hist.getCd())
                .addValue("last_chg_time", hist.getLastChgTime())
                .addValue("last_chg_user_id", hist.getLastChgUserId())
                .addValue("doc_purpose_cd", hist.getDocPurposeCd())
                .addValue("doc_status_cd", hist.getDocStatusCd())
                .addValue("cd_desc_txt", hist.getCdDescTxt())
                .addValue("sending_facility_nm", hist.getSendingFacilityNm())
                .addValue("nbs_interface_uid", hist.getNbsInterfaceUid())
                .addValue("sending_app_event_id", hist.getSendingAppEventId())
                .addValue("sending_app_patient_id", hist.getSendingAppPatientId())
                .addValue("nbs_document_uid", hist.getNbsDocumentUid())
                .addValue("phdc_doc_derived", hist.getPhdcDocDerived())
                .addValue("payload_view_ind_cd", hist.getPayloadViewIndCd())
                .addValue("nbs_document_metadata_uid", hist.getNbsDocumentMetadataUid())
                .addValue("external_version_ctrl_nbr", hist.getExternalVersionCtrlNbr())
                .addValue("processing_decision_txt", hist.getProcessingDecisionTxt())
                .addValue("processing_decision_cd", hist.getProcessingDecisionCd());

        jdbcTemplateOdse.update(MERGE_NBS_DOC_HIST, params);
    }
}
