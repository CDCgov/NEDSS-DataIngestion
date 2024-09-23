package gov.cdc.dataingestion.rti.repository.model;

import gov.cdc.dataingestion.rti.model.RtiLogStackDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "rti_log")
public class RtiLog {
    @Id
    @GenericGenerator(name = "generator", strategy = "guid", parameters = {})
    @GeneratedValue(generator = "generator")
    @Column(name = "id" , columnDefinition="uniqueidentifier")
    private String id;

    @Column(name = "nbs_interface_id", nullable = false)
    private int nbsInterfaceId;

    @Column(name = "rti_step", nullable = false, length = 255)
    private String rtiStep;

    @Column(name = "stack_trace", nullable = false, columnDefinition = "TEXT")
    private String stackTrace;

    @Column(name = "created_on", nullable = false)
    private Timestamp createdOn;


    public RtiLog() {

    }

    public RtiLog(RtiLogStackDto rtiLogStackDto) {
        nbsInterfaceId = rtiLogStackDto.getNbsInterfaceId();
        rtiStep = rtiLogStackDto.getStep();
        stackTrace = rtiLogStackDto.getStackTrace();
    }
}
