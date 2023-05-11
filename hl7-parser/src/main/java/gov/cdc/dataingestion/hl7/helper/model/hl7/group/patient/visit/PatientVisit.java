package gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient.visit;
import static gov.cdc.dataingestion.hl7.helper.helper.modelListHelper.*;

import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PatientVisit {
    String setIdPv1;
    String patientClass;
    Pl AssignPatientLocation = new Pl();
    String admissionType;
    Cx preadmitNumber = new Cx();
    Pl priorPatientLocation = new Pl();
    List<Xcn> attendingDoctor = new ArrayList<>();
    List<Xcn> referringDoctor = new ArrayList<>();
    List<Xcn> consultingDoctor = new ArrayList<>();
    String hospitalService;
    Pl temporaryLocation = new Pl();
    String preadmitTestIndicator;
    String reAdmissionIndicator;
    String admitSource;
    List<String> ambulatoryStatus = new ArrayList<>();
    String vipStatus;
    List<Xcn> admittingDoctor = new ArrayList<>();
    String patientType;
    Cx visitNumber = new Cx();
    List<Fc> financialClass = new ArrayList<>();
    String chargePriceIndicator;
    String courtesyCode;
    String creditRating;
    List<String> contractRole = new ArrayList<>();
    List<String> contractEffectiveDate = new ArrayList<>();
    List<String> contractAmount = new ArrayList<>();
    List<String> contractPeriod = new ArrayList<>();
    String interestCode;
    String transferToBadDebtCode;
    String transferToBadDebtDate;
    String badDebtAgencyCode;
    String badDebtTransferAmount;
    String badDebtRecoveryAmount;
    String deleteAccountIndicator;
    String deleteAccountDate;
    String dischargeDisposition;
    Dld dischargedToLocation = new Dld();
    Ce dietType = new Ce();
    String servicingFacility;
    String bedStatus;
    String accountStatus;
    Pl pendingLocation = new Pl();
    Pl priorTemporaryLocation = new Pl();
    Ts admitDateTime = new Ts();
    List<Ts> dischargeDateTime = new ArrayList<>();
    String currentPatientBalance;
    String totalCharge;
    String totalAdjustment;
    String totalPayment;
    Cx alternateVisitId = new Cx();
    String visitIndicator;
    List<Xcn> otherHealthcareProvider = new ArrayList<>();

    public PatientVisit() {

    }

    public PatientVisit(ca.uhn.hl7v2.model.v251.segment.PV1 pv1) {
        this.setIdPv1 = pv1.getSetIDPV1().getValue();
        this.patientClass = pv1.getPatientClass().getValue();
        this.AssignPatientLocation = new Pl(pv1.getAssignedPatientLocation());
        this.admissionType = pv1.getAdmissionType().getValue();
        this.preadmitNumber = new Cx(pv1.getPreadmitNumber());
        this.priorPatientLocation = new Pl(pv1.getPriorPatientLocation());
        this.attendingDoctor = GetXcnList(pv1.getAttendingDoctor());
        this.referringDoctor = GetXcnList(pv1.getReferringDoctor());
        this.consultingDoctor = GetXcnList(pv1.getConsultingDoctor());
        this.hospitalService = pv1.getHospitalService().getValue();
        this.temporaryLocation = new Pl(pv1.getTemporaryLocation());
        this.preadmitTestIndicator = pv1.getPreadmitTestIndicator().getValue();
        this.reAdmissionIndicator = pv1.getReAdmissionIndicator().getValue();
        this.admitSource = pv1.getAdmitSource().getValue();
        this.ambulatoryStatus = GetIsStringList(pv1.getAmbulatoryStatus());
        this.vipStatus = pv1.getVisitIndicator().getValue();
        this.admittingDoctor = GetXcnList(pv1.getAdmittingDoctor());
        this.patientType = pv1.getPatientType().getValue();
        this.visitNumber = new Cx(pv1.getVisitNumber());
        this.financialClass = GetFcList(pv1.getFinancialClass());
        this.chargePriceIndicator = pv1.getChargePriceIndicator().getValue();
        this.courtesyCode = pv1.getCourtesyCode().getValue();
        this.creditRating = pv1.getCreditRating().getValue();
        this.contractRole = GetIsStringList(pv1.getContractCode());
        this.contractEffectiveDate = GetDtStringList(pv1.getContractEffectiveDate());
        this.contractAmount = GetNmStringList(pv1.getContractAmount());
        this.contractPeriod = GetNmStringList(pv1.getContractPeriod());
        this.interestCode = pv1.getInterestCode().getValue();
        this.transferToBadDebtCode = pv1.getTransferToBadDebtCode().getValue();
        this.transferToBadDebtDate = pv1.getTransferToBadDebtDate().getValue();
        this.badDebtAgencyCode = pv1.getBadDebtAgencyCode().getValue();
        this.badDebtTransferAmount = pv1.getBadDebtTransferAmount().getValue();
        this.badDebtRecoveryAmount = pv1.getBadDebtRecoveryAmount().getValue();
        this.deleteAccountIndicator = pv1.getDeleteAccountIndicator().getValue();
        this.deleteAccountDate = pv1.getDeleteAccountDate().getValue();
        this.dischargeDisposition = pv1.getDischargeDisposition().getValue();
        this.dischargedToLocation = new Dld(pv1.getDischargedToLocation());
        this.dietType = new Ce(pv1.getDietType());
        this.servicingFacility = pv1.getServicingFacility().getValue();
        this.bedStatus = pv1.getBedStatus().getValue();
        this.accountStatus = pv1.getAccountStatus().getValue();
        this.pendingLocation = new Pl(pv1.getPendingLocation());
        this.priorTemporaryLocation = new Pl(pv1.getPriorPatientLocation());
        this.admitDateTime = new Ts(pv1.getAdmitDateTime());
        this.dischargeDateTime = GetTsList(pv1.getDischargeDateTime());
        this.currentPatientBalance = pv1.getCurrentPatientBalance().getValue();
        this.totalCharge = pv1.getTotalCharges().getValue();
        this.totalAdjustment = pv1.getTotalAdjustments().getValue();
        this.totalPayment = pv1.getTotalPayments().getValue();
        this.alternateVisitId = new Cx(pv1.getAlternateVisitID());
        this.visitIndicator = pv1.getVisitIndicator().getValue();
        this.otherHealthcareProvider = GetXcnList(pv1.getOtherHealthcareProvider());
    }
}
