package gov.cdc.dataprocessing.repository.nbs.odse.model.organization;

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
//
//    @ManyToOne
//    @JoinColumn(name = "organization_uid", referencedColumnName = "organizationUid", insertable = false, updatable = false)
//    private Organization organization;

    // Constructors and other methods (if needed)
}
