package gov.cdc.dataprocessing.utilities.component.wds;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.model.dto.edx.EdxRuleManageDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsQuestionMetadata;
import gov.cdc.dataprocessing.utilities.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.sql.Timestamp;

@Component
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 1192 - duplicate literal
 135 - for loop
 117 - naming
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192", "java:S135", "java:S117"})
public class WdsObjectChecker {
    private static final Logger logger = LoggerFactory.getLogger(WdsObjectChecker.class); // NOSONAR

    @SuppressWarnings("java:S6541")
    public boolean checkNbsObject(EdxRuleManageDto edxRuleManageDT, Object object, NbsQuestionMetadata metaData) {
        String dataLocation = metaData.getDataLocation();
        String setMethodName = dataLocation.replaceAll("_", "");
        setMethodName = "SET" + setMethodName.substring(setMethodName.indexOf(".") + 1, setMethodName.length());

        String getMethodName = dataLocation.replaceAll("_", "");
        getMethodName = "GET" + getMethodName.substring(getMethodName.indexOf(".") + 1, getMethodName.length());

        Class<?> phcClass = object.getClass();
        try {
            Method[] methodList = phcClass.getDeclaredMethods();
            for (Method item : methodList) {
                if (item.getName().equalsIgnoreCase(getMethodName)) {
                    Object ob = item.invoke(object, (Object[]) null);

                    String logic = edxRuleManageDT.getLogic();

                    if (ob == null && logic.equalsIgnoreCase("!="))
                        return true;
                    else if (ob == null)
                        return false;

                    if (metaData.getDataType().equalsIgnoreCase(
                            NEDSSConstant.NBS_QUESTION_DATATYPE_TEXT)
                            && (metaData.getMask() == null || (!metaData
                            .getMask().equals(
                                    NEDSSConstant.NUMERIC_CODE) && !metaData
                            .getMask()
                            .equals(NEDSSConstant.NBS_QUESTION_DATATYPE_MASK_NUM_YYYY)))
                            || metaData
                            .getDataType()
                            .equalsIgnoreCase(
                                    NEDSSConstant.NBS_QUESTION_DATATYPE_CODED_VALUE)) {
                        if (logic.equalsIgnoreCase("CT") && edxRuleManageDT.getValue() != null) {
                            // for multi-selects separated by commas
                            String[] values = edxRuleManageDT.getValue().split(
                                    ",");
                            for (String value : values) {
                                if (!(ob.toString().contains(value))) {
                                    return false;
                                }
                            }
                            return true;
                        } else if (logic.equalsIgnoreCase("=")) {
                            if (ob.toString().trim().equals(edxRuleManageDT.getValue())) {
                                return true;
                            }
                        } else if (logic.equalsIgnoreCase("!=")) {
                            if (!ob.toString().equals(edxRuleManageDT.getValue())) {
                                return true;
                            }

                        }

                    } else if (metaData.getDataType().equalsIgnoreCase(
                            NEDSSConstant.NBS_QUESTION_DATATYPE_DATETIME)
                            || metaData.getDataType().equalsIgnoreCase(
                            NEDSSConstant.DATETIME_DATATYPE)
                            || metaData.getDataType().equalsIgnoreCase(
                            NEDSSConstant.NBS_QUESTION_DATATYPE_DATE)
                            || metaData
                            .getDataType()
                            .equalsIgnoreCase(
                                    NEDSSConstant.NBS_QUESTION_DATATYPE_NUMERIC)
                            || (metaData.getMask() != null && (metaData
                            .getMask().equals(
                                    NEDSSConstant.NUMERIC_CODE) || metaData
                            .getMask()
                            .equals(NEDSSConstant.NBS_QUESTION_DATATYPE_MASK_NUM_YYYY)))) {
                        long sourceValue;
                        Long advanceCriteria = null;
                        if (metaData.getDataType().toUpperCase().contains(NEDSSConstant.DATE_DATATYPE)) {
                            Timestamp time = (Timestamp) (ob);
                            sourceValue = time.getTime();
                            Timestamp adCrtTime = StringUtils.stringToStrutsTimestamp(edxRuleManageDT.getValue());
                            if (adCrtTime != null) {
                                advanceCriteria = adCrtTime.getTime();
                            }

                        } else {
                            if (ob != null) {
                                sourceValue = Long.parseLong(ob.toString());
                            }
                            else
                            {
                                sourceValue = 0L;
                            }
                            if (edxRuleManageDT.getValue() != null)
                                advanceCriteria = Long.parseLong(edxRuleManageDT.getValue());
                            else
                                advanceCriteria = 0L;
                        }

                        if (advanceCriteria != null) {
                            if (logic.equalsIgnoreCase("!=")) {
                                if (sourceValue != advanceCriteria) {
                                    return true;
                                }
                            } else if (logic.equalsIgnoreCase(">")) {
                                if (sourceValue > advanceCriteria) {
                                    return true;
                                }
                            } else if (logic.equalsIgnoreCase(">=")) {
                                if ((sourceValue == advanceCriteria) || (sourceValue > advanceCriteria)) {
                                    return true;
                                }
                            } else if (logic.equalsIgnoreCase("<")) {
                                if (sourceValue < advanceCriteria) {
                                    return true;
                                }
                            } else if (logic.equalsIgnoreCase("<=")) {
                                if ((sourceValue == advanceCriteria) || (sourceValue < advanceCriteria)) {
                                    return true;
                                }
                            } else if (logic.equalsIgnoreCase("=")) {
                                if (sourceValue == advanceCriteria) {
                                    return true;
                                }
                            }
                        }

                    } else
                        return false;

                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return false;
    }


}
