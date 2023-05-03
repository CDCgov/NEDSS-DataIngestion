package gov.cdc.dataingestion.hl7.helper.model.hl7.messageSegment;

import ca.uhn.hl7v2.model.v251.segment.MSH;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.*;
import lombok.Getter;
import lombok.Setter;

import static gov.cdc.dataingestion.hl7.helper.helper.modelListHelper.*;

import java.util.List;

@Getter
@Setter
public class MessageHeader {
    String fieldSeparator;
    String encodingCharacters;
    Hd sendingApplication;
    Hd sendingFacility;
    Hd receivingApplication;
    Hd receivingFacility;
    Ts DateTimeOfMessage;
    String security;
    Msg messageType;
    String messageControlId;
    Pt processingId;
    Vid versionId;
    String sequenceNumber;
    String continuationPointer;
    String acceptAckType;
    String applicationAckType;
    String countryCode;
    List<String> characterSet;
    Ce principalLanguageOfMessage;
    String alternateCharacterSetHandlingScheme;
    List<Ei> messageProfileIdentifier;

    public MessageHeader(MSH msh) {
        this.fieldSeparator = msh.getFieldSeparator().getValue();
        this.encodingCharacters = msh.getEncodingCharacters().getValue();
        this.sendingApplication = new Hd(msh.getSendingApplication());
        this.sendingFacility = new Hd(msh.getSendingFacility());
        this.receivingApplication = new Hd(msh.getReceivingApplication());
        this.receivingFacility = new Hd(msh.getReceivingFacility());
        DateTimeOfMessage =  new Ts(msh.getDateTimeOfMessage());
        this.security = msh.getSecurity().getValue();
        this.messageType = new Msg(msh.getMessageType());
        this.messageControlId = msh.getMessageControlID().getValue();
        this.processingId = new Pt(msh.getProcessingID());
        this.versionId = new Vid(msh.getVersionID());
        this.sequenceNumber = msh.getSequenceNumber().getValue();
        this.continuationPointer = msh.getContinuationPointer().getValue();
        this.acceptAckType = msh.getAcceptAcknowledgmentType().getValue();
        this.applicationAckType = msh.getApplicationAcknowledgmentType().getValue();
        this.countryCode = msh.getCountryCode().getValue();
        this.characterSet = GetIdStringList(msh.getCharacterSet());
        this.principalLanguageOfMessage = new Ce(msh.getPrincipalLanguageOfMessage());
        this.alternateCharacterSetHandlingScheme = msh.getAlternateCharacterSetHandlingScheme().getValue();
        this.messageProfileIdentifier = GetEiList(msh.getMessageProfileIdentifier());
    }
}
