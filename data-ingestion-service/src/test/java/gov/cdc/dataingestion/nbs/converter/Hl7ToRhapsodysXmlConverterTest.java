package gov.cdc.dataingestion.nbs.converter;

import gov.cdc.dataingestion.hl7.helper.HL7Helper;
import gov.cdc.dataingestion.hl7.helper.integration.exception.DiHL7Exception;
import gov.cdc.dataingestion.hl7.helper.model.HL7ParsedMessage;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.CommonOrder;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.FinancialTransaction;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.ObservationRequest;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.observation.ObservationResult;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.timingQty.TimingQuantity;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.timingQty.TimingQuantityRelationship;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient.NextOfKin;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient.PatientIdentification;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient.visit.PatientVisit;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient.visit.PatientVisitAdditional;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.*;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageGroup.TimingQty;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageSegment.MessageHeader;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageType.OruR1;
import gov.cdc.dataingestion.nbs.converters.Hl7ToRhapsodysXmlConverter;
import gov.cdc.dataingestion.nbs.jaxb.*;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

class Hl7ToRhapsodysXmlConverterTest {
    Hl7ToRhapsodysXmlConverter target = new Hl7ToRhapsodysXmlConverter();
    HL7Helper hl7Helper = new HL7Helper();

    @Test
    void convertHL7ToXMLTest() throws DiHL7Exception, JAXBException, IOException {
        String rawId = "whatever";
        String hl7Message = TestData.randomGenerated251WithDataInAllFieldV1;

        var result = target.convert(rawId, target.parsedStringToHL7(hl7Message));

        Assertions.assertTrue(result.contains(rawId));
    }

    @Test
    void buildHL7LabReportTypeAllMissingConditional() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var parentClass = new Hl7ToRhapsodysXmlConverter();
        HL7ParsedMessage<OruR1> model = new HL7ParsedMessage();
        model.setParsedMessage(new OruR1());
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
    void buildHL7PIV2TypeAllMissingConditional_Coverage() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, DiHL7Exception {
        String hl7Message = TestData.randomGenerated251WithDataInAllFieldV1;
        HL7ParsedMessage hl7ParsedMsg = hl7Helper.hl7StringParser(hl7Message);
        OruR1 oru = (OruR1) hl7ParsedMsg.getParsedMessage();

        var parentClass = new Hl7ToRhapsodysXmlConverter();
        PatientVisitAdditional model = oru.getPatientResult().get(0).getPatient().getVisit().getPatientVisitAdditional();
        var expectedMessage = "20230615";
        List<Xon> xonList = new ArrayList<>();
        Xon xon = new Xon();
        xon.setCheckDigit(expectedMessage);
        xon.setCheckDigitScheme(expectedMessage);
        xonList.add(xon);
        model.setClinicOrganizationName(xonList);
        Method privateMethod = Hl7ToRhapsodysXmlConverter.class.getDeclaredMethod("buildHL7PIV2Type", PatientVisitAdditional.class);
        privateMethod.setAccessible(true);
        var result = (HL7PIV2Type) privateMethod.invoke(parentClass, model);
        Assertions.assertNotNull(result.getPriorPendingLocation());
        Assertions.assertNull(result.getPreviousServiceDate());
        Assertions.assertNotNull(result.getClinicOrganizationName());
        Assertions.assertEquals(new BigInteger(expectedMessage), result.getClinicOrganizationName().getHL7CheckDigit().getHL7Numeric());
        Assertions.assertEquals(expectedMessage, result.getClinicOrganizationName().getHL7CheckDigitScheme());
        Assertions.assertNull(result.getPatientStatusCode());
        Assertions.assertNull(result.getVisitPriorityCode());
        Assertions.assertNull(result.getPreviousTreatmentDate());
        Assertions.assertNull(result.getExpectedDischargeDisposition());
        Assertions.assertNull(result.getSignatureOnFileDate());
        Assertions.assertNull(result.getFirstSimilarIllnessDate());
        Assertions.assertNull(result.getPatientChargeAdjustmentCode());
        Assertions.assertNull(result.getRecurringServiceCode());
        Assertions.assertNull(result.getBillingMediaCode());
        Assertions.assertNull(result.getExpectedSurgeryDateAndTime());
        Assertions.assertNull(result.getMilitaryPartnershipCode());
        buildHL7PIV2TypeAllMissingConditional_CoverageAssertionHelper(result);
    }
    private void buildHL7PIV2TypeAllMissingConditional_CoverageAssertionHelper(HL7PIV2Type result) {
        Assertions.assertNull(result.getMilitaryNonAvailabilityCode());
        Assertions.assertNull(result.getNewbornBabyIndicator());
        Assertions.assertNull(result.getBabyDetainedIndicator());
        Assertions.assertNull(result.getRecreationalDrugUseCode());
        Assertions.assertNotNull(result.getAdmissionLevelOfCareCode());
        Assertions.assertNull(result.getPrecautionCode());
        Assertions.assertNotNull(result.getPatientConditionCode());
        Assertions.assertNull(result.getLivingWillCode());
        Assertions.assertNull(result.getOrganDonorCode());
        Assertions.assertNull(result.getPatientStatusEffectiveDate());
        Assertions.assertNull(result.getExpectedLOAReturnDateTime());
        Assertions.assertNull(result.getExpectedPreadmissionTestingDateTime());
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

    @Test
    void buildHL7TIMINGQuantiyTypeAllMissingConditional() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var parentClass = new Hl7ToRhapsodysXmlConverter();
        List<TimingQty> list = new ArrayList<>();
        TimingQty model = new TimingQty();
        TimingQuantity tq = new TimingQuantity();

        model.setTimeQuantity(tq);

        List<TimingQuantityRelationship> lstTiming = new ArrayList<>();
        TimingQuantityRelationship tqr = new TimingQuantityRelationship();
        lstTiming.add(tqr);
        model.setTimeQuantityRelationship(lstTiming);
        list.add(model);
        Method privateMethod = Hl7ToRhapsodysXmlConverter.class.getDeclaredMethod("buildHL7TIMINGQuantiyType", List.class);
        privateMethod.setAccessible(true);
        var result = (HL7TIMINGQuantiyType) privateMethod.invoke(parentClass, list);
        Assertions.assertNotNull( result);
        Assertions.assertNotNull(result.getTIMINGQTY());
    }

