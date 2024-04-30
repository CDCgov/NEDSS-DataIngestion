package gov.cdc.dataprocessing.utilities.component;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.CaseManagementDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.PublicHealthCaseDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.PublicHealthCaseVO;
import gov.cdc.dataprocessing.model.container.BasePamContainer;
import gov.cdc.dataprocessing.model.dsma_algorithm.CodedType;
import gov.cdc.dataprocessing.model.dsma_algorithm.DefaultValueType;
import gov.cdc.dataprocessing.model.dsma_algorithm.InvestigationDefaultValuesType;
import gov.cdc.dataprocessing.model.dto.ConfirmationMethodDto;
import gov.cdc.dataprocessing.model.dto.EdxRuleManageDto;
import gov.cdc.dataprocessing.model.dto.NbsCaseAnswerDto;
import gov.cdc.dataprocessing.model.dto.NbsQuestionMetadata;
import gov.cdc.dataprocessing.model.dto.act.ActIdDto;
import gov.cdc.dataprocessing.utilities.StringUtils;
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

    public void processNbsObject(EdxRuleManageDto edxRuleManageDT, PublicHealthCaseVO publicHealthCaseVO, NbsQuestionMetadata metaData){
        PublicHealthCaseDT publicHealthCaseDT = publicHealthCaseVO.getThePublicHealthCaseDT();
        processNBSObjectDT( edxRuleManageDT, publicHealthCaseVO, publicHealthCaseDT, metaData);
    }

    public void processNBSObjectDT(EdxRuleManageDto edxRuleManageDT, PublicHealthCaseVO publicHealthCaseVO, Object object, NbsQuestionMetadata metaData) {
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
            for (int i = 0; i < methodList.length; i++) {
                Method method = (Method) methodList[i];
                if (method.getName().equalsIgnoreCase(getMethodName)) {
                    String setMethodName = method.getName().replaceAll("get", "set");

                    Method setMethod = null;
                    if (metaData.getDataType().equalsIgnoreCase(NEDSSConstant.NBS_QUESTION_DATATYPE_TEXT) || metaData.getDataType().equalsIgnoreCase(NEDSSConstant.NBS_QUESTION_DATATYPE_CODED_VALUE)) {
                        setMethod = phcClass.getMethod(setMethodName, new String().getClass());

                    } else if (metaData.getDataType().equalsIgnoreCase(NEDSSConstant.NBS_QUESTION_DATATYPE_DATETIME) || metaData.getDataType().equalsIgnoreCase(NEDSSConstant.DATETIME_DATATYPE)
                            || metaData.getDataType().equalsIgnoreCase(NEDSSConstant.NBS_QUESTION_DATATYPE_DATE)) {
                        getCurrentDateValue(edxRuleManageDT);
                        setMethod = phcClass.getMethod(setMethodName, new Timestamp(0).getClass());
                    } else if (metaData.getDataType().equalsIgnoreCase(NEDSSConstant.NBS_QUESTION_DATATYPE_NUMERIC)) {
                        if(method.getReturnType().equals(Integer.class))
                            setMethod = phcClass.getMethod(setMethodName, Integer.valueOf(0).getClass());
                        else if(method.getReturnType().equals(Long.class))
                            setMethod = phcClass.getMethod(setMethodName, Long.valueOf(0).getClass());
                        else if(method.getReturnType().equals(BigDecimal.class))
                            setMethod = phcClass.getMethod(setMethodName, BigDecimal.valueOf(0).getClass());
                        else if(method.getReturnType().equals(String.class)) // Added because question INV139's datatype is NUMERIC in nbs_ui_metadata table but the datatype is varchar in Public_Health_Case table.
                            setMethod = phcClass.getMethod(setMethodName, String.class);
                    } else {
//                        logger.error("ValidateDecisionSupport.processNbsObject: There is an error, there seems to be metaData.getDataType() that is dufferent from the expected value" + metaData.toString());
                    }
                    Object ob = method.invoke(object, (Object[]) null);
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


    public void processNBSCaseAnswerDT(EdxRuleManageDto edxRuleManageDT, PublicHealthCaseVO publicHealthCaseVO, BasePamContainer pamVO, NbsQuestionMetadata metaData) {
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
            Collection<Object> list = new ArrayList<Object>();
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
                        edxPHCRDocumentUtil.setStandardNBSCaseAnswerVals(publicHealthCaseVO, nbsAnswerDT);
                        list.add(nbsAnswerDT);
                    }
                }
                answerMap.put(metaData.getQuestionIdentifier(), list);
            } else {
                String code = "";
                Collection<Object> toValueColl = edxRuleManageDT.getDefaultCodedValueColl();
                if (toValueColl != null) {
                    Iterator<?> iterator = toValueColl.iterator();
                    while (iterator.hasNext()) {
                        code = (String) iterator.next();
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
                edxPHCRDocumentUtil.setStandardNBSCaseAnswerVals(publicHealthCaseVO, nbsAnswerDT);
                answerMap.put(metaData.getQuestionIdentifier(), nbsAnswerDT);
            }
        } else {
            if (pamVO.getPamAnswerDTMap().get(metaData.getQuestionIdentifier()) == null) {
                Collection<Object> list = new ArrayList<Object>();
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
                            edxPHCRDocumentUtil.setStandardNBSCaseAnswerVals(publicHealthCaseVO, nbsAnswerDT);
                            list.add(nbsAnswerDT);
                        }
                    }
                    answerMap.put(metaData.getQuestionIdentifier(), list);
                } else {
                    String code = "";
                    Collection<Object> toValueColl = edxRuleManageDT.getDefaultCodedValueColl();
                    if (toValueColl != null) {
                        Iterator<?> iterator = toValueColl.iterator();
                        while (iterator.hasNext()) {
                            code = (String) iterator.next();
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
                    edxPHCRDocumentUtil.setStandardNBSCaseAnswerVals(publicHealthCaseVO, nbsAnswerDT);
                    answerMap.put(metaData.getQuestionIdentifier(), nbsAnswerDT);
                }
            } else {
//                logger.debug("pageActProxyVO.getPageVO().getPamAnswerDTMap().get(metaData.getQuestionIdentifier())!=null for  metaData.getQuestionIdentifier():-" + metaData.getQuestionIdentifier());
//                logger.error(edxRuleManageDT.toString());
            }
        }
        pamVO.setPamAnswerDTMap(answerMap);
    }

    public  void processConfirmationMethodCodeDT(EdxRuleManageDto edxRuleManageDT, PublicHealthCaseVO publicHealthCaseVO, NbsQuestionMetadata metaData) {
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
                    Iterator<?> iterator = toValueColl.iterator();
                    while (iterator.hasNext()) {
                        String code = (String) iterator.next();
                        ConfirmationMethodDto confirmDT = new ConfirmationMethodDto();
                        confirmDT.setConfirmationMethodCd(code);
                        confirmDT.setPublicHealthCaseUid(publicHealthCaseVO.getThePublicHealthCaseDT().getPublicHealthCaseUid());
                        confirmDT.setItNew(true);



                        //check the previous time entered:
                        Collection<ConfirmationMethodDto> confirmColl = publicHealthCaseVO.getTheConfirmationMethodDTCollection();
                        if(confirmColl!=null){
                            Iterator<ConfirmationMethodDto> cofirmIt = confirmColl.iterator();
                            Timestamp time = null;

                            while (cofirmIt.hasNext()) {
                                ConfirmationMethodDto confirmDTTime = (ConfirmationMethodDto) cofirmIt.next();
                                if (confirmDTTime.getConfirmationMethodTime() != null){
                                    time = confirmDTTime.getConfirmationMethodTime();
                                    confirmDT.setConfirmationMethodTime(time);
                                    break;
                                }
                            }
                        }


                        list.add(confirmDT);
                    }
                }
                publicHealthCaseVO.setTheConfirmationMethodDTCollection(list);
            }
        } else {
            if (publicHealthCaseVO.getTheConfirmationMethodDTCollection() == null) {
                Collection<ConfirmationMethodDto> list = new ArrayList<>();
                if (metaData.getNbsUiComponentUid().compareTo(1013L) == 0) {
                    Collection<Object> toValueColl = edxRuleManageDT.getDefaultCodedValueColl();
                    if (toValueColl != null) {
                        Iterator<?> iterator = toValueColl.iterator();
                        while (iterator.hasNext()) {
                            String code = (String) iterator.next();
                            ConfirmationMethodDto confirmDT = new ConfirmationMethodDto();
                            confirmDT.setConfirmationMethodCd(code);
                            confirmDT.setPublicHealthCaseUid(publicHealthCaseVO.getThePublicHealthCaseDT().getPublicHealthCaseUid());
                            confirmDT.setItNew(true);
                            list.add(confirmDT);
                        }
                    }
                    publicHealthCaseVO.setTheConfirmationMethodDTCollection(list);
                } else {
//                    logger.error("This should not happen! There is some critical error in the metadata! Please check." + metaData.toString());
                }
            } else {
                Collection<ConfirmationMethodDto> list = new ArrayList<>();
                Collection<Object> toValueColl = edxRuleManageDT.getDefaultCodedValueColl();
                if (toValueColl != null) {
                    Timestamp time = null;
                    Iterator<?> iterator = toValueColl.iterator();
                    while (iterator.hasNext()) {
                        String code = (String) iterator.next();
                        Collection<ConfirmationMethodDto> confirmColl = publicHealthCaseVO.getTheConfirmationMethodDTCollection();
                        Iterator<ConfirmationMethodDto> cofirmIt = confirmColl.iterator();
                        boolean matchFound = false;
                        while (cofirmIt.hasNext()) {
                            ConfirmationMethodDto confirmDT = (ConfirmationMethodDto) cofirmIt.next();
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
                            confirmDT.setPublicHealthCaseUid(publicHealthCaseVO.getThePublicHealthCaseDT().getPublicHealthCaseUid());
                            confirmDT.setItNew(true);
                            list.add(confirmDT);
                        }
                    }
                    publicHealthCaseVO.setTheConfirmationMethodDTCollection(list);
                }
            }
        }
    }


    public static void setMethod(Object nbsObject, Method setMethod, EdxRuleManageDto edxRuleManageDT) {
        try {
            Class<?>[] parameterArray = setMethod.getParameterTypes();
            for (int j = 0; j < parameterArray.length; j++) {
                Object object = parameterArray[j];
                if (object.toString().equalsIgnoreCase("class java.math.BigDecimal")) {
                    if(edxRuleManageDT.getDefaultNumericValue()!=null)
                        setMethod.invoke(nbsObject, new BigDecimal(edxRuleManageDT.getDefaultNumericValue()));
                    else
                        setMethod.invoke(nbsObject, new BigDecimal(edxRuleManageDT.getDefaultStringValue()));
                } else if (object.toString().equalsIgnoreCase("class java.lang.String")) {
                    if(edxRuleManageDT.getDefaultStringValue()!=null && !edxRuleManageDT.getDefaultStringValue().trim().equals(""))
                        setMethod.invoke(nbsObject, edxRuleManageDT.getDefaultStringValue());
                    else if(edxRuleManageDT.getDefaultCommentValue()!=null && !edxRuleManageDT.getDefaultCommentValue().trim().equals(""))
                        setMethod.invoke(nbsObject, edxRuleManageDT.getDefaultCommentValue());
                } else if (object.toString().equalsIgnoreCase("class java.sql.Timestamp")) {
                    setMethod.invoke(nbsObject, StringUtils.stringToStrutsTimestamp(edxRuleManageDT.getDefaultStringValue()));
                } else if (object.toString().equalsIgnoreCase("class java.lang.Integer")) {
                    if(edxRuleManageDT.getDefaultNumericValue()!=null)
                        setMethod.invoke(nbsObject, edxRuleManageDT.getDefaultNumericValue());
                    else
                        setMethod.invoke(nbsObject, edxRuleManageDT.getDefaultStringValue());
                } else if (object.toString().equalsIgnoreCase("class java.lang.Long")) {
                    if(edxRuleManageDT.getDefaultNumericValue()!=null)
                        setMethod.invoke(nbsObject, edxRuleManageDT.getDefaultNumericValue());
                    else
                        setMethod.invoke(nbsObject, edxRuleManageDT.getDefaultStringValue());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PublicHealthCaseVO processConfirmationMethodTimeDT(EdxRuleManageDto edxRuleManageDT, PublicHealthCaseVO publicHealthCaseVO, NbsQuestionMetadata metaData) {
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
            if (time != null && publicHealthCaseVO.getTheConfirmationMethodDTCollection() != null) {
                Iterator<?> iterator = publicHealthCaseVO.getTheConfirmationMethodDTCollection().iterator();
                while (iterator.hasNext()) {
                    ConfirmationMethodDto confirmDT = (ConfirmationMethodDto) iterator.next();
                    confirmDT.setConfirmationMethodTime(Timestamp.valueOf(time));
                    list.add(confirmDT);
                }
                publicHealthCaseVO.setTheConfirmationMethodDTCollection(list);
            } else {
                ConfirmationMethodDto confirmDT = new ConfirmationMethodDto();
                confirmDT.setConfirmationMethodTime(Timestamp.valueOf(time));
                confirmDT.setPublicHealthCaseUid(publicHealthCaseVO.getThePublicHealthCaseDT().getPublicHealthCaseUid());
                confirmDT.setItNew(true);


                //check previous code entered:
                Collection<ConfirmationMethodDto> confirmColl = publicHealthCaseVO.getTheConfirmationMethodDTCollection();
                if(confirmColl!=null){
                    Iterator<ConfirmationMethodDto> cofirmIt = confirmColl.iterator();
                    String code;

                    while (cofirmIt.hasNext()) {
                        ConfirmationMethodDto confirmDTCode = (ConfirmationMethodDto) cofirmIt.next();
                        if (confirmDTCode.getConfirmationMethodCd() != null){
                            code = confirmDTCode.getConfirmationMethodCd();
                            confirmDT.setConfirmationMethodCd(code);
                            break;
                        }
                    }
                }


                list.add(confirmDT);
                publicHealthCaseVO.setTheConfirmationMethodDTCollection(list);
            }

        } else {
            boolean loopbreak = false;
            if (publicHealthCaseVO.getTheConfirmationMethodDTCollection() == null) {
                Collection<ConfirmationMethodDto> list = new ArrayList<>();
                if (metaData.getNbsUiComponentUid().compareTo(1013L) == 0) {
                    ConfirmationMethodDto confirmDT = new ConfirmationMethodDto();
                    confirmDT.setConfirmationMethodTime(Timestamp.valueOf(time));
                    confirmDT.setPublicHealthCaseUid(publicHealthCaseVO.getThePublicHealthCaseDT().getPublicHealthCaseUid());
                    confirmDT.setItNew(true);
                    list.add(confirmDT);
                    publicHealthCaseVO.setTheConfirmationMethodDTCollection(list);
                } else {

                    if(publicHealthCaseVO.getTheConfirmationMethodDTCollection()!=null){
                        Iterator<?> iterator = publicHealthCaseVO.getTheConfirmationMethodDTCollection().iterator();
                        while (iterator.hasNext()) {
                            ConfirmationMethodDto confirmDT = (ConfirmationMethodDto) iterator.next();
                            if (confirmDT.getConfirmationMethodTime() != null) {
                                loopbreak = true;
                                break;
                            }
                            confirmDT.setConfirmationMethodTime(Timestamp.valueOf(time));
                            list.add(confirmDT);
                        }
                        if (!loopbreak)
                            publicHealthCaseVO.setTheConfirmationMethodDTCollection(list);

                    }
                    else{//if the getTheConfirmationMethodDtoCollection == null, overwrite

                        ConfirmationMethodDto confirmDT = new ConfirmationMethodDto();
                        confirmDT.setConfirmationMethodTime(Timestamp.valueOf(time));
                        confirmDT.setPublicHealthCaseUid(publicHealthCaseVO.getThePublicHealthCaseDT().getPublicHealthCaseUid());
                        confirmDT.setItNew(true);

                        list.add(confirmDT);
                        publicHealthCaseVO.setTheConfirmationMethodDTCollection(list);
                    }
                }
            } else {
//                logger.debug("publicHealthCaseVO().getTheConfirmationMethodDtoCollection()!=null for  metaData.getQuestionIdentifier():-" + metaData.getQuestionIdentifier());
//                logger.error(edxRuleManageDT.toString());
            }
        }
        return publicHealthCaseVO;
    }

    public void processNBSCaseManagementDT(EdxRuleManageDto edxRuleManageDT, PublicHealthCaseVO publicHealthCaseVO, NbsQuestionMetadata metaData){
        if (publicHealthCaseVO.getTheCaseManagementDT() != null) {
            CaseManagementDT caseManagementDT = publicHealthCaseVO.getTheCaseManagementDT();
            caseManagementDT.setCaseManagementDTPopulated(true);
            processNBSObjectDT( edxRuleManageDT, publicHealthCaseVO, caseManagementDT, metaData);
        } else{
//            logger.error("********Decision Support Setting Case Management value for non-STD/HIV Case?? Check STD_PROGRAM_AREAS setting in Property file*********");
        }
    }

    public void processConfirmationMethodCodeDTRequired(PublicHealthCaseVO publicHealthCaseVO){

        Collection<ConfirmationMethodDto> confirmColl = publicHealthCaseVO.getTheConfirmationMethodDTCollection();

        if(confirmColl!=null){
            Iterator<ConfirmationMethodDto> cofirmIt = confirmColl.iterator();

            while (cofirmIt.hasNext()) {
                ConfirmationMethodDto confirmDT = (ConfirmationMethodDto) cofirmIt.next();
                if (confirmDT.getConfirmationMethodCd() == null && confirmDT.getConfirmationMethodTime()!=null){
                    confirmDT.setConfirmationMethodCd("NA");
                    Collection<ConfirmationMethodDto> list = new ArrayList<>();
                    list.add(confirmDT);
                    publicHealthCaseVO.setTheConfirmationMethodDTCollection(list);
                }

            }
        }
    }

    public void parseInvestigationDefaultValuesType(Map<Object, Object> map, InvestigationDefaultValuesType investigationDefaultValuesType) {
        List<DefaultValueType> defaultValueTypeArray = investigationDefaultValuesType.getDefaultValue();
        for (int i = 0; i < defaultValueTypeArray.size(); i++) {
            DefaultValueType defaultValueType = defaultValueTypeArray.get(i);
            CodedType defaultQuestion = defaultValueType.getDefaultQuestion();

            EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
            edxRuleManageDT.setQuestionId(defaultQuestion.getCode());

            if (defaultValueType.getDefaultBehavior() != null) {
                CodedType defaultBehaviorType = defaultValueType.getDefaultBehavior();
                edxRuleManageDT.setBehavior(defaultBehaviorType.getCode());
            }

            List<CodedType> defaultCodedValueArray = defaultValueType.getDefaultCodedValue();
            if(defaultValueType.getDefaultParticipation()!=null){
                try{
                    edxRuleManageDT.setParticipationTypeCode(defaultValueType.getDefaultParticipation().getParticipationType().getCode());
                    edxRuleManageDT.setParticipationUid(Long.valueOf(defaultValueType.getDefaultParticipation().getEntityUid()));
                    edxRuleManageDT.setParticipationClassCode(defaultValueType.getDefaultParticipation().getEntityClass());
                }catch(Exception e){
//                    logger.error("The defaultValueType exception is not valid for code and/or uid and/or classCode. Please check: ", defaultValueType);
                }
            }else if (defaultValueType.getDefaultStringValue() != null) {
                edxRuleManageDT.setDefaultStringValue(defaultValueType.getDefaultStringValue());

            }else if (defaultValueType.getDefaultCommentValue() != null) {
                edxRuleManageDT.setDefaultCommentValue(defaultValueType.getDefaultCommentValue());
            }else if (defaultCodedValueArray != null) {
                Collection<Object> toValueColl = new ArrayList<Object>();
                for (int j = 0; j < defaultCodedValueArray.size(); j++) {
                    CodedType codedType = defaultCodedValueArray.get(j);
                    toValueColl.add(codedType.getCode());
                }
                edxRuleManageDT.setDefaultCodedValueColl(toValueColl);
            } else if (defaultValueType.getDefaultNumericValue() != null) {
                defaultValueType.getDefaultNumericValue().getValue1();
                edxRuleManageDT.setDefaultNumericValue(defaultValueType.getDefaultNumericValue().getValue1() + "");
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
            for (int i = 0; i < methodList.length; i++) {
                Method method = (Method) methodList[i];
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
                        if (logic.equalsIgnoreCase("CT") && edxRuleManageDT.getValue()!=null) {
                            // for multi-selects separated by commas
                            String[] values = edxRuleManageDT.getValue().split(
                                    ",");
                            for (String value : values) {
                                if (!(ob.toString().indexOf(value) >= 0)) {
                                    return false;
                                }
                            }
                            return true;
                        } else if (logic.equalsIgnoreCase("=")) {
                            if (ob.toString().equals(edxRuleManageDT.getValue())) {
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
                            .equals(NEDSSConstant.NBS_QUESTION_DATATYPE_MASK_NUM_YYYY))))
                    {
                        long sourceValue = 0;
                        long advanceCriteria = 0;
                        if (metaData.getDataType().toUpperCase().indexOf(NEDSSConstant.DATE_DATATYPE)>=0) {
                            Timestamp time = (Timestamp) (ob);
                            sourceValue = time.getTime();
                            Timestamp adCrtTime = StringUtils.stringToStrutsTimestamp(edxRuleManageDT.getValue());
                            advanceCriteria = adCrtTime.getTime();

                        } else {
                            if (ob != null) {
                                sourceValue = Long.parseLong(ob.toString());
                            } else
                                sourceValue = 0L;
                            if (edxRuleManageDT.getValue() != null)
                                advanceCriteria = Long.parseLong(edxRuleManageDT.getValue());
                            else
                                advanceCriteria =0L;
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
                                     PublicHealthCaseVO publicHealthCaseVO, NbsQuestionMetadata metaData) {
        String behavior = edxRuleManageDT.getBehavior();
        boolean isOverwrite = false;
        if (behavior.equalsIgnoreCase("1")) {
            isOverwrite = true;
        } else if (behavior.equalsIgnoreCase("2")) {
            isOverwrite = false;
        }
        Collection<ActIdDto> actIdColl = publicHealthCaseVO
                .getTheActIdDTCollection();
        if (actIdColl != null && actIdColl.size() > 0) {
            Iterator<ActIdDto> ite = actIdColl.iterator();
            ActIdDto actIdDT = (ActIdDto) ite.next();
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
