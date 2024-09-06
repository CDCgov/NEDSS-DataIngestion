package gov.cdc.nbsDedup.nbs.odse.model.observation;


import gov.cdc.nbsDedup.model.dto.observation.ObsValueCodedDto;
import gov.cdc.nbsDedup.nbs.odse.model.id_class.ObsValueCodedId;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;


@Data
@Entity
@Table(name = "Obs_value_coded")
@IdClass(ObsValueCodedId.class)
public class ObsValueCoded implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "observation_uid")
    private Long observationUid;

    @Id
    @Column(name = "code")
    private String code;

    @Column(name = "code_system_cd")
    private String codeSystemCd;

    @Column(name = "code_system_desc_txt")
    private String codeSystemDescTxt;

    @Column(name = "code_version")
    private String codeVersion;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "original_txt")
    private String originalTxt;

    @Column(name = "alt_cd")
    private String altCd;

    @Column(name = "alt_cd_desc_txt")
    private String altCdDescTxt;

    @Column(name = "alt_cd_system_cd")
    private String altCdSystemCd;

    @Column(name = "alt_cd_system_desc_txt")
    private String altCdSystemDescTxt;

    @Column(name = "code_derived_ind")
    private String  codeDerivedInd;

    // Constructors, getters, and setters (Lombok-generated)
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "observation_uid", referencedColumnName = "observation_uid", insertable = false, updatable = false)
//    private Observation observation;

    // Other relationships or methods if needed


    // Constructor in ObsValueCodedDto class (DTO to Domain)
    public ObsValueCoded(ObsValueCodedDto obsValueCodedDto) {
        this.observationUid = obsValueCodedDto.getObservationUid();
        this.altCd = obsValueCodedDto.getAltCd();
        this.altCdDescTxt = obsValueCodedDto.getAltCdDescTxt();
        this.altCdSystemCd = obsValueCodedDto.getAltCdSystemCd();
        this.altCdSystemDescTxt = obsValueCodedDto.getAltCdSystemDescTxt();
        this.code = obsValueCodedDto.getCode();
        this.codeDerivedInd = obsValueCodedDto.getCodeDerivedInd();
        this.codeSystemCd = obsValueCodedDto.getCodeSystemCd();
        this.codeSystemDescTxt = obsValueCodedDto.getCodeSystemDescTxt();
        this.codeVersion = obsValueCodedDto.getCodeVersion();
        this.displayName = obsValueCodedDto.getDisplayName();
        this.originalTxt = obsValueCodedDto.getOriginalTxt();
    }

    public ObsValueCoded() {

    }
}
