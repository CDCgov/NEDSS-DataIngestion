package gov.cdc.nbsDedup.nbs.odse.model.id_class;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class OrganizationNameHistId implements Serializable {
    private Long organizationUid;
    private int organizationNameSeq;
    private int versionCtrlNbr;
}
