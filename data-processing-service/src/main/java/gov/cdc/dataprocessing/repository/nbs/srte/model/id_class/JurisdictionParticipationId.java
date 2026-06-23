package gov.cdc.dataprocessing.repository.nbs.srte.model.id_class;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JurisdictionParticipationId implements Serializable {
  private String jurisdictionCd;
  private String fipsCd;
  private String typeCd;
}
