package gov.cdc.dataprocessing.repository.nbs.odse.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "Export_receiving_facility")
public class ExportReceivingFacility {

    @Id
    @Column(name = "export_receiving_facility_uid")
    private Long exportReceivingFacilityUid;

    @Column(name = "add_time")
    private Timestamp addTime;

    @Column(name = "report_type")
    private String reportType;

    @Column(name = "add_user_id")
    private Long addUserId;

    @Column(name = "last_chg_time")
    private Timestamp lastChgTime;

    @Column(name = "last_chg_user_id")
    private Long lastChgUserId;

    @Column(name = "record_status_cd")
    private String recordStatusCd;

    @Column(name = "receiving_system_nm")
    private String receivingSystemNm;

    @Column(name = "receiving_system_oid")
    private String receivingSystemOid;

    @Column(name = "receiving_system_short_nm")
    private String receivingSystemShortName;

    @Column(name = "receiving_system_owner")
    private String receivingSystemOwner;

    @Column(name = "receiving_system_owner_oid")
    private String receivingSystemOwnerOid;

    @Column(name = "receiving_system_desc_txt")
    private String receivingSystemDescTxt;

    @Column(name = "sending_ind_cd")
    private String sendingIndCd;

    @Column(name = "receiving_ind_cd")
    private String receivingIndCd;

    @Column(name = "allow_transfer_ind_cd")
    private String allowTransferIndCd;

    @Column(name = "admin_comment")
    private String adminComment;

    @Column(name = "sending_ind_desc_txt")
    private String sendingIndDescTxt;

    @Column(name = "receiving_ind_desc_txt")
    private String receivingIndDescTxt;

    @Column(name = "allow_transfer_ind_desc_txt")
    private String allowTransferIndDescTxt;

    @Column(name = "report_type_desc_txt")
    private String reportTypeDescTxt;

    @Column(name = "record_status_cd_desc_txt")
    private String recordStatusCdDescTxt;

    @Column(name = "jur_derive_ind_cd")
    private String jurDeriveIndCd;

}