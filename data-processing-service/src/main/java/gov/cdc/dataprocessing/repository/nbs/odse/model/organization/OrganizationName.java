package gov.cdc.dataprocessing.repository.nbs.odse.model.organization;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.OrganizationNameDT;
import gov.cdc.dataprocessing.repository.nbs.odse.model.id_class.OrganizationNameId;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@IdClass(OrganizationNameId.class)
@Table(name = "Organization_name")
public class OrganizationName {

    @Id
    @Column(name = "organization_uid", nullable = false)
    private Long organizationUid;

    @Id
    @Column(name = "organization_name_seq", nullable = false)
    private int organizationNameSeq;

    @Column(name = "nm_txt", length = 100)
    private String nameText;

    @Column(name = "nm_use_cd", length = 20)
    private String nameUseCode;

    @Column(name = "record_status_cd", length = 20)
    private String recordStatusCode;

    @Column(name = "default_nm_ind", length = 1)
    private String defaultNameIndicator;

    public OrganizationName() {
    }

    public OrganizationName(OrganizationNameDT organizationNameDT) {
        this.organizationUid = organizationNameDT.getOrganizationUid();
        this.organizationNameSeq = organizationNameDT.getOrganizationNameSeq();
        this.nameText = organizationNameDT.getNmTxt();
        this.nameUseCode = organizationNameDT.getNmUseCd();
        this.recordStatusCode = organizationNameDT.getRecordStatusCd();
        this.defaultNameIndicator = organizationNameDT.getDefaultNmInd();
    }
}
