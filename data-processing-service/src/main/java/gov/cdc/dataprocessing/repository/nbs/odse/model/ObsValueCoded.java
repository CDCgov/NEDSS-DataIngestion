package gov.cdc.dataprocessing.repository.nbs.odse.model;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "Obs_value_coded")
public class ObsValueCoded {

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
    private Character codeDerivedInd;

    // Constructors, getters, and setters (Lombok-generated)
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "observation_uid", referencedColumnName = "observation_uid", insertable = false, updatable = false)
//    private Observation observation;

    // Other relationships or methods if needed
}
