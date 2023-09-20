package gov.cdc.dataingestion.nbs.ecr;

import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedRecord;
import gov.cdc.nedss.phdc.cda.*;

import java.math.BigInteger;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

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

    public void test(EcrSelectedRecord input) {
        try {
            var clinical = ClinicalDocumentDocument1.Factory.newInstance();

        } catch (Exception e) {
            var error = e;
            System.out.println(e.getMessage());
        }
        POCDMT000040ClinicalDocument1 clinicalDocument = POCDMT000040ClinicalDocument1.Factory.newInstance();

        clinicalDocument.setRealmCodeArray(0, CS.Factory.newInstance());
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

            POCDMT000040RecordTarget[] recordTarget = {POCDMT000040RecordTarget.Factory.newInstance()};
            clinicalDocument.setRecordTargetArray(recordTarget);
            if (patient.getPatPrimaryLanguageCd() != null && !patient.getPatPrimaryLanguageCd().isEmpty()) {
                clinicalDocument.setLanguageCode(CS.Factory.newInstance());
                clinicalDocument.getLanguageCode().setCode(patient.getPatPrimaryLanguageCd());
            }
            if (patient.getPatLocalId() != null && !patient.getPatLocalId().isEmpty()) {
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getIdArray(patientIdentifier).setExtension(patient.getPatPrimaryLanguageCd());
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getIdArray(patientIdentifier).setRoot("2.16.840.1.113883.4.1");
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getIdArray(patientIdentifier).setAssigningAuthorityName("LR");
                patientIdentifier++;
            }
            if (patient.getPatIdMedicalRecordNbrTxt() != null && !patient.getPatIdMedicalRecordNbrTxt().isEmpty()) {
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getIdArray(patientIdentifier).setExtension(patient.getPatIdMedicalRecordNbrTxt());
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getIdArray(patientIdentifier).setRoot("2.16.840.1.113883.4.1");
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getIdArray(patientIdentifier).setAssigningAuthorityName("LR_MRN");
                patientIdentifier++;
            }
            if (patient.getPatIdSsnTxt() != null && !patient.getPatIdSsnTxt().isEmpty()) {
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getIdArray(patientIdentifier).setExtension(patient.getPatIdSsnTxt());
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getIdArray(patientIdentifier).setRoot("2.16.840.1.114222.4.5.1");
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getIdArray(patientIdentifier).setAssigningAuthorityName("SS");
                patientIdentifier++;
            }
            if (patient.getPatAddrStreetAddr1Txt() != null && !patient.getPatAddrStreetAddr1Txt().isEmpty()) {
                address1 += patient.getPatAddrStreetAddr1Txt();
            }
            if (patient.getPatAddrStreetAddr2Txt() != null && !patient.getPatAddrStreetAddr2Txt().isEmpty()) {
                address2 += patient.getPatAddrStreetAddr2Txt();
            }

            // PAT_ADDR_CITY_TXT
            // these need to be reinspect
            // line 158
            // PAT_ADDR_COUNTRY_CD

            if (patient.getPatWorkPhoneExtensionTxt() != null) {
                PAT_WORK_PHONE_EXTENSION_TXT = patient.getPatWorkPhoneExtensionTxt().toString();
            }
            if (patient.getPatHomePhoneNbrTxt() != null) {
                PAT_HOME_PHONE_NBR_TXT = patient.getPatHomePhoneNbrTxt();
            }
            if (patient.getPatWorkPhoneNbrTxt() != null) {
                wpNumber = patient.getPatWorkPhoneNbrTxt();
            }
            if (patient.getPatPhoneCountryCodeTxt() != null) {
                PAT_PHONE_COUNTRY_CODE_TXT = patient.getPatPhoneCountryCodeTxt().toString();
            }
            if (patient.getPatCellPhoneNbrTxt() != null) {
                cellNumber = patient.getPatCellPhoneNbrTxt();
            }
            if (patient.getPatNamePrefixCd() != null && !patient.getPatNamePrefixCd().trim().isEmpty()) {
                PAT_NAME_PREFIX_CD = patient.getPatNamePrefixCd();
            }
            if (patient.getPatNameFirstTxt() != null && !patient.getPatNameFirstTxt().trim().isEmpty()) {
                PAT_NAME_FIRST_TXT = patient.getPatNameFirstTxt();
            }
            if (patient.getPatNameMiddleTxt() != null && !patient.getPatNameMiddleTxt().trim().isEmpty()) {
                PAT_NAME_MIDDLE_TXT = patient.getPatNameMiddleTxt();
            }
            if (patient.getPatNameLastTxt() != null && !patient.getPatNameLastTxt().trim().isEmpty()) {
                PAT_NAME_LAST_TXT = patient.getPatNameLastTxt();
            }
            if (patient.getPatNameSuffixCd() != null && !patient.getPatNameSuffixCd().trim().isEmpty()) {
                PAT_NAME_SUFFIX_CD = patient.getPatNameSuffixCd();
            }
            if (patient.getPatNameAliasTxt() != null && !patient.getPatNameAliasTxt().trim().isEmpty()) {
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(1).setUse(new ArrayList<String> (Arrays.asList("P")));
                //out.recordTarget[0].patientRole.patient.name[1].PN#Grp1[1].given.#PCDATA=value;
            }

            // PAT_CURRENT_SEX_CD
            // these need to be reinspect
            // line 214
            // PAT_ADDR_CENSUS_TRACT_TXT
            if (patient.getPatEmailAddressTxt() != null && !patient.getPatEmailAddressTxt().trim().isEmpty()) {
                PAT_EMAIL_ADDRESS_TXT = patient.getPatEmailAddressTxt();
            }
            if (patient.getPatUrlAddressTxt() != null && !patient.getPatUrlAddressTxt().trim().isEmpty()) {
                PAT_URL_ADDRESS_TXT = patient.getPatUrlAddressTxt();
            }
            // PAT_NAME_AS_OF_DT
            // these need to be reinspect
            // line 270
            if (patient.getPatPhoneAsOfDt() != null) {
                PAT_PHONE_AS_OF_DT = patient.getPatPhoneAsOfDt().toString();
            }

            // PAT_INFO_AS_OF_DT
            // these need to be reinspect
            // line 274
        }
    }


    private String GetCurrentUtcDateTimeInCdaFormat() {
        ZonedDateTime utcNow = ZonedDateTime.now(ZoneId.of("UTC"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssX");
        String formattedDate = utcNow.format(formatter);
        return formattedDate;
    }
}
