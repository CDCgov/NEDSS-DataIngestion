package gov.cdc.dataprocessing.utilities.component.public_health_case;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dsma_algorithm.Algorithm;
import gov.cdc.dataprocessing.model.dsma_algorithm.CodedType;
import gov.cdc.dataprocessing.model.dsma_algorithm.InvCriteriaType;
import gov.cdc.dataprocessing.model.dsma_algorithm.InvValueType;
import gov.cdc.dataprocessing.model.dto.edx.EdxRuleManageDto;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
public class AdvancedCriteria {
    public Map<String, Object> getAdvancedInvCriteriaMap(Algorithm algorithmDocument) throws DataProcessingException {

        Map<String, Object> advanceInvCriteriaMap = new HashMap<>();
        try{
            InvCriteriaType advanceInvCriteriaType = algorithmDocument.getElrAdvancedCriteria().getInvCriteria();
            /* Create the advanced Criteria map to compare against matched PHCs */
            if (advanceInvCriteriaType != null) {
                for (int i = 0; i < advanceInvCriteriaType.getInvValue().size(); i++) {
                    InvValueType criteriaType = advanceInvCriteriaType
                            .getInvValue().get(i);
                    CodedType criteriaQuestionType = criteriaType.getInvQuestion();
                    CodedType criteriaLogicType = criteriaType
                            .getInvQuestionLogic();

                    if (criteriaType.getInvStringValue() == null
                            && !criteriaType.getInvCodedValue().isEmpty()) {
                        String value;
                        String[] array = new String[criteriaType
                                .getInvCodedValue().size()];
                        for (int j = 0; j < criteriaType.getInvCodedValue().size(); j++) {
                            array[j] = criteriaType.getInvCodedValue().get(j)
                                    .getCode();
                        }
                        Arrays.sort(array);
                        value = String.join(",", array);
                        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
                        edxRuleManageDT.setQuestionId(criteriaQuestionType
                                .getCode());
                        edxRuleManageDT.setLogic(criteriaLogicType.getCode());
                        edxRuleManageDT.setAdvanceCriteria(true);
                        edxRuleManageDT.setValue(value);
                        advanceInvCriteriaMap.put(criteriaQuestionType.getCode(),
                                edxRuleManageDT);

                    } else {
                        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
                        edxRuleManageDT.setQuestionId(criteriaQuestionType
                                .getCode());
                        edxRuleManageDT.setLogic(criteriaLogicType.getCode());
                        edxRuleManageDT.setAdvanceCriteria(true);
                        edxRuleManageDT.setValue(criteriaType.getInvStringValue());
                        advanceInvCriteriaMap.put(criteriaQuestionType.getCode(),
                                edxRuleManageDT);
                    }
                }
            }
        }catch(Exception ex){
            throw new DataProcessingException ("Exception while creating advanced Investigation Criteria Map: ", ex);
        }
        return advanceInvCriteriaMap;
    }

}
