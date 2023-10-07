package gov.cdc.dataingestion.nbs.ecr.service.helper;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.ecr.model.CdaPatientMapper;
import gov.cdc.dataingestion.nbs.ecr.model.patient.CdaPatientTelecom;
import gov.cdc.dataingestion.nbs.ecr.model.ValueMapper;
import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgPatientDto;
import gov.cdc.nedss.phdc.cda.POCDMT000040ClinicalDocument1;
import gov.cdc.nedss.phdc.cda.TEL;
import org.apache.xmlbeans.XmlObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static gov.cdc.dataingestion.nbs.ecr.constant.CdaConstantValue.*;
import static gov.cdc.dataingestion.nbs.ecr.service.helper.CdaMapHelper.mapToCData;
import static gov.cdc.dataingestion.nbs.ecr.service.helper.CdaMapHelper.mapToUsableTSElement;
import static gov.cdc.dataingestion.nbs.ecr.service.helper.CdaMapStringHelper.capitalize;

public class CdaPatientMappingHelper {
    public static boolean validatePatientGenericField(Field field, EcrMsgPatientDto patient) {
        switch (field.getName()) {
            case "patInfoAsOfDt":
                return patient.getPatInfoAsOfDt() != null;
            case "patAddrCommentTxt":
                return patient.getPatAddrCommentTxt() != null;
            case "patAdditionalGenderTxt":
                return patient.getPatAdditionalGenderTxt() != null;
            case "patSpeaksEnglishIndCd":
                return patient.getPatSpeaksEnglishIndCd() != null;
            case "patIdStateHivCaseNbrTxt":
                return patient.getPatIdStateHivCaseNbrTxt() != null;
            case "patEthnicityUnkReasonCd":
                return patient.getPatEthnicityUnkReasonCd() != null;
            case "patSexUnkReasonCd":
                return patient.getPatSexUnkReasonCd() != null;
            case "patPhoneCommentTxt":
                return patient.getPatPhoneCommentTxt() != null;
            case "patDeceasedIndCd":
                return patient.getPatDeceasedIndCd() != null;
            case "patPreferredGenderCd":
                return patient.getPatPreferredGenderCd() != null;
            case "patReportedAgeUnitCd":
                return patient.getPatReportedAgeUnitCd() != null;
            case "patCommentTxt":
                return patient.getPatCommentTxt() != null;
            case "patBirthSexCd":
                return patient.getPatBirthSexCd() != null;
            case "patDeceasedDt":
                return patient.getPatDeceasedDt() != null;
            case "patReportedAge":
                return patient.getPatReportedAge() != null;
            default:
                return false;
        }
    }

