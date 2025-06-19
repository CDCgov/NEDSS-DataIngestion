package gov.cdc.dataingestion.hl7.helper.unitTest.helper;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v231.segment.PID;
import gov.cdc.dataingestion.hl7.helper.HL7Helper;
import gov.cdc.dataingestion.hl7.helper.helper.Mapping231To251Helper;
import gov.cdc.dataingestion.hl7.helper.integration.exception.DiHL7Exception;
import gov.cdc.dataingestion.hl7.helper.model.HL7ParsedMessage;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.observation.ObservationResult;
import gov.cdc.dataingestion.hl7.helper.model.hl7.message_data_type.Hd;
import gov.cdc.dataingestion.hl7.helper.model.hl7.message_data_type.Sad;
import gov.cdc.dataingestion.hl7.helper.model.hl7.message_data_type.Ts;
import gov.cdc.dataingestion.hl7.helper.model.hl7.message_data_type.Xad;
import gov.cdc.dataingestion.hl7.helper.model.hl7.message_type.OruR1;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static gov.cdc.dataingestion.hl7.helper.unitTest.Hl7TestData.randomGenerated231WithDataInAllFieldV2;
import static org.junit.jupiter.api.Assertions.*;

class Mapping231To251HelperTest {
    static HL7Helper hl7Helper = new HL7Helper();
    Mapping231To251Helper target = new Mapping231To251Helper();

    static HL7ParsedMessage testObject;
    static ca.uhn.hl7v2.model.v231.message.ORU_R01 oruR01Hapi;
    @BeforeAll
    static void setUp() throws DiHL7Exception {
        testObject = hl7Helper.hl7StringParser(randomGenerated231WithDataInAllFieldV2);
        oruR01Hapi =hl7Helper.hl7StringParser231(randomGenerated231WithDataInAllFieldV2);
    }

    @Test
    void mapMsh_TestElse() throws DiHL7Exception {
        var oru = (OruR1) testObject.getParsedMessage();
        oru.getMessageHeader().setMessageProfileIdentifier(null);
        var result = target.mapMsh(null, oru.getMessageHeader());
        assertEquals("ISO",result.getMessageProfileIdentifier().get(0).getUniversalIdType());
    }

    @Test
    void mapPid_NullScenario() throws HL7Exception {
        var oru = (OruR1) testObject.getParsedMessage();
        PID pid = oruR01Hapi.getPIDPD1NK1NTEPV1PV2ORCOBRNTEOBXNTECTIAll().get(0).getPIDPD1NK1NTEPV1PV2().getPID();

        oru.getPatientResult().get(0).getPatient().getPatientIdentification().setPatientIdentifierList(null);
        oru.getPatientResult().get(0).getPatient().getPatientIdentification().setPatientName(null);
        var exception = Assertions.assertThrows(Exception.class, () -> {
            target.mapPid(pid, oru.getPatientResult().get(0).getPatient().getPatientIdentification());
        });
        assertNotNull(exception);

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

        var result = target.mapXad(xad, xad);

        assertEquals(sad,result.getStreetAddress());
        assertEquals("test",result.getOtherDesignation());
        assertEquals("test",result.getCity());
        assertEquals("test",result.getState());
        assertEquals("test",result.getZip());
        assertEquals("test",result.getCountry());
        assertEquals("test",result.getAddressType());
        assertEquals("test",result.getOtherGeographic());
        assertEquals("test",result.getCountyCode());
        assertEquals("test",result.getCensusTract());
        assertEquals("test",result.getAddressRepresentationCode());
        assertEquals(ts,result.getEffectiveDate());
        assertEquals(ts,result.getExpirationDate());
    }


    @Test
    void testMapAssignedFacility_AllFieldsNull() {
        // Arrange
        Hd out = new Hd();
        Hd inAssignAuthority = new Hd();
        inAssignAuthority.setUniversalId("universalId");
        inAssignAuthority.setUniversalIdType("universalIdType");
        inAssignAuthority.setNameSpaceId("nameSpaceId");

        // Act
        Hd result = target.mapAssignedFacility(out, inAssignAuthority);

        // Assert
        assertNotNull(result);
        assertEquals("universalId", result.getUniversalId());
        assertEquals("universalIdType", result.getUniversalIdType());
        assertEquals("nameSpaceId", result.getNameSpaceId());
    }

    @Test
    void testMapAssignedFacility_UniversalIdNotEmpty() {
        // Arrange
        Hd out = new Hd();
        out.setUniversalId("existingUniversalId");
        Hd inAssignAuthority = new Hd();
        inAssignAuthority.setUniversalId("universalId");

        // Act
        Hd result = target.mapAssignedFacility(out, inAssignAuthority);

        // Assert
        assertNotNull(result);
        assertEquals("existingUniversalId", result.getUniversalId());
    }

