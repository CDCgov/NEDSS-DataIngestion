package gov.cdc.dataprocessing.utilities.component.public_health_case;

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

public class AdvancedCriteria {
    public Map<String, Object> getAdvancedInvCriteriaMap(Algorithm algorithmDocument)   {

        Map<String, Object> advanceInvCriteriaMap = new HashMap<>();
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

        return advanceInvCriteriaMap;
    }

}
