package gov.cdc.dataprocessing.utilities.component.page_and_pam;

import gov.cdc.dataprocessing.cache.SrteCache;
import gov.cdc.dataprocessing.constant.MessageConstants;
import gov.cdc.dataprocessing.constant.elr.NBSBOLookup;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.base.BasePamContainer;
import gov.cdc.dataprocessing.model.container.model.*;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.edx.EDXEventProcessDto;
import gov.cdc.dataprocessing.model.dto.log.MessageLogDto;
import gov.cdc.dataprocessing.model.dto.lookup.DropDownCodeDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsActEntityDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsAnswerDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsCaseAnswerDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.CustomRepository;
import gov.cdc.dataprocessing.service.implementation.person.base.PatientMatchingBaseService;
import gov.cdc.dataprocessing.service.interfaces.page_and_pam.IPamService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IInvestigationService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IPublicHealthCaseService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IRetrieveSummaryService;
import gov.cdc.dataprocessing.service.interfaces.uid_generator.IUidService;
import gov.cdc.dataprocessing.service.model.action.PageActPatient;
import gov.cdc.dataprocessing.service.model.action.PageActPhc;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.act.ActRelationshipRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.edx.EdxEventProcessRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import gov.cdc.dataprocessing.utilities.component.nbs.NbsDocumentRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.nbs.NbsNoteRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.participation.ParticipationRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.*;

@Component
public class PageRepositoryUtil {

    private final IInvestigationService investigationService;
    private  final PatientRepositoryUtil patientRepositoryUtil;
    private final IUidService uidService;
    private final PamRepositoryUtil pamRepositoryUtil;
    private final PrepareAssocModelHelper prepareAssocModelHelper;
    private final IPublicHealthCaseService publicHealthCaseService;
    private final IRetrieveSummaryService retrieveSummaryService;
    private final ActRelationshipRepositoryUtil actRelationshipRepositoryUtil;
    private final EdxEventProcessRepositoryUtil edxEventProcessRepositoryUtil;
    private final NbsDocumentRepositoryUtil nbsDocumentRepositoryUtil;
    private final ParticipationRepositoryUtil participationRepositoryUtil;
    private final NbsNoteRepositoryUtil nbsNoteRepositoryUtil;
    private final CustomRepository  customRepository;
    private final IPamService pamService;
    private final PatientMatchingBaseService patientMatchingBaseService;

    private static final Logger logger = LoggerFactory.getLogger(PageRepositoryUtil.class);

    public PageRepositoryUtil(IInvestigationService investigationService,
                              PatientRepositoryUtil patientRepositoryUtil,
                              IUidService uidService, PamRepositoryUtil pamRepositoryUtil,
                              PrepareAssocModelHelper prepareAssocModelHelper, IPublicHealthCaseService publicHealthCaseService,
                              IRetrieveSummaryService retrieveSummaryService,
                              ActRelationshipRepositoryUtil actRelationshipRepositoryUtil,
                              EdxEventProcessRepositoryUtil edxEventProcessRepositoryUtil,
                              NbsDocumentRepositoryUtil nbsDocumentRepositoryUtil,
                              ParticipationRepositoryUtil participationRepositoryUtil,
                              NbsNoteRepositoryUtil nbsNoteRepositoryUtil, CustomRepository customRepository, IPamService pamService,
                              PatientMatchingBaseService patientMatchingBaseService) {
        this.investigationService = investigationService;
        this.patientRepositoryUtil = patientRepositoryUtil;
        this.uidService = uidService;
        this.pamRepositoryUtil = pamRepositoryUtil;
        this.prepareAssocModelHelper = prepareAssocModelHelper;
        this.publicHealthCaseService = publicHealthCaseService;
        this.retrieveSummaryService = retrieveSummaryService;
        this.actRelationshipRepositoryUtil = actRelationshipRepositoryUtil;
        this.edxEventProcessRepositoryUtil = edxEventProcessRepositoryUtil;
        this.nbsDocumentRepositoryUtil = nbsDocumentRepositoryUtil;
        this.participationRepositoryUtil = participationRepositoryUtil;
        this.nbsNoteRepositoryUtil = nbsNoteRepositoryUtil;
        this.customRepository = customRepository;
        this.pamService = pamService;
        this.patientMatchingBaseService = patientMatchingBaseService;
    }

