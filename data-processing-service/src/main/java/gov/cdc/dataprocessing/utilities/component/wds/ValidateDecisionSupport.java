package gov.cdc.dataprocessing.utilities.component.wds;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.model.container.model.PublicHealthCaseContainer;
import gov.cdc.dataprocessing.model.dto.phc.CaseManagementDto;
import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import gov.cdc.dataprocessing.model.container.base.BasePamContainer;
import gov.cdc.dataprocessing.model.dsma_algorithm.CodedType;
import gov.cdc.dataprocessing.model.dsma_algorithm.DefaultValueType;
import gov.cdc.dataprocessing.model.dsma_algorithm.InvestigationDefaultValuesType;
import gov.cdc.dataprocessing.model.dto.phc.ConfirmationMethodDto;
import gov.cdc.dataprocessing.model.dto.edx.EdxRuleManageDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsCaseAnswerDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsQuestionMetadata;
import gov.cdc.dataprocessing.model.dto.act.ActIdDto;
import gov.cdc.dataprocessing.utilities.StringUtils;
import gov.cdc.dataprocessing.utilities.component.edx.EdxPhcrDocumentUtil;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class ValidateDecisionSupport {
    private final EdxPhcrDocumentUtil edxPHCRDocumentUtil;

    public ValidateDecisionSupport(EdxPhcrDocumentUtil edxPHCRDocumentUtil) {
        this.edxPHCRDocumentUtil = edxPHCRDocumentUtil;
    }

    public void processNbsObject(EdxRuleManageDto edxRuleManageDT, PublicHealthCaseContainer publicHealthCaseContainer, NbsQuestionMetadata metaData){
        PublicHealthCaseDto publicHealthCaseDto = publicHealthCaseContainer.getThePublicHealthCaseDto();
        processNBSObjectDT( edxRuleManageDT, publicHealthCaseContainer, publicHealthCaseDto, metaData);
    }

    public void processNBSObjectDT(EdxRuleManageDto edxRuleManageDT, PublicHealthCaseContainer publicHealthCaseContainer, Object object, NbsQuestionMetadata metaData) {
        String behavior = edxRuleManageDT.getBehavior();
        boolean isOverwrite = false;

        if (behavior.equalsIgnoreCase("1")) {
            isOverwrite = true;
        } else if (behavior.equalsIgnoreCase("2")) {
            isOverwrite = false;
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
                        setMethod = phcClass.getMethod(setMethodName, new String().getClass());

                    } else if (metaData.getDataType().equalsIgnoreCase(NEDSSConstant.NBS_QUESTION_DATATYPE_DATETIME) || metaData.getDataType().equalsIgnoreCase(NEDSSConstant.DATETIME_DATATYPE)
                            || metaData.getDataType().equalsIgnoreCase(NEDSSConstant.NBS_QUESTION_DATATYPE_DATE)) {
                        getCurrentDateValue(edxRuleManageDT);
                        setMethod = phcClass.getMethod(setMethodName, new Timestamp(0).getClass());
                    } else if (metaData.getDataType().equalsIgnoreCase(NEDSSConstant.NBS_QUESTION_DATATYPE_NUMERIC)) {
                        if (value.getReturnType().equals(Integer.class))
                            setMethod = phcClass.getMethod(setMethodName, Integer.valueOf(0).getClass());
                        else if (value.getReturnType().equals(Long.class))
                            setMethod = phcClass.getMethod(setMethodName, Long.valueOf(0).getClass());
                        else if (value.getReturnType().equals(BigDecimal.class))
                            setMethod = phcClass.getMethod(setMethodName, BigDecimal.valueOf(0).getClass());
                        else if (value.getReturnType().equals(String.class)) // Added because question INV139's datatype is NUMERIC in nbs_ui_metadata table but the datatype is varchar in Public_Health_Case table.
                            setMethod = phcClass.getMethod(setMethodName, String.class);
                    } else {
//                        logger.error("ValidateDecisionSupport.processNbsObject: There is an error, there seems to be metaData.getDataType() that is dufferent from the expected value" + metaData.toString());
                    }
                    Object ob = value.invoke(object, (Object[]) null);
                    if (isOverwrite) {
                        setMethod(object, setMethod, edxRuleManageDT);
                    } else if (!isOverwrite && ob == null) {
                        setMethod(object, setMethod, edxRuleManageDT);
                    } else {
                        // do nothing
                    }
                } else {
                    // do nothing
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } 
        // System.out.println("solution is :"+publicHealthCaseDT.toString());



    }


    public void processNBSCaseAnswerDT(EdxRuleManageDto edxRuleManageDT, PublicHealthCaseContainer publicHealthCaseContainer, BasePamContainer pamVO, NbsQuestionMetadata metaData) {
        String behavior = edxRuleManageDT.getBehavior();
        boolean isOverwrite = false;
        if (behavior.equalsIgnoreCase("1")) {
            isOverwrite = true;
        } else if (behavior.equalsIgnoreCase("2")) {
            isOverwrite = false;
        }
        String value = edxRuleManageDT.getDefaultStringValue();
        if(value!=null && value.equalsIgnoreCase(NEDSSConstant.USE_CURRENT_DATE))
            value=new SimpleDateFormat("MM/dd/yyyy").format(Calendar.getInstance().getTime());
        edxRuleManageDT.setDefaultStringValue(value);
        Map<Object,Object> answerMap = pamVO.getPamAnswerDTMap();
        if (isOverwrite) {
            Collection<Object> list = new ArrayList<>();
            if (metaData.getNbsUiComponentUid().compareTo(1013L) == 0) {
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
        } else {
            if (pamVO.getPamAnswerDTMap().get(metaData.getQuestionIdentifier()) == null) {
                Collection<Object> list = new ArrayList<>();
                if (metaData.getNbsUiComponentUid().compareTo(1013L) == 0) {
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
            } else {
//                logger.debug("pageActProxyVO.getPageVO().getPamAnswerDTMap().get(metaData.getQuestionIdentifier())!=null for  metaData.getQuestionIdentifier():-" + metaData.getQuestionIdentifier());
//                logger.error(edxRuleManageDT.toString());
            }
        }
        pamVO.setPamAnswerDTMap(answerMap);
    }

    public  void processConfirmationMethodCodeDT(EdxRuleManageDto edxRuleManageDT, PublicHealthCaseContainer publicHealthCaseContainer, NbsQuestionMetadata metaData) {
        String behavior = edxRuleManageDT.getBehavior();
        boolean isOverwrite = false;
        if (behavior.equalsIgnoreCase("1")) {
            isOverwrite = true;
        } else if (behavior.equalsIgnoreCase("2")) {
            isOverwrite = false;
        }
        if (isOverwrite) {
            Collection<ConfirmationMethodDto> list = new ArrayList<>();
            if (metaData.getNbsUiComponentUid().compareTo(1013L) == 0) {
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
                if (metaData.getNbsUiComponentUid().compareTo(1013L) == 0) {
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
                } else {
//                    logger.error("This should not happen! There is some critical error in the metadata! Please check." + metaData.toString());
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
            e.printStackTrace();
        }
    }

    public PublicHealthCaseContainer processConfirmationMethodTimeDT(EdxRuleManageDto edxRuleManageDT, PublicHealthCaseContainer publicHealthCaseContainer, NbsQuestionMetadata metaData) {
        String behavior = edxRuleManageDT.getBehavior();
        boolean isOverwrite = false;
        if (behavior.equalsIgnoreCase("1")) {
            isOverwrite = true;
        } else if (behavior.equalsIgnoreCase("2")) {
            isOverwrite = false;
        }
        String time = edxRuleManageDT.getDefaultStringValue();

        //If the date selected is current date, the date is translated to MM/dd/yyyy
        if(time!=null && time.equalsIgnoreCase(NEDSSConstant.USE_CURRENT_DATE))
            time=new SimpleDateFormat("MM/dd/yyyy").format(Calendar.getInstance().getTime());

        if (isOverwrite) {
            Collection<ConfirmationMethodDto> list = new ArrayList<>();
            if (time != null && publicHealthCaseContainer.getTheConfirmationMethodDTCollection() != null) {
                for (ConfirmationMethodDto confirmDT : publicHealthCaseContainer.getTheConfirmationMethodDTCollection()) {
                    confirmDT.setConfirmationMethodTime(Timestamp.valueOf(time));
                    list.add(confirmDT);
                }
                publicHealthCaseContainer.setTheConfirmationMethodDTCollection(list);
            } else {
                ConfirmationMethodDto confirmDT = new ConfirmationMethodDto();
                if (time != null) {
                    confirmDT.setConfirmationMethodTime(Timestamp.valueOf(time));
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
            boolean loopbreak = false;
            if (publicHealthCaseContainer.getTheConfirmationMethodDTCollection() == null) {
                Collection<ConfirmationMethodDto> list = new ArrayList<>();
                if (metaData.getNbsUiComponentUid().compareTo(1013L) == 0) {
                    ConfirmationMethodDto confirmDT = new ConfirmationMethodDto();
                    if (time != null) {
                        confirmDT.setConfirmationMethodTime(Timestamp.valueOf(time));
                    }
                    confirmDT.setPublicHealthCaseUid(publicHealthCaseContainer.getThePublicHealthCaseDto().getPublicHealthCaseUid());
                    confirmDT.setItNew(true);
                    list.add(confirmDT);
                    publicHealthCaseContainer.setTheConfirmationMethodDTCollection(list);
                } else {

                    if(publicHealthCaseContainer.getTheConfirmationMethodDTCollection()!=null){
                        for (ConfirmationMethodDto confirmDT : publicHealthCaseContainer.getTheConfirmationMethodDTCollection()) {
                            if (confirmDT.getConfirmationMethodTime() != null) {
                                loopbreak = true;
                                break;
                            }
                            if (time != null) {
                                confirmDT.setConfirmationMethodTime(Timestamp.valueOf(time));
                            }
                            list.add(confirmDT);
                        }
                        if (!loopbreak)
                            publicHealthCaseContainer.setTheConfirmationMethodDTCollection(list);

                    }
                    else{//if the getTheConfirmationMethodDtoCollection == null, overwrite

                        ConfirmationMethodDto confirmDT = new ConfirmationMethodDto();
                        if (time != null) {
                            confirmDT.setConfirmationMethodTime(Timestamp.valueOf(time));
                        }
                        confirmDT.setPublicHealthCaseUid(publicHealthCaseContainer.getThePublicHealthCaseDto().getPublicHealthCaseUid());
                        confirmDT.setItNew(true);

                        list.add(confirmDT);
                        publicHealthCaseContainer.setTheConfirmationMethodDTCollection(list);
                    }
                }
            } else {
//                logger.debug("publicHealthCaseContainer().getTheConfirmationMethodDtoCollection()!=null for  metaData.getQuestionIdentifier():-" + metaData.getQuestionIdentifier());
//                logger.error(edxRuleManageDT.toString());
            }
        }
        return publicHealthCaseContainer;
    }

    public void processNBSCaseManagementDT(EdxRuleManageDto edxRuleManageDT, PublicHealthCaseContainer publicHealthCaseContainer, NbsQuestionMetadata metaData){
        if (publicHealthCaseContainer.getTheCaseManagementDto() != null) {
            CaseManagementDto caseManagementDto = publicHealthCaseContainer.getTheCaseManagementDto();
            caseManagementDto.setCaseManagementDTPopulated(true);
            processNBSObjectDT( edxRuleManageDT, publicHealthCaseContainer, caseManagementDto, metaData);
        } else{
//            logger.error("********Decision Support Setting Case Management value for non-STD/HIV Case?? Check STD_PROGRAM_AREAS setting in Property file*********");
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
//                    logger.error("The defaultValueType exception is not valid for code and/or uid and/or classCode. Please check: ", defaultValueType);
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
                Method method = item;
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

                    } else
                        return false;

                } else {
                    // return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return false;
    }


    public void processActIds(EdxRuleManageDto edxRuleManageDT,
                              PublicHealthCaseContainer publicHealthCaseContainer, NbsQuestionMetadata metaData) {
        String behavior = edxRuleManageDT.getBehavior();
        boolean isOverwrite = false;
        if (behavior.equalsIgnoreCase("1")) {
            isOverwrite = true;
        } else if (behavior.equalsIgnoreCase("2")) {
            isOverwrite = false;
        }
        Collection<ActIdDto> actIdColl = publicHealthCaseContainer
                .getTheActIdDTCollection();
        if (actIdColl != null && actIdColl.size() > 0) {
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
                else if (!isOverwrite && actIdDT.getRootExtensionTxt() == null)
                    actIdDT.setRootExtensionTxt(edxRuleManageDT
                            .getDefaultStringValue());
            } else if (actIdDT.getTypeCd() != null
                    && actIdDT.getTypeCd().equalsIgnoreCase("CITY")
                    && metaData.getDataCd() != null
                    && metaData.getDataCd().equalsIgnoreCase("CITY")) {
                if (isOverwrite)
                    actIdDT.setRootExtensionTxt(edxRuleManageDT
                            .getDefaultStringValue());
                else if (!isOverwrite && actIdDT.getRootExtensionTxt() == null)
                    actIdDT.setRootExtensionTxt(edxRuleManageDT
                            .getDefaultStringValue());
            }

        }
    }


    private void getCurrentDateValue(EdxRuleManageDto edxRuleManageDT) {
        if (edxRuleManageDT.getDefaultStringValue() != null
                && edxRuleManageDT.getDefaultStringValue().equals(
                NEDSSConstant.USE_CURRENT_DATE))
            edxRuleManageDT.setDefaultStringValue(StringUtils
                    .formatDate(new Timestamp((new Date()).getTime())));
    }
}
