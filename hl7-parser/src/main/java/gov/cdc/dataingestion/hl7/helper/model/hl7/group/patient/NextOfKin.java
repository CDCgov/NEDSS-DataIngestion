package gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient;

import gov.cdc.dataingestion.hl7.helper.model.hl7.message_data_type.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static gov.cdc.dataingestion.hl7.helper.helper.ModelListHelper.*;

@Getter
@Setter
public class NextOfKin {

    public NextOfKin(ca.uhn.hl7v2.model.v251.segment.NK1 nk1) {
        this.setIdNK1 = nk1.getSetIDNK1().getValue();
        this.nkName = getXpnList(nk1.getNK1Name());
        this.relationship = new Ce(nk1.getRelationship());
        this.address = getXadList(nk1.getAddress());
        this.phoneNumber = getXtnList(nk1.getPhoneNumber());
        this.businessPhoneNumber = getXtnList(nk1.getBusinessPhoneNumber());
        this.contactRole = new Ce(nk1.getContactRole());
        this.startDate = nk1.getStartDate().getValue();
        this.endDate = nk1.getEndDate().getValue();
        this.nextOfKinAssociatedPartiesJobTitle = nk1.getNextOfKinAssociatedPartiesJobTitle().getValue();
        this.nextOfKinAssociatedPartiesJobCode = new Jcc(nk1.getNextOfKinAssociatedPartiesJobCodeClass());
        this.nextOfKinAssociatedPartiesEmployee = new Cx(nk1.getNextOfKinAssociatedPartiesEmployeeNumber());
        this.organizationNameNk1 = getXonList(nk1.getOrganizationNameNK1());
        this.martialStatus = new Ce(nk1.getMaritalStatus());
        this.administrativeSex = nk1.getAdministrativeSex().getValue();
        this.dateTimeOfBirth = new Ts(nk1.getDateTimeOfBirth());
        this.livingDependency = getIsStringList(nk1.getLivingDependency());
        this.ambulatoryStatus = getIsStringList(nk1.getAmbulatoryStatus());
        this.citizenship = getCeList(nk1.getCitizenship());
        this.primaryLanguage = new Ce(nk1.getPrimaryLanguage());
        this.livingArrangement = nk1.getLivingArrangement().getValue();
        this.publicityCode = new Ce(nk1.getPublicityCode());
        this.protectionIndicator = nk1.getProtectionIndicator().getValue();
        this.studentIndicator = nk1.getStudentIndicator().getValue();
        this.religion = new Ce(nk1.getReligion());
        this.motherMaidenName = getXpnList(nk1.getMotherSMaidenName());
        this.nationality = new Ce(nk1.getNationality());
        this.ethnicGroup = getCeList(nk1.getEthnicGroup());
        this.contactReason = getCeList(nk1.getContactReason());
        this.contactPersonName = getXpnList(nk1.getContactPersonSName());
        this.contactPersonTelephoneNumber = getXtnList(nk1.getContactPersonSTelephoneNumber());
        this.contactPersonAddress = getXadList(nk1.getContactPersonSAddress());
        this.nextOfKinAssociatedPartyIdentifier = getCxList(nk1.getNextOfKinAssociatedPartySIdentifiers());
        this.jobStatus = nk1.getJobStatus().getValue();
        this.race = getCeList(nk1.getRace());
        this.handicap = nk1.getHandicap().getValue();
        this.contactPersonSocialSecurityNumber = nk1.getContactPersonSocialSecurityNumber().getValue();
        this.nextOfKinBirthPlace = nk1.getNextOfKinBirthPlace().getValue();
        this.vipIndicator = nk1.getVIPIndicator().getValue();
    }

    public NextOfKin(){

    }
    String setIdNK1;
    List<Xpn> nkName = new ArrayList<>();
    Ce relationship = new Ce();
    List<Xad> address = new ArrayList<>();
    List<Xtn> phoneNumber = new ArrayList<>();
    List<Xtn> businessPhoneNumber = new ArrayList<>();
    Ce contactRole = new Ce();
    String startDate;
    String endDate;
    String nextOfKinAssociatedPartiesJobTitle;
    Jcc nextOfKinAssociatedPartiesJobCode = new Jcc();
    Cx nextOfKinAssociatedPartiesEmployee = new Cx();
    List<Xon> organizationNameNk1 = new ArrayList<>();
    Ce martialStatus = new Ce();
    String administrativeSex;
    Ts dateTimeOfBirth = new Ts();
    List<String> livingDependency = new ArrayList<>();
    List<String> ambulatoryStatus = new ArrayList<>();
    List<Ce> citizenship = new ArrayList<>();
    Ce primaryLanguage = new Ce();
    String livingArrangement;
    Ce publicityCode =new Ce();
    String protectionIndicator;
    String studentIndicator;
    Ce religion = new Ce();
    List<Xpn> motherMaidenName = new ArrayList<>();
    Ce nationality = new Ce();
    List<Ce> ethnicGroup = new ArrayList<>();
    List<Ce> contactReason = new ArrayList<>();
    List<Xpn> contactPersonName = new ArrayList<>();
    List<Xtn> contactPersonTelephoneNumber = new ArrayList<>();
    List<Xad> contactPersonAddress = new ArrayList<>();
    List<Cx> nextOfKinAssociatedPartyIdentifier = new ArrayList<>();
    String jobStatus;
    List<Ce> race = new ArrayList<>();
    String handicap;
    String contactPersonSocialSecurityNumber;
    String nextOfKinBirthPlace;
    String vipIndicator;
}
