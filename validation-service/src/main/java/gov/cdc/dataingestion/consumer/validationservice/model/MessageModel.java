package gov.cdc.dataingestion.consumer.validationservice.model;

import gov.cdc.dataingestion.consumer.validationservice.model.enums.MessageType;

public class MessageModel {
    private String rawMessage;
    private MessageType messageType;
    private String messageVersion;

    public String getRawMessage() {
        return rawMessage;
    }

    public void setRawMessage(String rawMessage) {
        this.rawMessage = rawMessage;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public String getMessageVersion() {
        return messageVersion;
    }

    public void setMessageVersion(String messageVersion) {
        this.messageVersion = messageVersion;
    }

}