    @Test
    void testMapAssignedFacility_UniversalIdTypeNotEmpty() {
        // Arrange
        Hd out = new Hd();
        out.setUniversalIdType("existingUniversalIdType");
        Hd inAssignAuthority = new Hd();
        inAssignAuthority.setUniversalIdType("universalIdType");

        // Act
        Hd result = target.mapAssignedFacility(out, inAssignAuthority);

        // Assert
        assertNotNull(result);
        assertEquals("existingUniversalIdType", result.getUniversalIdType());
    }

    @Test
    void testMapAssignedFacility_NameSpaceIdNotEmpty() {
        // Arrange
        Hd out = new Hd();
        out.setNameSpaceId("existingNameSpaceId");
        Hd inAssignAuthority = new Hd();
        inAssignAuthority.setNameSpaceId("nameSpaceId");

        // Act
        Hd result = target.mapAssignedFacility(out, inAssignAuthority);

        // Assert
        assertNotNull(result);
        assertEquals("existingNameSpaceId", result.getNameSpaceId());
    }

    @Test
    void testMapAssignedFacility_EmptyFields() {
        // Arrange
        Hd out = new Hd();
        out.setUniversalId("");
        out.setUniversalIdType("");
        out.setNameSpaceId("");
        Hd inAssignAuthority = new Hd();
        inAssignAuthority.setUniversalId("universalId");
        inAssignAuthority.setUniversalIdType("universalIdType");
        inAssignAuthority.setNameSpaceId("nameSpaceId");

        // Act
        Hd result = target.mapAssignedFacility(out, inAssignAuthority);

        // Assert
        assertNotNull(result);
        assertEquals("universalId", result.getUniversalId());
        assertEquals("universalIdType", result.getUniversalIdType());
        assertEquals("nameSpaceId", result.getNameSpaceId());
    }

    @SuppressWarnings("java:S5976")
    @Test
    void testMapObservationResultAddressInfoCheck_AllConditionsTrue() {
        // Arrange
        ObservationResult obxIn = new ObservationResult();
        Xad address = new Xad();
        Sad streetAddress = new Sad();
        streetAddress.setStreetName(null);
        streetAddress.setStreetMailingAddress(null);
        address.setStreetAddress(streetAddress);
        obxIn.setPerformingOrganizationAddress(address);

        // Act
        boolean result = target.mapObservationResultAddressInfoCheck(obxIn);

        // Assert
        assertTrue(result);
    }



    @SuppressWarnings("java:S5976")
    @Test
    void testMapObservationResultAddressInfoCheck_StreetNameEmpty() {
        // Arrange
        ObservationResult obxIn = new ObservationResult();
        Xad address = new Xad();
        Sad streetAddress = new Sad();
        streetAddress.setStreetName("");
        streetAddress.setStreetMailingAddress(null);
        address.setStreetAddress(streetAddress);
        obxIn.setPerformingOrganizationAddress(address);

        // Act
        boolean result = target.mapObservationResultAddressInfoCheck(obxIn);

        // Assert
        assertTrue(result);
    }

    @SuppressWarnings("java:S5976")
    @Test
    void testMapObservationResultAddressInfoCheck_StreetMailingAddressEmpty() {
        // Arrange
        ObservationResult obxIn = new ObservationResult();
        Xad address = new Xad();
        Sad streetAddress = new Sad();
        streetAddress.setStreetName(null);
        streetAddress.setStreetMailingAddress("");
        address.setStreetAddress(streetAddress);
        obxIn.setPerformingOrganizationAddress(address);

        // Act
        boolean result = target.mapObservationResultAddressInfoCheck(obxIn);

        // Assert
        assertTrue(result);
    }


    @Test
    void testMapObservationResultAddressInfoCheck_StreetAddressNull() {
        // Arrange
        ObservationResult obxIn = new ObservationResult();
        Xad address = new Xad();
        address.setStreetAddress(null);
        obxIn.setPerformingOrganizationAddress(address);

        // Act
        boolean result = target.mapObservationResultAddressInfoCheck(obxIn);

        // Assert
        assertFalse(result);
    }

    @Test
    void testMapObservationResultCityInfoCheck_PerformingOrganizationAddressNull() {
        // Arrange
        ObservationResult obxIn = new ObservationResult();
        obxIn.setPerformingOrganizationAddress(null);

        // Act
        boolean result = target.mapObservationResultCityInfoCheck(obxIn);

        // Assert
        assertFalse(result);
    }

    @Test
    void testMapObservationResultCityInfoCheck_CityNull() {
        // Arrange
        ObservationResult obxIn = new ObservationResult();
        Xad address = new Xad();
        address.setCity(null);
        obxIn.setPerformingOrganizationAddress(address);

        // Act
        boolean result = target.mapObservationResultCityInfoCheck(obxIn);

        // Assert
        assertTrue(result);
    }

