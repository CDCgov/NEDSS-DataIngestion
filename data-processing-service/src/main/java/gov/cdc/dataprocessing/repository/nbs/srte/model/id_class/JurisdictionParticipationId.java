package gov.cdc.dataprocessing.repository.nbs.srte.model.id_class;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
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