    @SuppressWarnings("java:S6541")
    public Long setPageActProxyVO(PageActProxyContainer pageProxyVO) throws DataProcessingException {
        try {
            PageActProxyContainer pageActProxyContainer = pageProxyVO;
            PublicHealthCaseDto phcDT = pageActProxyContainer.getPublicHealthCaseContainer().getThePublicHealthCaseDto();
            boolean isCoInfectionCondition = pageActProxyContainer.getPublicHealthCaseContainer().isCoinfectionCondition();
            Long mprUid;

            // if both are false throw exception
            if ((!pageActProxyContainer.isItNew()) && (!pageActProxyContainer.isItDirty())) {
                throw new DataProcessingException("pageProxyVO.isItNew() = " + pageActProxyContainer.isItNew() + " and pageProxyVO.isItDirty() = " + pageActProxyContainer.isItDirty() + " for setPageProxy");
            }


            if (pageActProxyContainer.isItDirty() && !pageActProxyContainer.isConversionHasModified())
            {
                try {
                    // update auto resend notifications
                    investigationService.updateAutoResendNotificationsAsync(pageActProxyContainer);
                } catch (Exception e) {
                    //TODO: LOGGING NND LOG
//                    NNDActivityLogDto nndActivityLogDT = new NNDActivityLogDto();
//                    String phcLocalId = pageActProxyContainer.getPublicHealthCaseContainer()
//                            .getThePublicHealthCaseDto().getLocalId();
//                    nndActivityLogDT.setErrorMessageTxt(e.toString());
//                    if (phcLocalId != null)
//                        nndActivityLogDT.setLocalId(phcLocalId);
//                    else
//                        nndActivityLogDT.setLocalId("N/A");
//                    // catch & store auto resend notifications exceptions in
//                    // NNDActivityLog table
//                    nndMessageSenderHelper.persistNNDActivityLog(nndActivityLogDT);
//                    e.printStackTrace();
                }
            }

            processingParticipationPatTypeForPageAct(pageActProxyContainer);

            Long actualUid;

            Long falsePublicHealthCaseUid ;

            try
            {
                Long patientRevisionUid;
                Long phcUid;

                var pageActPatient = processingPersonContainerForPageAct(pageActProxyContainer, phcDT);
                phcDT = pageActPatient.getPhcDT();
                mprUid = pageActPatient.getMprUid();
                patientRevisionUid = pageActPatient.getPatientRevisionUid();


                var pageActPhc = processingPhcContainerForPageAct(pageActProxyContainer, isCoInfectionCondition);
                falsePublicHealthCaseUid = pageActPhc.getFalsePublicHealthCaseUid();
                actualUid = pageActPhc.getActualUid();
                phcUid = pageActPhc.getPhcUid();


                //TODO: LOGGING
                if (pageActProxyContainer.getMessageLogDTMap() != null && !pageActProxyContainer.getMessageLogDTMap().isEmpty())
                {

                    Set<String> set = pageActProxyContainer.getMessageLogDTMap().keySet();
                    for (String key : set) {
                        if (key.contains(MessageConstants.DISPOSITION_SPECIFIED_KEY))
                        {
                            //Investigator of Named by contact will get message for Named by contact and contact's investigation id.
                            continue;
                        }
                        MessageLogDto messageLogDT = pageActProxyContainer.getMessageLogDTMap().get(key);

                        messageLogDT.setPersonUid(patientRevisionUid);
                        if (messageLogDT.getEventUid() != null && messageLogDT.getEventUid() > 0)
                        {
                            continue;
                        }
                        else
                        {
                            messageLogDT.setEventUid(phcUid);
                        }

                    }

                    try {
                        //TODO: Message Log
                        //messageLogDAOImpl.storeMessageLogDTCollection(pageActProxyContainer.getMessageLogDTMap().values());
                    } catch (Exception e) {
                        logger.error("Unable to store the Error message for = "
                                + falsePublicHealthCaseUid.intValue());
                    }
                }


                // this collection should only be populated in edit scenario, xz
                // defect 11861 (10/01/04)
                processingNotificationSummaryForPageAct(pageActProxyContainer, phcDT);


                Long docUid;
                docUid = processingPhcActRelationshipForPageAct(pageActProxyContainer);

                processingEventProcessForPageAct(pageActProxyContainer, phcUid);

                /*
                 * Updating the Document table
                 */
                // Getting the DocumentEJB reference
                processingNbsDocumentForPageAct(pageActProxyContainer, docUid);

                processingParticipationForPageAct(pageActProxyContainer);


                if( pageActProxyContainer.isUnsavedNote() && pageActProxyContainer.getNbsNoteDTColl()!=null && pageActProxyContainer.getNbsNoteDTColl().size()>0){
                    nbsNoteRepositoryUtil.storeNotes(actualUid, pageActProxyContainer.getNbsNoteDTColl());
                }

                if (pageActProxyContainer.getPageVO() != null && pageActProxyContainer.isItNew()) {
                    pamService.insertPamVO(pageActProxyContainer.getPageVO(), pageActProxyContainer.getPublicHealthCaseContainer());

                } else if (pageActProxyContainer.getPageVO() != null && pageActProxyContainer.isItDirty()) {
                    //pamRootDAO.editPamVO(pageActProxyContainer.getPageVO(), pageActProxyContainer.getPublicHealthCaseContainer()); //NOSONAR
                    logger.info("test");
                } else
                {
                    logger.error("There is error in setPageActProxyVO as pageProxyVO.getPageVO() is null");
                }

            }
            catch (Exception e)
            {
                throw new DataProcessingException("ActControllerEJB Create : "+e.getMessage(), e);
            }

            handlingCoInfectionAndContactDisposition(pageActProxyContainer, mprUid, actualUid);


            return actualUid;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }


    public void updatForConInfectionId(PageActProxyContainer pageActProxyContainer, Long mprUid, Long currentPhclUid) throws DataProcessingException {
        try{
            updateForConInfectionId(pageActProxyContainer, null, mprUid,  null, currentPhclUid, null, null);
        }catch (Exception ex) {
            throw new DataProcessingException(ex.getMessage(), ex);
        }
    }

    /**
     * @param pageActProxyContainer:  PageActProxyContainer that will update the other investigations that are part of co-infection group
     * @param mprUid: MPR UId for the cases tied to co-infection group
     * @param currentPhclUid: PHC_UID tied to pageActProxyContainer
     * @param coinfectionSummaryVOCollection - Used for Merge Investigation
     * @param coinfectionIdToUpdate - coinfectionId Used for Merge Investigation
     */
    public void updateForConInfectionId(PageActProxyContainer pageActProxyContainer, PageActProxyContainer supersededProxyVO, Long mprUid,
                                        Map<Object, Object> coInSupersededEpliLinkIdMap, Long currentPhclUid,
                                        Collection<Object> coinfectionSummaryVOCollection, String coinfectionIdToUpdate)
            throws DataProcessingException {
        try {
            String coninfectionId= pageActProxyContainer.getPublicHealthCaseContainer().getThePublicHealthCaseDto().getCoinfectionId();
            if(coinfectionSummaryVOCollection==null)
                coinfectionSummaryVOCollection = getInvListForCoInfectionId(mprUid,coninfectionId);


            PageActProxyContainer pageActProxyCopyVO = (PageActProxyContainer) pageActProxyContainer.deepCopy();
            Map<Object, Object> answermapMap =pageActProxyCopyVO.getPageVO().getPamAnswerDTMap(); //NOSONAR

            Map<Object, Object> repeatingAnswermapMap =pageActProxyCopyVO.getPageVO().getPageRepeatingAnswerDTMap(); //NOSONAR

            String investigationFormCd = SrteCache.investigationFormConditionCode.get(pageActProxyContainer.getPublicHealthCaseContainer().getThePublicHealthCaseDto().getCd());
            Map<Object, Object> mapFromQuestions = new HashMap<>();
            //Collection<Object> nbsQuestionUidCollection = getCoinfectionQuestionListForFormCd(investigationFormCd); //NOSONAR
            Collection<Object> nbsQuestionUidCollection = new ArrayList<>();
            Map<Object,Object> updatedValuesMap = new HashMap<>();

            Map<Object,Object> updateValueInOtherTablesMap = new HashMap<>(); // Map is to update values in other table then NBS_CASE_Answer

            if(nbsQuestionUidCollection!=null) {
//                for (Object o : nbsQuestionUidCollection) {
//                    DropDownCodeDto dropDownCodeDT = (DropDownCodeDto) o;
//                    mapFromQuestions.put(dropDownCodeDT.getKey(), dropDownCodeDT);
//
//                    if (dropDownCodeDT.getAltValue() != null && (dropDownCodeDT.getAltValue().contains("CASE_MANAGEMENT.")
//                            || dropDownCodeDT.getAltValue().contains("PERSON.")
//                            || dropDownCodeDT.getAltValue().contains("PUBLIC_HEALTH_CASE."))) {
//                        updateValueInOtherTablesMap.put(dropDownCodeDT.getKey(), dropDownCodeDT.getAltValue());
//                    } else {
//                        if (answermapMap.get(dropDownCodeDT.getKey()) != null) {
//                            updatedValuesMap.put(dropDownCodeDT.getKey(), answermapMap.get(dropDownCodeDT.getKey()));
//                        } else if (answermapMap.get(dropDownCodeDT.getLongKey()) != null) {
//                            updatedValuesMap.put(dropDownCodeDT.getKey(), answermapMap.get(dropDownCodeDT.getLongKey()));
//                        } else if ((repeatingAnswermapMap.get(String.valueOf(dropDownCodeDT.getLongKey())) != null || repeatingAnswermapMap.get(dropDownCodeDT.getLongKey()) != null)
//                                && updatedValuesMap.get(dropDownCodeDT.getLongKey()) == null) {
//                            ArrayList list = (ArrayList) repeatingAnswermapMap.get(dropDownCodeDT.getLongKey().toString());
//                            if (list == null)
//                                list = (ArrayList) repeatingAnswermapMap.get(dropDownCodeDT.getLongKey());
//
//                            if (list != null && list.size() > 0)
//                                updatedValuesMap.put(dropDownCodeDT.getKey(), list);
//                        } else {
//                            //if(dropDownCodeDT.getIntValue()==null) {
//                            dropDownCodeDT.setValue(NEDSSConstant.NO_BATCH_ENTRY);
//                            updatedValuesMap.put(dropDownCodeDT.getKey(), dropDownCodeDT);
//                            //	}else {
//                            //		updatedValuesMap.put(dropDownCodeDT.getKey(), dropDownCodeDT.getIntValue());
//                            //	}
//                        }
//                    }
//                }

            }
            if(coinfectionSummaryVOCollection!=null && coinfectionSummaryVOCollection.size()>0) {
                /**Update for closed/open cases that are part of any co-infection groups */
                for (Object o : coinfectionSummaryVOCollection) {
                    CoinfectionSummaryContainer coninfectionSummaryVO = (CoinfectionSummaryContainer) o;
                    if (coninfectionSummaryVO.getPublicHealthCaseUid().compareTo(currentPhclUid) != 0) {
                        if (coinfectionIdToUpdate != null) {//Merge Case investigation scenario
                            updateCoInfectionInvest(updatedValuesMap, mapFromQuestions, pageActProxyContainer, pageActProxyContainer.getPublicHealthCaseContainer(),
                                    supersededProxyVO.getPublicHealthCaseContainer(), coInSupersededEpliLinkIdMap,
                                    coninfectionSummaryVO, coinfectionIdToUpdate, updateValueInOtherTablesMap);
                            /**Update for closed/open cases that are part of any co-infection groups */
                        } else {
                            updateCoInfectionInvest(updatedValuesMap, mapFromQuestions, pageActProxyContainer, pageActProxyContainer.getPublicHealthCaseContainer(),
                                    null, null,
                                    coninfectionSummaryVO, null, updateValueInOtherTablesMap);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new  DataProcessingException(e.getMessage(), e);
        }
    }


    private ArrayList<Object> getInvListForCoInfectionId(Long mprUid,String coInfectionId) throws DataProcessingException {
        ArrayList<Object> coinfectionInvList;
        coinfectionInvList = customRepository.getInvListForCoInfectionId(mprUid, coInfectionId);
        return coinfectionInvList;
    }


    private  void updateCoInfectionInvest(Map<Object, Object> mappedCoInfectionQuestions, Map<Object, Object>  fromMapQuestions,
                                          PageActProxyContainer pageActProxyContainer, PublicHealthCaseContainer publicHealthCaseContainer,
                                          PublicHealthCaseContainer supersededPublicHealthCaseContainer,
                                          Map<Object, Object> coInSupersededEpliLinkIdMap,
                                          CoinfectionSummaryContainer coninfectionSummaryVO,
                                          String coinfectionIdToUpdate,
                                          Map<Object, Object> updateValueInOtherTablesMap)
            throws DataProcessingException {
        Long publicHealthCaseUid;
        try {
            String investigationFormCd = SrteCache.investigationFormConditionCode.get(coninfectionSummaryVO.getConditionCd());
            //Collection<Object> toNbsQuestionUidCollection = getCoinfectionQuestionListForFormCd(investigationFormCd); //NOSONAR
            Collection<Object> toNbsQuestionUidCollection = new ArrayList<>();
            publicHealthCaseUid=coninfectionSummaryVO.getPublicHealthCaseUid();
            java.util.Date dateTime = new java.util.Date();
            Timestamp lastChgTime = new Timestamp(dateTime.getTime());
            Long lastChgUserId= AuthUtil.authUser.getNedssEntryId();
            PageActProxyContainer proxyVO =  investigationService.getPageProxyVO(NEDSSConstant.CASE, publicHealthCaseUid);
            if (!proxyVO.getPublicHealthCaseContainer().getThePublicHealthCaseDto().getInvestigationStatusCd().equalsIgnoreCase(NEDSSConstant.STATUS_OPEN)){
            }
            else{
                /*
                BasePamContainer pageVO = proxyVO.getPageVO();
                if(pageVO.getPamAnswerDTMap()!=null && toNbsQuestionUidCollection!=null) {
                    Iterator<Object> nbsQuestionIterator = toNbsQuestionUidCollection.iterator();
                    String currentToQuestionKey = "";
                    while(nbsQuestionIterator.hasNext( )) {
                        try
                        {
                            DropDownCodeDto toDropDownCodeDT= (DropDownCodeDto)nbsQuestionIterator.next();
                            currentToQuestionKey = toDropDownCodeDT.getKey();
                            if(fromMapQuestions.get(toDropDownCodeDT.getKey())==null){
                                logger.warn("TO Metadata question details: question_identifier:-"+ toDropDownCodeDT.getKey()+ "\nwith question_group_seq_nbr:-"+toDropDownCodeDT.getIntValue()+ "\nwith nbs_question_uid:-"+toDropDownCodeDT.getLongKey());
                                logger.warn("AssociatedInvestigationUpdateUtil.updateCoInfectionInvest:coinfection investigation form code for the coinfection case is :"+investigationFormCd);
                                logger.warn("AssociatedInvestigationUpdateUtil.updateCoInfectionInvest:The mapped question is missing in the current investigation" );

                                continue;
                            }else {
                                DropDownCodeDto fromDropDownCodeDT = (DropDownCodeDto)fromMapQuestions.get(toDropDownCodeDT.getKey());
                                if(fromDropDownCodeDT.getIntValue()!=null && fromDropDownCodeDT.getValue().equals(NEDSSConstant.NO_BATCH_ENTRY)  && toDropDownCodeDT.getIntValue()!=null){
                                    logger.warn("TO Metadata question details: question_identifier:-"+ toDropDownCodeDT.getKey()+ "\nwith question_group_seq_nbr:-"+toDropDownCodeDT.getIntValue()+ "\nwith nbs_question_uid:-"+toDropDownCodeDT.getLongKey());
                                    logger.warn("From Metadata question details: question_identifier:-"+ fromDropDownCodeDT.getKey()+ "\nwith question_group_seq_nbr:-"+fromDropDownCodeDT.getIntValue()+ "\nwith nbs_question_uid:-"+fromDropDownCodeDT.getLongKey());
                                    logger.warn("AssociatedInvestigationUpdateUtil.updateCoInfectionInvest:coinfection investigation form code for the coinfection case is :"+investigationFormCd);
                                    logger.warn("AssociatedInvestigationUpdateUtil.updateCoInfectionInvestThe mapped question is a single select question in the current investigation, however the question is a batch question in the coinfection investigation. Hence ignored" );
                                    continue;
                                }else {

                                    logger.debug("TO Metadata question details: question_identifier:-"+ toDropDownCodeDT.getKey()+ "\nwith question_group_seq_nbr:-"+toDropDownCodeDT.getIntValue()+ "\nwith nbs_question_uid:-"+toDropDownCodeDT.getLongKey());
                                    logger.debug("From Metadata question details: question_identifier:-"+ fromDropDownCodeDT.getKey()+ "\nwith question_group_seq_nbr:-"+fromDropDownCodeDT.getIntValue()+ "\nwith nbs_question_uid:-"+fromDropDownCodeDT.getLongKey());
                                    logger.debug("AssociatedInvestigationUpdateUtil.updateCoInfectionInvest:coinfection investigation form code for the coinfection case is :"+investigationFormCd);
                                    logger.debug("AssociatedInvestigationUpdateUtil.updateCoInfectionInvest:The mapped question is being updated" );
                                    if(mappedCoInfectionQuestions.get(toDropDownCodeDT.getKey())!=null && toDropDownCodeDT.getLongKey()!=null) {
                                        if(toDropDownCodeDT.getIntValue()==null) {
                                            Object object = mappedCoInfectionQuestions.get(toDropDownCodeDT.getKey());
                                            if(object !=null && object instanceof DropDownCodeDto && (((DropDownCodeDto)object).getValue().equalsIgnoreCase(NEDSSConstant.NO_BATCH_ENTRY))){
                                                Object thisObj = pageVO.getPamAnswerDTMap().get(toDropDownCodeDT.getLongKey()) ;
                                                if(thisObj!=null && thisObj instanceof NbsCaseAnswerDto) {
                                                    NbsCaseAnswerDto nbsCaseAnswerDT=(NbsCaseAnswerDto)pageVO.getPamAnswerDTMap().get(toDropDownCodeDT.getLongKey()) ;
                                                    nbsCaseAnswerDT.setItDelete(true);
                                                    nbsCaseAnswerDT.setItNew(false);
                                                    nbsCaseAnswerDT.setItDirty(false);
                                                    pageVO.getPamAnswerDTMap().put(toDropDownCodeDT.getLongKey(), nbsCaseAnswerDT);
                                                } else if (thisObj != null && thisObj  instanceof ArrayList) { //multiSelect
                                                    ArrayList<?> aDTList = (ArrayList<?>) thisObj;
                                                    for (Object ansDT : aDTList)
                                                    {
                                                        if (ansDT instanceof NbsCaseAnswerDto) {
                                                            NbsCaseAnswerDto nbsCaseAnswerDT=(NbsCaseAnswerDto)ansDT;
                                                            nbsCaseAnswerDT.setItDelete(true);
                                                            nbsCaseAnswerDT.setItNew(false);
                                                            nbsCaseAnswerDT.setItDirty(false);
                                                        }
                                                    }
                                                    pageVO.getPamAnswerDTMap().put(toDropDownCodeDT.getLongKey(), aDTList); //multiSelect ArrayList
                                                } //multiSel
                                            }
                                            else if(pageVO.getPamAnswerDTMap().get(toDropDownCodeDT.getLongKey())==null) {
                                                Object thisObj = mappedCoInfectionQuestions.get(toDropDownCodeDT.getKey());
                                                if (thisObj !=null && thisObj instanceof NbsCaseAnswerDto) {
                                                    NbsCaseAnswerDto fromNbsCaseAnswerDT=(NbsCaseAnswerDto)mappedCoInfectionQuestions.get(toDropDownCodeDT.getKey());
                                                    fromNbsCaseAnswerDT.setAnswerTxt(fromNbsCaseAnswerDT.getAnswerTxt());
                                                    fromNbsCaseAnswerDT.setActUid(publicHealthCaseContainer.getThePublicHealthCaseDto().getPublicHealthCaseUid());
                                                    fromNbsCaseAnswerDT.setItDelete(false);
                                                    fromNbsCaseAnswerDT.setItNew(true);
                                                    fromNbsCaseAnswerDT.setItDirty(false);
                                                    pageVO.getPamAnswerDTMap().put(toDropDownCodeDT.getLongKey(), fromNbsCaseAnswerDT);
                                                } else if (thisObj != null && thisObj  instanceof ArrayList) { //multiSelect
                                                    ArrayList<?> aDTList = (ArrayList<?>) thisObj;
                                                    for (Object ansDT : aDTList)
                                                    {
                                                        if (ansDT instanceof NbsCaseAnswerDto) {
                                                            NbsCaseAnswerDto fromNbsCaseAnswerDT=(NbsCaseAnswerDto)ansDT;
                                                            fromNbsCaseAnswerDT.setActUid(publicHealthCaseContainer.getThePublicHealthCaseDto().getPublicHealthCaseUid());
                                                            fromNbsCaseAnswerDT.setItDelete(false);
                                                            fromNbsCaseAnswerDT.setItNew(true);
                                                            fromNbsCaseAnswerDT.setItDirty(false);
                                                        }
                                                    }
                                                    pageVO.getPamAnswerDTMap().put(toDropDownCodeDT.getLongKey(), aDTList); //multi select arrayList
                                                }
                                            }else if(pageVO.getPamAnswerDTMap().get(toDropDownCodeDT.getLongKey())!=null) {
                                                Object thisObj = mappedCoInfectionQuestions.get(toDropDownCodeDT.getKey());
                                                if(thisObj !=null && thisObj instanceof NbsCaseAnswerDto) {
                                                    NbsCaseAnswerDto fromNbsCaseAnswerDT=(NbsCaseAnswerDto)mappedCoInfectionQuestions.get(toDropDownCodeDT.getKey());
                                                    NbsCaseAnswerDto toNbsCaseAnswerDT=(NbsCaseAnswerDto)pageVO.getPamAnswerDTMap().get(toDropDownCodeDT.getLongKey()) ;
                                                    toNbsCaseAnswerDT.setAnswerTxt(fromNbsCaseAnswerDT.getAnswerTxt());
                                                    toNbsCaseAnswerDT.setItDelete(false);
                                                    toNbsCaseAnswerDT.setItNew(false);
                                                    toNbsCaseAnswerDT.setItDirty(true);
                                                    pageVO.getPamAnswerDTMap().put(toDropDownCodeDT.getLongKey(), toNbsCaseAnswerDT);
                                                } else if (thisObj != null && thisObj  instanceof ArrayList) { //multiSelect upd
                                                    ArrayList<?> aFromDTList = (ArrayList<?>) mappedCoInfectionQuestions.get(toDropDownCodeDT.getKey());
                                                    ArrayList<NbsCaseAnswerDto> aToDTList = (ArrayList<NbsCaseAnswerDto>) pageVO.getPamAnswerDTMap().get(toDropDownCodeDT.getLongKey()) ;
                                                    if (aToDTList == null)
                                                        aToDTList = new ArrayList<>();
                                                    int theLastSeq = 0;
                                                    for (Object fromAnsDT : aFromDTList) {
                                                        if (fromAnsDT instanceof NbsCaseAnswerDto) {
                                                            NbsCaseAnswerDto fromNbsCaseAnswerDT=(NbsCaseAnswerDto)fromAnsDT;
                                                            boolean isNotThere = true;//update seq or add new or del old
                                                            for (Object toAnsDT : aToDTList) {
                                                                NbsCaseAnswerDto toNbsCaseAnswerDT=(NbsCaseAnswerDto)toAnsDT;
                                                                if (toNbsCaseAnswerDT.getSeqNbr().intValue() == fromNbsCaseAnswerDT.getSeqNbr().intValue()) {
                                                                    isNotThere = false;
                                                                    toNbsCaseAnswerDT.setAnswerTxt(fromNbsCaseAnswerDT.getAnswerTxt());
                                                                    toNbsCaseAnswerDT.setItDelete(false);
                                                                    toNbsCaseAnswerDT.setItNew(false);
                                                                    toNbsCaseAnswerDT.setItDirty(true);
                                                                }
                                                            }
                                                            if (isNotThere) {
                                                                NbsCaseAnswerDto newCaseAnswerDT = fromNbsCaseAnswerDT;
                                                                newCaseAnswerDT.setActUid(publicHealthCaseContainer.getThePublicHealthCaseDto().getPublicHealthCaseUid());
                                                                newCaseAnswerDT.setItDelete(false);
                                                                newCaseAnswerDT.setItNew(true);
                                                                newCaseAnswerDT.setItDirty(false);
                                                                newCaseAnswerDT.setSeqNbr(fromNbsCaseAnswerDT.getSeqNbr());
                                                                aToDTList.add(newCaseAnswerDT);
                                                            }
                                                            if (fromNbsCaseAnswerDT.getSeqNbr() > theLastSeq)
                                                            {
                                                                theLastSeq = fromNbsCaseAnswerDT.getSeqNbr();
                                                            }
                                                        }
                                                    } //fromAnsDT iter
                                                    //check if any are past the last sequence number and need to be deleted
                                                    for (Object toAnsDT : aToDTList) {
                                                        NbsCaseAnswerDto toNbsCaseAnswerDT=(NbsCaseAnswerDto)toAnsDT;
                                                        if (toNbsCaseAnswerDT.getSeqNbr() > theLastSeq) {
                                                            toNbsCaseAnswerDT.setItDelete(true);
                                                            toNbsCaseAnswerDT.setItNew(false);
                                                            toNbsCaseAnswerDT.setItDirty(false);
                                                        }
                                                    }

                                                    pageVO.getPamAnswerDTMap().put(toDropDownCodeDT.getLongKey(), aToDTList);
                                                } //multisel upd
                                            }

                                        }else if(toDropDownCodeDT.getIntValue()!=null && toDropDownCodeDT.getIntValue() >0) {
                                            Object objectRef = mappedCoInfectionQuestions.get(toDropDownCodeDT.getKey());
                                            if(objectRef !=null && objectRef instanceof DropDownCodeDto && ((DropDownCodeDto)objectRef).getValue().equalsIgnoreCase(NEDSSConstant.NO_BATCH_ENTRY)){
                                                ArrayList<NbsCaseAnswerDto> list=(ArrayList<NbsCaseAnswerDto>)pageVO.getPageRepeatingAnswerDTMap().get(toDropDownCodeDT.getLongKey()) ;

                                                list=changeStatus(list, proxyVO.getPublicHealthCaseContainer().getThePublicHealthCaseDto().getPublicHealthCaseUid(),false, false,true,lastChgUserId,lastChgTime);
                                                pageVO.getPageRepeatingAnswerDTMap().put(toDropDownCodeDT.getLongKey(), list);

                                            }
                                            else if(pageVO.getPageRepeatingAnswerDTMap().get(toDropDownCodeDT.getLongKey())==null) {

                                                ArrayList<NbsCaseAnswerDto> list=(ArrayList<NbsCaseAnswerDto>)mappedCoInfectionQuestions.get(toDropDownCodeDT.getKey());
                                                list=changeStatus(list, proxyVO.getPublicHealthCaseContainer().getThePublicHealthCaseDto().getPublicHealthCaseUid(),true, false,false,lastChgUserId,lastChgTime);

                                                pageVO.getPageRepeatingAnswerDTMap().put(toDropDownCodeDT.getLongKey(), list);

                                            }else if(pageVO.getPageRepeatingAnswerDTMap().get(toDropDownCodeDT.getLongKey())!=null) {
                                                ArrayList<NbsCaseAnswerDto> deleteList=(ArrayList<NbsCaseAnswerDto>)pageVO.getPageRepeatingAnswerDTMap().get(toDropDownCodeDT.getLongKey());
                                                deleteList=changeStatus(deleteList, proxyVO.getPublicHealthCaseContainer().getThePublicHealthCaseDto().getPublicHealthCaseUid(),false, false,true,lastChgUserId,lastChgTime);

                                                ArrayList<NbsCaseAnswerDto> list=(ArrayList<NbsCaseAnswerDto>)mappedCoInfectionQuestions.get(toDropDownCodeDT.getKey());
                                                list=changeStatus(list, proxyVO.getPublicHealthCaseContainer().getThePublicHealthCaseDto().getPublicHealthCaseUid(),true, false,false,lastChgUserId,lastChgTime);

                                                deleteList.addAll(list);

                                                pageVO.getPageRepeatingAnswerDTMap().put(toDropDownCodeDT.getLongKey(), deleteList);
                                            }
                                        }else {
                                            logger.error("\n\nPLEASE check!!!TO Metadata question details: question_identifier:-"+ toDropDownCodeDT.getKey()+ "\nwith question_group_seq_nbr:-"+toDropDownCodeDT.getIntValue()+ "\nwith nbs_question_uid:-"+toDropDownCodeDT.getLongKey());
                                            logger.error("PLEASE check!!!From Metadata question details: question_identifier:-"+ fromDropDownCodeDT.getKey()+ "\nwith question_group_seq_nbr:-"+fromDropDownCodeDT.getIntValue()+ "\nwith nbs_question_uid:-"+fromDropDownCodeDT.getLongKey());
                                            logger.error("PLEASE check!!!AssociatedInvestigationUpdateUtil.updateCoInfectionInvest:coinfection investigation form code for the coinfection case is :"+investigationFormCd);
                                            logger.error("PLEASE check!!!AssociatedInvestigationUpdateUtil.updateCoInfectionInvest:The mapped question is being updated" );
                                        }

                                    }else if(mappedCoInfectionQuestions.get(toDropDownCodeDT.getKey())==null && toDropDownCodeDT.getLongKey()!=null) {
                                        logger.debug("TO Metadata question details: question_identifier:-"+ toDropDownCodeDT.getKey()+ "\nwith question_group_seq_nbr:-"+toDropDownCodeDT.getIntValue()+ "\nwith nbs_question_uid:-"+toDropDownCodeDT.getLongKey());
                                        logger.debug("From Metadata question details: question_identifier:-"+ fromDropDownCodeDT.getKey()+ "\nwith question_group_seq_nbr:-"+fromDropDownCodeDT.getIntValue()+ "\nwith nbs_question_uid:-"+fromDropDownCodeDT.getLongKey());
                                        logger.debug("AssociatedInvestigationUpdateUtil.updateCoInfectionInvest:coinfection investigation form code for the coinfection case is :"+investigationFormCd);
                                        logger.debug("AssociatedInvestigationUpdateUtil.updateCoInfectionInvest:current investigation does not have that question, however the coninfection PHC case has question. Hence ignored" );

                                        continue;
                                    }else if(mappedCoInfectionQuestions.get(toDropDownCodeDT.getKey())!=null && toDropDownCodeDT.getLongKey()==null) {
                                        logger.debug("TO Metadata question details: question_identifier:-"+ toDropDownCodeDT.getKey()+ "\nwith question_group_seq_nbr:-"+toDropDownCodeDT.getIntValue()+ "\nwith nbs_question_uid:-"+toDropDownCodeDT.getLongKey());
                                        logger.debug("From Metadata question details: question_identifier:-"+ fromDropDownCodeDT.getKey()+ "\nwith question_group_seq_nbr:-"+fromDropDownCodeDT.getIntValue()+ "\nwith nbs_question_uid:-"+fromDropDownCodeDT.getLongKey());
                                        logger.debug("AssociatedInvestigationUpdateUtil.updateCoInfectionInvest:coinfection investigation form code for the coinfection case is :"+investigationFormCd);
                                        logger.debug("AssociatedInvestigationUpdateUtil.updateCoInfectionInvest:current investigation does have that question, however the coninfection PHC case does not has the same question. Hence ignored" );

                                        continue;
                                    }

                                }

                            }
                        }
                        catch (Exception e)
                        {
                            String errorMessage ="Error processing co-infection question " +currentToQuestionKey + " " + e.getCause()+ e.getMessage();
                        }


                    }

                }
                */
            }
            /**
             * Merge Investigation case issue where the superseded investigation should not allowed to update!!!
             * 1. Only cases that are not Merge Investigation are allowed to proceed
             * 2. Only cases that are Merge Investigation that are not superseded are allowed to proceed
             * 3. Even Closed cases that are part of co-infection are NOW allowed to proceed with updated co-infection id(https://nbsteamdev.atlassian.net/browse/ND-9114
             * 		Description Losing investigation's Coinfection is not assigned the correct Co-Infection Id when status = Closed)
             *
             */
            //Set the winning investigation's coinfectionId to losing investigation's related co-infection investigations.
            if(coinfectionIdToUpdate!=null){
                String survivingEpiLinkId = publicHealthCaseContainer.getTheCaseManagementDto().getEpiLinkId();
                String supersededEpiLinkId = supersededPublicHealthCaseContainer.getTheCaseManagementDto().getEpiLinkId();

                if(coInSupersededEpliLinkIdMap.get(proxyVO.getPublicHealthCaseContainer().getThePublicHealthCaseDto().getPublicHealthCaseUid()) !=null) {
                    proxyVO.getPublicHealthCaseContainer().getTheCaseManagementDto().setEpiLinkId(survivingEpiLinkId);
                }
                proxyVO.getPublicHealthCaseContainer().getThePublicHealthCaseDto().setCoinfectionId(coinfectionIdToUpdate);
                proxyVO.setMergeCase(true);
            }

            // Updates coinfection question's values in tables other than NBS_Case_Answer
            updateCoInfectionInvestForOtherTables(proxyVO, updateValueInOtherTablesMap, pageActProxyContainer, publicHealthCaseContainer);

            if(coinfectionIdToUpdate==null
                    || (supersededPublicHealthCaseContainer != null
                    && publicHealthCaseUid.compareTo(supersededPublicHealthCaseContainer.getThePublicHealthCaseDto().getPublicHealthCaseUid())!=0))
            {
                updatePageProxyVOInterface(proxyVO,lastChgTime,lastChgUserId);
                setPageActProxyVO( proxyVO);
                logger.debug("updateCoInfectionInvest method call completed for coinfectionIdToUpdate:"+ coinfectionIdToUpdate);
            }

        }catch(Exception e) {

            throw new DataProcessingException(e.toString() ,e);
        }
    }

    private ArrayList<NbsCaseAnswerDto> changeStatus(ArrayList<NbsCaseAnswerDto> list,Long publicHealthCaseUid,
                                                     boolean itNew, boolean itDirty, boolean itDelete,Long lastChgUserId, Timestamp lastChgTime){
        if(list!=null) {
            for (NbsCaseAnswerDto caseAnswerDT : list) {
                caseAnswerDT.setLastChgUserId(lastChgUserId);
                caseAnswerDT.setLastChgTime(lastChgTime);
                caseAnswerDT.setActUid(publicHealthCaseUid);
                caseAnswerDT.setItNew(itNew);
                caseAnswerDT.setItDirty(itDirty);
                caseAnswerDT.setItDelete(itDelete);
            }
        }
        return list;
    }


    private void updatePageProxyVOInterface(PageActProxyContainer proxyActVO, Timestamp lastChgTime, Long lastChgUserId) throws DataProcessingException {
        try {
            proxyActVO.setRenterant(true);


            proxyActVO.setItDirty(true);
            proxyActVO.getPublicHealthCaseContainer().setItDirty(true);
            proxyActVO.getPublicHealthCaseContainer().getThePublicHealthCaseDto().setLastChgTime(lastChgTime);
            proxyActVO.getPublicHealthCaseContainer().getThePublicHealthCaseDto().setLastChgUserId((lastChgUserId));
            proxyActVO.getPublicHealthCaseContainer().getThePublicHealthCaseDto().setItDirty(true);

            if (proxyActVO.getThePersonContainerCollection() != null) {
                for (PersonContainer personVO : proxyActVO.getThePersonContainerCollection()) {
                    if (personVO.getThePersonDto().getCd().equals(NEDSSConstant.PAT)) {
                        personVO.getThePersonDto().setLastChgTime(lastChgTime);
                        personVO.getThePersonDto().setLastChgUserId(lastChgUserId);
                        personVO.getThePersonDto().setItDirty(true);
                        personVO.getThePersonDto().setItNew(false);

                    }
                }

                if (proxyActVO.getPageVO() != null) {
                    Map<Object, Object> map = proxyActVO.getPageVO().getPamAnswerDTMap();
                    if(map!=null) {
                        updateNbsCaseAnswerInterfaceValues(map, lastChgTime, lastChgUserId);
                    }
                    Map<Object, Object> repeatingMap = proxyActVO.getPageVO().getPageRepeatingAnswerDTMap();
                    if(repeatingMap!=null) {
                        updateNbsCaseAnswerInterfaceValues(repeatingMap, lastChgTime, lastChgUserId);
                    }
                    if(proxyActVO.getPageVO().getActEntityDTCollection()!=null) {
                        for (NbsActEntityDto actEntityDT : proxyActVO.getPageVO().getActEntityDTCollection()) {
                            actEntityDT.setLastChgTime(lastChgTime);
                            actEntityDT.setLastChgUserId(lastChgUserId);
                            actEntityDT.setItDirty(true);
                            actEntityDT.setItNew(false);
                        }

                    }
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.toString());
        }
    }


    private Map<Object, Object> updateNbsCaseAnswerInterfaceValues(
            Map<Object, Object> map, Timestamp lastChgTime, Long lastChgUserId) throws DataProcessingException {
        Map<Object, Object> returnMap = new HashMap<>();

        try {
            for (Object key : map.keySet()) {
                Object object = map.get(key);
                if (object instanceof NbsCaseAnswerDto) {
                    NbsCaseAnswerDto caseAnswerDT = (NbsCaseAnswerDto) object;
                    caseAnswerDT.setLastChgTime(lastChgTime);
                    caseAnswerDT.setLastChgUserId(lastChgUserId);
                    //caseAnswerDT.setItDirty(true);
                    //caseAnswerDT.setItNew(false);
                    if (!caseAnswerDT.isItDelete() && !caseAnswerDT.isItDirty() && !caseAnswerDT.isItNew()) {
                        caseAnswerDT.setItDirty(true);
                        caseAnswerDT.setItNew(false);
                    }
                    returnMap.put(key, caseAnswerDT);
                } else if (object instanceof ArrayList) {
                    @SuppressWarnings("unchecked")
                    ArrayList<Object> list = (ArrayList<Object>) object;
                    ArrayList<NbsAnswerDto> returnList = new ArrayList<>();
                    for (Object o : list) {
                        NbsCaseAnswerDto caseAnswerDT = (NbsCaseAnswerDto) o;
                        caseAnswerDT.setLastChgTime(lastChgTime);
                        caseAnswerDT.setLastChgUserId(lastChgUserId);
                        //caseAnswerDT.setItDirty(false);
                        //caseAnswerDT.setItNew(true);
                        returnList.add(caseAnswerDT);
                    }
                    returnMap.put(key, returnList);
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.toString());
        }
        return returnMap;
    }


    /**
     *
     * Updates coinfection question's values in tables other than NBS_Case_Answer
     */
    private  void updateCoInfectionInvestForOtherTables(PageActProxyContainer pageActProxyVOofCoinfection,
                                                        Map<Object, Object> updateValueInOtherTablesMap,
                                                        PageActProxyContainer pageActProxyContainer,
                                                        PublicHealthCaseContainer publicHealthCaseContainer) throws DataProcessingException {
        try {
            for (Object key : updateValueInOtherTablesMap.keySet()) {
                String dbLocation = (String) updateValueInOtherTablesMap.get(key);
                if(dbLocation!=null && dbLocation.contains("PERSON.")){
                    //Commented out as its tries to update MPR concurrently within same transaction.
                    // First for current investigation's patient and then coinfection investigation's patient.
                }else if(dbLocation!=null && dbLocation.contains("CASE_MANAGEMENT.")){
//                    String columnName = dbLocation.substring(dbLocation.indexOf(".")+1,dbLocation.length());
//                    String getterMethod = DynamicBeanBinding.getGetterName(columnName);
//
//                    if(getterMethod!=null){
//                        String value = DynamicBeanBinding.getValueForMethod(publicHealthCaseContainer.getTheCaseManagementDto(),getterMethod,publicHealthCaseContainer.getTheCaseManagementDto().getClass().getName());
//
//                        if(value!=null){
//                            DynamicBeanBinding.populateBean(pageActProxyVOofCoinfection.getPublicHealthCaseContainer().getTheCaseManagementDto(), columnName, value);
//
//                            pageActProxyVOofCoinfection.getPublicHealthCaseContainer().getTheCaseManagementDto().setItDelete(true);
//                            pageActProxyVOofCoinfection.getPublicHealthCaseContainer().getTheCaseManagementDto().setItNew(false);
//                        }
//                    }else{
//                        logger.debug("getterMethod does not found from columnName: "+columnName +", not updating coinfection questions.");
//                    }
                }
            }

        }catch(Exception ex){
            throw new DataProcessingException(ex.toString());
        }
    }


    private void processingParticipationPatTypeForPageAct(PageActProxyContainer pageActProxyContainer) throws DataProcessingException {
        if (pageActProxyContainer.isItNew() && (!pageActProxyContainer.isItDirty()))
        {
            // changes according to new Analysis
            String classCd;
            String recordStatusCd;

            for (ParticipationDto participationDto : pageActProxyContainer.getTheParticipationDtoCollection())
            {

                if (participationDto.getSubjectEntityUid() != null && participationDto.getSubjectEntityUid().intValue() > 0)
                {
                    classCd = participationDto.getSubjectClassCd();
                    if (classCd != null && classCd.compareToIgnoreCase(NEDSSConstant.PERSON) == 0)
                    {
                        // Now, get PersonVO from Entity Controller and check if
                        // Person is active, if not throw
                        PersonContainer personVO = patientRepositoryUtil.loadPerson(participationDto.getSubjectEntityUid());
                        recordStatusCd = personVO.getThePersonDto().getRecordStatusCd();

                        if (recordStatusCd != null && recordStatusCd.trim().compareToIgnoreCase(NEDSSConstant.RECORD_STATUS_LOGICAL_DELETE) == 0) {
                            throw new DataProcessingException("The Person you are trying to create Investigation no Longer exists !!");
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("java:S6541")
    private PageActPatient processingPersonContainerForPageAct(PageActProxyContainer pageActProxyContainer,
                                                               PublicHealthCaseDto phcDT) throws DataProcessingException
    {
        PersonContainer personVO;
        Iterator<PersonContainer> anIterator;
        Long falseUid;
        Long realUid = null;
        Long mprUid;
        Long patientRevisionUid = null;
        PageActPatient pageActPatient = new PageActPatient();

        if (pageActProxyContainer.getThePersonContainerCollection() != null)
        {
            for (anIterator = pageActProxyContainer.getThePersonContainerCollection().iterator(); anIterator.hasNext();)
            {
                personVO = anIterator.next();
                if (personVO.getThePersonDto().getCd()!=null
                        && personVO.getThePersonDto().getCd().equals(NEDSSConstant.PAT))
                {
                    mprUid=personVO.getThePersonDto().getPersonParentUid();
                    pageActPatient.setMprUid(mprUid);
                }

                if (personVO.isItNew())
                {
                    if (personVO.getThePersonDto().getCd() != null && personVO.getThePersonDto().getCd().equals(NEDSSConstant.PAT))
                    {
                        // Patient
                        String businessTriggerCd = NEDSSConstant.PAT_CR;
                        try {
                            var fakeId = personVO.getThePersonDto().getPersonUid();
                            personVO.getThePersonDto().setPersonUid(personVO.getThePersonDto().getPersonParentUid());

                            patientRevisionUid= patientMatchingBaseService.setPatientRevision(personVO, businessTriggerCd, NEDSSConstant.PAT);
                            realUid = patientRevisionUid;
                            pageActPatient.setPatientRevisionUid(patientRevisionUid);
                            personVO.getThePersonDto().setPersonUid(fakeId);
                        } catch (Exception ex) {
                            throw new DataProcessingException("Error in entityController.setPatientRevision : " + ex.getMessage(), ex);
                        }
                    }
                    else if (personVO.getThePersonDto().getCd() != null && personVO.getThePersonDto().getCd().equals(NEDSSConstant.PRV))
                    {
                        // Provider
                        String businessTriggerCd = NEDSSConstant.PRV_CR;
                        try {
                            var data = patientRepositoryUtil.createPerson(personVO);
                            realUid = data.getPersonParentUid();
                        } catch (Exception ex) {
                            throw new DataProcessingException("Error in entityController.setProvider : " + ex.getMessage(), ex);
                        }

                    } // end of else if

                    falseUid = personVO.getThePersonDto().getPersonUid();

                    // replace the falseId with the realId
                    if (falseUid.intValue() < 0)
                    {
                        uidService.setFalseToNewForPageAct(pageActProxyContainer, falseUid, realUid);
                    }

                }
                else if (personVO.isItDirty())
                {
                    if (personVO.getThePersonDto().getCd() != null && personVO.getThePersonDto().getCd().equals(NEDSSConstant.PAT))
                    {
                        String businessTriggerCd = NEDSSConstant.PAT_EDIT;
                        try {
                            realUid = patientMatchingBaseService.setPatientRevision(personVO, businessTriggerCd, NEDSSConstant.PAT);
                            patientRevisionUid= realUid;
                            pageActPatient.setPatientRevisionUid(patientRevisionUid);
                        }  catch (Exception ex) {
                            throw new DataProcessingException("Error in entityController.setPatientRevision : " + ex.getMessage(), ex);
                        }
                    }
                    else if (personVO.getThePersonDto().getCd() != null && personVO.getThePersonDto().getCd().equals(NEDSSConstant.PRV))
                    {
                        String businessTriggerCd = NEDSSConstant.PRV_EDIT;
                        try {
                            patientRepositoryUtil.updateExistingPerson(personVO);
                            realUid = personVO.getThePersonDto().getPersonParentUid();
                        }  catch (Exception ex) {
                            throw new DataProcessingException("Error in entityController.setProvider : " + ex.getMessage(), ex);
                        }
                    }
                }
            }
            phcDT.setCurrentPatientUid(patientRevisionUid);
            pageActPatient.setPatientRevisionUid(patientRevisionUid);
            pageActPatient.setPhcDT(phcDT);
        }

        return pageActPatient;
    }
    @SuppressWarnings("java:S3457")
    private PageActPhc processingPhcContainerForPageAct(
            PageActProxyContainer pageActProxyContainer,
            boolean isCoInfectionCondition) throws DataProcessingException
    {
        PageActPhc pageActPhc = new PageActPhc();
        Long falsePublicHealthCaseUid = null;
        Long actualUid = null;
        Long phcUid = null;
        if (pageActProxyContainer.getPublicHealthCaseContainer() != null)
        {
            String businessTriggerCd = null;
            PublicHealthCaseContainer publicHealthCaseContainer = pageActProxyContainer.getPublicHealthCaseContainer();
            publicHealthCaseContainer.getThePublicHealthCaseDto().setPageCase(true);
            if(pageActProxyContainer.isItDirty())
            {
                pamRepositoryUtil.getPamHistory(pageActProxyContainer.getPublicHealthCaseContainer());
            }
            PublicHealthCaseDto publicHealthCaseDto = publicHealthCaseContainer.getThePublicHealthCaseDto();
            if(publicHealthCaseContainer.getNbsAnswerCollection()!=null)
            {
                logger.debug("********#publicHealthCaseContainer.getNbsAnswerCollection() size from history table: "+ publicHealthCaseContainer.getNbsAnswerCollection().size());
            }
            if(publicHealthCaseDto.getPublicHealthCaseUid()!=null && publicHealthCaseDto.getVersionCtrlNbr()!=null)
            {
                logger.debug("********#Public Health Case Uid: "+ publicHealthCaseDto.getPublicHealthCaseUid() +" Version: "+ publicHealthCaseDto.getVersionCtrlNbr());
            }

            RootDtoInterface rootDTInterface = publicHealthCaseDto;
            String businessObjLookupName = NBSBOLookup.INVESTIGATION;
            if (pageActProxyContainer.isItNew())
            {
                businessTriggerCd = "INV_CR";
                if(isCoInfectionCondition && pageActProxyContainer.getPublicHealthCaseContainer().getThePublicHealthCaseDto().getCoinfectionId()==null)
                {
                    logger.debug("AssociatedInvestigationUpdateUtil.updatForConInfectionId created an new coinfection id for the case");
                    pageActProxyContainer.getPublicHealthCaseContainer().getThePublicHealthCaseDto().setCoinfectionId(NEDSSConstant.COINFCTION_GROUP_ID_NEW_CODE);
                }
            }
            else if (pageActProxyContainer.isItDirty())
            {
                businessTriggerCd = "INV_EDIT";

            }
            String tableName = "PUBLIC_HEALTH_CASE";
            String moduleCd = "BASE";
            publicHealthCaseDto = (PublicHealthCaseDto) prepareAssocModelHelper.prepareVO(rootDTInterface, businessObjLookupName, businessTriggerCd, tableName, moduleCd, rootDTInterface.getVersionCtrlNbr());
            publicHealthCaseContainer.setThePublicHealthCaseDto(publicHealthCaseDto);

            falsePublicHealthCaseUid = publicHealthCaseContainer.getThePublicHealthCaseDto().getPublicHealthCaseUid();
            actualUid = publicHealthCaseService.setPublicHealthCase(publicHealthCaseContainer);
            phcUid= actualUid;
            logger.debug("actualUid.intValue() = " + actualUid.intValue());
            if (falsePublicHealthCaseUid.intValue() < 0)
            {
                logger.debug("falsePublicHealthCaseUid.intValue() = " + falsePublicHealthCaseUid.intValue());
                uidService.setFalseToNewForPageAct(pageActProxyContainer, falsePublicHealthCaseUid, actualUid);
                publicHealthCaseContainer.getThePublicHealthCaseDto().setPublicHealthCaseUid(actualUid);
            }

            logger.debug("falsePublicHealthCaseUid.intValue() = " + falsePublicHealthCaseUid.intValue());
        }

        pageActPhc.setPhcUid(phcUid);
        pageActPhc.setFalsePublicHealthCaseUid(falsePublicHealthCaseUid);
        pageActPhc.setActualUid(actualUid);
        return pageActPhc;
    }

    private Long processingPhcActRelationshipForPageAct(PageActProxyContainer pageActProxyContainer) throws DataProcessingException {
        Long docUid = null;
        Iterator<ActRelationshipDto> anIteratorActRelationship;
        if (pageActProxyContainer.getPublicHealthCaseContainer().getTheActRelationshipDTCollection() != null)
        {
            for (anIteratorActRelationship = pageActProxyContainer.getPublicHealthCaseContainer().getTheActRelationshipDTCollection().iterator(); anIteratorActRelationship.hasNext();)
            {
                ActRelationshipDto actRelationshipDT = anIteratorActRelationship.next();
                if (actRelationshipDT.getTypeCd() != null && actRelationshipDT.getTypeCd().equals(NEDSSConstant.DocToPHC))
                {
                    docUid = actRelationshipDT.getSourceActUid();
                }
                logger.debug("the actRelationshipDT statusTime is " + actRelationshipDT.getStatusTime());
                logger.debug("the actRelationshipDT statusCode is " + actRelationshipDT.getStatusCd());
                logger.debug("Got into The ActRelationship loop");

                try {
                    if (actRelationshipDT.isItDelete()) {
                        actRelationshipRepositoryUtil.insertActRelationshipHist(actRelationshipDT);
                    }
                    actRelationshipRepositoryUtil.storeActRelationship(actRelationshipDT);
                    logger.debug("Got into The ActRelationship, The ActUid is " + actRelationshipDT.getTargetActUid());
                } catch (Exception e) {
                    throw new DataProcessingException(e.getMessage());
                }
            }
        }

        return docUid;
    }

    private void processingParticipationForPageAct(PageActProxyContainer pageActProxyContainer) throws DataProcessingException {
        if (pageActProxyContainer.getTheParticipationDtoCollection() != null)
        {
            for (var item : pageActProxyContainer.getTheParticipationDtoCollection())
            {
                try {
                    if (item.isItDelete()) {
                        participationRepositoryUtil.insertParticipationHist(item);
                    }
                    participationRepositoryUtil.storeParticipation(item);
                } catch (Exception e) {
                    throw new DataProcessingException(e.getMessage());
                }
            }
        }
    }

    private void processingNotificationSummaryForPageAct(PageActProxyContainer pageActProxyContainer, PublicHealthCaseDto phcDT) throws DataProcessingException {
        if (pageActProxyContainer.getTheNotificationSummaryVOCollection() != null)
        {
            Collection<Object> notSumVOColl = pageActProxyContainer.getTheNotificationSummaryVOCollection();
            for (Object o : notSumVOColl) {
                NotificationSummaryContainer notSummaryVO = (NotificationSummaryContainer) o;
                // Only handles notifications that are not history and not
                // in auto-resend status.
                // for auto resend, it'll be handled separately. xz defect
                // 11861 (10/07/04)
                if (notSummaryVO.getIsHistory().equals("F") && notSummaryVO.getAutoResendInd().equals("F"))
                {
                    Long notificationUid = notSummaryVO.getNotificationUid();
                    String phcCd = phcDT.getCd();
                    String phcClassCd = phcDT.getCaseClassCd();
                    String progAreaCd = phcDT.getProgAreaCd();
                    String jurisdictionCd = phcDT.getJurisdictionCd();
                    String sharedInd = phcDT.getSharedInd();
                    String notificationRecordStatusCode = notSummaryVO.getRecordStatusCd();
                    if (notificationRecordStatusCode != null) {
                        String trigCd = null;

                        /*
                         * The notification status remains same when the
                         * Investigation or Associated objects are changed
                         */
                        if (notificationRecordStatusCode.equalsIgnoreCase(NEDSSConstant.APPROVED_STATUS))
                        {
                            trigCd = NEDSSConstant.NOT_CR_APR;
                        }

                        // change from pending approval to approved
                        if (notificationRecordStatusCode.equalsIgnoreCase(NEDSSConstant.PENDING_APPROVAL_STATUS))
                        {
                            trigCd = NEDSSConstant.NOT_CR_PEND_APR;
                        }

                        if (trigCd != null)
                        {
                            // we only need to update notification when
                            // trigCd is not null
                            retrieveSummaryService.updateNotification(
                                    notificationUid, trigCd, phcCd,
                                    phcClassCd, progAreaCd, jurisdictionCd,
                                    sharedInd);
                        }

                    }
                }
            }
        }
    }

    private void processingEventProcessForPageAct(PageActProxyContainer pageActProxyContainer, Long phcUid) throws DataProcessingException {
        if (pageActProxyContainer.getPublicHealthCaseContainer().getEdxEventProcessDtoCollection() != null)
        {
            for (EDXEventProcessDto processDT : pageActProxyContainer.getPublicHealthCaseContainer().getEdxEventProcessDtoCollection())
            {
                if(processDT.getDocEventTypeCd()!=null && processDT.getDocEventTypeCd().equals(NEDSSConstant.CASE))
                {
                    processDT.setNbsEventUid(phcUid);
                }
                edxEventProcessRepositoryUtil.insertEventProcess(processDT);
                logger.debug("Inserted the event Process for sourceId: " + processDT.getSourceEventId());
            }
        }
    }

    private void processingNbsDocumentForPageAct(PageActProxyContainer pageActProxyContainer, Long docUid) throws DataProcessingException {
        if (docUid != null)
        {
            try {

                // get the
                NbsDocumentContainer nbsDocVO = nbsDocumentRepositoryUtil.getNBSDocumentWithoutActRelationship(docUid);
                if (nbsDocVO.getNbsDocumentDT()!= null
                        && (nbsDocVO.getNbsDocumentDT().getJurisdictionCd() == null
                        || nbsDocVO.getNbsDocumentDT().getJurisdictionCd().equals(""))
                )
                {
                    nbsDocVO.getNbsDocumentDT().setJurisdictionCd(pageActProxyContainer.getPublicHealthCaseContainer()
                            .getThePublicHealthCaseDto()
                            .getJurisdictionCd());
                }
                nbsDocumentRepositoryUtil.updateDocumentWithOutthePatient(nbsDocVO);
            }
            catch (Exception e) {
                throw new DataProcessingException(e.getMessage());
            }
        }

    }

    private  void handlingCoInfectionAndContactDisposition(PageActProxyContainer pageActProxyContainer, Long mprUid, Long actualUid) throws DataProcessingException {
        if( !pageActProxyContainer.isRenterant() && pageActProxyContainer.getPublicHealthCaseContainer().getThePublicHealthCaseDto().getCoinfectionId()!=null
                && !pageActProxyContainer.getPublicHealthCaseContainer().getThePublicHealthCaseDto().getCoinfectionId().equalsIgnoreCase(NEDSSConstant.COINFCTION_GROUP_ID_NEW_CODE) && mprUid!=null
                && !pageActProxyContainer.isMergeCase() && !NEDSSConstant.INVESTIGATION_STATUS_CODE_CLOSED.equals(pageActProxyContainer.getPublicHealthCaseContainer().getThePublicHealthCaseDto().getInvestigationStatusCd()))
        {
            updatForConInfectionId(pageActProxyContainer, mprUid, actualUid);
        }

        if(pageActProxyContainer.getPublicHealthCaseContainer().getTheCaseManagementDto()!=null) {
            //TODO: NBS STD OR HIV PROG
//                boolean isStdHivProgramAreaCode =PropertyUtil.isStdOrHivProgramArea(pageProxyVO.getPublicHealthCaseContainer().getThePublicHealthCaseDto().getProgAreaCd());
//                if(isStdHivProgramAreaCode)
//                {
//                    updateNamedAsContactDisposition(pageActProxyContainer.getPublicHealthCaseContainer().getTheCaseManagementDto());
//                }

        }
    }



}
