package gov.cdc.dataingestion.nbs.ecr;

import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedRecord;
import gov.cdc.nedss.phdc.cda.*;

import java.math.BigInteger;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

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
        }
    }


    private String GetCurrentUtcDateTimeInCdaFormat() {
        ZonedDateTime utcNow = ZonedDateTime.now(ZoneId.of("UTC"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssX");
        String formattedDate = utcNow.format(formatter);
        return formattedDate;
    }
}
