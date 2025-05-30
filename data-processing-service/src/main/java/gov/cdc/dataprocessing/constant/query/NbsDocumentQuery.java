package gov.cdc.dataprocessing.constant.query;

public class NbsDocumentQuery {
    public static String MERGE_NBS_DOC = """
            MERGE INTO NBS_document AS target
            USING (
                SELECT
                    :nbs_document_uid AS nbs_document_uid,
                    :doc_payload AS doc_payload,
                    :doc_type_cd AS doc_type_cd,
                    :local_id AS local_id,
                    :record_status_cd AS record_status_cd,
                    :record_status_time AS record_status_time,
                    :add_user_id AS add_user_id,
                    :add_time AS add_time,
                    :prog_area_cd AS prog_area_cd,
                    :jurisdiction_cd AS jurisdiction_cd,
                    :txt AS txt,
                    :program_jurisdiction_oid AS program_jurisdiction_oid,
                    :shared_ind AS shared_ind,
                    :version_ctrl_nbr AS version_ctrl_nbr,
                    :cd AS cd,
                    :last_chg_time AS last_chg_time,
                    :last_chg_user_id AS last_chg_user_id,
                    :doc_purpose_cd AS doc_purpose_cd,
                    :doc_status_cd AS doc_status_cd,
                    :cd_desc_txt AS cd_desc_txt,
                    :sending_facility_nm AS sending_facility_nm,
                    :nbs_interface_uid AS nbs_interface_uid,
                    :sending_app_event_id AS sending_app_event_id,
                    :sending_app_patient_id AS sending_app_patient_id,
                    :phdc_doc_derived AS phdc_doc_derived,
                    :payload_view_ind_cd AS payload_view_ind_cd,
                    :external_version_ctrl_nbr AS external_version_ctrl_nbr,
                    :processing_decision_txt AS processing_decision_txt,
                    :processing_decision_cd AS processing_decision_cd
            ) AS source
            ON target.nbs_document_uid = source.nbs_document_uid
            
            WHEN MATCHED THEN UPDATE SET
                doc_payload = source.doc_payload,
                doc_type_cd = source.doc_type_cd,
                local_id = source.local_id,
                record_status_cd = source.record_status_cd,
                record_status_time = source.record_status_time,
                add_user_id = source.add_user_id,
                add_time = source.add_time,
                prog_area_cd = source.prog_area_cd,
                jurisdiction_cd = source.jurisdiction_cd,
                txt = source.txt,
                program_jurisdiction_oid = source.program_jurisdiction_oid,
                shared_ind = source.shared_ind,
                version_ctrl_nbr = source.version_ctrl_nbr,
                cd = source.cd,
                last_chg_time = source.last_chg_time,
                last_chg_user_id = source.last_chg_user_id,
                doc_purpose_cd = source.doc_purpose_cd,
                doc_status_cd = source.doc_status_cd,
                cd_desc_txt = source.cd_desc_txt,
                sending_facility_nm = source.sending_facility_nm,
                nbs_interface_uid = source.nbs_interface_uid,
                sending_app_event_id = source.sending_app_event_id,
                sending_app_patient_id = source.sending_app_patient_id,
                phdc_doc_derived = source.phdc_doc_derived,
                payload_view_ind_cd = source.payload_view_ind_cd,
                external_version_ctrl_nbr = source.external_version_ctrl_nbr,
                processing_decision_txt = source.processing_decision_txt,
                processing_decision_cd = source.processing_decision_cd
            
            WHEN NOT MATCHED THEN INSERT (
                nbs_document_uid, doc_payload, doc_type_cd, local_id, record_status_cd, record_status_time,
                add_user_id, add_time, prog_area_cd, jurisdiction_cd, txt, program_jurisdiction_oid, shared_ind,
                version_ctrl_nbr, cd, last_chg_time, last_chg_user_id, doc_purpose_cd, doc_status_cd, cd_desc_txt,
                sending_facility_nm, nbs_interface_uid, sending_app_event_id, sending_app_patient_id,
                phdc_doc_derived, payload_view_ind_cd, external_version_ctrl_nbr, processing_decision_txt, processing_decision_cd
            )
            VALUES (
                source.nbs_document_uid, source.doc_payload, source.doc_type_cd, source.local_id, source.record_status_cd, source.record_status_time,
                source.add_user_id, source.add_time, source.prog_area_cd, source.jurisdiction_cd, source.txt, source.program_jurisdiction_oid, source.shared_ind,
                source.version_ctrl_nbr, source.cd, source.last_chg_time, source.last_chg_user_id, source.doc_purpose_cd, source.doc_status_cd, source.cd_desc_txt,
                source.sending_facility_nm, source.nbs_interface_uid, source.sending_app_event_id, source.sending_app_patient_id,
                source.phdc_doc_derived, source.payload_view_ind_cd, source.external_version_ctrl_nbr, source.processing_decision_txt, source.processing_decision_cd
            );
            """;

