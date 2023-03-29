package gov.cdc.dataingestion.validation.model;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name = "elr_raw")
public class RawERLModel {

    @Id
    private String id;

    @Column(name = "message_type")
    private String type;
    private String payload;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

}
