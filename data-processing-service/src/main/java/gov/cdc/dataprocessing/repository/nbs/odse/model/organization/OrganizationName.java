package gov.cdc.dataprocessing.repository.nbs.odse.model.organization;

import gov.cdc.dataprocessing.model.dto.organization.OrganizationNameDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.id_class.OrganizationNameId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

    public OrganizationName(OrganizationNameDto organizationNameDto) {
        this.organizationUid = organizationNameDto.getOrganizationUid();
        if (organizationNameDto.getOrganizationNameSeq() != null) {
            this.organizationNameSeq = organizationNameDto.getOrganizationNameSeq();
        }
        this.nameText = organizationNameDto.getNmTxt();
        this.nameUseCode = organizationNameDto.getNmUseCd();
        this.recordStatusCode = organizationNameDto.getRecordStatusCd();
        this.defaultNameIndicator = organizationNameDto.getDefaultNmInd();
    }
}
