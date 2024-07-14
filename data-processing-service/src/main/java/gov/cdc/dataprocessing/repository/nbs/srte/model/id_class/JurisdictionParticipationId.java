package gov.cdc.dataprocessing.repository.nbs.srte.model.id_class;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@SuppressWarnings("all")
public class JurisdictionParticipationId implements Serializable {
    private String jurisdictionCd;
    private String fipsCd;
    private String typeCd;
}