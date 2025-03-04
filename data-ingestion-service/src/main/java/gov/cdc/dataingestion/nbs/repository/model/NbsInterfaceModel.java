package gov.cdc.dataingestion.nbs.repository.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "NBS_interface")
@NoArgsConstructor
@Getter
@Setter
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
public class NbsInterfaceModel {
	private static final String TOSTRING_COLUMN_SPACER = ", ";
	
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	@Column(name="nbs_interface_uid")
    private Integer nbsInterfaceUid;

    @Column(name = "payload", length = 2048, nullable = true)
    private String payload;
    
    @Column(name = "imp_exp_ind_cd", length = 8, nullable = true)
    private String impExpIndCd;
    
    @Column(name = "record_status_cd", length = 100, nullable = true)
    private String recordStatusCd;
    
    @Column(name = "record_status_time")
    private Timestamp recordStatusTime;
    
    @Column(name = "add_time", length = 100, nullable = false)
    private Timestamp addTime;
    
    @Column(name = "system_nm", length = 50, nullable = true)
    private String systemNm;
    
    @Column(name = "doc_type_cd", length = 20, nullable = true)
    private String docTypeCd;
    
    @Column(name = "original_payload", length = 1024, nullable = true)
    private String originalPayload;
    
    @Column(name = "original_doc_type_cd", length = 100, nullable = true)
    private String originalDocTypeCd;
    
    @Column(name = "filler_order_nbr", length = 250, nullable = true)
    private String fillerOrderNbr;      
    
    @Column(name = "lab_clia", length = 250, nullable = true)
    private String labClia;
    
    @Column(name = "specimen_coll_date")
    private Timestamp specimenCollDate;
    
    @Column(name = "order_test_code", length = 250, nullable = true)
    private String orderTestCode;
    
    @Column(name = "OBSERVATION_UID")
    private Integer observationUid;

    @Column(name = "original_payload_RR", length = 1024, nullable = true)
    private String originalPayloadRR;

    @Column(name = "original_doc_type_cd_RR", length = 100, nullable = true)
    private String originalDocTypeCdRR;

    @Override
    public String toString() {
        return this.getNbsInterfaceUid() +
                TOSTRING_COLUMN_SPACER +
                this.getImpExpIndCd() +
                TOSTRING_COLUMN_SPACER +
                this.getRecordStatusCd() +
                TOSTRING_COLUMN_SPACER +
                this.getSystemNm() +
                TOSTRING_COLUMN_SPACER +
                this.getDocTypeCd() +
                TOSTRING_COLUMN_SPACER +
                this.getRecordStatusTime().toString() +
                TOSTRING_COLUMN_SPACER +
                this.getAddTime().toString();
    }
}