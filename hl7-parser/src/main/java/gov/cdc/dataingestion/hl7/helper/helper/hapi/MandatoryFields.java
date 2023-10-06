package gov.cdc.dataingestion.hl7.helper.helper.hapi;

import ca.uhn.hl7v2.model.MessageVisitorFactory;


public class MandatoryFields implements MessageVisitorFactory<MandatoryFieldsVisitor> {

    private String targetType = "";
    public MandatoryFields(String targetType) {
        this.targetType = targetType;
    }
    public MandatoryFieldsVisitor create() {
        return new MandatoryFieldsVisitor(targetType);
    }

}
