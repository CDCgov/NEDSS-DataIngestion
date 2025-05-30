package gov.cdc.dataprocessing.utilities.component.edx;

import gov.cdc.dataprocessing.cache.OdseCache;
import gov.cdc.dataprocessing.constant.DecisionSupportConstants;
import gov.cdc.dataprocessing.constant.NBSConstantUtil;
import gov.cdc.dataprocessing.constant.enums.ObjectName;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PublicHealthCaseContainer;
import gov.cdc.dataprocessing.model.dto.nbs.NbsCaseAnswerDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsQuestionMetadata;
import gov.cdc.dataprocessing.service.interfaces.cache.ICacheApiService;
import gov.cdc.dataprocessing.service.interfaces.lookup_data.ILookupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.*;

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
public class EdxPhcrDocumentUtil {
    private static final Logger logger = LoggerFactory.getLogger(EdxPhcrDocumentUtil.class); // NOSONAR

    public static final String REQUIRED = "_REQUIRED";

    private final ILookupService lookupService;

    private final ICacheApiService cacheApiService;

    public EdxPhcrDocumentUtil(ILookupService lookupService, @Lazy ICacheApiService cacheApiService)
    {
        this.lookupService = lookupService;
        this.cacheApiService = cacheApiService;
    }

    @SuppressWarnings("java:S3776")
    public Map<Object, Object> loadQuestions(String conditionCode) throws DataProcessingException {
        String invFormCd = "";
        if (cacheApiService.getSrteCacheBool(ObjectName.INVESTIGATION_FORM_CONDITION_CODE.name(), conditionCode))
        {
            invFormCd = cacheApiService.getSrteCacheString(ObjectName.INVESTIGATION_FORM_CONDITION_CODE.name(), conditionCode);
        }
        if(invFormCd==null || invFormCd.startsWith("INV_FORM"))
        {
            invFormCd= DecisionSupportConstants.CORE_INV_FORM;
        }
        Map<Object,Object> tempMap = new HashMap<>();
        Map<Object,Object> generalMap = new HashMap<>();

        //Check to see if it is single condition or multiple conditions
        if(invFormCd != null)
        {
            if(invFormCd.equals(NBSConstantUtil.INV_FORM_RVCT)|| invFormCd.equals(NBSConstantUtil.INV_FORM_VAR))
            {
                if(lookupService.getQuestionMap()!=null && lookupService.getQuestionMap().containsKey(invFormCd))
                {
                    tempMap = (Map<Object, Object> )lookupService.getQuestionMap().get(invFormCd);
                }
            }
            else
            {
                if(OdseCache.dmbMap.containsKey(invFormCd))
                {
                    tempMap.putAll((Map<Object, Object> ) OdseCache.dmbMap.get(invFormCd));
                }
                else if(!OdseCache.dmbMap.containsKey(invFormCd))
                {
//                    Map<Object, Object> questions = (Map<Object, Object> )lookupService.getDMBQuestionMapAfterPublish().get(invFormCd);
                    Map<Object, Object> questions = (Map<Object, Object> ) OdseCache.DMB_QUESTION_MAP.get(invFormCd);

                    if(questions != null)
                    {
                        tempMap.putAll(questions);
                    }
                }
            }

            if(tempMap != null){
                for (Object o : tempMap.keySet()) {
                    String key = (String) o;
                    NbsQuestionMetadata metaData = (NbsQuestionMetadata) tempMap.get(key);
                    generalMap.put(key, metaData);
                }
            }
        }


        return generalMap;

    }


    @SuppressWarnings({"java:S3776", "java:S1066"})

    public String requiredFieldCheck(Map<Object, Object> requiredQuestionIdentifierMap, Map<Object, Object> nbsCaseAnswerMap) {
        //
        String requireFieldError = null;
        Iterator<Object> iter = (requiredQuestionIdentifierMap.keySet()).iterator();
        Collection<Object> errorTextColl = new ArrayList<>();
        try {
            while(iter.hasNext()){
                String reqdKey = (String) iter.next();
                if (reqdKey!=null) {
                    if (nbsCaseAnswerMap==null || nbsCaseAnswerMap.get(reqdKey) == null) {
                        NbsQuestionMetadata metaData = (NbsQuestionMetadata) requiredQuestionIdentifierMap
                                .get(reqdKey);
                        if(metaData.getQuestionGroupSeqNbr()==null){
                            errorTextColl.add("["+metaData.getQuestionLabel()+ "]");

                        }

                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        if(errorTextColl!=null && !errorTextColl.isEmpty()){
            Iterator<Object> iterator = errorTextColl.iterator();
            StringBuilder errorTextString = new StringBuilder();
            while(iterator.hasNext()){
                String errorText = (String)iterator.next();
                if(errorTextColl.size()==1){
                    errorTextString = new StringBuilder(errorText);
                }else{
                    if(iterator.hasNext()){
                        errorTextString.append(errorText).append("; ");
                    }else{
                        errorTextString.append(" and ").append(errorText).append(". ");
                    }
                }
            }
            if(errorTextColl.size()==1){
                requireFieldError = "The following required field is missing: "+ errorTextString;
            }else if(errorTextColl.size()>1){
                requireFieldError = "The following required field(s) are missing: "+ errorTextString;
            }else
                requireFieldError =null;

        }
        return requireFieldError;
    }


    public NbsCaseAnswerDto setStandardNBSCaseAnswerVals(
            PublicHealthCaseContainer publicHealthCaseContainer,
            NbsCaseAnswerDto nbsCaseAnswerDT) {

        nbsCaseAnswerDT.setActUid(publicHealthCaseContainer.getThePublicHealthCaseDto()
                .getPublicHealthCaseUid());
        nbsCaseAnswerDT.setAddTime(publicHealthCaseContainer
                .getThePublicHealthCaseDto().getAddTime());
        nbsCaseAnswerDT.setLastChgTime(publicHealthCaseContainer
                .getThePublicHealthCaseDto().getLastChgTime());
        nbsCaseAnswerDT.setAddUserId(publicHealthCaseContainer
                .getThePublicHealthCaseDto().getAddUserId());
        nbsCaseAnswerDT.setLastChgUserId(publicHealthCaseContainer
                .getThePublicHealthCaseDto().getLastChgUserId());
        nbsCaseAnswerDT.setRecordStatusCd("OPEN");
        if (nbsCaseAnswerDT.getSeqNbr() != null
                && nbsCaseAnswerDT.getSeqNbr() < 0)
            nbsCaseAnswerDT.setSeqNbr(0);
        nbsCaseAnswerDT.setRecordStatusTime(publicHealthCaseContainer
                .getThePublicHealthCaseDto().getRecordStatusTime());
        nbsCaseAnswerDT.setItNew(true);
        // if (nbsCaseAnswerDT.getNbsQuestionUid() == null) {
        // logger.error("There is no question identifier");
        // }
        return nbsCaseAnswerDT;
    }





}
