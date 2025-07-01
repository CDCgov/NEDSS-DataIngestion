package gov.cdc.dataprocessing.repository.nbs.msgoute.model;

import gov.cdc.dataprocessing.model.dto.dead_letter.RtiDltDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.sql.Timestamp;

@Entity
@Table(name = "rti_dlt")
@Getter
@Setter
public class RtiDlt {

    @Id
    @GenericGenerator(name = "generator", strategy = "guid", parameters = {})
    @GeneratedValue(generator = "generator")
    @Column(name = "id" , columnDefinition="uniqueidentifier")
    private String id;

    @Column(name = "nbs_interface_id")
    private Long nbsInterfaceId;


    @Column(length = 100)
    private String origin;

    @Column(length = 255)
    private String status;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String stackTrace;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String payload;

    @Column(name = "created_on")
    private Timestamp createdOn;

    @Column(name = "updated_on")
    private Timestamp updatedOn;

    public RtiDlt() {

    }

    public RtiDlt(RtiDltDto dto) {
        this.id = dto.getId();
        this.nbsInterfaceId = dto.getNbsInterfaceId();
        this.origin = dto.getOrigin();
        this.status = dto.getStatus();
        this.stackTrace = dto.getStackTrace();
        this.payload = dto.getPayload();
        this.createdOn = dto.getCreatedOn();
        this.updatedOn = dto.getUpdatedOn();
    }

}
