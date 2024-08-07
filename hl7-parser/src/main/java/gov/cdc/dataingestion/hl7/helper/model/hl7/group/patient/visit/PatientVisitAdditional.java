package gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient.visit;

import gov.cdc.dataingestion.hl7.helper.model.hl7.message_data_type.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static gov.cdc.dataingestion.hl7.helper.helper.ModelListHelper.*;

@Getter
@Setter
public class PatientVisitAdditional {
    Pl priorPendingLocation = new Pl();
    Ce accommodationCode = new Ce();
    Ce admitReason = new Ce();
    Ce transferReason = new Ce();
    List<String> patientValuables = new ArrayList<>();
    String patientValuablesLocation;
    List<String> visitUserCode = new ArrayList<>();
    Ts expectedAdmitDateTime = new Ts();
    Ts expectedDischargeDateTime = new Ts();
    String estimateLengthOfInpatientDay;
    String actualLengthOfInpatientDay;
    String visitDescription;
    List<Xcn> referralSourceCode = new ArrayList<>();
    String previousServiceDate;
    String employmentIllnessRelatedIndicator;
    String purgeStatusCode;
    String purgeStatusDate;
    String specialProgramCode;
    String retentionIndicator;
    String expectedNumberOfInsurancePlans;
    String visitPublicityCode;
    String visitProtectionIndicator;
    List<Xon> clinicOrganizationName = new ArrayList<>();
    String patientStatusCode;
    String visitPriorityCode;
    String previousTreatmentCode;
    String expectedDischargeDisposition;
    String signatureOnFileDate;
    String firstSimilarIllnessDate;
    Ce patientChargeAdjustmentCode = new Ce();
    String recurringServiceCode;
    String billingMediaCode;
    Ts expectedSurgeryDateTime = new Ts();
    String militaryPartnershipCode;
    String militaryNonAvailCode;
    String newbornBabyIndicator;
    String babyDetainedIndicator;
    Ce modeOfArrivalCode = new Ce();
    List<Ce> recreationalDrugUseCode = new ArrayList<>();
    Ce admissionLevelOfCareCode = new Ce();
    List<Ce> precautionCode = new ArrayList<>();
    Ce patientConditionCode = new Ce();
    String livingWillCode;
    String organDonorCode;
    List<Ce> advanceDirectiveCode = new ArrayList<>();
    String patientStatusEffectiveDate;
    Ts expectedLoaReturnDateTime = new Ts();
    Ts expectedPreAdmissionTestingDateTime = new Ts();
    List<String> notifyClergyCode = new ArrayList<>();

    public PatientVisitAdditional() {

    }

    public PatientVisitAdditional(ca.uhn.hl7v2.model.v251.segment.PV2 pv2) {
        this.priorPendingLocation = new Pl(pv2.getPriorPendingLocation());
        this.accommodationCode = new Ce(pv2.getAccommodationCode());
        this.admitReason = new Ce(pv2.getAdmitReason());
        this.transferReason = new Ce(pv2.getTransferReason());
        this.patientValuables = getStStringList(pv2.getPatientValuables());
        this.patientValuablesLocation = pv2.getPatientValuablesLocation().getValue();
        this.visitUserCode = getIsStringList(pv2.getVisitUserCode());
        this.expectedAdmitDateTime = new Ts(pv2.getExpectedAdmitDateTime());
        this.expectedDischargeDateTime = new Ts(pv2.getExpectedDischargeDateTime());
        this.estimateLengthOfInpatientDay = pv2.getEstimatedLengthOfInpatientStay().getValue();
        this.actualLengthOfInpatientDay = pv2.getActualLengthOfInpatientStay().getValue();
        this.visitDescription = pv2.getVisitDescription().getValue();
        this.referralSourceCode = getXcnList(pv2.getReferralSourceCode());
        this.previousServiceDate = pv2.getPreviousServiceDate().getValue();
        this.employmentIllnessRelatedIndicator = pv2.getEmploymentIllnessRelatedIndicator().getValue();
        this.purgeStatusCode = pv2.getPurgeStatusCode().getValue();
        this.purgeStatusDate = pv2.getPurgeStatusDate().getValue();
        this.specialProgramCode = pv2.getSpecialProgramCode().getValue();
        this.retentionIndicator = pv2.getRetentionIndicator().getValue();
        this.expectedNumberOfInsurancePlans = pv2.getExpectedNumberOfInsurancePlans().getValue();
        this.visitPublicityCode = pv2.getVisitPublicityCode().getValue();
        this.visitProtectionIndicator = pv2.getVisitProtectionIndicator().getValue();
        this.clinicOrganizationName = getXonList(pv2.getClinicOrganizationName());
        this.patientStatusCode = pv2.getPatientStatusCode().getValue();
        this.visitPriorityCode = pv2.getVisitPriorityCode().getValue();
        this.previousTreatmentCode = pv2.getPreviousTreatmentDate().getValue();
        this.expectedDischargeDisposition = pv2.getExpectedDischargeDisposition().getValue();
        this.signatureOnFileDate = pv2.getSignatureOnFileDate().getValue();
        this.firstSimilarIllnessDate = pv2.getFirstSimilarIllnessDate().getValue();
        this.patientChargeAdjustmentCode = new Ce(pv2.getPatientChargeAdjustmentCode());
        this.recurringServiceCode = pv2.getRecurringServiceCode().getValue();
        this.billingMediaCode = pv2.getBillingMediaCode().getValue();
        this.expectedSurgeryDateTime = new Ts(pv2.getExpectedAdmitDateTime());
        this.militaryPartnershipCode = pv2.getMilitaryPartnershipCode().getValue();
        this.militaryNonAvailCode = pv2.getMilitaryNonAvailabilityCode().getValue();
        this.newbornBabyIndicator = pv2.getNewbornBabyIndicator().getValue();
        this.babyDetainedIndicator = pv2.getBabyDetainedIndicator().getValue();
        this.modeOfArrivalCode = new Ce(pv2.getModeOfArrivalCode());
        this.recreationalDrugUseCode = getCeList(pv2.getRecreationalDrugUseCode());
        this.admissionLevelOfCareCode = new Ce(pv2.getAdmissionLevelOfCareCode());
        this.precautionCode = getCeList(pv2.getPrecautionCode());
        this.patientConditionCode = new Ce(pv2.getPatientConditionCode());
        this.livingWillCode = pv2.getLivingWillCode().getValue();
        this.organDonorCode = pv2.getOrganDonorCode().getValue();
        this.advanceDirectiveCode = getCeList(pv2.getAdvanceDirectiveCode());
        this.patientStatusEffectiveDate = pv2.getPatientStatusEffectiveDate().getValue();
        this.expectedLoaReturnDateTime = new Ts(pv2.getExpectedLOAReturnDateTime());
        this.expectedPreAdmissionTestingDateTime = new Ts(pv2.getExpectedPreAdmissionTestingDateTime());
        this.notifyClergyCode = getIsStringList(pv2.getNotifyClergyCode());
    }
}