    @Test
    void testMapObservationResultCityInfoCheck_CityEmpty() {
        // Arrange
        ObservationResult obxIn = new ObservationResult();
        Xad address = new Xad();
        address.setCity("");
        obxIn.setPerformingOrganizationAddress(address);

        // Act
        boolean result = target.mapObservationResultCityInfoCheck(obxIn);

        // Assert
        assertTrue(result);
    }

    @Test
    void testMapObservationResultCityInfoCheck_CityNotEmpty() {
        // Arrange
        ObservationResult obxIn = new ObservationResult();
        Xad address = new Xad();
        address.setCity("Some City");
        obxIn.setPerformingOrganizationAddress(address);

        // Act
        boolean result = target.mapObservationResultCityInfoCheck(obxIn);

        // Assert
        assertFalse(result);
    }

    @Test
    void testMapObservationResultStateInfoCheck_XadNull() {
        ObservationResult obxIn = new ObservationResult();
        obxIn.setPerformingOrganizationAddress(null);

        boolean result = target.mapObservationResultStateInfoCheck(obxIn);

        assertFalse(result);
    }

    @Test
    void testMapObservationResultStateInfoCheck_StateNull() {
        ObservationResult obxIn = new ObservationResult();
        Xad address = new Xad();
        address.setState(null);
        obxIn.setPerformingOrganizationAddress(address);

        boolean result = target.mapObservationResultStateInfoCheck(obxIn);

        assertTrue(result);
    }

    @Test
    void testMapObservationResultStateInfoCheck_StateEmpty() {
        ObservationResult obxIn = new ObservationResult();
        Xad address = new Xad();
        address.setState("");
        obxIn.setPerformingOrganizationAddress(address);

        boolean result = target.mapObservationResultStateInfoCheck(obxIn);

        assertTrue(result);
    }

    @Test
    void testMapObservationResultStateInfoCheck_StateNotEmpty() {
        ObservationResult obxIn = new ObservationResult();
        Xad address = new Xad();
        address.setState("Some State");
        obxIn.setPerformingOrganizationAddress(address);

        boolean result = target.mapObservationResultStateInfoCheck(obxIn);

        assertFalse(result);
    }

    // Tests for mapObservationResultCountryInfoCheck
    @Test
    void testMapObservationResultCountryInfoCheck_XadNull() {
        ObservationResult obxIn = new ObservationResult();
        obxIn.setPerformingOrganizationAddress(null);

        boolean result = target.mapObservationResultCountryInfoCheck(obxIn);

        assertFalse(result);
    }

    @Test
    void testMapObservationResultCountryInfoCheck_CountryNull() {
        ObservationResult obxIn = new ObservationResult();
        Xad address = new Xad();
        address.setCountry(null);
        obxIn.setPerformingOrganizationAddress(address);

        boolean result = target.mapObservationResultCountryInfoCheck(obxIn);

        assertTrue(result);
    }

    @Test
    void testMapObservationResultCountryInfoCheck_CountryEmpty() {
        ObservationResult obxIn = new ObservationResult();
        Xad address = new Xad();
        address.setCountry("");
        obxIn.setPerformingOrganizationAddress(address);

        boolean result = target.mapObservationResultCountryInfoCheck(obxIn);

        assertTrue(result);
    }

    @Test
    void testMapObservationResultCountryInfoCheck_CountryNotEmpty() {
        ObservationResult obxIn = new ObservationResult();
        Xad address = new Xad();
        address.setCountry("Some Country");
        obxIn.setPerformingOrganizationAddress(address);

        boolean result = target.mapObservationResultCountryInfoCheck(obxIn);

        assertFalse(result);
    }

    // Tests for mapObservationResultZipInfoCheck
    @Test
    void testMapObservationResultZipInfoCheck_XadNull() {
        ObservationResult obxIn = new ObservationResult();
        obxIn.setPerformingOrganizationAddress(null);

        boolean result = target.mapObservationResultZipInfoCheck(obxIn);

        assertFalse(result);
    }

    @Test
    void testMapObservationResultZipInfoCheck_ZipNull() {
        ObservationResult obxIn = new ObservationResult();
        Xad address = new Xad();
        address.setZip(null);
        obxIn.setPerformingOrganizationAddress(address);

        boolean result = target.mapObservationResultZipInfoCheck(obxIn);

        assertTrue(result);
    }

    @Test
    void testMapObservationResultZipInfoCheck_ZipEmpty() {
        ObservationResult obxIn = new ObservationResult();
        Xad address = new Xad();
        address.setZip("");
        obxIn.setPerformingOrganizationAddress(address);

        boolean result = target.mapObservationResultZipInfoCheck(obxIn);

        assertTrue(result);
    }

    @Test
    void testMapObservationResultZipInfoCheck_ZipNotEmpty() {
        ObservationResult obxIn = new ObservationResult();
        Xad address = new Xad();
        address.setZip("12345");
        obxIn.setPerformingOrganizationAddress(address);

        boolean result = target.mapObservationResultZipInfoCheck(obxIn);

        assertFalse(result);
    }

}
