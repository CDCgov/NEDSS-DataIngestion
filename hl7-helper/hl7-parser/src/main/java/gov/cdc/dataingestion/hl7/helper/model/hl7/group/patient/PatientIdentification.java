package gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient;
import ca.uhn.hl7v2.model.v251.datatype.*;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.*;
import lombok.Getter;
import java.util.List;

import static gov.cdc.dataingestion.hl7.helper.helper.modelListHelper.*;

@Getter
public class PatientIdentification {
    String setPid;
    Cx patientId;
    List<Cx> patientIdentifierList;
    List<Cx> alternativePatientId;
    List<Xpn> patientName;
    List<Xpn> motherMaidenName;
    Ts dateTimeOfBirth;
    String administrativeSex;
    List<Xpn> patientAlias;
    List<Ce> race;
    List<Xad> patientAddress;
    String countyCode;
    List<Xtn> phoneNumberHome;
    List<Xtn> phoneNumberBusiness;
    Ce primaryLanguage;
    Ce martialStatus;
    Ce religion;
    Cx patientAccountNumber;
    String ssnNumberPatient;
    Dln driverLicenseNumberPatient;
    List<Cx> motherIdentifier;
    List<Ce> ethnicGroup;
    String birthPlace;
    String multipleBirthIndicator;
    String birthOrder;
    List<Ce> citizenship;
    Ce veteranStatus;
    Ce nationality;
    Ts patientDeathDateAndTime;
    String patientDeathIndicator;
    String identityUnknownIndicator;
    List<String> identityReliabilityCode;
    Ts lastUpdateDateTime;
    Hd lastUpdateFacility;
    Ce speciesCode;
    Ce breedCode;
    String strain;
    Ce productionClassCode;
    List<Cwe> tribalCitizenship;
    public PatientIdentification(ca.uhn.hl7v2.model.v251.segment.PID pid) {
        this.setPid = pid.getSetIDPID().getValue();
        this.patientId = new Cx(pid.getPatientID());
        this.patientIdentifierList = GetCxList(pid.getPatientIdentifierList());
        this.alternativePatientId = GetCxList(pid.getAlternatePatientIDPID());
        this.patientName = GetXpnList(pid.getPatientName());
        this.motherMaidenName = GetXpnList(pid.getMotherSMaidenName());
        this.dateTimeOfBirth = new Ts(pid.getDateTimeOfBirth());
        this.administrativeSex = pid.getAdministrativeSex().getValue();
        this.patientAlias = GetXpnList(pid.getPatientAlias());
        this.race = GetCeList(pid.getRace());
        this.patientAddress = GetXadList(pid.getPatientAddress());
        this.countyCode = pid.getCountyCode().getValue();
        this.phoneNumberHome = GetXtnList(pid.getPhoneNumberHome());
        this.phoneNumberBusiness = GetXtnList(pid.getPhoneNumberBusiness());
        this.primaryLanguage = new Ce(pid.getPrimaryLanguage());
        this.martialStatus = new Ce(pid.getMaritalStatus());
        this.religion = new Ce(pid.getReligion());
        this.patientAccountNumber = new Cx(pid.getPatientAccountNumber());
        this.ssnNumberPatient = pid.getSSNNumberPatient().getValue();
        this.driverLicenseNumberPatient = new Dln(pid.getDriverSLicenseNumberPatient());
        this.motherIdentifier = GetCxList(pid.getMotherSIdentifier());
        this.ethnicGroup = GetCeList(pid.getEthnicGroup());
        this.birthPlace = pid.getBirthPlace().getValue();
        this.multipleBirthIndicator = pid.getMultipleBirthIndicator().getValue();
        this.birthOrder = pid.getBirthOrder().getValue();
        this.citizenship = GetCeList(pid.getCitizenship());
        this.veteranStatus = new Ce(pid.getVeteransMilitaryStatus());
        this.nationality = new Ce(pid.getNationality());
        this.patientDeathDateAndTime = new Ts(pid.getPatientDeathDateAndTime());
        this.patientDeathIndicator = pid.getPatientDeathIndicator().getValue();
        this.identityUnknownIndicator = pid.getIdentityUnknownIndicator().getValue();
        this.identityReliabilityCode = GetIsStringList(pid.getIdentityReliabilityCode());
        this.lastUpdateDateTime = new Ts(pid.getLastUpdateDateTime());
        this.lastUpdateFacility = new Hd(pid.getLastUpdateFacility());
        this.speciesCode = new Ce(pid.getSpeciesCode());
        this.breedCode = new Ce(pid.getBreedCode());
        this.strain = pid.getStrain().getValue();
        this.productionClassCode = new Ce(pid.getProductionClassCode());
        this.tribalCitizenship = GetCweList(pid.getTribalCitizenship());
    }


}
