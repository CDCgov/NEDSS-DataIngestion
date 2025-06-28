package gov.cdc.dataprocessing.model.dto.dead_letter;

import gov.cdc.dataprocessing.repository.nbs.msgoute.model.RtiDlt;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class RtiDltDto {
    private String id;
    private Long nbsInterfaceId;
    private String origin;
    private String status;
    private String stackTrace;
    private String payload;
    private Timestamp createdOn;
    private Timestamp updatedOn;

    public RtiDltDto() {

    }

    public RtiDltDto(RtiDlt domain) {
        this.id = domain.getId();
        this.nbsInterfaceId = domain.getNbsInterfaceId();
        this.origin = domain.getOrigin();
        this.status = domain.getStatus();
        this.stackTrace = domain.getStackTrace();
        this.payload = domain.getPayload();
        this.createdOn = domain.getCreatedOn();
        this.updatedOn = domain.getUpdatedOn();
    }

}
