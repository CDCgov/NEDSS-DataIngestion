package gov.cdc.dataingestion.nbs.ecr;

import gov.cdc.dataingestion.nbs.ecr.model.MessageAnswer;
import gov.cdc.dataingestion.nbs.ecr.model.MultiSelect;
import gov.cdc.dataingestion.nbs.repository.implementation.EcrLookUpRepository;
import gov.cdc.dataingestion.nbs.repository.model.IEcrLookUpRepository;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedCase;
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
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
         * CASE - TEST NEEDED
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
        var t = printXmlForTesting(clinicalDocument);


        /**
         * XML ANSWER - TEST NEEDED
         * */
        if(!input.getMsgXmlAnswers().isEmpty()) {
            for(int i = 0; i < input.getMsgXmlAnswers().size(); i++) {
                componentCounter++;
                POCDMT000040Component3 out = clinicalDocument.getComponent().getStructuredBody().getComponentArray(componentCounter);
                var mappedData = MapToExtendedData(input.getMsgXmlAnswers().get(i), out);
                clinicalDocument.getComponent().getStructuredBody().setComponentArray(componentCounter, mappedData);
            }

        }

        /**
         * PROVIDER -- REVIEW NEEDED
         * **/
        if(!input.getMsgProviders().isEmpty()) {
            // 449
           for(int i = 0; i < input.getMsgProviders().size(); i++) {
                if (input.getMsgProviders().get(i).getPrvAuthorId().equalsIgnoreCase(inv168)) {
                    // ignore
                }
                else {
                    if (performerComponentCounter < 1) {
                        componentCounter++;
                        performerComponentCounter = componentCounter;

                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(performerComponentCounter).getSection().getCode().setCode("123-4567");
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(performerComponentCounter).getSection().getCode().setCodeSystem("Local-codesystem-oid");
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(performerComponentCounter).getSection().getCode().setCodeSystemName("LocalSystem");
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(performerComponentCounter).getSection().getCode().setDisplayName("Interested Parties Section");
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(performerComponentCounter).getSection().getCode().set(MapToCData("INTERESTED PARTIES SECTION"));
                    }

                    performerSectionCounter = clinicalDocument.getComponent().getStructuredBody().getComponentArray(performerComponentCounter).getSection().getEntryArray().length;
                    // CHECK MapToPSN
                    POCDMT000040Participant2 out = clinicalDocument.getComponent().getStructuredBody().getComponentArray(performerComponentCounter).getSection().getEntryArray(performerSectionCounter).getAct().getParticipantArray(0);
                    POCDMT000040Participant2 output = MapToPSN(
                            input.getMsgProviders().get(i),
                            out
                    );
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(performerComponentCounter).getSection().getEntryArray(performerSectionCounter).getAct().setParticipantArray(0, output);

                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(performerComponentCounter).getSection().getEntryArray(performerSectionCounter).setTypeCode(XActRelationshipEntry.COMP);
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(performerComponentCounter).getSection().getEntryArray(performerSectionCounter).getAct().setClassCode(XActClassDocumentEntryAct.ACT);
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(performerComponentCounter).getSection().getEntryArray(performerSectionCounter).getAct().setMoodCode(XDocumentActMood.EVN);
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(performerComponentCounter).getSection().getEntryArray(performerSectionCounter).getAct().getCode().setCode("PSN");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(performerComponentCounter).getSection().getEntryArray(performerSectionCounter).getAct().getCode().setCodeSystem("Local-codesystem-oid");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(performerComponentCounter).getSection().getEntryArray(performerSectionCounter).getAct().getCode().setCodeSystemName("LocalSystem");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(performerComponentCounter).getSection().getEntryArray(performerSectionCounter).getAct().getCode().setDisplayName("Interested Party");

                }
           }
        }

        /**
         * ORGANIZATION -- REVIEW NEEDED
         * **/
        if(!input.getMsgOrganizations().isEmpty()) {
            // 474
            for(int i = 0; i < input.getMsgOrganizations().size(); i++) {
                if (performerComponentCounter < 1) {
                    componentCounter++;
                    performerComponentCounter = componentCounter;
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(performerComponentCounter).getSection().getCode().setCode("123-4567");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(performerComponentCounter).getSection().getCode().setCodeSystem("Local-codesystem-oid");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(performerComponentCounter).getSection().getCode().setCodeSystemName("LocalSystem");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(performerComponentCounter).getSection().getCode().setDisplayName("Interested Parties Section");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(performerComponentCounter).getSection().getCode().set(MapToCData("INTERESTED PARTIES SECTION"));


                }
                performerSectionCounter = clinicalDocument.getComponent().getStructuredBody().getComponentArray(performerComponentCounter).getSection().getEntryArray().length;
                // CHECK MapToORG
                POCDMT000040Participant2 out = clinicalDocument.getComponent().getStructuredBody().getComponentArray(performerComponentCounter).getSection().getEntryArray(performerSectionCounter).getAct().getParticipantArray(0);
                POCDMT000040Participant2 output = MapToORG(input.getMsgOrganizations().get(i), out);
                clinicalDocument.getComponent().getStructuredBody().getComponentArray(performerComponentCounter).getSection().getEntryArray(performerSectionCounter).getAct().setParticipantArray(0, output);

                clinicalDocument.getComponent().getStructuredBody().getComponentArray(performerComponentCounter).getSection().getEntryArray(performerSectionCounter).setTypeCode(XActRelationshipEntry.COMP);
                clinicalDocument.getComponent().getStructuredBody().getComponentArray(performerComponentCounter).getSection().getEntryArray(performerSectionCounter).getAct().setClassCode(XActClassDocumentEntryAct.ACT);
                clinicalDocument.getComponent().getStructuredBody().getComponentArray(performerComponentCounter).getSection().getEntryArray(performerSectionCounter).getAct().setMoodCode(XDocumentActMood.EVN);
                clinicalDocument.getComponent().getStructuredBody().getComponentArray(performerComponentCounter).getSection().getEntryArray(performerSectionCounter).getAct().getCode().setCode("ORG");
                clinicalDocument.getComponent().getStructuredBody().getComponentArray(performerComponentCounter).getSection().getEntryArray(performerSectionCounter).getAct().getCode().setCodeSystem("Local-codesystem-oid");
                clinicalDocument.getComponent().getStructuredBody().getComponentArray(performerComponentCounter).getSection().getEntryArray(performerSectionCounter).getAct().getCode().setCodeSystemName("LocalSystem");
                clinicalDocument.getComponent().getStructuredBody().getComponentArray(performerComponentCounter).getSection().getEntryArray(performerSectionCounter).getAct().getCode().setDisplayName("Interested Party");

            }
        }

        /**
         * PLACE -- REVIEW NEEDED
         * */
        if(!input.getMsgPlaces().isEmpty()) {
            // 498
            for(int i = 0; i < input.getMsgPlaces().size(); i++) {
                if (performerComponentCounter < 1) {
                    componentCounter++;
                    performerComponentCounter = componentCounter;

                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(performerComponentCounter).getSection().getCode().setCode("123-4567");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(performerComponentCounter).getSection().getCode().setCodeSystem("Local-codesystem-oid");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(performerComponentCounter).getSection().getCode().setCodeSystemName("LocalSystem");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(performerComponentCounter).getSection().getCode().setDisplayName("Interested Parties Section");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(performerComponentCounter).getSection().getCode().set(MapToCData("INTERESTED PARTIES SECTION"));
                }

                performerSectionCounter = clinicalDocument.getComponent().getStructuredBody().getComponentArray(performerComponentCounter).getSection().getEntryArray().length;
                // CHECK MapToPlace
                POCDMT000040Participant2 out = clinicalDocument.getComponent().getStructuredBody().getComponentArray(performerComponentCounter).getSection().getEntryArray(performerSectionCounter).getAct().getParticipantArray(0);
                POCDMT000040Participant2 output = MapToPlace(input.getMsgPlaces().get(i), out);
                clinicalDocument.getComponent().getStructuredBody().getComponentArray(performerComponentCounter).getSection().getEntryArray(performerSectionCounter).getAct().setParticipantArray(0, output);

                clinicalDocument.getComponent().getStructuredBody().getComponentArray(performerComponentCounter).getSection().getEntryArray(performerSectionCounter).setTypeCode(XActRelationshipEntry.COMP);
                clinicalDocument.getComponent().getStructuredBody().getComponentArray(performerComponentCounter).getSection().getEntryArray(performerSectionCounter).getAct().setClassCode(XActClassDocumentEntryAct.ACT);
                clinicalDocument.getComponent().getStructuredBody().getComponentArray(performerComponentCounter).getSection().getEntryArray(performerSectionCounter).getAct().setMoodCode(XDocumentActMood.EVN);
                clinicalDocument.getComponent().getStructuredBody().getComponentArray(performerComponentCounter).getSection().getEntryArray(performerSectionCounter).getAct().getCode().setCode("PLC");
                clinicalDocument.getComponent().getStructuredBody().getComponentArray(performerComponentCounter).getSection().getEntryArray(performerSectionCounter).getAct().getCode().setCodeSystem("Local-codesystem-oid");
                clinicalDocument.getComponent().getStructuredBody().getComponentArray(performerComponentCounter).getSection().getEntryArray(performerSectionCounter).getAct().getCode().setCodeSystemName("LocalSystem");
                clinicalDocument.getComponent().getStructuredBody().getComponentArray(performerComponentCounter).getSection().getEntryArray(performerSectionCounter).getAct().getCode().setDisplayName("Interested Party");

            }
        }

        /**
         * PLACE -- INTERVIEW NEEDED
         * */
        if(!input.getMsgInterviews().isEmpty()) {
            // 523
            for(int i = 0; i < input.getMsgInterviews().size(); i++) {
                if (interviewCounter < 1) {
                    interviewCounter = componentCounter + 1;
                    componentCounter++;

                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(interviewCounter).getSection().getCode().setCode("IXS");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(interviewCounter).getSection().getCode().setCodeSystem("Local-codesystem-oid");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(interviewCounter).getSection().getCode().setCodeSystemName("LocalSystem");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(interviewCounter).getSection().getCode().setDisplayName("Interviews");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(interviewCounter).getSection().getCode().set(MapToCData("INTERVIEW SECTION"));
                }

                POCDMT000040Component3 ot = clinicalDocument.getComponent().getStructuredBody().getComponentArray(interviewCounter);
                // CHECK MapToInterview

                POCDMT000040Component3 output = MapToInterview(input.getMsgInterviews().get(i).getMsgInterview(), ot);
                clinicalDocument.getComponent().getStructuredBody().setComponentArray(interviewCounter, output);
            }
        }

        /**
         * TREATMENT -- INTERVIEW NEEDED
         * */
        if(!input.getMsgTreatments().isEmpty()) {
            // 543
            for(int i = 0; i < input.getMsgTreatments().size(); i++) {

                if (treatmentCounter < 1) {
                    treatmentCounter++;
                    componentCounter++;
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray()[treatmentCounter].getSection().getCode().setCode("55753-8");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray()[treatmentCounter].getSection().getCode().setCodeSystem("2.16.840.1.113883.6.1");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray()[treatmentCounter].getSection().getCode().setCodeSystemName("LOINC");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray()[treatmentCounter].getSection().getCode().setDisplayName("Treatment Information");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray()[treatmentCounter].getSection().set(MapToCData("TREATMENT INFORMATION"));
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(treatmentCounter).getSection().set(MapToCData("CDA Treatment Information Section"));
                }

                clinicalDocument.getComponent().getStructuredBody().getComponentArray()[treatmentCounter].getSection().getEntryArray(treatmentSectionCounter).getSubstanceAdministration().getStatusCode().setCode("active");
                clinicalDocument.getComponent().getStructuredBody().getComponentArray()[treatmentCounter].getSection().getEntryArray(treatmentSectionCounter).getSubstanceAdministration().getEntryRelationshipArray(0).setTypeCode(XActRelationshipEntryRelationship.COMP);
                var outpp = clinicalDocument.getComponent().getStructuredBody().getComponentArray()[treatmentCounter].getSection().getEntryArray(treatmentSectionCounter)
                        .getSubstanceAdministration();
                String treatmentvalue = "";
                clinicalDocument.getComponent().getStructuredBody().getComponentArray()[treatmentCounter].getSection().getEntryArray(treatmentSectionCounter).getSubstanceAdministration().setClassCode("SBADM");
                clinicalDocument.getComponent().getStructuredBody().getComponentArray()[treatmentCounter].getSection().getEntryArray(treatmentSectionCounter).getSubstanceAdministration().setMoodCode(XDocumentSubstanceMood.EVN);
                clinicalDocument.getComponent().getStructuredBody().getComponentArray()[treatmentCounter].getSection().getEntryArray(treatmentSectionCounter).getSubstanceAdministration().setNegationInd(false);

                var o1 = clinicalDocument.getComponent().getStructuredBody().getComponentArray(treatmentCounter).getSection().getEntryArray(treatmentSectionCounter).getSubstanceAdministration();
                var o2 = clinicalDocument.getComponent().getStructuredBody().getComponentArray(treatmentCounter).getSection().getText();
                var mappedVal = MapToTreatment(input.getMsgTreatments().get(0),
                        o1,
                        o2,
                        treatmentSectionCounter);
                clinicalDocument.getComponent().getStructuredBody().getComponentArray(treatmentCounter).getSection().getEntryArray(treatmentSectionCounter).setSubstanceAdministration(mappedVal);
                treatmentSectionCounter= treatmentSectionCounter+1;
            }
        }


        String value ="";
        int k =0;

        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getIdArray(0).setExtension(MapToTranslatedValue("CUS101"));
        value = MapToTranslatedValue("CUS102");

        var element = clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization();
        MapToElementValue("CHECK MAP TO ELEMENT VALUE");

        value = MapToTranslatedValue("CUS103");
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().getStreetAddressLineArray(k).set(MapToCData(value));
        k = k+1;
        value = MapToTranslatedValue("CUS104");
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().getStreetAddressLineArray(k).set(MapToCData(value));
        k = k+1;
        value = MapToTranslatedValue("CUS105");
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().getCityArray(k).set(MapToCData(value));
        k = k+1;
        value = MapToTranslatedValue("CUS106");
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().getStateArray(k).set(MapToCData(value));
        k = k+1;
        value = MapToTranslatedValue("CUS107");
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().getPostalCodeArray(k).set(MapToCData(value));
        k = k+1;
        value = MapToTranslatedValue("CUS108");
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr().getCountryArray(k).set(MapToCData(value));
        k = k+1;
        value = MapToTranslatedValue("CUS109");
        clinicalDocument.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getTelecom().setValue(value);
        k = k+1;

        value = MapToTranslatedValue("AUT101");
        clinicalDocument.getAuthorArray(0).getAssignedAuthor().getIdArray(0).setRoot(value);
        value = MapToTranslatedValue("AUT102");
        clinicalDocument.getAuthorArray(0).getAssignedAuthor().getAssignedPerson().getNameArray(0).getFamilyArray(1).set(MapToCData(value));
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
    
    private POCDMT000040SubstanceAdministration MapToTreatment(EcrSelectedTreatment input, POCDMT000040SubstanceAdministration output, StrucDocText list, int counter) throws XmlException {
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

        for (Map.Entry<String, Object> entry : in.getDataMap().entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue().toString();
        }

        if(input.getMsgTreatment().getTrtTreatmentDt() != null) {
            TRT_TREATMENT_DT= input.getMsgTreatment().getTrtTreatmentDt().toString();
        }

        if(input.getMsgTreatment().getTrtFrequencyAmtCd() != null && !input.getMsgTreatment().getTrtFrequencyAmtCd().isEmpty()) {
            TRT_FREQUENCY_AMT_CD= input.getMsgTreatment().getTrtFrequencyAmtCd();
        }

        if(input.getMsgTreatment().getTrtDosageUnitCd() != null && !input.getMsgTreatment().getTrtDosageUnitCd().isEmpty()) {
            TRT_DOSAGE_UNIT_CD= input.getMsgTreatment().getTrtDosageUnitCd();
            output.getDoseQuantity().setUnit(TRT_DOSAGE_UNIT_CD);
        }

        if(input.getMsgTreatment().getTrtDosageAmt() != null) {
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

        if(input.getMsgTreatment().getTrtDrugCd() != null && !input.getMsgTreatment().getTrtDrugCd().isEmpty()) {
            treatmentNameQuestion = MapToQuestionId("TRT_DRUG_CD");;
            treatmentName = input.getMsgTreatment().getTrtDrugCd();
        }


        if(input.getMsgTreatment().getTrtLocalId() != null && !input.getMsgTreatment().getTrtLocalId().isEmpty()) {
            output.getIdArray(0).setRoot("2.16.840.999999");
            output.getIdArray(0).setAssigningAuthorityName("LR");
            output.getIdArray(0).setExtension(input.getMsgTreatment().getTrtLocalId());
            treatmentUid=input.getMsgTreatment().getTrtLocalId();
        }

        if(input.getMsgTreatment().getTrtCustomTreatmentTxt() != null && !input.getMsgTreatment().getTrtCustomTreatmentTxt().isEmpty()) {
            customTreatment= input.getMsgTreatment().getTrtCustomTreatmentTxt();
        }

        if(input.getMsgTreatment().getTrtCompositeCd() != null && !input.getMsgTreatment().getTrtCompositeCd().isEmpty()) {

        }

        if(input.getMsgTreatment().getTrtCommentTxt() != null && !input.getMsgTreatment().getTrtCommentTxt().isEmpty()) {

        }

        if(input.getMsgTreatment().getTrtDurationAmt() != null) {
            TRT_DURATION_AMT = input.getMsgTreatment().getTrtDurationAmt().toString();
        }

        if(input.getMsgTreatment().getTrtDurationUnitCd() != null && !input.getMsgTreatment().getTrtDurationUnitCd().isEmpty()) {
            TRT_DURATION_UNIT_CD = input.getMsgTreatment().getTrtDurationUnitCd();
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
        }

        if (!TRT_FREQUENCY_AMT_CD.isEmpty()) {
            // CHECK MapToTreatment
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

    private POCDMT000040Component3 MapToInterview(EcrMsgInterviewDto in, POCDMT000040Component3 out) throws XmlException {
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

        
        if(in.getMsgContainerUid() != null || !in.getIxsAuthorId().isEmpty() || in.getIxsEffectiveTime() != null){
            // CHECK MapToInterview
            out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getIdArray(0).setExtension(in.getMsgContainerUid().toString());
        }
        if (!in.getIxsLocalId().isEmpty()){
            out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getIdArray(0).setExtension(in.getIxsLocalId());
        }

        if (!in.getIxsStatusCd().isEmpty()){
            out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getStatusCode().setCode(in.getIxsStatusCd());
        }
        if (in.getIxsInterviewDt() != null){
            var ts = MapToTsType(in.getIxsInterviewDt().toString());
            out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEffectiveTime().setValue(ts.toString());
        }

        if (!in.getIxsIntervieweeRoleCd().isEmpty()){
            String questionCode = MapToQuestionId("IXS_INTERVIEWEE_ROLE_CD");
            
            out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray(entryCounter).setTypeCode(XActRelationshipEntryRelationship.COMP);
            var obs = out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray(entryCounter).getObservation();
            MapToObservation(questionCode, in.getIxsIntervieweeRoleCd(), obs);
            out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray(entryCounter).setObservation(obs);
            entryCounter= entryCounter+ 1;
        }
        if (!in.getIxsInterviewTypeCd().isEmpty()){
            String questionCode = MapToQuestionId("IXS_INTERVIEW_TYPE_CD");

            out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray(entryCounter).setTypeCode(XActRelationshipEntryRelationship.COMP);
            var obs = out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray(entryCounter).getObservation();
            MapToObservation(questionCode, in.getIxsInterviewTypeCd(), obs);
            out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray(entryCounter).setObservation(obs);
            entryCounter= entryCounter+ 1;

        }
        if (!in.getIxsInterviewLocCd().isEmpty()){
            String questionCode = MapToQuestionId("IXS_INTERVIEW_LOC_CD");

            out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray(entryCounter).setTypeCode(XActRelationshipEntryRelationship.COMP);
            var obs = out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray(entryCounter).getObservation();
            MapToObservation(questionCode, in.getIxsInterviewLocCd(), obs);
            out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray(entryCounter).setObservation(obs);
            entryCounter= entryCounter+ 1;
        }

        return out;
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
            String value = entry.getValue().toString();
        }

        if(!in.getPlaLocalId().isEmpty()){
            out.setTypeCode("PRF");
            out.getParticipantRole().getIdArray(0).setRoot("2.16.840.1.113883.4.6");
            out.getParticipantRole().getIdArray(0).setExtension(in.getPlaLocalId());
            out.getParticipantRole().getIdArray(0).setAssigningAuthorityName("LR");
        } 
        if (!in.getPlaNameTxt().isEmpty()){
            PN val = PN.Factory.newInstance();
            val.set(MapToCData(in.getPlaNameTxt()));
            out.getParticipantRole().getPlayingEntity().addNewName();
            out.getParticipantRole().getPlayingEntity().setNameArray(0, val);
        }
        if (!in.getPlaAddrStreetAddr1Txt().isEmpty()){
            streetAddress1= in.getPlaAddrStreetAddr1Txt();
        }
        if (!in.getPlaAddrStreetAddr2Txt().isEmpty()){
            streetAddress2 =in.getPlaAddrStreetAddr2Txt();
        }
        if (!in.getPlaAddrCityTxt().isEmpty()){
            city= in.getPlaAddrCityTxt();
        } 
        if (!in.getPlaAddrCountyCd().isEmpty()){
            county= in.getPlaAddrCountyCd();
        }
        if (!in.getPlaAddrStateCd().isEmpty()){
            state= in.getPlaAddrStateCd();
        } 
        if (!in.getPlaAddrZipCodeTxt().isEmpty()){
            zip = in.getPlaAddrZipCodeTxt();
        }
        if (!in.getPlaAddrCountryCd().isEmpty()){
            country=in.getPlaAddrCountryCd();
        }
        if (!in.getPlaPhoneNbrTxt().isEmpty()){
            workPhone=in.getPlaPhoneNbrTxt();
        }
        if (in.getPlaAddrAsOfDt() != null){
            postalAsOfDate=in.getPlaAddrAsOfDt().toString();
        }
        if (!in.getPlaCensusTractTxt().isEmpty()){
            censusTract=in.getPlaCensusTractTxt();
        }
        if (in.getPlaPhoneAsOfDt() != null ){
            teleAsOfDate=in.getPlaPhoneAsOfDt().toString();
        }
        if (!in.getPlaPhoneExtensionTxt().isEmpty()){
            workExtn= in.getPlaPhoneExtensionTxt();
        }
        if (!in.getPlaCommentTxt().isEmpty()){
            placeAddressComments= in.getPlaCommentTxt();
        }
        if (!in.getPlaPhoneCountryCodeTxt().isEmpty()){
            workCountryCode= in.getPlaPhoneCountryCodeTxt();
        }
        if (!in.getPlaEmailAddressTxt().isEmpty()){
            workEmail= in.getPlaEmailAddressTxt();
        }
        if (!in.getPlaUrlAddressTxt().isEmpty()){
            workURL= in.getPlaUrlAddressTxt();
        }
        if (!in.getPlaPhoneCommentTxt().isEmpty()){
            placeComments= in.getPlaPhoneCommentTxt();
        }
        if (!in.getPlaTypeCd().isEmpty()){

            String questionCode= MapToQuestionId("PLA_TYPE_CD");
            out.getParticipantRole().addNewCode();
            out.getParticipantRole().setCode(MapToCEAnswerType(in.getPlaTypeCd(), questionCode));
        } 
        if (!in.getPlaCommentTxt().isEmpty()){
            out.getParticipantRole().getPlayingEntity().getDesc().set(MapToCData(in.getPlaCommentTxt()));
        }
         
        if (!in.getPlaIdQuickCode().isEmpty()){
            out.getParticipantRole().getIdArray(1).setRoot("2.16.840.1.113883.4.6");
            out.getParticipantRole().getIdArray(1).setExtension(in.getPlaIdQuickCode());
            out.getParticipantRole().getIdArray(1).setAssigningAuthorityName("LR_QEC");
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
            AdxpStreetAddressLine val = AdxpStreetAddressLine.Factory.newInstance();
            val.set(MapToCData(streetAddress2));
            out.getParticipantRole().getAddrArray(0).addNewStreetAddressLine();
            if (out.getParticipantRole().getAddrArray().length > 1) {
                out.getParticipantRole().getAddrArray(0).setStreetAddressLineArray(1, val);
            }
            else {
                out.getParticipantRole().getAddrArray(0).setStreetAddressLineArray(0, val);
            }
            isAddressPopulated=1;
        }
        if(!city.isEmpty()){
            AdxpCity val = AdxpCity.Factory.newInstance();
            val.set(MapToCData(city));
            out.getParticipantRole().getAddrArray(0).addNewCity();
            out.getParticipantRole().getAddrArray(0).setCityArray(0, val);
            isAddressPopulated=1;
        }
        if(!state.isEmpty()){
            AdxpState val = AdxpState.Factory.newInstance();
            val.set(MapToCData(state));
            out.getParticipantRole().getAddrArray(0).addNewState();
            out.getParticipantRole().getAddrArray(0).setStateArray(0, val);
            isAddressPopulated=1;
        }
        if(!county.isEmpty()){
            AdxpCounty val = AdxpCounty.Factory.newInstance();
            val.set(MapToCData(county));
            out.getParticipantRole().getAddrArray(0).addNewCounty();
            out.getParticipantRole().getAddrArray(0).setCountyArray(0, val);
            isAddressPopulated=1;
        }
        if(!zip.isEmpty()){
            AdxpPostalCode val = AdxpPostalCode.Factory.newInstance();
            val.set(MapToCData(zip));
            out.getParticipantRole().getAddrArray(0).addNewPostalCode();
            out.getParticipantRole().getAddrArray(0).setPostalCodeArray(0, val);
            isAddressPopulated=1;
        }
        if(!country.isEmpty()){
            AdxpCountry val = AdxpCountry.Factory.newInstance();
            val.set(MapToCData(country));
            out.getParticipantRole().getAddrArray(0).addNewCountry();
            out.getParticipantRole().getAddrArray(0).setCountryArray(0, val);
            isAddressPopulated=1;
        }
        if(!censusTract.isEmpty()){
            AdxpCensusTract val = AdxpCensusTract.Factory.newInstance();
            val.set(MapToCData(censusTract));
            out.getParticipantRole().getAddrArray(0).addNewCensusTract();
            out.getParticipantRole().getAddrArray(0).setCensusTractArray(0, val);
        }
        if(isAddressPopulated>0){
            out.getParticipantRole().getAddrArray()[0].setUse(Arrays.asList("WP"));
            if(!postalAsOfDate.isEmpty()){
                // OutXML::Element element = (OutXML::Element)out.getParticipantRole().addr[0];
                // MapToUsableTSElement(postalAsOfDate, element, "useablePeriod");
                // CHECK MapToUsableTSElement
            }
        }
        if(!placeAddressComments.isEmpty()){
            AdxpAdditionalLocator val = AdxpAdditionalLocator.Factory.newInstance();
            val.set(MapToCData(placeAddressComments));
            out.getParticipantRole().getAddrArray(0).addNewAdditionalLocator();
            out.getParticipantRole().getAddrArray(0).setAdditionalLocatorArray(0, val);
        }

        if(!workPhone.isEmpty()){
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
            String value = entry.getValue().toString();
        }

        if(!in.getOrgLocalId().isEmpty()){
            out.getParticipantRole().getIdArray(0).setRoot("2.16.840.1.113883.4.6");
            out.getParticipantRole().getIdArray(0).setExtension(in.getOrgLocalId());
            out.getParticipantRole().getIdArray(0).setAssigningAuthorityName("LR");
        }
        if(!in.getOrgNameTxt().isEmpty()){
            PN val = PN.Factory.newInstance();
            val.set(MapToCData(in.getOrgNameTxt()));
            out.getParticipantRole().getPlayingEntity().addNewName();
            out.getParticipantRole().getPlayingEntity().setNameArray(0, val);
        }
        if(!in.getOrgAddrStreetAddr1Txt().isEmpty()){
            streetAddress1= in.getOrgAddrStreetAddr1Txt();
        }
        if(!in.getOrgAddrStreetAddr2Txt().isEmpty()){
            streetAddress2 =in.getOrgAddrStreetAddr2Txt();
        }
        if(!in.getOrgAddrCityTxt().isEmpty()){
            city= in.getOrgAddrCityTxt();
        }
        if(!in.getOrgAddrCountyCd().isEmpty()){
            county = MapToAddressType( in.getOrgAddrCountyCd(), "COUNTY");
        }
         if ( !in.getOrgAddrStateCd().isEmpty()){
            state= MapToAddressType( in.getOrgAddrStateCd(), "STATE");
        }
        if(!in.getOrgAddrZipCodeTxt().isEmpty()){
            zip = in.getOrgAddrZipCodeTxt();
        }
        if(!in.getOrgAddrCountryCd().isEmpty()){
            country = MapToAddressType( in.getOrgAddrCountryCd(), "COUNTRY");
        }
        if(!in.getOrgPhoneNbrTxt().isEmpty()){
            phone=in.getOrgPhoneNbrTxt();
        }
        if(in.getOrgPhoneExtensionTxt() != null){
            extn= in.getOrgPhoneExtensionTxt().toString();
        }
        if(!in.getOrgIdCliaNbrTxt().isEmpty()){
            out.getParticipantRole().getIdArray(1).setRoot("2.16.840.1.113883.4.6");
            out.getParticipantRole().getIdArray(1).setExtension(in.getOrgIdCliaNbrTxt());
            out.getParticipantRole().getIdArray(1).setAssigningAuthorityName("LR_CLIA");
        }

        int isAddressPopulated= 0;
        if(!streetAddress1.isEmpty()){
            AdxpStreetAddressLine val = AdxpStreetAddressLine.Factory.newInstance();
            val.set(MapToCData(streetAddress1));
            out.getParticipantRole().getAddrArray(0).addNewStreetAddressLine();
            out.getParticipantRole().getAddrArray(0).setStreetAddressLineArray(0, val);
            isAddressPopulated=1;
        }
        if(!streetAddress2.isEmpty() ){
            AdxpStreetAddressLine val = AdxpStreetAddressLine.Factory.newInstance();
            val.set(MapToCData(streetAddress2));
            out.getParticipantRole().getAddrArray(0).addNewStreetAddressLine();
            if (out.getParticipantRole().getAddrArray(0).getStreetAddressLineArray().length > 1) {
                out.getParticipantRole().getAddrArray(0).setStreetAddressLineArray(1, val);
            }
            else {
                out.getParticipantRole().getAddrArray(0).setStreetAddressLineArray(0, val);
            }

            isAddressPopulated=1;
        }
        if(!city.isEmpty()){
            AdxpCity val = AdxpCity.Factory.newInstance();
            val.set(MapToCData(city));

            out.getParticipantRole().getAddrArray(0).addNewCity();
            out.getParticipantRole().getAddrArray(0).setCityArray(0, val);

            isAddressPopulated=1;
        }
        if(!state.isEmpty()){
            AdxpState val = AdxpState.Factory.newInstance();
            val.set(MapToCData(state));

            out.getParticipantRole().getAddrArray(0).addNewState();
            out.getParticipantRole().getAddrArray(0).setStateArray(0, val);

            isAddressPopulated=1;
        }
        if(!county.isEmpty()){
            AdxpCounty val = AdxpCounty.Factory.newInstance();
            val.set(MapToCData(county));

            out.getParticipantRole().getAddrArray(0).addNewCounty();
            out.getParticipantRole().getAddrArray(0).setCountyArray(0, val);

            isAddressPopulated=1;
        }
        if(!zip.isEmpty()){
            AdxpPostalCode val = AdxpPostalCode.Factory.newInstance();
            val.set(MapToCData(zip));

            out.getParticipantRole().getAddrArray(0).addNewPostalCode();
            out.getParticipantRole().getAddrArray(0).setPostalCodeArray(0, val);
            isAddressPopulated=1;
        }
        if(!country.isEmpty()){
            AdxpCountry val = AdxpCountry.Factory.newInstance();
            val.set(MapToCData(zip));

            out.getParticipantRole().getAddrArray(0).addNewCountry();
            out.getParticipantRole().getAddrArray(0).setCountryArray(0, val);

            isAddressPopulated=1;
        }
        if(isAddressPopulated>0)
            out.getParticipantRole().getAddrArray(0).setUse(new ArrayList(Arrays.asList("WP")));


        if(!phone.isEmpty()){
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
            String value = entry.getValue().toString();
        }

        if (!in.getPrvLocalId().isEmpty()) {
            out.getParticipantRole().getIdArray(0).setExtension(in.getPrvLocalId());
            out.getParticipantRole().getIdArray(0).setRoot("2.16.840.1.113883.11.19745");
            out.getParticipantRole().getIdArray(0).setAssigningAuthorityName("LR");
        }
        if (!in.getPrvNameFirstTxt().isEmpty()) {
            firstName = in.getPrvNameFirstTxt();
        }
        if (!in.getPrvNamePrefixCd().isEmpty()) {
            prefix = in.getPrvNamePrefixCd();
        }
        if (!in.getPrvNameLastTxt().isEmpty()) {
            prefix = in.getPrvNameLastTxt();
        }
        if(!in.getPrvNameSuffixCd().isEmpty()) {
           lastName = in.getPrvNameSuffixCd();
        }
        if(!in.getPrvNameDegreeCd().isEmpty()) {
            degree = in.getPrvNameDegreeCd();
        }
        if(!in.getPrvAddrStreetAddr1Txt().isEmpty()) {
            address1 = in.getPrvAddrStreetAddr1Txt();
        }
        if(!in.getPrvAddrStreetAddr2Txt().isEmpty()) {
            address2 = in.getPrvAddrStreetAddr2Txt();
        }
        if(!in.getPrvAddrCityTxt().isEmpty()) {
            city = in.getPrvAddrCityTxt();
        }
        if(!in.getPrvAddrCountyCd().isEmpty()) {
            county = MapToAddressType(in.getPrvAddrCountyCd(), "COUNTY");
        }
        if(!in.getPrvAddrStateCd().isEmpty()) {
            state = MapToAddressType(in.getPrvAddrStateCd(), "STATE");
        }
        if(!in.getPrvAddrZipCodeTxt().isEmpty()) {
            zip = in.getPrvAddrZipCodeTxt();
        }
        if(!in.getPrvAddrCountryCd().isEmpty()) {
            country = MapToAddressType(in.getPrvAddrCountryCd(), "COUNTRY");
        }
        if(!in.getPrvPhoneNbrTxt().isEmpty()) {
            telephone = in.getPrvPhoneNbrTxt();
        }
        if(in.getPrvPhoneExtensionTxt() != null) {
            extn = in.getPrvPhoneExtensionTxt().toString();
        }
        if(!in.getPrvIdQuickCodeTxt().isEmpty()) {
            out.getParticipantRole().getIdArray(1).setExtension(in.getPrvIdQuickCodeTxt());
            out.getParticipantRole().getIdArray(1).setRoot("2.16.840.1.113883.11.19745");
            out.getParticipantRole().getIdArray(1).setAssigningAuthorityName("LR_QEC");
        }
        if(!in.getPrvEmailAddressTxt().isEmpty()) {
            email = in.getPrvEmailAddressTxt();
        }

        /////



        if(!firstName.isEmpty()){
            var mapVal = MapToCData(firstName);
            EnGiven enG = EnGiven.Factory.newInstance();
            enG.set(mapVal);
            out.getParticipantRole().getPlayingEntity().getNameArray(0).addNewGiven();
            out.getParticipantRole().getPlayingEntity().getNameArray(0).setGivenArray(0, enG);
            out.getParticipantRole().getPlayingEntity().getNameArray(0).setUse(new ArrayList(Arrays.asList("L")));
        }
        if(!lastName.isEmpty()){
            var mapVal = MapToCData(lastName);
            EnFamily enG = EnFamily.Factory.newInstance();
            enG.set(mapVal);
            out.getParticipantRole().getPlayingEntity().getNameArray(0).addNewFamily();
            out.getParticipantRole().getPlayingEntity().getNameArray(0).setFamilyArray(0, enG);
            out.getParticipantRole().getPlayingEntity().getNameArray(0).setUse(new ArrayList(Arrays.asList("L")));
        }
        if(!prefix.isEmpty()){
            var mapVal = MapToCData(prefix);
            EnPrefix enG = EnPrefix.Factory.newInstance();
            enG.set(mapVal);
            out.getParticipantRole().getPlayingEntity().getNameArray(0).addNewPrefix();
            out.getParticipantRole().getPlayingEntity().getNameArray(0).setPrefixArray(0, enG);
        }
        if(!suffix.isEmpty()){
            var mapVal = MapToCData(suffix);
            EnSuffix enG = EnSuffix.Factory.newInstance();
            enG.set(mapVal);
            out.getParticipantRole().getPlayingEntity().getNameArray(0).addNewSuffix();
            out.getParticipantRole().getPlayingEntity().getNameArray(0).setSuffixArray(0, enG);
        }
        if(!address1.isEmpty()){
            var mapVal = MapToCData(address1);
            AdxpStreetAddressLine enG = AdxpStreetAddressLine.Factory.newInstance();
            enG.set(mapVal);
            out.getParticipantRole().getAddrArray(0).addNewStreetAddressLine();
            out.getParticipantRole().getAddrArray(0).setStreetAddressLineArray(0, enG);
        }
        if(!address2.isEmpty()){
            var mapVal = MapToCData(address2);
            AdxpStreetAddressLine enG = AdxpStreetAddressLine.Factory.newInstance();
            enG.set(mapVal);

            out.getParticipantRole().getAddrArray(0).addNewStreetAddressLine();
            if (out.getParticipantRole().getAddrArray(0).getStreetAddressLineArray().length > 1) {
                out.getParticipantRole().getAddrArray(0).setStreetAddressLineArray(1, enG);
            }
            out.getParticipantRole().getAddrArray(0).setStreetAddressLineArray(0, enG);
        }
        if(!city.isEmpty()){
            var mapVal = MapToCData(city);
            AdxpCity enG = AdxpCity.Factory.newInstance();
            enG.set(mapVal);
            out.getParticipantRole().getAddrArray(0).addNewCity();
            out.getParticipantRole().getAddrArray(0).setCityArray(0, enG);
        }
        if(!county.isEmpty()){
            var mapVal = MapToCData(county);
            AdxpCounty enG = AdxpCounty.Factory.newInstance();
            enG.set(mapVal);
            out.getParticipantRole().getAddrArray(0).addNewCounty();
            out.getParticipantRole().getAddrArray(0).setCountyArray(0, enG);
        }
        if(!zip.isEmpty()){
            var mapVal = MapToCData(zip);
            AdxpPostalCode enG = AdxpPostalCode.Factory.newInstance();
            enG.set(mapVal);
            out.getParticipantRole().getAddrArray(0).addNewPostalCode();
            out.getParticipantRole().getAddrArray(0).setPostalCodeArray(0, enG);
        }

        if(!state.isEmpty()){
            var mapVal = MapToCData(state);
            AdxpState enG = AdxpState.Factory.newInstance();
            enG.set(mapVal);
            out.getParticipantRole().getAddrArray(0).addNewState();
            out.getParticipantRole().getAddrArray(0).setStateArray(0, enG);
        }
        if(!country.isEmpty()){
            var mapVal = MapToCData(country);
            AdxpCountry enG = AdxpCountry.Factory.newInstance();
            enG.set(mapVal);
            out.getParticipantRole().getAddrArray(0).addNewCountry();
            out.getParticipantRole().getAddrArray(0).setCountryArray(0, enG);
        }
        if(!telephone.isEmpty()){
            out.getParticipantRole().getTelecomArray(teleCounter).setUse(new ArrayList(Arrays.asList("WP")));
            int phoneExtnSize= extn.length();
            if(phoneExtnSize>0){
                telephone=telephone+ ";extn="+ extn;
            }else
                telephone=telephone;
            out.getParticipantRole().getTelecomArray(teleCounter).setValue(telephone);
            teleCounter = teleCounter+1;
        } if(!email.isEmpty()){
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
            String value = entry.getValue().toString();

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
                    var obs = MapTripletToObservation(
                            caseDto.getMsgCase().getInvConditionCd(),
                            quesId,
                            output.getComponentArray(componentCaseCounter).getSection().getEntryArray(counter).getObservation()
                    );
                    output.getComponentArray(componentCaseCounter).getSection().getEntryArray(counter).setObservation(obs);
                    repeats = 0;
                }
                else {
                    POCDMT000040Observation obs = MapToObservation(
                            quesId,
                            caseDto.getMsgCase().getInvConditionCd(),
                            output.getComponentArray(componentCaseCounter).getSection().getEntryArray(counter).getObservation()
                    );
                    output.getComponentArray(componentCaseCounter).getSection().getEntryArray(counter).setObservation(obs);
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
            String value = entry.getValue().toString();

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
