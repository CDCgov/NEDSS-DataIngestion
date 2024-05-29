package gov.cdc.dataprocessing.utilities.component;

import gov.cdc.dataprocessing.cache.SrteCache;
import gov.cdc.dataprocessing.constant.MessageConstants;
import gov.cdc.dataprocessing.constant.elr.NBSBOLookup;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.DropDownCodeDto;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.EDXEventProcessDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.PublicHealthCaseDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.PageActProxyVO;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.PublicHealthCaseVO;
import gov.cdc.dataprocessing.model.container.*;
import gov.cdc.dataprocessing.model.dto.NbsCaseAnswerDto;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.log.MessageLogDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsActEntityDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsAnswerDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.CustomRepository;
import gov.cdc.dataprocessing.service.implementation.person.base.PatientMatchingBaseService;
import gov.cdc.dataprocessing.service.interfaces.IInvestigationService;
import gov.cdc.dataprocessing.service.interfaces.IPamService;
import gov.cdc.dataprocessing.service.interfaces.IRetrieveSummaryService;
import gov.cdc.dataprocessing.service.interfaces.other.IUidService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IPublicHealthCaseService;
import gov.cdc.dataprocessing.service.model.action.PageActPatient;
import gov.cdc.dataprocessing.service.model.action.PageActPhc;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
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
    private final NbsNoteRepositoryUtil  nbsNoteRepositoryUtil;
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

    public Long setPageActProxyVO(PageActProxyVO pageProxyVO) throws DataProcessingException {
        Long phcPatientRevisionUid=null;
        try {
            PageActProxyVO pageActProxyVO = pageProxyVO;
            PublicHealthCaseDT phcDT = pageActProxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT();
            boolean isCoInfectionCondition =pageActProxyVO.getPublicHealthCaseVO().isCoinfectionCondition();
            Long mprUid;

            // if both are false throw exception
            if ((!pageActProxyVO.isItNew()) && (!pageActProxyVO.isItDirty())) {
                throw new DataProcessingException("pageProxyVO.isItNew() = " + pageActProxyVO.isItNew() + " and pageProxyVO.isItDirty() = " + pageActProxyVO.isItDirty() + " for setPageProxy");
            }


            /**
             * Permission checking -- most this crap is not implemented yet
             * */
            if (pageActProxyVO.isItNew()) {

                //TODO: PERM
//                boolean checkInvestigationAutoCreatePermission = nbsSecurityObj
//                        .getPermission(NBSBOLookup.INVESTIGATION,
//                                NBSOperationLookup.AUTOCREATE, phcDT
//                                        .getProgAreaCd(),
//                                ProgramAreaJurisdictionUtil.ANY_JURISDICTION, phcDT
//                                        .getSharedInd());
//
//                if (!nbsSecurityObj.getPermission(NBSBOLookup.INVESTIGATION,
//                        NBSOperationLookup.ADD, phcDT.getProgAreaCd(),
//                        ProgramAreaJurisdictionUtil.ANY_JURISDICTION, phcDT
//                                .getSharedInd())
//                        && !(checkInvestigationAutoCreatePermission)) {
//                    logger.info("no add permissions for setPageProxy");
//                    throw new NEDSSSystemException(
//                            "NO ADD PERMISSIONS for setPageProxy");
//                }
//                logger.info("user has add permissions for setPageProxy");
            }
            else if (pageActProxyVO.isItDirty()) {
                //TODO: PERM
//                if (!nbsSecurityObj.getPermission(NBSBOLookup.INVESTIGATION,
//                        NBSOperationLookup.EDIT, phcDT.getProgAreaCd(), phcDT
//                                .getJurisdictionCd(), phcDT.getSharedInd())) {
//                    logger.info("no edit permissions for setPageProxy");
//                    throw new NEDSSSystemException(
//                            "NO EDIT PERMISSIONS for setPageProxy");
//                }
            }


            if (pageActProxyVO.isItDirty() && !pageActProxyVO.isConversionHasModified())
            {
                try {
                    // update auto resend notifications
                    investigationService.updateAutoResendNotificationsAsync(pageActProxyVO);
                } catch (Exception e) {
                    //TODO: LOG NND LOG
//                    NNDActivityLogDto nndActivityLogDT = new NNDActivityLogDto();
//                    String phcLocalId = pageActProxyVO.getPublicHealthCaseVO()
//                            .getThePublicHealthCaseDT().getLocalId();
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

            processingParticipationPatTypeForPageAct(pageActProxyVO);

            Long actualUid;

            Long falsePublicHealthCaseUid ;

            try
            {
                Long patientRevisionUid;
                Long phcUid;

                var pageActPatient = processingPersonContainerForPageAct(pageActProxyVO, phcDT);
                phcDT = pageActPatient.getPhcDT();
                mprUid = pageActPatient.getMprUid();
                patientRevisionUid = pageActPatient.getPatientRevisionUid();


                var pageActPhc = processingPhcContainerForPageAct(pageActProxyVO, isCoInfectionCondition);
                falsePublicHealthCaseUid = pageActPhc.getFalsePublicHealthCaseUid();
                actualUid = pageActPhc.getActualUid();
                phcUid = pageActPhc.getPhcUid();


                //TODO: LOGGING
                if (pageActProxyVO.getMessageLogDTMap() != null && !pageActProxyVO.getMessageLogDTMap().isEmpty())
                {

                    Set<String> set = pageActProxyVO.getMessageLogDTMap().keySet();
                    for (String key : set) {
                        if (key.contains(MessageConstants.DISPOSITION_SPECIFIED_KEY))
                        {
                            //Investigator of Named by contact will get message for Named by contact and contact's investigation id.
                            continue;
                        }
                        MessageLogDto messageLogDT = pageActProxyVO.getMessageLogDTMap().get(key);

                        messageLogDT.setPersonUid(patientRevisionUid);
                        if (messageLogDT.getEventUid() != null && messageLogDT.getEventUid().longValue() > 0)
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
                        //messageLogDAOImpl.storeMessageLogDTCollection(pageActProxyVO.getMessageLogDTMap().values());
                    } catch (Exception e) {
                        logger.error("Unable to store the Error message for = "
                                + falsePublicHealthCaseUid.intValue());
                    }
                }


                // this collection should only be populated in edit scenario, xz
                // defect 11861 (10/01/04)
                processingNotificationSummaryForPageAct(pageActProxyVO, phcDT);


                Long docUid = null;
                docUid = processingPhcActRelationshipForPageAct(pageActProxyVO);

                processingEventProcessForPageAct(pageActProxyVO, phcUid);

                /*
                 * Updating the Document table
                 */
                // Getting the DocumentEJB reference
                processingNbsDocumentForPageAct(pageActProxyVO, docUid);

                processingParticipationForPageAct(pageActProxyVO);

                if( pageActProxyVO.isUnsavedNote() && pageActProxyVO.getNbsNoteDTColl()!=null && pageActProxyVO.getNbsNoteDTColl().size()>0){
                    nbsNoteRepositoryUtil.storeNotes(actualUid, pageActProxyVO.getNbsNoteDTColl());
                }

                //TODO: PAM
                if (pageActProxyVO.getPageVO() != null && pageActProxyVO.isItNew()) {
                    pamService.insertPamVO(pageActProxyVO.getPageVO(), pageActProxyVO.getPublicHealthCaseVO());

                } else if (pageActProxyVO.getPageVO() != null && pageActProxyVO.isItDirty()) {
//                    pamRootDAO.editPamVO(pageActProxyVO.getPageVO(), pageActProxyVO.getPublicHealthCaseVO());
                    logger.info("test");
                } else
                {
                    logger.error("There is error in setPageActProxyVO as pageProxyVO.getPageVO() is null");
                }


            }
            catch (Exception e)
            {
                throw new DataProcessingException("ActControllerEJB Create : "+e.getMessage() + e.toString());
            }

            handlingCoInfectionAndContactDisposition(pageActProxyVO, mprUid, actualUid);


            return actualUid;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }


    public void updatForConInfectionId(PageActProxyVO pageActProxyVO, Long mprUid, Long currentPhclUid) throws DataProcessingException {
        try{
            updateForConInfectionId(pageActProxyVO, null, mprUid,  null, currentPhclUid, null, null);
        }catch (Exception ex) {
            throw new DataProcessingException(ex.getMessage(), ex);
        }
    }

    /**
     * @param pageActProxyVO:  PageActProxyVO that will update the other investigations that are part of co-infection group
     * @param mprUid: MPR UId for the cases tied to co-infection group
     * @param currentPhclUid: PHC_UID tied to pageActProxyVO
     * @param coinfectionSummaryVOCollection - Used for Merge Investigation
     * @param coinfectionIdToUpdate - coinfectionId Used for Merge Investigation
     */
    public void updateForConInfectionId(PageActProxyVO pageActProxyVO, PageActProxyVO supersededProxyVO, Long mprUid,
                                        Map<Object, Object> coInSupersededEpliLinkIdMap, Long currentPhclUid,
                                        Collection<Object> coinfectionSummaryVOCollection, String coinfectionIdToUpdate)
            throws DataProcessingException {
        try {
            String coninfectionId=pageActProxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().getCoinfectionId();
            if(coinfectionSummaryVOCollection==null)
                coinfectionSummaryVOCollection = getInvListForCoInfectionId(mprUid,coninfectionId);


            PageActProxyVO pageActProxyCopyVO = (PageActProxyVO)pageActProxyVO.deepCopy();
            Map<Object, Object> answermapMap =pageActProxyCopyVO.getPageVO().getPamAnswerDTMap();

            Map<Object, Object> repeatingAnswermapMap =pageActProxyCopyVO.getPageVO().getPageRepeatingAnswerDTMap();

            String investigationFormCd = SrteCache.investigationFormConditionCode.get(pageActProxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().getCd());
            Map<Object, Object> mapFromQuestions = new HashMap<Object,Object>();
//           TODO CONINFECT FORM CD
//            Collection<Object> nbsQuestionUidCollection = getCoinfectionQuestionListForFormCd(investigationFormCd);
            Collection<Object> nbsQuestionUidCollection = new ArrayList<>();
            Map<Object,Object> updatedValuesMap = new HashMap<Object, Object>();

            Map<Object,Object> updateValueInOtherTablesMap = new HashMap<Object, Object>(); // Map is to update values in other table then NBS_CASE_Answer

            if(nbsQuestionUidCollection!=null) {
                Iterator<Object> iterator = nbsQuestionUidCollection.iterator();
                while(iterator.hasNext()) {
                    DropDownCodeDto dropDownCodeDT= (DropDownCodeDto)iterator.next();
                    mapFromQuestions.put(dropDownCodeDT.getKey(), dropDownCodeDT);

                    if(dropDownCodeDT.getAltValue()!=null && (dropDownCodeDT.getAltValue().contains("CASE_MANAGEMENT.")
                            || dropDownCodeDT.getAltValue().contains("PERSON.")
                            || dropDownCodeDT.getAltValue().contains("PUBLIC_HEALTH_CASE."))){
                        updateValueInOtherTablesMap.put(dropDownCodeDT.getKey(), dropDownCodeDT.getAltValue());
                    }else {
                        if(answermapMap.get(dropDownCodeDT.getKey())!=null) {
                            updatedValuesMap.put(dropDownCodeDT.getKey(), answermapMap.get(dropDownCodeDT.getKey()));
                        } else if(answermapMap.get(dropDownCodeDT.getLongKey())!=null) {
                            updatedValuesMap.put(dropDownCodeDT.getKey(), answermapMap.get(dropDownCodeDT.getLongKey()));
                        } else if((repeatingAnswermapMap.get(dropDownCodeDT.getLongKey()+"")!=null || repeatingAnswermapMap.get(dropDownCodeDT.getLongKey())!=null)
                                && updatedValuesMap.get(dropDownCodeDT.getLongKey())==null){
                            ArrayList list = (ArrayList)repeatingAnswermapMap.get(dropDownCodeDT.getLongKey().toString());
                            if(list == null)
                                list = (ArrayList)repeatingAnswermapMap.get(dropDownCodeDT.getLongKey());

                            if(list!=null && list.size()>0)
                                updatedValuesMap.put(dropDownCodeDT.getKey(), list);
                        }
                        else {
                            //if(dropDownCodeDT.getIntValue()==null) {
                            dropDownCodeDT.setValue( NEDSSConstant.NO_BATCH_ENTRY);
                            updatedValuesMap.put(dropDownCodeDT.getKey(), dropDownCodeDT);
                            //	}else {
                            //		updatedValuesMap.put(dropDownCodeDT.getKey(), dropDownCodeDT.getIntValue());
                            //	}
                        }
                    }
                }

            }
            if(coinfectionSummaryVOCollection!=null && coinfectionSummaryVOCollection.size()>0) {
                Iterator<Object> coinfectionsummIterator =coinfectionSummaryVOCollection.iterator();
                while(coinfectionsummIterator.hasNext()) {
                    CoinfectionSummaryContainer coninfectionSummaryVO= (CoinfectionSummaryContainer)coinfectionsummIterator.next();
                    if(coninfectionSummaryVO.getPublicHealthCaseUid().compareTo(currentPhclUid)!=0){
                        if(coinfectionIdToUpdate!=null){//Merge Case investigation scenario
                            updateCoInfectionInvest(updatedValuesMap, mapFromQuestions,pageActProxyVO, pageActProxyVO.getPublicHealthCaseVO(),
                                    supersededProxyVO.getPublicHealthCaseVO(), coInSupersededEpliLinkIdMap,
                                    coninfectionSummaryVO, coinfectionIdToUpdate, updateValueInOtherTablesMap);
                            /**Update for closed/open cases that are part of any co-infection groups */
                        }

                        else{
                            updateCoInfectionInvest(updatedValuesMap, mapFromQuestions,pageActProxyVO,  pageActProxyVO.getPublicHealthCaseVO(),
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
        ArrayList<Object> coinfectionInvList = new ArrayList<Object>();
        coinfectionInvList = customRepository.getInvListForCoInfectionId(mprUid, coInfectionId);
        return coinfectionInvList;
    }


    private  void updateCoInfectionInvest(Map<Object, Object> mappedCoInfectionQuestions,Map<Object, Object>  fromMapQuestions,
                                          PageActProxyVO pageActProxyVO ,PublicHealthCaseVO publicHealthCaseVO,
                                          PublicHealthCaseVO supersededPublicHealthCaseVO,
                                          Map<Object, Object> coInSupersededEpliLinkIdMap,
                                          CoinfectionSummaryContainer coninfectionSummaryVO,
                                          String coinfectionIdToUpdate,
                                          Map<Object, Object> updateValueInOtherTablesMap)
            throws DataProcessingException {
        Long publicHealthCaseUid =null;
        try {
            // TODO: CONINFECT FORM CD
            String investigationFormCd = SrteCache.investigationFormConditionCode.get(coninfectionSummaryVO.getConditionCd());
//            Collection<Object> toNbsQuestionUidCollection = getCoinfectionQuestionListForFormCd(investigationFormCd);

            Collection<Object> toNbsQuestionUidCollection = new ArrayList<>();
            publicHealthCaseUid=coninfectionSummaryVO.getPublicHealthCaseUid();
            java.util.Date dateTime = new java.util.Date();
            Timestamp lastChgTime = new Timestamp(dateTime.getTime());
            Long lastChgUserId= Long.valueOf(AuthUtil.authUser.getUserId());
            PageActProxyVO proxyVO = (PageActProxyVO) investigationService.getPageProxyVO(NEDSSConstant.CASE, publicHealthCaseUid);
            if (!proxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().getInvestigationStatusCd().equalsIgnoreCase(NEDSSConstant.STATUS_OPEN)){
            }
            else{
                BasePamContainer pageVO = proxyVO.getPageVO();
                if(pageVO.getPamAnswerDTMap()!=null && toNbsQuestionUidCollection!=null) {
                    Iterator<Object> nbsQuestionIterator = toNbsQuestionUidCollection.iterator();
                    String currentToQuestionKey = "";
                    while(nbsQuestionIterator.hasNext( )) {
                        try {
                            DropDownCodeDto toDropDownCodeDT= (DropDownCodeDto)nbsQuestionIterator.next();
                            currentToQuestionKey = toDropDownCodeDT.getKey();
                            if(fromMapQuestions.get(toDropDownCodeDT.getKey())==null){
                                logger.warn("TO Metadata question details: question_identifier:-"+ toDropDownCodeDT.getKey()+ "\nwith question_group_seq_nbr:-"+toDropDownCodeDT.getIntValue()+ "\nwith nbs_question_uid:-"+toDropDownCodeDT.getLongKey());
                                logger.warn("AssociatedInvestigationUpdateUtil.updateCoInfectionInvest:coinfection investigation form code for the coinfection case is :"+investigationFormCd);
                                logger.warn("AssociatedInvestigationUpdateUtil.updateCoInfectionInvest:The mapped question is missing in the current investigation" );

                                continue;
                            }else {
                                DropDownCodeDto fromDropDownCodeDT = (DropDownCodeDto)fromMapQuestions.get(toDropDownCodeDT.getKey());
                                /*if(fromDropDownCodeDT.getIntValue()!=null && toDropDownCodeDT.getIntValue()==null){
									logger.warn("TO Metadata question details: question_identifier:-"+ toDropDownCodeDT.getKey()+ "\nwith question_group_seq_nbr:-"+toDropDownCodeDT.getIntValue()+ "\nwith nbs_question_uid:-"+toDropDownCodeDT.getLongKey());
									logger.warn("AssociatedInvestigationUpdateUtil.updateCoInfectionInvest:coinfection investigation form code for the coinfection case is :"+investigationFormCd);
									logger.warn("AssociatedInvestigationUpdateUtil.updateCoInfectionInvest:The mapped question is a batch question in the current investigation, however the question is not a batch question in the coinfection investigation. Hence ignored" );
									continue;
								}else*/
                                if(fromDropDownCodeDT.getIntValue()!=null && fromDropDownCodeDT.getIntValue().equals(NEDSSConstant.NO_BATCH_ENTRY)  && toDropDownCodeDT.getIntValue()!=null){
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
                                        //	 if(pageVO.getPamAnswerDTMap().get(toDropDownCodeDT.getLongKey()) instanceof NbsCaseAnswerDT) {
                                        if(toDropDownCodeDT.getIntValue()==null) {
                                            Object object = mappedCoInfectionQuestions.get(toDropDownCodeDT.getKey());
                                            if(object !=null && object instanceof DropDownCodeDto && (((DropDownCodeDto)object).getValue().equalsIgnoreCase(NEDSSConstant.NO_BATCH_ENTRY))){
                                                //&& object.toString().equalsIgnoreCase(NEDSSConstant.DEL)) {
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
                                                    //NbsCaseAnswerDT nbsCaseAnswerDT=(NbsCaseAnswerDT)pageVO.getPamAnswerDTMap().get(toDropDownCodeDT.getLongKey()) ;
                                                    fromNbsCaseAnswerDT.setAnswerTxt(fromNbsCaseAnswerDT.getAnswerTxt());
                                                    fromNbsCaseAnswerDT.setActUid(publicHealthCaseVO.getThePublicHealthCaseDT().getPublicHealthCaseUid());
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
                                                            //fromNbsCaseAnswerDT.setAnswerTxt(fromNbsCaseAnswerDT.getAnswerTxt());
                                                            fromNbsCaseAnswerDT.setActUid(publicHealthCaseVO.getThePublicHealthCaseDT().getPublicHealthCaseUid());
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
                                                        aToDTList = new ArrayList<NbsCaseAnswerDto>();
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
                                                                newCaseAnswerDT.setActUid(publicHealthCaseVO.getThePublicHealthCaseDT().getPublicHealthCaseUid());
                                                                newCaseAnswerDT.setItDelete(false);
                                                                newCaseAnswerDT.setItNew(true);
                                                                newCaseAnswerDT.setItDirty(false);
                                                                newCaseAnswerDT.setSeqNbr(fromNbsCaseAnswerDT.getSeqNbr().intValue());
                                                                aToDTList.add(newCaseAnswerDT);
                                                            }
                                                            if (fromNbsCaseAnswerDT.getSeqNbr().intValue() > theLastSeq)
                                                                theLastSeq = fromNbsCaseAnswerDT.getSeqNbr().intValue();
                                                        }
                                                    } //fromAnsDT iter
                                                    //check if any are past the last sequence number and need to be deleted
                                                    for (Object toAnsDT : aToDTList) {
                                                        NbsCaseAnswerDto toNbsCaseAnswerDT=(NbsCaseAnswerDto)toAnsDT;
                                                        if (toNbsCaseAnswerDT.getSeqNbr().intValue() > theLastSeq) {
                                                            toNbsCaseAnswerDT.setItDelete(true);
                                                            toNbsCaseAnswerDT.setItNew(false);
                                                            toNbsCaseAnswerDT.setItDirty(false);
                                                        }
                                                    }

                                                    pageVO.getPamAnswerDTMap().put(toDropDownCodeDT.getLongKey(), aToDTList);
                                                } //multisel upd
                                            }

                                        }else if(toDropDownCodeDT.getIntValue()!=null && toDropDownCodeDT.getIntValue().intValue()>0) {
                                            Object objectRef = mappedCoInfectionQuestions.get(toDropDownCodeDT.getKey());
                                            if(objectRef !=null && objectRef instanceof DropDownCodeDto && ((DropDownCodeDto)objectRef).getValue().equalsIgnoreCase(NEDSSConstant.NO_BATCH_ENTRY)){
                                                ArrayList<NbsCaseAnswerDto> list=(ArrayList<NbsCaseAnswerDto>)pageVO.getPageRepeatingAnswerDTMap().get(toDropDownCodeDT.getLongKey()) ;
                                                //pageVO.getPageRepeatingAnswerDTMap().remove(toDropDownCodeDT.getLongKey());
                                                //pageVO.getPageRepeatingAnswerDTMap().put(toDropDownCodeDT.getLongKey().toString(), null);


                                                //ArrayList<?> list=(ArrayList<?>)mappedCoInfectionQuestions.get(toDropDownCodeDT.getKey());
                                                list=changeStatus(list, proxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().getPublicHealthCaseUid(),false, false,true,lastChgUserId,lastChgTime);
                                                pageVO.getPageRepeatingAnswerDTMap().put(toDropDownCodeDT.getLongKey(), list);

                                            }
                                            else if(pageVO.getPageRepeatingAnswerDTMap().get(toDropDownCodeDT.getLongKey())==null) {

                                                ArrayList<NbsCaseAnswerDto> list=(ArrayList<NbsCaseAnswerDto>)mappedCoInfectionQuestions.get(toDropDownCodeDT.getKey());
                                                list=changeStatus(list, proxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().getPublicHealthCaseUid(),true, false,false,lastChgUserId,lastChgTime);

                                                pageVO.getPageRepeatingAnswerDTMap().put(toDropDownCodeDT.getLongKey(), list);

                                            }else if(pageVO.getPageRepeatingAnswerDTMap().get(toDropDownCodeDT.getLongKey())!=null) {
                                                ArrayList<NbsCaseAnswerDto> deleteList=(ArrayList<NbsCaseAnswerDto>)pageVO.getPageRepeatingAnswerDTMap().get(toDropDownCodeDT.getLongKey());
                                                deleteList=changeStatus(deleteList, proxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().getPublicHealthCaseUid(),false, false,true,lastChgUserId,lastChgTime);

                                                ArrayList<NbsCaseAnswerDto> list=(ArrayList<NbsCaseAnswerDto>)mappedCoInfectionQuestions.get(toDropDownCodeDT.getKey());
                                                list=changeStatus(list, proxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().getPublicHealthCaseUid(),true, false,false,lastChgUserId,lastChgTime);

                                                deleteList.addAll(list);

                                                pageVO.getPageRepeatingAnswerDTMap().put(toDropDownCodeDT.getLongKey(), deleteList);
                                            }
                                        }else {
                                            logger.error("\n\nPLEASE check!!!TO Metadata question details: question_identifier:-"+ toDropDownCodeDT.getKey()+ "\nwith question_group_seq_nbr:-"+toDropDownCodeDT.getIntValue()+ "\nwith nbs_question_uid:-"+toDropDownCodeDT.getLongKey());
                                            logger.error("PLEASE check!!!From Metadata question details: question_identifier:-"+ fromDropDownCodeDT.getKey()+ "\nwith question_group_seq_nbr:-"+fromDropDownCodeDT.getIntValue()+ "\nwith nbs_question_uid:-"+fromDropDownCodeDT.getLongKey());
                                            logger.error("PLEASE check!!!AssociatedInvestigationUpdateUtil.updateCoInfectionInvest:coinfection investigation form code for the coinfection case is :"+investigationFormCd);
                                            logger.error("PLEASE check!!!AssociatedInvestigationUpdateUtil.updateCoInfectionInvest:The mapped question is being updated" );

                                        /*NbsCaseAnswerDT currentNbsCaseAnswerDT=(NbsCaseAnswerDT)mappedCoInfectionQuestions.get(toDropDownCodeDT.getKey());
										currentNbsCaseAnswerDT.setAddTime(lastChgTime);
										currentNbsCaseAnswerDT.setAddUserId(lastChgUserId);
										currentNbsCaseAnswerDT.setItNew(true);
										currentNbsCaseAnswerDT.setActUid(publicHealthCaseUid);
										pageVO.getPamAnswerDTMap().put(toDropDownCodeDT.getKey(), currentNbsCaseAnswerDT);*/
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
                        }catch (Exception e) {
                            String errorMessage ="Error processing co-infection question " +currentToQuestionKey + " " + e.getCause()+ e.getMessage();

                        }

                    }

                }
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
                String survivingEpiLinkId = publicHealthCaseVO.getTheCaseManagementDT().getEpiLinkId();
                String supersededEpiLinkId = supersededPublicHealthCaseVO.getTheCaseManagementDT().getEpiLinkId();

                if(coInSupersededEpliLinkIdMap.get(proxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().getPublicHealthCaseUid()) !=null) {
                    proxyVO.getPublicHealthCaseVO().getTheCaseManagementDT().setEpiLinkId(survivingEpiLinkId);
                }
                proxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().setCoinfectionId(coinfectionIdToUpdate);
                proxyVO.setMergeCase(true);
            }

            // Updates coinfection question's values in tables other than NBS_Case_Answer
            updateCoInfectionInvestForOtherTables(proxyVO, updateValueInOtherTablesMap, pageActProxyVO, publicHealthCaseVO);

            if(coinfectionIdToUpdate==null
                    || (supersededPublicHealthCaseVO!= null
                    && publicHealthCaseUid.compareTo(supersededPublicHealthCaseVO.getThePublicHealthCaseDT().getPublicHealthCaseUid())!=0))
            {
                updatePageProxyVOInterface(proxyVO,lastChgTime,lastChgUserId);
                Long phcUid = setPageActProxyVO( proxyVO);
                logger.debug("updateCoInfectionInvest method call completed for coinfectionIdToUpdate:"+ coinfectionIdToUpdate);
            }

        }catch(Exception e) {

            throw new DataProcessingException(e.toString() ,e);
        }
    }

    private ArrayList<NbsCaseAnswerDto> changeStatus(ArrayList<NbsCaseAnswerDto> list,Long publicHealthCaseUid,
                                                     boolean itNew, boolean itDirty, boolean itDelete,Long lastChgUserId, Timestamp lastChgTime){
        if(list!=null) {
            Iterator<NbsCaseAnswerDto> iterator= list.iterator();
            while(iterator.hasNext()) {
                NbsCaseAnswerDto caseAnswerDT =  (NbsCaseAnswerDto)iterator.next();
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


    private void updatePageProxyVOInterface(PageActProxyVO proxyActVO,Timestamp lastChgTime, Long lastChgUserId) throws DataProcessingException {
        try {
            proxyActVO.setRenterant(true);


            proxyActVO.setItDirty(true);
            proxyActVO.getPublicHealthCaseVO().setItDirty(true);
            proxyActVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().setLastChgTime(lastChgTime);
            proxyActVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().setLastChgUserId((lastChgUserId));
            proxyActVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().setItDirty(true);

            if (proxyActVO.getThePersonContainerCollection() != null) {
                for (Iterator<PersonContainer> anIterator = proxyActVO.getThePersonContainerCollection().iterator(); anIterator.hasNext();) {
                    PersonContainer personVO= (PersonContainer)anIterator.next();
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
                        Iterator<NbsActEntityDto> iterator = proxyActVO.getPageVO().getActEntityDTCollection().iterator();
                        while(iterator.hasNext()) {
                            NbsActEntityDto actEntityDT= (NbsActEntityDto)iterator.next();
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
        Map<Object, Object> returnMap = new HashMap();

        try {
            Iterator<Object> iterator = map.keySet().iterator();
            while(iterator.hasNext()) {
                Object key = iterator.next();
                Object object = map.get(key);
                if(object instanceof NbsCaseAnswerDto) {
                    NbsCaseAnswerDto caseAnswerDT = (NbsCaseAnswerDto)object;
                    caseAnswerDT.setLastChgTime(lastChgTime);
                    caseAnswerDT.setLastChgUserId(lastChgUserId);
                    //caseAnswerDT.setItDirty(true);
                    //caseAnswerDT.setItNew(false);
                    if(!caseAnswerDT.isItDelete() && !caseAnswerDT.isItDirty() && !caseAnswerDT.isItNew()) {
                        caseAnswerDT.setItDirty(true);
                        caseAnswerDT.setItNew(false);
                    }
                    returnMap.put(key, caseAnswerDT);
                }else if(object instanceof ArrayList) {
                    @SuppressWarnings("unchecked")
                    ArrayList<Object>  list =(ArrayList<Object>)object;
                    ArrayList<NbsAnswerDto> returnList= new ArrayList<>();
                    Iterator<Object> listIterator = list.iterator();
                    while(listIterator.hasNext()) {
                        NbsCaseAnswerDto caseAnswerDT = (NbsCaseAnswerDto)listIterator.next();
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
     *
     * @param pageActProxyVOofCoinfection
     * @param updateValueInOtherTablesMap
     * @param pageActProxyVO
     * @param publicHealthCaseVO
     */
    private  void updateCoInfectionInvestForOtherTables(PageActProxyVO pageActProxyVOofCoinfection,
                                                        Map<Object, Object> updateValueInOtherTablesMap,
                                                        PageActProxyVO pageActProxyVO ,
                                                        PublicHealthCaseVO publicHealthCaseVO) throws DataProcessingException {
        try {
            for (Object key : updateValueInOtherTablesMap.keySet()) {
                String dbLocation = (String) updateValueInOtherTablesMap.get(key);
                if(dbLocation!=null && dbLocation.contains("PERSON.")){
                    //Commented out as its tries to update MPR concurrently within same transaction.
                    // First for current investigation's patient and then coinfection investigation's patient.
                }else if(dbLocation!=null && dbLocation.contains("CASE_MANAGEMENT.")){
                    //TODO: INVESTIGATE THIS
//                    String columnName = dbLocation.substring(dbLocation.indexOf(".")+1,dbLocation.length());
//                    String getterMethod = DynamicBeanBinding.getGetterName(columnName);
//
//                    if(getterMethod!=null){
//                        String value = DynamicBeanBinding.getValueForMethod(publicHealthCaseVO.getTheCaseManagementDT(),getterMethod,publicHealthCaseVO.getTheCaseManagementDT().getClass().getName());
//
//                        if(value!=null){
//                            DynamicBeanBinding.populateBean(pageActProxyVOofCoinfection.getPublicHealthCaseVO().getTheCaseManagementDT(), columnName, value);
//
//                            pageActProxyVOofCoinfection.getPublicHealthCaseVO().getTheCaseManagementDT().setItDelete(true);
//                            pageActProxyVOofCoinfection.getPublicHealthCaseVO().getTheCaseManagementDT().setItNew(false);
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


    private void processingParticipationPatTypeForPageAct(PageActProxyVO pageActProxyVO) throws DataProcessingException {
        if (pageActProxyVO.isItNew() && (!pageActProxyVO.isItDirty()))
        {
            // changes according to new Analysis
            String classCd;
            String recordStatusCd;

            for (ParticipationDto participationDto : pageActProxyVO.getTheParticipationDtoCollection())
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

    private PageActPatient processingPersonContainerForPageAct(PageActProxyVO pageActProxyVO,
                                                               PublicHealthCaseDT phcDT) throws DataProcessingException
    {
        PersonContainer personVO;
        Iterator<PersonContainer> anIterator;
        Long falseUid;
        Long realUid = null;
        Long mprUid;
        Long patientRevisionUid = null;
        PageActPatient pageActPatient = new PageActPatient();

        if (pageActProxyVO.getThePersonContainerCollection() != null)
        {
            for (anIterator = pageActProxyVO.getThePersonContainerCollection().iterator(); anIterator.hasNext();)
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
                    if (personVO.getThePersonDto().getCd().equals(NEDSSConstant.PAT))
                    {
                        // Patient
                        String businessTriggerCd = NEDSSConstant.PAT_CR;
                        try {
                            var fakeId = personVO.getThePersonDto().getPersonUid();
                            personVO.getThePersonDto().setPersonUid(personVO.getThePersonDto().getPersonParentUid());
                        //    patientRepositoryUtil.updateExistingPerson(personVO);

                            var data = patientMatchingBaseService.setPatientRevision(personVO, businessTriggerCd);



                            patientRevisionUid= data;
                            realUid = patientRevisionUid;
                            pageActPatient.setPatientRevisionUid(patientRevisionUid);
                            personVO.getThePersonDto().setPersonUid(fakeId);
                        } catch (Exception ex) {
                            throw new DataProcessingException("Error in entityController.setPatientRevision : " + ex.toString());
                        }
                    }
                    else if (personVO.getThePersonDto().getCd().equals(NEDSSConstant.PRV))
                    {
                        // Provider
                        String businessTriggerCd = NEDSSConstant.PRV_CR;
                        try {
                            var data = patientRepositoryUtil.createPerson(personVO);
                            realUid = data.getPersonParentUid();
                        } catch (Exception ex) {
                            throw new DataProcessingException("Error in entityController.setProvider : " + ex.toString());
                        }

                    } // end of else if

                    falseUid = personVO.getThePersonDto().getPersonUid();

                    // replace the falseId with the realId
                    if (falseUid.intValue() < 0)
                    {
                        uidService.setFalseToNewForPageAct(pageActProxyVO, falseUid, realUid);
                    }

                }
                else if (personVO.isItDirty())
                {
                    if (personVO.getThePersonDto().getCd().equals(NEDSSConstant.PAT))
                    {
                        String businessTriggerCd = NEDSSConstant.PAT_EDIT;
                        try {
                            //patientRepositoryUtil.updateExistingPerson(personVO);
                            var data = patientMatchingBaseService.setPatientRevision(personVO, businessTriggerCd);

                            realUid = data;
                            patientRevisionUid= realUid;
                            pageActPatient.setPatientRevisionUid(patientRevisionUid);
                        }  catch (Exception ex) {
                            throw new DataProcessingException("Error in entityController.setPatientRevision : " + ex.toString());
                        }
                    }
                    else if (personVO.getThePersonDto().getCd().equals(NEDSSConstant.PRV))
                    {
                        String businessTriggerCd = NEDSSConstant.PRV_EDIT;
                        try {
                            patientRepositoryUtil.updateExistingPerson(personVO);
                            realUid = personVO.getThePersonDto().getPersonParentUid();
                        }  catch (Exception ex) {
                            throw new DataProcessingException("Error in entityController.setProvider : " + ex.toString());
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

    private PageActPhc processingPhcContainerForPageAct(
            PageActProxyVO pageActProxyVO,
            boolean isCoInfectionCondition) throws DataProcessingException
    {
        PageActPhc pageActPhc = new PageActPhc();
        Long falsePublicHealthCaseUid = null;
        Long actualUid = null;
        Long phcUid = null;
        if (pageActProxyVO.getPublicHealthCaseVO() != null)
        {
            String businessTriggerCd = null;
            PublicHealthCaseVO publicHealthCaseVO = pageActProxyVO.getPublicHealthCaseVO();
            publicHealthCaseVO.getThePublicHealthCaseDT().setPageCase(true);
            if(pageActProxyVO.isItDirty())
            {
                pamRepositoryUtil.getPamHistory(pageActProxyVO.getPublicHealthCaseVO());
            }
            PublicHealthCaseDT publicHealthCaseDT = publicHealthCaseVO.getThePublicHealthCaseDT();
            if(publicHealthCaseVO.getNbsAnswerCollection()!=null)
            {
                logger.debug("********#publicHealthCaseVO.getNbsAnswerCollection() size from history table: "+publicHealthCaseVO.getNbsAnswerCollection().size());
            }
            if(publicHealthCaseDT.getPublicHealthCaseUid()!=null && publicHealthCaseDT.getVersionCtrlNbr()!=null)
            {
                logger.debug("********#Public Health Case Uid: "+publicHealthCaseDT.getPublicHealthCaseUid().longValue()+"" +" Version: "+publicHealthCaseDT.getVersionCtrlNbr().intValue()+"");
            }

            RootDtoInterface rootDTInterface = publicHealthCaseDT;
            String businessObjLookupName = NBSBOLookup.INVESTIGATION;
            if (pageActProxyVO.isItNew())
            {
                businessTriggerCd = "INV_CR";
                if(isCoInfectionCondition && pageActProxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().getCoinfectionId()==null)
                {
                    logger.debug("AssociatedInvestigationUpdateUtil.updatForConInfectionId created an new coinfection id for the case");
                    pageActProxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().setCoinfectionId(NEDSSConstant.COINFCTION_GROUP_ID_NEW_CODE);
                }
            }
            else if (pageActProxyVO.isItDirty())
            {
                businessTriggerCd = "INV_EDIT";

            }
            String tableName = "PUBLIC_HEALTH_CASE";
            String moduleCd = "BASE";
            publicHealthCaseDT = (PublicHealthCaseDT) prepareAssocModelHelper.prepareVO(rootDTInterface, businessObjLookupName, businessTriggerCd, tableName, moduleCd, rootDTInterface.getVersionCtrlNbr());
            publicHealthCaseVO.setThePublicHealthCaseDT(publicHealthCaseDT);

            falsePublicHealthCaseUid = publicHealthCaseVO.getThePublicHealthCaseDT().getPublicHealthCaseUid();
            actualUid = publicHealthCaseService.setPublicHealthCase(publicHealthCaseVO);
            phcUid= actualUid;
            logger.debug("actualUid.intValue() = " + actualUid.intValue());
            if (falsePublicHealthCaseUid.intValue() < 0)
            {
                logger.debug("falsePublicHealthCaseUid.intValue() = " + falsePublicHealthCaseUid.intValue());
                uidService.setFalseToNewForPageAct(pageActProxyVO, falsePublicHealthCaseUid, actualUid);
                publicHealthCaseVO.getThePublicHealthCaseDT().setPublicHealthCaseUid(actualUid);
            }

            logger.debug("falsePublicHealthCaseUid.intValue() = " + falsePublicHealthCaseUid.intValue());
        }

        pageActPhc.setPhcUid(phcUid);
        pageActPhc.setFalsePublicHealthCaseUid(falsePublicHealthCaseUid);
        pageActPhc.setActualUid(actualUid);
        return pageActPhc;
    }

    private Long processingPhcActRelationshipForPageAct(PageActProxyVO pageActProxyVO) throws DataProcessingException {
        Long docUid = null;
        Iterator<ActRelationshipDto> anIteratorActRelationship;
        if (pageActProxyVO.getPublicHealthCaseVO().getTheActRelationshipDTCollection() != null)
        {
            for (anIteratorActRelationship = pageActProxyVO.getPublicHealthCaseVO().getTheActRelationshipDTCollection().iterator(); anIteratorActRelationship.hasNext();)
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

    private void processingParticipationForPageAct(PageActProxyVO pageActProxyVO) throws DataProcessingException {
        if (pageActProxyVO.getTheParticipationDtoCollection() != null)
        {
            for (var item : pageActProxyVO.getTheParticipationDtoCollection())
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

    private void processingNotificationSummaryForPageAct(PageActProxyVO pageActProxyVO, PublicHealthCaseDT phcDT) throws DataProcessingException {
        if (pageActProxyVO.getTheNotificationSummaryVOCollection() != null)
        {
            Collection<Object> notSumVOColl = pageActProxyVO.getTheNotificationSummaryVOCollection();
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

    private void processingEventProcessForPageAct(PageActProxyVO pageActProxyVO, Long phcUid) throws DataProcessingException {
        if (pageActProxyVO.getPublicHealthCaseVO().getEdxEventProcessDTCollection() != null)
        {
            for (EDXEventProcessDT processDT : pageActProxyVO.getPublicHealthCaseVO().getEdxEventProcessDTCollection())
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

    private void processingNbsDocumentForPageAct(PageActProxyVO pageActProxyVO, Long docUid) throws DataProcessingException {
        if (docUid != null)
        {
            try {

                // get the
                NbsDocumentContainer nbsDocVO = nbsDocumentRepositoryUtil.getNBSDocumentWithoutActRelationship(docUid);
                if (nbsDocVO.getNbsDocumentDT().getJurisdictionCd() == null
                        || (nbsDocVO.getNbsDocumentDT().getJurisdictionCd() != null
                        && nbsDocVO.getNbsDocumentDT().getJurisdictionCd().equals(""))
                )
                {
                    nbsDocVO.getNbsDocumentDT().setJurisdictionCd(pageActProxyVO.getPublicHealthCaseVO()
                            .getThePublicHealthCaseDT()
                            .getJurisdictionCd());
                }
                nbsDocumentRepositoryUtil.updateDocumentWithOutthePatient(nbsDocVO);
            }
            catch (Exception e) {
                throw new DataProcessingException(e.getMessage());
            }
        }

    }

    private  void handlingCoInfectionAndContactDisposition(PageActProxyVO pageActProxyVO, Long mprUid, Long actualUid) throws DataProcessingException {
        if( !pageActProxyVO.isRenterant() && pageActProxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().getCoinfectionId()!=null
                && !pageActProxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().getCoinfectionId().equalsIgnoreCase(NEDSSConstant.COINFCTION_GROUP_ID_NEW_CODE) && mprUid!=null
                && !pageActProxyVO.isMergeCase() && !NEDSSConstant.INVESTIGATION_STATUS_CODE_CLOSED.equals(pageActProxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().getInvestigationStatusCd()))
        {
            updatForConInfectionId(pageActProxyVO, mprUid, actualUid);
        }

        if(pageActProxyVO.getPublicHealthCaseVO().getTheCaseManagementDT()!=null) {
            //TODO: NBS STD OR HIV PROG
//                boolean isStdHivProgramAreaCode =PropertyUtil.isStdOrHivProgramArea(pageProxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().getProgAreaCd());
//                if(isStdHivProgramAreaCode)
//                {
//                    updateNamedAsContactDisposition(pageActProxyVO.getPublicHealthCaseVO().getTheCaseManagementDT());
//                }

        }
    }



}
