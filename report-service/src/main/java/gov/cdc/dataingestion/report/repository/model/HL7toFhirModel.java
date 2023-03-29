package gov.cdc.dataingestion.report.repository.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.sql.Timestamp;

@Entity
@Table(name = "elr_fhir")
public class HL7toFhirModel {
    @Id
    private String id;
    private String raw_id;
    @Column(name = "fhir_msg")
    private String fhirMessage;
    private String created_by;
    private String updated_by;
    private Timestamp created_on;
    private Timestamp updated_on;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRaw_id() {
        return raw_id;
    }

    public void setRaw_id(String raw_id) {
        this.raw_id = raw_id;
    }

    public String getFhirMessage() {
        return fhirMessage;
    }

    public void setFhirMessage(String fhirMessage) {
        this.fhirMessage = fhirMessage;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getUpdated_by() {
        return updated_by;
    }

    public void setUpdated_by(String updated_by) {
        this.updated_by = updated_by;
    }

    public Timestamp getCreated_on() {
        return created_on;
    }

    public void setCreated_on(Timestamp created_on) {
        this.created_on = created_on;
    }

    public Timestamp getUpdated_on() {
        return updated_on;
    }

    public void setUpdated_on(Timestamp updated_on) {
        this.updated_on = updated_on;
    }
}
