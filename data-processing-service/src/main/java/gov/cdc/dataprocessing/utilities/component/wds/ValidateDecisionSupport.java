package gov.cdc.dataprocessing.utilities.component.wds;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.base.BasePamContainer;
import gov.cdc.dataprocessing.model.container.model.PublicHealthCaseContainer;
import gov.cdc.dataprocessing.model.dsma_algorithm.CodedType;
import gov.cdc.dataprocessing.model.dsma_algorithm.DefaultValueType;
import gov.cdc.dataprocessing.model.dsma_algorithm.InvestigationDefaultValuesType;
import gov.cdc.dataprocessing.model.dto.act.ActIdDto;
import gov.cdc.dataprocessing.model.dto.edx.EdxRuleManageDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsCaseAnswerDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsQuestionMetadata;
import gov.cdc.dataprocessing.model.dto.phc.CaseManagementDto;
import gov.cdc.dataprocessing.model.dto.phc.ConfirmationMethodDto;
import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import gov.cdc.dataprocessing.utilities.StringUtils;
import gov.cdc.dataprocessing.utilities.component.edx.EdxPhcrDocumentUtil;
import gov.cdc.dataprocessing.utilities.time.TimeStampUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
public class ValidateDecisionSupport {
    private static final Logger logger = LoggerFactory.getLogger(ValidateDecisionSupport.class);
    @Value("${service.timezone}")
    private String tz = "UTC";

    private final EdxPhcrDocumentUtil edxPHCRDocumentUtil;

    public ValidateDecisionSupport(EdxPhcrDocumentUtil edxPHCRDocumentUtil) {
        this.edxPHCRDocumentUtil = edxPHCRDocumentUtil;
    }

    public void processNbsObject(EdxRuleManageDto edxRuleManageDT, PublicHealthCaseContainer publicHealthCaseContainer, NbsQuestionMetadata metaData){
        PublicHealthCaseDto publicHealthCaseDto = publicHealthCaseContainer.getThePublicHealthCaseDto();
        processNBSObjectDT( edxRuleManageDT, publicHealthCaseContainer, publicHealthCaseDto, metaData);
    }

