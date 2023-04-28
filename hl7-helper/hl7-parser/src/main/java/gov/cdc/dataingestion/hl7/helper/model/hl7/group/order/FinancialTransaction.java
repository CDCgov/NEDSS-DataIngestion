package gov.cdc.dataingestion.hl7.helper.model.hl7.group.order;

import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.*;
import lombok.Getter;
import static gov.cdc.dataingestion.hl7.helper.helper.modelListHelper.*;

import java.util.List;

@Getter
public class FinancialTransaction {
    String setIdFT1;
    String transactionId;
    String transactionBatchId;
    Dr transactionDate;
    Ts transactionPostingDate;
    String transactionType;
    Ce transactionCode;
    String transactionDescription;
    String transactionDescriptionAlter;
    String transactionQuantity;
    Cp transactionAmountExt;
    Cp transactionAmountUnit;
    Ce departmentCode;
    Ce insurancePlanId;
    Cp insuranceAmount;
    Pl assignedPatientLocation;
    String feeSchedule;
    String patientType;
    List<Ce> diagnosisCode;
    List<Xcn> performedByCode;
    List<Xcn> orderedByCode;
    Cp unitCost;
    Ei fillerOrderNumber;
    List<Xcn> enteredByCode;
    Ce procedureCode;
    List<Ce> procedureCodeModifier;
    Ce advancedBeneficiaryNoticeCode;
    Cwe medicallyNecessaryDuplicateProcedureReason;
    Cne ndcCode;
    Cx paymentReferenceId;
    List<String> transactionReferenceKey;

    public FinancialTransaction(ca.uhn.hl7v2.model.v251.segment.FT1 ft1) {
        this.setIdFT1 = ft1.getSetIDFT1().getValue();
        this.transactionId = ft1.getTransactionID().getValue();
        this.transactionBatchId = ft1.getTransactionBatchID().getValue();
        this.transactionDate = new Dr(ft1.getTransactionDate());
        this.transactionPostingDate = new Ts(ft1.getTransactionPostingDate());
        this.transactionType = ft1.getTransactionType().getValue();
        this.transactionCode = new Ce(ft1.getTransactionCode());
        this.transactionDescription = ft1.getTransactionDescription().getValue();
        this.transactionDescriptionAlter = ft1.getTransactionDescriptionAlt().getValue();
        this.transactionQuantity = ft1.getTransactionQuantity().getValue();
        this.transactionAmountExt = new Cp(ft1.getTransactionAmountExtended());
        this.transactionAmountUnit = new Cp(ft1.getTransactionAmountUnit());
        this.departmentCode = new Ce(ft1.getDepartmentCode());
        this.insurancePlanId = new Ce(ft1.getInsurancePlanID());
        this.insuranceAmount = new Cp(ft1.getInsuranceAmount());
        this.assignedPatientLocation = new Pl(ft1.getAssignedPatientLocation());
        this.feeSchedule = ft1.getFeeSchedule().getValue();
        this.patientType = ft1.getPatientType().getValue();
        this.diagnosisCode = GetCeList(ft1.getDiagnosisCodeFT1());
        this.performedByCode = GetXcnList(ft1.getPerformedByCode());
        this.orderedByCode = GetXcnList(ft1.getOrderedByCode());
        this.unitCost = new Cp(ft1.getUnitCost());
        this.fillerOrderNumber = new Ei(ft1.getFillerOrderNumber());
        this.enteredByCode = GetXcnList(ft1.getEnteredByCode());
        this.procedureCode = new Ce(ft1.getProcedureCode());
        this.procedureCodeModifier = GetCeList(ft1.getProcedureCodeModifier());
        this.advancedBeneficiaryNoticeCode = new Ce(ft1.getAdvancedBeneficiaryNoticeCode());
        this.medicallyNecessaryDuplicateProcedureReason = new Cwe(ft1.getMedicallyNecessaryDuplicateProcedureReason());
        this.ndcCode = new Cne(ft1.getNDCCode());
        this.paymentReferenceId = new Cx(ft1.getPaymentReferenceID());
        this.transactionReferenceKey = GetSiStringList(ft1.getTransactionReferenceKey());
    }
}