    public static final String MERGE_NBS_DOC_HIST = """
MERGE INTO NBS_document_hist AS target
USING (SELECT 
    :nbsDocumentHistUid AS nbs_document_hist_uid,
    :docPayload AS doc_payload,
    :docTypeCd AS doc_type_cd,
    :localId AS local_id,
    :recordStatusCd AS record_status_cd,
    :recordStatusTime AS record_status_time,
    :addUserId AS add_user_id,
    :addTime AS add_time,
    :progAreaCd AS prog_area_cd,
    :jurisdictionCd AS jurisdiction_cd,
    :txt AS txt,
    :programJurisdictionOid AS program_jurisdiction_oid,
    :sharedInd AS shared_ind,
    :versionCtrlNbr AS version_ctrl_nbr,
    :cd AS cd,
    :lastChgTime AS last_chg_time,
    :lastChgUserId AS last_chg_user_id,
    :docPurposeCd AS doc_purpose_cd,
    :docStatusCd AS doc_status_cd,
    :cdDescTxt AS cd_desc_txt,
    :sendingFacilityNm AS sending_facility_nm,
    :nbsInterfaceUid AS nbs_interface_uid,
    :sendingAppEventId AS sending_app_event_id,
    :sendingAppPatientId AS sending_app_patient_id,
    :nbsDocumentUid AS nbs_document_uid,
    :phdcDocDerived AS phdc_doc_derived,
    :payloadViewIndCd AS payload_view_ind_cd,
    :nbsDocumentMetadataUid AS nbs_document_metadata_uid,
    :externalVersionCtrlNbr AS external_version_ctrl_nbr,
    :processingDecisionTxt AS processing_decision_txt,
    :processingDecisionCd AS processing_decision_cd
) AS source
ON target.nbs_document_hist_uid = source.nbs_document_hist_uid
WHEN MATCHED THEN UPDATE SET
    doc_payload = source.doc_payload,
    doc_type_cd = source.doc_type_cd,
    local_id = source.local_id,
    record_status_cd = source.record_status_cd,
    record_status_time = source.record_status_time,
    add_user_id = source.add_user_id,
    add_time = source.add_time,
    prog_area_cd = source.prog_area_cd,
    jurisdiction_cd = source.jurisdiction_cd,
    txt = source.txt,
    program_jurisdiction_oid = source.program_jurisdiction_oid,
    shared_ind = source.shared_ind,
    version_ctrl_nbr = source.version_ctrl_nbr,
    cd = source.cd,
    last_chg_time = source.last_chg_time,
    last_chg_user_id = source.last_chg_user_id,
    doc_purpose_cd = source.doc_purpose_cd,
    doc_status_cd = source.doc_status_cd,
    cd_desc_txt = source.cd_desc_txt,
    sending_facility_nm = source.sending_facility_nm,
    nbs_interface_uid = source.nbs_interface_uid,
    sending_app_event_id = source.sending_app_event_id,
    sending_app_patient_id = source.sending_app_patient_id,
    nbs_document_uid = source.nbs_document_uid,
    phdc_doc_derived = source.phdc_doc_derived,
    payload_view_ind_cd = source.payload_view_ind_cd,
    nbs_document_metadata_uid = source.nbs_document_metadata_uid,
    external_version_ctrl_nbr = source.external_version_ctrl_nbr,
    processing_decision_txt = source.processing_decision_txt,
    processing_decision_cd = source.processing_decision_cd
WHEN NOT MATCHED THEN INSERT (
    nbs_document_hist_uid, doc_payload, doc_type_cd, local_id, record_status_cd, record_status_time,
    add_user_id, add_time, prog_area_cd, jurisdiction_cd, txt, program_jurisdiction_oid,
    shared_ind, version_ctrl_nbr, cd, last_chg_time, last_chg_user_id, doc_purpose_cd,
    doc_status_cd, cd_desc_txt, sending_facility_nm, nbs_interface_uid, sending_app_event_id,
    sending_app_patient_id, nbs_document_uid, phdc_doc_derived, payload_view_ind_cd,
    nbs_document_metadata_uid, external_version_ctrl_nbr, processing_decision_txt, processing_decision_cd
) VALUES (
    source.nbs_document_hist_uid, source.doc_payload, source.doc_type_cd, source.local_id, source.record_status_cd, source.record_status_time,
    source.add_user_id, source.add_time, source.prog_area_cd, source.jurisdiction_cd, source.txt, source.program_jurisdiction_oid,
    source.shared_ind, source.version_ctrl_nbr, source.cd, source.last_chg_time, source.last_chg_user_id, source.doc_purpose_cd,
    source.doc_status_cd, source.cd_desc_txt, source.sending_facility_nm, source.nbs_interface_uid, source.sending_app_event_id,
    source.sending_app_patient_id, source.nbs_document_uid, source.phdc_doc_derived, source.payload_view_ind_cd,
    source.nbs_document_metadata_uid, source.external_version_ctrl_nbr, source.processing_decision_txt, source.processing_decision_cd
);
""";

}
