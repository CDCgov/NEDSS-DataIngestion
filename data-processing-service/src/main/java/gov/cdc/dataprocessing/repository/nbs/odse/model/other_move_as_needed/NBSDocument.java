package gov.cdc.dataprocessing.repository.nbs.odse.model.other_move_as_needed;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Blob;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "NBS_document")
public class NBSDocument  {

    @Id
    @Column(name = "nbs_document_uid")
    private Long nbsDocumentUid;

    @Lob
    @Column(name = "doc_payload")
    private Blob docPayload;

    @Column(name = "doc_type_cd")
    private String docTypeCd;

    @Column(name = "local_id")
    private String localId;

    @Column(name = "record_status_cd")
    private String recordStatusCd;

    @Column(name = "record_status_time")
    private Timestamp recordStatusTime;

    @Column(name = "add_user_id")
    private Long addUserId;

    @Column(name = "add_time")
    private Timestamp addTime;

    @Column(name = "prog_area_cd")
    private String progAreaCd;

    @Column(name = "jurisdiction_cd")
    private String jurisdictionCd;

    @Column(name = "txt")
    private String txt;

    @Column(name = "program_jurisdiction_oid")
    private Long programJurisdictionOid;

    @Column(name = "shared_ind")
    private String sharedInd;

    @Column(name = "version_ctrl_nbr")
    private Integer versionCtrlNbr;

    @Column(name = "cd")
    private String cd;

    @Column(name = "last_chg_time")
    private Timestamp lastChgTime;

    @Column(name = "last_chg_user_id")
    private Long lastChgUserId;

    @Column(name = "doc_purpose_cd")
    private String docPurposeCd;

    @Column(name = "doc_status_cd")
    private String docStatusCd;

    @Column(name = "cd_desc_txt")
    private String cdDescTxt;

    @Column(name = "sending_facility_nm")
    private String sendingFacilityNm;

    @Column(name = "nbs_interface_uid")
    private Long nbsInterfaceUid;

    @Column(name = "sending_app_event_id")
    private String sendingAppEventId;

    @Column(name = "sending_app_patient_id")
    private String sendingAppPatientId;

    @Lob
    @Column(name = "phdc_doc_derived")
    private Blob phdcDocDerived;

    @Column(name = "payload_view_ind_cd")
    private String payloadViewIndCd;

    @Column(name = "nbs_document_metadata_uid")
    private Long nbsDocumentMetadataUid;

    @Column(name = "external_version_ctrl_nbr")
    private Integer externalVersionCtrlNbr;

    @Column(name = "processing_decision_txt")
    private String processingDecisionTxt;

    @Column(name = "processing_decision_cd")
    private String processingDecisionCd;

    @Column(name = "effective_time")
    private Timestamp effectiveTime;

    // Constructors, getters, and setters

    // Additional variables not in SQL should be excluded here
}
