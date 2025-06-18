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

    @SuppressWarnings({"java:S3776","java:S5411"})
    public Map<Object, Object> loadQuestions(String conditionCode) throws DataProcessingException {
        String invFormCd = "";
        if (cacheApiService.getSrteCacheBool(ObjectName.INVESTIGATION_FORM_CONDITION_CODE.name(), conditionCode)) {
            invFormCd = cacheApiService.getSrteCacheString(ObjectName.INVESTIGATION_FORM_CONDITION_CODE.name(), conditionCode);
        }

        if (invFormCd == null || invFormCd.startsWith("INV_FORM")) {
            invFormCd = DecisionSupportConstants.CORE_INV_FORM;
        }

        Map<Object, Object> tempMap = getQuestionMapForFormCode(invFormCd);
        Map<Object, Object> generalMap = new HashMap<>();

        if (tempMap != null) {
            for (Object o : tempMap.keySet()) {
                String key = (String) o;
                NbsQuestionMetadata metaData = (NbsQuestionMetadata) tempMap.get(key);
                generalMap.put(key, metaData);
            }
        }

        return generalMap;
    }

    protected Map<Object, Object> getQuestionMapForFormCode(String invFormCd) {
        Map<Object, Object> tempMap = new HashMap<>();

        if (invFormCd == null) {
            return tempMap;
        }

        if (invFormCd.equals(NBSConstantUtil.INV_FORM_RVCT) || invFormCd.equals(NBSConstantUtil.INV_FORM_VAR)) {
            TreeMap<Object, Object> questionMap = lookupService.getQuestionMap();
            if (questionMap != null && questionMap.containsKey(invFormCd)) {
                tempMap = (Map<Object, Object>) questionMap.get(invFormCd);
            }
        } else {
            if (OdseCache.dmbMap.containsKey(invFormCd)) {
                tempMap.putAll((Map<Object, Object>) OdseCache.dmbMap.get(invFormCd));
            } else {
                Map<Object, Object> questions = (Map<Object, Object>) OdseCache.DMB_QUESTION_MAP.get(invFormCd);
                if (questions != null) {
                    tempMap.putAll(questions);
                }
            }
        }

        return tempMap;
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
        if(!errorTextColl.isEmpty()){
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
            }

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

        return nbsCaseAnswerDT;
    }





}
