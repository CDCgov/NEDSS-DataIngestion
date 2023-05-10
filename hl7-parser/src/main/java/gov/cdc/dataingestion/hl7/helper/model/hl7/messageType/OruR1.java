package gov.cdc.dataingestion.hl7.helper.model.hl7.messageType;

import ca.uhn.hl7v2.HL7Exception;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageGroup.PatientResult;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageSegment.ContinuationPointer;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageSegment.MessageHeader;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageSegment.SoftwareSegment;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class OruR1 {
    MessageHeader messageHeader;
    List<SoftwareSegment> softwareSegment;
    List<PatientResult> patientResult;
    ContinuationPointer continuationPointer;

    public OruR1(ca.uhn.hl7v2.model.v251.message.ORU_R01 oruR01) throws HL7Exception {

        this.messageHeader = new MessageHeader(oruR01.getMSH());
        this.softwareSegment = new ArrayList<>();
        for(var item : oruR01.getSFTAll()) {
            this.softwareSegment.add(new SoftwareSegment(item));
        }
        this.patientResult = new ArrayList<>();
        for(var item : oruR01.getPATIENT_RESULTAll()) {
            this.patientResult.add(new PatientResult(item));
        }
        this.continuationPointer = new ContinuationPointer(oruR01.getDSC());

    }
}
