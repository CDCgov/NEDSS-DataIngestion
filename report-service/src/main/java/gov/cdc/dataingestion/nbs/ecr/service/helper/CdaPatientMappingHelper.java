package gov.cdc.dataingestion.nbs.ecr.service.helper;

import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgPatientDto;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Timestamp;

import static gov.cdc.dataingestion.nbs.ecr.service.helper.CdaMapStringHelper.capitalize;

public class CdaPatientMappingHelper {
    public static boolean validatePatientGenericField(Field field, EcrMsgPatientDto patient) {
        switch (field.getName()) {
            case "patInfoAsOfDt":
                return patient.getPatInfoAsOfDt() != null;
            case "patAddrCommentTxt":
            case "patAdditionalGenderTxt":
            case "patSpeaksEnglishIndCd":
            case "patIdStateHivCaseNbrTxt":
            case "patEthnicityUnkReasonCd":
            case "patSexUnkReasonCd":
            case "patPhoneCommentTxt":
            case "patDeceasedIndCd":
            case "patPreferredGenderCd":
            case "patReportedAgeUnitCd":
            case "patCommentTxt":
            case "patBirthSexCd":
                String value = getStringValue(field.getName(), patient);
                return value != null && !value.isEmpty();
            case "patDeceasedDt":
            case "patReportedAge":
                return getDateTimeValue(field.getName(), patient) != null;
            default:
                return false;
        }
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

}
