package gov.cdc.dataprocessing.utilities.component.page_and_pam;

import gov.cdc.dataprocessing.constant.MessageConstants;
import gov.cdc.dataprocessing.constant.elr.NBSBOLookup;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.*;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.edx.EDXEventProcessDto;
import gov.cdc.dataprocessing.model.dto.log.MessageLogDto;
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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192"})
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

    @SuppressWarnings({"java:S6541","java:S3776", "java:S1854"})
    public Long setPageActProxyVO(PageActProxyContainer pageProxyVO) throws DataProcessingException {
        try {
            PublicHealthCaseDto phcDT = pageProxyVO.getPublicHealthCaseContainer().getThePublicHealthCaseDto();
            boolean isCoInfectionCondition = pageProxyVO.getPublicHealthCaseContainer().isCoinfectionCondition();
            Long mprUid;

            // if both are false throw exception
            if ((!pageProxyVO.isItNew()) && (!pageProxyVO.isItDirty())) {
                throw new DataProcessingException("pageProxyVO.isItNew() = " + pageProxyVO.isItNew() + " and pageProxyVO.isItDirty() = " + pageProxyVO.isItDirty() + " for setPageProxy");
            }


            if (pageProxyVO.isItDirty() && !pageProxyVO.isConversionHasModified())
            {
                try {
                    // update auto resend notifications
                    investigationService.updateAutoResendNotificationsAsync(pageProxyVO);
                } catch (Exception e) {
                    //TODO: LOGGING NND LOG
                }
            }

            processingParticipationPatTypeForPageAct(pageProxyVO);

            Long actualUid;

            try
            {
                Long patientRevisionUid;
                Long phcUid;

                var pageActPatient = processingPersonContainerForPageAct(pageProxyVO, phcDT);
                phcDT = pageActPatient.getPhcDT();
                mprUid = pageActPatient.getMprUid();
                patientRevisionUid = pageActPatient.getPatientRevisionUid();


                var pageActPhc = processingPhcContainerForPageAct(pageProxyVO, isCoInfectionCondition);
                actualUid = pageActPhc.getActualUid();
                phcUid = pageActPhc.getPhcUid();


                //TODO: LOGGING
                if (pageProxyVO.getMessageLogDTMap() != null && !pageProxyVO.getMessageLogDTMap().isEmpty())
                {

                    Set<String> set = pageProxyVO.getMessageLogDTMap().keySet();
                    for (String key : set) {
                        if (key.contains(MessageConstants.DISPOSITION_SPECIFIED_KEY))
                        {
                            //Investigator of Named by contact will get message for Named by contact and contact's investigation id.
                            continue;
                        }
                        MessageLogDto messageLogDT = pageProxyVO.getMessageLogDTMap().get(key);

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
                }


                // this collection should only be populated in edit scenario, xz
                // defect 11861 (10/01/04)
                processingNotificationSummaryForPageAct(pageProxyVO, phcDT);


                Long docUid;
                docUid = processingPhcActRelationshipForPageAct(pageProxyVO);

                processingEventProcessForPageAct(pageProxyVO, phcUid);

                /*
                 * Updating the Document table
                 */
                // Getting the DocumentEJB reference
                processingNbsDocumentForPageAct(pageProxyVO, docUid);

                processingParticipationForPageAct(pageProxyVO);


                if( pageProxyVO.isUnsavedNote() && pageProxyVO.getNbsNoteDTColl()!=null
                        && !pageProxyVO.getNbsNoteDTColl().isEmpty()){
                    nbsNoteRepositoryUtil.storeNotes(actualUid, pageProxyVO.getNbsNoteDTColl());
                }

                if (pageProxyVO.getPageVO() != null && pageProxyVO.isItNew()) {
                    pamService.insertPamVO(pageProxyVO.getPageVO(), pageProxyVO.getPublicHealthCaseContainer());

                } else if (pageProxyVO.getPageVO() != null && pageProxyVO.isItDirty()) {
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

            handlingCoInfectionAndContactDisposition(pageProxyVO, mprUid, actualUid);


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
    @SuppressWarnings("java:S125")
    public void updateForConInfectionId(PageActProxyContainer pageActProxyContainer, PageActProxyContainer supersededProxyVO, Long mprUid,
                                        Map<Object, Object> coInSupersededEpliLinkIdMap, Long currentPhclUid,
                                        Collection<Object> coinfectionSummaryVOCollection, String coinfectionIdToUpdate)
            throws DataProcessingException {
        try {
            String coninfectionId= pageActProxyContainer.getPublicHealthCaseContainer().getThePublicHealthCaseDto().getCoinfectionId();
            if(coinfectionSummaryVOCollection==null)
                coinfectionSummaryVOCollection = getInvListForCoInfectionId(mprUid,coninfectionId);

            Map<Object, Object> mapFromQuestions = new HashMap<>();
            Map<Object,Object> updatedValuesMap = new HashMap<>();

            Map<Object,Object> updateValueInOtherTablesMap = new HashMap<>(); // Map is to update values in other table then NBS_CASE_Answer

            if(coinfectionSummaryVOCollection!=null && !coinfectionSummaryVOCollection.isEmpty()) {
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

    @SuppressWarnings({"java:S1172","java:S1854", "java:S1481", "java:S125"})
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
            publicHealthCaseUid=coninfectionSummaryVO.getPublicHealthCaseUid();
            java.util.Date dateTime = new java.util.Date();
            Timestamp lastChgTime = new Timestamp(dateTime.getTime());
            Long lastChgUserId= AuthUtil.authUser.getNedssEntryId();
            PageActProxyContainer proxyVO =  investigationService.getPageProxyVO(NEDSSConstant.CASE, publicHealthCaseUid);
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

                if(coInSupersededEpliLinkIdMap.get(proxyVO.getPublicHealthCaseContainer().getThePublicHealthCaseDto().getPublicHealthCaseUid()) !=null) {
                    proxyVO.getPublicHealthCaseContainer().getTheCaseManagementDto().setEpiLinkId(survivingEpiLinkId);
                }
                proxyVO.getPublicHealthCaseContainer().getThePublicHealthCaseDto().setCoinfectionId(coinfectionIdToUpdate);
                proxyVO.setMergeCase(true);
            }

            // Updates coinfection question's values in tables other than NBS_Case_Answer
            updateCoInfectionInvestForOtherTables(proxyVO, updateValueInOtherTablesMap, pageActProxyContainer, publicHealthCaseContainer);

            if(coinfectionIdToUpdate==null
                    || (supersededPublicHealthCaseContainer != null // NOSONAR
                    && publicHealthCaseUid.compareTo(supersededPublicHealthCaseContainer.getThePublicHealthCaseDto().getPublicHealthCaseUid())!=0))
            {
                updatePageProxyVOInterface(proxyVO,lastChgTime,lastChgUserId);
                setPageActProxyVO( proxyVO);
                logger.debug("updateCoInfectionInvest method call completed for coinfectionIdToUpdate:"+ coinfectionIdToUpdate);
            }

        }catch(Exception e) {

            throw new DataProcessingException(e.getMessage() ,e);
        }
    }
    @SuppressWarnings("java:S3776")
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
            throw new DataProcessingException(e.getMessage(), e);
        }
    }


    private Map<Object, Object> updateNbsCaseAnswerInterfaceValues(
            Map<Object, Object> map, Timestamp lastChgTime, Long lastChgUserId) throws DataProcessingException {
        Map<Object, Object> returnMap = new HashMap<>();

        try {
            for (Object key : map.keySet()) // NOSONAR
            {
                Object object = map.get(key);
                if (object instanceof NbsCaseAnswerDto) {
                    NbsCaseAnswerDto caseAnswerDT = (NbsCaseAnswerDto) object;
                    caseAnswerDT.setLastChgTime(lastChgTime);
                    caseAnswerDT.setLastChgUserId(lastChgUserId);
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
                        returnList.add(caseAnswerDT);
                    }
                    returnMap.put(key, returnList);
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
        return returnMap;
    }


    /**
     *
     * Updates coinfection question's values in tables other than NBS_Case_Answer
     */
    @SuppressWarnings({"java:S1172", "java:S1871"})
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
            throw new DataProcessingException(ex.getMessage(), ex);
        }
    }

    @SuppressWarnings("java:S3776")
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

    @SuppressWarnings({"java:S6541", "java:S3776"})
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
    @SuppressWarnings({"java:S3457","java:S3776"})
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
                    throw new DataProcessingException(e.getMessage(), e);
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
                    throw new DataProcessingException(e.getMessage(), e);
                }
            }
        }
    }
    @SuppressWarnings("java:S3776")
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
                throw new DataProcessingException(e.getMessage(), e);
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
        }
    }



}
