package gov.cdc.dataprocessing.repository.nbs.odse.model.id_class;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrganizationNameHistId implements Serializable {
  private Long organizationUid;
  private int organizationNameSeq;
  private int versionCtrlNbr;
}
