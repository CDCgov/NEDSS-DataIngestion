package gov.cdc.dataprocessing.service.implementation.public_health_case;

import gov.cdc.dataprocessing.cache.SrteCache;
import gov.cdc.dataprocessing.constant.EdxPHCRConstants;
import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
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
import gov.cdc.dataprocessing.service.interfaces.notification.INotificationService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IInvestigationNotificationService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IInvestigationService;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.*;

import static gov.cdc.dataprocessing.constant.elr.NEDSSConstant.PHCR_IMPORT_SRT;
import static gov.cdc.dataprocessing.constant.elr.NEDSSConstant.STATE_STR;

@Service
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 1118 - Private constructor complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118"})
public class InvestigationNotificationService  implements IInvestigationNotificationService {
    private final IInvestigationService investigationService;
    private final INotificationService notificationService;
    private final CustomNbsQuestionRepository customNbsQuestionRepository;

    public InvestigationNotificationService(
            IInvestigationService investigationService,
            INotificationService notificationService,
            CustomNbsQuestionRepository customNbsQuestionRepository) {
        this.investigationService = investigationService;
        this.notificationService = notificationService;
        this.customNbsQuestionRepository = customNbsQuestionRepository;
    }

    public EDXActivityDetailLogDto sendNotification(Object pageObj, String nndComment) throws DataProcessingException {
        NotificationProxyContainer notProxyVO;
        // Create the Notification object
        PublicHealthCaseContainer publicHealthCaseContainer;
        if (pageObj instanceof PageActProxyContainer) {
            publicHealthCaseContainer = ((PageActProxyContainer) pageObj).getPublicHealthCaseContainer();
        } else if (pageObj instanceof PamProxyContainer) {
            publicHealthCaseContainer = ((PamProxyContainer) pageObj).getPublicHealthCaseContainer();
        } else if (pageObj instanceof PublicHealthCaseContainer) {
            publicHealthCaseContainer = ((PublicHealthCaseContainer) pageObj);
        } else {
            throw new DataProcessingException("Cannot create Notification for unknown page type: " + pageObj.getClass().getCanonicalName());
        }
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
        actRelColl.add(0, actDT1);
        notProxyVO.setTheActRelationshipDTCollection(actRelColl);

        // EdxPHCRDocumentUtil.sendProxyToEJB(notProxyVO, pageObj);
        return sendProxyToEJB(notProxyVO, pageObj);
    }

