package gov.cdc.dataingestion.nbs.converter;

import ca.uhn.hl7v2.HL7Exception;
import gov.cdc.dataingestion.exception.DuplicateHL7FileFoundException;
import gov.cdc.dataingestion.hl7.helper.integration.exception.DiHL7Exception;
import gov.cdc.dataingestion.hl7.helper.model.HL7ParsedMessage;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.observation.ObservationResult;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient.visit.PatientVisitAdditional;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.*;
import gov.cdc.dataingestion.nbs.converters.Hl7ToRhapsodysXmlConverter;
import gov.cdc.dataingestion.nbs.converters.RhapsodysXmlToHl7Converter;
import gov.cdc.dataingestion.nbs.jaxb.*;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Hl7ToRhapsodysXmlConverterTest {
    Hl7ToRhapsodysXmlConverter target = new Hl7ToRhapsodysXmlConverter();

    @Test
    void convertHL7ToXMLTest() throws DiHL7Exception, JAXBException, IOException {
        String rawId = "whatever";
        String hl7Message = TestData.randomGenerated251WithDataInAllFieldV1;

        var result = target.convert(rawId, hl7Message);

        Assertions.assertTrue(result.contains(rawId));
    }

    @Test
    void buildHL7LabReportTypeAllMissingConditional() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var parentClass = new Hl7ToRhapsodysXmlConverter();
        HL7ParsedMessage model = new HL7ParsedMessage();
        model.setParsedMessage("test");
        Method privateMethod = Hl7ToRhapsodysXmlConverter.class.getDeclaredMethod("buildHL7LabReportType", HL7ParsedMessage.class);
        privateMethod.setAccessible(true);
        var result = (HL7LabReportType) privateMethod.invoke(parentClass, model);
        Assertions.assertNull(result.getHL7ContinuationPointer());
    }

    @Test
    void buildHL7CNETypeAllMissingConditional() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var parentClass = new Hl7ToRhapsodysXmlConverter();
        Cne model = new Cne();
        model.setIdentifier("test");
        model.setText("test");
        model.setNameOfAlternateCodingSystem("test");
        model.setAlternateIdentifier("test");
        model.setAlternateText("test");
        model.setNameOfAlternateCodingSystem("test");
        model.setCodingSystemVersionId("test");
        model.setAlternateCodingSystemVersionId("test");
        model.setOriginalText("test");
        String expectedMeg = "test";
        Method privateMethod = Hl7ToRhapsodysXmlConverter.class.getDeclaredMethod("buildHL7CNEType", Cne.class);
        privateMethod.setAccessible(true);
        var result = (HL7CNEType) privateMethod.invoke(parentClass, model);
        Assertions.assertEquals(expectedMeg, result.getHL7Identifier());
        Assertions.assertEquals(expectedMeg, result.getHL7Text());
        Assertions.assertEquals(expectedMeg, result.getHL7NameofCodingSystem());
        Assertions.assertEquals(expectedMeg, result.getHL7AlternateIdentifier());
        Assertions.assertEquals(expectedMeg, result.getHL7AlternateText());
        Assertions.assertEquals(expectedMeg, result.getHL7NameofAlternateCodingSystem());
        Assertions.assertEquals(expectedMeg, result.getHL7CodingSystemVersionID());
        Assertions.assertEquals(expectedMeg, result.getHL7AlternateCodingSystemVersionID());
        Assertions.assertEquals(expectedMeg, result.getHL7OriginalText());
    }

    @Test
    void buildHL7OBXTypeAllMissingConditional() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var parentClass = new Hl7ToRhapsodysXmlConverter();
        ObservationResult model = new ObservationResult();
        model.setValueType("SN");
        List<String> listStr = new ArrayList<>();
        listStr.add("SN");
        model.setObservationValue(listStr);
        Method privateMethod = Hl7ToRhapsodysXmlConverter.class.getDeclaredMethod("buildHL7OBXType", ObservationResult.class);
        privateMethod.setAccessible(true);
        var result = (HL7OBXType) privateMethod.invoke(parentClass, model);
        Assertions.assertNull(result.getSetIDOBX().getHL7SequenceID());
        Assertions.assertEquals("SN",result.getValueType());
        Assertions.assertNotNull(result.getObservationIdentifier());
        Assertions.assertNull(result.getObservationSubID());
        Assertions.assertNotNull(result.getObservationValue());
        Assertions.assertNull(result.getUnits());
        Assertions.assertNull(result.getReferencesRange());
        Assertions.assertNotNull(result.getAbnormalFlags());
        Assertions.assertNotNull(result.getProbability());
        Assertions.assertNotNull(result.getNatureOfAbnormalTest());
        Assertions.assertEquals("",result.getObservationResultStatus());
        Assertions.assertNull(result.getUserDefinedAccessChecks());
        Assertions.assertNull(result.getDateTimeOftheObservation());
        Assertions.assertNull(result.getProducersReference());
        Assertions.assertNotNull(result.getResponsibleObserver());
        Assertions.assertNotNull(result.getObservationMethod());
        Assertions.assertNotNull(result.getEquipmentInstanceIdentifier());
        Assertions.assertNull(result.getDateTimeOftheAnalysis());
        Assertions.assertNull(result.getReservedforHarmonizationWithV261());
        Assertions.assertNull(result.getReservedForHarmonizationwithV262());
        Assertions.assertNull(result.getReservedForHarmonizationWithV263());
        Assertions.assertNull(result.getPerformingOrganizationName());
        Assertions.assertNull(result.getPerformingOrganizationAddress());
        Assertions.assertNull(result.getPerformingOrganizationMedicalDirector());

    }

    @Test
    void buildHL7PIV2TypeAllMissingConditional() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var parentClass = new Hl7ToRhapsodysXmlConverter();
        PatientVisitAdditional model = new PatientVisitAdditional();
        model.setPriorPendingLocation(new Pl());
        model.setAccommodationCode(new Ce());
        model.setAdmitReason(new Ce());
        model.setTransferReason(new Ce());
        List<String> patientValuables = new ArrayList<>();
        patientValuables.add("20230615");
        model.setPatientValuables(patientValuables);
        model.setPatientValuablesLocation("20230615");
        List<String> visitUserCode = new ArrayList<>();
        visitUserCode.add("20230615");
        model.setVisitUserCode(visitUserCode);
        model.setExpectedAdmitDateTime(new Ts());
        model.setExpectedDischargeDateTime(new Ts());
        model.setEstimateLengthOfInpatientDay("20230615");
        model.setActualLengthOfInpatientDay("20230615");
        model.setVisitDescription("20230615");
        List<Xcn> ref = new ArrayList<>();
        ref.add(new Xcn());
        model.setReferralSourceCode(ref);
        model.setPreviousServiceDate("20230615");
        model.setEmploymentIllnessRelatedIndicator("20230615");
        model.setPurgeStatusCode("20230615");
        model.setPurgeStatusDate("20230615");
        model.setSpecialProgramCode("20230615");
        model.setRetentionIndicator("20230615");
        model.setExpectedNumberOfInsurancePlans("20230615");
        model.setVisitPublicityCode("20230615");
        model.setVisitProtectionIndicator("20230615");
        List<Xcn> refSrc = new ArrayList<>();
        refSrc.add(new Xcn());
        model.setReferralSourceCode(refSrc);
        model.setPatientStatusCode("20230615");
        model.setVisitPriorityCode("20230615");
        model.setPreviousServiceDate("20230615");
        model.setExpectedDischargeDisposition("20230615");
        model.setSignatureOnFileDate("20230615");
        model.setFirstSimilarIllnessDate("20230615");
        model.setRecurringServiceCode("20230615");
        model.setBillingMediaCode("20230615");
        model.setExpectedSurgeryDateTime(new Ts());
        model.setMilitaryPartnershipCode("20230615");
        model.setMilitaryNonAvailCode("20230615");
        model.setNewbornBabyIndicator("20230615");
        model.setBabyDetainedIndicator("20230615");
        model.setModeOfArrivalCode(new Ce());
        List<Ce> drug = new ArrayList<>();
        drug.add(new Ce());
        model.setRecreationalDrugUseCode(drug);
        model.setAdmissionLevelOfCareCode(new Ce());
        List<Ce> preCau = new ArrayList<>();
        preCau.add(new Ce());
        model.setPrecautionCode(preCau);
        model.setPatientConditionCode(new Ce());
        model.setLivingWillCode("20230615");
        model.setOrganDonorCode("20230615");
        List<Ce> advance = new ArrayList<>();
        advance.add(new Ce());
        model.setAdvanceDirectiveCode(advance);
        model.setPatientStatusEffectiveDate("20230615");
        model.setExpectedLoaReturnDateTime(new Ts());
        model.setExpectedPreAdmissionTestingDateTime(new Ts());
        List<String> notify = new ArrayList<>();
        notify.add("20230615");
        model.setNotifyClergyCode(notify);

        List<Xon> clinicOrganizationName = new ArrayList<>();
        clinicOrganizationName.add(new Xon());
        model.setClinicOrganizationName(clinicOrganizationName);


        Method privateMethod = Hl7ToRhapsodysXmlConverter.class.getDeclaredMethod("buildHL7PIV2Type", PatientVisitAdditional.class);
        privateMethod.setAccessible(true);
        var result = (HL7PIV2Type) privateMethod.invoke(parentClass, model);
        Assertions.assertNotNull(result.getPriorPendingLocation());
        Assertions.assertNotNull(result.getAccommodationCode());
        Assertions.assertNotNull(result.getAdmitReason());
        Assertions.assertNotNull(result.getTransferReason());
        Assertions.assertEquals("20230615",result.getPatientValuables().get(0));
        Assertions.assertEquals("20230615",result.getPatientValuablesLocation());
        Assertions.assertEquals("20230615",result.getVisitUserCode().get(0));
        Assertions.assertNull(result.getExpectedAdmitDateTime());
        Assertions.assertNull(result.getExpectedDischargeDateTime());
        Assertions.assertEquals(1, result.getEstimatedLengthOfInpatientStay().getHL7Numeric().signum());
        Assertions.assertEquals(1, result.getActualLengthOfInpatientStay().getHL7Numeric().signum());
        Assertions.assertEquals("20230615", result.getEmploymentIllnessRelatedIndicator());
        Assertions.assertEquals("20230615", result.getPurgeStatusCode());
        Assertions.assertNotNull( result.getPurgeStatusDate());
        Assertions.assertEquals("20230615", result.getSpecialProgramCode());
        Assertions.assertEquals("20230615", result.getRetentionIndicator());
        Assertions.assertNotNull(result.getExpectedNumberOfInsurancePlans());
        Assertions.assertNull(result.getVisitPublicityCode());
    }

    @Test
    void buildHL7JCCTypeAllMissingConditional() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var parentClass = new Hl7ToRhapsodysXmlConverter();
        Jcc model = new Jcc();
        model.setJobCode("test");
        model.setJobClass("test");
        model.setJobDescriptionText("test");
        Method privateMethod = Hl7ToRhapsodysXmlConverter.class.getDeclaredMethod("buildHL7JCCType", Jcc.class);
        privateMethod.setAccessible(true);
        var result = (HL7JCCType) privateMethod.invoke(parentClass, model);
        Assertions.assertEquals("test", result.getHL7JobCode());
        Assertions.assertEquals("test", result.getHL7JobClass());
        Assertions.assertEquals("test", result.getHL7JobDescriptionText().getHL7String());

    }

    @Test
    void buildHL7CXTypeAllMissingConditional() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var parentClass = new Hl7ToRhapsodysXmlConverter();
        Cx model = new Cx();
        Cwe cwe = new Cwe();
        cwe.setText("20230615");
        model.setIdNumber("20230615");
        model.setExpirationDate("20230615");
        model.setAssignJurisdiction(cwe);
        model.setAssignAgentOrDept(cwe);
        Method privateMethod = Hl7ToRhapsodysXmlConverter.class.getDeclaredMethod("buildHL7CXType", Cx.class);
        privateMethod.setAccessible(true);
        var result = (HL7CXType) privateMethod.invoke(parentClass, model);
        Assertions.assertEquals("20230615", result.getHL7IDNumber ());


    }

}
