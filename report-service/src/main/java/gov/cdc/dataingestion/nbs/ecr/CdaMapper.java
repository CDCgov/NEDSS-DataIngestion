package gov.cdc.dataingestion.nbs.ecr;

import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedRecord;
import gov.cdc.nedss.phdc.cda.*;
import gov.cdc.nedss.phdc.cda.impl.ClinicalDocumentDocument1Impl;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;

import java.math.BigInteger;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public void test(EcrSelectedRecord input) throws XmlException {


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

            int k = 1;
            int nameCounter = 1;

            POCDMT000040RecordTarget rtarget= POCDMT000040RecordTarget.Factory.newInstance();
            POCDMT000040PatientRole patientRole = POCDMT000040PatientRole.Factory.newInstance();
            rtarget.setPatientRole(patientRole);
            POCDMT000040RecordTarget[] recordTarget = {rtarget};
            clinicalDocument.setRecordTargetArray(recordTarget);
            if (patient.getPatPrimaryLanguageCd() != null && !patient.getPatPrimaryLanguageCd().isEmpty()) {
                clinicalDocument.setLanguageCode(CS.Factory.newInstance());
                clinicalDocument.getLanguageCode().setCode(patient.getPatPrimaryLanguageCd());
            }

            List<II> idArrayList = new ArrayList<II>();
            if (patient.getPatLocalId() != null && !patient.getPatLocalId().isEmpty()) {
                II ii = II.Factory.newInstance();
                ii.setExtension(patient.getPatPrimaryLanguageCd());
                ii.setRoot("2.16.840.1.113883.4.1");
                ii.setAssigningAuthorityName("LR");
                idArrayList.add(ii);
            }
            if (patient.getPatIdMedicalRecordNbrTxt() != null && !patient.getPatIdMedicalRecordNbrTxt().isEmpty()) {
                II ii = II.Factory.newInstance();
                ii.setExtension(patient.getPatIdMedicalRecordNbrTxt());
                ii.setRoot("2.16.840.1.113883.4.1");
                ii.setAssigningAuthorityName("LR_MRN");
                idArrayList.add(ii);
            }
            if (patient.getPatIdSsnTxt() != null && !patient.getPatIdSsnTxt().isEmpty()) {
                II ii = II.Factory.newInstance();
                ii.setExtension(patient.getPatIdSsnTxt());
                ii.setRoot("2.16.840.1.114222.4.5.1");
                ii.setAssigningAuthorityName("SS");
                idArrayList.add(ii);
            }
            II[] iiArray = idArrayList.toArray(new II[0]);
            clinicalDocument.getRecordTargetArray(0).getPatientRole().setIdArray(iiArray);

            if (patient.getPatAddrStreetAddr1Txt() != null && !patient.getPatAddrStreetAddr1Txt().isEmpty()) {
                address1 += patient.getPatAddrStreetAddr1Txt();
            }
            if (patient.getPatAddrStreetAddr2Txt() != null && !patient.getPatAddrStreetAddr2Txt().isEmpty()) {
                address2 += patient.getPatAddrStreetAddr2Txt();
            }

            AD addr = AD.Factory.newInstance();
            AdxpCity city = AdxpCity.Factory.newInstance();
            AdxpCity[] cityArray = {city};
            addr.setCityArray(cityArray);
            AD[] addrArray = {addr};
            if(patient.getPatAddrCityTxt() != null && !patient.getPatAddrCityTxt().isEmpty()) {
                clinicalDocument.getRecordTargetArray(0).getPatientRole().setAddrArray(addrArray);
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).getCityArray(0).set(MapToCData(patient.getPatAddrCityTxt()));
                // set City here
                k++;
            }
            if(patient.getPatAddrStateCd() != null && !patient.getPatAddrStateCd().isEmpty()) {
                var state = MapToAddressType(patient.getPatAddrStateCd(), "STATE");
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).getStateArray(0).set(MapToCData(state));
                k++;
            }
            if(patient.getPatAddrZipCodeTxt() != null && !patient.getPatAddrZipCodeTxt().isEmpty()) {
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).getPostalCodeArray(0).set(MapToCData(patient.getPatAddrZipCodeTxt()));
                k++;
            }
            if(patient.getPatAddrCountyCd() != null && !patient.getPatAddrCountyCd().isEmpty()) {
                var state = MapToAddressType(patient.getPatAddrCountyCd(), "COUNTY");
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).getCountyArray(0).set(MapToCData(state));
                k++;
            }
            if(patient.getPatAddrCountryCd() != null && !patient.getPatAddrCountryCd().isEmpty()) {
                var state = MapToAddressType(patient.getPatAddrCountryCd(), "COUNTRY");
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).getCountryArray(0).set(MapToCData(state));
                k++;
            }
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
                // ORIG: 211
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(1).setUse(new ArrayList<String> (Arrays.asList("P")));
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(1).getGivenArray(0).set(MapToCData(patient.getPatNameAliasTxt()));
            }

            if(patient.getPatCurrentSexCd() != null && !patient.getPatCurrentSexCd().isEmpty()) {
                String questionCode = MapToQuestionId(patient.getPatCurrentSexCd());
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


            if(!PAT_NAME_PREFIX_CD.isEmpty()) {
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).getPrefixArray(0).set(MapToCData(PAT_NAME_PREFIX_CD));
                nameCounter++;
            }
            if(!PAT_NAME_FIRST_TXT.isEmpty()) {
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).getGivenArray(0).set(MapToCData(PAT_NAME_PREFIX_CD));
                nameCounter++;
            }
            if(!PAT_NAME_MIDDLE_TXT.isEmpty()) {
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).getGivenArray(0).set(MapToCData(PAT_NAME_PREFIX_CD));
                nameCounter++;
            }
            if(!PAT_NAME_LAST_TXT.isEmpty()) {
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).getFamilyArray(0).set(MapToCData(PAT_NAME_PREFIX_CD));
                nameCounter++;
            }
            if(!PAT_NAME_SUFFIX_CD.isEmpty()) {
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).getSuffixArray(0).set(MapToCData(PAT_NAME_PREFIX_CD));
                nameCounter++;
            }

            // PAT_HOME_PHONE_NBR_TXT
            // line 352
            // line 412

            if(!address1.isEmpty()) {
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).getStreetAddressLineArray(0).set(MapToCData(address1));
            }

            if(!address2.isEmpty()) {
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).getStreetAddressLineArray(0).set(MapToCData(address2));
            }

        }
        //endregion

        if(!input.getMsgCases().isEmpty()) {
            if (componentCounter < 0) {
                componentCounter++;
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
        }

        if(!input.getMsgXmlAnswers().isEmpty()) {
            // 444
        }

        if(!input.getMsgProviders().isEmpty()) {
            // 449
        }

        if(!input.getMsgOrganizations().isEmpty()) {
            // 474
        }

        if(!input.getMsgPlaces().isEmpty()) {
            // 498
        }

        if(!input.getMsgInterviews().isEmpty()) {
            // 523
        }

        if(!input.getMsgTreatments().isEmpty()) {
            // 543
        }


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

    private String MapToAddressType(String data, String questionCode) {
        String output = "";
        String code="";
        String toDisplayName="";
        String codeSystemeDescTxt="";
        String codeSystem="";
        String codeSystemName="";
        String displayName="";
        String transCodeSystem="";
        String transCode="";
        String transCodeSystemName="";
        String transDisplayName="";

        // Mapping To Code Anwser goes here, may have to call out to RhapsodyAnswer table

        if (!code.isEmpty()) {
            output = code;
        }

        if (!displayName.isEmpty()) {
            output = output + "^" + displayName;
        }

        if (!codeSystemName.isEmpty()) {
            output = output + "^" + codeSystemName;
        }


        return output;

    }

    private String MapToQuestionId(String data) {
        // Rhapsody look up
        return "";
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
}
