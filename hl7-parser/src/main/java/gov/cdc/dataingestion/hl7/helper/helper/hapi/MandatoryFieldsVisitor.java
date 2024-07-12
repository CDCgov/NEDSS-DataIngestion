package gov.cdc.dataingestion.hl7.helper.helper.hapi;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.Location;
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.validation.builder.support.ValidatingMessageVisitor;

import static gov.cdc.dataingestion.hl7.helper.constant.hl7.EventTrigger.ORU_01;
import static gov.cdc.dataingestion.hl7.helper.constant.hl7.MessageType.ORU;
import static gov.cdc.dataingestion.hl7.helper.helper.validator.OruR01Validator.patientResultValidator;

public class MandatoryFieldsVisitor extends ValidatingMessageVisitor {
    private String targetType = "";
    public MandatoryFieldsVisitor(String targetType) {
        this.targetType = targetType;
    }
    @Override
    public boolean start(Group group, Location location) throws HL7Exception {
        if (this.targetType.equalsIgnoreCase(ORU + "_" + ORU_01)) {
            patientResultValidator(group);
        }
        return false;
    }




}
