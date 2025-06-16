package gov.cdc.dataprocessing.service.implementation.public_health_case;

import gov.cdc.dataprocessing.constant.EdxPHCRConstants;
import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.ObjectName;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.base.BasePamContainer;
import gov.cdc.dataprocessing.model.container.model.*;
import gov.cdc.dataprocessing.model.dto.act.ActIdDto;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.edx.EdxRuleAlgorothmManagerDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.locator.PostalLocatorDto;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityDetailLogDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsQuestionMetadata;
import gov.cdc.dataprocessing.model.dto.notification.NotificationDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.model.dto.person.PersonRaceDto;
import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.custom_model.QuestionRequiredNnd;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.CustomNbsQuestionRepository;
import gov.cdc.dataprocessing.service.interfaces.cache.ICacheApiService;
import gov.cdc.dataprocessing.service.interfaces.notification.INotificationService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IInvestigationNotificationService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IInvestigationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.*;

import static gov.cdc.dataprocessing.constant.elr.NEDSSConstant.*;

@Service

public class InvestigationNotificationService  implements IInvestigationNotificationService {
    private static final Logger logger = LoggerFactory.getLogger(InvestigationNotificationService.class); // NOSONAR

    private final IInvestigationService investigationService;
    private final INotificationService notificationService;
    private final CustomNbsQuestionRepository customNbsQuestionRepository;
    private final ICacheApiService cacheApiService;

    public InvestigationNotificationService(
            IInvestigationService investigationService,
            INotificationService notificationService,
            CustomNbsQuestionRepository customNbsQuestionRepository, @Lazy ICacheApiService cacheApiService) {
        this.investigationService = investigationService;
        this.notificationService = notificationService;
        this.customNbsQuestionRepository = customNbsQuestionRepository;
        this.cacheApiService = cacheApiService;
    }


    public EDXActivityDetailLogDto sendNotification(Object pageObj, String nndComment) throws DataProcessingException {
        NotificationProxyContainer notProxyVO;
        // Create the Notification object
        PublicHealthCaseContainer publicHealthCaseContainer = switch (pageObj) {
            case PageActProxyContainer pageActProxyContainer -> pageActProxyContainer.getPublicHealthCaseContainer();
            case PamProxyContainer pamProxyContainer -> pamProxyContainer.getPublicHealthCaseContainer();
            case PublicHealthCaseContainer healthCaseContainer -> healthCaseContainer;
            case null, default -> {
                if (pageObj == null) {
                    throw new DataProcessingException("Cannot create Notification: pageObj is null");
                }
                throw new DataProcessingException("Cannot create Notification for unknown page type: " + pageObj.getClass().getCanonicalName());
            }
        };
        NotificationDto notDT = new NotificationDto();
        notDT.setItNew(true);
        notDT.setNotificationUid(-1L);
        notDT.setAddTime(new java.sql.Timestamp(new Date().getTime()));
        notDT.setTxt(nndComment);
        notDT.setStatusCd("A");
        notDT.setCaseClassCd(publicHealthCaseContainer.getThePublicHealthCaseDto().getCaseClassCd());
        notDT.setStatusTime(new java.sql.Timestamp(new Date().getTime()));
        notDT.setVersionCtrlNbr(1);
        notDT.setSharedInd("T");
        notDT.setCaseConditionCd(publicHealthCaseContainer.getThePublicHealthCaseDto().getCd());
        notDT.setAutoResendInd("F");

        NotificationContainer notVO = new NotificationContainer();
        notVO.setTheNotificationDT(notDT);
        notVO.setItNew(true);

        // create the act relationship between the phc & notification
        ActRelationshipDto actDT1 = new ActRelationshipDto();
        actDT1.setItNew(true);
        actDT1.setTargetActUid(publicHealthCaseContainer.getThePublicHealthCaseDto().getPublicHealthCaseUid());
        actDT1.setSourceActUid(notDT.getNotificationUid());
        actDT1.setAddTime(new java.sql.Timestamp(new Date().getTime()));
        actDT1.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        actDT1.setSequenceNbr(1);
        actDT1.setStatusCd("A");
        actDT1.setStatusTime(new java.sql.Timestamp(new Date().getTime()));
        actDT1.setTypeCd(NEDSSConstant.ACT106_TYP_CD);
        actDT1.setSourceClassCd(NEDSSConstant.ACT106_SRC_CLASS_CD);
        actDT1.setTargetClassCd(NEDSSConstant.ACT106_TAR_CLASS_CD);

        notProxyVO = new NotificationProxyContainer();
        notProxyVO.setItNew(true);
        notProxyVO.setThePublicHealthCaseContainer(publicHealthCaseContainer);
        notProxyVO.setTheNotificationContainer(notVO);

        ArrayList<Object> actRelColl = new ArrayList<>();
        actRelColl.addFirst(actDT1);
        notProxyVO.setTheActRelationshipDTCollection(actRelColl);

        return sendProxyToEJB(notProxyVO, pageObj);
    }

