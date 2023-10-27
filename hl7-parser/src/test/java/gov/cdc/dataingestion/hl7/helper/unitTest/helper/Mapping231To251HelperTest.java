package gov.cdc.dataingestion.hl7.helper.unitTest.helper;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v231.segment.PID;
import gov.cdc.dataingestion.hl7.helper.HL7Helper;
import gov.cdc.dataingestion.hl7.helper.helper.Mapping231To251Helper;
import gov.cdc.dataingestion.hl7.helper.integration.exception.DiHL7Exception;
import gov.cdc.dataingestion.hl7.helper.model.HL7ParsedMessage;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient.NextOfKin;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Sad;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Ts;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Xad;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageType.OruR1;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static gov.cdc.dataingestion.hl7.helper.unitTest.Hl7TestData.messageByRhapsody;
import static gov.cdc.dataingestion.hl7.helper.unitTest.Hl7TestData.randomGenerated231WithDataInAllFieldV2;

class Mapping231To251HelperTest {
    static HL7Helper hl7Helper = new HL7Helper();
    Mapping231To251Helper target = new Mapping231To251Helper();

    static HL7ParsedMessage testObject;
    static ca.uhn.hl7v2.model.v231.message.ORU_R01 oruR01Hapi;
    @BeforeAll
    public static void setUp() throws DiHL7Exception {
        testObject = hl7Helper.hl7StringParser(randomGenerated231WithDataInAllFieldV2);
        oruR01Hapi =hl7Helper.hl7StringParser231(randomGenerated231WithDataInAllFieldV2);
    }

    @Test
    void mapMsh_TestElse() throws DiHL7Exception {
        var oru = (OruR1) testObject.getParsedMessage();
        oru.getMessageHeader().setMessageProfileIdentifier(null);
        var result = target.MapMsh(null, oru.getMessageHeader());
        Assertions.assertTrue(result.getMessageProfileIdentifier().get(0).getUniversalIdType().equals("ISO"));
    }

    @Test
    void mapPid_NullScenario() throws HL7Exception {
        var oru = (OruR1) testObject.getParsedMessage();
        PID pid = oruR01Hapi.getPIDPD1NK1NTEPV1PV2ORCOBRNTEOBXNTECTIAll().get(0).getPIDPD1NK1NTEPV1PV2().getPID();

        oru.getPatientResult().get(0).getPatient().getPatientIdentification().setPatientIdentifierList(null);
        oru.getPatientResult().get(0).getPatient().getPatientIdentification().setPatientName(null);
        var exception = Assertions.assertThrows(Exception.class, () -> {
            target.MapPid(pid, oru.getPatientResult().get(0).getPatient().getPatientIdentification());
        });
        Assertions.assertNotNull(exception);

    }

    @Test
    void mapXad_Success() {
        Xad xad = new Xad();
        Sad sad = new Sad();
        Ts ts = new Ts();
        ts.setTime("test");
        ts.setDegreeOfPrecision("test");
        sad.setStreetMailingAddress("test");
        sad.setStreetName("test");
        sad.setDwellingNumber("test");
        xad.setStreetAddress(sad);
        xad.setOtherDesignation("test");
        xad.setCity("test");
        xad.setState("test");
        xad.setZip("test");
        xad.setCountry("test");
        xad.setAddressType("test");
        xad.setOtherGeographic("test");
        xad.setCountyCode("test");
        xad.setCensusTract("test");
        xad.setAddressRepresentationCode("test");
        xad.setEffectiveDate(ts);
        xad.setExpirationDate(ts);

        var result = target.MapXad(xad, xad);

        Assertions.assertEquals(sad,result.getStreetAddress());
        Assertions.assertEquals("test",result.getOtherDesignation());
        Assertions.assertEquals("test",result.getCity());
        Assertions.assertEquals("test",result.getState());
        Assertions.assertEquals("test",result.getZip());
        Assertions.assertEquals("test",result.getCountry());
        Assertions.assertEquals("test",result.getAddressType());
        Assertions.assertEquals("test",result.getOtherGeographic());
        Assertions.assertEquals("test",result.getCountyCode());
        Assertions.assertEquals("test",result.getCensusTract());
        Assertions.assertEquals("test",result.getAddressRepresentationCode());
        Assertions.assertEquals(ts,result.getEffectiveDate());
        Assertions.assertEquals(ts,result.getExpirationDate());
    }

}
