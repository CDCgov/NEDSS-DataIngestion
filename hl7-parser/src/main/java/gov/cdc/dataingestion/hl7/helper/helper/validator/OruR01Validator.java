package gov.cdc.dataingestion.hl7.helper.helper.validator;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.Location;
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.model.Segment;
import org.apache.commons.lang3.StringUtils;

public class OruR01Validator {
    private static final String REQ_FLD_MISSING = "Required field missing ";
    private static final String PATIENT_RESULT = "PATIENT_RESULT";
    private static final String ORDER_OBSERVATION = "ORDER_OBSERVATION";
    private static final String OBSERVATION = "OBSERVATION";
    private static final String PATIENT = "PATIENT";
    private static final String SPECIMEN = "SPECIMEN";

    private OruR01Validator() {

    }

    public static void patientResultValidator(Group group, Location location) throws HL7Exception {
        var groupName = group.getName();
        var groupNameWithLocation = location.toString();
        if (groupName.equalsIgnoreCase(PATIENT_RESULT)) {
            /// PATIENT
            patientValidator(group, groupNameWithLocation);
            // ORDER OBSERVATION
            orderObservationValidator(group, groupNameWithLocation);
        }
    }

    public static void mshValidator(Segment segment) throws HL7Exception {
        String dateTime = segment.getField(7, 0).encode();
        if (StringUtils.isEmpty(dateTime)) {
            throw new HL7Exception(REQ_FLD_MISSING + "/MSH/DateTimeOfMessage/Time/Year");
        }

        String msgControlId = segment.getField(10, 0).encode();
        if (StringUtils.isEmpty(msgControlId)) {
            throw new HL7Exception(REQ_FLD_MISSING + "/MSH/MessageControlID");
        }

        String processingId = segment.getField(11, 0).encode();
        if (StringUtils.isEmpty(processingId)) {
            throw new HL7Exception(REQ_FLD_MISSING + "/MSH/ProcessingID");
        }
    }

    public static void sftValidator(Segment segment, String locationName) throws HL7Exception {
        if (!segment.isEmpty()) {
            String vendorOrg = segment.getField(1, 0).encode();
            if (StringUtils.isEmpty(vendorOrg)) {
                throw new HL7Exception(REQ_FLD_MISSING + locationName + "/SoftwareVendorOrganization");
            }
            String versionNo = segment.getField(2, 0).encode();
            if (StringUtils.isEmpty(versionNo)) {
                throw new HL7Exception(REQ_FLD_MISSING + locationName + "/SoftwareCertifiedVersionOrReleaseNumber");
            }
            String sftPrdName = segment.getField(3, 0).encode();
            if (StringUtils.isEmpty(sftPrdName)) {
                throw new HL7Exception(REQ_FLD_MISSING + locationName + "/SoftwareProductName");
            }
            String sftBinaryId = segment.getField(4, 0).encode();
            if (StringUtils.isEmpty(sftBinaryId)) {
                throw new HL7Exception(REQ_FLD_MISSING + locationName + "/SoftwareBinaryID");
            }
        }
    }

    private static void patientValidator(Group group, String groupNameWithLocation) throws HL7Exception {
        var patientCounter = group.getAll(PATIENT).length;
        if (patientCounter == 0) {
            throw new HL7Exception(groupNameWithLocation + "PATIENT Group is Empty");
        }
        for (int i = 0; i < patientCounter; i++) {
            var orderObsGroup = group.get(PATIENT, i);

            Group patientGroup = (Group) orderObsGroup;
            //PID
            var pid = (Segment) patientGroup.get("PID");
            String patientName = pid.getField(5, 0).encode();
            if (StringUtils.isEmpty(patientName)) {
                throw new HL7Exception(REQ_FLD_MISSING + groupNameWithLocation + PATIENT + "(" + i + ")/PID/PatientName");
            }
            //NK1 - optional
            var nk1 = (Segment) patientGroup.get("NK1");
            if (!nk1.isEmpty()) {
                String nk1Value = nk1.getField(1, 0).encode();
                if (StringUtils.isEmpty(nk1Value)) {
                    throw new HL7Exception(REQ_FLD_MISSING + groupNameWithLocation + PATIENT + "(" + i + ")/NK1/SetID");
                }
            }
        }
    }

    private static void orderObservationValidator(Group group, String parentGroup) throws HL7Exception {
        var orderCounter = group.getAll(ORDER_OBSERVATION).length;
        if (orderCounter == 0) {
            throw new HL7Exception(parentGroup + ORDER_OBSERVATION + " Group is Empty");
        }
        for (int i = 0; i < orderCounter; i++) {
            var orderObsGroup = (Group) group.get(ORDER_OBSERVATION, i);
            //OBR
            obrValidator(orderObsGroup, i, parentGroup);
            //OBX
            obxValidator(orderObsGroup, i, parentGroup);
            //SPM
            spmValidator(orderObsGroup, i, parentGroup);
            //ORC
            orcValidator(orderObsGroup, i, parentGroup);
            //CTD
            ctdValidator(orderObsGroup, i, parentGroup);
            //CTI
            ctiValidator(orderObsGroup, i, parentGroup);
            //FT1
            ft1Validator(orderObsGroup, i, parentGroup);
        }
    }