    public static ValueMapper getPatientVariableNameAndValue(Field field,
                                                  EcrMsgPatientDto patient) {
        String colName = "";
        String value = "";
        if (field.getName().equals("patInfoAsOfDt") && isFieldValid(patient.getPatInfoAsOfDt())) {
            colName = "PAT_INFO_AS_OF_DT";
            value = patient.getPatInfoAsOfDt().toString();
        } else if (field.getName().equals("patAddrCommentTxt") && isFieldValid(patient.getPatAddrCommentTxt())) {
            colName = "PAT_ADDR_COMMENT_TXT";
            value = patient.getPatAddrCommentTxt();
        } else if (field.getName().equals("patAdditionalGenderTxt") && isFieldValid(patient.getPatAdditionalGenderTxt())) {
            colName = "PAT_ADDITIONAL_GENDER_TXT";
            value = patient.getPatAdditionalGenderTxt();
        } else if (field.getName().equals("patSpeaksEnglishIndCd") && isFieldValid(patient.getPatSpeaksEnglishIndCd())) {
            colName = "PAT_SPEAKS_ENGLISH_IND_CD";
            value = patient.getPatSpeaksEnglishIndCd();
        } else if (field.getName().equals("patIdStateHivCaseNbrTxt") && isFieldValid(patient.getPatIdStateHivCaseNbrTxt())) {
            colName = "PAT_ID_STATE_HIV_CASE_NBR_TXT";
            value = patient.getPatIdStateHivCaseNbrTxt();
        } else if (field.getName().equals("patEthnicityUnkReasonCd") && isFieldValid(patient.getPatEthnicityUnkReasonCd())) {
            colName = "PAT_ETHNICITY_UNK_REASON_CD";
            value = patient.getPatEthnicityUnkReasonCd();
        } else if (field.getName().equals("patSexUnkReasonCd") && isFieldValid(patient.getPatSexUnkReasonCd())) {
            colName = "PAT_SEX_UNK_REASON_CD";
            value = patient.getPatSexUnkReasonCd();
        } else if (field.getName().equals("patPhoneCommentTxt") && isFieldValid(patient.getPatPhoneCommentTxt())) {
            colName = "PAT_PHONE_COMMENT_TXT";
            value = patient.getPatPhoneCommentTxt();
        } else if (field.getName().equals("patDeceasedIndCd") && isFieldValid(patient.getPatDeceasedIndCd())) {
            colName = "PAT_DECEASED_IND_CD";
            value = patient.getPatDeceasedIndCd();
        } else if (field.getName().equals("patDeceasedDt") && isFieldValid(patient.getPatDeceasedDt())) {
            colName = "PAT_DECEASED_DT";
            value = patient.getPatDeceasedDt().toString();
        } else if (field.getName().equals("patPreferredGenderCd") && isFieldValid(patient.getPatPreferredGenderCd())) {
            colName = "PAT_PREFERRED_GENDER_CD";
            value = patient.getPatPreferredGenderCd();
        } else if (field.getName().equals("patReportedAge") && patient.getPatReportedAge() != null) {
            colName = "PAT_REPORTED_AGE";
            value = String.valueOf(patient.getPatReportedAge());
        } else if (field.getName().equals("patReportedAgeUnitCd") && isFieldValid(patient.getPatReportedAgeUnitCd())) {
            colName = "PAT_REPORTED_AGE_UNIT_CD";
            value = patient.getPatReportedAgeUnitCd();
        } else if (field.getName().equals("patCommentTxt") && isFieldValid(patient.getPatCommentTxt())) {
            colName = "PAT_COMMENT_TXT";
            value = patient.getPatCommentTxt();
        } else if (field.getName().equals("patBirthSexCd") && isFieldValid(patient.getPatBirthSexCd())) {
            colName = "PAT_BIRTH_SEX_CD";
            value = patient.getPatBirthSexCd();
        }

        ValueMapper map = new ValueMapper();
        map.setColName(colName);
        map.setValue(value);
        return map;
    }

    public static CdaPatientMapper mapPatientStructureComponent(
            int patientComponentCounter,
            POCDMT000040ClinicalDocument1 clinicalDocument,
            String inv168) throws EcrCdaXmlException {
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

            clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().getId().setRoot(ROOT_ID);
            clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().getId().setExtension(inv168);
            clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().getId().setAssigningAuthorityName("LR");
            clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().getCode().setCode("297622");
            clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().getCode().setCodeSystem(CODE_SYSTEM);
            clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().getCode().setCodeSystemName(CODE_SYSTEM_NAME);
            clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().getCode().setDisplayName("Social History");
            clinicalDocument.getComponent().getStructuredBody()
                    .getComponentArray(patientComponentCounter).getSection().getTitle().set(mapToCData("SOCIAL HISTORY INFORMATION"));

        }

