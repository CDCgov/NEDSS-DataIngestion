package gov.cdc.dataingestion.hl7.helper.unitTest;

import com.google.gson.Gson;
import gov.cdc.dataingestion.hl7.helper.HL7Helper;
import gov.cdc.dataingestion.hl7.helper.integration.exception.DiHL7Exception;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Ce;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageType.OruR1;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static gov.cdc.dataingestion.hl7.helper.unitTest.Hl7TestData.*;

public class HL7HelperTest {
    private HL7Helper target;
    private String data = "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169838-v25|T|2.5\n"
            + "PID|||7005728^^^TML^MR||TEST^RACHEL^DIAMOND||19310313|F|||200 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\n"
            + "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\n"
            + "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\n"
            + "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\n"
            + "OBX|1|ST|||Test Value";

    private String validData = "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169838-v25|T|2.5\r"
            + "PID|||7005728^^^TML^MR||TEST^RACHEL^DIAMOND||19310313|F|||200 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r"
            + "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r"
            + "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r"
            + "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\r"
            + "OBX|1|ST|||Test Value";

    private String invalidData = "TEST TEST";
    @BeforeEach
    public void setUp() {
        target = new HL7Helper();
    }



    @Test
    public void hl7StringValidatorTest_ReturnValidMessage() throws DiHL7Exception {
        var result = target.hl7StringValidator(data);
        Assertions.assertEquals(validData, result);
    }

    @Test
    public void hl7StringParser_ReturnValidMessage() throws  DiHL7Exception {
        var result = target.hl7StringParser(testMessageForXmlIssue);
        Gson gson = new Gson();
        String json = gson.toJson(result);
        Assertions.assertEquals("R01", result.getEventTrigger());
    }

    @Test
    public void hl7StringParser_ReturnValidFromRhapsodyMessage() throws  DiHL7Exception {
        var result = target.hl7StringParser(messageByRhapsody);
        Assertions.assertEquals("R01", result.getEventTrigger());
    }


    @Test
    public void hl7StringConvert231To251_ReturnValidMessage() throws  DiHL7Exception {
        var result = target.convert231To251(testMessage);

        Assertions.assertEquals("R01", result.getEventTrigger());
    }

    @Test
    public void hl7v231StringParser_ReturnException()  {

        var exception = Assertions.assertThrows(DiHL7Exception.class, () -> {
            target.convert231To251(messageByRhapsody);
        });

        Assertions.assertNotNull(exception);
    }

    @Test
    public void hl7StringParserWith231_ReturnValidMessage() throws  DiHL7Exception {
        var result = target.hl7StringParser(testMessage);

        Assertions.assertEquals("R01", result.getEventTrigger());
    }

    @Test
    public void hl7StringParserWith251_ReturnValidMessage_RandomV1() throws  DiHL7Exception {
        var result = target.hl7StringParser(randomGenerated251WithDataInAllField);
        Assertions.assertEquals("R01", result.getEventTrigger());
    }

