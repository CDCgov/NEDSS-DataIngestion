package gov.cdc.nbsDedup.nbs.srte.model.id_class;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class JurisdictionParticipationId implements Serializable {
    private String jurisdictionCd;
    private String fipsCd;
    private String typeCd;
}
