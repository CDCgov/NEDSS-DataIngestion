package gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient;

import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.*;
import lombok.Getter;

import java.util.List;

import static gov.cdc.dataingestion.hl7.helper.helper.modelListHelper.*;

@Getter
public class NextOfKin {

    public NextOfKin(ca.uhn.hl7v2.model.v251.segment.NK1 nk1) {
        this.setIdNK1 = nk1.getSetIDNK1().getValue();
        this.nkName = GetXpnList(nk1.getNK1Name());
        this.relationship = new Ce(nk1.getRelationship());
        this.address = GetXadList(nk1.getAddress());
        this.phoneNumber = GetXtnList(nk1.getPhoneNumber());
        this.businessPhoneNumber = GetXtnList(nk1.getBusinessPhoneNumber());
        this.contactRole = new Ce(nk1.getContactRole());
        this.startDate = nk1.getStartDate().getValue();
        this.endDate = nk1.getEndDate().getValue();
        this.nextOfKinAssociatedPartiesJobTitle = nk1.getNextOfKinAssociatedPartiesJobTitle().getValue();
        this.nextOfKinAssociatedPartiesJobCode = new Jcc(nk1.getNextOfKinAssociatedPartiesJobCodeClass());
        this.nextOfKinAssociatedPartiesEmployee = new Cx(nk1.getNextOfKinAssociatedPartiesEmployeeNumber());
        this.organizationNameNk1 = GetXonList(nk1.getOrganizationNameNK1());
        this.martialStatus = new Ce(nk1.getMaritalStatus());
        this.administrativeSex = nk1.getAdministrativeSex().getValue();
        this.dateTimeOfBirth = new Ts(nk1.getDateTimeOfBirth());
        this.livingDependency = GetIsStringList(nk1.getLivingDependency());
        this.ambulatoryStatus = GetIsStringList(nk1.getAmbulatoryStatus());
        this.citizenship = GetCeList(nk1.getCitizenship());
        this.primaryLanguage = new Ce(nk1.getPrimaryLanguage());
        this.livingArrangement = nk1.getLivingArrangement().getValue();
        this.publicityCode = new Ce(nk1.getPublicityCode());
        this.protectionIndicator = nk1.getProtectionIndicator().getValue();
        this.studentIndicator = nk1.getStudentIndicator().getValue();
        this.religion = new Ce(nk1.getReligion());
        this.motherMaidenName = GetXpnList(nk1.getMotherSMaidenName());
        this.nationality = new Ce(nk1.getNationality());
        this.ethnicGroup = GetCeList(nk1.getEthnicGroup());
        this.contactReason = GetCeList(nk1.getContactReason());
        this.contactPersonName = GetXpnList(nk1.getContactPersonSName());
        this.contactPersonTelephoneNumber = GetXtnList(nk1.getContactPersonSTelephoneNumber());
        this.contactPersonAddress = GetXadList(nk1.getContactPersonSAddress());
        this.nextOfKinAssociatedPartyIdentifier = GetCxList(nk1.getNextOfKinAssociatedPartySIdentifiers());
        this.jobStatus = nk1.getJobStatus().getValue();
        this.race = GetCeList(nk1.getRace());
        this.handicap = nk1.getHandicap().getValue();
        this.contactPersonSocialSecurityNumber = nk1.getContactPersonSocialSecurityNumber().getValue();
        this.nextOfKinBirthPlace = nk1.getNextOfKinBirthPlace().getValue();
        this.vipIndicator = nk1.getVIPIndicator().getValue();
    }

    String setIdNK1;
    List<Xpn> nkName;
    Ce relationship;
    List<Xad> address;
    List<Xtn> phoneNumber;
    List<Xtn> businessPhoneNumber;
    Ce contactRole;
    String startDate;
    String endDate;
    String nextOfKinAssociatedPartiesJobTitle;
    Jcc nextOfKinAssociatedPartiesJobCode;
    Cx nextOfKinAssociatedPartiesEmployee;
    List<Xon> organizationNameNk1;
    Ce martialStatus;
    String administrativeSex;
    Ts dateTimeOfBirth;
    List<String> livingDependency;
    List<String> ambulatoryStatus;
    List<Ce> citizenship;
    Ce primaryLanguage;
    String livingArrangement;
    Ce publicityCode;
    String protectionIndicator;
    String studentIndicator;
    Ce religion;
    List<Xpn> motherMaidenName;
    Ce nationality;
    List<Ce> ethnicGroup;
    List<Ce> contactReason;
    List<Xpn> contactPersonName;
    List<Xtn> contactPersonTelephoneNumber;
    List<Xad> contactPersonAddress;
    List<Cx> nextOfKinAssociatedPartyIdentifier;
    String jobStatus;
    List<Ce> race;
    String handicap;
    String contactPersonSocialSecurityNumber;
    String nextOfKinBirthPlace;
    String vipIndicator;
}