        CdaPatientMapper mapper = new CdaPatientMapper();
        mapper.setClinicalDocument(clinicalDocument);
        mapper.setPatientComponentCounter(patientComponentCounter);
        return mapper;
    }


    public static POCDMT000040ClinicalDocument1 checkPatientRoleAddrArray(POCDMT000040ClinicalDocument1 clinicalDocument) {
        if (clinicalDocument.getRecordTargetArray(0)
                .getPatientRole().getAddrArray().length == 0) {
            clinicalDocument.getRecordTargetArray(0)
                    .getPatientRole().addNewAddr();
        }
        return clinicalDocument;
    }

    public static POCDMT000040ClinicalDocument1 checkPatientRole(POCDMT000040ClinicalDocument1 clinicalDocument) {
        if (!clinicalDocument.getRecordTargetArray(0).getPatientRole().isSetPatient()) {
            clinicalDocument.getRecordTargetArray(0)
                    .getPatientRole().addNewPatient();
        }
        return clinicalDocument;
    }

    public static POCDMT000040ClinicalDocument1 checkLanguageCode(POCDMT000040ClinicalDocument1 clinicalDocument) {
        if(!clinicalDocument.isSetLanguageCode()){
            clinicalDocument.addNewLanguageCode();
        }
        return clinicalDocument;
    }

    public static POCDMT000040ClinicalDocument1 checkPatientRoleAddrPostal(POCDMT000040ClinicalDocument1 clinicalDocument) {
        clinicalDocument = checkPatientRoleAddrArray(clinicalDocument);
        if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).getPostalCodeArray().length == 0) {
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).addNewPostalCode();
        }
        return clinicalDocument;
    }

    public static POCDMT000040ClinicalDocument1 checkPatientRoleAddrState(POCDMT000040ClinicalDocument1 clinicalDocument) {
        clinicalDocument = checkPatientRoleAddrArray(clinicalDocument);
        if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).getStateArray().length == 0) {
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).addNewState();
        }
        return clinicalDocument;
    }

    public static POCDMT000040ClinicalDocument1 checkPatientRoleAddrCity(POCDMT000040ClinicalDocument1 clinicalDocument) {
        clinicalDocument = checkPatientRoleAddrArray(clinicalDocument);
        if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).getCityArray().length == 0) {
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).addNewCity();
        }
        return clinicalDocument;
    }


    public static POCDMT000040ClinicalDocument1 checkPatientRoleAddrCountry(POCDMT000040ClinicalDocument1 clinicalDocument) {
        clinicalDocument = checkPatientRoleAddrArray(clinicalDocument);
        if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).getCountryArray().length == 0) {
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).addNewCountry();
        }
        return clinicalDocument;
    }

    public static POCDMT000040ClinicalDocument1 checkPatientRoleAddrCounty(POCDMT000040ClinicalDocument1 clinicalDocument) {
        clinicalDocument = checkPatientRoleAddrArray(clinicalDocument);
        if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).getCountyArray().length == 0) {
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(0).addNewCounty();
        }
        return clinicalDocument;
    }

    public static POCDMT000040ClinicalDocument1
        checkPatientRoleBirthCountry(POCDMT000040ClinicalDocument1 clinicalDocument) {
        clinicalDocument = checkPatientRole(clinicalDocument);
        if (!clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().isSetBirthplace()) {
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().addNewBirthplace();
        }
        return clinicalDocument;
    }

    public static POCDMT000040ClinicalDocument1 checkPatientRoleGenderCode(POCDMT000040ClinicalDocument1 clinicalDocument) {
        clinicalDocument = checkPatientRole(clinicalDocument);
        if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().isSetAdministrativeGenderCode()) {
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().addNewAdministrativeGenderCode();
        }
        return clinicalDocument;
    }

    public static POCDMT000040ClinicalDocument1 checkPatientRoleNameArray(POCDMT000040ClinicalDocument1 clinicalDocument) {
        clinicalDocument = checkPatientRole(clinicalDocument);
        if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray().length == 0) {
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().addNewName();
        }
        return clinicalDocument;
    }

    public static POCDMT000040ClinicalDocument1 checkPatientRoleFamilyName(POCDMT000040ClinicalDocument1 clinicalDocument) {
        clinicalDocument = checkPatientRoleNameArray(clinicalDocument);
        if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).getFamilyArray().length == 0) {
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).addNewFamily();
        };
        return clinicalDocument;
    }

    public static POCDMT000040ClinicalDocument1 checkPatientRoleSuffix(POCDMT000040ClinicalDocument1 clinicalDocument) {
        clinicalDocument = checkPatientRoleNameArray(clinicalDocument);
        if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).getSuffixArray().length == 0) {
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).addNewSuffix();
        }
        return clinicalDocument;
    }

    public static POCDMT000040ClinicalDocument1 checkPatientRolePrefix(POCDMT000040ClinicalDocument1 clinicalDocument) {
        clinicalDocument = checkPatientRoleNameArray(clinicalDocument);
        if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).getPrefixArray().length == 0) {
            clinicalDocument.getRecordTargetArray(0).getPatientRole().getPatient().getNameArray(0).addNewPrefix();
        }
        return clinicalDocument;
    }



    public static POCDMT000040ClinicalDocument1 checkPatientRoleAlias(POCDMT000040ClinicalDocument1 clinicalDocument) {
        clinicalDocument = checkPatientRole(clinicalDocument);

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
        return clinicalDocument;
    }

    public static CdaPatientTelecom mapPatientWpNumber(POCDMT000040ClinicalDocument1 clinicalDocument,
                                                       String wpNumber,
                                                       String PAT_WORK_PHONE_EXTENSION_TXT,
                                                       String PAT_PHONE_AS_OF_DT,
                                                       int phoneCounter) throws EcrCdaXmlException {
        CdaPatientTelecom param = new CdaPatientTelecom();
        try {
            if (!wpNumber.isEmpty()) {
                int pCount = 0;
                if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray().length == 0) {
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewTelecom();
                } else {
                    pCount = clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray().length + 1 - 1;
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewTelecom();
                }

                if(!PAT_WORK_PHONE_EXTENSION_TXT.isEmpty()){
                    wpNumber=wpNumber+ ";ext="+ PAT_WORK_PHONE_EXTENSION_TXT;
                }
                if(!PAT_PHONE_AS_OF_DT.isEmpty()){
                    TEL element = clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount);
                    element.set(XmlObject.Factory.parse(STUD));
                    var out = mapToUsableTSElement(PAT_PHONE_AS_OF_DT, element, USESABLE_PERIOD);
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().setTelecomArray(pCount, (TEL) out);
                }

                clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount).setUse(new ArrayList<String>(Arrays.asList("WP")));
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount).setValue(wpNumber);

                phoneCounter = phoneCounter +1;
            }

            param.setClinicalDocument(clinicalDocument);
            param.setPhoneCounter(phoneCounter);
            param.setWpNumber(wpNumber);
        } catch (Exception e) {
            throw new EcrCdaXmlException(e.getMessage());
        }

        return param;
    }

    public static CdaPatientTelecom mapPatientCellPhone(POCDMT000040ClinicalDocument1 clinicalDocument,
                                           String cellNumber,
                                           String PAT_PHONE_AS_OF_DT,
                                           int phoneCounter) throws EcrCdaXmlException {
        CdaPatientTelecom model = new CdaPatientTelecom();
        try {
            if(!cellNumber.isEmpty()) {
                int pCount = 0;
                if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray().length == 0) {
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewTelecom();
                } else {
                    pCount = clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray().length + 1 - 1;
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewTelecom();
                }

                if(!PAT_PHONE_AS_OF_DT.isEmpty()){
                    TEL element = clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount);
                    // CHECK mapToUsableTSElement
                    element.set(XmlObject.Factory.parse(STUD));
                    var out = mapToUsableTSElement(PAT_PHONE_AS_OF_DT, element, USESABLE_PERIOD);
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().setTelecomArray(pCount, (TEL) out);
                }

                clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount).setUse(new ArrayList<String>(Arrays.asList("MC")));
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount).setValue(cellNumber);
                phoneCounter =phoneCounter +1;
            }

            model.setPhoneCounter(phoneCounter);
            model.setClinicalDocument(clinicalDocument);
        } catch (Exception e) {
            throw new EcrCdaXmlException(e.getMessage());
        }

        return model;
    }

    public static CdaPatientTelecom mapPatientEmail(POCDMT000040ClinicalDocument1 clinicalDocument,
                               String PAT_EMAIL_ADDRESS_TXT,
                                       String PAT_PHONE_AS_OF_DT,
                                       int phoneCounter) throws EcrCdaXmlException {
        CdaPatientTelecom model = new CdaPatientTelecom();
        try {
            if(!PAT_EMAIL_ADDRESS_TXT.isEmpty()) {
                int pCount = 0;
                if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray().length == 0) {
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewTelecom();
                } else {
                    pCount = clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray().length + 1 - 1;
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewTelecom();
                }


                clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount).setUse(new ArrayList(Arrays.asList("HP")));
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount).setValue(MAIL_TO+PAT_EMAIL_ADDRESS_TXT);
                if(!PAT_PHONE_AS_OF_DT.isEmpty()){
                    TEL element = clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount);
                    // CHECK mapToUsableTSElement
                    element.set(XmlObject.Factory.parse(STUD));
                    var out = mapToUsableTSElement(PAT_PHONE_AS_OF_DT, element, USESABLE_PERIOD);
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().setTelecomArray(pCount, (TEL) out);
                }

                clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount).setUse(new ArrayList<String>(Arrays.asList("HP")));
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount).setValue(MAIL_TO + PAT_EMAIL_ADDRESS_TXT);
                phoneCounter =phoneCounter +1;
            }
            model.setClinicalDocument(clinicalDocument);
            model.setPhoneCounter(phoneCounter);
        } catch (Exception e ){
            throw new EcrCdaXmlException(e.getMessage());
        }
        return model;
    }

    public static CdaPatientTelecom mapPatientUrlAddress(POCDMT000040ClinicalDocument1 clinicalDocument,
                                             String PAT_URL_ADDRESS_TXT,
                                                         String PAT_PHONE_AS_OF_DT,
                                                         int phoneCounter) throws EcrCdaXmlException {
        CdaPatientTelecom model = new CdaPatientTelecom();
        try {
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
                    // CHECK mapToUsableTSElement
                    element.set(XmlObject.Factory.parse(STUD));
                    var out = mapToUsableTSElement(PAT_PHONE_AS_OF_DT, element, USESABLE_PERIOD);
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().setTelecomArray(pCount, (TEL) out);
                }

                clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount).setUse(new ArrayList<String>(Arrays.asList("HP")));
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getTelecomArray(pCount).setValue(PAT_URL_ADDRESS_TXT);
                phoneCounter =phoneCounter +1;
            }
            model.setClinicalDocument(clinicalDocument);
            model.setPhoneCounter(phoneCounter);
        } catch (Exception e) {
            throw new EcrCdaXmlException(e.getMessage());
        }
        return model;
    }

    public static POCDMT000040ClinicalDocument1 mapPatientAddress1(POCDMT000040ClinicalDocument1 clinicalDocument,
                                          String address1) throws EcrCdaXmlException {
        try {
            if(!address1.isEmpty()) {
                int c1 = 0;
                int c2 = 0;
                clinicalDocument = checkPatientRoleNameArray(clinicalDocument);
                if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(c1).getStreetAddressLineArray().length == 0) {
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(c1).addNewStreetAddressLine();
                }
                else {
                    c2 = clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(c1).getStreetAddressLineArray().length;
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(c1).getStreetAddressLineArray();
                }

                clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(c1).getStreetAddressLineArray(c2).set(mapToCData(address1));
            }
            return clinicalDocument;
        } catch (Exception e) {
            throw new EcrCdaXmlException(e.getMessage());
        }
    }

    public static POCDMT000040ClinicalDocument1 mapPatientAddress2(POCDMT000040ClinicalDocument1 clinicalDocument,
                                                                   String address2) throws EcrCdaXmlException {
        try {
            if(!address2.isEmpty()) {
                int c1 = 0;
                int c2 = 0;
                if (clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray().length == 0) {
                    clinicalDocument.getRecordTargetArray(0).getPatientRole().addNewAddr();
                }
                else {
                    c1 = clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray().length - 1;

                    if ( clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(c1).getStreetAddressLineArray().length == 0) {
                        clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(c1).addNewStreetAddressLine();
                    } else {
                        c2 = clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(c1).getStreetAddressLineArray().length;
                        clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(c1).addNewStreetAddressLine();
                    }
                }
                clinicalDocument.getRecordTargetArray(0).getPatientRole().getAddrArray(c1).getStreetAddressLineArray(c2).set(mapToCData(address2));
            }
            return clinicalDocument;
        } catch (Exception e) {
            throw new EcrCdaXmlException(e.getMessage());
        }
    }

    private static boolean isFieldValid(String fieldValue) {
        return fieldValue != null && !fieldValue.isEmpty();
    }

    private static boolean isFieldValid(Date fieldValue) {
        return fieldValue != null;
    }

}