    @Test
    void buildHL7TQ2TypeAllMissingConditional() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var parentClass = new Hl7ToRhapsodysXmlConverter();
        TimingQuantityRelationship model = new TimingQuantityRelationship();
        Method privateMethod = Hl7ToRhapsodysXmlConverter.class.getDeclaredMethod("buildHL7TQ2Type", TimingQuantityRelationship.class);
        privateMethod.setAccessible(true);
        var result = (HL7TQ2Type) privateMethod.invoke(parentClass, model);
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getRelatedPlacerNumber());
        Assertions.assertNotNull(result.getRelatedFillerNumber());
        Assertions.assertNotNull(result.getRelatedPlacerGroupNumber());
    }

    @Test
    void buildHL7TQ1TypeAllMissingConditional() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var parentClass = new Hl7ToRhapsodysXmlConverter();
        TimingQuantity model = new TimingQuantity();
        Method privateMethod = Hl7ToRhapsodysXmlConverter.class.getDeclaredMethod("buildHL7TQ1Type", TimingQuantity.class);
        privateMethod.setAccessible(true);
        var result = (HL7TQ1Type) privateMethod.invoke(parentClass, model);
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getSetIDTQ1());
        Assertions.assertNotNull(result.getQuantity());
        Assertions.assertNotNull(result.getRepeatPattern());
        Assertions.assertNotNull(result.getExplicitTime());
        Assertions.assertNotNull(result.getRelativeTimeAndUnits());
        Assertions.assertNotNull(result.getServiceDuration());
        Assertions.assertNull(result.getStartdatetime());
        Assertions.assertNull(result.getEnddatetime());
        Assertions.assertNotNull(result.getPriority());
        Assertions.assertNull(result.getConjunction());
        Assertions.assertNotNull(result.getConditiontext());
        Assertions.assertNotNull(result.getTextinstruction());
        Assertions.assertNotNull(result.getOccurrenceDuration());
        Assertions.assertNotNull(result.getTotalOccurrences());
    }

    @Test
    void buildHL7RPTTypeAllMissingConditional() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var parentClass = new Hl7ToRhapsodysXmlConverter();
        Rpt model = new Rpt();
        Cwe cwe = new Cwe();
        model.setRepeatPatternCode(cwe);
        model.setCalendarAlignment("20230630");
        model.setPhaseRangeBeginValue("20230630");
        model.setPhaseRangeEndValue("20230630");
        model.setPeriodQuantity("20230630");
        model.setPeriodUnits("20230630");
        model.setInstitutionSpecifiedTime("20230630");
        model.setEvent("20230630");
        model.setEventOffsetQuantity("20230630");
        model.setEventOffsetUnits("20230630");

        var expectedValue = "20230630";

        Method privateMethod = Hl7ToRhapsodysXmlConverter.class.getDeclaredMethod("buildHL7RPTType", Rpt.class);
        privateMethod.setAccessible(true);
        var result = (HL7RPTType) privateMethod.invoke(parentClass, model);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedValue, result.getCalendarAlignment());
        Assertions.assertEquals(new BigInteger(expectedValue), result.getPhaseRangeBeginValue().getHL7Numeric());
        Assertions.assertEquals(new BigInteger(expectedValue), result.getPhaseRangeEndValue().getHL7Numeric());
        Assertions.assertEquals(new BigInteger(expectedValue), result.getPeriodQuantity().getHL7Numeric());
        Assertions.assertEquals(expectedValue, result.getPeriodUnits());
        Assertions.assertEquals(expectedValue, result.getInstitutionSpecifiedTime());
        Assertions.assertEquals(expectedValue, result.getEvent());
        Assertions.assertEquals(expectedValue, result.getEventOffsetUnits());
        Assertions.assertEquals(new BigInteger(expectedValue), result.getEventOffsetQuantity().getHL7Numeric());
        Assertions.assertNull(result.getGeneralTimingSpecification());

    }

    @Test
    void buildHL7PIV1TypeAllMissingConditional() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, DiHL7Exception {
        String hl7Message = TestData.randomGenerated251WithDataInAllFieldV1;
        HL7ParsedMessage hl7ParsedMsg = hl7Helper.hl7StringParser(hl7Message);
        OruR1 oru = (OruR1) hl7ParsedMsg.getParsedMessage();

        var parentClass = new Hl7ToRhapsodysXmlConverter();
        PatientVisit model = oru.getPatientResult().get(0).getPatient().getVisit().getPatientVisit();
        var expectedMessage = "20230615";
        Method privateMethod = Hl7ToRhapsodysXmlConverter.class.getDeclaredMethod("buildHL7PIV1Type", PatientVisit.class);
        privateMethod.setAccessible(true);
        var result = (HL7PIV1Type) privateMethod.invoke(parentClass, model);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedMessage, result.getSetIDPV1().getHL7SequenceID());
        Assertions.assertEquals(expectedMessage, result.getPatientClass());
        Assertions.assertEquals(expectedMessage, result.getPatientClass());
        Assertions.assertEquals(expectedMessage, result.getAssignedPatientLocation().getHL7PointofCare());
        Assertions.assertNull(result.getAssignedPatientLocation().getHL7Room());
        Assertions.assertNull(result.getAssignedPatientLocation().getHL7Bed());

        buildHL7PIV1TypeAllMissingConditionalHelper1(expectedMessage, result);
        buildHL7PIV1TypeAllMissingConditionalHelper2(expectedMessage, result);
        buildHL7PIV1TypeAllMissingConditionalHelper3(expectedMessage, result);
        buildHL7PIV1TypeAllMissingConditionalHelper4(expectedMessage, result);
    }

    private void buildHL7PIV1TypeAllMissingConditionalHelper1(String expectedMessage, HL7PIV1Type result) {
        Assertions.assertEquals(expectedMessage,result.getDeleteAccountIndicator());
        Assertions.assertNotNull(result.getDeleteAccountDate());
        Assertions.assertEquals(expectedMessage,result.getDischargeDisposition());
        Assertions.assertEquals(expectedMessage,result.getDischargedToLocation().getDischargeLocation());
        Assertions.assertNull(result.getDischargedToLocation().getEffectiveDate());
        Assertions.assertNotNull(result.getDietType());
        Assertions.assertEquals(expectedMessage,result.getServicingFacility());
        Assertions.assertEquals(expectedMessage,result.getBedStatus());
        Assertions.assertEquals(expectedMessage,result.getAccountStatus());
        Assertions.assertNotNull(result.getPendingLocation());
        Assertions.assertNotNull(result.getPriorTemporaryLocation());
        Assertions.assertNotNull(result.getAdmitDateTime());
        Assertions.assertNotNull(result.getDischargeDateTime());
        Assertions.assertNotNull(result.getCurrentPatientBalance());
        Assertions.assertNotNull(result.getTotalCharges());
        Assertions.assertNotNull(result.getTotalAdjustments());
        Assertions.assertNotNull(result.getTotalPayments());
        Assertions.assertNotNull(result.getAlternateVisitID());
        Assertions.assertEquals(expectedMessage,result.getVisitIndicator());
        Assertions.assertNotNull(result.getOtherHealthcareProvider());
    }
    private void buildHL7PIV1TypeAllMissingConditionalHelper2(String expectedMessage, HL7PIV1Type result) {
        Assertions.assertNull(result.getVIPIndicator());
        Assertions.assertNotNull(result.getAdmittingDoctor());
        Assertions.assertEquals(expectedMessage,result.getPatientType());
        Assertions.assertNotNull(result.getVisitNumber());
        Assertions.assertNotNull(result.getFinancialClass());
        Assertions.assertEquals(expectedMessage,result.getChargePriceIndicator());
        Assertions.assertEquals(expectedMessage,result.getCourtesyCode());
        Assertions.assertEquals(expectedMessage,result.getCreditRating());
        Assertions.assertNotNull(result.getContractCode());
        Assertions.assertNotNull(result.getContractEffectiveDate());
        Assertions.assertNotNull(result.getContractAmount());
        Assertions.assertNotNull(result.getContractPeriod());
        Assertions.assertEquals(expectedMessage,result.getInterestCode());
        Assertions.assertEquals(expectedMessage,result.getTransferToBadDebtCode());
        Assertions.assertEquals(new BigInteger("2023"),result.getTransferToBadDebtDate().getYear());
        Assertions.assertEquals(new BigInteger("6"),result.getTransferToBadDebtDate().getMonth());
        Assertions.assertEquals(new BigInteger("15"),result.getTransferToBadDebtDate().getDay());
        Assertions.assertEquals(expectedMessage,result.getBadDebtAgencyCode());
        Assertions.assertNotNull(result.getBadDebtRecoveryAmount());
        Assertions.assertNotNull(result.getBadDebtTransferAmount());
    }
    private void buildHL7PIV1TypeAllMissingConditionalHelper3(String expectedMessage, HL7PIV1Type result){
        Assertions.assertNull(result.getAttendingDoctor().get(0)
                .getHL7AssigningAuthority());
        Assertions.assertNull(result.getAttendingDoctor().get(0)
                .getHL7NameTypeCode());
        Assertions.assertNull(result.getAttendingDoctor().get(0)
                .getHL7AssigningFacility());
        Assertions.assertNull(result.getAttendingDoctor().get(0)
                .getHL7NameRepresentationCode());
        Assertions.assertNull(result.getAttendingDoctor().get(0)
                .getHL7NameContext());
        Assertions.assertNull(result.getAttendingDoctor().get(0)
                .getHL7NameValidityRange());
        Assertions.assertNull(result.getAttendingDoctor().get(0)
                .getHL7NameAssemblyOrder());
        Assertions.assertNull(result.getAttendingDoctor().get(0)
                .getHL7EffectiveDate());
        Assertions.assertNull(result.getAttendingDoctor().get(0)
                .getHL7ExpirationDate());
        Assertions.assertNull(result.getAttendingDoctor().get(0)
                .getHL7ProfessionalSuffix());
        Assertions.assertNull(result.getAttendingDoctor().get(0)
                .getHL7AssigningJurisdiction());
        Assertions.assertNull(result.getAttendingDoctor().get(0)
                .getHL7AssigningAgencyOrDepartment());
        Assertions.assertNotNull(result.getReferringDoctor());
        Assertions.assertNotNull(result.getConsultingDoctor());
        Assertions.assertEquals(expectedMessage,result.getHospitalService());
        Assertions.assertNotNull(result.getTemporaryLocation());
        Assertions.assertEquals(expectedMessage,result.getPreadmitTestIndicator());
        Assertions.assertEquals(expectedMessage,result.getReAdmissionIndicator());
        Assertions.assertNull(result.getAdmitSource());
        Assertions.assertNotNull(result.getAmbulatoryStatus());
    }
    private void buildHL7PIV1TypeAllMissingConditionalHelper4(String expectedMessage, HL7PIV1Type result) {
        Assertions.assertNull(result.getAssignedPatientLocation().getHL7Facility());
        Assertions.assertNull(result.getAssignedPatientLocation().getHL7LocationStatus());
        Assertions.assertNull(result.getAssignedPatientLocation().getHL7PersonLocationType());
        Assertions.assertNull(result.getAssignedPatientLocation().getHL7Building());
        Assertions.assertNull(result.getAssignedPatientLocation().getHL7Floor());
        Assertions.assertNull(result.getAssignedPatientLocation().getHL7LocationDescription());
        Assertions.assertNotNull(result.getAssignedPatientLocation().getHL7ComprehensiveLocationIdentifier());
        Assertions.assertNotNull(result.getAssignedPatientLocation().getHL7AssigningAuthorityforLocation());
        Assertions.assertEquals(expectedMessage, result.getAdmissionType());
        Assertions.assertNotNull(result.getPreadmitNumber());
        Assertions.assertNotNull(result.getPriorPatientLocation());
        Assertions.assertNotNull(result.getAttendingDoctor());
        Assertions.assertEquals(expectedMessage, result.getAttendingDoctor().get(0).getHL7IDNumber());
        Assertions.assertEquals(null, result.getAttendingDoctor().get(0).getHL7FamilyName().getHL7OwnSurname());
        Assertions.assertNull(result.getAttendingDoctor().get(0)
                .getHL7GivenName());
        Assertions.assertNull(result.getAttendingDoctor().get(0)
                .getHL7SecondAndFurtherGivenNamesOrInitialsThereof());
        Assertions.assertNull(result.getAttendingDoctor().get(0)
                .getHL7Suffix());
        Assertions.assertNull(result.getAttendingDoctor().get(0)
                .getHL7Prefix());
        Assertions.assertNull(result.getAttendingDoctor().get(0)
                .getHL7Degree());
        Assertions.assertNull(result.getAttendingDoctor().get(0)
                .getHL7SourceTable());
    }

    @Test
    void buildHL7OBRTypeAllMissingConditional() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, DiHL7Exception {
        String hl7Message = TestData.randomGenerated251WithDataInAllFieldV1;
        HL7ParsedMessage hl7ParsedMsg = hl7Helper.hl7StringParser(hl7Message);
        OruR1 oru = (OruR1) hl7ParsedMsg.getParsedMessage();

        var parentClass = new Hl7ToRhapsodysXmlConverter();
        ObservationRequest model = oru.getPatientResult().get(0).getOrderObservation().get(0).getObservationRequest();
        var expectedMessage = "20230615";
        Method privateMethod = Hl7ToRhapsodysXmlConverter.class.getDeclaredMethod("buildHL7OBRType", ObservationRequest.class);
        privateMethod.setAccessible(true);
        var result = (HL7OBRType) privateMethod.invoke(parentClass, model);
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getPlacerOrderNumber());
        Assertions.assertEquals(expectedMessage,result.getPriorityOBR());
        Assertions.assertEquals(new BigInteger("2023"),result.getRequestedDateTime().getYear());
        Assertions.assertEquals(new BigInteger("6"),result.getRequestedDateTime().getMonth());
        Assertions.assertEquals(new BigInteger("15"),result.getRequestedDateTime().getDay());
        Assertions.assertEquals(new BigInteger("0"),result.getRequestedDateTime().getHours());
        Assertions.assertEquals(new BigInteger("0"),result.getRequestedDateTime().getMinutes());
        Assertions.assertEquals(new BigInteger("0"),result.getRequestedDateTime().getSeconds());
        Assertions.assertNull(result.getRequestedDateTime().getMillis());
        Assertions.assertEquals("",result.getRequestedDateTime().getGmtOffset());
        Assertions.assertNotNull(result.getObservationEndDateTime());
        Assertions.assertNotNull(result.getCollectionVolume());
        Assertions.assertNotNull(result.getCollectionVolume().getHL7Quantity());
        Assertions.assertNotNull(result.getCollectionVolume().getHL7Units());
        Assertions.assertEquals(expectedMessage,result.getSpecimenActionCode());
        Assertions.assertNotNull(result.getDangerCode());
        Assertions.assertEquals(expectedMessage,result.getRelevantClinicalInformation());

        buildHL7OBRTypeAllMissingConditionalHelper1(expectedMessage, result);
        buildHL7OBRTypeAllMissingConditionalHelper2(expectedMessage, result);
        buildHL7OBRTypeAllMissingConditionalHelper3(result);
        buildHL7OBRTypeAllMissingConditionalHelper4(expectedMessage, result);
    }

    private void buildHL7OBRTypeAllMissingConditionalHelper1(String expectedMessage, HL7OBRType result) {
        Assertions.assertNull(result.getPrincipalResultInterpreter().getHL7Name().getHL7Degree());
        Assertions.assertNull(result.getPrincipalResultInterpreter().getHL7Name().getHL7SourceTable());
        Assertions.assertNull(result.getPrincipalResultInterpreter().getHL7Name().getHL7AssigningAuthorityNamespaceID());
        Assertions.assertNull(result.getPrincipalResultInterpreter().getHL7Name().getAssigningAuthorityUniversalID());
        Assertions.assertNull(result.getPrincipalResultInterpreter().getHL7Name().getAssigningAuthorityUniversalIDType());
        Assertions.assertNull(result.getPrincipalResultInterpreter().getHL7StartDatetime());
        Assertions.assertNull(result.getPrincipalResultInterpreter().getHL7EndDatetime());
        Assertions.assertNull(result.getPrincipalResultInterpreter().getHL7PointOfCare());
        Assertions.assertNull(result.getPrincipalResultInterpreter().getHL7Room());
        Assertions.assertNull(result.getPrincipalResultInterpreter().getHL7Bed());
        Assertions.assertNull(result.getPrincipalResultInterpreter().getHL7Facility());
        Assertions.assertNull(result.getPrincipalResultInterpreter().getHL7LocationStatus());
        Assertions.assertNull(result.getPrincipalResultInterpreter().getHL7PatientLocationType());
        Assertions.assertNull(result.getPrincipalResultInterpreter().getHL7Building());
        Assertions.assertNull(result.getPrincipalResultInterpreter().getHL7Floor());
        Assertions.assertNotNull(result.getScheduledDateTime());
        Assertions.assertNotNull(result.getTransportArrangementResponsibility());
        Assertions.assertEquals(expectedMessage,result.getTransportArranged());
        Assertions.assertEquals(expectedMessage,result.getEscortRequired());
        Assertions.assertNotNull(result.getProcedureCode());
        Assertions.assertNotNull(result.getMedicallyNecessaryDuplicateProcedureReason());
        Assertions.assertEquals(expectedMessage,result.getResultHandling());
        Assertions.assertNotNull(result.getParentUniversalServiceIdentifier());
    }
    private void buildHL7OBRTypeAllMissingConditionalHelper2(String expectedMessage, HL7OBRType result) {
        Assertions.assertEquals("",result.getQuantityTiming().getHL7OrderSequencing().getHL7FillerOrderNumberEntityIdentifier());
        Assertions.assertEquals(null,result.getQuantityTiming().getHL7OrderSequencing().getHL7FillerOrderNumberNamespaceID());
        Assertions.assertEquals(null,result.getQuantityTiming().getHL7OrderSequencing().getHL7SequenceConditionValue());
        Assertions.assertNotNull(result.getQuantityTiming().getHL7OrderSequencing().getHL7MaximumNumberOfRepeats());
        Assertions.assertNull(result.getQuantityTiming().getHL7OrderSequencing().getHL7FillerOrderNumberUniversalIDType());
        Assertions.assertEquals("",result.getQuantityTiming().getHL7OrderSequencing().getHL7PlacerOrderNumberUniversalID());
        Assertions.assertEquals("",result.getQuantityTiming().getHL7OrderSequencing().getHL7PlacerOrderNumberUniversalIDType());
        Assertions.assertEquals("",result.getQuantityTiming().getHL7OrderSequencing().getHL7FillerOrderNumberUniversalID());
        Assertions.assertNotNull(result.getResultCopiesTo());
        Assertions.assertNull(result.getResultCopiesTo().get(0).getHL7IdentifierCheckDigit());
        Assertions.assertNull(result.getResultCopiesTo().get(0).getHL7CheckDigitScheme());
        Assertions.assertNotNull(result.getParent());
        Assertions.assertNotNull(result.getParent().getHL7PlacerAssignedIdentifier());
        Assertions.assertNotNull(result.getParent().getHL7FillerAssignedIdentifier());
        Assertions.assertEquals(expectedMessage,result.getTransportationMode());
        Assertions.assertNotNull(result.getReasonforStudy());
        Assertions.assertNotNull(result.getPrincipalResultInterpreter());
        Assertions.assertEquals(expectedMessage,result.getPrincipalResultInterpreter().getHL7Name().getHL7IDNumber());
        Assertions.assertNull(result.getPrincipalResultInterpreter().getHL7Name().getHL7FamilyName());
        Assertions.assertNull(result.getPrincipalResultInterpreter().getHL7Name().getHL7GivenName());
        Assertions.assertNull(result.getPrincipalResultInterpreter().getHL7Name().getHL7SecondAndFurtherGivenNamesOrInitialsThereof());
        Assertions.assertNull(result.getPrincipalResultInterpreter().getHL7Name().getHL7Suffix());
        Assertions.assertNull(result.getPrincipalResultInterpreter().getHL7Name().getHL7Prefix());
    }
    private void buildHL7OBRTypeAllMissingConditionalHelper3(HL7OBRType result) {
        Assertions.assertNotNull(result.getQuantityTiming().getHL7Quantity());
        Assertions.assertNotNull(result.getQuantityTiming().getHL7Interval());
        Assertions.assertNull(result.getQuantityTiming().getHL7Interval().getRepeatPattern());
        Assertions.assertNull(result.getQuantityTiming().getHL7Interval().getExplicitTimeInterval());
        Assertions.assertNull(result.getQuantityTiming().getHL7Duration());
        Assertions.assertNull(result.getQuantityTiming().getHL7StartDateTime());
        Assertions.assertNull(result.getQuantityTiming().getHL7EndDateTime());
        Assertions.assertNull(result.getQuantityTiming().getHL7Priority());
        Assertions.assertNull(result.getQuantityTiming().getHL7Condition());
        Assertions.assertNotNull(result.getQuantityTiming().getHL7Text());
        Assertions.assertNull(result.getQuantityTiming().getHL7Conjunction());
        Assertions.assertNotNull(result.getQuantityTiming().getHL7TotalOccurrences());
        Assertions.assertNotNull(result.getQuantityTiming().getHL7OccurrenceDuration());
        Assertions.assertNull(result.getQuantityTiming().getHL7OccurrenceDuration().getHL7Identifier());
        Assertions.assertNull(result.getQuantityTiming().getHL7OccurrenceDuration().getHL7Text());
        Assertions.assertNull(result.getQuantityTiming().getHL7OccurrenceDuration().getHL7NameofAlternateCodingSystem());
        Assertions.assertNull(result.getQuantityTiming().getHL7OccurrenceDuration().getHL7AlternateIdentifier());
        Assertions.assertNull(result.getQuantityTiming().getHL7OccurrenceDuration().getHL7AlternateText());
        Assertions.assertNull(result.getQuantityTiming().getHL7OccurrenceDuration().getHL7NameofAlternateCodingSystem());
        Assertions.assertNotNull(result.getQuantityTiming().getHL7OrderSequencing());
        Assertions.assertEquals("",result.getQuantityTiming().getHL7OrderSequencing().getHL7SequenceResultsFlag());
        Assertions.assertEquals("",result.getQuantityTiming().getHL7OrderSequencing().getHL7PlacerOrderNumberEntityIdentifier());
        Assertions.assertEquals(null,result.getQuantityTiming().getHL7OrderSequencing().getHL7PlacerOrderNumberNamespaceID());
    }
    private void buildHL7OBRTypeAllMissingConditionalHelper4(String expectedMessage, HL7OBRType result) {
        Assertions.assertNotNull(result.getSpecimenReceivedDateTime());
        Assertions.assertNotNull(result.getSpecimenSource());
        Assertions.assertNotNull(result.getSpecimenSource().getHL7SpecimenSourceNameOrCode());
        Assertions.assertNotNull(result.getSpecimenSource().getHL7Additives());
        Assertions.assertNull(result.getSpecimenSource().getHL7SpecimenCollectionMethod());
        Assertions.assertNotNull(result.getSpecimenSource().getHL7BodySite());
        Assertions.assertNotNull(result.getSpecimenSource().getHL7SiteModifier());
        Assertions.assertNotNull(result.getSpecimenSource().getHL7CollectionMethodModifierCode());
        Assertions.assertNotNull(result.getSpecimenSource().getHL7SpecimenRole());
        Assertions.assertEquals(expectedMessage,result.getPlacerField1());
        Assertions.assertEquals(expectedMessage,result.getPlacerField2());
        Assertions.assertEquals(expectedMessage,result.getFillerField1());
        Assertions.assertEquals(expectedMessage,result.getFillerField2());
        Assertions.assertNotNull(result.getResultsRptStatusChngDateTime());
        Assertions.assertNotNull(result.getChargeToPractice());
        Assertions.assertNotNull(result.getChargeToPractice().getHL7ChargeCode());
        Assertions.assertNotNull(result.getChargeToPractice().getHL7MonetaryAmount().getHL7Quantity());
        Assertions.assertNull(result.getChargeToPractice().getHL7MonetaryAmount().getHL7Denomination());
        Assertions.assertEquals(expectedMessage,result.getDiagnosticServSectID());
        Assertions.assertEquals(expectedMessage,result.getResultStatus());
        Assertions.assertNotNull(result.getQuantityTiming());
        Assertions.assertNotNull(result.getQuantityTiming().getHL7Quantity().getHL7Quantity());
        Assertions.assertNotNull(result.getQuantityTiming().getHL7Quantity().getHL7Units());
    }
    @Test
    void buildHL7FT1TypeAllMissingConditional() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, DiHL7Exception {
        String hl7Message = TestData.randomGenerated251WithDataInAllFieldV1;
        HL7ParsedMessage hl7ParsedMsg = hl7Helper.hl7StringParser(hl7Message);
        OruR1 oru = (OruR1) hl7ParsedMsg.getParsedMessage();

        var parentClass = new Hl7ToRhapsodysXmlConverter();
        FinancialTransaction model = oru.getPatientResult().get(0).getOrderObservation().get(0).getFinancialTransaction().get(0);
        var expectedMessage = "20230615";
        Method privateMethod = Hl7ToRhapsodysXmlConverter.class.getDeclaredMethod("buildHL7FT1Type", FinancialTransaction.class);
        privateMethod.setAccessible(true);
        var result = (HL7FT1Type) privateMethod.invoke(parentClass, model);
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getSetIDFT1());
        Assertions.assertEquals(expectedMessage,result.getTransactionID());
        Assertions.assertEquals(expectedMessage,result.getTransactionBatchID());
        Assertions.assertNotNull(result.getTransactionDate().getHL7RangeStartDateTime());
        Assertions.assertNull(result.getTransactionDate().getHL7RangeEndDateTime());
        Assertions.assertNotNull(result.getTransactionPostingDate());
        Assertions.assertNull(result.getTransactionPostingDate().getMillis());
        Assertions.assertEquals(expectedMessage,result.getTransactionType());
        Assertions.assertNotNull(result.getTransactionCode());
        Assertions.assertNull(result.getTransactionCode().getHL7NameofCodingSystem());
        Assertions.assertEquals(expectedMessage,result.getTransactionDescription());
        Assertions.assertEquals(expectedMessage,result.getTransactionDescriptionAlt());
        Assertions.assertNotNull(result.getTransactionQuantity());
        Assertions.assertNotNull(result.getTransactionAmountExtended());
        Assertions.assertNotNull(result.getTransactionAmountExtended().getHL7Price());
        Assertions.assertNull(result.getTransactionAmountExtended().getHL7PriceType());
        Assertions.assertNotNull(result.getTransactionAmountExtended().getHL7FromValue());
        Assertions.assertNotNull(result.getTransactionAmountExtended().getHL7ToValue());
        Assertions.assertNotNull(result.getTransactionAmountExtended().getHL7RangeUnits());
        Assertions.assertNull(result.getTransactionAmountExtended().getHL7RangeType());
        Assertions.assertNotNull(result.getTransactionAmountUnit());
        Assertions.assertNotNull(result.getDepartmentCode());
        Assertions.assertNotNull(result.getInsurancePlanID());
        Assertions.assertNotNull(result.getInsuranceAmount());
        Assertions.assertNotNull(result.getAssignedPatientLocation());
        Assertions.assertEquals(expectedMessage,result.getFeeSchedule());
        Assertions.assertEquals(expectedMessage,result.getPatientType());
        Assertions.assertNotNull(result.getDiagnosisCodeFT1());
        Assertions.assertNotNull(result.getPerformedByCode());
        Assertions.assertNotNull(result.getOrderedByCode());
        Assertions.assertNull(result.getUnitCost());
        Assertions.assertNull(result.getFillerOrderNumber());
        Assertions.assertNotNull(result.getEnteredByCode());
        Assertions.assertNotNull(result.getProcedureCode());
        Assertions.assertNotNull(result.getProcedureCodeModifier());
        Assertions.assertNotNull(result.getAdvancedBeneficiaryNoticeCode());
        Assertions.assertNotNull(result.getMedicallyNecessaryDuplicateProcedureReason());
        Assertions.assertNotNull(result.getPaymentReferenceID());
        Assertions.assertNotNull(result.getTransactionReferenceKey());
        Assertions.assertNotNull(result.getNDCCode());
    }

    @Test
    void buildHL7SPMTypeAllMissingConditional() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, DiHL7Exception {
        String hl7Message = TestData.randomGenerated251WithDataInAllFieldV1;
        HL7ParsedMessage hl7ParsedMsg = hl7Helper.hl7StringParser(hl7Message);
        OruR1 oru = (OruR1) hl7ParsedMsg.getParsedMessage();

        var parentClass = new Hl7ToRhapsodysXmlConverter();
        gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.specimen.Specimen model = oru.getPatientResult().get(0).getOrderObservation().get(0).getSpecimen().get(0).getSpecimen();
        var expectedMessage = "20230615";
        Method privateMethod = Hl7ToRhapsodysXmlConverter.class.getDeclaredMethod("buildHL7SPMType", gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.specimen.Specimen.class);
        privateMethod.setAccessible(true);
        var result = (HL7SPMType) privateMethod.invoke(parentClass, model);
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getSetIDSPM());
        Assertions.assertNotNull(result.getSpecimenID());
        Assertions.assertNotNull(result.getSpecimenParentIDs());
        Assertions.assertNotNull(result.getSpecimenType());
        Assertions.assertNotNull(result.getSpecimenCollectionMethod());
        Assertions.assertNotNull(result.getSpecimenAdditives());
        Assertions.assertNotNull(result.getSpecimenSourceSite());
        Assertions.assertNotNull(result.getSpecimenSourceSiteModifier());
        Assertions.assertNotNull(result.getSpecimenRole());
        Assertions.assertNotNull(result.getSpecimenCollectionAmount());
        Assertions.assertNotNull(result.getGroupedSpecimenCount());
        Assertions.assertNotNull(result.getSpecimenDescription());
        buildHL7SPMTypeAllMissingConditionalHelper(result);
    }

    private void buildHL7SPMTypeAllMissingConditionalHelper(HL7SPMType result) {
        Assertions.assertNotNull(result.getSpecimenHandlingCode());
        Assertions.assertNotNull(result.getSpecimenRiskCode());
        Assertions.assertNull(result.getSpecimenCollectionDateTime());
        Assertions.assertNotNull(result.getSpecimenReceivedDateTime());
        Assertions.assertNotNull(result.getSpecimenExpirationDateTime());
        Assertions.assertNotNull(result.getSpecimenAvailability());
        Assertions.assertNotNull(result.getSpecimenRejectReason());
        Assertions.assertNotNull(result.getSpecimenQuality());
        Assertions.assertNotNull(result.getSpecimenAppropriateness());
        Assertions.assertNotNull(result.getSpecimenCondition());
        Assertions.assertNotNull(result.getSpecimenCurrentQuantity());
        Assertions.assertNotNull(result.getNumberOfSpecimenContainers());
        Assertions.assertNotNull(result.getContainerType());
        Assertions.assertNotNull(result.getContainerCondition());
        Assertions.assertNotNull(result.getSpecimenChildRole());
    }

    @Test
    void buildHL7MSHTypeAllMissingConditional() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, DiHL7Exception {
        String hl7Message = TestData.randomGenerated251WithDataInAllFieldV1;
        HL7ParsedMessage hl7ParsedMsg = hl7Helper.hl7StringParser(hl7Message);
        OruR1 oru = (OruR1) hl7ParsedMsg.getParsedMessage();

        var parentClass = new Hl7ToRhapsodysXmlConverter();
        MessageHeader model = oru.getMessageHeader();
        var expectedMessage = "20230615";
        Method privateMethod = Hl7ToRhapsodysXmlConverter.class.getDeclaredMethod("buildHL7MSHType", MessageHeader.class);
        privateMethod.setAccessible(true);
        var result = (HL7MSHType) privateMethod.invoke(parentClass, model);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("|", result.getFieldSeparator());
        Assertions.assertEquals(new BigInteger("20230615"), result.getSequenceNumber().getHL7Numeric());
        Assertions.assertEquals(expectedMessage, result.getContinuationPointer());
        Assertions.assertEquals(expectedMessage, result.getAcceptAcknowledgmentType());
        Assertions.assertEquals(expectedMessage, result.getApplicationAcknowledgmentType());
        Assertions.assertEquals(expectedMessage, result.getCountryCode());
        Assertions.assertNull(result.getPrincipalLanguageOfMessage());
        Assertions.assertEquals(expectedMessage, result.getAlternateCharacterSetHandlingScheme());

    }

    @Test
    void buildHL7NK1TypeAllMissingConditional() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, DiHL7Exception {
        String hl7Message = TestData.randomGenerated251WithDataInAllFieldV1;
        HL7ParsedMessage hl7ParsedMsg = hl7Helper.hl7StringParser(hl7Message);
        OruR1 oru = (OruR1) hl7ParsedMsg.getParsedMessage();

        var parentClass = new Hl7ToRhapsodysXmlConverter();
        NextOfKin model = oru.getPatientResult().get(0).getPatient().getNextOfKin().get(0);
        var expectedMessage = "20230615";
        Method privateMethod = Hl7ToRhapsodysXmlConverter.class.getDeclaredMethod("buildHL7NK1Type", NextOfKin.class);
        privateMethod.setAccessible(true);
        var result = (HL7NK1Type) privateMethod.invoke(parentClass, model);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("1", result.getSetIDNK1());
        Assertions.assertNotNull(result.getRelationship());
        Assertions.assertNotNull(result.getStartDate());
        Assertions.assertNotNull(result.getEndDate());
        Assertions.assertNull(result.getNextOfKinAssociatedPartiesJobCodeClass());
        Assertions.assertNotNull(result.getNextOfKinAssociatedPartiesEmployeeNumber());
        Assertions.assertNotNull(result.getDateTimeOfBirth());
        Assertions.assertNotNull(result.getPrimaryLanguage());
        Assertions.assertEquals(expectedMessage, result.getLivingArrangement());
        Assertions.assertNotNull(result.getPublicityCode());
        Assertions.assertEquals(expectedMessage, result.getProtectionIndicator());
        Assertions.assertEquals(expectedMessage, result.getStudentIndicator());
        Assertions.assertNotNull(result.getReligion());
        Assertions.assertNotNull(result.getNationality());
        Assertions.assertNotNull(result.getContactPersonsTelephoneNumber());
        Assertions.assertEquals(expectedMessage, result.getJobStatus());
        Assertions.assertEquals(expectedMessage, result.getHandicap());
        Assertions.assertEquals(expectedMessage, result.getContactPersonSocialSecurityNumber());
        Assertions.assertEquals(expectedMessage, result.getNextOfKinBirthPlace());
        Assertions.assertEquals(expectedMessage, result.getVIPIndicator());
    }

    @Test
    void buildHL7ORCTypeAllMissingConditional() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, DiHL7Exception {
        String hl7Message = TestData.randomGenerated251WithDataInAllFieldV1;
        HL7ParsedMessage hl7ParsedMsg = hl7Helper.hl7StringParser(hl7Message);
        OruR1 oru = (OruR1) hl7ParsedMsg.getParsedMessage();

        var parentClass = new Hl7ToRhapsodysXmlConverter();
        CommonOrder model = oru.getPatientResult().get(0).getOrderObservation().get(0).getCommonOrder();
        var expectedMessage = "20230615";
        Method privateMethod = Hl7ToRhapsodysXmlConverter.class.getDeclaredMethod("buildHL7ORCType", CommonOrder.class);
        privateMethod.setAccessible(true);
        var result = (HL7ORCType) privateMethod.invoke(parentClass, model);
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getParent());
        Assertions.assertNull(result.getEnterersLocation());
        Assertions.assertNotNull(result.getOrderControlCodeReason());
        Assertions.assertNotNull(result.getEnteringOrganization());
        Assertions.assertNotNull(result.getEnteringDevice());
        Assertions.assertNotNull(result.getAdvancedBeneficiaryNoticeCode());
        Assertions.assertNotNull(result.getOrderStatusModifier());
        Assertions.assertNotNull(result.getAdvancedBeneficiaryNoticeOverrideReason());
        Assertions.assertNotNull(result.getFillersExpectedAvailabilityDateTime());
        Assertions.assertNotNull(result.getConfidentialityCode());
        Assertions.assertNotNull(result.getOrderType());
        Assertions.assertNotNull(result.getEntererAuthorizationMode());
        Assertions.assertNull(result.getParentUniversalServiceIdentifier());

    }

    @Test
    void buildHL7PIDTypeAllMissingConditional() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, DiHL7Exception {
        String hl7Message = TestData.randomGenerated251WithDataInAllFieldV1;
        HL7ParsedMessage hl7ParsedMsg = hl7Helper.hl7StringParser(hl7Message);
        OruR1 oru = (OruR1) hl7ParsedMsg.getParsedMessage();

        var parentClass = new Hl7ToRhapsodysXmlConverter();
        PatientIdentification model = oru.getPatientResult().get(0).getPatient().getPatientIdentification();
        var expectedMessage = "20230615";
        Method privateMethod = Hl7ToRhapsodysXmlConverter.class.getDeclaredMethod("buildHL7PIDType", PatientIdentification.class);
        privateMethod.setAccessible(true);
        var result = (HL7PIDType) privateMethod.invoke(parentClass, model);
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getPatientAlias());
        Assertions.assertNull(result.getPatientAlias().getHL7NameRepresentationCode());
        Assertions.assertNull(result.getPatientAlias().getHL7NameContext());
        Assertions.assertNull(result.getPatientAlias().getHL7NameValidityRange());
        Assertions.assertNull(result.getPatientAlias().getHL7NameAssemblyOrder());
        Assertions.assertNull(result.getPatientAlias().getHL7EffectiveDate());
        Assertions.assertNull(result.getPatientAlias().getHL7ExpirationDate());
        Assertions.assertNull(result.getPatientAlias().getHL7ProfessionalSuffix());
        Assertions.assertEquals(expectedMessage, result.getCountyCode());
        Assertions.assertNotNull(result.getMaritalStatus());
        Assertions.assertNotNull(result.getReligion());
        Assertions.assertNull(result.getPatientAccountNumber());
        Assertions.assertEquals(expectedMessage, result.getSSNNumberPatient());
        buildHL7PIDTypeAllMissingConditionalHelper(expectedMessage, result);
    }
    
    private void buildHL7PIDTypeAllMissingConditionalHelper(String expectedMessage, HL7PIDType result) {
        Assertions.assertNotNull(result.getDriversLicenseNumberPatient());
        Assertions.assertEquals(expectedMessage, result.getDriversLicenseNumberPatient().getHL7LicenseNumber());
        Assertions.assertNull(result.getDriversLicenseNumberPatient().getHL7IssuingStateProvinceCountry());
        Assertions.assertNull(result.getDriversLicenseNumberPatient().getHL7ExpirationDate());
        Assertions.assertEquals(expectedMessage, result.getBirthPlace());
        Assertions.assertEquals(expectedMessage, result.getMultipleBirthIndicator());
        Assertions.assertNotNull(result.getBirthOrder());
        Assertions.assertNotNull(result.getVeteransMilitaryStatus());
        Assertions.assertNotNull(result.getNationality());
        Assertions.assertNotNull(result.getPatientDeathDateAndTime());
        Assertions.assertEquals(expectedMessage, result.getPatientDeathIndicator());
        Assertions.assertEquals(expectedMessage, result.getIdentityUnknownIndicator());
        Assertions.assertNotNull(result.getLastUpdateDateTime());
        Assertions.assertNull(result.getLastUpdateFacility());
        Assertions.assertNotNull(result.getSpeciesCode());
        Assertions.assertNotNull(result.getBreedCode());
        Assertions.assertEquals(expectedMessage, result.getStrain());
    }


    @ParameterizedTest
    @CsvSource({
            "20230615123059-timezone, 20230615123059",
            "20230615123, 2023061512300",
            "20230615, 20230615000000"
    })
    void testAppendingTimeStamp(String payload, String expectedMessage) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var parentClass = new Hl7ToRhapsodysXmlConverter();
        Method privateMethod = Hl7ToRhapsodysXmlConverter.class.getDeclaredMethod("appendingTimeStamp", String.class);
        privateMethod.setAccessible(true);
        var result = (String) privateMethod.invoke(parentClass, payload);

        Assertions.assertEquals(expectedMessage, result);
    }

    @ParameterizedTest
    @CsvSource({
            "20230615123059-050",
            "20230615123059-0500",
            "20230615123059-05000",
    })
    void testBuildHL7TSType(String payload) throws Exception {
        var instance = new Hl7ToRhapsodysXmlConverter();
        Method method = Hl7ToRhapsodysXmlConverter.class.getDeclaredMethod("buildHL7TSType", String.class, int.class);
        method.setAccessible(true);
        HL7TSType result = (HL7TSType) method.invoke(instance, payload, 0);
        Assertions.assertNotNull(result);
    }

}
