package gov.cdc.nbsDedup.nbs.odse.model.organization;

import gov.cdc.nbsDedup.nbs.odse.model.id_class.OrganizationNameHistId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@IdClass(OrganizationNameHistId.class)
@Table(name = "Organization_name_hist")
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
