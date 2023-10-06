package gov.cdc.dataingestion.hl7.helper.helper.validator;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.model.Segment;

public class OruR01Validator {
    private OruR01Validator() {

    }
    public static void patientResultValidator(Group group) throws HL7Exception {
        var groupName = group.getName();
        if(groupName.equalsIgnoreCase("PATIENT_RESULT")) {
            /// PATIENT
            patientValidator(group);
            // ORDER OBSERVATION
            orderObservationValidator(group);

        }
    }

    private static void patientValidator(Group group) throws HL7Exception {
        var patientCounter = group.getAll("PATIENT").length;
        if (patientCounter == 0) {
            throw new HL7Exception("Patient Group is Empty");
        }
        for (int i = 0; i < patientCounter; i++) {
            var orderObsGroup = group.get("PATIENT", i);

            Group patientGroup = (Group) orderObsGroup;
            var pid = (Segment) patientGroup.get("PID");

            String patientName = pid.getField(5, 0).encode();

            if(patientName == null || patientName.isEmpty()) {
                throw new HL7Exception("Error Occurred at PID-5");
            }

        }
    }

    private static void orderObservationValidator(Group group) throws HL7Exception {
        var orderCounter = group.getAll("ORDER_OBSERVATION").length;
        if (orderCounter == 0) {
            throw new HL7Exception("Order Observation is Empty");
        }

        for (int i = 0; i < orderCounter; i++) {
            var orderObsGroup = (Group) group.get("ORDER_OBSERVATION", i);
            var obr = (Segment) orderObsGroup.get("OBR");

            String identifierCode = obr.getField(4, 0).encode();

            if(identifierCode == null ||  identifierCode.isEmpty()) {
                throw new HL7Exception("Error Occurred at OBR-4");
            }
        }
    }
}