    @SuppressWarnings("java:S3776")
    protected EDXActivityDetailLogDto  sendProxyToEJB(NotificationProxyContainer notificationProxyVO, Object pageObj)
    {
        EDXActivityDetailLogDto eDXActivityDetailLogDT = new EDXActivityDetailLogDto();

        eDXActivityDetailLogDT.setRecordType(EdxPHCRConstants.MSG_TYPE.Notification.name());
        eDXActivityDetailLogDT.setRecordName(PHCR_IMPORT_SRT);
        eDXActivityDetailLogDT.setLogType(EdxRuleAlgorothmManagerDto.STATUS_VAL.Success.name());

        try {
            boolean formatErr = false;

            PublicHealthCaseDto phcDT = notificationProxyVO.getThePublicHealthCaseContainer().getThePublicHealthCaseDto();
            Long publicHealthCaseUid = phcDT.getPublicHealthCaseUid();

            Map<Object,Object> subMap = new HashMap<>();
            String investigationFormCd = cacheApiService.getSrteCacheString(ObjectName.INVESTIGATION_FORM_CONDITION_CODE.name(), phcDT.getCd());
            Collection<QuestionRequiredNnd>  notifReqColl;

            notifReqColl = customNbsQuestionRepository.retrieveQuestionRequiredNnd(investigationFormCd);

            if(notifReqColl != null && !notifReqColl.isEmpty()) {
                for (QuestionRequiredNnd questionRequiredNnd : notifReqColl) {
                    NbsQuestionMetadata metaData = new NbsQuestionMetadata(questionRequiredNnd);
                    subMap.put(metaData.getNbsQuestionUid(), metaData);
                }
            }

            Map<?,?> result;
            result= validatePAMNotficationRequiredFieldsGivenPageProxy(pageObj, publicHealthCaseUid, subMap,investigationFormCd);
            StringBuilder errorText =new StringBuilder(20);
            if(result!=null && !result.isEmpty()){
                int i =  result.size();
                Collection<?> coll =result.values();
                Iterator<?> it= coll.iterator();
                while(it.hasNext()){
                    String label = (String)it.next();
                    --i;
                    errorText.append("[").append(label).append("]");
                    if(it.hasNext()){
                        errorText.append("; and ");
                    }
                    if(i==0)
                        errorText.append(".");

                }
                formatErr = true;
                eDXActivityDetailLogDT.setLogType(EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure.name());
                eDXActivityDetailLogDT.setComment(EdxELRConstant.MISSING_NOTF_REQ_FIELDS+ errorText);
            }

            String programAreaCd = notificationProxyVO.getThePublicHealthCaseContainer().getThePublicHealthCaseDto().getProgAreaCd();
            NotificationContainer notifVO = notificationProxyVO.getTheNotificationContainer();
            NotificationDto notifDT = notifVO.getTheNotificationDT();
            notifDT.setProgAreaCd(programAreaCd);
            notifVO.setTheNotificationDT(notifDT);
            notificationProxyVO.setTheNotificationContainer(notifVO);
            Long realNotificationUid = notificationService.setNotificationProxy(notificationProxyVO);
            eDXActivityDetailLogDT.setRecordId(String.valueOf(realNotificationUid));
            if (!formatErr)
            {
                eDXActivityDetailLogDT.setComment("Notification created (UID: "+realNotificationUid+")");
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
            eDXActivityDetailLogDT.setLogType(EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure.name());
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            String exceptionMessage = errors.toString();
            exceptionMessage = exceptionMessage.substring(0,Math.min(exceptionMessage.length(), 2000));
            eDXActivityDetailLogDT.setComment(exceptionMessage);
        }
        return eDXActivityDetailLogDT;
    }


    /**
     * Returns the list of Fields that are required (and not filled) to Create Notification from PAM Cases
     */
    @SuppressWarnings({"java:S3776", "java:S6541", "java:S1871"})
//    protected Map<Object, Object> validatePAMNotficationRequiredFieldsGivenPageProxy(Object pageObj, Long publicHealthCaseUid,
//                                                                                  Map<Object, Object>  reqFields, String formCd) throws DataProcessingException {
//
//        Map<Object, Object>  missingFields = new TreeMap<>();
//
//        BasePamContainer pamVO;
//        Collection<ParticipationDto> participationDTCollection;
//        PublicHealthCaseDto publicHealthCaseDto;
//        Collection<PersonContainer> personVOCollection;
//        Map<Object, Object>  answerMap;
//        Collection<ActIdDto>  actIdColl;
//
//        if(formCd.equalsIgnoreCase(NEDSSConstant.INV_FORM_RVCT)||formCd.equalsIgnoreCase(NEDSSConstant.INV_FORM_VAR))
//        {
//            PamProxyContainer proxyVO = new PamProxyContainer();
//            if(pageObj == null || pageObj instanceof PublicHealthCaseContainer)
//            {
//                // This should be empty
//            }
//            else
//            {
//                proxyVO = (PamProxyContainer) pageObj;
//            }
//            pamVO = proxyVO.getPamVO();
//            answerMap = pamVO.getPamAnswerDTMap();
//            if(pageObj == null || pageObj instanceof PublicHealthCaseContainer)
//            {
//                participationDTCollection = new ArrayList<>();
//            }
//            else
//            {
//                participationDTCollection = proxyVO.getTheParticipationDTCollection();
//            }
//            personVOCollection  = proxyVO.getThePersonVOCollection();
//            publicHealthCaseDto = proxyVO.getPublicHealthCaseContainer().getThePublicHealthCaseDto();
//            actIdColl = proxyVO.getPublicHealthCaseContainer().getTheActIdDTCollection();
//        }
//        else
//        {
//            // HIT THIS
//            PageActProxyContainer pageProxyVO;
//            if(pageObj == null  || pageObj instanceof PublicHealthCaseContainer)
//            {
//                pageProxyVO =  investigationService.getPageProxyVO(NEDSSConstant.CASE, publicHealthCaseUid);
//            }
//            else
//            {
//                pageProxyVO = (PageActProxyContainer) pageObj;
//            }
//            PageActProxyContainer pageActProxyContainer =pageProxyVO;
//
//            answerMap = pageProxyVO.getPageVO().getPamAnswerDTMap();
//            if(pageObj == null || pageObj instanceof PublicHealthCaseContainer)
//            {
//                participationDTCollection  = pageActProxyContainer.getPublicHealthCaseContainer().getTheParticipationDTCollection();
//            }
//            else
//            {
//                participationDTCollection = pageActProxyContainer.getTheParticipationDtoCollection();
//            }
//            personVOCollection  = pageActProxyContainer.getThePersonContainerCollection();
//            publicHealthCaseDto = pageActProxyContainer.getPublicHealthCaseContainer().getThePublicHealthCaseDto();
//            actIdColl = pageActProxyContainer.getPublicHealthCaseContainer().getTheActIdDTCollection();
//        }
//
//
//        PersonContainer personVO = getPersonVO(NEDSSConstant.PHC_PATIENT, participationDTCollection,personVOCollection );
//        PersonDto personDT = new PersonDto();
//        if (personVO != null) {
//            personDT = personVO.getThePersonDto();
//        }
//
//        for (Object o : reqFields.keySet()) {
//            Long key = (Long) o;
//            NbsQuestionMetadata metaData = (NbsQuestionMetadata) reqFields.get(key);
//            String dLocation = metaData.getDataLocation() == null ? "" : metaData.getDataLocation();
//            String label = metaData.getQuestionLabel() == null ? "" : metaData.getQuestionLabel();
//            Long nbsQueUid = metaData.getNbsQuestionUid();
//            if (!dLocation.isEmpty()) {
//                if (dLocation.startsWith("NBS_Answer.") && answerMap != null) {
//                    if (answerMap.get(key) == null) {
//                        missingFields.put(metaData.getQuestionIdentifier(), metaData.getQuestionLabel());
//                    }
//                }
//                else if (dLocation.toLowerCase().startsWith("public_health_case.")) {
//                    String attrToChk = dLocation.substring(dLocation.indexOf(".") + 1);
//
//                    String getterNm = createGetterMethod(attrToChk);
//                    Map<Object, Object> methodMap = getMethods(publicHealthCaseDto.getClass());
//                    Method method = (Method) methodMap.get(getterNm.toLowerCase());
//                    try {
//                        Object obj = method.invoke(publicHealthCaseDto, (Object[]) null);
//                        checkObject(obj, missingFields, metaData);
//                    } catch (Exception e) {
//                        throw new DataProcessingException(e.getMessage(), e);
//                    }
//
//                }
//                else if (dLocation.toLowerCase().startsWith("person."))
//                {
//                    String attrToChk = dLocation.substring(dLocation.indexOf(".") + 1);
//                    String getterNm = createGetterMethod(attrToChk);
//                    Map<Object, Object> methodMap = getMethods(personDT.getClass());
//                    Method method = (Method) methodMap.get(getterNm.toLowerCase());
//                    try {
//                        Object obj = method.invoke(personDT, (Object[]) null);
//                        checkObject(obj, missingFields, metaData);
//                    } catch (Exception e) {
//                        throw new DataProcessingException(e.getMessage(), e);
//                    }
//                }
//                else if (dLocation.toLowerCase().startsWith("postal_locator."))
//                {
//                    String attrToChk = dLocation.substring(dLocation.indexOf(".") + 1);
//                    String getterNm = createGetterMethod(attrToChk);
//                    PostalLocatorDto postalLocator = new PostalLocatorDto();
//                    Map<Object, Object> methodMap = getMethods(postalLocator.getClass());
//                    Method method = (Method) methodMap.get(getterNm.toLowerCase());
//                    if (personVO != null
//                            && personVO.getTheEntityLocatorParticipationDtoCollection() != null
//                            && !personVO.getTheEntityLocatorParticipationDtoCollection().isEmpty()) {
//                        for (EntityLocatorParticipationDto elp : personVO.getTheEntityLocatorParticipationDtoCollection()) {
//                            if (elp.getThePostalLocatorDto() != null) {
//                                //check if this is the correct entity locator to check
//                                if (elp.getUseCd() != null &&
//                                        metaData.getDataUseCd() != null &&
//                                        metaData.getDataUseCd().equalsIgnoreCase(elp.getUseCd())) {
//                                    postalLocator = elp.getThePostalLocatorDto();
//                                    try {
//                                        Object obj = method.invoke(postalLocator, (Object[]) null);
//                                        checkObject(obj, missingFields, metaData);
//                                    } catch (Exception e) {
//                                        throw new DataProcessingException(e.getMessage(), e);
//                                    }
//                                }
//                            } else if (elp.getClassCd() != null
//                                    && elp.getClassCd().equals("PST")
//                                    && elp.getTheTeleLocatorDto() == null) {
//                                checkObject(null, missingFields, metaData);
//                            }
//                        }
//                    } else {
//                        checkObject(null, missingFields, metaData);
//                    }
//                }
//                else if (dLocation.toLowerCase().startsWith("person_race."))
//                {
//                    String attrToChk = dLocation.substring(dLocation.indexOf(".") + 1);
//                    String getterNm = createGetterMethod(attrToChk);
//                    PersonRaceDto personRace = new PersonRaceDto();
//                    Map<Object, Object> methodMap = getMethods(personRace.getClass());
//                    Method method = (Method) methodMap.get(getterNm.toLowerCase());
//                    if (personVO != null
//                            && personVO.getThePersonRaceDtoCollection() != null
//                            && !personVO.getThePersonRaceDtoCollection().isEmpty()) {
//                        for (PersonRaceDto personRaceDto : personVO.getThePersonRaceDtoCollection()) {
//                            personRace = personRaceDto;
//                            try {
//                                Object obj = method.invoke(personRace, (Object[]) null);
//                                checkObject(obj, missingFields, metaData);
//                            } catch (Exception e) {
//                                throw new DataProcessingException(e.getMessage(), e);
//                            }
//                        }
//                    } else {
//                        checkObject(null, missingFields, metaData);
//                    }
//                }
//                else if (dLocation.toLowerCase().startsWith("act_id."))
//                {
//                    String attrToChk = dLocation.substring(dLocation.indexOf(".") + 1);
//                    String getterNm = createGetterMethod(attrToChk);
//                    if (actIdColl != null && !actIdColl.isEmpty()) {
//                        for (ActIdDto adt : actIdColl) {
//                            String typeCd = adt.getTypeCd() == null ? "" : adt.getTypeCd();
//                            String value = adt.getRootExtensionTxt() == null ? "" : adt.getRootExtensionTxt();
//                            if (typeCd.equalsIgnoreCase(NEDSSConstant.ACT_ID_STATE_TYPE_CD) && value.isEmpty() && (label.toLowerCase().contains(STATE_STR))) {
//                                Map<Object, Object> methodMap = getMethods(adt.getClass());
//                                Method method = (Method) methodMap.get(getterNm.toLowerCase());
//                                try {
//                                    Object obj = method.invoke(adt, (Object[]) null);
//                                    checkObject(obj, missingFields, metaData);
//                                } catch (Exception e) {
//                                    throw new DataProcessingException(e.getMessage(), e);
//                                }
//                            } else if (typeCd.equalsIgnoreCase(NEDSSConstant.ACT_ID_STATE_TYPE_CD)
//                                    && formCd.equalsIgnoreCase(NEDSSConstant.INV_FORM_RVCT)
//                                    && (label.toLowerCase().contains(STATE_STR))) {
//                                Map<Object, Object> methodMap = getMethods(adt.getClass());
//                                Method method = (Method) methodMap.get(getterNm.toLowerCase());
//                                try {
//                                    Object obj = method.invoke(adt, (Object[]) null);
//                                    checkObject(obj, missingFields, metaData);
//                                } catch (Exception e) {
//                                    throw new DataProcessingException(e.getMessage(), e);
//                                }
//                            } else if (typeCd.equalsIgnoreCase("CITY")
//                                    && value.isEmpty() && (label.toLowerCase().contains("city"))) {
//                                Map<Object, Object> methodMap = getMethods(adt.getClass());
//                                Method method = (Method) methodMap.get(getterNm.toLowerCase());
//                                try {
//                                    Object obj = method.invoke(adt, (Object[]) null);
//                                    checkObject(obj, missingFields, metaData);
//                                } catch (Exception e) {
//                                    throw new DataProcessingException(e.getMessage(), e);
//                                }
//                            }
//                        }
//                    } else if (formCd.equalsIgnoreCase(NEDSSConstant.INV_FORM_RVCT)
//                            && (label.toLowerCase().contains(STATE_STR))) {
//                        missingFields.put(metaData.getQuestionIdentifier(), metaData.getQuestionLabel());
//                    }
//                }
//                else if (dLocation.toLowerCase().startsWith("nbs_case_answer.")
//                        && !(formCd.equalsIgnoreCase(NEDSSConstant.INV_FORM_RVCT)
//                        || formCd.equalsIgnoreCase(NEDSSConstant.INV_FORM_VAR)))
//                {
//                    if (answerMap == null || answerMap.isEmpty() || (answerMap.get(nbsQueUid) == null && answerMap.get(metaData.getQuestionIdentifier()) == null)) {
//                        missingFields.put(metaData.getQuestionIdentifier(), metaData.getQuestionLabel());
//                    }
//                }
//                else if (dLocation.toLowerCase().startsWith("nbs_case_answer.") && (formCd.equalsIgnoreCase(NEDSSConstant.INV_FORM_RVCT)) &&
//                        answerMap == null || Objects.requireNonNull(answerMap).isEmpty() || (answerMap.get(nbsQueUid) == null && answerMap.get(metaData.getQuestionIdentifier()) == null))
//                {
//                    missingFields.put(metaData.getQuestionIdentifier(), metaData.getQuestionLabel());
//                }
//
//            }
//        }
//
//
//        if (missingFields.isEmpty())
//        {
//            return null; // NOSONAR
//        }
//        else
//        {
//            return missingFields;
//        }
//    }
//


    protected Map<Object, Object> validatePAMNotficationRequiredFieldsGivenPageProxy(
            Object pageObj, Long publicHealthCaseUid,
            Map<Object, Object> reqFields, String formCd) throws DataProcessingException {

        ValidationContext context = buildValidationContext(pageObj, publicHealthCaseUid, formCd);
        Map<Object, Object> missingFields = new TreeMap<>();

        for (Map.Entry<Object, Object> entry : reqFields.entrySet()) {
            Long key = (Long) entry.getKey();
            NbsQuestionMetadata metaData = (NbsQuestionMetadata) entry.getValue();

            String dLocation = safe(metaData.getDataLocation());
            String label = safe(metaData.getQuestionLabel());
            Long nbsQueUid = metaData.getNbsQuestionUid();

            if (dLocation.isEmpty()) {
                continue;
            }

            try {
                if (dLocation.startsWith("NBS_Answer.")) {
                    validateNbsAnswer(context, key, metaData, missingFields);
                } else if (dLocation.toLowerCase().startsWith("public_health_case.")) {
                    validateReflection(context.publicHealthCaseDto, dLocation, metaData, missingFields);
                } else if (dLocation.toLowerCase().startsWith("person.")) {
                    validateReflection(context.personDto, dLocation, metaData, missingFields);
                } else if (dLocation.toLowerCase().startsWith("postal_locator.")) {
                    validatePostalLocator(context, dLocation, metaData, missingFields);
                } else if (dLocation.toLowerCase().startsWith("person_race.")) {
                    validatePersonRace(context, dLocation, metaData, missingFields);
                } else if (dLocation.toLowerCase().startsWith("act_id.")) {
                    validateActId(context, dLocation, label, formCd, metaData, missingFields);
                } else if (dLocation.toLowerCase().startsWith("nbs_case_answer.")) {
                    validateNbsCaseAnswer(context, nbsQueUid, metaData, missingFields);
                }
            } catch (Exception e) {
                throw new DataProcessingException("Validation error at: " + dLocation, e);
            }
        }

        return missingFields.isEmpty() ? null : missingFields;
    }

    protected void validateNbsAnswer(ValidationContext context, Long key, NbsQuestionMetadata metaData,
                                   Map<Object, Object> missingFields) {
        if (context.answerMap == null || context.answerMap.get(key) == null) {
            addMissing(metaData, missingFields);
        }
    }

    protected void validateReflection(Object target, String dLocation, NbsQuestionMetadata metaData,
                                    Map<Object, Object> missingFields) throws Exception {
        Object val = reflectGet(target, dLocation);
        checkObject(val, missingFields, metaData);
    }

    protected void validatePostalLocator(ValidationContext context, String dLocation, NbsQuestionMetadata metaData,
                                       Map<Object, Object> missingFields) throws Exception {
        String attr = dLocation.substring(dLocation.indexOf('.') + 1);
        String getter = createGetterMethod(attr);
        Method method = getMethod(PostalLocatorDto.class, getter);

        if (context.personVO != null && context.personVO.getTheEntityLocatorParticipationDtoCollection() != null) {
            for (EntityLocatorParticipationDto elp : context.personVO.getTheEntityLocatorParticipationDtoCollection()) {
                if (elp.getThePostalLocatorDto() != null
                        && metaData.getDataUseCd() != null
                        && metaData.getDataUseCd().equalsIgnoreCase(elp.getUseCd())) {
                    Object obj = method.invoke(elp.getThePostalLocatorDto());
                    checkObject(obj, missingFields, metaData);
                } else if ("PST".equals(elp.getClassCd()) && elp.getTheTeleLocatorDto() == null) {
                    checkObject(null, missingFields, metaData);
                }
            }
        } else {
            checkObject(null, missingFields, metaData);
        }
    }

    protected void validatePersonRace(ValidationContext context, String dLocation, NbsQuestionMetadata metaData,
                                    Map<Object, Object> missingFields) throws Exception {
        String attr = dLocation.substring(dLocation.indexOf('.') + 1);
        String getter = createGetterMethod(attr);
        Method method = getMethod(PersonRaceDto.class, getter);

        if (context.personVO != null && context.personVO.getThePersonRaceDtoCollection() != null) {
            for (PersonRaceDto race : context.personVO.getThePersonRaceDtoCollection()) {
                Object obj = method.invoke(race);
                checkObject(obj, missingFields, metaData);
            }
        } else {
            checkObject(null, missingFields, metaData);
        }
    }

    protected void validateActId(ValidationContext context, String dLocation, String label, String formCd,
                               NbsQuestionMetadata metaData, Map<Object, Object> missingFields) throws Exception {
        String attr = dLocation.substring(dLocation.indexOf('.') + 1);
        String getter = createGetterMethod(attr);

        if (context.actIdColl != null) {
            for (ActIdDto adt : context.actIdColl) {
                String typeCd = safe(adt.getTypeCd());
                String value = safe(adt.getRootExtensionTxt());

                boolean shouldValidate =
                        (typeCd.equalsIgnoreCase(NEDSSConstant.ACT_ID_STATE_TYPE_CD) && value.isEmpty() && label.toLowerCase().contains(LOWER_CASE_STATE)) ||
                                (typeCd.equalsIgnoreCase(NEDSSConstant.ACT_ID_STATE_TYPE_CD) && formCd.equalsIgnoreCase(NEDSSConstant.INV_FORM_RVCT) && label.toLowerCase().contains(LOWER_CASE_STATE)) ||
                                (typeCd.equalsIgnoreCase(UPPER_CASE_CITY) && value.isEmpty() && label.toLowerCase().contains(LOWER_CASE_CITY));

                if (shouldValidate) {
                    Method method = getMethod(adt.getClass(), getter);
                    Object obj = method.invoke(adt);
                    checkObject(obj, missingFields, metaData);
                }
            }
        } else if (formCd.equalsIgnoreCase(NEDSSConstant.INV_FORM_RVCT) && label.toLowerCase().contains(LOWER_CASE_STATE)) {
            addMissing(metaData, missingFields);
        }
    }

    protected void validateNbsCaseAnswer(ValidationContext context, Long nbsQueUid,
                                       NbsQuestionMetadata metaData, Map<Object, Object> missingFields) {
        boolean isMissing = context.answerMap == null || context.answerMap.isEmpty()
                || (context.answerMap.get(nbsQueUid) == null
                && context.answerMap.get(metaData.getQuestionIdentifier()) == null);

        if (isMissing) {
            addMissing(metaData, missingFields);
        }
    }

    protected Method getMethod(Class<?> clazz, String getter) {
        Map<Object, Object> methodMap = getMethods(clazz);
        return (Method) methodMap.get(getter.toLowerCase());
    }

    protected String safe(String val) {
        return val == null ? "" : val;
    }


    protected static class ValidationContext {
        Map<Object, Object> answerMap;
        Collection<ParticipationDto> participationDTCollection;
        Collection<PersonContainer> personVOCollection;
        PublicHealthCaseDto publicHealthCaseDto;
        Collection<ActIdDto> actIdColl;
        PersonDto personDto;
        PersonContainer personVO;
        String formCd;
    }

    @SuppressWarnings("java:S3776")
    protected ValidationContext buildValidationContext(Object pageObj, Long uid, String formCd) throws DataProcessingException {
        ValidationContext ctx = new ValidationContext();
        ctx.formCd = formCd;

        if (formCd.equalsIgnoreCase(NEDSSConstant.INV_FORM_RVCT)
                || formCd.equalsIgnoreCase(NEDSSConstant.INV_FORM_VAR)) {
            PamProxyContainer proxy = (pageObj instanceof PamProxyContainer) ? (PamProxyContainer) pageObj : new PamProxyContainer();
            BasePamContainer pamVO = proxy.getPamVO();
            ctx.answerMap = pamVO != null ? pamVO.getPamAnswerDTMap() : null;
            ctx.participationDTCollection = proxy.getTheParticipationDTCollection() != null ?
                    proxy.getTheParticipationDTCollection() : new ArrayList<>();
            ctx.personVOCollection = proxy.getThePersonVOCollection();
            ctx.publicHealthCaseDto = proxy.getPublicHealthCaseContainer() != null ?
                    proxy.getPublicHealthCaseContainer().getThePublicHealthCaseDto() : null;
            ctx.actIdColl = proxy.getPublicHealthCaseContainer() != null ?
                    proxy.getPublicHealthCaseContainer().getTheActIdDTCollection() : null;
        } else {
            PageActProxyContainer proxy = (pageObj instanceof PageActProxyContainer)
                    ? (PageActProxyContainer) pageObj
                    : investigationService.getPageProxyVO(NEDSSConstant.CASE, uid);

            ctx.answerMap = proxy.getPageVO() != null ? proxy.getPageVO().getPamAnswerDTMap() : null;
            ctx.participationDTCollection = proxy.getTheParticipationDtoCollection() != null ?
                    proxy.getTheParticipationDtoCollection() :
                    proxy.getPublicHealthCaseContainer().getTheParticipationDTCollection();
            ctx.personVOCollection = proxy.getThePersonContainerCollection();
            ctx.publicHealthCaseDto = proxy.getPublicHealthCaseContainer().getThePublicHealthCaseDto();
            ctx.actIdColl = proxy.getPublicHealthCaseContainer().getTheActIdDTCollection();
        }

        ctx.personVO = getPersonVO(NEDSSConstant.PHC_PATIENT, ctx.participationDTCollection, ctx.personVOCollection);
        ctx.personDto = ctx.personVO != null ? ctx.personVO.getThePersonDto() : new PersonDto();
        return ctx;
    }

    protected Object reflectGet(Object target, String dLocation) throws Exception {
        String attr = dLocation.substring(dLocation.indexOf('.') + 1);
        String getter = createGetterMethod(attr);
        Method method = (Method) getMethods(target.getClass()).get(getter.toLowerCase());
        return method.invoke(target);
    }

    protected void addMissing(NbsQuestionMetadata metaData, Map<Object, Object> missingFields) {
        missingFields.put(metaData.getQuestionIdentifier(), metaData.getQuestionLabel());
    }


    protected String createGetterMethod(String attrToChk) {
        StringTokenizer tokenizer = new StringTokenizer(attrToChk,"_");
        StringBuilder methodName = new StringBuilder();
        while (tokenizer.hasMoreTokens()){
            String token = tokenizer.nextToken();
            methodName.append(Character.toUpperCase(token.charAt(0))).append(token.substring(1).toLowerCase());

        }
        return "get" + methodName;
    }

    @SuppressWarnings("java:S3740")
    protected  Map<Object, Object>  getMethods(Class beanClass) {
        Method[] gettingMethods = beanClass.getMethods();
        Map<Object, Object>  resultMap = new HashMap<>();
        for (Method gettingMethod : gettingMethods) {
            String methodName = ( gettingMethod).getName().toLowerCase();
            resultMap.put(methodName,  gettingMethod);
        }
        return resultMap;
    }

    protected void checkObject(Object obj,  Map<Object, Object>  missingFields, NbsQuestionMetadata metaData)  {
        String value = obj == null ? "" : obj.toString();
        if(value == null || value.trim().isEmpty()) {
            missingFields.put(metaData.getQuestionIdentifier(), metaData.getQuestionLabel());
        }
    }
    @SuppressWarnings({"java:S3776","java:S3626"})
    protected PersonContainer getPersonVO(String typeCd, Collection<ParticipationDto> participationDTCollection,
                                        Collection<PersonContainer> personVOCollection)  {
        ParticipationDto participationDT;
        PersonContainer personVO;
        if (participationDTCollection  != null) {
            Iterator<ParticipationDto> anIterator1;
            Iterator<PersonContainer> anIterator2 ;
            for (anIterator1 = participationDTCollection.iterator(); anIterator1.hasNext();) {
                participationDT =  anIterator1.next();
                if (participationDT.getTypeCd() != null && (participationDT.getTypeCd()).compareTo(typeCd) == 0) {
                    for (anIterator2 = personVOCollection.iterator(); anIterator2.hasNext();) {
                        personVO =  anIterator2.next();
                        if (personVO.getThePersonDto().getPersonUid().longValue() == participationDT
                                .getSubjectEntityUid().longValue()) {
                            return personVO;
                        }
                        else
                        {
                            continue;
                        }
                    }
                }
                else
                {
                    continue;
                }
            }
        }
        return null;
    }


}
