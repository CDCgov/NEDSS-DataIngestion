package gov.cdc.dataingestion.nbs.ecr;

import gov.cdc.dataingestion.nbs.ecr.model.*;
import gov.cdc.dataingestion.nbs.repository.implementation.EcrLookUpRepository;
import gov.cdc.dataingestion.nbs.repository.model.IEcrLookUpRepository;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedCase;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedInterview;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedRecord;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedTreatment;
import gov.cdc.dataingestion.nbs.repository.model.dao.LookUp.PhdcAnswerDao;
import gov.cdc.dataingestion.nbs.repository.model.dao.LookUp.QuestionIdentifierMapDao;
import gov.cdc.dataingestion.nbs.repository.model.dto.*;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.PhdcQuestionLookUpDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.QuestionIdentifierMapDto;
import gov.cdc.nedss.phdc.cda.*;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;

import javax.xml.namespace.QName;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.text.ParseException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static gov.cdc.dataingestion.nbs.ecr.CdaMapStringHelper.GetStringsBeforeCaret;
import static gov.cdc.dataingestion.nbs.ecr.CdaMapStringHelper.GetStringsBeforePipe;

public class CdaMapper {

    private String xmlns = "http://www.w3.org/2001/XMLSchema-instance";
    private String xmlns_prefix1 = "sdtcxmlnamespaceholder";
    private String xmlns_prefix2 = "xsi";
    private String typeRootId = "2.16.840.1.113883.1.3";
    private String idRootId = "2.16.840.1.113883.19";
    private String typeExtension = "POCD_HD000040";
    private String typeAuthorityName = "LR";
    private String realmCode = "US";
    private String code = "55751-2";
    private String codeSystem = "2.16.840.1.113883.6.1";
    private String codeSystemName = "LOINC";

    private IEcrLookUpRepository ecrLookUpRepository = new EcrLookUpRepository();

