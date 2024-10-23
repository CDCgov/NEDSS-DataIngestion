package gov.cdc.dataprocessing.repository.nbs.odse.model.nbs;

import gov.cdc.dataprocessing.model.dto.nbs.NBSDocumentDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.sql.Timestamp;


@Entity
@Table(name = "NBS_document_hist")
@Data
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 6809 - TEST
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S6809"})
public class NbsDocumentHist {
    @Id
    @Column(name = "nbs_document_hist_uid")
    private Long nbsDocumentHistUid;

    @Column(name = "doc_payload", nullable = false)
    private String docPayload;

    @Column(name = "doc_type_cd", nullable = false)
    private String docTypeCd;

    @Column(name = "local_id", nullable = false)
    private String localId;

    @Column(name = "record_status_cd", nullable = false)
    private String recordStatusCd;

    @Column(name = "record_status_time", nullable = false)
    private Timestamp recordStatusTime;

    @Column(name = "add_user_id", nullable = false)
    private Long addUserId;

    @Column(name = "add_time", nullable = false)
    private Timestamp addTime;

    @Column(name = "prog_area_cd")
    private String progAreaCd;

    @Column(name = "jurisdiction_cd")
    private String jurisdictionCd;

    @Column(name = "txt")
    private String txt;

    @Column(name = "program_jurisdiction_oid")
    private Long programJurisdictionOid;

    @Column(name = "shared_ind", nullable = false)
    private String sharedInd;

    @Column(name = "version_ctrl_nbr", nullable = false)
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

    @Column(name = "nbs_interface_uid", nullable = false)
    private Long nbsInterfaceUid;

    @Column(name = "sending_app_event_id")
    private String sendingAppEventId;

    @Column(name = "sending_app_patient_id")
    private String sendingAppPatientId;

    @Column(name = "nbs_document_uid", nullable = false)
    private Long nbsDocumentUid;

    @Column(name = "phdc_doc_derived")
    private String phdcDocDerived;

    @Column(name = "payload_view_ind_cd")
    private String payloadViewIndCd;

    @Column(name = "nbs_document_metadata_uid", nullable = false)
    private Long nbsDocumentMetadataUid;

    @Column(name = "external_version_ctrl_nbr")
    private Integer externalVersionCtrlNbr;

    @Column(name = "processing_decision_txt")
    private String processingDecisionTxt;

    @Column(name = "processing_decision_cd")
    private String processingDecisionCd;


    public NbsDocumentHist() {

    }

    public NbsDocumentHist(NBSDocumentDto documentDto) {
        this.docPayload = documentDto.getDocPayload();
        this.docTypeCd = documentDto.getDocTypeCd();
        this.localId = documentDto.getLocalId();
        this.recordStatusCd = documentDto.getRecordStatusCd();
        this.recordStatusTime = documentDto.getRecordStatusTime();
        this.addUserId = documentDto.getAddUserId();
        this.addTime = documentDto.getAddTime();
        this.progAreaCd = documentDto.getProgAreaCd();
        this.jurisdictionCd = documentDto.getJurisdictionCd();
        this.txt = documentDto.getTxt();
        this.programJurisdictionOid = documentDto.getProgramJurisdictionOid();
        this.sharedInd = documentDto.getSharedInd();
        this.versionCtrlNbr = documentDto.getVersionCtrlNbr();
        this.cd = documentDto.getCd();
        this.lastChgTime = documentDto.getLastChgTime();
        this.lastChgUserId = documentDto.getLastChgUserId();
        this.docPurposeCd = documentDto.getDocPurposeCd();
        this.docStatusCd = documentDto.getDocStatusCd();
        this.cdDescTxt = documentDto.getCdDescTxt();
        this.sendingFacilityNm = documentDto.getSendingFacilityNm();
        this.nbsInterfaceUid = documentDto.getNbsInterfaceUid();
        this.sendingAppEventId = documentDto.getSendingAppEventId();
        this.sendingAppPatientId = documentDto.getSendingAppPatientId();
        this.nbsDocumentUid = documentDto.getNbsDocumentUid();
        this.phdcDocDerived = documentDto.getPhdcDocDerived();
        this.payloadViewIndCd = documentDto.getPayloadViewIndCd();
        this.nbsDocumentMetadataUid = documentDto.getNbsDocumentMetadataUid();
        this.externalVersionCtrlNbr = documentDto.getExternalVersionCtrlNbr();
        this.processingDecisionTxt = documentDto.getProcessingDecisiontxt();
        this.processingDecisionCd = documentDto.getProcessingDecisionCd();
    }
}