    @Test
    public void hl7StringParserWith251_ReturnValidMessage_RandomV2() throws  DiHL7Exception {
        var result = target.hl7StringParser(randomGenerated251WithDataInAllFieldV2);
        var oru = (OruR1) result.getParsedMessage();
        Assertions.assertEquals("R01", result.getEventTrigger());

        //region Observation Request
        var observationRequest = oru.getPatientResult().get(0).getOrderObservation().get(0).getObservationRequest();
        var observationRequestCnn = observationRequest.getPrincipalResultInterpreter().getName();
        Assertions.assertEquals("20230615",observationRequestCnn.getIdNumber());
        Assertions.assertNull(observationRequestCnn.getFamilyName());
        Assertions.assertNull(observationRequestCnn.getGivenName());
        Assertions.assertNull(observationRequestCnn.getSecondAndFurtherGivenNameOrInitial());
        Assertions.assertNull(observationRequestCnn.getSuffix());
        Assertions.assertNull(observationRequestCnn.getPrefix());
        Assertions.assertNull(observationRequestCnn.getDegree());
        Assertions.assertNull(observationRequestCnn.getSourceTable());
        Assertions.assertNull(observationRequestCnn.getAssignAuthorityNamespaceId());
        Assertions.assertNull(observationRequestCnn.getAssignAuthorityUniversalId());
        Assertions.assertNull(observationRequestCnn.getAssignAuthorityUniversalIdType());
        Assertions.assertEquals("1",observationRequest.getSetIdObr());
        Assertions.assertEquals("20230615",observationRequest.getPriorityObr());
        Assertions.assertEquals("20230615",observationRequest.getSpecimenActionCode());
        Assertions.assertEquals("20230615",observationRequest.getRelevantClinicalInformation());
        Assertions.assertEquals("20230615",observationRequest.getPlacerField1());
        Assertions.assertEquals("20230615",observationRequest.getPlacerField2());
        Assertions.assertEquals("20230615",observationRequest.getFillerField1());
        Assertions.assertEquals("20230615",observationRequest.getFillerField2());
        Assertions.assertEquals("20230615",observationRequest.getDiagnosticServSectId());
        Assertions.assertEquals("20230615",observationRequest.getResultStatus());
        Assertions.assertEquals("20230615",observationRequest.getTransportationMode());
        Assertions.assertEquals("20230615",observationRequest.getTransportArranged());
        Assertions.assertEquals("20230615",observationRequest.getEscortRequired());
        Assertions.assertEquals("20230615",observationRequest.getResultHandling());
        //endregion

        //region finance
        var finance = oru.getPatientResult().get(0).getOrderObservation().get(0).getFinancialTransaction().get(0);
        var financeCne = finance.getNdcCode();
        var financeCp = finance.getTransactionAmountExt();
        var financeCx = finance.getPaymentReferenceId();
        var financeCwe = finance.getMedicallyNecessaryDuplicateProcedureReason();
        var financePl = finance.getAssignedPatientLocation();
        var financeXcn = finance.getPerformedByCode().get(0);
        var financeEi = finance.getFillerOrderNumber();
        Assertions.assertEquals("20230615",financeEi.getEntityIdentifier());
        Assertions.assertNull(financeEi.getNameSpaceId());
        Assertions.assertNull(financeEi.getUniversalId());
        Assertions.assertNull(financeEi.getUniversalIdType());
        Assertions.assertEquals("20230615",financeXcn.getIdNumber());
        Assertions.assertNull(financeXcn.getGivenName());
        Assertions.assertNull(financeXcn.getSecondAndFurtherGivenNameOrInitial());
        Assertions.assertNull(financeXcn.getSuffix());
        Assertions.assertNull(financeXcn.getPrefix());
        Assertions.assertNull(financeXcn.getDegree());
        Assertions.assertNull(financeXcn.getSourceTable());
        Assertions.assertNull(financeXcn.getNameTypeCode());
        Assertions.assertNull(financeXcn.getIdentifierCheckDigit());
        Assertions.assertNull(financeXcn.getCheckDigitScheme());
        Assertions.assertNull(financeXcn.getIdentifierTypeCode());
        Assertions.assertNull(financeXcn.getNameRepresentationCode());
        Assertions.assertNull(financeXcn.getNameAssemblyOrder());
        Assertions.assertNull(financeXcn.getProfessionalSuffix());
        Assertions.assertEquals("20230615",financePl.getPointOfCare());
        Assertions.assertNull(financePl.getRoom());
        Assertions.assertNull(financePl.getBed());
        Assertions.assertNull(financePl.getPersonLocationType());
        Assertions.assertNull(financePl.getBuilding());
        Assertions.assertNull(financePl.getFloor());
        Assertions.assertNull(financePl.getLocationDescription());
        Assertions.assertEquals("20230615",financeCwe.getIdentifier());
        Assertions.assertNull(financeCwe.getText());
        Assertions.assertNull(financeCwe.getNameOfAlterCodeSystem());
        Assertions.assertNull(financeCwe.getNameOfCodingSystem());
        Assertions.assertNull(financeCwe.getAlternateIdentifier());
        Assertions.assertNull(financeCwe.getAlternateText());
        Assertions.assertNull(financeCwe.getCodeSystemVerId());
        Assertions.assertNull(financeCwe.getAlterCodeSystemVerId());
        Assertions.assertNull(financeCwe.getOriginalText());
        Assertions.assertEquals("20230615",financeCx.getIdNumber());
        Assertions.assertNull(financeCx.getCheckDigit());
        Assertions.assertNull(financeCx.getCheckDigitScheme());
        Assertions.assertNull(financeCx.getIdentifierTypeCode());
        Assertions.assertNull(financeCx.getEffectiveDate());
        Assertions.assertNull(financeCx.getExpirationDate());
        Assertions.assertNull(financeCp.getPriceType());
        Assertions.assertNull(financeCp.getFromValue());
        Assertions.assertNull(financeCp.getToValue());
        Assertions.assertNull(financeCp.getRangeType());
        Assertions.assertEquals("20230615",financeCne.getIdentifier());
        Assertions.assertNull(financeCne.getText());
        Assertions.assertNull(financeCne.getNameOfCodingSystem());
        Assertions.assertNull(financeCne.getAlternateIdentifier());
        Assertions.assertNull(financeCne.getAlternateText());
        Assertions.assertNull(financeCne.getNameOfAlternateCodingSystem());
        Assertions.assertNull(financeCne.getCodingSystemVersionId());
        Assertions.assertNull(financeCne.getAlternateCodingSystemVersionId());
        Assertions.assertNull(financeCne.getOriginalText());
        Assertions.assertEquals("1",finance.getSetIdFT1());
        Assertions.assertEquals("20230615",finance.getTransactionId());
        Assertions.assertEquals("20230615",finance.getTransactionBatchId());
        Assertions.assertEquals("20230615",finance.getTransactionType());
        Assertions.assertEquals("20230615",finance.getTransactionDescription());
        Assertions.assertEquals("20230615",finance.getTransactionDescriptionAlter());
        Assertions.assertEquals("20230615",finance.getTransactionQuantity());
        Assertions.assertEquals("20230615",finance.getFeeSchedule());
        Assertions.assertEquals("20230615",finance.getPatientType());
        //endregion

        //region Time Qty
        var timeQty = oru.getPatientResult().get(0).getOrderObservation().get(0).getTimingQty().get(0).getTimeQuantity();
        var timeQtyRelation = oru.getPatientResult().get(0).getOrderObservation().get(0).getTimingQty().get(0).getTimeQuantityRelationship();
        Assertions.assertEquals("1",timeQty.getSetIdTq1());
        Assertions.assertEquals("20230615",timeQty.getConditionText());
        Assertions.assertEquals("20230615",timeQty.getTextInstruction());
        Assertions.assertEquals("20230615",timeQty.getConjunction());
        Assertions.assertEquals("20230615",timeQty.getTotalOccurrences());
        Assertions.assertEquals("1",timeQtyRelation.get(0).getSetIdTq2());
        Assertions.assertEquals("20230615",timeQtyRelation.get(0).getSequenceResultFlag());
        Assertions.assertEquals("20230615",timeQtyRelation.get(0).getSequenceConditionCode());
        Assertions.assertEquals("20230615",timeQtyRelation.get(0).getCyclicGroupMaximumNumberOfRepeats());
        Assertions.assertEquals("20230615",timeQtyRelation.get(0).getSpecialServiceRequestRelationship());
        //endregion

        //region Observation Result
        var observationResult = oru.getPatientResult().get(0).getOrderObservation().get(0).getObservation().get(0).getObservationResult();
        Assertions.assertEquals("1",observationResult.getSetIdObx());
        Assertions.assertEquals("ST",observationResult.getValueType());
        Assertions.assertEquals("20230615",observationResult.getObservationSubId());
        Assertions.assertEquals("20230615",observationResult.getReferencesRange());
        Assertions.assertEquals("21",observationResult.getProbability());
        Assertions.assertEquals("20230615",observationResult.getObservationResultStatus());
        Assertions.assertEquals("20230615",observationResult.getUserDefinedAccessChecks());
        Assertions.assertEquals("Varies[20230615]",observationResult.getReservedForHarmonizationWithV261());
        Assertions.assertEquals("Varies[20230615]",observationResult.getReservedForHarmonizationWithV262());
        Assertions.assertEquals("Varies[20230615]",observationResult.getReservedForHarmonizationWithV263());
        //endregion

        //region patient visit
        var patientVisit = oru.getPatientResult().get(0).getPatient().getVisit().getPatientVisit();
        Assertions.assertEquals("20230615",patientVisit.getSetIdPv1());
        Assertions.assertEquals("20230615",patientVisit.getPatientClass());
        Assertions.assertEquals("20230615",patientVisit.getAdmissionType());
        Assertions.assertEquals("20230615",patientVisit.getHospitalService());
        Assertions.assertEquals("20230615",patientVisit.getPreadmitTestIndicator());
        Assertions.assertEquals("20230615",patientVisit.getReAdmissionIndicator());
        Assertions.assertEquals("20230615",patientVisit.getAdmitSource());
        Assertions.assertEquals("20230615",patientVisit.getVipStatus());
        Assertions.assertEquals("20230615",patientVisit.getPatientType());
        Assertions.assertEquals("20230615",patientVisit.getInterestCode());
        Assertions.assertEquals("20230615",patientVisit.getTransferToBadDebtCode());
        Assertions.assertEquals("20230615",patientVisit.getTransferToBadDebtDate());
        Assertions.assertEquals("20230615",patientVisit.getBadDebtAgencyCode());
        Assertions.assertEquals("20230615",patientVisit.getBadDebtTransferAmount());
        Assertions.assertEquals("20230615",patientVisit.getBadDebtRecoveryAmount());
        Assertions.assertEquals("20230615",patientVisit.getDeleteAccountDate());
        Assertions.assertEquals("20230615",patientVisit.getDischargeDisposition());
        Assertions.assertEquals("20230615",patientVisit.getServicingFacility());
        Assertions.assertEquals("20230615",patientVisit.getBedStatus());
        Assertions.assertEquals("20230615",patientVisit.getAccountStatus());
        Assertions.assertEquals("20230615",patientVisit.getCurrentPatientBalance());
        Assertions.assertEquals("20230615",patientVisit.getTotalCharge());
        Assertions.assertEquals("20230615",patientVisit.getTotalAdjustment());
        Assertions.assertEquals("20230615",patientVisit.getTotalPayment());
        Assertions.assertEquals("20230615",patientVisit.getVisitIndicator());
        //endregion

        //region patient identification
        var patientIdentify = oru.getPatientResult().get(0).getPatient().getPatientIdentification();
        Assertions.assertNotNull(patientIdentify.getPatientId());
        Assertions.assertEquals("20230615", patientIdentify.getAdministrativeSex());
        Assertions.assertEquals("20230615", patientIdentify.getCountyCode());
        Assertions.assertEquals("20230615", patientIdentify.getBirthPlace());
        Assertions.assertEquals("20230615", patientIdentify.getMultipleBirthIndicator());
        Assertions.assertEquals("20230615", patientIdentify.getBirthOrder());
        Assertions.assertEquals("20230615", patientIdentify.getPatientDeathIndicator());
        Assertions.assertEquals("20230615", patientIdentify.getIdentityUnknownIndicator());
        Assertions.assertEquals("20230615", patientIdentify.getStrain());
        //endregion

        //region patient additional demo
        var patientAdditionalDemo = oru.getPatientResult().get(0).getPatient().getPatientAdditionalDemographic();
        Assertions.assertEquals("20230615",patientAdditionalDemo.getLivingDependency().get(0));
        Assertions.assertEquals("20230615",patientAdditionalDemo.getLivingArrangement());
        Assertions.assertNotNull(patientAdditionalDemo.getPatientPrimaryFacility().get(0));
        Assertions.assertEquals("20230615",patientAdditionalDemo.getStudentIndicator());
        Assertions.assertEquals("20230615",patientAdditionalDemo.getHandiCap());
        Assertions.assertEquals("20230615",patientAdditionalDemo.getLivingWillCode());
        Assertions.assertEquals("20230615",patientAdditionalDemo.getOrganDonorCode());
        Assertions.assertEquals("20230615",patientAdditionalDemo.getSeparateBill());
        Assertions.assertEquals("20230615",patientAdditionalDemo.getProtectionIndicator());
        Assertions.assertEquals("20230615",patientAdditionalDemo.getProtectionIndicatorEffectiveDate());
        Assertions.assertEquals("20230615",patientAdditionalDemo.getImmunizationRegistryStatus());
        Assertions.assertEquals("20230615",patientAdditionalDemo.getImmunizationRegistryStatusEffectiveDate());
        Assertions.assertEquals("20230615",patientAdditionalDemo.getPublicityCodeEffectiveDate());
        Assertions.assertEquals("20230615",patientAdditionalDemo.getMilitaryBranch());
        Assertions.assertEquals("20230615",patientAdditionalDemo.getMilitaryRank());
        Assertions.assertNull(patientAdditionalDemo.getMilitaryStatus());
        //endregion

        //region NEXT OF KIN
        var nextKin = oru.getPatientResult().get(0).getPatient().getNextOfKin().get(0);
        Assertions.assertEquals("1", nextKin.getSetIdNK1());
        var familyName = nextKin.getNkName().get(0).getFamilyName();
        Assertions.assertEquals("TESTNOK114B",familyName.getSurname());
        Assertions.assertNull(familyName.getOwnSurnamePrefix());
        Assertions.assertNull(familyName.getOwnSurname());
        Assertions.assertNull(familyName.getSurnameFromPartner());
        Assertions.assertNull(familyName.getSurnamePrefixFromPartner());
        Assertions.assertEquals("FIRSTNOK1", nextKin.getNkName().get(0).getGivenName());
        Assertions.assertEquals("X", nextKin.getNkName().get(0).getSecondAndFurtherGivenNameOrInitial());
        Assertions.assertEquals("JR", nextKin.getNkName().get(0).getSuffix());
        Assertions.assertEquals("DR", nextKin.getNkName().get(0).getPrefix());
        Assertions.assertEquals("MD", nextKin.getNkName().get(0).getDegree());
        Assertions.assertNull( nextKin.getNkName().get(0).getNameTypeCode());
        Assertions.assertNull( nextKin.getNkName().get(0).getNameRepresentationCode());
        Assertions.assertNull( nextKin.getNkName().get(0).getNameContext().getIdentifier());
        Assertions.assertNull( nextKin.getNkName().get(0).getNameContext().getText());
        Assertions.assertNull( nextKin.getNkName().get(0).getNameContext().getNameOfCodingSystem());
        Assertions.assertNull( nextKin.getNkName().get(0).getNameContext().getAlternateIdentifier());
        Assertions.assertNull( nextKin.getNkName().get(0).getNameContext().getAlternateText());
        Assertions.assertNull( nextKin.getNkName().get(0).getNameContext().getNameOfAlternateCodingSystem());
        Assertions.assertNull( nextKin.getNkName().get(0).getProfessionalSuffix());
        Assertions.assertEquals("20230615", nextKin.getStartDate());
        Assertions.assertEquals("20230615", nextKin.getEndDate());
        Assertions.assertEquals("20230615", nextKin.getNextOfKinAssociatedPartiesJobTitle());
        Assertions.assertEquals("20230615", nextKin.getAdministrativeSex());
        Assertions.assertEquals("20230615", nextKin.getLivingArrangement());
        Assertions.assertEquals("20230615", nextKin.getProtectionIndicator());
        Assertions.assertEquals("20230615", nextKin.getStudentIndicator());
        Assertions.assertEquals("20230615", nextKin.getJobStatus());
        Assertions.assertEquals("20230615", nextKin.getHandicap());
        Assertions.assertEquals("20230615", nextKin.getContactPersonSocialSecurityNumber());
        Assertions.assertEquals("20230615", nextKin.getNextOfKinBirthPlace());
        Assertions.assertEquals("20230615", nextKin.getVipIndicator());
        //endregion

        Assertions.assertEquals("20230615",oru.getContinuationPointer().getContinuationPointer());
        Assertions.assertEquals("20230615",oru.getContinuationPointer().getContinuationStyle());

        //region HEADER
        Assertions.assertEquals("|",oru.getMessageHeader().getFieldSeparator());
        Assertions.assertEquals("^~\\&",oru.getMessageHeader().getEncodingCharacters());
        Assertions.assertEquals("20230615",oru.getMessageHeader().getSecurity());
        Assertions.assertEquals("123456789",oru.getMessageHeader().getMessageControlId());

        Assertions.assertEquals("20230615",oru.getMessageHeader().getSequenceNumber());
        Assertions.assertEquals("20230615",oru.getMessageHeader().getContinuationPointer());
        Assertions.assertEquals("20230615",oru.getMessageHeader().getAcceptAckType());
        Assertions.assertEquals("20230615",oru.getMessageHeader().getApplicationAckType());
        Assertions.assertEquals("20230615",oru.getMessageHeader().getCountryCode());
        Assertions.assertEquals("20230615",oru.getMessageHeader().getAlternateCharacterSetHandlingScheme());
        //endregion

        Assertions.assertEquals("20230615",oru.getSoftwareSegment().get(0).getSoftwareCertifiedVersionOrReleaseNumber());
        Assertions.assertEquals("20230615",oru.getSoftwareSegment().get(0).getSoftwareProductName());
        Assertions.assertEquals("20230615",oru.getSoftwareSegment().get(0).getSoftwareBinaryId());
        Assertions.assertEquals("20230615",oru.getSoftwareSegment().get(0).getSoftwareProductInformation());

        Assertions.assertEquals("1",oru.getPatientResult().get(0).getPatient().getNoteAndComment().get(0).getSetIdNte());
        Assertions.assertEquals("20230615",oru.getPatientResult().get(0).getPatient().getNoteAndComment().get(0).getSourceOfComment());


        // Code coverage for setter
        oru.getContinuationPointer().setContinuationPointer("AA");
        oru.getContinuationPointer().setContinuationStyle("AA");
        Assertions.assertEquals("AA",oru.getContinuationPointer().getContinuationPointer());
        Assertions.assertEquals("AA",oru.getContinuationPointer().getContinuationStyle());

        oru.getPatientResult().get(0).getPatient().getNoteAndComment().get(0).setSetIdNte("3");
        oru.getPatientResult().get(0).getPatient().getNoteAndComment().get(0).setSourceOfComment("AA");
        oru.getPatientResult().get(0).getPatient().getNoteAndComment().get(0).setComment(new ArrayList<>());
        oru.getPatientResult().get(0).getPatient().getNoteAndComment().get(0).setCommentType(new Ce());
        Assertions.assertEquals("3",oru.getPatientResult().get(0).getPatient().getNoteAndComment().get(0).getSetIdNte());
        Assertions.assertEquals("AA",oru.getPatientResult().get(0).getPatient().getNoteAndComment().get(0).getSourceOfComment());


    }

