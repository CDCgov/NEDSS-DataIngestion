package gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient;

import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Ce;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Cx;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Xcn;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Xon;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static gov.cdc.dataingestion.hl7.helper.helper.modelListHelper.*;

@Getter
@Setter
public class PatientAdditionalDemographic {
    List<String> livingDependency = new ArrayList<>();
    String livingArrangement;
    List<Xon> patientPrimaryFacility = new ArrayList<>();
    List<Xcn> patientPrimaryCareProviderNameAndIdNo = new ArrayList<>();
    String studentIndicator;
    String handiCap;
    String livingWillCode;
    String organDonorCode;
    String separateBill;
    List<Cx> duplicatePatient = new ArrayList<>();
    Ce publicityCode = new Ce();
    String protectionIndicator;
    String protectionIndicatorEffectiveDate;
    List<Xon> placeOfWorship = new ArrayList<>();
    List<Ce> advanceDirectiveCode = new ArrayList<>();
    String immunizationRegistryStatus;
    String immunizationRegistryStatusEffectiveDate;
    String publicityCodeEffectiveDate;
    String militaryBranch;
    String militaryRank;
    String militaryStatus;

    public PatientAdditionalDemographic() {

    }

    public PatientAdditionalDemographic(ca.uhn.hl7v2.model.v251.segment.PD1 pd1) {
        this.livingDependency = GetIsStringList(pd1.getLivingDependency());
        this.livingArrangement = pd1.getLivingArrangement().getValue();
        this.patientPrimaryFacility = GetXonList(pd1.getPatientPrimaryFacility());
        this.patientPrimaryCareProviderNameAndIdNo = GetXcnList(pd1.getPatientPrimaryCareProviderNameIDNo());
        this.studentIndicator = pd1.getStudentIndicator().getValue();
        this.handiCap = pd1.getHandicap().getValue();
        this.livingWillCode = pd1.getLivingWillCode().getValue();
        this.organDonorCode = pd1.getOrganDonorCode().getValue();
        this.separateBill = pd1.getSeparateBill().getValue();
        this.duplicatePatient = GetCxList(pd1.getDuplicatePatient());
        this.publicityCode = new Ce(pd1.getPublicityCode());
        this.protectionIndicator = pd1.getProtectionIndicator().getValue();
        this.protectionIndicatorEffectiveDate = pd1.getProtectionIndicatorEffectiveDate().getValue();
        this.placeOfWorship = GetXonList(pd1.getPlaceOfWorship());
        this.advanceDirectiveCode = GetCeList(pd1.getAdvanceDirectiveCode());
        this.immunizationRegistryStatus = pd1.getImmunizationRegistryStatus().getValue();
        this.immunizationRegistryStatusEffectiveDate = pd1.getImmunizationRegistryStatusEffectiveDate().getValue();
        this.publicityCodeEffectiveDate = pd1.getPublicityCodeEffectiveDate().getValue();
        this.militaryBranch = pd1.getMilitaryBranch().getValue();
        this.militaryRank = pd1.getMilitaryRankGrade().getValue();
        this.militaryStatus = pd1.getMilitaryStatus().getValue();
    }
}