    @SuppressWarnings("java:S3776")
    private EDXActivityDetailLogDto  sendProxyToEJB(NotificationProxyContainer notificationProxyVO, Object pageObj)
    {
        HashMap<Object,Object> nndRequiredMap = new HashMap<>();
        EDXActivityDetailLogDto eDXActivityDetailLogDT = new EDXActivityDetailLogDto();

        eDXActivityDetailLogDT.setRecordType(EdxPHCRConstants.MSG_TYPE.Notification.name());
        eDXActivityDetailLogDT.setRecordName(PHCR_IMPORT_SRT);
        eDXActivityDetailLogDT.setLogType(EdxRuleAlgorothmManagerDto.STATUS_VAL.Success.name());

        try {
            boolean formatErr = false;

            PublicHealthCaseDto phcDT = notificationProxyVO.getThePublicHealthCaseContainer().getThePublicHealthCaseDto();
            Long publicHealthCaseUid = phcDT.getPublicHealthCaseUid();

            Map<Object,Object> subMap = new HashMap<>();
            HashMap<String, String> condAndFormCdTreeMap = SrteCache.investigationFormConditionCode;

            String investigationFormCd = condAndFormCdTreeMap.get(phcDT.getCd());
            Collection<QuestionRequiredNnd>  notifReqColl;

            notifReqColl = customNbsQuestionRepository.retrieveQuestionRequiredNnd(investigationFormCd);

            if(notifReqColl != null && notifReqColl.size() > 0) {
                for (QuestionRequiredNnd questionRequiredNnd : notifReqColl) {
                    NbsQuestionMetadata metaData = new NbsQuestionMetadata(questionRequiredNnd);
                    subMap.put(metaData.getNbsQuestionUid(), metaData);
                }
            }

            Map<?,?> result;
            result= validatePAMNotficationRequiredFieldsGivenPageProxy(pageObj, publicHealthCaseUid, subMap,investigationFormCd);
            StringBuilder errorText =new StringBuilder(20);
            if(result!=null && result.size()>0){
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
            Long realNotificationUid = setNotificationProxy(notificationProxyVO);
            eDXActivityDetailLogDT.setRecordId(String.valueOf(realNotificationUid));
            if (!formatErr)
            {
                eDXActivityDetailLogDT.setComment("Notification created (UID: "+realNotificationUid+")");
            }

        } catch (Exception e) {
            e.printStackTrace();
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
    protected Map<Object, Object> validatePAMNotficationRequiredFieldsGivenPageProxy(Object pageObj, Long publicHealthCaseUid,
                                                                                  Map<Object, Object>  reqFields, String formCd) throws DataProcessingException {

        Map<Object, Object>  missingFields = new TreeMap<>();

        BasePamContainer pamVO;
        Collection<ParticipationDto> participationDTCollection;
        PublicHealthCaseDto publicHealthCaseDto;
        Collection<PersonContainer> personVOCollection;
        Map<Object, Object>  answerMap;
        Collection<ActIdDto>  actIdColl;

        if(formCd.equalsIgnoreCase(NEDSSConstant.INV_FORM_RVCT)||formCd.equalsIgnoreCase(NEDSSConstant.INV_FORM_VAR))
        {
            PamProxyContainer proxyVO = new PamProxyContainer();
            if(pageObj == null || pageObj instanceof PublicHealthCaseContainer)
            {
                // proxyVO =  pamproxy.getPamProxy(publicHealthCaseUid);
            }
            else
            {
                proxyVO = (PamProxyContainer) pageObj;
            }
            pamVO = proxyVO.getPamVO();
            answerMap = pamVO.getPamAnswerDTMap();
            if(pageObj == null || pageObj instanceof PublicHealthCaseContainer)
            {
                participationDTCollection = new ArrayList<>();
            }
            else
            {
                participationDTCollection = proxyVO.getTheParticipationDTCollection();
            }
            personVOCollection  = proxyVO.getThePersonVOCollection();
            publicHealthCaseDto = proxyVO.getPublicHealthCaseContainer().getThePublicHealthCaseDto();
            actIdColl = proxyVO.getPublicHealthCaseContainer().getTheActIdDTCollection();
        }
        else
        {
            // HIT THIS
            PageActProxyContainer pageProxyVO;
            if(pageObj == null  || pageObj instanceof PublicHealthCaseContainer)
            {
                pageProxyVO =  investigationService.getPageProxyVO(NEDSSConstant.CASE, publicHealthCaseUid);
            }
            else
            {
                pageProxyVO = (PageActProxyContainer) pageObj;
            }
            PageActProxyContainer pageActProxyContainer =pageProxyVO;
            pamVO= pageActProxyContainer.getPageVO();

            answerMap = (pageProxyVO).getPageVO().getPamAnswerDTMap();
            if(pageObj == null || pageObj instanceof PublicHealthCaseContainer)
            {
                participationDTCollection  = pageActProxyContainer.getPublicHealthCaseContainer().getTheParticipationDTCollection();
            }
            else
            {
                participationDTCollection = pageActProxyContainer.getTheParticipationDtoCollection();
            }
            personVOCollection  = pageActProxyContainer.getThePersonContainerCollection();
            publicHealthCaseDto = pageActProxyContainer.getPublicHealthCaseContainer().getThePublicHealthCaseDto();
            actIdColl = pageActProxyContainer.getPublicHealthCaseContainer().getTheActIdDTCollection();
        }


        PersonContainer personVO = getPersonVO(NEDSSConstant.PHC_PATIENT, participationDTCollection,personVOCollection );
        PersonDto personDT = new PersonDto();
        if (personVO != null) {
            personDT = personVO.getThePersonDto();
        }


        String programAreaCode = publicHealthCaseDto.getProgAreaCd();
        String jurisdictionCode = publicHealthCaseDto.getJurisdictionCd();
        String shared = publicHealthCaseDto.getSharedInd();

        try {
            for (Object o : reqFields.keySet()) {
                Long key = (Long) o;
                NbsQuestionMetadata metaData = (NbsQuestionMetadata) reqFields.get(key);
                String dLocation = metaData.getDataLocation() == null ? "" : metaData.getDataLocation();
                String label = metaData.getQuestionLabel() == null ? "" : metaData.getQuestionLabel();
                Long nbsQueUid = metaData.getNbsQuestionUid();
                if (!dLocation.equals("")) {
                    if (dLocation.startsWith("NBS_Answer.") && answerMap != null) {
                        if (answerMap.get(key) == null) {
                            missingFields.put(metaData.getQuestionIdentifier(), metaData.getQuestionLabel());
                        }
                    }
                    else if (dLocation.toLowerCase().startsWith("public_health_case.")) {
                        String attrToChk = dLocation.substring(dLocation.indexOf(".") + 1);

                        String getterNm = createGetterMethod(attrToChk);
                        Map<Object, Object> methodMap = getMethods(publicHealthCaseDto.getClass());
                        Method method = (Method) methodMap.get(getterNm.toLowerCase());
                        Object obj = method.invoke(publicHealthCaseDto, (Object[]) null);
                        checkObject(obj, missingFields, metaData);
                    }
                    else if (dLocation.toLowerCase().startsWith("person."))
                    {
                        String attrToChk = dLocation.substring(dLocation.indexOf(".") + 1);
                        String getterNm = createGetterMethod(attrToChk);
                        Map<Object, Object> methodMap = getMethods(personDT.getClass());
                        Method method = (Method) methodMap.get(getterNm.toLowerCase());
                        Object obj = method.invoke(personDT, (Object[]) null);
                        checkObject(obj, missingFields, metaData);
                    }
                    else if (dLocation.toLowerCase().startsWith("postal_locator."))
                    {
                        String attrToChk = dLocation.substring(dLocation.indexOf(".") + 1);
                        String getterNm = createGetterMethod(attrToChk);
                        PostalLocatorDto postalLocator = new PostalLocatorDto();
                        Map<Object, Object> methodMap = getMethods(postalLocator.getClass());
                        Method method = (Method) methodMap.get(getterNm.toLowerCase());
                        if (personVO != null
                                && personVO.getTheEntityLocatorParticipationDtoCollection() != null
                                && personVO.getTheEntityLocatorParticipationDtoCollection().size() > 0) {
                            for (EntityLocatorParticipationDto elp : personVO.getTheEntityLocatorParticipationDtoCollection()) {
                                if (elp.getThePostalLocatorDto() != null) {
                                    //check if this is the correct entity locator to check
                                    if (elp.getUseCd() != null &&
                                            metaData.getDataUseCd() != null &&
                                            metaData.getDataUseCd().equalsIgnoreCase(elp.getUseCd())) {
                                        postalLocator = elp.getThePostalLocatorDto();
                                        Object obj = method.invoke(postalLocator, (Object[]) null);
                                        checkObject(obj, missingFields, metaData);
                                    }
                                } else if (elp.getClassCd() != null
                                        && elp.getClassCd().equals("PST")
                                        && elp.getTheTeleLocatorDto() == null) {
                                    checkObject(null, missingFields, metaData);
                                }
                            }
                        } else {
                            checkObject(null, missingFields, metaData);
                        }
                    }
                    else if (dLocation.toLowerCase().startsWith("person_race."))
                    {
                        String attrToChk = dLocation.substring(dLocation.indexOf(".") + 1);
                        String getterNm = createGetterMethod(attrToChk);
                        PersonRaceDto personRace = new PersonRaceDto();
                        Map<Object, Object> methodMap = getMethods(personRace.getClass());
                        Method method = (Method) methodMap.get(getterNm.toLowerCase());
                        if (personVO != null
                                && personVO.getThePersonRaceDtoCollection() != null
                                && personVO.getThePersonRaceDtoCollection().size() > 0) {
                            for (PersonRaceDto personRaceDto : personVO.getThePersonRaceDtoCollection()) {
                                personRace = personRaceDto;
                                Object obj = method.invoke(personRace, (Object[]) null);
                                checkObject(obj, missingFields, metaData);
                            }
                        } else {
                            checkObject(null, missingFields, metaData);
                        }
                    }
                    else if (dLocation.toLowerCase().startsWith("act_id."))
                    {
                        String attrToChk = dLocation.substring(dLocation.indexOf(".") + 1);
                        String getterNm = createGetterMethod(attrToChk);
                        if (actIdColl != null && actIdColl.size() > 0) {
                            for (ActIdDto adt : actIdColl) {
                                String typeCd = adt.getTypeCd() == null ? "" : adt.getTypeCd();
                                String value = adt.getRootExtensionTxt() == null ? "" : adt.getRootExtensionTxt();
                                if (typeCd.equalsIgnoreCase(NEDSSConstant.ACT_ID_STATE_TYPE_CD) && value.equals("") && (label.toLowerCase().contains(STATE_STR))) {
                                    Map<Object, Object> methodMap = getMethods(adt.getClass());
                                    Method method = (Method) methodMap.get(getterNm.toLowerCase());
                                    Object obj = method.invoke(adt, (Object[]) null);
                                    checkObject(obj, missingFields, metaData);
                                } else if (typeCd.equalsIgnoreCase(NEDSSConstant.ACT_ID_STATE_TYPE_CD)
                                        && formCd.equalsIgnoreCase(NEDSSConstant.INV_FORM_RVCT)
                                        && (label.toLowerCase().contains(STATE_STR))) {
                                    Map<Object, Object> methodMap = getMethods(adt.getClass());
                                    Method method = (Method) methodMap.get(getterNm.toLowerCase());
                                    Object obj = method.invoke(adt, (Object[]) null);
                                    checkObject(obj, missingFields, metaData);
                                } else if (typeCd.equalsIgnoreCase("CITY")
                                        && value.equals("") && (label.toLowerCase().contains("city"))) {
                                    Map<Object, Object> methodMap = getMethods(adt.getClass());
                                    Method method = (Method) methodMap.get(getterNm.toLowerCase());
                                    Object obj = method.invoke(adt, (Object[]) null);
                                    checkObject(obj, missingFields, metaData);
                                }
                            }
                        } else if (formCd.equalsIgnoreCase(NEDSSConstant.INV_FORM_RVCT)
                                && (label.toLowerCase().contains(STATE_STR))) {
                            missingFields.put(metaData.getQuestionIdentifier(), metaData.getQuestionLabel());
                        }
                    }
                    else if (dLocation.toLowerCase().startsWith("nbs_case_answer.")
                            && !(formCd.equalsIgnoreCase(NEDSSConstant.INV_FORM_RVCT)
                            || formCd.equalsIgnoreCase(NEDSSConstant.INV_FORM_VAR)))
                    {
                        if (answerMap == null || answerMap.size() == 0 || (answerMap.get(nbsQueUid) == null && answerMap.get(metaData.getQuestionIdentifier()) == null)) {
                            missingFields.put(metaData.getQuestionIdentifier(), metaData.getQuestionLabel());
                        }
                    }
                    else if (dLocation.toLowerCase().startsWith("nbs_case_answer.") && (formCd.equalsIgnoreCase(NEDSSConstant.INV_FORM_RVCT)))
                    {
                        if (answerMap == null || answerMap.size() == 0 || (answerMap.get(nbsQueUid) == null && answerMap.get(metaData.getQuestionIdentifier()) == null)) {
                            missingFields.put(metaData.getQuestionIdentifier(), metaData.getQuestionLabel());
                        }
                    }

                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }

        if (missingFields.size() == 0)
        {
            return null;
        }
        else
        {
            return missingFields;
        }
    }


    private String createGetterMethod(String attrToChk) {
        StringTokenizer tokenizer = new StringTokenizer(attrToChk,"_");
        StringBuilder methodName = new StringBuilder();
        while (tokenizer.hasMoreTokens()){
            String token = tokenizer.nextToken();
            methodName.append(Character.toUpperCase(token.charAt(0))).append(token.substring(1).toLowerCase());

        }
        return "get" + methodName;
    }

    @SuppressWarnings("java:S3740")
    private  Map<Object, Object>  getMethods(Class beanClass) {
        Method[] gettingMethods = beanClass.getMethods();
        Map<Object, Object>  resultMap = new HashMap<>();
        for (Method gettingMethod : gettingMethods) {
            String methodName = ( gettingMethod).getName().toLowerCase();
            resultMap.put(methodName,  gettingMethod);
        }
        return resultMap;
    }

    private void checkObject(Object obj,  Map<Object, Object>  missingFields, NbsQuestionMetadata metaData)  {
        String value = obj == null ? "" : obj.toString();
        if(value == null || (value != null && value.trim().length() == 0)) {
            missingFields.put(metaData.getQuestionIdentifier(), metaData.getQuestionLabel());
        }
    }
    @SuppressWarnings("java:S3776")
    private PersonContainer getPersonVO(String type_cd, Collection<ParticipationDto> participationDTCollection,
                                        Collection<PersonContainer> personVOCollection)  {
        ParticipationDto participationDT;
        PersonContainer personVO;
        if (participationDTCollection  != null) {
            Iterator<ParticipationDto> anIterator1;
            Iterator<PersonContainer> anIterator2 ;
            for (anIterator1 = participationDTCollection.iterator(); anIterator1.hasNext();) {
                participationDT =  anIterator1.next();
                if (participationDT.getTypeCd() != null && (participationDT.getTypeCd()).compareTo(type_cd) == 0) {
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


    private Long setNotificationProxy(NotificationProxyContainer notificationProxyVO) throws DataProcessingException
    {
        return notificationService.setNotificationProxy(notificationProxyVO);
    }

}