    @SuppressWarnings({"java:S5361","java:S3776"})
    public void processNBSObjectDT(EdxRuleManageDto edxRuleManageDT, PublicHealthCaseContainer publicHealthCaseContainer, Object object, NbsQuestionMetadata metaData) {
        String behavior = edxRuleManageDT.getBehavior();
        boolean isOverwrite = false;

        if (behavior.equalsIgnoreCase("1")) {
            isOverwrite = true;
        } else if (behavior.equalsIgnoreCase("2")) {
            isOverwrite = false; // NOSONAR
        }
        String dataLocation = metaData.getDataLocation();
        /*
         * String setMethodName = dataLocation.replaceAll("_", "");
         * setMethodName = "SET"+ setMethodName.substring(
         * setMethodName.indexOf(".")+1, setMethodName.length());
         */

        String getMethodName = dataLocation.replaceAll("_", "");
        getMethodName = "GET" + getMethodName.substring(getMethodName.indexOf(".") + 1, getMethodName.length());

        Class<?> phcClass = object.getClass();
        try {
            Method[] methodList = phcClass.getDeclaredMethods();
            for (Method value : methodList) {
                if (value.getName().equalsIgnoreCase(getMethodName)) {
                    String setMethodName = value.getName().replaceAll("get", "set");

                    Method setMethod = null;
                    if (metaData.getDataType().equalsIgnoreCase(NEDSSConstant.NBS_QUESTION_DATATYPE_TEXT) || metaData.getDataType().equalsIgnoreCase(NEDSSConstant.NBS_QUESTION_DATATYPE_CODED_VALUE)) {
                        setMethod = phcClass.getMethod(setMethodName, String.class);

                    } else if (metaData.getDataType().equalsIgnoreCase(NEDSSConstant.NBS_QUESTION_DATATYPE_DATETIME) || metaData.getDataType().equalsIgnoreCase(NEDSSConstant.DATETIME_DATATYPE)
                            || metaData.getDataType().equalsIgnoreCase(NEDSSConstant.NBS_QUESTION_DATATYPE_DATE)) {
                        getCurrentDateValue(edxRuleManageDT);
                        setMethod = phcClass.getMethod(setMethodName, Timestamp.class);
                    } else if (metaData.getDataType().equalsIgnoreCase(NEDSSConstant.NBS_QUESTION_DATATYPE_NUMERIC)) {
                        if (value.getReturnType().equals(Integer.class))
                            setMethod = phcClass.getMethod(setMethodName, Integer.valueOf(0).getClass());
                        else if (value.getReturnType().equals(Long.class))
                            setMethod = phcClass.getMethod(setMethodName, Long.valueOf(0).getClass());
                        else if (value.getReturnType().equals(BigDecimal.class))
                            setMethod = phcClass.getMethod(setMethodName, BigDecimal.valueOf(0).getClass());
                        else if (value.getReturnType().equals(String.class)) // Added because question INV139's datatype is NUMERIC in nbs_ui_metadata table but the datatype is varchar in Public_Health_Case table.
                            setMethod = phcClass.getMethod(setMethodName, String.class);
                    }
                    Object ob = value.invoke(object, (Object[]) null);
                    if (isOverwrite) {
                        setMethod(object, setMethod, edxRuleManageDT);
                    } else if (!isOverwrite && ob == null) { // NOSONAR
                        setMethod(object, setMethod, edxRuleManageDT);// NOSONAR
                    } // NOSONAR
                }
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        } 


    }

    @SuppressWarnings({"java:S6541","java:S3776"})
    public void processNBSCaseAnswerDT(EdxRuleManageDto edxRuleManageDT, PublicHealthCaseContainer publicHealthCaseContainer, BasePamContainer pamVO, NbsQuestionMetadata metaData) {
        String behavior = edxRuleManageDT.getBehavior();
        boolean isOverwrite = false;
        if (behavior.equalsIgnoreCase("1")) {
            isOverwrite = true;
        } else if (behavior.equalsIgnoreCase("2")) {
            isOverwrite = false; //NOSONAR
        }
        String value = edxRuleManageDT.getDefaultStringValue();
        if(value!=null && value.equalsIgnoreCase(NEDSSConstant.USE_CURRENT_DATE))
        {
            value=new SimpleDateFormat("MM/dd/yyyy").format(Calendar.getInstance().getTime());
        }
        edxRuleManageDT.setDefaultStringValue(value);
        Map<Object,Object> answerMap = pamVO.getPamAnswerDTMap();
        if (isOverwrite) {
            Collection<Object> list = new ArrayList<>();
            if (metaData.getNbsUiComponentUid().compareTo(1013L) == 0) // NOSONAR
            {
                Collection<Object> toValueColl = edxRuleManageDT.getDefaultCodedValueColl();
                if (toValueColl != null) {
                    Iterator<?> iterator = toValueColl.iterator();
                    int i = 1;
                    while (iterator.hasNext()) {
                        String code = (String) iterator.next();
                        NbsCaseAnswerDto nbsAnswerDT = new NbsCaseAnswerDto();
                        nbsAnswerDT.setAnswerTxt(code);
                        nbsAnswerDT.setNbsQuestionUid(metaData.getNbsQuestionUid());
                        nbsAnswerDT.setSeqNbr(i++);
                        edxPHCRDocumentUtil.setStandardNBSCaseAnswerVals(publicHealthCaseContainer, nbsAnswerDT);
                        list.add(nbsAnswerDT);
                    }
                }
                answerMap.put(metaData.getQuestionIdentifier(), list);
            } else {
                String code = "";
                Collection<Object> toValueColl = edxRuleManageDT.getDefaultCodedValueColl();
                if (toValueColl != null) {
                    for (Object o : toValueColl) {
                        code = (String) o;
                    }
                }
                NbsCaseAnswerDto nbsAnswerDT = new NbsCaseAnswerDto();
                if (code != null)
                    nbsAnswerDT.setAnswerTxt(code);
                if (edxRuleManageDT.getDefaultNumericValue() != null)
                    nbsAnswerDT.setAnswerTxt(edxRuleManageDT.getDefaultNumericValue());
                if (edxRuleManageDT.getDefaultStringValue() != null)
                    nbsAnswerDT.setAnswerTxt(edxRuleManageDT.getDefaultStringValue());
                nbsAnswerDT.setNbsQuestionUid(metaData.getNbsQuestionUid());
                nbsAnswerDT.setSeqNbr(0);
                edxPHCRDocumentUtil.setStandardNBSCaseAnswerVals(publicHealthCaseContainer, nbsAnswerDT);
                answerMap.put(metaData.getQuestionIdentifier(), nbsAnswerDT);
            }
        }
        else
        {
            if (pamVO.getPamAnswerDTMap().get(metaData.getQuestionIdentifier()) == null) {
                Collection<Object> list = new ArrayList<>();
                if (metaData.getNbsUiComponentUid().compareTo(1013L) == 0) //NOSONAR
                {
                    Collection<Object> toValueColl = edxRuleManageDT.getDefaultCodedValueColl();
                    if (toValueColl != null) {
                        Iterator<?> iterator = toValueColl.iterator();
                        int i = 1;
                        while (iterator.hasNext()) {
                            String code = (String) iterator.next();
                            NbsCaseAnswerDto nbsAnswerDT = new NbsCaseAnswerDto();
                            nbsAnswerDT.setAnswerTxt(code);
                            nbsAnswerDT.setNbsQuestionUid(metaData.getNbsQuestionUid());
                            nbsAnswerDT.setSeqNbr(i++);
                            edxPHCRDocumentUtil.setStandardNBSCaseAnswerVals(publicHealthCaseContainer, nbsAnswerDT);
                            list.add(nbsAnswerDT);
                        }
                    }
                    answerMap.put(metaData.getQuestionIdentifier(), list);
                } else {
                    String code = "";
                    Collection<Object> toValueColl = edxRuleManageDT.getDefaultCodedValueColl();
                    if (toValueColl != null) {
                        for (Object o : toValueColl) {
                            code = (String) o;
                        }
                    }
                    NbsCaseAnswerDto nbsAnswerDT = new NbsCaseAnswerDto();
                    if (code != null)
                        nbsAnswerDT.setAnswerTxt(code);
                    if (edxRuleManageDT.getDefaultNumericValue() != null)
                        nbsAnswerDT.setAnswerTxt(edxRuleManageDT.getDefaultNumericValue());
                    if (edxRuleManageDT.getDefaultStringValue() != null)
                        nbsAnswerDT.setAnswerTxt(edxRuleManageDT.getDefaultStringValue());
                    nbsAnswerDT.setNbsQuestionUid(metaData.getNbsQuestionUid());
                    nbsAnswerDT.setSeqNbr(0);
                    edxPHCRDocumentUtil.setStandardNBSCaseAnswerVals(publicHealthCaseContainer, nbsAnswerDT);
                    answerMap.put(metaData.getQuestionIdentifier(), nbsAnswerDT);
                }
            }
        }
        pamVO.setPamAnswerDTMap(answerMap);
    }
    @SuppressWarnings({"java:S6541", "java:S3776"})
    public  void processConfirmationMethodCodeDT(EdxRuleManageDto edxRuleManageDT, PublicHealthCaseContainer publicHealthCaseContainer, NbsQuestionMetadata metaData) {
        String behavior = edxRuleManageDT.getBehavior();
        boolean isOverwrite = false;
        if (behavior.equalsIgnoreCase("1")) {
            isOverwrite = true;
        } else if (behavior.equalsIgnoreCase("2")) {
            isOverwrite = false; //NOSONAR
        }
        if (isOverwrite) {
            Collection<ConfirmationMethodDto> list = new ArrayList<>();
            if (metaData.getNbsUiComponentUid().compareTo(1013L) == 0) // NOSONAR
            {
                Collection<Object> toValueColl = edxRuleManageDT.getDefaultCodedValueColl();
                if (toValueColl != null) {
                    for (Object o : toValueColl) {
                        String code = (String) o;
                        ConfirmationMethodDto confirmDT = new ConfirmationMethodDto();
                        confirmDT.setConfirmationMethodCd(code);
                        confirmDT.setPublicHealthCaseUid(publicHealthCaseContainer.getThePublicHealthCaseDto().getPublicHealthCaseUid());
                        confirmDT.setItNew(true);


                        //check the previous time entered:
                        Collection<ConfirmationMethodDto> confirmColl = publicHealthCaseContainer.getTheConfirmationMethodDTCollection();
                        if (confirmColl != null) {
                            Iterator<ConfirmationMethodDto> cofirmIt = confirmColl.iterator();
                            Timestamp time;

                            while (cofirmIt.hasNext()) {
                                ConfirmationMethodDto confirmDTTime =  cofirmIt.next();
                                if (confirmDTTime.getConfirmationMethodTime() != null) {
                                    time = confirmDTTime.getConfirmationMethodTime();
                                    confirmDT.setConfirmationMethodTime(time);
                                    break;
                                }
                            }
                        }


                        list.add(confirmDT);
                    }
                }
                publicHealthCaseContainer.setTheConfirmationMethodDTCollection(list);
            }
        } else {
            if (publicHealthCaseContainer.getTheConfirmationMethodDTCollection() == null) {
                Collection<ConfirmationMethodDto> list = new ArrayList<>();
                if (metaData.getNbsUiComponentUid().compareTo(1013L) == 0) // NOSONAR
                {
                    Collection<Object> toValueColl = edxRuleManageDT.getDefaultCodedValueColl();
                    if (toValueColl != null) {
                        for (Object o : toValueColl) {
                            String code = (String) o;
                            ConfirmationMethodDto confirmDT = new ConfirmationMethodDto();
                            confirmDT.setConfirmationMethodCd(code);
                            confirmDT.setPublicHealthCaseUid(publicHealthCaseContainer.getThePublicHealthCaseDto().getPublicHealthCaseUid());
                            confirmDT.setItNew(true);
                            list.add(confirmDT);
                        }
                    }
                    publicHealthCaseContainer.setTheConfirmationMethodDTCollection(list);
                }
            } else {
                Collection<ConfirmationMethodDto> list = new ArrayList<>();
                Collection<Object> toValueColl = edxRuleManageDT.getDefaultCodedValueColl();
                if (toValueColl != null) {
                    Timestamp time = null;
                    for (Object o : toValueColl) {
                        String code = (String) o;
                        Collection<ConfirmationMethodDto> confirmColl = publicHealthCaseContainer.getTheConfirmationMethodDTCollection();
                        Iterator<ConfirmationMethodDto> cofirmIt = confirmColl.iterator();
                        boolean matchFound = false;
                        while (cofirmIt.hasNext()) {
                            ConfirmationMethodDto confirmDT =  cofirmIt.next();
                            if (confirmDT.getConfirmationMethodTime() != null)
                                time = confirmDT.getConfirmationMethodTime();
                            if (confirmDT.getConfirmationMethodCd() == null || confirmDT.getConfirmationMethodCd().trim().equals("")) {
                                break;
                            } else {
                                if (confirmDT.getConfirmationMethodCd().equals(code)) {
                                    break;
                                } else {
                                    list.add(confirmDT);
                                }
                            }
                            confirmDT.setConfirmationMethodCd(code);
                        }
                        if (!matchFound) {
                            ConfirmationMethodDto confirmDT = new ConfirmationMethodDto();
                            confirmDT.setConfirmationMethodCd(code);
                            confirmDT.setConfirmationMethodTime(time);
                            confirmDT.setPublicHealthCaseUid(publicHealthCaseContainer.getThePublicHealthCaseDto().getPublicHealthCaseUid());
                            confirmDT.setItNew(true);
                            list.add(confirmDT);
                        }
                    }
                    publicHealthCaseContainer.setTheConfirmationMethodDTCollection(list);
                }
            }
        }
    }

    @SuppressWarnings("java:S3776")
    public static void setMethod(Object nbsObject, Method setMethod, EdxRuleManageDto edxRuleManageDT) {
        try {
            Class<?>[] parameterArray = setMethod.getParameterTypes();
            for (Object object : parameterArray) {
                if (object.toString().equalsIgnoreCase("class java.math.BigDecimal")) {
                    if (edxRuleManageDT.getDefaultNumericValue() != null)
                        setMethod.invoke(nbsObject, new BigDecimal(edxRuleManageDT.getDefaultNumericValue()));
                    else
                        setMethod.invoke(nbsObject, new BigDecimal(edxRuleManageDT.getDefaultStringValue()));
                } else if (object.toString().equalsIgnoreCase("class java.lang.String")) {
                    if (edxRuleManageDT.getDefaultStringValue() != null && !edxRuleManageDT.getDefaultStringValue().trim().equals(""))
                        setMethod.invoke(nbsObject, edxRuleManageDT.getDefaultStringValue());
                    else if (edxRuleManageDT.getDefaultCommentValue() != null && !edxRuleManageDT.getDefaultCommentValue().trim().equals(""))
                        setMethod.invoke(nbsObject, edxRuleManageDT.getDefaultCommentValue());
                } else if (object.toString().equalsIgnoreCase("class java.sql.Timestamp")) {
                    setMethod.invoke(nbsObject, StringUtils.stringToStrutsTimestamp(edxRuleManageDT.getDefaultStringValue()));
                } else if (object.toString().equalsIgnoreCase("class java.lang.Integer")) {
                    if (edxRuleManageDT.getDefaultNumericValue() != null)
                        setMethod.invoke(nbsObject, edxRuleManageDT.getDefaultNumericValue());
                    else
                        setMethod.invoke(nbsObject, edxRuleManageDT.getDefaultStringValue());
                } else if (object.toString().equalsIgnoreCase("class java.lang.Long")) {
                    if (edxRuleManageDT.getDefaultNumericValue() != null)
                        setMethod.invoke(nbsObject, edxRuleManageDT.getDefaultNumericValue());
                    else
                        setMethod.invoke(nbsObject, edxRuleManageDT.getDefaultStringValue());
                }
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }
    @SuppressWarnings({"java:S3776", "java:S6541"})
    public PublicHealthCaseContainer processConfirmationMethodTimeDT(EdxRuleManageDto edxRuleManageDT, PublicHealthCaseContainer publicHealthCaseContainer, NbsQuestionMetadata metaData) throws DataProcessingException {
        String behavior = edxRuleManageDT.getBehavior();
        boolean isOverwrite = false;
        if (behavior.equalsIgnoreCase("1")) {
            isOverwrite = true;
        } else if (behavior.equalsIgnoreCase("2")) {
            isOverwrite = false; // NOSONAR
        }
        String time = edxRuleManageDT.getDefaultStringValue();
        //If the date selected is current date, the date is translated to MM/dd/yyyy
        if(time!=null && time.equalsIgnoreCase(NEDSSConstant.USE_CURRENT_DATE))
        {
            time= TimeStampUtil.convertTimestampToString(tz);
        }

        if (isOverwrite) {
            Collection<ConfirmationMethodDto> list = new ArrayList<>();
            if (time != null && publicHealthCaseContainer.getTheConfirmationMethodDTCollection() != null) {
                for (ConfirmationMethodDto confirmDT : publicHealthCaseContainer.getTheConfirmationMethodDTCollection()) {
                    confirmDT.setConfirmationMethodTime(TimeStampUtil.convertStringToTimestamp(time));
                    list.add(confirmDT);
                }
                publicHealthCaseContainer.setTheConfirmationMethodDTCollection(list);
            } else {
                ConfirmationMethodDto confirmDT = new ConfirmationMethodDto();
                if (time != null) {
                    confirmDT.setConfirmationMethodTime(TimeStampUtil.convertStringToTimestamp(time));
                }
                confirmDT.setPublicHealthCaseUid(publicHealthCaseContainer.getThePublicHealthCaseDto().getPublicHealthCaseUid());
                confirmDT.setItNew(true);


                //check previous code entered:
                Collection<ConfirmationMethodDto> confirmColl = publicHealthCaseContainer.getTheConfirmationMethodDTCollection();
                if(confirmColl!=null){
                    Iterator<ConfirmationMethodDto> cofirmIt = confirmColl.iterator();
                    String code;

                    while (cofirmIt.hasNext()) {
                        ConfirmationMethodDto confirmDTCode =  cofirmIt.next();
                        if (confirmDTCode.getConfirmationMethodCd() != null){
                            code = confirmDTCode.getConfirmationMethodCd();
                            confirmDT.setConfirmationMethodCd(code);
                            break;
                        }
                    }
                }


                list.add(confirmDT);
                publicHealthCaseContainer.setTheConfirmationMethodDTCollection(list);
            }

        } else {
            if (publicHealthCaseContainer.getTheConfirmationMethodDTCollection() == null) {
                Collection<ConfirmationMethodDto> list = new ArrayList<>();
                if (metaData.getNbsUiComponentUid().compareTo(1013L) == 0)  // NOSONAR
                {
                    ConfirmationMethodDto confirmDT = new ConfirmationMethodDto();
                    if (time != null) {
                        confirmDT.setConfirmationMethodTime(TimeStampUtil.convertStringToTimestamp(time));
                    }
                    confirmDT.setPublicHealthCaseUid(publicHealthCaseContainer.getThePublicHealthCaseDto().getPublicHealthCaseUid());
                    confirmDT.setItNew(true);
                    list.add(confirmDT);
                    publicHealthCaseContainer.setTheConfirmationMethodDTCollection(list);
                } else {
                    ConfirmationMethodDto confirmDT = new ConfirmationMethodDto();
                    if (time != null) {
                        confirmDT.setConfirmationMethodTime(TimeStampUtil.convertStringToTimestamp(time));
                    }
                    confirmDT.setPublicHealthCaseUid(publicHealthCaseContainer.getThePublicHealthCaseDto().getPublicHealthCaseUid());
                    confirmDT.setItNew(true);

                    list.add(confirmDT);
                    publicHealthCaseContainer.setTheConfirmationMethodDTCollection(list);
                }
            }
        }
        return publicHealthCaseContainer;
    }

    public void processNBSCaseManagementDT(EdxRuleManageDto edxRuleManageDT, PublicHealthCaseContainer publicHealthCaseContainer, NbsQuestionMetadata metaData){
        if (publicHealthCaseContainer.getTheCaseManagementDto() != null) {
            CaseManagementDto caseManagementDto = publicHealthCaseContainer.getTheCaseManagementDto();
            caseManagementDto.setCaseManagementDTPopulated(true);
            processNBSObjectDT( edxRuleManageDT, publicHealthCaseContainer, caseManagementDto, metaData);
        }
    }

    public void processConfirmationMethodCodeDTRequired(PublicHealthCaseContainer publicHealthCaseContainer){

        Collection<ConfirmationMethodDto> confirmColl = publicHealthCaseContainer.getTheConfirmationMethodDTCollection();

        if(confirmColl!=null){

            for (ConfirmationMethodDto confirmDT : confirmColl) {
                if (confirmDT.getConfirmationMethodCd() == null && confirmDT.getConfirmationMethodTime() != null) {
                    confirmDT.setConfirmationMethodCd("NA");
                    Collection<ConfirmationMethodDto> list = new ArrayList<>();
                    list.add(confirmDT);
                    publicHealthCaseContainer.setTheConfirmationMethodDTCollection(list);
                }

            }
        }
    }

    public void parseInvestigationDefaultValuesType(Map<Object, Object> map, InvestigationDefaultValuesType investigationDefaultValuesType) {
        List<DefaultValueType> defaultValueTypeArray = investigationDefaultValuesType.getDefaultValue();
        for (DefaultValueType defaultValueType : defaultValueTypeArray) {
            CodedType defaultQuestion = defaultValueType.getDefaultQuestion();

            EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
            edxRuleManageDT.setQuestionId(defaultQuestion.getCode());

            if (defaultValueType.getDefaultBehavior() != null) {
                CodedType defaultBehaviorType = defaultValueType.getDefaultBehavior();
                edxRuleManageDT.setBehavior(defaultBehaviorType.getCode());
            }

            List<CodedType> defaultCodedValueArray = defaultValueType.getDefaultCodedValue();
            if (defaultValueType.getDefaultParticipation() != null) {
                try {
                    edxRuleManageDT.setParticipationTypeCode(defaultValueType.getDefaultParticipation().getParticipationType().getCode());
                    edxRuleManageDT.setParticipationUid(Long.valueOf(defaultValueType.getDefaultParticipation().getEntityUid()));
                    edxRuleManageDT.setParticipationClassCode(defaultValueType.getDefaultParticipation().getEntityClass());
                } catch (Exception e) {
                    logger.error("The defaultValueType exception is not valid for code and/or uid and/or classCode. Please check: {}", defaultValueType); //NOSONAR
                }
            } else if (defaultValueType.getDefaultStringValue() != null) {
                edxRuleManageDT.setDefaultStringValue(defaultValueType.getDefaultStringValue());

            } else if (defaultValueType.getDefaultCommentValue() != null) {
                edxRuleManageDT.setDefaultCommentValue(defaultValueType.getDefaultCommentValue());
            } else if (defaultCodedValueArray != null) {
                Collection<Object> toValueColl = new ArrayList<>();
                for (CodedType codedType : defaultCodedValueArray) {
                    toValueColl.add(codedType.getCode());
                }
                edxRuleManageDT.setDefaultCodedValueColl(toValueColl);
            } else if (defaultValueType.getDefaultNumericValue() != null) {
                edxRuleManageDT.setDefaultNumericValue(String.valueOf(defaultValueType.getDefaultNumericValue().getValue1()));
            }
            map.put(edxRuleManageDT.getQuestionId(), edxRuleManageDT);
        }
    }


    @SuppressWarnings({"java:S3776", "java:S1871"})
    public void processActIds(EdxRuleManageDto edxRuleManageDT,
                              PublicHealthCaseContainer publicHealthCaseContainer, NbsQuestionMetadata metaData) {
        String behavior = edxRuleManageDT.getBehavior();
        boolean isOverwrite = behavior.equalsIgnoreCase("1"); // NOSONAR
        Collection<ActIdDto> actIdColl = publicHealthCaseContainer
                .getTheActIdDTCollection();
        if (actIdColl != null && !actIdColl.isEmpty()) {
            Iterator<ActIdDto> ite = actIdColl.iterator();
            ActIdDto actIdDT =  ite.next();
            if (actIdDT.getTypeCd() != null
                    && actIdDT.getTypeCd().equalsIgnoreCase(
                    NEDSSConstant.ACT_ID_STATE_TYPE_CD)
                    && metaData.getDataCd() != null
                    && metaData.getDataCd().equalsIgnoreCase(
                    NEDSSConstant.ACT_ID_STATE_TYPE_CD)) {
                if (isOverwrite)
                    actIdDT.setRootExtensionTxt(edxRuleManageDT
                            .getDefaultStringValue());
                else if (actIdDT.getRootExtensionTxt() == null)
                    actIdDT.setRootExtensionTxt(edxRuleManageDT
                            .getDefaultStringValue());
            } else if (actIdDT.getTypeCd() != null
                    && actIdDT.getTypeCd().equalsIgnoreCase("CITY")
                    && metaData.getDataCd() != null
                    && metaData.getDataCd().equalsIgnoreCase("CITY")) {
                if (isOverwrite)
                    actIdDT.setRootExtensionTxt(edxRuleManageDT
                            .getDefaultStringValue());
                else if (actIdDT.getRootExtensionTxt() == null)
                    actIdDT.setRootExtensionTxt(edxRuleManageDT
                            .getDefaultStringValue());
            }

        }
    }


    protected void getCurrentDateValue(EdxRuleManageDto edxRuleManageDT) {
        if (edxRuleManageDT.getDefaultStringValue() != null
                && edxRuleManageDT.getDefaultStringValue().equals(
                NEDSSConstant.USE_CURRENT_DATE))
        {
            edxRuleManageDT.setDefaultStringValue(StringUtils
                    .formatDate(TimeStampUtil.getCurrentTimeStamp(tz)));
        }
    }
}
