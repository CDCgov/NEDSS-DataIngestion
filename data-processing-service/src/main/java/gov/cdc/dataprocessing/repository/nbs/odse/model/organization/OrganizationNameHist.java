package gov.cdc.dataprocessing.repository.nbs.odse.model.organization;

import gov.cdc.dataprocessing.repository.nbs.odse.model.id_class.OrganizationNameHistId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@IdClass(OrganizationNameHistId.class)
@Table(name = "Organization_name_hist")
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809"})
public class OrganizationNameHist {
//    @EmbeddedId
//    private OrganizationNameHistId id;
//
//    @MapsId("id")
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumns({
//            @JoinColumn(name = "organization_uid", referencedColumnName = "organization_uid", nullable = false),
//            @JoinColumn(name = "organization_name_seq", referencedColumnName = "organization_name_seq", nullable = false)
//    })
//    private OrganizationName organizationName;

    @Id
    @Column(name = "organization_uid", nullable = false)
    private Long organizationUid;

    @Id
    @Column(name = "organization_name_seq", nullable = false)
    private int organizationNameSeq;

    @Id
    @Column(name = "version_ctrl_nbr", nullable = false)
    private int versionCtrlNbr;

    @Column(name = "nm_txt", length = 100)
    private String nmTxt;

    @Column(name = "nm_use_cd", length = 20)
    private String nmUseCd;

    @Column(name = "record_status_cd", length = 20)
    private String recordStatusCd;

    @Column(name = "default_nm_ind")
    private String defaultNmInd;

}