    @Test
    public void hl7StringParserWith231_ReturnValidMessage_RandomV1() throws  DiHL7Exception {
        var result = target.hl7StringParser(randomGenerated231WithDataInAllFieldV1);
        Gson gson = new Gson();
        var test = gson.toJson(result);
        Assertions.assertEquals("R01", result.getEventTrigger());
    }

    @Test
    public void hl7MessageStringValidation_carrier() throws DiHL7Exception {
        String msg = "test\r";
        var result = target.hl7StringValidator(msg);
        Assertions.assertNotNull(result);
    }

    @Test
    public void hl7MessageStringValidation_carrierAndNewLine() throws DiHL7Exception {
        String msg = "test\n\r";
        var result = target.hl7StringValidator(msg);
        Assertions.assertNotNull(result);
    }

    @Test
    public void hl7MessageStringValidation_newLine() throws DiHL7Exception {
        String msg = "test\n";
        var result = target.hl7StringValidator(msg);
        Assertions.assertNotNull(result);
    }

    @Test
    public void hl7MessageStringValidation_doubleSlashNewLine() throws DiHL7Exception {
        String msg = "test\\n";
        var result = target.hl7StringValidator(msg);
        Assertions.assertNotNull(result);
    }

    @Test
    public void hl7MessageStringValidation_doubleSlashCarrier() throws DiHL7Exception {
        String msg = "test\\r";
        var result = target.hl7StringValidator(msg);
        Assertions.assertNotNull(result);
    }



}
