package gov.cdc.dataprocessing.repository.nbs.odse.model;

import gov.cdc.dataprocessing.model.PlaceDto;
import jakarta.persistence.Column;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "Place")
public class Place implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_uid")
    private Long placeUid;

    @Column(name = "add_reason_cd")
    private String addReasonCd;

    @Column(name = "add_time")
    private Timestamp addTime;

    @Column(name = "add_user_id")
    private Long addUserId;

    @Column(name = "cd")
    private String cd;

    @Column(name = "cd_desc_txt")
    private String cdDescTxt;

    @Column(name = "description")
    private String description;

    @Column(name = "duration_amt")
    private String durationAmt;

    @Column(name = "duration_unit_cd")
    private String durationUnitCd;

    @Column(name = "from_time")
    private Timestamp fromTime;

    @Column(name = "last_chg_reason_cd")
    private String lastChgReasonCd;

    @Column(name = "last_chg_time")
    private Timestamp lastChgTime;

    @Column(name = "last_chg_user_id")
    private Long lastChgUserId;

    @Column(name = "local_id")
    private String localId;

    @Column(name = "nm")
    private String nm;

    @Column(name = "record_status_cd")
    private String recordStatusCd;

    @Column(name = "record_status_time")
    private Timestamp recordStatusTime;

    @Column(name = "status_cd")
    private String statusCd;

    @Column(name = "status_time")
    private Timestamp statusTime;

    @Column(name = "to_time")
    private Timestamp toTime;

    @Column(name = "user_affiliation_txt")
    private String userAffiliationTxt;

    @Column(name = "street_addr1")
    private String streetAddr1;

    @Column(name = "street_addr2")
    private String streetAddr2;

    @Column(name = "city_cd")
    private String cityCd;

    @Column(name = "city_desc_txt")
    private String cityDescTxt;

    @Column(name = "state_cd")
    private String stateCd;

    @Column(name = "zip_cd")
    private String zipCd;

    @Column(name = "cnty_cd")
    private String cntyCd;

    @Column(name = "cntry_cd")
    private String cntryCd;

    @Column(name = "phone_nbr")
    private String phoneNbr;

    @Column(name = "phone_cntry_cd")
    private String phoneCntryCd;

    @Column(name = "version_ctrl_nbr")
    private Integer versionCtrlNbr;

    // Constructors, getters, and setters

    public Place() {

    }

    public Place(PlaceDto placeDto) {
        this.placeUid = placeDto.getPlaceUid();
        this.addReasonCd = placeDto.getAddReasonCd();
        this.addTime = placeDto.getAddTime();
        this.addUserId = placeDto.getAddUserId();
        this.cd = placeDto.getCd();
        this.cdDescTxt = placeDto.getCdDescTxt();
        this.description = placeDto.getDescription();
        this.durationAmt = placeDto.getDurationAmt();
        this.durationUnitCd = placeDto.getDurationUnitCd();
        this.fromTime = placeDto.getFromTime();
        this.lastChgReasonCd = placeDto.getLastChgReasonCd();
        this.lastChgTime = placeDto.getLastChgTime();
        this.lastChgUserId = placeDto.getLastChgUserId();
        this.localId = placeDto.getLocalId();
        this.nm = placeDto.getNm();
        this.recordStatusCd = placeDto.getRecordStatusCd();
        this.recordStatusTime = placeDto.getRecordStatusTime();
        this.statusCd = placeDto.getStatusCd();
        this.statusTime = placeDto.getStatusTime();
        this.toTime = placeDto.getToTime();
        this.userAffiliationTxt = placeDto.getUserAffiliationTxt();
//        this.streetAddr1 = placeDto.getStreetAddr1();
//        this.streetAddr2 = placeDto.getStreetAddr2();
//        this.cityCd = placeDto.getCityCd();
//        this.cityDescTxt = placeDto.getCityDescTxt();
//        this.stateCd = placeDto.getStateCd();
//        this.zipCd = placeDto.getZipCd();
//        this.cntyCd = placeDto.getCntyCd();
//        this.cntryCd = placeDto.getCntryCd();
//        this.phoneNbr = placeDto.getPhoneNbr();
//        this.phoneCntryCd = placeDto.getPhoneCntryCd();
        this.versionCtrlNbr = placeDto.getVersionCtrlNbr();
    }

}
