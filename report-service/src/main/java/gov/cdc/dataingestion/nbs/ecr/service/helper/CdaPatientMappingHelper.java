package gov.cdc.dataingestion.nbs.ecr.service.helper;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.ecr.model.CdaPatientMapper;
import gov.cdc.dataingestion.nbs.ecr.model.ValueMapper;
import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgPatientDto;
import gov.cdc.nedss.phdc.cda.ClinicalDocumentDocument1;
import gov.cdc.nedss.phdc.cda.POCDMT000040ClinicalDocument1;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Date;

import static gov.cdc.dataingestion.nbs.ecr.constant.CdaConstantValue.*;
import static gov.cdc.dataingestion.nbs.ecr.service.helper.CdaMapHelper.mapToCData;
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

            clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().getId().setRoot(rootId);
            clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().getId().setExtension(inv168);
            clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().getId().setAssigningAuthorityName("LR");
            clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().getCode().setCode("297622");
            clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().getCode().setCodeSystem(codeSystem);
            clinicalDocument.getComponent().getStructuredBody().getComponentArray(patientComponentCounter).getSection().getCode().setCodeSystemName(codeSystemName);
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

    public static String getStringValue(String fieldName, EcrMsgPatientDto patient) {
        // Use reflection to get the value using the getter
        try {
            Method method = EcrMsgPatientDto.class.getMethod("get" + capitalize(fieldName));
            return (String) method.invoke(patient);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Timestamp getDateTimeValue(String fieldName, EcrMsgPatientDto patient) {
        // Use reflection to get the value using the getter
        try {
            Method method = EcrMsgPatientDto.class.getMethod("get" + capitalize(fieldName));
            return (Timestamp) method.invoke(patient);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static boolean isFieldValid(String fieldValue) {
        return fieldValue != null && !fieldValue.isEmpty();
    }

    private static boolean isFieldValid(Date fieldValue) {
        return fieldValue != null;
    }

}
