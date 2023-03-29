package gov.cdc.dataingestion.validation.model;

import jakarta.persistence.*;

@Entity
@Table(name = "elr_validated")
public class ValidatedELRModel {
    @Id
    private String id;

    @Column(name = "validated_message")
    private String rawMessage;

    @Column(name = "message_type")
    private String messageType;

    @Column(name = "message_version")
    private String messageVersion;

    public String getRawMessage() {
        return rawMessage;
    }

    public void setRawMessage(String rawMessage) {
        this.rawMessage = rawMessage;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessageVersion() {
        return messageVersion;
    }

    public void setMessageVersion(String messageVersion) {
        this.messageVersion = messageVersion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}