    private static void obrValidator(Group orderOBSGroup, int orderObsNo, String parentGroup) throws HL7Exception {
        var obr = (Segment) orderOBSGroup.get("OBR");
        String obr4Value = obr.getField(4, 0).encode();
        if (StringUtils.isEmpty(obr4Value)) {
            throw new HL7Exception(REQ_FLD_MISSING + parentGroup + ORDER_OBSERVATION + "(" + orderObsNo + ")/OBR/UniversalServiceID");
        }
    }

    /**
     * Optional segment
     */
    private static void orcValidator(Group orderObsGroup, int orderObsNo, String parentGroup) throws HL7Exception {
        var orc = (Segment) orderObsGroup.get("ORC");
        if (!orc.isEmpty()) {
            String orc1Value = orc.getField(1, 0).encode();
            if (StringUtils.isEmpty(orc1Value)) {
                throw new HL7Exception(REQ_FLD_MISSING + parentGroup + ORDER_OBSERVATION + "(" + orderObsNo + ")/ORC/OrderControl");
            }
        }
    }

    /**
     * Optional segment
     */
    private static void ctdValidator(Group orderObsGroup, int orderObsNo, String parentGroup) throws HL7Exception {
        var ctd = (Segment) orderObsGroup.get("CTD");
        if (!ctd.isEmpty()) {
            String ctd1Value = ctd.getField(1, 0).encode();
            if (StringUtils.isEmpty(ctd1Value)) {
                throw new HL7Exception(REQ_FLD_MISSING + parentGroup + ORDER_OBSERVATION + "(" + orderObsNo + ")/CTD/ContactRole");
            }
        }
    }

    /**
     * Optional segment
     */
    private static void ctiValidator(Group orderObsGroup, int orderObsNo, String parentGroup) throws HL7Exception {
        var cti = (Segment) orderObsGroup.get("CTI");
        if (!cti.isEmpty()) {
            String cti1Value = cti.getField(1, 0).encode();
            if (StringUtils.isEmpty(cti1Value)) {
                throw new HL7Exception(REQ_FLD_MISSING + parentGroup + ORDER_OBSERVATION + "(" + orderObsNo + ")/CTI/SponsorStudyID");
            }
        }
    }

    /**
     * Optional segment
     */
    private static void ft1Validator(Group orderObsGroup, int orderObsNo, String parentGroup) throws HL7Exception {
        var ft1 = (Segment) orderObsGroup.get("FT1");
        if (!ft1.isEmpty()) {
            String ft1Value4 = ft1.getField(4, 0).encode();
            if (StringUtils.isEmpty(ft1Value4)) {
                throw new HL7Exception(REQ_FLD_MISSING + parentGroup + ORDER_OBSERVATION + "(" + orderObsNo + ")/FT1/TransactionDate");
            }
            String ft1Value6 = ft1.getField(6, 0).encode();
            if (StringUtils.isEmpty(ft1Value6)) {
                throw new HL7Exception(REQ_FLD_MISSING + parentGroup + ORDER_OBSERVATION + "(" + orderObsNo + ")/FT1/TransactionType");
            }
            String ft1Value7 = ft1.getField(7, 0).encode();
            if (StringUtils.isEmpty(ft1Value7)) {
                throw new HL7Exception(REQ_FLD_MISSING + parentGroup + ORDER_OBSERVATION + "(" + orderObsNo + ")/FT1/TransactionCode");
            }
        }
    }

    private static void obxValidator(Group orderObsGroup, int orderObsNo, String parentGroup) throws HL7Exception {
        var obsCount = orderObsGroup.getAll(OBSERVATION).length;
        for (int i = 0; i < obsCount; i++) {
            var obsGrup = (Group) orderObsGroup.get(OBSERVATION, i);
            var obx = (Segment) obsGrup.get("OBX");
            //OBX - Observation Identifier (3-0)
            String obxIdentifier = obx.getField(3, 0).encode();
            if (StringUtils.isEmpty(obxIdentifier)) {
                throw new HL7Exception(REQ_FLD_MISSING + parentGroup + ORDER_OBSERVATION + "(" + orderObsNo + ")/" + OBSERVATION + "(" + i + ")/OBX/ObservationIdentifier");
            }
            //OBX - ObservationResultStatus (11.0)
            String obsResultStatus = obx.getField(11, 0).encode();
            if (StringUtils.isEmpty(obsResultStatus)) {
                throw new HL7Exception(REQ_FLD_MISSING + parentGroup + ORDER_OBSERVATION + "(" + orderObsNo + ")/" + OBSERVATION + "(" + i + ")/OBX/ObservationResultStatus");
            }
        }
    }

    private static void spmValidator(Group orderObsGroup, int orderObsNo, String parentGroup) throws HL7Exception {
        var spmCount = orderObsGroup.getAll(SPECIMEN).length;
        for (int i = 0; i < spmCount; i++) {
            var spmGrup = (Group) orderObsGroup.get(SPECIMEN, i);
            var spm = (Segment) spmGrup.get("SPM");
            //SPM - Specimen Type-4
            String specimenType = spm.getField(4, 0).encode();
            if (StringUtils.isEmpty(specimenType)) {
                throw new HL7Exception(REQ_FLD_MISSING + parentGroup + ORDER_OBSERVATION + "(" + orderObsNo + ")/" + SPECIMEN + "(" + i + ")/SPM/SpecimenType");
            }
        }
    }
}