package gov.cdc.dataingestion.report.integration.service.convert;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v251.datatype.CE;
import ca.uhn.hl7v2.model.v251.message.ORU_R01;
import ca.uhn.hl7v2.model.v251.segment.MSH;
import ca.uhn.hl7v2.model.v251.segment.SFT;
import ca.uhn.hl7v2.model.v251.segment.PID;
import ca.uhn.hl7v2.model.v251.segment.PV1;
import ca.uhn.hl7v2.model.v251.segment.OBR;
import ca.uhn.hl7v2.model.v251.segment.OBX;
import ca.uhn.hl7v2.parser.CanonicalModelClassFactory;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class ConvertCsvToHl7Service  implements  IConvertCsvToHl7Service {

    /**
     * Converts csv to hl7.
     * @param input csv format
     * @return fhir format
     */
    @Override
    public String execute(final String input) {
        var mappedCsvValues  = this.convertCsvToMap(input);
        return this.buildHL7Message(mappedCsvValues);
    }

    /**
     * Builds hl7 message.
     * @param mappedCsvValues mapped csv values.
     * @return hl7 message string.
     */
    private String buildHL7Message(
            final Map<String, String> mappedCsvValues)  {
        ORU_R01 message = new ORU_R01();

        // Set the message header (MSH) fields
        MSH mshSegment = message.getMSH();
        try {
            mshSegment.getFieldSeparator().setValue("|");

        mshSegment.getEncodingCharacters().setValue("^~\\&");
        mshSegment.getSendingApplication().getNamespaceID().setValue(
                "CDC PRIME - Atlanta, Georgia (Dekalb)"
                        +
                        "^2.16.840.1.114222.4.1.237821^ISO");
        mshSegment.getSendingFacility().getNamespaceID()
                .setValue("CDC PRIME^11D2030855^CLIA");
        mshSegment.getReceivingApplication().getNamespaceID()
                .setValue("NH_ELR^2.16.840.1.114222.4.3.2.2.3.600.4^ISO");
        mshSegment.getReceivingFacility().getNamespaceID()
                .setValue("NH_DHHS^2.16.840.1.114222.4.1.3669^ISO");

        mshSegment.getMessageType().getMsg1_MessageCode().setValue("ORU");
        mshSegment.getMessageType().getMsg2_TriggerEvent().setValue("R01");
        mshSegment.getMessageControlID().setValue("336131");
        mshSegment.getProcessingID().getPt1_ProcessingID().setValue("P");
        mshSegment.getVersionID().getVersionID().setValue("2.5.1");
        mshSegment.getAcceptAcknowledgmentType().setValue("NE");
        mshSegment.getApplicationAcknowledgmentType().setValue("NE");
        mshSegment.getCountryCode().setValue("USA");

        // Create and populate the PID segment
        PID pid = message.getPATIENT_RESULT().getPATIENT().getPID();
        pid.getPatientName(0).getGivenName()
                .setValue(mappedCsvValues.get("Patient_last_name"));
        pid.getPatientIdentifierList(0).getIdentifierTypeCode()
                .setValue("MR");

        // Create and populate the PV1 segment
        PV1 pv1 = message.getPATIENT_RESULT().getPATIENT().getVISIT().getPV1();
        pv1.getPatientClass().setValue("O");
        pv1.getAssignedPatientLocation().getFacility()
                .getNamespaceID().setValue("MyHospital");

        // Set the observation request (OBR) fields
        OBR obrSegment = message.getPATIENT_RESULT()
                .getORDER_OBSERVATION().getOBR();
        obrSegment.getSetIDOBR().setValue("1");
        obrSegment.getUniversalServiceIdentifier()
                .getIdentifier().setValue("12345");
        obrSegment.getUniversalServiceIdentifier()
                .getText().setValue("Test Observation");

        // Set the observation result (OBX) fields
        OBX obxSegment = message.getPATIENT_RESULT()
                .getORDER_OBSERVATION().getOBSERVATION().getOBX();
        obxSegment.getSetIDOBX().setValue("1");
        obxSegment.getValueType().setValue("CWE");
        obxSegment.getObservationIdentifier().getIdentifier()
                .setValue("94558-4");
        obxSegment.getObservationSubID()
                .setValue("SARS-CoV-2 (COVID-19) Ag [Presence]"
                        +
                        " in Respiratory specimen by Rapid immunoassay");
        obxSegment.getObservationResultStatus().setValue("LN");

        CE valueCode = new CE(message);
        valueCode.getIdentifier().setValue("12345");
        valueCode.getText().setValue("Positive");
        obxSegment.getObservationValue(0).setData(valueCode);


        // Set the software vendor information
        SFT sft = message.getSFT();
        sft.getSoftwareVendorOrganization().getOrganizationName()
                .setValue("Centers for Disease Control and Prevention");
        sft.getSoftwareCertifiedVersionOrReleaseNumber()
                .setValue("0.2-SNAPSHOT");
        sft.getSoftwareProductName().setValue("PRIME ReportStream");
        sft.getSoftwareProductInformation().setValue("MyProductInformation");
        sft.getSoftwareBinaryID().setValue("0.2-SNAPSHOT");
        sft.getSoftwareInstallDate().getTs1_Time()
                .setValue("20230209000000+0000");

        // Convert HL7 message to string and print to console
        Parser parser = new PipeParser(
                new CanonicalModelClassFactory("2.3.1"));
        String hl7String = parser.encode(message);
        log.info("hl7String:{}", hl7String);
        return hl7String;
        } catch (HL7Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Convert csv tp map.
     * @param csvString csv formatted string.
     * @return mapped values.
     */
    public Map<String, String> convertCsvToMap(final String csvString) {
        Map<String, String> dataMap = new HashMap<>();

        String[] lines = csvString.split("\n");
        String[] headers = lines[0].split(",");
        String[] parts = lines[1].split(",");

        for (int i = 0; i < parts.length; i++) {
            dataMap.put(headers[i], parts[i]);
        }
        return dataMap;
    }


}
