package gov.cdc.dataprocessing.repository.nbs.odse.model.organization;

import gov.cdc.dataprocessing.model.dto.organization.OrganizationNameDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.id_class.OrganizationNameId;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@IdClass(OrganizationNameId.class)
@Table(name = "Organization_name")
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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107"})
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
        if(organizationNameDto.getOrganizationNameSeq()!=null){
            this.organizationNameSeq = organizationNameDto.getOrganizationNameSeq();
        }
        this.nameText = organizationNameDto.getNmTxt();
        this.nameUseCode = organizationNameDto.getNmUseCd();
        this.recordStatusCode = organizationNameDto.getRecordStatusCd();
        this.defaultNameIndicator = organizationNameDto.getDefaultNmInd();
    }
}
