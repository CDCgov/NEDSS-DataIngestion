package gov.cdc.dataprocessing.repository.nbs.odse.model.nbs;

import gov.cdc.dataprocessing.model.dto.nbs.NBSDocumentDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "NBS_document")
@Data
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201"})
public class NbsDocument {
    @Id
    @Column(name = "nbs_document_uid")
    private Long nbsDocumentUid;

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

    @Column(name = "phdc_doc_derived")
    private String phdcDocDerived;

    @Column(name = "payload_view_ind_cd")
    private String payloadViewIndCd;

    @Column(name = "external_version_ctrl_nbr")
    private Integer externalVersionCtrlNbr;

    @Column(name = "processing_decision_txt")
    private String processingDecisionTxt;

    @Column(name = "processing_decision_cd")
    private String processingDecisionCd;

    public  NbsDocument() {

    }


    public NbsDocument(NBSDocumentDto nbsDocumentDto) {
        this.nbsDocumentUid = nbsDocumentDto.getNbsDocumentUid();
        this.docPayload = nbsDocumentDto.getDocPayload().toString();
        this.docTypeCd = nbsDocumentDto.getDocTypeCd();
        this.localId = nbsDocumentDto.getLocalId();
        this.recordStatusCd = nbsDocumentDto.getRecordStatusCd();
        this.recordStatusTime = nbsDocumentDto.getRecordStatusTime();
        this.addUserId = nbsDocumentDto.getAddUserId();
        this.addTime = nbsDocumentDto.getAddTime();
        this.progAreaCd = nbsDocumentDto.getProgAreaCd();
        this.jurisdictionCd = nbsDocumentDto.getJurisdictionCd();
        this.txt = nbsDocumentDto.getTxt();
        this.programJurisdictionOid = nbsDocumentDto.getProgramJurisdictionOid();
        this.sharedInd = nbsDocumentDto.getSharedInd();
        this.versionCtrlNbr = nbsDocumentDto.getVersionCtrlNbr();
        this.cd = nbsDocumentDto.getCd();
        this.lastChgTime = nbsDocumentDto.getLastChgTime();
        this.lastChgUserId = nbsDocumentDto.getLastChgUserId();
        this.docPurposeCd = nbsDocumentDto.getDocPurposeCd();
        this.docStatusCd = nbsDocumentDto.getDocStatusCd();
        this.cdDescTxt = nbsDocumentDto.getCdDescTxt();
        this.sendingFacilityNm = nbsDocumentDto.getSendingFacilityNm();
        this.nbsInterfaceUid = nbsDocumentDto.getNbsInterfaceUid();
        this.sendingAppEventId = nbsDocumentDto.getSendingAppEventId();
        this.sendingAppPatientId = nbsDocumentDto.getSendingAppPatientId();
        this.phdcDocDerived = nbsDocumentDto.getPhdcDocDerived().toString();
        this.payloadViewIndCd = nbsDocumentDto.getPayloadViewIndCd();
        this.externalVersionCtrlNbr = nbsDocumentDto.getExternalVersionCtrlNbr();
        this.processingDecisionTxt = nbsDocumentDto.getProcessingDecisiontxt();
        this.processingDecisionCd = nbsDocumentDto.getProcessingDecisionCd();
    }
}
