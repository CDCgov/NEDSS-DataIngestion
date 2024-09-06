package gov.cdc.nbsDedup.nbs.odse.model.id_class;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class OrganizationHistId implements Serializable {
    private Long organizationUid;
    private int versionCtrlNbr;
}