    public void test(EcrSelectedRecord input) throws XmlException, ParseException {


        POCDMT000040ClinicalDocument1 clinicalDocument = POCDMT000040ClinicalDocument1.Factory.newInstance();

        CS[] realmCodeArray = { CS.Factory.newInstance()};
        clinicalDocument.setRealmCodeArray(realmCodeArray);
        clinicalDocument.getRealmCodeArray(0).setCode(realmCode);

        clinicalDocument.setTypeId(POCDMT000040InfrastructureRootTypeId.Factory.newInstance());
        clinicalDocument.getTypeId().setRoot(typeRootId);
        clinicalDocument.getTypeId().setExtension(typeExtension);


        String inv168 = "";
        Integer nbsInterfaceUid = null;
        String systemName = "";
        Integer versionCtrNbr = null;
        Integer dataMigrationStatus = null;

        if (input.getMsgContainer().getInvLocalId() != null && !input.getMsgContainer().getInvLocalId().isEmpty()) {
            clinicalDocument.setId(II.Factory.newInstance());
            clinicalDocument.getId().setRoot(idRootId);
            clinicalDocument.getId().setExtension(input.getMsgContainer().getInvLocalId());
            clinicalDocument.getId().setAssigningAuthorityName(typeAuthorityName);
            inv168 = input.getMsgContainer().getInvLocalId();
        }

        if (input.getMsgContainer().getOngoingCase() != null &&
            !input.getMsgContainer().getOngoingCase().isEmpty()) {
            clinicalDocument.setSetId(II.Factory.newInstance());
            clinicalDocument.getSetId().setExtension("ONGOING_CASE");
            if (input.getMsgContainer().getOngoingCase().equalsIgnoreCase("yes")) {
                clinicalDocument.getSetId().setDisplayable(true);
            } else {
                clinicalDocument.getSetId().setDisplayable(false);
            }
        }

        if(input.getMsgContainer().getNbsInterfaceUid() != null) {
            nbsInterfaceUid = input.getMsgContainer().getNbsInterfaceUid();
        }

        if(input.getMsgContainer().getReceivingSystem() != null &&
            !input.getMsgContainer().getReceivingSystem().isEmpty()) {
            if(input.getMsgContainer().getReceivingSystem().length() > 0) {
                systemName = input.getMsgContainer().getReceivingSystem();
            } else {
                systemName = "NBS";
            }
        }

        if (input.getMsgContainer().getVersionCtrNbr() != null) {
            versionCtrNbr = input.getMsgContainer().getVersionCtrNbr();
        }

        if(input.getMsgContainer().getDataMigrationStatus() != null) {
            dataMigrationStatus = input.getMsgContainer().getDataMigrationStatus();
        }

        clinicalDocument.setCode(CE.Factory.newInstance());
        clinicalDocument.getCode().setCode(code);
        clinicalDocument.getCode().setCodeSystem(codeSystem);
        clinicalDocument.getCode().setCodeSystemName(codeSystemName);
        clinicalDocument.getCode().setDisplayName("Public Health Case Report - PHRI");
        clinicalDocument.setTitle(ST.Factory.newInstance());

        // This need to be checked
        clinicalDocument.getTitle().setLanguage("Public Health Case Report - Data from Legacy System to CDA");

        clinicalDocument.setEffectiveTime(TS.Factory.newInstance());
        clinicalDocument.getEffectiveTime().setValue(GetCurrentUtcDateTimeInCdaFormat());

        if(versionCtrNbr != null && versionCtrNbr > 0) {
            clinicalDocument.setVersionNumber(INT.Factory.newInstance());
            clinicalDocument.getVersionNumber().setValue(BigInteger.valueOf(versionCtrNbr));
        }

        clinicalDocument.setConfidentialityCode(CE.Factory.newInstance());
        clinicalDocument.getConfidentialityCode().setCode("N");
        clinicalDocument.getConfidentialityCode().setCodeSystem("2.16.840.1.113883.5.25");

        int componentCounter=-1;
        int componentCaseCounter=-1;
        int interviewCounter= 0;
        int treatmentCounter=0;
        int treatmentSectionCounter=0;
        int caseEntryCounter=0;
        int performerCounter=0;
        int patientComponentCounter=-1;
        int performerComponentCounter=0;
        int performerSectionCounter=0;
        int clinicalCounter= 0;
        int performerEntityCounter=0;
        int signsAndSymptomCounter=0;
        int medicalHistoryCounter=0;
        int GenericBatchEntryCounter=0;

        // Set RecordTarget && patient Role
        clinicalDocument.addNewRecordTarget();
        clinicalDocument.getRecordTargetArray(0).addNewPatientRole();

        //region PATIENT
        for(var patient : input.getMsgPatients()) {
            String address1 ="";
            String address2 ="";
            String homeExtn="";
            String name1 = "Patient";
            String PAT_HOME_PHONE_NBR_TXT  ="";
            String PAT_WORK_PHONE_EXTENSION_TXT="";
            String wpNumber="";
            String cellNumber="";
            String PAT_NAME_FIRST_TXT="";
            String PAT_NAME_MIDDLE_TXT="";
            String PAT_NAME_PREFIX_CD="";
            String PAT_NAME_LAST_TXT="";
            String PAT_NAME_SUFFIX_CD="";

            int raceCodeCounter=0;
            int phoneCounter = 0;
            String PAT_RACE_DESC_TXT="";
            String PAT_ADDR_CENSUS_TRACT_TXT="";
            String PAT_EMAIL_ADDRESS_TXT="";
            String PAT_URL_ADDRESS_TXT="";
            String PAT_NAME_AS_OF_DT="";
            String PAT_ADDR_AS_OF_DT="";
            String PAT_PHONE_AS_OF_DT="";
            String PAT_PHONE_COUNTRY_CODE_TXT="";
            int patientIdentifier =0;
            int caseInvCounter= -1;

            int nameCounter = 1;
            ///// PATIENT - CHECKED
            if (input.getMsgPatients() != null && input.getMsgPatients().size() > 0) {
                int k = 1;
                Field[] fields = EcrMsgPatientDto.class.getDeclaredFields();
                for (Field field : fields) {
                    if (!"numberOfField".equals(field.getName())) {
                        if (field.getName().equals("patPrimaryLanguageCd") &&
                                patient.getPatPrimaryLanguageCd() != null && !patient.getPatPrimaryLanguageCd().isEmpty()) {
                            if(!clinicalDocument.isSetLanguageCode()){
                                clinicalDocument.addNewLanguageCode();
                            }
                            clinicalDocument.getLanguageCode().setCode(patient.getPatPrimaryLanguageCd());
                        }
                        else if (field.getName().equals("patLocalId") &&
                                patient.getPatLocalId() != null && !patient.getPatLocalId().isEmpty()) {
                            clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewId();
                            clinicalDocument.getRecordTargetArray(0).getPatientRole().getIdArray(patientIdentifier).setExtension(patient.getPatPrimaryLanguageCd());
                            clinicalDocument.getRecordTargetArray(0).getPatientRole().getIdArray(patientIdentifier).setRoot("2.16.840.1.113883.4.1");
                            clinicalDocument.getRecordTargetArray(0).getPatientRole().getIdArray(patientIdentifier).setAssigningAuthorityName("LR");
                            patientIdentifier++;
                        }
                        else if (field.getName().equals("patIdMedicalRecordNbrTxt") &&
                                patient.getPatIdMedicalRecordNbrTxt() != null && !patient.getPatIdMedicalRecordNbrTxt().isEmpty()) {
                            clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewId();
                            clinicalDocument.getRecordTargetArray(0).getPatientRole().getIdArray(patientIdentifier).setExtension(patient.getPatIdMedicalRecordNbrTxt());
                            clinicalDocument.getRecordTargetArray(0).getPatientRole().getIdArray(patientIdentifier).setRoot("2.16.840.1.113883.4.1");
                            clinicalDocument.getRecordTargetArray(0).getPatientRole().getIdArray(patientIdentifier).setAssigningAuthorityName("LR_MRN");
                            patientIdentifier++;
                        }
                        else if (field.getName().equals("patIdSsnTxt") &&
                                patient.getPatIdSsnTxt() != null && !patient.getPatIdSsnTxt().isEmpty()) {
                            clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewId();
                            clinicalDocument.getRecordTargetArray(0).getPatientRole().getIdArray(patientIdentifier).setExtension(patient.getPatIdSsnTxt());
                            clinicalDocument.getRecordTargetArray(0).getPatientRole().getIdArray(patientIdentifier).setRoot("2.16.840.1.114222.4.5.1");
                            clinicalDocument.getRecordTargetArray(0).getPatientRole().getIdArray(patientIdentifier).setAssigningAuthorityName("SS");
                            patientIdentifier++;
                        }
                        else if (field.getName().equals("patAddrStreetAddr1Txt") && patient.getPatAddrStreetAddr1Txt() != null && !patient.getPatAddrStreetAddr1Txt().isEmpty()) {
                            address1 += patient.getPatAddrStreetAddr1Txt();
                        }
                        else if (field.getName().equals("patAddrStreetAddr2Txt") && patient.getPatAddrStreetAddr2Txt() != null && !patient.getPatAddrStreetAddr2Txt().isEmpty()) {
                            address2 += patient.getPatAddrStreetAddr2Txt();
                        }

                        else if(field.getName().equals("patAddrCityTxt") && patient.getPatAddrCityTxt() != null && !patient.getPatAddrCityTxt().isEmpty()) {
                            if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray().length == 0) {
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewAddr();
                            }
                            if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).getCityArray().length == 0) {
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).addNewCity();
                            }
                            // original code start at index 1
                            clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0)
                                    .getCityArray(0).set(MapToCData(patient.getPatAddrCityTxt()));
                            k++;
                        }
                        else if(field.getName().equals("patAddrStateCd") && patient.getPatAddrStateCd() != null && !patient.getPatAddrStateCd().isEmpty()) {
                            if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray().length == 0) {
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewAddr();
                            }
                            if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).getStateArray().length == 0) {
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).addNewState();
                            }
                            var state = MapToAddressType(patient.getPatAddrStateCd(), "STATE");
                            clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).getStateArray(0).set(MapToCData(state));
                            k++;
                        }
                        else if(field.getName().equals("patAddrZipCodeTxt") && patient.getPatAddrZipCodeTxt() != null && !patient.getPatAddrZipCodeTxt().isEmpty()) {
                            if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray().length == 0) {
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewAddr();
                            }
                            if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).getPostalCodeArray().length == 0) {
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).addNewPostalCode();
                            }
                            clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).getPostalCodeArray(0).set(MapToCData(patient.getPatAddrZipCodeTxt()));
                            k++;
                        }
                        // PAT_ADDR_COUNTY_CD
                        else if(field.getName().equals("patAddrCountyCd") && patient.getPatAddrCountyCd() != null && !patient.getPatAddrCountyCd().isEmpty()) {
                            if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray().length == 0) {
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewAddr();
                            }
                            if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).getCountyArray().length == 0) {
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).addNewCounty();
                            }

                            var val = MapToAddressType(patient.getPatAddrCountyCd(), "COUNTY");
                            clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).getCountyArray(0).set(MapToCData(val));
                            k++;
                        }
                        // PAT_ADDR_COUNTRY_CD
                        else if(field.getName().equals("patAddrCountryCd") && patient.getPatAddrCountryCd() != null && !patient.getPatAddrCountryCd().isEmpty()) {
                            if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray().length == 0) {
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewAddr();
                            }
                            if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).getCountryArray().length == 0) {
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).addNewCountry();
                            }
                            var val = MapToAddressType(patient.getPatAddrCountryCd(), "COUNTRY");
                            clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).getCountryArray(0).set(MapToCData(val));
                            k++;
                        }

                        else if (field.getName().equals("patWorkPhoneExtensionTxt") && patient.getPatWorkPhoneExtensionTxt() != null) {
                            PAT_WORK_PHONE_EXTENSION_TXT = patient.getPatWorkPhoneExtensionTxt().toString();
                        }
                        else if (field.getName().equals("patHomePhoneNbrTxt") && patient.getPatHomePhoneNbrTxt() != null) {
                            PAT_HOME_PHONE_NBR_TXT = patient.getPatHomePhoneNbrTxt();
                        }
                        else if (field.getName().equals("patWorkPhoneNbrTxt") && patient.getPatWorkPhoneNbrTxt() != null) {
                            wpNumber = patient.getPatWorkPhoneNbrTxt();
                        }
                        else if (field.getName().equals("patPhoneCountryCodeTxt") && patient.getPatPhoneCountryCodeTxt() != null) {
                            PAT_PHONE_COUNTRY_CODE_TXT = patient.getPatPhoneCountryCodeTxt().toString();
                        }
                        else if (field.getName().equals("patCellPhoneNbrTxt") && patient.getPatCellPhoneNbrTxt() != null) {
                            cellNumber = patient.getPatCellPhoneNbrTxt();
                        }
                        else if (field.getName().equals("patNamePrefixCd") && patient.getPatNamePrefixCd() != null && !patient.getPatNamePrefixCd().trim().isEmpty()) {
                            PAT_NAME_PREFIX_CD = patient.getPatNamePrefixCd();
                        }
                        else if (field.getName().equals("patNameFirstTxt") && patient.getPatNameFirstTxt() != null && !patient.getPatNameFirstTxt().trim().isEmpty()) {
                            PAT_NAME_FIRST_TXT = patient.getPatNameFirstTxt();
                        }
                        else if (field.getName().equals("patNameMiddleTxt") && patient.getPatNameMiddleTxt() != null && !patient.getPatNameMiddleTxt().trim().isEmpty()) {
                            PAT_NAME_MIDDLE_TXT = patient.getPatNameMiddleTxt();
                        }
                        else if (field.getName().equals("patNameLastTxt") && patient.getPatNameLastTxt() != null && !patient.getPatNameLastTxt().trim().isEmpty()) {
                            PAT_NAME_LAST_TXT = patient.getPatNameLastTxt();
                        }
                        else if (field.getName().equals("patNameSuffixCd") && patient.getPatNameSuffixCd() != null && !patient.getPatNameSuffixCd().trim().isEmpty()) {
                            PAT_NAME_SUFFIX_CD = patient.getPatNameSuffixCd();
                        }

                        // PAT_NAME_ALIAS_TXT
                        else if (field.getName().equals("patNameAliasTxt") && patient.getPatNameAliasTxt() != null && !patient.getPatNameAliasTxt().trim().isEmpty()) {
                            // CHECK ORIG: 211
                            if (!clinicalDocument.getRecordTargetArray(0).getPatientRole().isSetPatient()) {
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewPatient();
                            }

                            if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray().length == 0) {
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().addNewName();
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().addNewName();
                            }
                            else if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray().length == 1) {
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().addNewName();
                            }

                            if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(1).getGivenArray().length == 0) {
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(1).addNewGiven();
                            }

                            clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(1).setUse(new ArrayList<String> (Arrays.asList("P")));
                            clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(1).getGivenArray(0).set(MapToCData(patient.getPatNameAliasTxt()));
                        }
                        // PAT_CURRENT_SEX_CD
                        else if(field.getName().equals("patCurrentSexCd") && patient.getPatCurrentSexCd() != null && !patient.getPatCurrentSexCd().isEmpty()) {
                            String questionCode = MapToQuestionId(patient.getPatCurrentSexCd());

                            if (!clinicalDocument.getRecordTargetArray(0).getPatientRole().isSetPatient()) {
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewPatient();
                            }

                            if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().isSetAdministrativeGenderCode()) {
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().addNewAdministrativeGenderCode();
                            }
                            CE administrativeGender = MapToCEAnswerType(patient.getPatCurrentSexCd(), questionCode);
                            clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().setAdministrativeGenderCode(administrativeGender);
                        }
                        // PAT_BIRTH_DT
                        else if(field.getName().equals("patBirthDt") && patient.getPatBirthDt() != null) {
                            if (!clinicalDocument.getRecordTargetArray(0).getPatientRole().isSetPatient()) {
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewPatient();
                            }

                            clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().setBirthTime(MapToTsType(patient.getPatBirthDt().toString()));
                        }
                        // PAT_MARITAL_STATUS_CD
                        else if(field.getName().equals("patMaritalStatusCd") && patient.getPatMaritalStatusCd() != null  && !patient.getPatMaritalStatusCd().isEmpty()) {
                            String questionCode = MapToQuestionId(patient.getPatMaritalStatusCd());
                            CE ce = MapToCEAnswerType(patient.getPatMaritalStatusCd(), questionCode);
                            if (!clinicalDocument.getRecordTargetArray(0).getPatientRole().isSetPatient()) {
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewPatient();
                            }
                            clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().setMaritalStatusCode(ce);
                        }
                        // PAT_RACE_CATEGORY_CD
                        else if(field.getName().equals("patRaceCategoryCd") && patient.getPatRaceCategoryCd() != null  && !patient.getPatRaceCategoryCd().isEmpty()) {
                            List<CE> raceCode2List = new ArrayList<>();
                            long counter = patient.getPatRaceCategoryCd().chars().filter(x -> x == '|').count();

                            List<String> raceCatList = new ArrayList<>();
                            if (counter > 0) {
                                raceCatList = GetStringsBeforePipe(patient.getPatRaceCategoryCd());
                            } else {
                                raceCatList.add(patient.getPatRaceCategoryCd());
                            }
                            for(int i = 0; i < raceCatList.size(); i++) {
                                String val = raceCatList.get(i);
                                String questionCode = MapToQuestionId("PAT_RACE_CATEGORY_CD");
                                if (!questionCode.isEmpty()) {
                                    CE ce = MapToCEAnswerType(val, questionCode);
                                    raceCode2List.add(ce);
                                    raceCodeCounter = i;
                                }
                            }
                        }
                        // PAT_RACE_DESC_TXT
                        else if(field.getName().equals("patRaceDescTxt") && patient.getPatRaceDescTxt() != null  && !patient.getPatRaceDescTxt().isEmpty()) {
                            if (!clinicalDocument.getRecordTargetArray(0).getPatientRole().isSetPatient()) {
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewPatient();
                            }
                            var counter = 0;
                            if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getRaceCode2Array().length == 0) {
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().addNewRaceCode2();
                            }
                            else {
                                counter = clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getRaceCode2Array().length + 1 - 1;
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().addNewRaceCode2();
                            }
                            ED originalText = ED.Factory.newInstance();
                            // CHECK LINE 246
                            originalText.set(MapToCData(patient.getPatRaceDescTxt()));
                            clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getRaceCode2Array(counter).setOriginalText(originalText);
                            clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getRaceCode2Array(counter).setCode("OTH");
                        }

                        // PAT_ETHNIC_GROUP_IND_CD
                        else if(field.getName().equals("patEthnicGroupIndCd") && patient.getPatEthnicGroupIndCd() != null  && !patient.getPatEthnicGroupIndCd().isEmpty()) {
                            if (!clinicalDocument.getRecordTargetArray(0).getPatientRole().isSetPatient()) {
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewPatient();
                            }

                            String questionCode = MapToQuestionId(patient.getPatEthnicGroupIndCd());
                            CE ce = MapToCEAnswerType(patient.getPatEthnicGroupIndCd(), questionCode);

                            clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().setEthnicGroupCode(ce);
                        }

                        // PAT_BIRTH_COUNTRY_CD
                        else if(field.getName().equals("patBirthCountryCd") && patient.getPatBirthCountryCd() != null  && !patient.getPatBirthCountryCd().isEmpty()) {
                            if (!clinicalDocument.getRecordTargetArray(0).getPatientRole().isSetPatient()) {
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewPatient();
                            }

                            if (!clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().isSetBirthplace()) {
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().addNewBirthplace();
                            }


                            String val = MapToAddressType(patient.getPatBirthCountryCd(), "COUNTRY");
                            POCDMT000040Place place = POCDMT000040Place.Factory.newInstance();
                            clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getBirthplace().setPlace(place);

                            AD ad = AD.Factory.newInstance();
                            clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getBirthplace().getPlace().setAddr(ad);

                            AdxpCounty county = AdxpCounty.Factory.newInstance();
                            county.set(MapToCData(val));
                            AdxpCounty[] countyArr = {county};
                            clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getBirthplace().getPlace().getAddr().setCountyArray(countyArr);
                        }

                        // PAT_ADDR_CENSUS_TRACT_TXT
                        else if(field.getName().equals("patAddrCensusTractTxt") && patient.getPatAddrCensusTractTxt() != null  && !patient.getPatAddrCensusTractTxt().isEmpty()) {

                            if ( clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray().length == 0) {
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewAddr();
                            }
                            AdxpCensusTract census = AdxpCensusTract.Factory.newInstance();
                            census.set(MapToCData(patient.getPatAddrCensusTractTxt() ));
                            AdxpCensusTract[] censusArr = {census};
                            clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).setCensusTractArray(censusArr);
                            k++;
                        }

                        else if (field.getName().equals("patEmailAddressTxt") && patient.getPatEmailAddressTxt() != null && !patient.getPatEmailAddressTxt().trim().isEmpty()) {
                            PAT_EMAIL_ADDRESS_TXT = patient.getPatEmailAddressTxt();
                        }
                        else if (field.getName().equals("patUrlAddressTxt") && patient.getPatUrlAddressTxt() != null && !patient.getPatUrlAddressTxt().trim().isEmpty()) {
                            PAT_URL_ADDRESS_TXT = patient.getPatUrlAddressTxt();
                        }
                        // PAT_NAME_AS_OF_DT
                        else if(field.getName().equals("patNameAsOfDt") && patient.getPatNameAsOfDt() != null) {
                            if (!clinicalDocument.getRecordTargetArray(0).getPatientRole().isSetPatient()) {
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewPatient();
                            }
                            if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray().length == 0) {
                                clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().addNewName();
                            }

                            PN pn = PN.Factory.newInstance();
                            IVLTS time = IVLTS.Factory.newInstance();
                            var ts = MapToTsType(patient.getPatNameAsOfDt().toString());
                            time.set(MapToCData(ts.getValue()));
                            pn.setValidTime(time);
                            clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().setNameArray(0, pn);
                        }

                        else if (field.getName().equals("patPhoneAsOfDt") &&  patient.getPatPhoneAsOfDt() != null) {
                            PAT_PHONE_AS_OF_DT = patient.getPatPhoneAsOfDt().toString();
                        }

                        // PAT_INFO_AS_OF_DT
                        else if (
                                (field.getName().equals("patInfoAsOfDt") && patient.getPatInfoAsOfDt() != null) ||
                                        (field.getName().equals("patAddrCommentTxt") && patient.getPatAddrCommentTxt() != null && !patient.getPatAddrCommentTxt().isEmpty()) ||
                                        (field.getName().equals("patAdditionalGenderTxt") && patient.getPatAdditionalGenderTxt() != null && !patient.getPatAdditionalGenderTxt().isEmpty()) ||
                                        (field.getName().equals("patSpeaksEnglishIndCd") && patient.getPatSpeaksEnglishIndCd() != null && !patient.getPatSpeaksEnglishIndCd().isEmpty()) ||
                                        (field.getName().equals("patIdStateHivCaseNbrTxt") && patient.getPatIdStateHivCaseNbrTxt() != null && !patient.getPatIdStateHivCaseNbrTxt().isEmpty()) ||
                                        (field.getName().equals("patEthnicityUnkReasonCd") && patient.getPatEthnicityUnkReasonCd() != null && !patient.getPatEthnicityUnkReasonCd().isEmpty()) ||
                                        (field.getName().equals("patSexUnkReasonCd") && patient.getPatSexUnkReasonCd() != null && !patient.getPatSexUnkReasonCd().isEmpty()) ||
                                        (field.getName().equals("patPhoneCommentTxt") && patient.getPatPhoneCommentTxt() != null && !patient.getPatPhoneCommentTxt().isEmpty()) ||
                                        (field.getName().equals("patDeceasedIndCd") && patient.getPatDeceasedIndCd() != null && !patient.getPatDeceasedIndCd().isEmpty()) ||
                                        (field.getName().equals("patDeceasedDt") && patient.getPatDeceasedDt() != null) ||
                                        (field.getName().equals("patPreferredGenderCd") && patient.getPatPreferredGenderCd() != null && !patient.getPatPreferredGenderCd().isEmpty()) ||
                                        (field.getName().equals("patReportedAge") && patient.getPatReportedAge() != null) ||
                                        (field.getName().equals("patReportedAgeUnitCd") && patient.getPatReportedAgeUnitCd() != null && !patient.getPatReportedAgeUnitCd().isEmpty()) ||
                                        (field.getName().equals("patCommentTxt") && patient.getPatCommentTxt() != null && !patient.getPatCommentTxt().isEmpty()) ||
                                        (field.getName().equals("patBirthSexCd") && patient.getPatBirthSexCd() != null && !patient.getPatBirthSexCd().isEmpty())
                        ) {
                            String colName = "";
                            String value = "";

                            if (field.getName().equals("patInfoAsOfDt") && isFieldValid(field.getName(), patient.getPatInfoAsOfDt())) {
                                colName = "PAT_INFO_AS_OF_DT";
                                value = patient.getPatInfoAsOfDt().toString();
                            } else if (field.getName().equals("patAddrCommentTxt") && isFieldValid(field.getName(), patient.getPatAddrCommentTxt())) {
                                colName = "PAT_ADDR_COMMENT_TXT";
                                value = patient.getPatAddrCommentTxt();
                            } else if (field.getName().equals("patAdditionalGenderTxt") && isFieldValid(field.getName(), patient.getPatAdditionalGenderTxt())) {
                                colName = "PAT_ADDITIONAL_GENDER_TXT";
                                value = patient.getPatAdditionalGenderTxt();
                            } else if (field.getName().equals("patSpeaksEnglishIndCd") && isFieldValid(field.getName(), patient.getPatSpeaksEnglishIndCd())) {
                                colName = "PAT_SPEAKS_ENGLISH_IND_CD";
                                value = patient.getPatSpeaksEnglishIndCd();
                            } else if (field.getName().equals("patIdStateHivCaseNbrTxt") && isFieldValid(field.getName(), patient.getPatIdStateHivCaseNbrTxt())) {
                                colName = "PAT_ID_STATE_HIV_CASE_NBR_TXT";
                                value = patient.getPatIdStateHivCaseNbrTxt();
                            } else if (field.getName().equals("patEthnicityUnkReasonCd") && isFieldValid(field.getName(), patient.getPatEthnicityUnkReasonCd())) {
                                colName = "PAT_ETHNICITY_UNK_REASON_CD";
                                value = patient.getPatEthnicityUnkReasonCd();
                            } else if (field.getName().equals("patSexUnkReasonCd") && isFieldValid(field.getName(), patient.getPatSexUnkReasonCd())) {
                                colName = "PAT_SEX_UNK_REASON_CD";
                                value = patient.getPatSexUnkReasonCd();
                            } else if (field.getName().equals("patPhoneCommentTxt") && isFieldValid(field.getName(), patient.getPatPhoneCommentTxt())) {
                                colName = "PAT_PHONE_COMMENT_TXT";
                                value = patient.getPatPhoneCommentTxt();
                            } else if (field.getName().equals("patDeceasedIndCd") && isFieldValid(field.getName(), patient.getPatDeceasedIndCd())) {
                                colName = "PAT_DECEASED_IND_CD";
                                value = patient.getPatDeceasedIndCd();
                            } else if (field.getName().equals("patDeceasedDt") && isFieldValid(field.getName(), patient.getPatDeceasedDt())) {
                                colName = "PAT_DECEASED_DT";
                                value = patient.getPatDeceasedDt().toString();
                            } else if (field.getName().equals("patPreferredGenderCd") && isFieldValid(field.getName(), patient.getPatPreferredGenderCd())) {
                                colName = "PAT_PREFERRED_GENDER_CD";
                                value = patient.getPatPreferredGenderCd();
                            } else if (field.getName().equals("patReportedAge") && patient.getPatReportedAge() != null) { // Assuming this is a numeric or date type
                                colName = "PAT_REPORTED_AGE";
                                value = String.valueOf(patient.getPatReportedAge()); // Convert to String
                            } else if (field.getName().equals("patReportedAgeUnitCd") && isFieldValid(field.getName(), patient.getPatReportedAgeUnitCd())) {
                                colName = "PAT_REPORTED_AGE_UNIT_CD";
                                value = patient.getPatReportedAgeUnitCd();
                            } else if (field.getName().equals("patCommentTxt") && isFieldValid(field.getName(), patient.getPatCommentTxt())) {
                                colName = "PAT_COMMENT_TXT";
                                value = patient.getPatCommentTxt();
                            } else if (field.getName().equals("patBirthSexCd") && isFieldValid(field.getName(), patient.getPatBirthSexCd())) {
                                colName = "PAT_BIRTH_SEX_CD";
                                value = patient.getPatBirthSexCd();
                            }
                            // USE ai to generate these condition

                            if (patientComponentCounter < 0 ) {

                                if (clinicalDocument.getComponent() == null) {
                                    clinicalDocument.addNewComponent();
                                }

                                if (!clinicalDocument.getComponent().isSetStructuredBody()) {
                                    clinicalDocument.getComponent().addNewStructuredBody();
                                }

                                if (clinicalDocument.getComponent().getStructuredBody().getComponentArray().length == 0) {
                                    clinicalDocument.getComponent().getStructuredBody().addNewComponent();
                                    patientComponentCounter = 0;
                                }
                                else {
                                    patientComponentCounter = clinicalDocument.getComponent().addNewStructuredBody().getComponentArray().length+ 1;
                                    clinicalDocument.getComponent().getStructuredBody().addNewComponent();
                                }

                                if (clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection() == null) {
                                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).addNewSection();
                                }

                                if (!clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().isSetId()) {
                                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().addNewId();
                                }

                                if (!clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().isSetCode()) {
                                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().addNewCode();
                                }

                                if (!clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().isSetTitle()) {
                                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().addNewTitle();
                                }

                                clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().getId().setRoot("2.16.840.1.113883.19");
                                clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().getId().setExtension(inv168);
                                clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().getId().setAssigningAuthorityName("LR");
                                clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().getCode().setCode("29762-2");
                                clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().getCode().setCodeSystem("2.16.840.1.113883.6.1");
                                clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().getCode().setCodeSystemName("LOINC");
                                clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().getCode().setDisplayName("Social History");
                                clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().getTitle().set(MapToCData("SOCIAL HISTORY INFORMATION"));

                            }

                            POCDMT000040Component3 comp = clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter);

                            int patEntityCounter = clinicalDocument.getComponent().getStructuredBody().getComponentArray(0).getSection().getEntryArray().length;


                            var compPatient = MapToPatient(patEntityCounter, colName, value, comp);

                            clinicalDocument.getComponent().getStructuredBody().setComponentArray(patientComponentCounter, compPatient);
                        }


                    }
                    if (k > 1) {
                        if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray().length == 0) {
                            clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewAddr();
                        }
                        clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).setUse(new ArrayList<String>(Arrays.asList("H")));
                    }
                    if (k> 1 && patient.getPatAddrAsOfDt() != null ) {
                        if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray().length == 0) {
                            clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewAddr();
                        }
                        // CHECK MapToUsableTSElement in Ori
                        AD element = clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0);
                        var ad = MapToUsableTSElement(patient.getPatAddrAsOfDt().toString(), element, "useablePeriod");
                        clinicalDocument.getRecordTargetArray(0).getPatientRole().setAddrArray(0, (AD) ad);
                    }

                }
            }

            if(!PAT_NAME_PREFIX_CD.isEmpty()) {
                if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray().length == 0) {
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().addNewName();
                }
                if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).getPrefixArray().length == 0) {
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).addNewPrefix();
                }
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).getPrefixArray(0).set(MapToCData(PAT_NAME_PREFIX_CD));
                nameCounter++;
            }
            if(!PAT_NAME_FIRST_TXT.isEmpty()) {
                if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray().length == 0) {
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().addNewName();
                }
                var count = 0;
                if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).getGivenArray().length == 0) {
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).addNewGiven();
                } else {
                    count = clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).getGivenArray().length + 1 - 1;
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).addNewGiven();
                }
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).getGivenArray(count).set(MapToCData(PAT_NAME_PREFIX_CD));
                nameCounter++;
            }
            if(!PAT_NAME_MIDDLE_TXT.isEmpty()) {
                if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray().length == 0) {
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().addNewName();
                }
                var count = 0;
                if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).getGivenArray().length == 0) {
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).addNewGiven();
                } else {
                    count = clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).getGivenArray().length + 1 - 1;
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).addNewGiven();
                }
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).getGivenArray(count).set(MapToCData(PAT_NAME_MIDDLE_TXT));
                nameCounter++;
            }
            if(!PAT_NAME_LAST_TXT.isEmpty()) {
                if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray().length == 0) {
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().addNewName();
                }
                if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).getFamilyArray().length == 0) {
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).addNewFamily();
                }
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).getFamilyArray(0).set(MapToCData(PAT_NAME_LAST_TXT));
                nameCounter++;
            }
            if(!PAT_NAME_SUFFIX_CD.isEmpty()) {
                if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray().length == 0) {
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().addNewName();
                }
                if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).getSuffixArray().length == 0) {
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).addNewSuffix();
                }
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).getSuffixArray(0).set(MapToCData(PAT_NAME_SUFFIX_CD));
                nameCounter++;
            }
            if (!PAT_HOME_PHONE_NBR_TXT.isEmpty()) {
                int pCount = 0;
                if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray().length == 0) {
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewTelecom();
                } else {
                    pCount = clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray().length + 1 - 1;
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewTelecom();
                }

                clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount).setUse(new ArrayList<String>(Arrays.asList("HP")));
                String phoneHome = "";
                if(!PAT_PHONE_COUNTRY_CODE_TXT.isEmpty()) {
                    PAT_HOME_PHONE_NBR_TXT =  "+"+PAT_PHONE_COUNTRY_CODE_TXT+"-"+PAT_HOME_PHONE_NBR_TXT;
                }
                int homeExtnSize = homeExtn.length();
                if(homeExtnSize>0){
                    phoneHome=PAT_HOME_PHONE_NBR_TXT+ ";ext="+ homeExtn;
                }
                else {
                    phoneHome=PAT_HOME_PHONE_NBR_TXT;
                }
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount).setValue(phoneHome);
                if(!PAT_PHONE_AS_OF_DT.isEmpty()){
                    TEL element = clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount);
                    // CHECK MapToUsableTSElement
                    var out = MapToUsableTSElement(PAT_PHONE_AS_OF_DT, element, "useablePeriod");
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().setTelecomArray(pCount, (TEL) out);
                }
                phoneCounter =phoneCounter +1;
            }

            // wpNumber
            if (!wpNumber.isEmpty()) {
                int pCount = 0;
                if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray().length == 0) {
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewTelecom();
                } else {
                    pCount = clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray().length + 1 - 1;
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewTelecom();
                }

                clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount).setUse(new ArrayList(Arrays.asList("WP")));
                if(!PAT_WORK_PHONE_EXTENSION_TXT.isEmpty()){
                    wpNumber=wpNumber+ ";ext="+ PAT_WORK_PHONE_EXTENSION_TXT;
                }
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount).setValue(wpNumber);
                if(!PAT_PHONE_AS_OF_DT.isEmpty()){
                    //OutXML::Element element = (OutXML::Element)out.recordTarget[0].patientRole.telecom[phoneCounter];
                    //MapToUsableTSElement(PAT_PHONE_AS_OF_DT, element, "useablePeriod");
                    // CHECK MapToUsableTSElement
                    TEL element = clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount);
                    // CHECK MapToUsableTSElement
                    var out = MapToUsableTSElement(PAT_PHONE_AS_OF_DT, element, "useablePeriod");
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().setTelecomArray(pCount, (TEL) out);
                }

                phoneCounter =phoneCounter +1;
            }

            // cellNumber
            if(!cellNumber.isEmpty()) {
                int pCount = 0;
                if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray().length == 0) {
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewTelecom();
                } else {
                    pCount = clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray().length + 1 - 1;
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewTelecom();
                }

                clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount).setUse(new ArrayList(Arrays.asList("MC")));
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount).setValue(cellNumber);

                if(!PAT_PHONE_AS_OF_DT.isEmpty()){
                    TEL element = clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount);
                    // CHECK MapToUsableTSElement
                    var out = MapToUsableTSElement(PAT_PHONE_AS_OF_DT, element, "useablePeriod");
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().setTelecomArray(pCount, (TEL) out);
                }
                phoneCounter =phoneCounter +1;
            }

            // PAT_EMAIL_ADDRESS_TXT
            if(!PAT_EMAIL_ADDRESS_TXT.isEmpty()) {
                int pCount = 0;
                if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray().length == 0) {
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewTelecom();
                } else {
                    pCount = clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray().length + 1 - 1;
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewTelecom();
                }


                clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount).setUse(new ArrayList(Arrays.asList("HP")));
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount).setValue("mailto:"+PAT_EMAIL_ADDRESS_TXT);
                if(!PAT_PHONE_AS_OF_DT.isEmpty()){
                    TEL element = clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount);
                    // CHECK MapToUsableTSElement
                    var out = MapToUsableTSElement(PAT_PHONE_AS_OF_DT, element, "useablePeriod");
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().setTelecomArray(pCount, (TEL) out);
                }
                phoneCounter =phoneCounter +1;
            }

            // PAT_URL_ADDRESS_TXT
            if(!PAT_URL_ADDRESS_TXT.isEmpty()) {
                int pCount = 0;
                if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray().length == 0) {
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewTelecom();
                } else {
                    pCount = clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray().length + 1 - 1;
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewTelecom();
                }
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount).setUse(new ArrayList(Arrays.asList("HP")));
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount).setValue(PAT_URL_ADDRESS_TXT);
                if(!PAT_PHONE_AS_OF_DT.isEmpty()){
                    TEL element = clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount);
                    // CHECK MapToUsableTSElement
                    var out = MapToUsableTSElement(PAT_PHONE_AS_OF_DT, element, "useablePeriod");
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().setTelecomArray(pCount, (TEL) out);
                }
                phoneCounter =phoneCounter +1;
            }


            if(!address1.isEmpty()) {
                int c1 = 0;
                int c2 = 0;
                if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray().length == 0) {
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewAddr();
                }
                else {
                    c1 = clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray().length;
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewAddr();
                }

                if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(c1).getStreetAddressLineArray().length == 0) {
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(c1).addNewStreetAddressLine();
                }
                else {
                    c2 = clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(c1).getStreetAddressLineArray().length;
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(c1).addNewStreetAddressLine();
                }

                clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(c1).getStreetAddressLineArray(c2).set(MapToCData(address1));
            }

            if(!address2.isEmpty()) {
                int c1 = 0;
                int c2 = 0;
                if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray().length == 0) {
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewAddr();
                }
                else {
                    c1 = clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray().length;
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewAddr();
                }

                if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(c1).getStreetAddressLineArray().length == 0) {
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(c1).addNewStreetAddressLine();
                }
                else {
                    c2 = clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(c1).getStreetAddressLineArray().length;
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(c1).addNewStreetAddressLine();
                }

                clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(c1).getStreetAddressLineArray(c2).set(MapToCData(address2));
            }



        }
        //endregion

        /**
         * CASE - 1st PHASE TESTED
         * **/
        if(!input.getMsgCases().isEmpty()) {

            if (clinicalDocument.getComponent() == null) {
                clinicalDocument.addNewComponent();
            }
            else {
                if (!clinicalDocument.getComponent().isSetStructuredBody()) {
                    clinicalDocument.getComponent().addNewStructuredBody();
                }
                else {
                    if (clinicalDocument.getComponent().getStructuredBody().getComponentArray().length == 0) {
                        clinicalDocument.getComponent().getStructuredBody().addNewComponent();
                    }
                }
            }

            for(int i = 0; i < input.getMsgCases().size(); i++) {
                if (componentCounter < 0) {
                    componentCounter++;
                    if (clinicalDocument.getComponent().getStructuredBody().getComponentArray(componentCounter).getSection() == null) {
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(componentCounter).addNewSection();
                    }
                    else {
                        if( clinicalDocument.getComponent().getStructuredBody().getComponentArray(componentCounter).getSection().getId() == null) {
                            clinicalDocument.getComponent().getStructuredBody().getComponentArray(componentCounter).getSection().addNewId();
                        }
                        if( clinicalDocument.getComponent().getStructuredBody().getComponentArray(componentCounter).getSection().getCode() == null) {
                            clinicalDocument.getComponent().getStructuredBody().getComponentArray(componentCounter).getSection().addNewCode();
                        }
                        if( clinicalDocument.getComponent().getStructuredBody().getComponentArray(componentCounter).getSection().getTitle() == null) {
                            clinicalDocument.getComponent().getStructuredBody().getComponentArray(componentCounter).getSection().addNewTitle();
                        }
                    }
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(componentCounter).getSection().getId().setRoot("2.16.840.1.113883.19");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(componentCounter).getSection().getId().setExtension(inv168);
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(componentCounter).getSection().getId().setAssigningAuthorityName("LR");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(componentCounter).getSection().getCode().setCode("55752-0");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(componentCounter).getSection().getCode().setCodeSystem("2.16.840.1.113883.6.1");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(componentCounter).getSection().getCode().setCodeSystemName("LOINC");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(componentCounter).getSection().getCode().setDisplayName("Clinical Information");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(componentCounter).getSection().getTitle().set(MapToCData("CLINICAL INFORMATION"));
                }

                // MAP TO CASE code line 438
                clinicalCounter = clinicalDocument.getComponent().getStructuredBody().getComponentArray(componentCaseCounter).getSection().getEntryArray().length;
                // CHECK MapToCase
                POCDMT000040StructuredBody output = clinicalDocument.getComponent().getStructuredBody();

                var mappedCase = MapToCase(clinicalCounter, input.getMsgCases().get(i), output);
                clinicalDocument.getComponent().setStructuredBody(mappedCase);
                componentCounter = clinicalDocument.getComponent().getStructuredBody().getComponentArray().length - 1;
            }

        }


        /**
         * XML ANSWER - PHASE 1 Test Done
         * */
        if(!input.getMsgXmlAnswers().isEmpty()) {
            for(int i = 0; i < input.getMsgXmlAnswers().size(); i++) {
                componentCounter++;
                int c = 0;
                if (clinicalDocument.getComponent().getStructuredBody().getComponentArray().length == 0) {
                    clinicalDocument.getComponent().getStructuredBody().addNewComponent();
                } else {
                    c = clinicalDocument.getComponent().getStructuredBody().getComponentArray().length;
                    clinicalDocument.getComponent().getStructuredBody().addNewComponent();
                }
                POCDMT000040Component3 out = clinicalDocument.getComponent().getStructuredBody().getComponentArray(c);
                var mappedData = MapToExtendedData(input.getMsgXmlAnswers().get(i), out);
                clinicalDocument.getComponent().getStructuredBody().setComponentArray(c, mappedData);
            }

        }

        /**
         * PROVIDER -- PHASE 1 TESTED
         * **/
        if(!input.getMsgProviders().isEmpty()) {
            // 449
           for(int i = 0; i < input.getMsgProviders().size(); i++) {
                if (input.getMsgProviders().get(i).getPrvAuthorId() != null
                        && input.getMsgProviders().get(i).getPrvAuthorId().equalsIgnoreCase(inv168)) {
                    // ignore
                }
                else {
                    int c = 0;
                    if (clinicalDocument.getComponent().getStructuredBody().getComponentArray().length == 0) {
                        clinicalDocument.getComponent().getStructuredBody().addNewComponent();
                    } else {
                        c = clinicalDocument.getComponent().getStructuredBody().getComponentArray().length;
                        clinicalDocument.getComponent().getStructuredBody().addNewComponent();
                    }
                    if (clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection() == null) {
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).addNewSection();
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewCode();
                    } else {
                        if (clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection() == null) {
                            clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewCode();
                        }
                    }

                    if (performerComponentCounter < 1) {
                        componentCounter++;
                        performerComponentCounter = componentCounter;

                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setCode("123-4567");
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setCodeSystem("Local-codesystem-oid");
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setCodeSystemName("LocalSystem");
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setDisplayName("Interested Parties Section");
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().set(MapToCData("INTERESTED PARTIES SECTION"));
                    }

                    performerSectionCounter = clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray().length;

                    if ( clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray().length == 0) {
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewEntry();
                        performerSectionCounter = 0;
                    }
                    else {
                        performerSectionCounter = clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray().length;
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewEntry();
                    }


                    if (clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(performerSectionCounter).getAct() == null) {
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(performerSectionCounter).addNewAct();
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(performerSectionCounter).getAct().addNewParticipant();
                    } else {
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(performerSectionCounter).getAct().addNewParticipant();
                    }
                    // CHECK MapToPSN
                    POCDMT000040Participant2 out = clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(performerSectionCounter).getAct().getParticipantArray(0);
                    POCDMT000040Participant2 output = MapToPSN(
                            input.getMsgProviders().get(i),
                            out
                    );
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(performerSectionCounter).getAct().setParticipantArray(0, output);

                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(performerSectionCounter).setTypeCode(XActRelationshipEntry.COMP);
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(performerSectionCounter).getAct().setClassCode(XActClassDocumentEntryAct.ACT);
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(performerSectionCounter).getAct().setMoodCode(XDocumentActMood.EVN);

                    if (clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(performerSectionCounter).getAct().getCode() == null){
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(performerSectionCounter).getAct().addNewCode();
                    }
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(performerSectionCounter).getAct().getCode().setCode("PSN");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(performerSectionCounter).getAct().getCode().setCodeSystem("Local-codesystem-oid");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(performerSectionCounter).getAct().getCode().setCodeSystemName("LocalSystem");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(performerSectionCounter).getAct().getCode().setDisplayName("Interested Party");

                }
           }
        }


        /**
         * ORGANIZATION -- PHASE 1 TESTED
         * **/
        if(!input.getMsgOrganizations().isEmpty()) {
            // 474
            for(int i = 0; i < input.getMsgOrganizations().size(); i++) {

                int c = 0;
                if (clinicalDocument.getComponent().getStructuredBody().getComponentArray().length == 0) {
                    clinicalDocument.getComponent().getStructuredBody().addNewComponent();
                } else {
                    c = clinicalDocument.getComponent().getStructuredBody().getComponentArray().length;
                    clinicalDocument.getComponent().getStructuredBody().addNewComponent();
                }
                if (clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection() == null) {
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).addNewSection();
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewCode();
                } else {
                    if (clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection() == null) {
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewCode();
                    }
                }

                if (performerComponentCounter < 1) {
                    componentCounter++;
                    performerComponentCounter = componentCounter;
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setCode("123-4567");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setCodeSystem("Local-codesystem-oid");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setCodeSystemName("LocalSystem");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setDisplayName("Interested Parties Section");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().set(MapToCData("INTERESTED PARTIES SECTION"));


                }
                performerSectionCounter = clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray().length;
                if ( clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray().length == 0) {
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewEntry();
                    performerSectionCounter = 0;
                }
                else {
                    performerSectionCounter = clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray().length;
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewEntry();
                }


                if (clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(performerSectionCounter).getAct() == null) {
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(performerSectionCounter).addNewAct();
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(performerSectionCounter).getAct().addNewParticipant();
                } else {
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(performerSectionCounter).getAct().addNewParticipant();
                }

                // CHECK MapToORG
                POCDMT000040Participant2 out = clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(performerSectionCounter).getAct().getParticipantArray(0);
                POCDMT000040Participant2 output = MapToORG(input.getMsgOrganizations().get(i), out);
                clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(performerSectionCounter).getAct().setParticipantArray(0, output);

                clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(performerSectionCounter).setTypeCode(XActRelationshipEntry.COMP);
                clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(performerSectionCounter).getAct().setClassCode(XActClassDocumentEntryAct.ACT);
                clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(performerSectionCounter).getAct().setMoodCode(XDocumentActMood.EVN);

                if (clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(performerSectionCounter).getAct().getCode() == null){
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(performerSectionCounter).getAct().addNewCode();
                }

                clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(performerSectionCounter).getAct().getCode().setCode("ORG");
                clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(performerSectionCounter).getAct().getCode().setCodeSystem("Local-codesystem-oid");
                clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(performerSectionCounter).getAct().getCode().setCodeSystemName("LocalSystem");
                clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(performerSectionCounter).getAct().getCode().setDisplayName("Interested Party");

            }
        }

        /**
         * PLACE -- TEST NEEDED
         * */
        if(!input.getMsgPlaces().isEmpty()) {
            // 498
            for(int i = 0; i < input.getMsgPlaces().size(); i++) {

                int c = 0;
                if (clinicalDocument.getComponent().getStructuredBody().getComponentArray().length == 0) {
                    clinicalDocument.getComponent().getStructuredBody().addNewComponent();
                } else {
                    c = clinicalDocument.getComponent().getStructuredBody().getComponentArray().length;
                    clinicalDocument.getComponent().getStructuredBody().addNewComponent();
                }
                if (clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection() == null) {
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).addNewSection();
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewCode();
                } else {
                    if (clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection() == null) {
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewCode();
                    }
                }


                if (performerComponentCounter < 1) {
                    componentCounter++;
                    performerComponentCounter = componentCounter;

                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setCode("123-4567");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setCodeSystem("Local-codesystem-oid");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setCodeSystemName("LocalSystem");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setDisplayName("Interested Parties Section");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().set(MapToCData("INTERESTED PARTIES SECTION"));
                }

                performerSectionCounter = clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray().length;

                if ( clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray().length == 0) {
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewEntry();
                    performerSectionCounter = 0;
                }
                else {
                    performerSectionCounter = clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray().length;
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewEntry();
                }


                if (clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(performerSectionCounter).getAct() == null) {
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(performerSectionCounter).addNewAct();
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(performerSectionCounter).getAct().addNewParticipant();
                } else {
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(performerSectionCounter).getAct().addNewParticipant();
                }

                // CHECK MapToPlace
                POCDMT000040Participant2 out = clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(performerSectionCounter).getAct().getParticipantArray(0);
                POCDMT000040Participant2 output = MapToPlace(input.getMsgPlaces().get(i), out);
                clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(performerSectionCounter).getAct().setParticipantArray(0, output);

                clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(performerSectionCounter).setTypeCode(XActRelationshipEntry.COMP);
                clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(performerSectionCounter).getAct().setClassCode(XActClassDocumentEntryAct.ACT);
                clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(performerSectionCounter).getAct().setMoodCode(XDocumentActMood.EVN);

                if (clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(performerSectionCounter).getAct().getCode() == null){
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(performerSectionCounter).getAct().addNewCode();
                }
                clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(performerSectionCounter).getAct().getCode().setCode("PLC");
                clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(performerSectionCounter).getAct().getCode().setCodeSystem("Local-codesystem-oid");
                clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(performerSectionCounter).getAct().getCode().setCodeSystemName("LocalSystem");
                clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(performerSectionCounter).getAct().getCode().setDisplayName("Interested Party");

            }
        }

        var t = printXmlForTesting(clinicalDocument);


        /**
         * INTERVIEW -- TEST NEEDED
         * */
        if(!input.getMsgInterviews().isEmpty()) {
            // 523
            for(int i = 0; i < input.getMsgInterviews().size(); i++) {

                int c = 0;
                if (clinicalDocument.getComponent().getStructuredBody().getComponentArray().length == 0) {
                    clinicalDocument.getComponent().getStructuredBody().addNewComponent();
                } else {
                    c = clinicalDocument.getComponent().getStructuredBody().getComponentArray().length;
                    clinicalDocument.getComponent().getStructuredBody().addNewComponent();
                }
                if (clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection() == null) {
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).addNewSection();
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewCode();
                } else {
                    if (clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection() == null) {
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewCode();
                    }
                }


                if (interviewCounter < 1) {
                    interviewCounter = componentCounter + 1;
                    componentCounter++;

                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setCode("IXS");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setCodeSystem("Local-codesystem-oid");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setCodeSystemName("LocalSystem");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setDisplayName("Interviews");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().set(MapToCData("INTERVIEW SECTION"));
                }

                POCDMT000040Component3 ot = clinicalDocument.getComponent().getStructuredBody().getComponentArray(c);
                // CHECK MapToInterview

                POCDMT000040Component3 output = MapToInterview(input.getMsgInterviews().get(i), ot);
                clinicalDocument.getComponent().getStructuredBody().setComponentArray(c, output);
            }
        }

        /**
         * TREATMENT -- REVIEW NEEDED
         * */
        if(!input.getMsgTreatments().isEmpty()) {
            // 543
            for(int i = 0; i < input.getMsgTreatments().size(); i++) {

                int c = 0;
                if (clinicalDocument.getComponent().getStructuredBody().getComponentArray().length == 0) {
                    clinicalDocument.getComponent().getStructuredBody().addNewComponent();
                } else {
                    c = clinicalDocument.getComponent().getStructuredBody().getComponentArray().length;
                    clinicalDocument.getComponent().getStructuredBody().addNewComponent();
                }
                if (clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection() == null) {
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).addNewSection();
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewCode();
                } else {
                    if (clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection() == null) {
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewCode();
                    }
                }



                if (treatmentCounter < 1) {
                    treatmentCounter++;
                    componentCounter++;
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setCode("55753-8");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setCodeSystem("2.16.840.1.113883.6.1");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setCodeSystemName("LOINC");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setDisplayName("Treatment Information");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().set(MapToCData("TREATMENT INFORMATION"));
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().set(MapToCData("CDA Treatment Information Section"));
                }

                clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(treatmentSectionCounter).getSubstanceAdministration().getStatusCode().setCode("active");
                clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(treatmentSectionCounter).getSubstanceAdministration().getEntryRelationshipArray(0).setTypeCode(XActRelationshipEntryRelationship.COMP);
                var outpp = clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(treatmentSectionCounter)
                        .getSubstanceAdministration();
                String treatmentvalue = "";
                clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(treatmentSectionCounter).getSubstanceAdministration().setClassCode("SBADM");
                clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(treatmentSectionCounter).getSubstanceAdministration().setMoodCode(XDocumentSubstanceMood.EVN);
                clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(treatmentSectionCounter).getSubstanceAdministration().setNegationInd(false);

                var o1 = clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(treatmentSectionCounter).getSubstanceAdministration();
                var o2 = clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getText();
                var mappedVal = MapToTreatment(input.getMsgTreatments().get(0),
                        o1,
                        o2,
                        treatmentSectionCounter);
                clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(treatmentSectionCounter).setSubstanceAdministration(mappedVal);
                treatmentSectionCounter= treatmentSectionCounter+1;
            }
        }


        String value ="";
        int k =0;

        // Custodian should be null as nothing initiate it on up stream process
        clinicalDocument.addNewCustodian().addNewAssignedCustodian().addNewRepresentedCustodianOrganization().addNewId();
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().addNewAddr();
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr();
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().addNewTelecom();


        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getIdArray(0).setExtension(MapToTranslatedValue("CUS101"));
        value = MapToTranslatedValue("CUS102");

        var element = clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization();
        MapToElementValue("CHECK MAP TO ELEMENT VALUE");

        value = MapToTranslatedValue("CUS103");
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().addNewStreetAddressLine();
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().getStreetAddressLineArray(k).set(MapToCData(value));
        k = k+1;
        value = MapToTranslatedValue("CUS104");
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().addNewStreetAddressLine();
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().getStreetAddressLineArray(k).set(MapToCData(value));
        k = k+1;

        k = 0;
        value = MapToTranslatedValue("CUS105");
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().addNewCity();
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().getCityArray(k).set(MapToCData(value));
        k = k+1;

        k = 0;
        value = MapToTranslatedValue("CUS106");
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().addNewState();
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().getStateArray(k).set(MapToCData(value));
        k = k+1;

        value = MapToTranslatedValue("CUS107");
        k = 0;
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().addNewPostalCode();
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().getPostalCodeArray(k).set(MapToCData(value));
        k = k+1;

        value = MapToTranslatedValue("CUS108");
        k = 0;
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().addNewCountry();
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().getCountryArray(k).set(MapToCData(value));
        k = k+1;

        value = MapToTranslatedValue("CUS109");
        k = 0;
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getTelecom().setValue(value);
        k = k+1;

        clinicalDocument.addNewAuthor().addNewAssignedAuthor();
        clinicalDocument.getAuthorArray(0).addNewTime();
        clinicalDocument.getAuthorArray(0).getAssignedAuthor().addNewId();

        value = MapToTranslatedValue("AUT101");
        clinicalDocument.getAuthorArray(0).getAssignedAuthor().getIdArray(0).setRoot(value);

        clinicalDocument.getAuthorArray(0).getAssignedAuthor().addNewAssignedPerson().addNewName();
        clinicalDocument.getAuthorArray(0).getAssignedAuthor().getAssignedPerson().getNameArray(0).addNewFamily();
        value = MapToTranslatedValue("AUT102");
        clinicalDocument.getAuthorArray(0).getAssignedAuthor().getAssignedPerson().getNameArray(0).getFamilyArray(0).set(MapToCData(value));
        clinicalDocument.getAuthorArray(0).getTime().setValue("DateChangeFormat(SQLServCurrentSmallDateTime(), \"yyyy-MM-dd HH:mm:ss\", \"HL7\");");






        XmlOptions options = new XmlOptions();
        options.setSavePrettyPrint();
        options.setSavePrettyPrintIndent(4);  // Set indentation

        // Use a default namespace instead of a prefixed one (like urn:)
        options.setUseDefaultNamespace();

        // Set to always use full tags instead of self-closing tags
        options.setSaveNoXmlDecl();
        options.setSaveOuter();

        String xmlOutput = clinicalDocument.xmlText(options);
        System.out.println(xmlOutput);
    }


    private String printXmlForTesting(POCDMT000040ClinicalDocument1 clinicalDocument) {
        XmlOptions options = new XmlOptions();
        options.setSavePrettyPrint();
        options.setSavePrettyPrintIndent(4);  // Set indentation

        // Use a default namespace instead of a prefixed one (like urn:)
        options.setUseDefaultNamespace();

        // Set to always use full tags instead of self-closing tags
        options.setSaveNoXmlDecl();
        options.setSaveOuter();

        String xmlOutput = clinicalDocument.xmlText(options);
        xmlOutput = xmlOutput.replaceAll("<to-be-remove>(.*?)</to-be-remove>", "$1");
        xmlOutput = xmlOutput.replaceAll("<to-be-remove xmlns=\".*?\">(.*?)</to-be-remove>", "$1");

        return xmlOutput;
    }

    private String MapToTranslatedValue(String input) {
        var res = ecrLookUpRepository.FetchConstantLookUpByCriteriaWithColumn("QuestionIdentifier", input);
        if (res != null && !res.getSampleValue().isEmpty()) {
            return res.getSampleValue();
        }
        else {
            return "NOT_FOUND";
        }
    }
    
    private POCDMT000040SubstanceAdministration MapToTreatment(
            EcrSelectedTreatment input, POCDMT000040SubstanceAdministration output,
            StrucDocText list,
            int counter) throws XmlException {
        String PROV="";
        String ORG="";
        String treatmentUid="";
        String TRT_TREATMENT_DT="";
        String TRT_FREQUENCY_AMT_CD="";
        String TRT_DOSAGE_UNIT_CD="";
        String TRT_DURATION_AMT="";
        String TRT_DURATION_UNIT_CD="";

        String treatmentName ="";
        String treatmentNameQuestion ="";

        String subjectAreaTRT ="TREATMENT";
        String customTreatment="";


        for (Map.Entry<String, Object> entry : input.getMsgTreatment().getDataMap().entrySet()) {
            String name = entry.getKey();
            String value = null;
            if (entry.getValue() != null) {
                value = entry.getValue().toString();
            }

            if(name.equals("trtTreatmentDt")  && value != null && input.getMsgTreatment().getTrtTreatmentDt() != null) {
                TRT_TREATMENT_DT= input.getMsgTreatment().getTrtTreatmentDt().toString();
            }

            if(name.equals("trtFrequencyAmtCd")  && value != null && input.getMsgTreatment().getTrtFrequencyAmtCd() != null && !input.getMsgTreatment().getTrtFrequencyAmtCd().isEmpty()) {
                TRT_FREQUENCY_AMT_CD= input.getMsgTreatment().getTrtFrequencyAmtCd();
            }

            if(name.equals("trtDosageUnitCd") && value != null && input.getMsgTreatment().getTrtDosageUnitCd() != null && !input.getMsgTreatment().getTrtDosageUnitCd().isEmpty()) {
                TRT_DOSAGE_UNIT_CD= input.getMsgTreatment().getTrtDosageUnitCd();
                output.getDoseQuantity().setUnit(TRT_DOSAGE_UNIT_CD);
            }

            if(name.equals("trtDosageAmt") && value != null && input.getMsgTreatment().getTrtDosageAmt() != null) {
                String dosageSt = input.getMsgTreatment().getTrtDosageAmt().toString();
                if(!dosageSt.isEmpty()) {
                    String dosageStQty = "";
                    String dosageStUnit = "";
                    String dosageStCodeSystemName = "";
                    String dosageStDisplayName = "";
                    // CHECK MapToTreatment
                    output.getDoseQuantity().setValue(input.getMsgTreatment().getTrtDosageAmt());
                }
            }

            if(name.equals("trtDrugCd") && value != null && input.getMsgTreatment().getTrtDrugCd() != null && !input.getMsgTreatment().getTrtDrugCd().isEmpty()) {
                treatmentNameQuestion = MapToQuestionId("TRT_DRUG_CD");;
                treatmentName = input.getMsgTreatment().getTrtDrugCd();
            }


            if(name.equals("trtLocalId")  && value != null&& input.getMsgTreatment().getTrtLocalId() != null && !input.getMsgTreatment().getTrtLocalId().isEmpty()) {
                output.getIdArray(0).setRoot("2.16.840.999999");
                output.getIdArray(0).setAssigningAuthorityName("LR");
                output.getIdArray(0).setExtension(input.getMsgTreatment().getTrtLocalId());
                treatmentUid=input.getMsgTreatment().getTrtLocalId();
            }

            if(name.equals("trtCustomTreatmentTxt")  && value != null && input.getMsgTreatment().getTrtCustomTreatmentTxt() != null && !input.getMsgTreatment().getTrtCustomTreatmentTxt().isEmpty()) {
                customTreatment= input.getMsgTreatment().getTrtCustomTreatmentTxt();
            }

            if(name.equals("trtCompositeCd")  && value != null && input.getMsgTreatment().getTrtCompositeCd() != null && !input.getMsgTreatment().getTrtCompositeCd().isEmpty()) {

            }

            if(name.equals("trtCommentTxt")  && value != null && input.getMsgTreatment().getTrtCommentTxt() != null && !input.getMsgTreatment().getTrtCommentTxt().isEmpty()) {

            }

            if(name.equals("trtDurationAmt") && value != null && input.getMsgTreatment().getTrtDurationAmt() != null) {
                TRT_DURATION_AMT = input.getMsgTreatment().getTrtDurationAmt().toString();
            }

            if(name.equals("trtDurationUnitCd") && value != null && input.getMsgTreatment().getTrtDurationUnitCd() != null && !input.getMsgTreatment().getTrtDurationUnitCd().isEmpty()) {
                TRT_DURATION_UNIT_CD = input.getMsgTreatment().getTrtDurationUnitCd();
            }
        }

        ///

        if(!customTreatment.isEmpty()){
            // CHECK MapToTreatment
            list.getListArray(counter).set(MapToCData(customTreatment));

        }else{
            // OutXML::Element element1= (OutXML::Element)list.item[counter];
        }

        if (!treatmentName.isEmpty()) {
            var ot = output.getConsumable().getManufacturedProduct().getManufacturedLabeledDrug().getCode();
            var ce = MapToCEAnswerType(
                    treatmentName,
                    treatmentNameQuestion
            );
            ot = ce;
            output.getConsumable().getManufacturedProduct().getManufacturedLabeledDrug().setCode(ot);

        } else {
            output.getConsumable().getManufacturedProduct().getManufacturedLabeledDrug().getCode().setNullFlavor("OTH");
            output.getConsumable().getManufacturedProduct().getManufacturedLabeledDrug().getName().set(MapToCData(customTreatment));
        }

        if(!TRT_TREATMENT_DT.isEmpty()){
            // CHECK MapToTreatment
            var lowElement = output.getEffectiveTimeArray(0);
            XmlCursor cursor = lowElement.newCursor();
            cursor.setAttributeText(new QName("http://www.w3.org/2001/XMLSchema-instance", "type"), "IVL_TS");
            cursor.beginElement(new QName("urn:hl7-org:v3", "low"));
            String newValue = MapToTsType(TRT_TREATMENT_DT).toString();
            cursor.insertAttributeWithValue("value", newValue);
            cursor.toEndToken();
            if (TRT_DURATION_AMT != null && !TRT_DURATION_AMT.isEmpty() && TRT_DURATION_UNIT_CD != null && !TRT_DURATION_UNIT_CD.isEmpty()) {
                cursor.beginElement(new QName("urn:hl7-org:v3", "width"));
                if (!TRT_DURATION_AMT.isEmpty()) {
                    cursor.insertAttributeWithValue("value", TRT_DURATION_AMT);
                }

                if (!TRT_DURATION_UNIT_CD.isEmpty()) {
                    cursor.insertAttributeWithValue("unit", TRT_DURATION_UNIT_CD);
                }

                cursor.toEndToken();  // Move to the end of the current element (width)
            }

            cursor.dispose();

            output.setEffectiveTimeArray(0, lowElement);
        }

        if (!TRT_FREQUENCY_AMT_CD.isEmpty()) {
            // CHECK MapToTreatment
            var element = output.getEffectiveTimeArray(1);
            XmlCursor cursor = element.newCursor();

            cursor.setAttributeText(new QName("http://www.w3.org/2001/XMLSchema-instance", "type"), "PIVL_TS");

            cursor.beginElement(new QName("urn:hl7-org:v3", "period"));

            String hertz = TRT_FREQUENCY_AMT_CD;
            AttributeMapper res = MapToAttributes(hertz);
            if (cursor.toFirstAttribute()) {
                cursor.setName(new QName("value"));
                cursor.setTextValue(res.getAttribute1());
            }
            if (cursor.toNextAttribute()) {
                cursor.setName(new QName("unit"));
                cursor.setTextValue(res.getAttribute2());
            }

            cursor.dispose();

            output.setEffectiveTimeArray(1, element);
        }

        int org = 0;
        int provider= 0;
        int performerCounter=0;
        if (input.getMsgTreatmentOrganizations().size() > 0 ||  input.getMsgTreatmentProviders().size() > 0) {
            for(int i = 0; i < input.getMsgTreatmentOrganizations().size(); i++) {
                var ot = output.getParticipantArray(performerCounter);
                var mappedVal = MapToORG( input.getMsgTreatmentOrganizations().get(i), ot);
                output.setParticipantArray(performerCounter, mappedVal);
                output.getParticipantArray(performerCounter).getParticipantRole().getIdArray(0).setAssigningAuthorityName("LR_ORG");
                performerCounter++;
                org = 1;
            }

            for(int i = 0; i < input.getMsgTreatmentProviders().size(); i++) {
                var ot = output.getParticipantArray(performerCounter);
                var mappedVal = MapToPSN(input.getMsgTreatmentProviders().get(i), ot);
                output.getParticipantArray(performerCounter).getParticipantRole().getIdArray(0).setAssigningAuthorityName("LR_ORG");
                performerCounter++;
                provider = 1;
            }
        }

        return output;
    }

    private AttributeMapper MapToAttributes(String input) {
        AttributeMapper model = new AttributeMapper();
        if (!input.isEmpty()) {
            if (input.equals("BID")) {
                model.setAttribute1("12");
                model.setAttribute2("h");
            } else if (input.equals("5ID")) {
                model.setAttribute1("4.5");
                model.setAttribute2("h");
            } else if (input.equals("TID")) {
                model.setAttribute1("8");
                model.setAttribute2("h");
            } else if (input.equals("QW")) {
                model.setAttribute1("1");
                model.setAttribute2("wk");
            } else if (input.equals("QID")) {
                model.setAttribute1("6");
                model.setAttribute2("h");
            } else if (input.equals("QD")) {
                model.setAttribute1("1");
                model.setAttribute2("d");
            } else if (input.equals("Q8H")) {
                model.setAttribute1("8");
                model.setAttribute2("h");
            } else if (input.equals("Q6H")) {
                model.setAttribute1("6");
                model.setAttribute2("h");
            } else if (input.equals("Q5D")) {
                model.setAttribute1("1.4");
                model.setAttribute2("d");
            } else if (input.equals("Q4H")) {
                model.setAttribute1("4");
                model.setAttribute2("h");
            } else if (input.equals("Q3D")) {
                model.setAttribute1("3.5");
                model.setAttribute2("d");
            } else if (input.equals("Once")) {
                model.setAttribute1("24");
                model.setAttribute2("h");
            } else if (input.equals("Q12H")) {
                model.setAttribute1("12");
                model.setAttribute2("h");
            }

        }
        return model;
    }

    private POCDMT000040Component3 MapToInterview(EcrSelectedInterview in, POCDMT000040Component3 out) throws XmlException {
        int repeatCounter=0;
        int sectionEntryCounter= out.getSection().getEntryArray().length;

        out.getSection().getEntryArray(sectionEntryCounter).getEncounter().setClassCode("ENC");
        out.getSection().getEntryArray(sectionEntryCounter).getEncounter().setMoodCode(XDocumentEncounterMood.EVN);
        out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getCode().setCode("54520-2");
        out.getSection().getEntryArray(0).getEncounter().getIdArray(0).setRoot("LR");

        int entryCounter= 0;
        int outerEntryCounter= 1;
        String IXS_INTERVIEWER_ID="";
        String interviewer = "";

        for (Map.Entry<String, Object> entry : in.getMsgInterview().getDataMap().entrySet()) {

            String name = entry.getKey();
            String value = null;
            if (entry.getValue() != null) {
                value = entry.getValue().toString();
            }

            if((name.equals("msgContainerUid") && value != null && in.getMsgInterview().getMsgContainerUid() != null )
                    || (name.equals("ixsAuthorId")  && value != null  && !in.getMsgInterview().getIxsAuthorId().isEmpty() )
                    || (name.equals("ixsEffectiveTime")  && value != null  && in.getMsgInterview().getIxsEffectiveTime() != null)){
                // CHECK MapToInterview
                out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getIdArray(0).setExtension(in.getMsgInterview().getMsgContainerUid().toString());
            }
            else if (name.equals("ixsLocalId")  && value != null && !in.getMsgInterview().getIxsLocalId().isEmpty()){
                out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getIdArray(0).setExtension(in.getMsgInterview().getIxsLocalId());
            }

            else if (name.equals("ixsStatusCd")  && value != null && !in.getMsgInterview().getIxsStatusCd().isEmpty()){
                out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getStatusCode().setCode(in.getMsgInterview().getIxsStatusCd());
            }
            else if (name.equals("ixsInterviewDt")  && value != null && in.getMsgInterview().getIxsInterviewDt() != null){
                var ts = MapToTsType(in.getMsgInterview().getIxsInterviewDt().toString());
                out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEffectiveTime().setValue(ts.toString());
            }

            else if (name.equals("ixsIntervieweeRoleCd")  && value != null && !in.getMsgInterview().getIxsIntervieweeRoleCd().isEmpty()){
                String questionCode = MapToQuestionId("IXS_INTERVIEWEE_ROLE_CD");

                out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray(entryCounter).setTypeCode(XActRelationshipEntryRelationship.COMP);
                var obs = out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray(entryCounter).getObservation();
                MapToObservation(questionCode, in.getMsgInterview().getIxsIntervieweeRoleCd(), obs);
                out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray(entryCounter).setObservation(obs);
                entryCounter= entryCounter+ 1;
            }
            else if (name.equals("ixsInterviewTypeCd")  && value != null && !in.getMsgInterview().getIxsInterviewTypeCd().isEmpty()){
                String questionCode = MapToQuestionId("IXS_INTERVIEW_TYPE_CD");

                out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray(entryCounter).setTypeCode(XActRelationshipEntryRelationship.COMP);
                var obs = out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray(entryCounter).getObservation();
                MapToObservation(questionCode, in.getMsgInterview().getIxsInterviewTypeCd(), obs);
                out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray(entryCounter).setObservation(obs);
                entryCounter= entryCounter+ 1;

            }
            else if (name.equals("ixsInterviewLocCd")  && value != null && !in.getMsgInterview().getIxsInterviewLocCd().isEmpty()){
                String questionCode = MapToQuestionId("IXS_INTERVIEW_LOC_CD");

                out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray(entryCounter).setTypeCode(XActRelationshipEntryRelationship.COMP);
                var obs = out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray(entryCounter).getObservation();
                MapToObservation(questionCode, in.getMsgInterview().getIxsInterviewLocCd(), obs);
                out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray(entryCounter).setObservation(obs);
                entryCounter= entryCounter+ 1;
            }
        }

        int questionGroupCounter=0;
        int componentCounter=0;
        int answerGroupCounter=0;
        String OldQuestionId="CHANGED";
        String OldRepeatQuestionId="CHANGED";
        int sectionCounter = 0;
        int repeatComponentCounter=0;
        int providerRoleCounter=0;


        if (!in.getMsgInterviewProviders().isEmpty() || !in.getMsgInterviewAnswers().isEmpty() || !in.getMsgInterviewAnswerRepeats().isEmpty()) {

            for(int i = 0; i < in.getMsgInterviewProviders().size(); i++) {
                var element = out.getSection().getEntryArray(sectionCounter).getEncounter().getParticipantArray(providerRoleCounter);
                var ot = MapToPSN(in.getMsgInterviewProviders().get(i),
                        element);
                out.getSection().getEntryArray(sectionCounter).getEncounter().setParticipantArray(providerRoleCounter, ot);
                var element2 = out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getParticipantArray(providerRoleCounter)
                        .getParticipantRole().getCode();
                CE ce = MapToCEQuestionType("IXS102", element2);
                out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getParticipantArray(providerRoleCounter)
                        .getParticipantRole().setCode(ce);
                providerRoleCounter=providerRoleCounter+1;
            }
            for(int i = 0; i < in.getMsgInterviewAnswers().size(); i++) {
                String newQuestionId="";
                var element = out.getSection().getEntryArray(sectionCounter).getEncounter();
                var ot = MapToInterviewObservation(in.getMsgInterviewAnswers().get(i), entryCounter, OldQuestionId,
                        element );

                entryCounter = ot.getCounter();
                OldQuestionId = ot.getQuestionSeq();
                out.getSection().getEntryArray(sectionCounter).setEncounter(ot.getComponent());

                if(newQuestionId.equals(OldQuestionId)){
                }
                else{
                    OldQuestionId=newQuestionId;
                }
            }
            for(int i = 0; i < in.getMsgInterviewAnswerRepeats().size(); i++) {
                var element = out.getSection().getEntryArray(sectionEntryCounter).getEncounter();
                var mapped = MapToInterviewMultiSelectObservation(in.getMsgInterviewAnswerRepeats().get(i),
                        answerGroupCounter,
                        questionGroupCounter,
                        sectionCounter,
                        OldRepeatQuestionId,
                        element);

                answerGroupCounter = mapped.getAnswerGroupCounter();
                questionGroupCounter = mapped.getQuestionGroupCounter();
                sectionCounter = mapped.getSectionCounter();
                OldRepeatQuestionId = mapped.getQuestionId();
                out.getSection().getEntryArray(sectionEntryCounter).setEncounter(mapped.getComponent());

            }
        }


        return out;
    }

    private InterviewAnswerMultiMapper MapToInterviewMultiSelectObservation(EcrMsgInterviewAnswerRepeatDto in,
                                                                            Integer answerGroupCounter,
                                                                            Integer questionGroupCounter,
                                                                            Integer sectionCounter,
                                                                            String questionId,
                                                                            POCDMT000040Encounter out) {
        int componentCounter = 0;
        String dataType="DATE";
        int seqNbr = 0;
        int questionGroupSeqNbr = 0;
        int answerGroupSeqNbr = 0;
        String questionIdentifier="";

        InterviewAnswerMultiMapper model = new InterviewAnswerMultiMapper();

        for (Map.Entry<String, Object> entry : in.getDataMap().entrySet()) {
            String name = entry.getKey();
            String value = null;
            if (entry.getValue() != null) {
                value = entry.getValue().toString();
            }

            if(name.equals("questionGroupSeqNbr") && !in.getQuestionGroupSeqNbr().isEmpty()){
                questionGroupSeqNbr= Integer.valueOf(in.getQuestionGroupSeqNbr());
            }
            else if(name.equals("answerGroupSeqNbr") && !in.getAnswerGroupSeqNbr().isEmpty()){
                answerGroupSeqNbr= Integer.valueOf(in.getAnswerGroupSeqNbr());
                if((answerGroupSeqNbr==answerGroupCounter) &&
                        (questionGroupSeqNbr ==questionGroupCounter))
                {
                    componentCounter = out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray().length;
                }
                else
                {
                    sectionCounter = out.getEntryRelationshipArray().length;
                    questionGroupCounter=questionGroupSeqNbr ;
                    answerGroupCounter=answerGroupSeqNbr;

                    out.getEntryRelationshipArray(sectionCounter).getOrganizer().getCode().setCode(String.valueOf(questionGroupSeqNbr));
                    out.getEntryRelationshipArray(sectionCounter).getOrganizer().getCode().setCode("1234567-RPT");
                    out.getEntryRelationshipArray(sectionCounter).getOrganizer().getCode().setCodeSystem("Local-codesystem-oid");
                    out.getEntryRelationshipArray(sectionCounter).getOrganizer().getCode().setCodeSystemName("LocalSystem");
                    out.getEntryRelationshipArray(sectionCounter).getOrganizer().getCode().setDisplayName("Generic Repeating Questions Section");

                    out.getEntryRelationshipArray(sectionCounter).setTypeCode(XActRelationshipEntryRelationship.COMP);
                    out.getEntryRelationshipArray(sectionCounter).getOrganizer().setClassCode(XActClassDocumentEntryOrganizer.CLUSTER);
                    out.getEntryRelationshipArray(sectionCounter).getOrganizer().setMoodCode("EVN");
                    out.getEntryRelationshipArray(sectionCounter).getOrganizer().getStatusCode().setCode("completed");
                    componentCounter=0;

                }
            }

            else if(name.equals("dataType") && !in.getDataType().isEmpty() ){
                dataType= in.getDataType();
            }else if(name.equals("seqNbr") && !in.getSeqNbr().isEmpty()){
                seqNbr= Integer.valueOf(in.getSeqNbr()) ;
            }

            if(dataType.equalsIgnoreCase("CODED") || dataType.equalsIgnoreCase("CODED_COUNTY")){
                CE ce = CE.Factory.newInstance();
                if (name.equals("answerTxt") && !in.getAnswerTxt().isEmpty()) {
                    ce.getTranslationArray(0).setCode(in.getAnswerTxt());
                }
                else if (name.equals("ansCodeSystemCd") && !in.getAnsCodeSystemCd().isEmpty()) {
                    ce.getTranslationArray(0).setCodeSystem(in.getAnsCodeSystemCd());
                }
                else if (name.equals("ansCodeSystemDescTxt") && !in.getAnsCodeSystemDescTxt().isEmpty()) {
                    ce.getTranslationArray(0).setCodeSystemName(in.getAnsCodeSystemDescTxt());
                }
                else if (name.equals("ansDisplayTxt") && !in.getAnsDisplayTxt().isEmpty()) {
                    ce.getTranslationArray(0).setDisplayName(in.getAnsDisplayTxt());
                }
                else if (name.equals("ansToCode") && !in.getAnsToCode().isEmpty()) {
                    ce.setCode(in.getAnsToCode());
                }
                else if (name.equals("ansToCodeSystemCd") && !in.getAnsToCodeSystemCd().isEmpty()) {
                    ce.setCodeSystem(in.getAnsToCodeSystemCd());
                }
                else if (name.equals("ansToCodeSystemDescTxt") && !in.getAnsToCodeSystemDescTxt().isEmpty()) {
                    ce.setCodeSystemName(in.getAnsToCodeSystemDescTxt());
                }
                else if (name.equals("ansToDisplayNm") && !in.getAnsToDisplayNm().isEmpty()) {
                    ce.setDisplayName(in.getAnsToDisplayNm());

                }
                out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().getValueArray(seqNbr).set(ce);
            }
            else if ((dataType.equalsIgnoreCase("TEXT") || dataType.equalsIgnoreCase("NUMERIC")) &&
                    name.equals("answerTxt")){
                if(questionIdentifier.equalsIgnoreCase("NBS243") ||
                        questionIdentifier.equalsIgnoreCase("NBS290")) {

                    var element = out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation();
                    var ot = MapToObservationPlace(value,
                            element);
                    out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).setObservation((POCDMT000040Observation) ot);

                }
                else {
                    var element = out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation();

                    var ot = MapToSTValue(value,element);
                    out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).setObservation((POCDMT000040Observation)ot);
                }
            }
            else if(dataType.equalsIgnoreCase("DATE")){
                if(name.equals("answerTxt")){
                    var element = out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().getValueArray(0);
                    XmlCursor cursor = element.newCursor();
                    cursor.toFirstChild();
                    cursor.setAttributeText(new QName("http://www.w3.org/2001/XMLSchema-instance", "type"), "TS");
                    cursor.setAttributeText(new QName("", "value"), null);
                    if (name.equals("answerTxt")) {
                        String newValue = MapToTsType(in.getAnswerTxt()).toString();
                        cursor.setAttributeText(new QName("", "value"), newValue);
                    }
                    cursor.dispose();
                }
            }


            if(name.equals("questionIdentifier")){
                questionIdentifier= value;
                if(value.equals(questionId)){
                    //Test
                }else{
                    if(questionId=="CHANGED"){

                    }else{
                        sectionCounter =  sectionCounter+1;
                    }

                    questionId =value;
                }

                out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().setClassCode("OBS");
                out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().setMoodCode(XActMoodDocumentObservation.EVN);
                out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().getCode().setCode(value);
            }
            else if(name.equals("quesCodeSystemCd")){
                out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().getCode().setCodeSystem(value);
            }
            else if(name.equals("quesCodeSystemDescTxt")){
                out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().getCode().setCodeSystemName(value);
            }
            else if(name.equals("quesDisplayTxt")){
                out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().getCode().setDisplayName(value);
            }
        }

        model.setAnswerGroupCounter(answerGroupCounter);
        model.setQuestionGroupCounter(questionGroupCounter);
        model.setSectionCounter(sectionCounter);
        model.setQuestionId(questionId);
        model.setComponent(out);

        return model;
    }

    private InterviewAnswerMapper MapToInterviewObservation(EcrMsgInterviewAnswerDto in, int counter,
                                                            String questionSeq,
                                                            POCDMT000040Encounter out) throws XmlException {
        String dataType="";
        //string hl7DataType="CWE";
        int sequenceNbr = 0;
        int questionGroupSeqNbr = 0;
        int answerGroupSeqNbr = 0;

        InterviewAnswerMapper model = new InterviewAnswerMapper();

        for (Map.Entry<String, Object> entry : in.getDataMap().entrySet()) {
            String name = entry.getKey();
            String value = null;
            if (entry.getValue() != null) {
                value = entry.getValue().toString();
            }

            if(name.equals("questionGroupSeqNbr") && !in.getQuestionGroupSeqNbr().isEmpty()){
                questionGroupSeqNbr= Integer.valueOf(in.getQuestionGroupSeqNbr());
            }
            else if(name.equals("answerGroupSeqNbr") && !in.getAnswerGroupSeqNbr().isEmpty() ){
                answerGroupSeqNbr= Integer.valueOf(in.getAnswerGroupSeqNbr());
            }
            else if(name.equals("dataType") && !in.getDataType().isEmpty()){
                dataType=in.getDataType();
            }
            else if(name.equals("seqNbr") && !in.getSeqNbr().isEmpty()){
                sequenceNbr= Integer.valueOf(in.getSeqNbr());
                if(sequenceNbr>0) {
                    sequenceNbr =sequenceNbr-1;
                }
            }

            if(dataType.equalsIgnoreCase("CODED") || dataType.equalsIgnoreCase("COUNTY")){
                CE ce = CE.Factory.newInstance();
                if (name.equals("answerTxt") && !in.getAnswerTxt().isEmpty()) {
                    ce.getTranslationArray(0).setCode(in.getAnswerTxt());
                }
                else if (name.equals("ansCodeSystemCd") && !in.getAnsCodeSystemCd().isEmpty()) {
                    ce.getTranslationArray(0).setCodeSystem(in.getAnsCodeSystemCd());
                }
                else if (name.equals("ansCodeSystemDescTxt") && !in.getAnsCodeSystemDescTxt().isEmpty()) {
                    ce.getTranslationArray(0).setCodeSystemName(in.getAnsCodeSystemDescTxt());
                }
                else if (name.equals("ansDisplayTxt") && !in.getAnsDisplayTxt().isEmpty()) {
                    ce.getTranslationArray(0).setDisplayName(in.getAnsDisplayTxt());
                }
                else if (name.equals("ansToCode") && !in.getAnsToCode().isEmpty()) {
                    ce.setCode(in.getAnsToCode());
                }
                else if (name.equals("ansToCodeSystemCd") && !in.getAnsToCodeSystemCd().isEmpty()) {
                    ce.setCodeSystem(in.getAnsToCodeSystemCd());
                }
                else if (name.equals("ansToCodeSystemDescTxt") && !in.getAnsToCodeSystemDescTxt().isEmpty()) {
                    ce.setCodeSystemName(in.getAnsToCodeSystemDescTxt());
                }
                else if (name.equals("ansToDisplayNm") && !in.getAnsToDisplayNm().isEmpty()) {
                    ce.setDisplayName(in.getAnsToDisplayNm());

                }
                out.getEntryRelationshipArray(counter).getObservation().getValueArray(sequenceNbr).set(ce);

            }
            else if(
                    dataType.equals("TEXT") ||
                    dataType.equals("NUMERIC")){
                if(name.equals("answerTxt") && !in.getAnswerTxt().isEmpty()){
                    var element = out.getEntryRelationshipArray(counter).getObservation();
                    var ot = MapToSTValue(value, element);
                    out.getEntryRelationshipArray(counter).setObservation((POCDMT000040Observation) ot);
                }

            }
            else if(dataType.equalsIgnoreCase(  "DATE")){
                if(name.equals("answerTxt") && !in.getAnswerTxt().isEmpty()){
                    var element = out.getEntryRelationshipArray(counter).getObservation().getValueArray(0);
                    XmlCursor cursor = element.newCursor();
                    cursor.setAttributeText(new QName("http://www.w3.org/2001/XMLSchema-instance", "type"), "TS");
                    cursor.setAttributeText(new QName("name"), "value");  // This is an assumption based on the original code

                    if(name.equals("answerTxt") && !in.getAnswerTxt().isEmpty()){
                        String newValue = MapToTsType(in.getAnswerTxt()).toString();
                        cursor.setAttributeText(new QName("name"), "value");
                        cursor.setTextValue(newValue);
                    }
                    else {
                        element = out.getEntryRelationshipArray(counter).getObservation().getValueArray(0);
                        cursor = element.newCursor();
                        cursor.setAttributeText(new QName("http://www.w3.org/2001/XMLSchema-instance", "type"), "ST");

                        if(name.equals("answerTxt") && !in.getAnswerTxt().isEmpty()) {
                            cursor.setTextValue(MapToCData(in.getAnswerTxt()).toString());
                        }
                    }

                    out.getEntryRelationshipArray(counter).getObservation().setValueArray(0, element);
                    cursor.dispose();
                }
            }
            if(name.equals("questionIdentifier") && !in.getQuestionIdentifier().isEmpty()){
                if(in.getQuestionIdentifier().equals(value)){
                    //Test
                }else{
                    if(questionSeq=="CHANGED"){

                    }else{
                        counter =  counter+1;
                    }

                    questionSeq =value;

                    out.getEntryRelationshipArray(counter).getObservation().setClassCode("OBS");
                    out.getEntryRelationshipArray(counter).getObservation().setMoodCode(XActMoodDocumentObservation.EVN);
                    out.getEntryRelationshipArray(counter).getObservation().getCode().setCode(in.getQuestionIdentifier());
                }
            }
            else if(name.equals("quesCodeSystemCd") && !in.getQuesCodeSystemCd().isEmpty()){
                out.getEntryRelationshipArray(counter).getObservation().getCode().setCodeSystem(in.getQuesCodeSystemCd());
            }
            else if(name.equals("quesCodeSystemDescTxt") && !in.getQuesCodeSystemDescTxt().isEmpty()){
                out.getEntryRelationshipArray(counter).getObservation().getCode().setCodeSystemName(in.getQuesCodeSystemDescTxt());
            }
            else if(name.equals("quesDisplayTxt") && !in.getQuesDisplayTxt().isEmpty()){
                out.getEntryRelationshipArray(counter).getObservation().getCode().setDisplayName(in.getQuesDisplayTxt());
            }

        }

        model.setComponent(out);
        model.setCounter(counter);
        model.setQuestionSeq(questionSeq);
        return model;
    }



    private CE MapToCEQuestionType(String questionCode, CE output) {
        var ot = MapToCodedQuestionType(questionCode);
        output.setCodeSystem(ot.getQuesCodeSystemCd());
        output.setCodeSystemName(ot.getQuesCodeSystemDescTxt());
        output.setDisplayName(ot.getQuesDisplayName());
        output.setCode(questionCode);

        return output;
    }

    private POCDMT000040Participant2 MapToPlace(EcrMsgPlaceDto in, POCDMT000040Participant2 out) throws XmlException {
        String state="";
        String streetAddress1="";
        String streetAddress2="";
        String city = "";
        String county = "";
        String country = "";
        String zip = "";
        String workPhone= "";
        String workExtn = "";
        String workURL = "";
        String workEmail = "";
        String workCountryCode="";
        String placeComments="";
        String placeAddressComments="";
        int teleCounter=0;
        String teleAsOfDate="";
        String postalAsOfDate="";
        String censusTract="";

        for (Map.Entry<String, Object> entry : in.getDataMap().entrySet()) {
            String name = entry.getKey();
            String value = null;
            if (entry.getValue() != null) {
                value = entry.getValue().toString();
            }

            if(name.equals("plaLocalId") && value != null && !in.getPlaLocalId().isEmpty()){
                if (out.getParticipantRole() == null) {
                    out.addNewParticipantRole().addNewId();
                } else {
                    out.getParticipantRole().addNewId();
                }

                out.setTypeCode("PRF");
                out.getParticipantRole().getIdArray(0).setRoot("2.16.840.1.113883.4.6");
                out.getParticipantRole().getIdArray(0).setExtension(in.getPlaLocalId());
                out.getParticipantRole().getIdArray(0).setAssigningAuthorityName("LR");
            }
            if (name.equals("plaNameTxt") && value != null && !in.getPlaNameTxt().isEmpty()){
                if (out.getParticipantRole() == null) {
                    out.addNewParticipantRole().addNewPlayingEntity().addNewName();
                } else {
                    out.getParticipantRole().addNewPlayingEntity().addNewName();
                }

                PN val = PN.Factory.newInstance();
                val.set(MapToCData(in.getPlaNameTxt()));
                out.getParticipantRole().getPlayingEntity().addNewName();
                out.getParticipantRole().getPlayingEntity().setNameArray(0, val);
            }
            if (name.equals("plaAddrStreetAddr1Txt")&& value != null && !in.getPlaAddrStreetAddr1Txt().isEmpty()){
                streetAddress1= in.getPlaAddrStreetAddr1Txt();
            }
            if (name.equals("plaAddrStreetAddr2Txt")&& value != null && !in.getPlaAddrStreetAddr2Txt().isEmpty()){
                streetAddress2 =in.getPlaAddrStreetAddr2Txt();
            }
            if (name.equals("plaAddrCityTxt")&& value != null && !in.getPlaAddrCityTxt().isEmpty()){
                city= in.getPlaAddrCityTxt();
            }
            if (name.equals("plaAddrCountyCd")&& value != null && !in.getPlaAddrCountyCd().isEmpty()){
                county= in.getPlaAddrCountyCd();
            }
            if (name.equals("plaAddrStateCd")&& value != null && !in.getPlaAddrStateCd().isEmpty()){
                state= in.getPlaAddrStateCd();
            }
            if (name.equals("plaAddrZipCodeTxt")&& value != null && !in.getPlaAddrZipCodeTxt().isEmpty()){
                zip = in.getPlaAddrZipCodeTxt();
            }
            if (name.equals("plaAddrCountryCd")&& value != null && !in.getPlaAddrCountryCd().isEmpty()){
                country=in.getPlaAddrCountryCd();
            }
            if (name.equals("plaPhoneNbrTxt") && value != null&& !in.getPlaPhoneNbrTxt().isEmpty()){
                workPhone=in.getPlaPhoneNbrTxt();
            }
            if (name.equals("plaAddrAsOfDt") && value != null&& in.getPlaAddrAsOfDt() != null){
                postalAsOfDate=in.getPlaAddrAsOfDt().toString();
            }
            if (name.equals("plaCensusTractTxt") && value != null&& !in.getPlaCensusTractTxt().isEmpty()){
                censusTract=in.getPlaCensusTractTxt();
            }
            if (name.equals("plaPhoneAsOfDt") && value != null&& in.getPlaPhoneAsOfDt() != null ){
                teleAsOfDate=in.getPlaPhoneAsOfDt().toString();
            }
            if (name.equals("plaPhoneExtensionTxt") && value != null&& !in.getPlaPhoneExtensionTxt().isEmpty()){
                workExtn= in.getPlaPhoneExtensionTxt();
            }
            if (name.equals("plaCommentTxt") && value != null&& !in.getPlaCommentTxt().isEmpty()){
                placeAddressComments= in.getPlaCommentTxt();
            }
            if (name.equals("plaPhoneCountryCodeTxt") && value != null&& !in.getPlaPhoneCountryCodeTxt().isEmpty()){
                workCountryCode= in.getPlaPhoneCountryCodeTxt();
            }
            if (name.equals("plaEmailAddressTxt") && value != null&& !in.getPlaEmailAddressTxt().isEmpty()){
                workEmail= in.getPlaEmailAddressTxt();
            }
            if (name.equals("plaUrlAddressTxt") && value != null&& !in.getPlaUrlAddressTxt().isEmpty()){
                workURL= in.getPlaUrlAddressTxt();
            }
            if (name.equals("plaPhoneCommentTxt") && value != null&& !in.getPlaPhoneCommentTxt().isEmpty()){
                placeComments= in.getPlaPhoneCommentTxt();
            }
            if (name.equals("plaTypeCd") && value != null&& !in.getPlaTypeCd().isEmpty()){
                if (out.getParticipantRole() == null) {
                    out.addNewParticipantRole().addNewCode();
                } else {
                    out.getParticipantRole().addNewCode();
                }

                String questionCode= MapToQuestionId("PLA_TYPE_CD");
                out.getParticipantRole().addNewCode();
                out.getParticipantRole().setCode(MapToCEAnswerType(in.getPlaTypeCd(), questionCode));
            }
            if (name.equals("plaCommentTxt") && value != null&& !in.getPlaCommentTxt().isEmpty()){
                if (out.getParticipantRole() == null) {
                    out.addNewParticipantRole().addNewPlayingEntity().addNewDesc();
                } else {
                    out.getParticipantRole().addNewPlayingEntity().addNewDesc();
                }

                out.getParticipantRole().getPlayingEntity().getDesc().set(MapToCData(in.getPlaCommentTxt()));
            }

            if (name.equals("plaIdQuickCode") && value != null&& !in.getPlaIdQuickCode().isEmpty()){

                int c = 0;
                if (out.getParticipantRole() == null) {
                    out.addNewParticipantRole().addNewId();
                } else {
                    if (out.getParticipantRole().getIdArray().length > 0) {
                        c = out.getParticipantRole().getIdArray().length;
                    }
                    out.addNewParticipantRole().addNewId();
                }


                // Index is 1 in original code
                out.getParticipantRole().getIdArray(c).setRoot("2.16.840.1.113883.4.6");
                out.getParticipantRole().getIdArray(c).setExtension(in.getPlaIdQuickCode());
                out.getParticipantRole().getIdArray(c).setAssigningAuthorityName("LR_QEC");
            }

        }



        int isAddressPopulated= 0;
        if(!streetAddress1.isEmpty() ){
            AdxpStreetAddressLine val = AdxpStreetAddressLine.Factory.newInstance();
            val.set(MapToCData(streetAddress1));
            out.getParticipantRole().getAddrArray(0).addNewStreetAddressLine();
            out.getParticipantRole().getAddrArray(0).setStreetAddressLineArray(0, val);
            isAddressPopulated=1;
        }
        if(!streetAddress2.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }
            out.getParticipantRole().getAddrArray(0).addNewStreetAddressLine();
            if (out.getParticipantRole().getAddrArray().length > 1) {
                out.getParticipantRole().getAddrArray(0).setStreetAddressLineArray(1,  AdxpStreetAddressLine.Factory.newInstance());
                out.getParticipantRole().getAddrArray(0).getStreetAddressLineArray(1).set(MapToCData(streetAddress2));
            }
            else {
                out.getParticipantRole().getAddrArray(0).setStreetAddressLineArray(0,  AdxpStreetAddressLine.Factory.newInstance());
                out.getParticipantRole().getAddrArray(0).getStreetAddressLineArray(0).set(MapToCData(streetAddress2));
            }
            isAddressPopulated=1;
        }
        if(!city.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }

            out.getParticipantRole().getAddrArray(0).addNewCity();
            out.getParticipantRole().getAddrArray(0).setCityArray(0,  AdxpCity.Factory.newInstance());
            out.getParticipantRole().getAddrArray(0).getCityArray(0).set(MapToCData(city));

            isAddressPopulated=1;
        }
        if(!state.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }
            out.getParticipantRole().getAddrArray(0).addNewState();
            out.getParticipantRole().getAddrArray(0).setStateArray(0,  AdxpState.Factory.newInstance());
            out.getParticipantRole().getAddrArray(0).getStreetAddressLineArray(0).set(MapToCData(state  ));
            isAddressPopulated=1;
        }
        if(!county.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }
            out.getParticipantRole().getAddrArray(0).addNewCounty();

            out.getParticipantRole().getAddrArray(0).setCountyArray(0,  AdxpCounty.Factory.newInstance());
            out.getParticipantRole().getAddrArray(0).getCountyArray(0).set(MapToCData(county));
            isAddressPopulated=1;
        }
        if(!zip.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }
            out.getParticipantRole().getAddrArray(0).addNewPostalCode();

            out.getParticipantRole().getAddrArray(0).setPostalCodeArray(0,  AdxpPostalCode.Factory.newInstance());
            out.getParticipantRole().getAddrArray(0).getPostalCodeArray(0).set(MapToCData(zip   ));
            isAddressPopulated=1;
        }
        if(!country.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }
            AdxpCountry val = AdxpCountry.Factory.newInstance();
            val.set(MapToCData(country));
            out.getParticipantRole().getAddrArray(0).addNewCountry();

            out.getParticipantRole().getAddrArray(0).setCountryArray(0,  AdxpCountry.Factory.newInstance());
            out.getParticipantRole().getAddrArray(0).getCountryArray(0).set(MapToCData(country));
            isAddressPopulated=1;
        }
        if(!censusTract.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }
            out.getParticipantRole().getAddrArray(0).addNewCensusTract();

            out.getParticipantRole().getAddrArray(0).setCensusTractArray(0,  AdxpCensusTract.Factory.newInstance());
            out.getParticipantRole().getAddrArray(0).getCensusTractArray(0).set(MapToCData(censusTract));
        }
        if(isAddressPopulated>0){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }
            out.getParticipantRole().getAddrArray()[0].setUse(Arrays.asList("WP"));
            if(!postalAsOfDate.isEmpty()){
                // OutXML::Element element = (OutXML::Element)out.getParticipantRole().addr[0];
                // MapToUsableTSElement(postalAsOfDate, element, "useablePeriod");
                // CHECK MapToUsableTSElement
            }
        }
        if(!placeAddressComments.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }
            out.getParticipantRole().getAddrArray(0).addNewAdditionalLocator();

            out.getParticipantRole().getAddrArray(0).setAdditionalLocatorArray(0,  AdxpAdditionalLocator.Factory.newInstance());
            out.getParticipantRole().getAddrArray(0).getAdditionalLocatorArray(0).set(MapToCData(placeAddressComments));
        }

        if(!workPhone.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewTelecom();
            } else {
                out.getParticipantRole().addNewTelecom();
            }
            out.getParticipantRole().getTelecomArray(teleCounter).setUse(Arrays.asList("WP"));
            //workCountryCode
            int countryphoneCodeSize= workCountryCode.length();
            if(countryphoneCodeSize>0){
                workPhone=workCountryCode+"-"+ workPhone;
            }else
                workPhone=workPhone;


            int phoneExtnSize = workExtn.length();
            if(phoneExtnSize>0){
                workPhone=workPhone+ ";extn="+ workExtn;
            }else
                workPhone=workPhone;
            out.getParticipantRole().getTelecomArray(teleCounter).setValue(workPhone);

            if(!teleAsOfDate.isEmpty()){
                // OutXML::Element element = (OutXML::Element)out.getParticipantRole().telecom[teleCounter];
                // MapToUsableTSElement(teleAsOfDate, element, "useablePeriod");
                // CHECK MapToUsableTSElement
            }
            teleCounter = teleCounter+1;
        }
        if(!workEmail.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewTelecom();
            } else {
                out.getParticipantRole().addNewTelecom();
            }
            out.getParticipantRole().getTelecomArray(teleCounter).setUse(Arrays.asList("WP"));
            out.getParticipantRole().getTelecomArray(teleCounter).setValue("mailto:"+workEmail);
            if(!teleAsOfDate.isEmpty()){
                // OutXML::Element element = (OutXML::Element)out.getParticipantRole().telecom[teleCounter];
                // MapToUsableTSElement(teleAsOfDate, element, "useablePeriod");
                // CHECK MapToUsableTSElement
            }
            teleCounter = teleCounter +1;
        }
        if(!workURL.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewTelecom();
            } else {
                out.getParticipantRole().addNewTelecom();
            }
            out.getParticipantRole().getTelecomArray(teleCounter).setUse(Arrays.asList("WP"));
            out.getParticipantRole().getTelecomArray(teleCounter).setValue(workURL);
            if(!teleAsOfDate.isEmpty()){
                // OutXML::Element element = (OutXML::Element)out.getParticipantRole().telecom[teleCounter];
                // MapToUsableTSElement(teleAsOfDate, element, "useablePeriod");
                // CHECK MapToUsableTSElement
            }
            teleCounter=teleCounter+1;
        }
        return out;
    }

    private POCDMT000040Participant2 MapToORG(EcrMsgOrganizationDto in, POCDMT000040Participant2 out) throws XmlException {
        String state="";
        String streetAddress1="";
        String streetAddress2="";
        String city = "";
        String county = "";
        String country = "";
        String zip = "";
        String phone= "";
        String extn = "";

        out.setTypeCode("PRF");

        for (Map.Entry<String, Object> entry : in.getDataMap().entrySet()) {
            String name = entry.getKey();
            String value = null;
            if (entry.getValue() != null) {
                value = entry.getValue().toString();
            }

            if(name.equals("orgLocalId") && in.getOrgLocalId()!=null && !in.getOrgLocalId().isEmpty()){
                if (out.getParticipantRole() == null) {
                    out.addNewParticipantRole().addNewId();
                } else {
                    out.getParticipantRole().addNewId();
                }
                out.getParticipantRole().getIdArray(0).setRoot("2.16.840.1.113883.4.6");
                out.getParticipantRole().getIdArray(0).setExtension(in.getOrgLocalId());
                out.getParticipantRole().getIdArray(0).setAssigningAuthorityName("LR");
            }
            else if(name.equals("orgNameTxt") && in.getOrgNameTxt() != null && !in.getOrgNameTxt().isEmpty()){
                if (out.getParticipantRole() == null) {
                    out.addNewParticipantRole().addNewPlayingEntity();
                } else {
                    out.getParticipantRole().addNewPlayingEntity();
                }
                var val = MapToCData(in.getOrgNameTxt());
                out.getParticipantRole().getPlayingEntity().addNewName();
                out.getParticipantRole().getPlayingEntity().setNameArray(0,  PN.Factory.newInstance());
                out.getParticipantRole().getPlayingEntity().getNameArray(0).set(val);
            }
            else if(name.equals("orgAddrStreetAddr1Txt") && in.getOrgAddrStreetAddr1Txt() != null && !in.getOrgAddrStreetAddr1Txt().isEmpty()){
                streetAddress1= in.getOrgAddrStreetAddr1Txt();
            }
            else if(name.equals("orgAddrStreetAddr2Txt") && in.getOrgAddrStreetAddr2Txt() != null && !in.getOrgAddrStreetAddr2Txt().isEmpty()){
                streetAddress2 =in.getOrgAddrStreetAddr2Txt();
            }
            else if(name.equals("orgAddrCityTxt") && in.getOrgAddrCityTxt() !=null && !in.getOrgAddrCityTxt().isEmpty()){
                city= in.getOrgAddrCityTxt();
            }
            else if(name.equals("orgAddrCountyCd") && in.getOrgAddrCountyCd() != null && !in.getOrgAddrCountyCd().isEmpty()){
                county = MapToAddressType( in.getOrgAddrCountyCd(), "COUNTY");
            }
            else if (name.equals("orgAddrStateCd") && in.getOrgAddrStateCd() != null &&  !in.getOrgAddrStateCd().isEmpty()){
                state= MapToAddressType( in.getOrgAddrStateCd(), "STATE");
            }
            else if(name.equals("orgAddrZipCodeTxt") && in.getOrgAddrZipCodeTxt() != null && !in.getOrgAddrZipCodeTxt().isEmpty()){
                zip = in.getOrgAddrZipCodeTxt();
            }
            else if(name.equals("orgAddrCountryCd") && in.getOrgAddrCountryCd() != null && !in.getOrgAddrCountryCd().isEmpty()){
                country = MapToAddressType( in.getOrgAddrCountryCd(), "COUNTRY");
            }
            else if(name.equals("orgPhoneNbrTxt") && in.getOrgPhoneNbrTxt() != null && !in.getOrgPhoneNbrTxt().isEmpty()){
                phone=in.getOrgPhoneNbrTxt();
            }
            else if(name.equals("orgPhoneExtensionTxt") && in.getOrgPhoneExtensionTxt() != null && in.getOrgPhoneExtensionTxt() != null){
                extn= in.getOrgPhoneExtensionTxt().toString();
            }
            else if(name.equals("orgIdCliaNbrTxt") && in.getOrgIdCliaNbrTxt() != null && !in.getOrgIdCliaNbrTxt().isEmpty()){
                if (out.getParticipantRole() == null) {
                    out.addNewParticipantRole().addNewId();
                } else {
                    out.getParticipantRole().addNewId();
                }

                out.getParticipantRole().getIdArray(1).setRoot("2.16.840.1.113883.4.6");
                out.getParticipantRole().getIdArray(1).setExtension(in.getOrgIdCliaNbrTxt());
                out.getParticipantRole().getIdArray(1).setAssigningAuthorityName("LR_CLIA");
            }
        }



        int isAddressPopulated= 0;
        if(!streetAddress1.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }

            out.getParticipantRole().getAddrArray(0).addNewStreetAddressLine();
            out.getParticipantRole().getAddrArray(0).setStreetAddressLineArray(0, AdxpStreetAddressLine.Factory.newInstance());
            out.getParticipantRole().getAddrArray(0).getStreetAddressLineArray(0).set(MapToCData(streetAddress1));

            isAddressPopulated=1;
        }
        if(!streetAddress2.isEmpty() ){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }
            out.getParticipantRole().getAddrArray(0).addNewStreetAddressLine();
            if (out.getParticipantRole().getAddrArray(0).getStreetAddressLineArray().length > 1) {
                out.getParticipantRole().getAddrArray(0).setStreetAddressLineArray(1, AdxpStreetAddressLine.Factory.newInstance());
                out.getParticipantRole().getAddrArray(0).getStreetAddressLineArray(1).set(MapToCData(streetAddress2));
            }
            else {
                out.getParticipantRole().getAddrArray(0).setStreetAddressLineArray(0, AdxpStreetAddressLine.Factory.newInstance());
                out.getParticipantRole().getAddrArray(0).getStreetAddressLineArray(0).set(MapToCData(streetAddress2));
            }

            isAddressPopulated=1;
        }
        if(!city.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }

            out.getParticipantRole().getAddrArray(0).addNewCity();
            out.getParticipantRole().getAddrArray(0).setCityArray(0, AdxpCity.Factory.newInstance());
            out.getParticipantRole().getAddrArray(0).getCityArray(0).set(MapToCData(city));

            isAddressPopulated=1;
        }
        if(!state.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }

            out.getParticipantRole().getAddrArray(0).addNewState();

            out.getParticipantRole().getAddrArray(0).setStateArray(0, AdxpState.Factory.newInstance());
            out.getParticipantRole().getAddrArray(0).getStateArray(0).set(MapToCData(state));

            isAddressPopulated=1;
        }
        if(!county.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }

            out.getParticipantRole().getAddrArray(0).addNewCounty();

            out.getParticipantRole().getAddrArray(0).setCountyArray(0, AdxpCounty.Factory.newInstance());
            out.getParticipantRole().getAddrArray(0).getCountyArray(0).set(MapToCData(county));

            isAddressPopulated=1;
        }
        if(!zip.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }

            out.getParticipantRole().getAddrArray(0).addNewPostalCode();

            out.getParticipantRole().getAddrArray(0).setPostalCodeArray(0, AdxpPostalCode.Factory.newInstance());
            out.getParticipantRole().getAddrArray(0).getPostalCodeArray(0).set(MapToCData(zip));
            isAddressPopulated=1;
        }
        if(!country.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }

            out.getParticipantRole().getAddrArray(0).addNewCountry();

            out.getParticipantRole().getAddrArray(0).setCountryArray(0, AdxpCountry.Factory.newInstance());
            out.getParticipantRole().getAddrArray(0).getCountryArray(0).set(MapToCData(zip));

            isAddressPopulated=1;
        }
        if(isAddressPopulated>0)
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }
            out.getParticipantRole().getAddrArray(0).setUse(new ArrayList(Arrays.asList("WP")));


        if(!phone.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewTelecom();
            } else {
                out.getParticipantRole().addNewTelecom();
            }
            out.getParticipantRole().getTelecomArray(0).setUse(new ArrayList(Arrays.asList("WP")));
            int phoneExtnSize = extn.length();
            if(phoneExtnSize>0){
                phone=phone+ ";extn="+ extn;
            }else
                phone=phone;
            out.getParticipantRole().getTelecomArray(0).setValue(phone);

        }
        return out;
    }

    private POCDMT000040Participant2 MapToPSN(EcrMsgProviderDto in, POCDMT000040Participant2 out) throws XmlException {
        String firstName="";
        String lastName="";
        String suffix="";
        String degree="";
        String address1="";
        String address2="";
        String city="";
        String county="";
        String state="";
        String zip="";
        String country="";
        String telephone="";
        String extn="";
        String qec="";
        String email="";
        String prefix="";
        int teleCounter=0;

        out.setTypeCode("PRF");

        for (Map.Entry<String, Object> entry : in.getDataMap().entrySet()) {
            String name = entry.getKey();
            String value = null;
            if (entry.getValue() != null) {
                value = entry.getValue().toString();
            }

            if (name.equals("prvLocalId") && in.getPrvLocalId() != null && !in.getPrvLocalId().isEmpty()) {
                if (out.getParticipantRole() == null) {
                    out.addNewParticipantRole().addNewId();
                } else {
                    out.getParticipantRole().addNewId();
                }
                out.getParticipantRole().getIdArray(0).setExtension(in.getPrvLocalId());
                out.getParticipantRole().getIdArray(0).setRoot("2.16.840.1.113883.11.19745");
                out.getParticipantRole().getIdArray(0).setAssigningAuthorityName("LR");
            }
            else if (name.equals("prvNameFirstTxt") && in.getPrvNameFirstTxt() !=null && !in.getPrvNameFirstTxt().isEmpty()) {
                firstName = in.getPrvNameFirstTxt();
            }
            else if (name.equals("prvNamePrefixCd") && in.getPrvNamePrefixCd() != null && !in.getPrvNamePrefixCd().isEmpty()) {
                prefix = in.getPrvNamePrefixCd();
            }
            else if (name.equals("prvNameLastTxt") && in.getPrvNameLastTxt() != null && !in.getPrvNameLastTxt().isEmpty()) {
                prefix = in.getPrvNameLastTxt();
            }
            else if(name.equals("prvNameSuffixCd") && in.getPrvNameSuffixCd() != null && !in.getPrvNameSuffixCd().isEmpty()) {
                lastName = in.getPrvNameSuffixCd();
            }
            else if(name.equals("prvNameDegreeCd") && in.getPrvNameDegreeCd()!=null && !in.getPrvNameDegreeCd().isEmpty()) {
                degree = in.getPrvNameDegreeCd();
            }
            else if(name.equals("prvAddrStreetAddr1Txt") && in.getPrvAddrStreetAddr1Txt() !=null && !in.getPrvAddrStreetAddr1Txt().isEmpty()) {
                address1 = in.getPrvAddrStreetAddr1Txt();
            }
            else if(name.equals("prvAddrStreetAddr2Txt") && in.getPrvAddrStreetAddr2Txt() != null && !in.getPrvAddrStreetAddr2Txt().isEmpty()) {
                address2 = in.getPrvAddrStreetAddr2Txt();
            }
            else if(name.equals("prvAddrCityTxt") && in.getPrvAddrCityTxt() != null && !in.getPrvAddrCityTxt().isEmpty()) {
                city = in.getPrvAddrCityTxt();
            }
            if(name.equals("prvAddrCountyCd") && in.getPrvAddrCountyCd() != null && !in.getPrvAddrCountyCd().isEmpty()) {
                county = MapToAddressType(in.getPrvAddrCountyCd(), "COUNTY");
            }
            else if(name.equals("prvAddrStateCd") && in.getPrvAddrStateCd() != null  && !in.getPrvAddrStateCd().isEmpty()) {
                state = MapToAddressType(in.getPrvAddrStateCd(), "STATE");
            }
            else if(name.equals("prvAddrZipCodeTxt") && in.getPrvAddrZipCodeTxt() != null && !in.getPrvAddrZipCodeTxt().isEmpty()) {
                zip = in.getPrvAddrZipCodeTxt();
            }
            else if(name.equals("prvAddrCountryCd") && in.getPrvAddrCountryCd() != null && !in.getPrvAddrCountryCd().isEmpty()) {
                country = MapToAddressType(in.getPrvAddrCountryCd(), "COUNTRY");
            }
            else if(name.equals("prvPhoneNbrTxt") && in.getPrvPhoneNbrTxt() != null && !in.getPrvPhoneNbrTxt().isEmpty()) {
                telephone = in.getPrvPhoneNbrTxt();
            }
            else  if(name.equals("prvPhoneExtensionTxt") && in.getPrvPhoneExtensionTxt() != null) {
                extn = in.getPrvPhoneExtensionTxt().toString();
            }
            else if(name.equals("prvIdQuickCodeTxt") && in.getPrvIdQuickCodeTxt() != null && !in.getPrvIdQuickCodeTxt().isEmpty()) {
                if (out.getParticipantRole() == null) {
                    out.addNewParticipantRole().addNewId();
                } else {
                    out.getParticipantRole().addNewId();
                }
                out.getParticipantRole().getIdArray(1).setExtension(in.getPrvIdQuickCodeTxt());
                out.getParticipantRole().getIdArray(1).setRoot("2.16.840.1.113883.11.19745");
                out.getParticipantRole().getIdArray(1).setAssigningAuthorityName("LR_QEC");
            }
            else if(name.equals("prvEmailAddressTxt") && in.getPrvEmailAddressTxt() != null && !in.getPrvEmailAddressTxt().isEmpty()) {
                email = in.getPrvEmailAddressTxt();
            }

            /////
        }



        if(!firstName.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewPlayingEntity().addNewName();
            } else {
                out.getParticipantRole().addNewPlayingEntity().addNewName();
            }
            var mapVal = MapToCData(firstName);
            EnGiven enG = EnGiven.Factory.newInstance();
            enG.set(mapVal);
            out.getParticipantRole().getPlayingEntity().getNameArray(0).addNewGiven();
            out.getParticipantRole().getPlayingEntity().getNameArray(0).setGivenArray(0,  EnGiven.Factory.newInstance());
            out.getParticipantRole().getPlayingEntity().getNameArray(0).getGivenArray(0).set(mapVal);
            out.getParticipantRole().getPlayingEntity().getNameArray(0).setUse(new ArrayList(Arrays.asList("L")));
        }
        if(!lastName.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewPlayingEntity().addNewName();
            } else {
                out.getParticipantRole().addNewPlayingEntity().addNewName();
            }
            var mapVal = MapToCData(lastName);
            EnFamily enG = EnFamily.Factory.newInstance();
            enG.set(mapVal);
            out.getParticipantRole().getPlayingEntity().getNameArray(0).addNewFamily();
            out.getParticipantRole().getPlayingEntity().getNameArray(0).setFamilyArray(0,  EnFamily.Factory.newInstance());
            out.getParticipantRole().getPlayingEntity().getNameArray(0).getFamilyArray(0).set(mapVal);
            out.getParticipantRole().getPlayingEntity().getNameArray(0).setUse(new ArrayList(Arrays.asList("L")));
        }
        if(!prefix.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewPlayingEntity().addNewName();
            } else {
                out.getParticipantRole().addNewPlayingEntity().addNewName();
            }
            var mapVal = MapToCData(prefix);
            EnPrefix enG = EnPrefix.Factory.newInstance();
            enG.set(mapVal);
            out.getParticipantRole().getPlayingEntity().getNameArray(0).addNewPrefix();
            out.getParticipantRole().getPlayingEntity().getNameArray(0).setPrefixArray(0,  EnPrefix.Factory.newInstance());
            out.getParticipantRole().getPlayingEntity().getNameArray(0).getPrefixArray(0).set(mapVal);
        }
        if(!suffix.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewPlayingEntity().addNewName();
            } else {
                out.getParticipantRole().addNewPlayingEntity().addNewName();
            }
            var mapVal = MapToCData(suffix);
            EnSuffix enG = EnSuffix.Factory.newInstance();
            enG.set(mapVal);
            out.getParticipantRole().getPlayingEntity().getNameArray(0).addNewSuffix();

            out.getParticipantRole().getPlayingEntity().getNameArray(0).setSuffixArray(0,  EnSuffix.Factory.newInstance());
            out.getParticipantRole().getPlayingEntity().getNameArray(0).getSuffixArray(0).set(mapVal);
        }
        if(!address1.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }
            var mapVal = MapToCData(address1);
            AdxpStreetAddressLine enG = AdxpStreetAddressLine.Factory.newInstance();
            enG.set(mapVal);
            out.getParticipantRole().getAddrArray(0).addNewStreetAddressLine();
            out.getParticipantRole().getAddrArray(0).setStreetAddressLineArray(0,  AdxpStreetAddressLine.Factory.newInstance());
            out.getParticipantRole().getAddrArray(0).getStreetAddressLineArray(0).set(mapVal);
        }
        if(!address2.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }
            var mapVal = MapToCData(address2);
            AdxpStreetAddressLine enG = AdxpStreetAddressLine.Factory.newInstance();
            enG.set(mapVal);

            out.getParticipantRole().getAddrArray(0).addNewStreetAddressLine();
            if (out.getParticipantRole().getAddrArray(0).getStreetAddressLineArray().length > 1) {
                out.getParticipantRole().getAddrArray(0).setStreetAddressLineArray(1,  AdxpStreetAddressLine.Factory.newInstance());
                out.getParticipantRole().getAddrArray(0).getStreetAddressLineArray(1).set(mapVal);
            }
            out.getParticipantRole().getAddrArray(0).setStreetAddressLineArray(0,  AdxpStreetAddressLine.Factory.newInstance());
            out.getParticipantRole().getAddrArray(0).getStreetAddressLineArray(0).set(mapVal);
        }
        if(!city.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }
            var mapVal = MapToCData(city);
            AdxpCity enG = AdxpCity.Factory.newInstance();
            enG.set(mapVal);
            out.getParticipantRole().getAddrArray(0).addNewCity();

            out.getParticipantRole().getAddrArray(0).setCityArray(0,  AdxpCity.Factory.newInstance());
            out.getParticipantRole().getAddrArray(0).getCityArray(0).set(mapVal);
        }
        if(!county.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }
            var mapVal = MapToCData(county);
            AdxpCounty enG = AdxpCounty.Factory.newInstance();
            enG.set(mapVal);
            out.getParticipantRole().getAddrArray(0).addNewCounty();

            out.getParticipantRole().getAddrArray(0).setCountyArray(0,  AdxpCounty.Factory.newInstance());
            out.getParticipantRole().getAddrArray(0).getCountyArray(0).set(mapVal);
        }
        if(!zip.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }
            var mapVal = MapToCData(zip);
            AdxpPostalCode enG = AdxpPostalCode.Factory.newInstance();
            enG.set(mapVal);
            out.getParticipantRole().getAddrArray(0).addNewPostalCode();

            out.getParticipantRole().getAddrArray(0).setPostalCodeArray(0,  AdxpPostalCode.Factory.newInstance());
            out.getParticipantRole().getAddrArray(0).getPostalCodeArray(0).set(mapVal);
        }

        if(!state.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }
            var mapVal = MapToCData(state);
            AdxpState enG = AdxpState.Factory.newInstance();
            enG.set(mapVal);
            out.getParticipantRole().getAddrArray(0).addNewState();

            out.getParticipantRole().getAddrArray(0).setStateArray(0,  AdxpState.Factory.newInstance());
            out.getParticipantRole().getAddrArray(0).getStateArray(0).set(mapVal);
        }
        if(!country.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewAddr();
            } else {
                out.getParticipantRole().addNewAddr();
            }
            var mapVal = MapToCData(country);
            AdxpCountry enG = AdxpCountry.Factory.newInstance();
            enG.set(mapVal);
            out.getParticipantRole().getAddrArray(0).addNewCountry();

            out.getParticipantRole().getAddrArray(0).setCountryArray(0,  AdxpCountry.Factory.newInstance());
            out.getParticipantRole().getAddrArray(0).getCountryArray(0).set(mapVal);
        }
        if(!telephone.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewTelecom();
            } else {
                out.getParticipantRole().addNewTelecom();
            }
            out.getParticipantRole().getTelecomArray(teleCounter).setUse(new ArrayList(Arrays.asList("WP")));
            int phoneExtnSize= extn.length();
            if(phoneExtnSize>0){
                telephone=telephone+ ";extn="+ extn;
            }else
                telephone=telephone;
            out.getParticipantRole().getTelecomArray(teleCounter).setValue(telephone);
            teleCounter = teleCounter+1;
        } if(!email.isEmpty()){
            if (out.getParticipantRole() == null) {
                out.addNewParticipantRole().addNewTelecom();
            } else {
                out.getParticipantRole().addNewTelecom();
            }
            out.getParticipantRole().getTelecomArray(teleCounter).setUse(new ArrayList(Arrays.asList("WP")));
            out.getParticipantRole().getTelecomArray(teleCounter).setValue(email);
            teleCounter= teleCounter + 1;
        }



        return out;
    }

    private POCDMT000040Component3 MapToExtendedData(EcrMsgXmlAnswerDto in, POCDMT000040Component3 out) throws XmlException {
        // CHECK MapToExtendedData
        String dataType="";
        if (!in.getDataType().isEmpty()) {
            dataType = in.getDataType();
        }
        if (!in.getAnswerXmlTxt().isEmpty()) {
            XmlCursor cursor = out.newCursor();
            cursor.toEndToken(); // Move to the end token of the current element
            cursor.beginElement("childElementName"); // Replace "childElementName" with the name of your new child element
            cursor.insertChars(in.getAnswerXmlTxt());
            cursor.dispose();
        }
        return out;
    }



    private String MapToAddressType(String data, String questionCode) {
        String output = "";
        // Mapping To Code Anwser goes here, may have to call out to RhapsodyAnswer table
        // MapToCodedAnswer
        var answer = MapToCodedAnswer(data, questionCode);

        if (!answer.getCode().isEmpty()) {
            output = answer.getCode();
        }

        if (!answer.getDisplayName().isEmpty()) {
            output = output + "^" + answer.getDisplayName();
        }

        if (!answer.getCodeSystemName().isEmpty()) {
            output = output + "^" + answer.getCodeSystemName();
        }


        return output;

    }

    private PhdcAnswerDao MapToCodedAnswer(String data, String questionCode) {
        PhdcAnswerDao model = new PhdcAnswerDao();
        String translation="";
        String isTranslationReq= "YES";
        String code = "";
        String transCode = data;
        String transCodeSystem = "";
        String transCodeSystemName = "";
        String transDisplayName = "";
        String codeSystem = "";
        String codeSystemName = "";
        String displayName = "";

        // RhapsodyTableLookup(output, tableName, resultColumnName, defaultValue, queryColumn1, queryValue1, queryColumn2, queryValue2, ...)
        // DI - output = RhapsodyTableLookup(resultColumnName, defaultValue, queryColumn1, queryValue1, queryColumn2, queryValue2, ...)
        var phdcAnswer = ecrLookUpRepository.FetchPhdcAnswerByCriteriaForTranslationCode(questionCode, data);
        if (phdcAnswer != null) {
            isTranslationReq = phdcAnswer.getCodeTranslationRequired();
            code = phdcAnswer.getAnsToCode();
            transCodeSystem = phdcAnswer.getAnsFromCodeSystemCd();
            transCodeSystemName = phdcAnswer.getAnsFromCodeSystemCd();
            transDisplayName = phdcAnswer.getAnsFromDisplayNm();
            codeSystem = phdcAnswer.getAnsToCodeSystemCd();
            codeSystemName = phdcAnswer.getAnsToCodeSystemDescTxt();
            displayName = phdcAnswer.getAnsToDisplayNm();
        }
        else {
            transCodeSystem = "2.16.840.999999";
            codeSystem = "2.16.840.999999";
            isTranslationReq = "NOT_MAPPED";
            code = "NOT_MAPPED";
            transCodeSystemName = "NOT_MAPPED";
            transDisplayName = "NOT_MAPPED";
            codeSystemName ="NOT_MAPPED";
            displayName = "NOT_MAPPED";;
        }

        if (code.equalsIgnoreCase("NOT_MAPPED")) {
            code = data;
        }

        if (code.equalsIgnoreCase("NULL") || code.isEmpty()) {
            code = data ;
            codeSystem = transCodeSystem;
            codeSystemName = transCodeSystemName;
            displayName = transDisplayName;
        }

        model.setCode(code);
        model.setCodeSystem(codeSystem);
        model.setCodeSystemName(codeSystemName);
        model.setDisplayName(displayName);
        model.setTransCode(transCode);
        model.setTransCodeSystem(transCodeSystem);
        model.setTransCodeSystemName(transCodeSystemName);
        model.setTransDisplayName(transDisplayName);

        return model;
    }

    private String MapToQuestionId(String data) {
        String output = "";
        QuestionIdentifierMapDao model = new QuestionIdentifierMapDao();
        var qIdentifier = ecrLookUpRepository.FetchQuestionIdentifierMapByCriteriaByCriteria("COLUMN_NM", data);
        if(qIdentifier != null) {
            if (qIdentifier.getDynamicQuestionIdentifier().equalsIgnoreCase("STANDARD")) {
                model.setQuestionIdentifier(qIdentifier.getQuestionIdentifier());
                output = model.getQuestionIdentifier();
            } else {
                model.setDynamicQuestionIdentifier(qIdentifier.getDynamicQuestionIdentifier());
                output = model.getDynamicQuestionIdentifier();
            }
        }
        return "TEST_OUTPUT";
    }

    private String MapToElementValue(String data) {
        // CHECK MapToElementValue
        return "To Be Implemented";
    }

    private CE MapToCEAnswerType(String data, String questionCode) {
        CE ce = CE.Factory.newInstance();
        var answer = MapToCodedAnswer(data, questionCode);

        ce.setCode(answer.getCode());
        ce.setCodeSystem(answer.getCodeSystem());
        ce.setCodeSystemName(answer.getCodeSystemName());
        ce.setDisplayName(answer.getDisplayName());

        CD cd = CD.Factory.newInstance();
        cd.setCode(answer.getTransCode());
        cd.setCodeSystem(answer.getTransCodeSystem());
        cd.setCodeSystemName(answer.getTransCodeSystemName());
        cd.setDisplayName(answer.getTransDisplayName());
        CD[] cdArr = {cd};
        ce.setTranslationArray(cdArr);
        return ce;
    }

    private TS MapToTsType(String data) {
        TS ts = TS.Factory.newInstance();
        String result = "";
        boolean checkerCode = data.contains("/");
        boolean checkerCodeDash = data.contains("-");
        if (!checkerCode && !checkerCodeDash) {
            result = data;
        }
        else if (checkerCode && !data.isEmpty()) {
            // logic to fix ts
        }
        else if (checkerCodeDash && !data.isEmpty()) {
            // logic to fix ts
        }

        ts.setValue(result);
        return ts;
    }

    private XmlObject MapToCData(String data) throws XmlException {
        String xmlTemplate = "<to-be-remove><![CDATA[REPLACE_STRING]]></to-be-remove>";
        String updatedXML = xmlTemplate.replace("REPLACE_STRING", data);
        return XmlObject.Factory.parse(updatedXML);
    }



    private String GetCurrentUtcDateTimeInCdaFormat() {
        ZonedDateTime utcNow = ZonedDateTime.now(ZoneId.of("UTC"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssX");
        String formattedDate = utcNow.format(formatter);
        return formattedDate;
    }

    private POCDMT000040Component3 MapToPatient(int counter, String colName, String data, POCDMT000040Component3 component3) throws XmlException {
       var questionCode = MapToQuestionId(colName);
       var count = 0;
       if (component3.getSection() == null) {
           component3.addNewSection();
       }

       if (component3.getSection().getEntryArray().length == 0){
           component3.getSection().addNewEntry();
       } else {
           count = component3.getSection().getEntryArray().length + 1 - 1;
           component3.getSection().addNewEntry();
       }

       if (!component3.getSection().getEntryArray(count).isSetObservation()) {
           component3.getSection().getEntryArray(count).addNewObservation();
       }

       POCDMT000040Observation observation = component3.getSection().getEntryArray(count).getObservation();
       observation = MapToObservation(questionCode, data, observation);

        component3.getSection().getEntryArray(counter).setObservation(observation);
        return component3;
    }

    /*
    * TEST NEEDED
    * */
    private POCDMT000040Observation MapToObservation(String questionCode, String data, POCDMT000040Observation observation) throws XmlException {
        observation.setClassCode("OBS");
        observation.setMoodCode(XActMoodDocumentObservation.EVN);
        String dataType="DATE";
        String defaultQuestionIdentifier = "";

        PhdcQuestionLookUpDto questionLup = new PhdcQuestionLookUpDto();
        questionLup.setQuestionIdentifier("NOT_FOUND");
        questionLup.setQuesCodeSystemCd("NOT_FOUND");
        questionLup.setQuesCodeSystemDescTxt("NOT_FOUND");
        questionLup.setQuesDisplayName("NOT_FOUND");
        questionLup.setDataType("NOT_FOUND");
        var result = ecrLookUpRepository.FetchPhdcQuestionByCriteria(questionCode);
        /// TEST CODE
        result = new PhdcQuestionLookUpDto();
        result.setQuestionIdentifier("test");
        result.setQuesCodeSystemCd("test");
        result.setQuesCodeSystemDescTxt("test");
        result.setQuesDisplayName("test");
        result.setDataType("test");
        /// END TEST
        if (result != null) {
            if (!result.getQuestionIdentifier().isEmpty()) {
                questionLup.setQuestionIdentifier(result.getQuestionIdentifier());
            }
            if (!result.getQuesCodeSystemCd().isEmpty()) {
                questionLup.setQuesCodeSystemCd(result.getQuesCodeSystemCd());
            }
            if (!result.getQuesCodeSystemDescTxt().isEmpty()) {
                questionLup.setQuesCodeSystemDescTxt(result.getQuesCodeSystemDescTxt());
            }
            if (!result.getQuesDisplayName().isEmpty()) {
                questionLup.setQuesDisplayName(result.getQuesDisplayName());
            }
            if (!result.getDataType().isEmpty()) {
                questionLup.setDataType(result.getDataType());
            }

            QuestionIdentifierMapDto map = new QuestionIdentifierMapDto();
            map.setDynamicQuestionIdentifier("NOT_FOUND");
            QuestionIdentifierMapDto identifierMap = ecrLookUpRepository.FetchQuestionIdentifierMapByCriteriaByCriteria("Question_Identifier", questionCode);
            // TEST CODE
            identifierMap = new QuestionIdentifierMapDto();
            identifierMap.setDynamicQuestionIdentifier("test");
            identifierMap.setQuestionIdentifier("test");
            // END TEST
            if(identifierMap != null && !identifierMap.getDynamicQuestionIdentifier().isEmpty()) {
                map.setDynamicQuestionIdentifier(identifierMap.getDynamicQuestionIdentifier());
            }

            if(map.getDynamicQuestionIdentifier().equalsIgnoreCase("STANDARD")
                    || map.getDynamicQuestionIdentifier().equalsIgnoreCase("NOT_FOUND")) {
                defaultQuestionIdentifier = questionCode;
            }

            if (!result.getDataType().isEmpty()) {
                if (result.getDataType().equalsIgnoreCase("CODED")) {
                    var dataList = GetStringsBeforePipe(data);
                    for(int i = 0; i < dataList.size(); i++) {
                        int c = 0;
                        if (observation.getValueArray().length == 0) {
                            observation.addNewValue();
                        }
                        else {
                            c = observation.getValueArray().length;
                            observation.addNewValue();
                        }
                        CE ce = MapToCEAnswerType(
                                dataList.get(i),
                                defaultQuestionIdentifier);
                        observation.setValueArray(c, ce);
                    }
                }
                else {
                    if (result.getDataType().equalsIgnoreCase("TEXT")) {
                        // CHECK MapToSTValue from ori code
                        XmlObject out = MapToSTValue(data, observation);
                        observation.set(out);
                    }
                    else if (result.getDataType().equalsIgnoreCase("PART")) {
                        // CHECK MapToObservation from ori 47
                        if (observation.getValueArray().length == 0) {
                            observation.addNewValue();
                        }
                        var element = observation.getValueArray(0);
                        XmlCursor cursor = element.newCursor();
                        cursor.toFirstAttribute();
                        cursor.toNextToken();
                        cursor.insertAttributeWithValue(new QName("http://www.w3.org/2001/XMLSchema-instance", "type"), "II");
                        cursor.insertAttributeWithValue("root", "2.3.3.3.322.23.34");

                        var val = ecrLookUpRepository.FetchPhdcQuestionByCriteriaWithColumn("Question_Identifier", defaultQuestionIdentifier);
                        cursor.setAttributeText(new QName("root"), val.getQuesCodeSystemCd());

                        cursor.insertAttributeWithValue("extension", data);
                        cursor.dispose();

                    }
                    else if (result.getDataType().equalsIgnoreCase("DATE")) {
                        // CHECK MapToObservation from ori 66
                        if (observation.getValueArray().length == 0) {
                            observation.addNewValue();
                        }
                        var element = observation.getValueArray(0);
                        XmlCursor cursor = element.newCursor();
                        cursor.toFirstAttribute();
                        cursor.setAttributeText(new QName("http://www.w3.org/2001/XMLSchema-instance", "type"), "TS");
                        if (cursor.getAttributeText(new QName("value")) != null) {
                            cursor.setAttributeText(new QName("value"), MapToTsType(data).toString()); // Assuming MapToTsType returns a value.
                        } else {
                            cursor.insertAttributeWithValue("value", MapToTsType(data).toString()); // Assuming MapToTsType returns a value.
                        }
                        cursor.dispose();
                    }
                    else {
                        // CHECK MapToObservation from ori 77
                        if (observation.getValueArray().length == 0) {
                            observation.addNewValue();
                        }
                        var element = observation.getValueArray(0);
                        XmlCursor cursor = element.newCursor();
                        cursor.toFirstAttribute();
                        cursor.setAttributeText(new QName("http://www.w3.org/2001/XMLSchema-instance", "type"), "ST");
                        cursor.toParent();
                        cursor.setTextValue(data);
                        cursor.dispose();
                    }
                }
            }
        } else {
            observation.getCode().setCode(data + questionCode);
            observation.getCode().setCodeSystem("CODE NOT MAPPED");
            observation.getCode().setCodeSystemName("CODE NOT MAPPED");
            observation.getCode().setDisplayName("CODE NOT MAPPED");
        }
        return observation;
    }

    private XmlObject MapToSTValue(String input, XmlObject output) {
        XmlCursor cursor = output.newCursor();

        // Navigate to the 'value' element
        if (cursor.toChild(new QName("value"))) {
            // Set the attributes of the 'value' element
            cursor.setAttributeText(new QName("http://www.w3.org/2001/XMLSchema-instance", "type"), "ST");

            // Add child CDATA to 'value' element
            cursor.toNextToken(); // Move to the end of the current element (value element)
            cursor.insertChars(input);
        }

        cursor.dispose();
        return output;
    }

    private XmlObject MapToUsableTSElement(String data, XmlObject output, String name) {
        XmlCursor cursor = output.newCursor();

        // Ensure the cursor is at the correct position
        cursor.toFirstChild();  // Move to the root element

        // Create the childName element
        cursor.beginElement(name);

        // Insert the namespaces for childName
        cursor.insertNamespace("", "urn:hl7-org:v3");
        cursor.insertNamespace("xmlns", "http://www.w3.org/2001/XMLSchema-instance");

        // Set the 'type' attribute for childName
        cursor.insertAttributeWithValue("type", "IVL_TS");

        // Create the widthChildName element inside childName
        cursor.toFirstChild();  // Move inside childName
        cursor.beginElement("low");

        // Set namespace for widthChildName
        cursor.insertNamespace("", "urn:hl7-org:v3");


        cursor.insertAttributeWithValue("value", MapToTsType(data).toString());

        // Dispose of any cursors we've created
        cursor.dispose();
        return output;
    }

    private POCDMT000040StructuredBody MapToCase(int entryCounter, EcrSelectedCase caseDto, POCDMT000040StructuredBody output) throws XmlException {
        int componentCaseCounter=output.getComponentArray().length -1;
        int repeats = 0;

        // case index in case list
        int counter= entryCounter;

        for (Map.Entry<String, Object> entry : caseDto.getMsgCase().getDataMap().entrySet()) {
            String name = entry.getKey();

            String value = null;
            if (entry.getValue() != null) {
                value = entry.getValue().toString();
            }

            boolean patLocalIdFailedCheck = (name.equalsIgnoreCase("patLocalId") && caseDto.getMsgCase().getPatLocalId() == null)
                    || (name.equalsIgnoreCase("patLocalId")  && caseDto.getMsgCase().getPatLocalId() != null && caseDto.getMsgCase().getPatLocalId().isEmpty());
            boolean patInvEffTimeFailedCheck = name.equalsIgnoreCase("invEffectiveTime")  && caseDto.getMsgCase().getInvEffectiveTime() == null;
            boolean patInvAuthorIdFailedCheck = (name.equalsIgnoreCase("invAuthorId") && caseDto.getMsgCase().getInvAuthorId() == null)
                    || (name.equalsIgnoreCase("invAuthorId") && caseDto.getMsgCase().getInvAuthorId() != null && caseDto.getMsgCase().getInvAuthorId().isEmpty());
            if (patLocalIdFailedCheck || patInvEffTimeFailedCheck || patInvAuthorIdFailedCheck) {
                // do nothing
            }
            else if (value != null && !value.isEmpty()) {
                String questionId= "";
                var quesId = MapToQuestionId(questionId);

                if (name.equalsIgnoreCase("invConditionCd")) {
                    repeats = (int) caseDto.getMsgCase().getInvConditionCd().chars().filter(x -> x == '^').count();
                }


                if (repeats > 1) {
                    int c = 0;
                    if (output.getComponentArray(componentCaseCounter).getSection().getEntryArray().length == 0) {
                        output.getComponentArray(componentCaseCounter).getSection().addNewEntry();
                    } else {
                        c = output.getComponentArray(componentCaseCounter).getSection().getEntryArray().length;
                        output.getComponentArray(componentCaseCounter).getSection().addNewEntry();
                    }

                    if (output.getComponentArray(componentCaseCounter).getSection().getEntryArray(c).getObservation() == null) {
                        output.getComponentArray(componentCaseCounter).getSection().getEntryArray(c).addNewObservation();
                    }


                    var element = output.getComponentArray(componentCaseCounter).getSection().getEntryArray(c).getObservation();
                    var obs = MapTripletToObservation(
                            caseDto.getMsgCase().getInvConditionCd(),
                            quesId,
                            element
                    );
                    output.getComponentArray(componentCaseCounter).getSection().getEntryArray(c).setObservation(obs);
                    repeats = 0;
                }
                else {
                    int c = 0;
                    if (output.getComponentArray(componentCaseCounter).getSection().getEntryArray().length == 0) {
                        output.getComponentArray(componentCaseCounter).getSection().addNewEntry();
                    } else {
                        c = output.getComponentArray(componentCaseCounter).getSection().getEntryArray().length;
                        output.getComponentArray(componentCaseCounter).getSection().addNewEntry();
                    }

                    if (output.getComponentArray(componentCaseCounter).getSection().getEntryArray(c).getObservation() == null) {
                        output.getComponentArray(componentCaseCounter).getSection().getEntryArray(c).addNewObservation();
                    }
                    var element = output.getComponentArray(componentCaseCounter).getSection().getEntryArray(c).getObservation();
                    POCDMT000040Observation obs = MapToObservation(
                            quesId,
                            caseDto.getMsgCase().getInvConditionCd(),
                            element
                    );
                    output.getComponentArray(componentCaseCounter).getSection().getEntryArray(c).setObservation(obs);
                }
                counter++;
            }
        }


        int questionGroupCounter=0;
        int componentCounter=0;
        int answerGroupCounter=0;
        String OldQuestionId="CHANGED";
        int sectionCounter = 0;
        int repeatComponentCounter=0;


        if (caseDto.getMsgCaseParticipants().size() > 0
                || caseDto.getMsgCaseAnswers().size() > 0 || caseDto.getMsgCaseAnswerRepeats().size() > 0) {

            /**
             * CASE PARTICIPANT
             * */
            for(int i = 0; i < caseDto.getMsgCaseParticipants().size(); i++) {
                int c = 0;
                if (output.getComponentArray(componentCaseCounter).getSection().getEntryArray().length == 0) {
                    output.getComponentArray(componentCaseCounter).getSection().addNewEntry();
                }
                else {
                    c = output.getComponentArray(componentCaseCounter).getSection().getEntryArray().length;
                    output.getComponentArray(componentCaseCounter).getSection().addNewEntry();
                }

                if (!output.getComponentArray(componentCaseCounter).getSection().getEntryArray(c).isSetObservation()) {
                    output.getComponentArray(componentCaseCounter).getSection().getEntryArray(c).addNewObservation();
                }

                var element = output.getComponentArray(componentCaseCounter).getSection().getEntryArray(c).getObservation();

                POCDMT000040Observation out = MapToObsFromParticipant(
                        caseDto.getMsgCaseParticipants().get(i),
                        element
                );
                output.getComponentArray(componentCaseCounter).getSection().getEntryArray(c).setObservation(out);
                counter++;
            }

            /**
             * CASE ANSWER
             * */
            for(int i = 0; i < caseDto.getMsgCaseAnswers().size(); i++) {
                var out = output.getComponentArray(componentCaseCounter);
                var res = MapToMessageAnswer(
                        caseDto.getMsgCaseAnswers().get(i),
                        OldQuestionId,
                        counter,
                        out );

                OldQuestionId = res.getQuestionSeq();
                counter = res.getCounter();
                output.setComponentArray(componentCounter, res.getComponent());
            }

            for(int i = 0; i < caseDto.getMsgCaseAnswerRepeats().size(); i++) {
                if (repeatComponentCounter == 0) {
                    componentCounter++;
                    repeatComponentCounter = 1;
                }

                var out = output.getComponentArray(componentCaseCounter).getSection();

                var ot = MapToMultiSelect(caseDto.getMsgCaseAnswerRepeats().get(i),
                        answerGroupCounter, questionGroupCounter, sectionCounter, out);

                answerGroupCounter = ot.getAnswerGroupCounter();
                questionGroupCounter = ot.getQuestionGroupCounter();
                sectionCounter = ot.getSectionCounter();

                output.getComponentArray(componentCaseCounter).setSection(ot.getComponent());
            }
        }


        // CHECK MapToCase
        return output;


    }



    private MultiSelect MapToMultiSelect(EcrMsgCaseAnswerRepeatDto in,
                                         int answerGroupCounter,
                                         int questionGroupCounter,
                                         int sectionCounter, POCDMT000040Section out) throws XmlException {
        out.getCode().setCode("1234567-RPT");
        out.getCode().setCodeSystem("Local-codesystem-oid");
        out.getCode().setCodeSystemName("LocalSystem");
        out.getCode().setDisplayName("Generic Repeating Questions Section");
        out.getTitle().set(MapToCData("REPEATING QUESTIONS"));
        int componentCounter = 0;
        String dataType="DATE";
        int seqNbr = 0;
        int questionGroupSeqNbr = 0;
        int answerGroupSeqNbr = 0;
        String questionIdentifier="";

        MultiSelect model = new MultiSelect();

        // CHECK MapToMutliSelect
        for (Map.Entry<String, Object> entry : in.getDataMap().entrySet()) {
            String name= entry.getKey();
            String value= entry.getValue().toString();

            if (name.equalsIgnoreCase("questionGroupSeqNbr")) {
                questionGroupSeqNbr = Integer.valueOf(in.getQuestionGroupSeqNbr());
            }
            else if (name.equalsIgnoreCase("answerGroupSeqNbr")) {
                answerGroupSeqNbr = Integer.valueOf(in.getAnswerGroupSeqNbr());
                if((answerGroupSeqNbr==answerGroupCounter) && (questionGroupSeqNbr ==questionGroupCounter)){
                    componentCounter = out.getEntryArray(sectionCounter).getOrganizer().getComponentArray().length;
                }
                else {
                    sectionCounter = out.getEntryArray().length;
                    questionGroupCounter=questionGroupSeqNbr ;
                    answerGroupCounter=answerGroupSeqNbr;
                    out.getEntryArray(sectionCounter).getOrganizer().getCode().setCode(String.valueOf(questionGroupSeqNbr));
                    out.getEntryArray(sectionCounter).setTypeCode(XActRelationshipEntry.COMP);
                    out.getEntryArray(sectionCounter).getOrganizer().setClassCode(XActClassDocumentEntryOrganizer.CLUSTER);;
                    out.getEntryArray(sectionCounter).getOrganizer().setMoodCode("EVN");
                    out.getEntryArray(sectionCounter).getOrganizer().getStatusCode().setCode("completed");;
                    componentCounter=0;
                }
                out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().setClassCode("OBS");
                out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().setMoodCode(XActMoodDocumentObservation.EVN);

            }
            else if (name.equalsIgnoreCase("dataType")) {
                dataType = in.getDataType();
            }
            else if (name.equalsIgnoreCase("seqNbr")) {
                seqNbr = Integer.valueOf(in.getSeqNbr());
            }

            if(dataType.equalsIgnoreCase("CODED") || dataType.equalsIgnoreCase("CODED_COUNTY")){
                CE ce = CE.Factory.newInstance();
                if (name.equals("answerTxt") && !in.getAnswerTxt().isEmpty()) {
                    ce.getTranslationArray(0).setCode(in.getAnswerTxt());
                }
                else if (name.equals("ansCodeSystemCd") && !in.getAnsCodeSystemCd().isEmpty()) {
                    ce.getTranslationArray(0).setCodeSystem(in.getAnsCodeSystemCd());
                }
                else if (name.equals("ansCodeSystemDescTxt") && !in.getAnsCodeSystemDescTxt().isEmpty()) {
                    ce.getTranslationArray(0).setCodeSystemName(in.getAnsCodeSystemDescTxt());
                }
                else if (name.equals("ansDisplayTxt") && !in.getAnsDisplayTxt().isEmpty()) {
                    ce.getTranslationArray(0).setDisplayName(in.getAnsDisplayTxt());
                }
                else if (name.equals("ansToCode") && !in.getAnsToCode().isEmpty()) {
                    ce.setCode(in.getAnsToCode());
                }
                else if (name.equals("ansToCodeSystemCd") && !in.getAnsToCodeSystemCd().isEmpty()) {
                    ce.setCodeSystem(in.getAnsToCodeSystemCd());
                }
                else if (name.equals("ansToCodeSystemDescTxt") && !in.getAnsToCodeSystemDescTxt().isEmpty()) {
                    ce.setCodeSystemName(in.getAnsToCodeSystemDescTxt());
                }
                else if (name.equals("ansToDisplayNm") && !in.getAnsToDisplayNm().isEmpty()) {
                    ce.setDisplayName(in.getAnsToDisplayNm());

                }
                out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().getValueArray(seqNbr).set(ce);
            }
            else if ((dataType.equalsIgnoreCase("TEXT") || dataType.equalsIgnoreCase("NUMERIC")) &&
                    name.equals("answerTxt")) {
                if(questionIdentifier.equalsIgnoreCase("NBS243") ||
                        questionIdentifier.equalsIgnoreCase("NBS290")) {
                    var element = out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation();
                    var ot = MapToObservationPlace(
                            in.getAnswerTxt(),
                            element);
                    out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).setObservation((POCDMT000040Observation) ot);
                }
                else {

                    var element = out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation();
                    var ot = MapToSTValue(
                            in.getAnswerTxt(),
                            element);
                    out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).setObservation((POCDMT000040Observation)ot);
                }



            }
            else if(dataType.equalsIgnoreCase("DATE")){
                if(name.equals("answerTxt")){
                    var element = out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().getValueArray(0);
                    XmlCursor cursor = element.newCursor();
                    cursor.toFirstChild();
                    cursor.setAttributeText(new QName("http://www.w3.org/2001/XMLSchema-instance", "type"), "TS");
                    cursor.setAttributeText(new QName("", "value"), null);
                    if (name.equals("answerTxt")) {
                        String newValue = MapToTsType(in.getAnswerTxt()).toString();
                        cursor.setAttributeText(new QName("", "value"), newValue);
                    }
                    cursor.dispose();
                }
            }

            if(name.equals("questionIdentifier")){
                questionIdentifier= value;
                out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation()
                        .getCode().setCode(in.getQuestionIdentifier());
            }
            else if(name.equals("quesCodeSystemCd")){
                out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation()
                        .getCode().setCodeSystem(in.getQuesCodeSystemCd());
            }
            else if(name.equals("quesCodeSystemDescTxt")){
                out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation()
                        .getCode().setCodeSystemName(in.getQuesCodeSystemDescTxt());;
            }
            else if(name.equals("quesDisplayTxt")){
                out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation()
                        .getCode().setDisplayName(in.getQuesDisplayTxt());
            }
        }

        model.setAnswerGroupCounter(answerGroupCounter);
        model.setQuestionGroupCounter(questionGroupCounter);
        model.setSectionCounter(sectionCounter);
        model.setComponent(out);
        return model;
    }

    private XmlObject MapToObservationPlace(String in, XmlObject out) {
        XmlCursor cursor = out.newCursor();
        cursor.toFirstChild();
        cursor.setAttributeText(new QName("http://www.w3.org/2001/XMLSchema-instance", "type"), "II");
        cursor.setAttributeText(new QName("", "root"), "2.3.3.3.322.23.34");
        cursor.setAttributeText(new QName("", "extension"), in);
        cursor.dispose();

        return out;
    }

    private MessageAnswer MapToMessageAnswer(EcrMsgCaseAnswerDto in, String questionSeq, int counter, POCDMT000040Component3 out) {
        String dataType="";
        int sequenceNbr = 0;
        int questionGroupSeqNbr = 0;
        int answerGroupSeqNbr = 0;

        MessageAnswer model = new MessageAnswer();
        for (Map.Entry<String, Object> entry : in.getDataMap().entrySet()) {
            String name = entry.getKey();
            String value = null;
            if (entry.getValue() != null) {
                value = entry.getValue().toString();
            }

            if (name.equals("questionGroupSeqNbr") &&  !in.getQuestionGroupSeqNbr().isEmpty()) {
                questionGroupSeqNbr = Integer.valueOf(in.getQuestionGroupSeqNbr());
            }
            else if (name.equals("answerGroupSeqNbr") && !in.getAnswerGroupSeqNbr().isEmpty()) {
                answerGroupSeqNbr = Integer.valueOf(in.getAnswerGroupSeqNbr());
            }
            else if (name.equals("dataType") && !in.getDataType().isEmpty()) {
                dataType = in.getDataType();
            }
            else if (name.equals("seqNbr") && !in.getSeqNbr().isEmpty()) {
                sequenceNbr = out.getSection().getEntryArray(counter).getObservation().getValueArray().length;
            }
            else if (dataType.equalsIgnoreCase("CODED") || dataType.equalsIgnoreCase("COUNTY")) {
                CE ce = CE.Factory.newInstance();
                if (name.equals("answerTxt") && !in.getAnswerTxt().isEmpty()) {
                    ce.getTranslationArray(0).setCode(in.getAnswerTxt());
                }
                else if (name.equals("ansCodeSystemCd") && !in.getAnsCodeSystemCd().isEmpty()) {
                    ce.getTranslationArray(0).setCodeSystem(in.getAnsCodeSystemCd());
                }
                else if (name.equals("ansCodeSystemDescTxt") && !in.getAnsCodeSystemDescTxt().isEmpty()) {
                    ce.getTranslationArray(0).setCodeSystemName(in.getAnsCodeSystemDescTxt());
                }
                else if (name.equals("ansDisplayTxt") && !in.getAnsDisplayTxt().isEmpty()) {
                    ce.getTranslationArray(0).setDisplayName(in.getAnsDisplayTxt());
                }
                else if (name.equals("ansToCode") && !in.getAnsToCode().isEmpty()) {
                    ce.setCode(in.getAnsToCode());
                }
                else if (name.equals("ansToCodeSystemCd") && !in.getAnsToCodeSystemCd().isEmpty()) {
                    ce.setCodeSystem(in.getAnsToCodeSystemCd());
                }
                else if (name.equals("ansToCodeSystemDescTxt") && !in.getAnsToCodeSystemDescTxt().isEmpty()) {
                    ce.setCodeSystemName(in.getAnsToCodeSystemDescTxt());
                }
                else if (name.equals("ansToDisplayNm") && !in.getAnsToDisplayNm().isEmpty()) {
                    if(ce.getTranslationArray(0).getDisplayName().equals("OTH^")) {
                        ce.setDisplayName(ce.getTranslationArray(0).getDisplayName());
                    }
                    else {
                        ce.setDisplayName(in.getAnsToDisplayNm());
                    }
                }
                out.getSection().getEntryArray(counter).getObservation().getValueArray(sequenceNbr).set(ce);
            }

            else if (dataType.equalsIgnoreCase("TEXT") || dataType.equalsIgnoreCase("NUMERIC")) {
                if (name.equals("answerTxt") && !in.getAnswerTxt().isEmpty()) {
                    // CHECK MapToSTValue
                    var element = out.getSection().getEntryArray(counter).getObservation();
                    var ot = MapToSTValue(in.getAnswerTxt(), element);
                    out.getSection().getEntryArray(counter).setObservation((POCDMT000040Observation) ot);
                }
            }
            else if (dataType.equalsIgnoreCase("DATE")) {
                if (name.equals("answerTxt") && !in.getAnswerTxt().isEmpty()) {
                    // CHECK MapToMessageAnswer
                    var element = out.getSection().getEntryArray(counter).getObservation().getValueArray(0);
                    XmlCursor cursor = element.newCursor();
                    cursor.toFirstAttribute();
                    cursor.insertAttributeWithValue(new QName("http://www.w3.org/2001/XMLSchema-instance", "type"), "TS");
                    cursor.insertAttributeWithValue("value", ""); // As per your code, it's empty

                    var ot = MapToTsType(in.getAnswerTxt()).toString();
                    cursor.setAttributeText(new QName("", "value"), ot);
                    cursor.dispose();
                }
            }

            if (!in.getQuestionIdentifier().isEmpty()) {
                if (in.getQuestionIdentifier().equalsIgnoreCase(questionSeq)) {
                    // ignore
                }
                else {
                    if (questionSeq.equalsIgnoreCase("CHANGED")) {
                        // ignore
                    }
                    else {
                        counter++;
                        sequenceNbr = 0;
                    }
                    questionSeq = in.getQuestionIdentifier();
                    out.getSection().getEntryArray(counter).getObservation().setClassCode("OBS");
                    out.getSection().getEntryArray(counter).getObservation().setMoodCode(XActMoodDocumentObservation.EVN);
                    out.getSection().getEntryArray(counter).getObservation().getCode().setCode(in.getQuestionIdentifier());
                }
            }
            else if (!in.getQuesCodeSystemCd().isEmpty()) {
                out.getSection().getEntryArray(counter).getObservation().getCode().setCodeSystem(in.getQuesCodeSystemCd());
            }
            else if (!in.getQuesCodeSystemDescTxt().isEmpty()) {
                out.getSection().getEntryArray(counter).getObservation().getCode().setCodeSystemName(in.getQuesCodeSystemDescTxt());
            }
            else if (!in.getQuesDisplayTxt().isEmpty()) {
                out.getSection().getEntryArray(counter).getObservation().getCode().setDisplayName(in.getQuesDisplayTxt());
            }

        }

        model.setQuestionSeq(questionSeq);
        model.setCounter(counter);
        model.setComponent(out);
        return model;
    }

    /**
     * TEST NEEDED
     * */
    private POCDMT000040Observation MapToObsFromParticipant(EcrMsgCaseParticipantDto in, POCDMT000040Observation out) throws XmlException {
        String localId = "";
        String questionCode ="";

        if (in.getAnswerTxt() != null && !in.getAnswerTxt().isEmpty()) {
            localId = in.getAnswerTxt();
        }

        if (in.getQuestionIdentifier() != null && !in.getQuestionIdentifier().isEmpty()) {
            questionCode = in.getQuestionIdentifier();
        }

        return MapToObservation(questionCode, localId, out);
    }

    private POCDMT000040Observation MapTripletToObservation(String invConditionCd, String questionId, POCDMT000040Observation output) {
        output.setClassCode("OBS");
        output.setMoodCode(XActMoodDocumentObservation.EVN);
        List<String> repeats = GetStringsBeforePipe(invConditionCd);

        String tripletCodedValue =  "";
        PhdcQuestionLookUpDto questionLookUpDto = MapToCodedQuestionType(questionId);
        output.getCode().setCode(questionLookUpDto.getQuesCodeSystemCd());
        output.getCode().setCodeSystem(questionLookUpDto.getQuesCodeSystemDescTxt());
        output.getCode().setDisplayName(questionLookUpDto.getQuesDisplayName());
        output.getCode().setCode(questionId);

        for(int i = 0; i < repeats.size(); i++) {
            if (repeats.size() == 1) {
                tripletCodedValue = invConditionCd;
            } else {
                tripletCodedValue = repeats.get(i);
            }
            var caretStringList = GetStringsBeforeCaret(repeats.get(i));

            if (tripletCodedValue.length() > 0 && caretStringList.size() == 4) {
                // CHECK MapTripletToObservation
                String code = caretStringList.get(0);
                String displayName = caretStringList.get(1);
                String codeSystemName = caretStringList.get(2);
                String codeSystem = caretStringList.get(3);
                int c = 0;
                if (output.getValueArray().length == 0) {
                    output.addNewValue();
                }
                else {
                    c = output.getValueArray().length;
                    output.addNewValue();
                }

                CE ce = CE.Factory.newInstance();
                ce.setCode(code);
                ce.setCodeSystem(codeSystem);
                ce.setCodeSystemName(codeSystemName);
                ce.setDisplayName(displayName);
                output.getValueArray(c).set(ce);
            }
        }

        return output;

    }

    private PhdcQuestionLookUpDto MapToCodedQuestionType(String questionIdentifier) {
        PhdcQuestionLookUpDto dto = new PhdcQuestionLookUpDto();
        dto.setQuesCodeSystemCd("NOT_FOUND");
        dto.setQuesCodeSystemDescTxt("NOT_FOUND");
        dto.setQuesDisplayName("NOT_FOUND");
        if (!questionIdentifier.isEmpty()) {
            var result = ecrLookUpRepository.FetchPhdcQuestionByCriteriaWithColumn("QUESTION_IDENTIFIER", questionIdentifier);
            if (result != null) {
                if (result.getQuesCodeSystemCd() != null && !result.getQuesCodeSystemCd().isEmpty()) {
                    dto.setQuesCodeSystemCd(result.getQuesCodeSystemCd());
                }
                else if (result.getQuesCodeSystemDescTxt() != null && !result.getQuesCodeSystemDescTxt().isEmpty()) {
                    dto.setQuesCodeSystemDescTxt(result.getQuesCodeSystemDescTxt());
                }
                else if (result.getQuesDisplayName() != null && !result.getQuesDisplayName().isEmpty()) {
                    dto.setQuesDisplayName(result.getQuesDisplayName());
                }
            }
        }
        return dto;
    }

    private boolean isFieldValid(String fieldName, String fieldValue) {
        return fieldValue != null && !fieldValue.isEmpty();
    }

    private boolean isFieldValid(String fieldName, Date fieldValue) {
        return fieldValue != null;
    }
}
