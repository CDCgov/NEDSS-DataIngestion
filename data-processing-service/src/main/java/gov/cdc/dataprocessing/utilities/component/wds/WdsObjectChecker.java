package gov.cdc.dataprocessing.utilities.component.wds;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.model.dto.edx.EdxRuleManageDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsQuestionMetadata;
import gov.cdc.dataprocessing.utilities.StringUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.sql.Timestamp;

@Component
public class WdsObjectChecker {
    @SuppressWarnings("java:S6541")
    public boolean checkNbsObject(EdxRuleManageDto edxRuleManageDT, Object object, NbsQuestionMetadata metaData) {
        String dataLocation = metaData.getDataLocation();
        String setMethodName = dataLocation.replaceAll("_", "");
        setMethodName = "SET" + setMethodName.substring(setMethodName.indexOf(".") + 1);

        String getMethodName = dataLocation.replaceAll("_", "");
        getMethodName = "GET" + getMethodName.substring(getMethodName.indexOf(".") + 1);

        Class<?> phcClass = object.getClass();
        try {
            Method[] methodList = phcClass.getDeclaredMethods();
            for (Method method : methodList) {
                if (method.getName().equalsIgnoreCase(getMethodName)) {
                    //System.out.println(method.getName());
                    Object ob = method.invoke(object, (Object[]) null);

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
                            } else {
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
            e.printStackTrace();
        }
        return false;
    }

}
