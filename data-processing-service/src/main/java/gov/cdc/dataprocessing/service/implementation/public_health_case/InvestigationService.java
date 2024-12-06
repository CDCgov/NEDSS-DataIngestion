package gov.cdc.dataprocessing.service.implementation.public_health_case;

import gov.cdc.dataprocessing.constant.elr.NBSBOLookup;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.ObjectName;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.container.base.BasePamContainer;
import gov.cdc.dataprocessing.model.container.interfaces.ReportSummaryInterface;
import gov.cdc.dataprocessing.model.container.model.*;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.generic_helper.StateDefinedFieldDataDto;
import gov.cdc.dataprocessing.model.dto.notification.NotificationDto;
import gov.cdc.dataprocessing.model.dto.notification.UpdatedNotificationDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.model.dto.organization.OrganizationNameDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.Observation_Lab_Summary_ForWorkUp_New;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.observation.ObservationRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.observation.Observation_SummaryRepository;
import gov.cdc.dataprocessing.repository.nbs.srte.repository.LabTestRepository;
import gov.cdc.dataprocessing.service.implementation.act.ActRelationshipService;
import gov.cdc.dataprocessing.service.interfaces.cache.ICacheApiService;
import gov.cdc.dataprocessing.service.interfaces.cache.ICatchingValueService;
import gov.cdc.dataprocessing.service.interfaces.material.IMaterialService;
import gov.cdc.dataprocessing.service.interfaces.notification.INotificationService;
import gov.cdc.dataprocessing.service.interfaces.observation.IObservationSummaryService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IContactSummaryService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IInvestigationService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.ILdfService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IRetrieveSummaryService;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import gov.cdc.dataprocessing.utilities.component.observation.ObservationRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.organization.OrganizationRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.public_health_case.PublicHealthCaseRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.sql.QueryHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;

import static gov.cdc.dataprocessing.constant.elr.EdxELRConstant.AND_UPPERCASE;
import static gov.cdc.dataprocessing.constant.elr.NEDSSConstant.LABORATORY_UID;

@Service
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
public class InvestigationService implements IInvestigationService {
    private static final Logger logger = LoggerFactory.getLogger(InvestigationService.class);

    private final PublicHealthCaseRepositoryUtil publicHealthCaseRepositoryUtil;
    private final OrganizationRepositoryUtil organizationRepositoryUtil;
    private final PatientRepositoryUtil patientRepositoryUtil;

    private final IMaterialService materialService;

    private final PrepareAssocModelHelper prepareAssocModelHelper;
    private final ObservationRepositoryUtil observationRepositoryUtil;
    private final ObservationRepository observationRepository;
    private final IRetrieveSummaryService retrieveSummaryService;
    private final ActRelationshipService actRelationshipService;

    private final INotificationService notificationService;
    private final IObservationSummaryService observationSummaryService;
    private final QueryHelper queryHelper;
    private final Observation_SummaryRepository observationSummaryRepository;
    private final IContactSummaryService contactSummaryService;
    private final ILdfService ldfService;
    private final LabTestRepository labTestRepository;
    private static final String LAB_EVENT_LIST = "labEventList";

    private final ICacheApiService cacheApiService;
    private final ICatchingValueService catchingValueService;

    public InvestigationService(PublicHealthCaseRepositoryUtil publicHealthCaseRepositoryUtil,
                                OrganizationRepositoryUtil organizationRepositoryUtil,
                                PatientRepositoryUtil patientRepositoryUtil,
                                IMaterialService materialService,
                                PrepareAssocModelHelper prepareAssocModelHelper,
                                ObservationRepositoryUtil observationRepositoryUtil,
                                ObservationRepository observationRepository, IRetrieveSummaryService retrieveSummaryService,
                                ActRelationshipService actRelationshipService,
                                INotificationService notificationService,
                                IObservationSummaryService observationSummaryService,
                                QueryHelper queryHelper,
                                Observation_SummaryRepository observationSummaryRepository,
                                IContactSummaryService contactSummaryService,
                                ILdfService ldfService,
                                LabTestRepository labTestRepository, ICacheApiService cacheApiService, ICatchingValueService catchingValueService) {
        this.publicHealthCaseRepositoryUtil = publicHealthCaseRepositoryUtil;
        this.organizationRepositoryUtil = organizationRepositoryUtil;
        this.patientRepositoryUtil = patientRepositoryUtil;
        this.materialService = materialService;
        this.prepareAssocModelHelper = prepareAssocModelHelper;
        this.observationRepositoryUtil = observationRepositoryUtil;
        this.observationRepository = observationRepository;
        this.retrieveSummaryService = retrieveSummaryService;
        this.actRelationshipService = actRelationshipService;
        this.notificationService = notificationService;
        this.observationSummaryService = observationSummaryService;
        this.queryHelper = queryHelper;
        this.observationSummaryRepository = observationSummaryRepository;
        this.contactSummaryService = contactSummaryService;
        this.ldfService = ldfService;
        this.labTestRepository = labTestRepository;
        this.cacheApiService = cacheApiService;
        this.catchingValueService = catchingValueService;
    }

    @SuppressWarnings({"java:S125","java:S5411"})
    @Transactional
    public void setAssociations(Long investigationUID,
                                Collection<LabReportSummaryContainer>  reportSumVOCollection,
                                Collection<Object>  vaccinationSummaryVOCollection,
                                Collection<Object>  summaryDTColl,
                                Collection<Object> treatmentSumColl,
                                Boolean isNNDResendCheckRequired) throws DataProcessingException {
        InvestigationContainer invVO = new InvestigationContainer();
        try {
            if(reportSumVOCollection!=null && !reportSumVOCollection.isEmpty() ){
                setObservationAssociationsImpl(investigationUID, reportSumVOCollection);
            }
            if(isNNDResendCheckRequired){
                 invVO = getInvestigationProxy(investigationUID);
                updateAutoResendNotificationsAsync(invVO);
            }
            if(reportSumVOCollection!=null && !reportSumVOCollection.isEmpty()){
                retrieveSummaryService.checkBeforeCreateAndStoreMessageLogDTCollection(investigationUID, reportSumVOCollection);
            }
        }
        catch (Exception e) {
//            NNDActivityLogDto nndActivityLogDT = new  NNDActivityLogDto();
//            String phcLocalId = invVO.getThePublicHealthCaseContainer().getThePublicHealthCaseDto().getLocalId();
//            nndActivityLogDT.setErrorMessageTxt(e.toString());
//            if (phcLocalId!=null)
//            {
//                nndActivityLogDT.setLocalId(phcLocalId);
//            }
//            else
//            {
//                nndActivityLogDT.setLocalId("N/A");
//            }
//            n1.persistNNDActivityLog(nndActivityLogDT);
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    /**
     * This method Associates the observation(LAb or MORB) to Investigation 
     * @param investigationUID -- The UID for the investigation to which observation is to be associated or disassociates
     * @param invFromEvent - flag to indicates if lab or morb report is the reactor for investigation.
     */
    @SuppressWarnings("java:S3776")
    public void setObservationAssociationsImpl(Long investigationUID, Collection<LabReportSummaryContainer>  reportSumVOCollection, boolean invFromEvent) throws DataProcessingException
    {
        PublicHealthCaseDto phcDT =  publicHealthCaseRepositoryUtil.findPublicHealthCase(investigationUID);
        try
        {
            //For each report summary vo
            if(!reportSumVOCollection.isEmpty())
            {
                for (LabReportSummaryContainer reportSumVO : reportSumVOCollection) {
                    ActRelationshipDto actRelationshipDT;
                    RootDtoInterface rootDT = null;

                    //Gets and checks whether any association change; if changed, do something, else go next one
                    boolean isTouched = reportSumVO.getIsTouched();
                    if (!isTouched) {
                        continue;
                    }

                    actRelationshipDT = new ActRelationshipDto();
                    //Sets the properties of ActRelationshipDT object
                    actRelationshipDT.setTargetActUid(investigationUID);
                    actRelationshipDT.setSourceActUid(reportSumVO.getObservationUid());
                    actRelationshipDT.setFromTime(reportSumVO.getActivityFromTime());
                    actRelationshipDT.setLastChgUserId(AuthUtil.authUser.getNedssEntryId());
                    //Set from time same as investigation create time if act relationship is created while creating investigation from lab or morbidity report
                    if (invFromEvent) {
                        actRelationshipDT.setFromTime(phcDT.getAddTime());
                    }
                    actRelationshipDT.setSourceClassCd(NEDSSConstant.OBSERVATION_CLASS_CODE);
                    actRelationshipDT.setTargetClassCd(NEDSSConstant.PUBLIC_HEALTH_CASE_CLASS_CODE);
                    //actRelationshipDT.setStatusTime(new Timestamp(new java.util.Date().getTime()));
                    boolean reportFromDoc = false;
                    actRelationshipDT.setTypeCd(NEDSSConstant.LAB_DISPALY_FORM);
                    if (reportSumVO.isLabFromDoc()) {
                        reportFromDoc = true;
                    }
                    if (invFromEvent) {
                        actRelationshipDT.setAddReasonCd(reportSumVO.getProcessingDecisionCd());
                    }


                    if (reportSumVO.getIsAssociated()) {
                        actRelationshipDT.setRecordStatusCd(NEDSSConstant.ACTIVE);
                        actRelationshipDT.setStatusCd(NEDSSConstant.A);
                    } else {
                        actRelationshipDT.setRecordStatusCd(NEDSSConstant.INACTIVE);
                        actRelationshipDT.setStatusCd(NEDSSConstant.I);
                    }

                    actRelationshipDT = prepareAssocModelHelper.prepareAssocDTForActRelationship(actRelationshipDT);
                    // needs to be done here as prepareAssocDT will always set dirty flag true
                    if (reportSumVO.getIsAssociated())
                    {
                        actRelationshipDT.setItNew(true);
                        actRelationshipDT.setItDirty(false);
                    }
                    else
                    {
                        actRelationshipDT.setItDelete(true);
                        actRelationshipDT.setItDirty(false);
                    }
                    observationRepositoryUtil.saveActRelationship(actRelationshipDT);

                    if (!reportFromDoc) {
                        //Obtains the core observation object
                        var obs = observationRepositoryUtil.loadObject(reportSumVO.getObservationUid());
                        ObservationDto obsDT = obs.getTheObservationDto();
                        //Starts persist observationDT
                        if (reportSumVO.getIsAssociated())
                        {
                            obsDT.setItDirty(true);
                            String businessObjLookupName = "";
                            String businessTriggerCd = "";
                            String tableName = NEDSSConstant.OBSERVATION;
                            String moduleCd = NEDSSConstant.BASE;
                            businessObjLookupName = NEDSSConstant.OBSERVATIONLABREPORT;
                            businessTriggerCd = NEDSSConstant.OBS_LAB_ASC;

                            rootDT = prepareAssocModelHelper.prepareVO(obsDT, businessObjLookupName, businessTriggerCd, tableName, moduleCd, obsDT.getVersionCtrlNbr());
                        }

                        // processing non associate report summary
                        rootDT = this.processingNonAssociatedReportSummaryContainer(reportSumVO, obsDT, rootDT);

                        obsDT = (ObservationDto) rootDT;
                        //set the previous entered processing decision to null
                        obsDT.setProcessingDecisionCd(null);
                        observationRepositoryUtil.setObservationInfo(obsDT);
                    }
                }
            }
        }
        catch(Exception e)
        {
            logger.info(e.getMessage());
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    protected RootDtoInterface processingNonAssociatedReportSummaryContainer(LabReportSummaryContainer reportSumVO,
                                                                 ObservationDto obsDT, RootDtoInterface rootDT)
            throws DataProcessingException {
        if (!reportSumVO.getIsAssociated())
        {
            obsDT.setItDirty(true);
            String businessObjLookupName = "";
            String businessTriggerCd = "";
            String tableName = NEDSSConstant.OBSERVATION;
            String moduleCd = NEDSSConstant.BASE;
            Collection<ActRelationshipDto> actRelColl = actRelationshipService.loadActRelationshipBySrcIdAndTypeCode(reportSumVO.getObservationUid(), "LabReport");
            businessObjLookupName = NEDSSConstant.OBSERVATIONLABREPORT;
            if (actRelColl != null && !actRelColl.isEmpty())
            {
                businessTriggerCd = NEDSSConstant.OBS_LAB_DIS_ASC;
            }
            else
            {
                businessTriggerCd = NEDSSConstant.OBS_LAB_UNPROCESS;
            }
            rootDT = prepareAssocModelHelper.prepareVO(obsDT, businessObjLookupName, businessTriggerCd, tableName, moduleCd, obsDT.getVersionCtrlNbr());
        }
        return  rootDT;
    }

    public void updateAutoResendNotificationsAsync(BaseContainer v)
    {
        updateAutoResendNotifications(v);
    }

    @SuppressWarnings({"java:S6541", "java:S3776"})
    public PageActProxyContainer getPageProxyVO(String typeCd, Long publicHealthCaseUID) throws DataProcessingException {
        PageActProxyContainer pageProxyVO = new PageActProxyContainer();

        PublicHealthCaseContainer thePublicHealthCaseContainer;

        ArrayList<PersonContainer> thePersonVOCollection = new ArrayList<>();
        ArrayList<OrganizationContainer> theOrganizationVOCollection = new ArrayList<>();
        ArrayList<MaterialContainer> theMaterialVOCollection = new ArrayList<>();

        // Summary Collections
        ArrayList<Object> theInvestigationAuditLogSummaryVOCollection;
        ArrayList<Object> theDocumentSummaryVOCollection;


        thePublicHealthCaseContainer = publicHealthCaseRepositoryUtil.loadObject(publicHealthCaseUID);

        thePublicHealthCaseContainer.getThePublicHealthCaseDto().setAddUserName(AuthUtil.authUser.getUserId());
        thePublicHealthCaseContainer.getThePublicHealthCaseDto().setLastChgUserName(AuthUtil.authUser.getUserId());


        BasePamContainer pageVO = publicHealthCaseRepositoryUtil.getPamVO(publicHealthCaseUID);
        pageProxyVO.setPageVO(pageVO);
        String strClassCd;
        String recordStatusCd;
        Long nEntityID;
        ParticipationDto participationDT;

        Iterator<ParticipationDto> participationIterator = thePublicHealthCaseContainer
                .getTheParticipationDTCollection().iterator();
        logger.debug("ParticipationDTCollection() = {}", thePublicHealthCaseContainer.getTheParticipationDTCollection());

        // Populate the Entity collections with the results
        while (participationIterator.hasNext()) {
            participationDT = participationIterator
                    .next();
            nEntityID = participationDT.getSubjectEntityUid();
            strClassCd = participationDT.getSubjectClassCd();
            recordStatusCd = participationDT.getRecordStatusCd();
            if (strClassCd != null
                    && strClassCd.compareToIgnoreCase(NEDSSConstant.ORGANIZATION) == 0
                    && recordStatusCd != null
                    && recordStatusCd.equals(NEDSSConstant.RECORD_STATUS_ACTIVE))
            {
                theOrganizationVOCollection.add(organizationRepositoryUtil.loadObject(nEntityID, null));

                continue;
            }
            if (strClassCd != null
                    && strClassCd
                    .compareToIgnoreCase(NEDSSConstant.PERSON) == 0
                    && recordStatusCd != null
                    && recordStatusCd
                    .equals(NEDSSConstant.RECORD_STATUS_ACTIVE)) {
                thePersonVOCollection.add(patientRepositoryUtil.loadPerson(nEntityID));
                continue;
            }
            if (strClassCd != null
                    && strClassCd
                    .compareToIgnoreCase(NEDSSConstant.MATERIAL) == 0
                    && recordStatusCd != null
                    && recordStatusCd
                    .equals(NEDSSConstant.RECORD_STATUS_ACTIVE)) {
                theMaterialVOCollection.add(materialService.loadMaterialObject(nEntityID));
            }
        }

        pageProxyVO.setTheOrganizationContainerCollection(theOrganizationVOCollection);
        pageProxyVO.setPublicHealthCaseContainer(thePublicHealthCaseContainer);
        pageProxyVO.setThePersonContainerCollection(thePersonVOCollection);

        pageProxyVO.setTheNotificationSummaryVOCollection(retrieveSummaryService.notificationSummaryOnInvestigation(thePublicHealthCaseContainer, pageProxyVO));

        if (pageProxyVO.getTheNotificationSummaryVOCollection() != null) {
            for (Object o : pageProxyVO.getTheNotificationSummaryVOCollection())
            {
                NotificationSummaryContainer notifVO = (NotificationSummaryContainer) o;
                for (ActRelationshipDto actRelationDT : pageProxyVO
                        .getPublicHealthCaseContainer()
                        .getTheActRelationshipDTCollection())
                {
                    if ((notifVO.getCdNotif().equalsIgnoreCase(NEDSSConstant.CLASS_CD_SHARE_NOTF)
                            || notifVO.getCdNotif().equalsIgnoreCase(NEDSSConstant.CLASS_CD_SHARE_NOTF_PHDC))
                            && notifVO.getNotificationUid().compareTo(actRelationDT.getSourceActUid()) == 0) {
                        actRelationDT.setShareInd(true);
                    }
                    if ((notifVO.getCdNotif().equalsIgnoreCase(NEDSSConstant.CLASS_CD_EXP_NOTF)
                            || notifVO.getCdNotif().equalsIgnoreCase(NEDSSConstant.CLASS_CD_EXP_NOTF_PHDC))
                            && notifVO.getNotificationUid().compareTo(actRelationDT.getSourceActUid()) == 0) {
                        actRelationDT.setExportInd(true);
                    }
                    if ((notifVO.getCdNotif().equalsIgnoreCase(NEDSSConstant.CLASS_CD_NOTF))
                            && notifVO.getNotificationUid().compareTo(actRelationDT.getSourceActUid()) == 0) {
                        actRelationDT.setNNDInd(true);
                    }
                }
            }
        }

        if(typeCd!=null && !typeCd.equals(NEDSSConstant.CASE_LITE)) {
            Collection<Object> labSumVOCol = new ArrayList<>();
            HashMap<Object, Object> labSumVOMap;

            String labReportViewClause = queryHelper
                    .getDataAccessWhereClause(
                            NBSBOLookup.OBSERVATIONLABREPORT,
                            "VIEW", "obs");
            labReportViewClause = labReportViewClause != null ? AND_UPPERCASE
                    + labReportViewClause : "";

            Collection<UidSummaryContainer> LabReportUidSummarVOs =  observationSummaryService
                    .findAllActiveLabReportUidListForManage(
                            publicHealthCaseUID, labReportViewClause);

            String uidType = LABORATORY_UID;
            Collection<?> labReportSummaryVOCollection;
            LabReportSummaryContainer labReportSummaryVOs;
            if (LabReportUidSummarVOs != null && !LabReportUidSummarVOs.isEmpty()) {
                boolean isCDCFormPrintCase;
                if(typeCd.equalsIgnoreCase(NEDSSConstant.PRINT_CDC_CASE)){
                    isCDCFormPrintCase = true;
                    if(LabReportUidSummarVOs!=null && !LabReportUidSummarVOs.isEmpty()){
                        for (UidSummaryContainer uidSummaryVO : LabReportUidSummarVOs) {
                            uidSummaryVO.setStatusTime(thePublicHealthCaseContainer.getThePublicHealthCaseDto().getAddTime());
                        }
                    }

                }else{
                    isCDCFormPrintCase= false;
                }
                labSumVOMap = retrieveLabReportSummaryRevisited(
                        LabReportUidSummarVOs,isCDCFormPrintCase,
                        uidType);

                if (labSumVOMap.containsKey(LAB_EVENT_LIST)) {
                    labReportSummaryVOCollection = (ArrayList<?>) labSumVOMap
                            .get(LAB_EVENT_LIST);
                    for (Object o : labReportSummaryVOCollection) {
                        labReportSummaryVOs = (LabReportSummaryContainer) o;
                        labSumVOCol.add(labReportSummaryVOs);
                    }
                }

            }
            Map<Long, LabReportSummaryContainer> labMapfromDOC =  new HashMap<>();

            if(labMapfromDOC!=null && !labMapfromDOC.isEmpty())
            {
                labSumVOCol.addAll(labMapfromDOC.values());
            }
            if (labSumVOCol != null) {
                pageProxyVO.setTheLabReportSummaryVOCollection(labSumVOCol);

            }
            theInvestigationAuditLogSummaryVOCollection = new ArrayList<>();
            pageProxyVO.setTheInvestigationAuditLogSummaryVOCollection(theInvestigationAuditLogSummaryVOCollection);
            theDocumentSummaryVOCollection = new ArrayList<>(retrieveSummaryService.retrieveDocumentSummaryVOForInv(publicHealthCaseUID).values());
            pageProxyVO.setTheDocumentSummaryVOCollection(theDocumentSummaryVOCollection);
            Collection<Object> contactCollection = contactSummaryService.getContactListForInvestigation(publicHealthCaseUID);
            pageProxyVO.setTheCTContactSummaryDTCollection(contactCollection);
        }


        return pageProxyVO;
    }

    /**
     * This method Associates or disassociates the observation(LAb or MORB) to Investigation from
     * the manage observation page
     * @param investigationUID -- The UID for the investigation to which observation is to be associated or disassociates
     */

    private void setObservationAssociationsImpl(Long investigationUID, Collection<LabReportSummaryContainer> reportSumVOCollection) throws DataProcessingException {
        // false flag indicates that investigation already exists and LAB/MORB
        // is associated to it using manage associations page
        setObservationAssociationsImpl(investigationUID, reportSumVOCollection, false);
    }

    /**
     * Nothing in here for LabResult Proxy Yet
     * */
    @SuppressWarnings({"java:S3776", "java:S1066"})
    protected void updateAutoResendNotifications(BaseContainer vo)
    {
        try {
            logger.info("enter NNDMessageSenderHelper.updateAutoResendNotifications--------------");
            if(
                    !(vo instanceof LabResultProxyContainer)
                            &&!(vo instanceof InvestigationContainer)
                            &&!(vo instanceof PageActProxyContainer)
                            &&!(vo instanceof PamProxyContainer)
            )
            {
                throw new DataProcessingException("vo not instance of VaccinationProxyVO,LabResultProxyVO, or MorbidityProxyVO,PamProxyVO, SummaryReportProxyVO");
            }
            Collection<Object>  notSumVOColl =null;
            PublicHealthCaseDto phcDT = null;


            if(
                    vo instanceof InvestigationContainer
                            || vo instanceof PamProxyContainer
                            ||  vo instanceof PageActProxyContainer
            ){
                if(vo instanceof InvestigationContainer)
                {
                    InvestigationContainer invVO = (InvestigationContainer)vo;
                    phcDT = invVO.thePublicHealthCaseContainer.getThePublicHealthCaseDto();
                    notSumVOColl = invVO.getTheNotificationSummaryVOCollection();
                }
                if(
                        vo instanceof InvestigationContainer
                                || vo instanceof PamProxyContainer
                                || vo instanceof PageActProxyContainer
                )
                {
                    if(notSumVOColl!=null && !notSumVOColl.isEmpty()){
                        for (Object o : notSumVOColl) {
                            NotificationSummaryContainer notSummaryVO = (NotificationSummaryContainer) o;
                            if (notSummaryVO.getIsHistory().equals("F") && !notSummaryVO.getAutoResendInd().equals("F")) {
                                Long notificationUid = notSummaryVO.getNotificationUid();
                                String phcCd = phcDT.getCd();
                                String phcClassCd = phcDT.getCaseClassCd();
                                String progAreaCd = phcDT.getProgAreaCd();
                                String jurisdictionCd = phcDT.getJurisdictionCd();
                                String sharedInd = phcDT.getSharedInd();

                                // retrieve the status change
                                boolean caseStatusChange = phcDT.isCaseStatusDirty();
                                updateNotification(false, notificationUid, phcCd, phcClassCd, progAreaCd, jurisdictionCd, sharedInd, caseStatusChange);

                            }
                        }
                    }
                }

            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }


    }
    @SuppressWarnings({"java:S1172", "java:S2589"})
    private  void updateNotification(boolean isSummaryCase, Long notificationUid, String phcCd,
                                   String phcClassCd, String progAreaCd, String jurisdictionCd,
                                   String sharedInd, boolean caseStatusChange) throws DataProcessingException {
        boolean checkNotificationPermission1 = true;//nbsSecurityObj.getPermission(NBSBOLookup.NOTIFICATION, NBSOperationLookup.CREATENEEDSAPPROVAL,progAreaCd,jurisdictionCd,sharedInd);
        String businessTriggerCd;
        businessTriggerCd = NEDSSConstant.NOT_CR_APR;


        var notification = notificationService.getNotificationById(notificationUid);
        NotificationContainer notificationContainer = new NotificationContainer();
        if (notification != null) {
            notificationContainer.setTheNotificationDT(notification);
        }
        NotificationDto newNotificationDT;
        NotificationDto notificationDT = notificationContainer.getTheNotificationDT();
        notificationDT.setProgAreaCd(progAreaCd);
        notificationDT.setJurisdictionCd(jurisdictionCd);
        notificationDT.setCaseConditionCd(phcCd);
        notificationDT.setSharedInd(sharedInd);
        notificationDT.setCaseClassCd(phcClassCd);
        notificationContainer.setItDirty(true);
        notificationDT.setItDirty(true);

        //retreive the new NotificationDT generated by PrepareVOUtils
        newNotificationDT = (NotificationDto) prepareAssocModelHelper.prepareVO(
                notificationDT, NBSBOLookup.NOTIFICATION, businessTriggerCd,
                "Notification", NEDSSConstant.BASE, notificationDT.getVersionCtrlNbr());

        //replace old NotificationDT in NotificationContainer with the new NotificationDT
        notificationContainer.setTheNotificationDT(newNotificationDT);

        // If the user has "NEEDS APPROVAL" permissions and the notification
        // is in AUTO_RESEND status, new record is created for review.
        // This record is visible in Updated Notifications Queue

        if(checkNotificationPermission1 &&
                (notificationDT.getAutoResendInd().equalsIgnoreCase("T"))){
            UpdatedNotificationDto updatedNotification = new UpdatedNotificationDto();

            updatedNotification.setAddTime(new Timestamp(System.currentTimeMillis()));
            updatedNotification.setAddUserId(AuthUtil.authUser.getNedssEntryId());
            updatedNotification.setCaseStatusChg(caseStatusChange);
            updatedNotification.setItNew(true);
            updatedNotification.setNotificationUid(notificationDT.getNotificationUid());
            updatedNotification.setStatusCd("A");
            updatedNotification.setCaseClassCd(notificationDT.getCaseClassCd());
            notificationContainer.setTheUpdatedNotificationDto(updatedNotification);
        }

        notificationService.saveNotification(notificationContainer);

        logger.info("updateNotification on NNDMessageSenderHelper complete");
    }//updateNotification

    private InvestigationContainer getInvestigationProxy(Long publicHealthCaseUID) throws DataProcessingException {

        return getInvestigationProxyLite(publicHealthCaseUID, false);
    }
    @SuppressWarnings("java:S3776")
    private InvestigationContainer getInvestigationProxyLite(Long publicHealthCaseUID, boolean lite) throws DataProcessingException {
        var investigationProxyVO = new InvestigationContainer();
        PublicHealthCaseDto thePublicHealthCaseDto;
        PublicHealthCaseContainer thePublicHealthCaseContainer;
        ArrayList<Object>  thePersonVOCollection  = new ArrayList<> (); // Person (VO)
        ArrayList<Object>  theOrganizationVOCollection  = new ArrayList<> (); // Organization (VO)
        ArrayList<Object>  theMaterialVOCollection  = new ArrayList<> (); // Material (VO)
        ArrayList<ObservationContainer>  theObservationVOCollection  = new ArrayList<> (); // Observation (VO)
        ArrayList<Object>  theInterventionVOCollection  = new ArrayList<> (); // Itervention (VO)
        ArrayList<Object>  theEntityGroupVOCollection  = new ArrayList<> (); // Group (VO)
        ArrayList<Object>  theNonPersonLivingSubjectVOCollection  = new ArrayList<> (); // NPLS (VO)
        ArrayList<Object>  thePlaceVOCollection  = new ArrayList<> (); // Place (VO)
        ArrayList<Object>  theReferralVOCollection  = new ArrayList<> (); // Referral (VO)
        ArrayList<Object>  thePatientEncounterVOCollection  = new ArrayList<> (); // PatientEncounter (VO)
        ArrayList<Object>  theClinicalDocumentVOCollection  = new ArrayList<> (); // Clinical Document (VO)

        // Summary Collections
        ArrayList<StateDefinedFieldDataDto>  theStateDefinedFieldDTCollection  = new ArrayList<> ();
        ArrayList<Object>  theTreatmentSummaryVOCollection;
        ArrayList<Object>  theDocumentSummaryVOCollection;

        try {
            thePublicHealthCaseContainer = publicHealthCaseRepositoryUtil.loadObject(publicHealthCaseUID);
            thePublicHealthCaseContainer.getThePublicHealthCaseDto().setAddUserName(AuthUtil.authUser.getUserId());
            thePublicHealthCaseContainer.getThePublicHealthCaseDto().setLastChgUserName(AuthUtil.authUser.getUserId());
            thePublicHealthCaseDto = thePublicHealthCaseContainer.getThePublicHealthCaseDto();
            Long PatientGroupID = thePublicHealthCaseDto.getPatientGroupId();
            if (PatientGroupID != null) {
                var entityGrp = publicHealthCaseRepositoryUtil.getEntityGroup(thePublicHealthCaseDto.getPatientGroupId());
                if (entityGrp != null) {
                    EntityGroupContainer entityGroupContainer = new EntityGroupContainer();
                    entityGroupContainer.setTheEntityGroupDT(entityGrp);
                    theEntityGroupVOCollection.add(entityGroupContainer);
                }
            }
            logger.debug("PatientGroupID = {}", PatientGroupID);
            String strTypeCd;
            String strClassCd;
            String recordStatusCd;
            Long nEntityID;
            ParticipationDto participationDT ;

            Iterator<ParticipationDto>  participationIterator = thePublicHealthCaseContainer.
                    getTheParticipationDTCollection().iterator();
            logger.debug("ParticipationDTCollection() = {}", thePublicHealthCaseContainer.getTheParticipationDTCollection());
            while (participationIterator.hasNext()) {
                participationDT = participationIterator.next();
                nEntityID = participationDT.getSubjectEntityUid();
                strClassCd = participationDT.getSubjectClassCd();
                recordStatusCd = participationDT.getRecordStatusCd();
                if (strClassCd != null &&
                        strClassCd.compareToIgnoreCase(NEDSSConstant.PLACE) == 0 &&
                        recordStatusCd != null &&
                        recordStatusCd.equals(NEDSSConstant.RECORD_STATUS_ACTIVE) && !lite) {

                    var plc = publicHealthCaseRepositoryUtil.getPlace(nEntityID);
                    if (plc != null) {
                        PlaceContainer placeContainer = new PlaceContainer();
                        placeContainer.setThePlaceDT(plc);
                        thePlaceVOCollection.add(placeContainer);
                    }
                    continue;
                }
                if (strClassCd != null &&
                        strClassCd.compareToIgnoreCase(NEDSSConstant.
                                NONPERSONLIVINGSUBJECT) ==
                                0 && recordStatusCd != null &&
                        recordStatusCd.equals(NEDSSConstant.RECORD_STATUS_ACTIVE)) {
                    var nonpDto = publicHealthCaseRepositoryUtil.getNonPersonLivingSubject(nEntityID);
                    if (nonpDto != null) {
                        NonPersonLivingSubjectContainer nonPersonLivingSubjectContainer = new NonPersonLivingSubjectContainer();
                        nonPersonLivingSubjectContainer.setTheNonPersonLivingSubjectDT(nonpDto);
                        theNonPersonLivingSubjectVOCollection.add(nonPersonLivingSubjectContainer);

                    }
                    continue;
                }
                if (strClassCd != null &&
                        strClassCd.compareToIgnoreCase(NEDSSConstant.ORGANIZATION) == 0 &&
                        recordStatusCd != null &&
                        recordStatusCd.equals(NEDSSConstant.RECORD_STATUS_ACTIVE)) {
                    theOrganizationVOCollection.add(organizationRepositoryUtil.loadObject(nEntityID, null));
                    continue;
                }
                if (strClassCd != null &&
                        strClassCd.compareToIgnoreCase(NEDSSConstant.PERSON) == 0 &&
                        recordStatusCd != null &&
                        recordStatusCd.equals(NEDSSConstant.RECORD_STATUS_ACTIVE)) {
                    thePersonVOCollection.add(patientRepositoryUtil.loadPerson(nEntityID));
                    continue;
                }
                if (strClassCd != null &&
                        strClassCd.compareToIgnoreCase(NEDSSConstant.MATERIAL) == 0 &&
                        recordStatusCd != null &&
                        recordStatusCd.equals(NEDSSConstant.RECORD_STATUS_ACTIVE)) {
                    theMaterialVOCollection.add(materialService.loadMaterialObject(nEntityID));
                    continue;
                }
                if (strClassCd != null &&
                        strClassCd.compareToIgnoreCase(NEDSSConstant.ENTITYGROUP) == 0 &&
                        recordStatusCd != null &&
                        recordStatusCd.equals(NEDSSConstant.RECORD_STATUS_ACTIVE)) {

                    var entityGrp = publicHealthCaseRepositoryUtil.getEntityGroup(nEntityID);
                    if (entityGrp != null) {
                        EntityGroupContainer entityGroupContainer = new EntityGroupContainer();
                        entityGroupContainer.setTheEntityGroupDT(entityGrp);
                        theEntityGroupVOCollection.add(entityGroupContainer);
                    }
                }
            }

            ActRelationshipDto actRelationshipDT;
            //Get the Vaccinations for a PublicHealthCase/Investigation

            // Populate the ACT collections in the results
            for (ActRelationshipDto actRelationshipDto : thePublicHealthCaseContainer.
                    getTheActRelationshipDTCollection()) {
                actRelationshipDT = actRelationshipDto;
                Long nSourceActID = actRelationshipDT.getSourceActUid();
                strClassCd = actRelationshipDT.getSourceClassCd();
                strTypeCd = actRelationshipDT.getTypeCd();
                recordStatusCd = actRelationshipDT.getRecordStatusCd();

                if (strClassCd != null && strClassCd.compareToIgnoreCase(NEDSSConstant.CLINICAL_DOCUMENT_CLASS_CODE) == 0
                        && recordStatusCd != null && recordStatusCd.equals(NEDSSConstant.RECORD_STATUS_ACTIVE)) {

                    var doc = publicHealthCaseRepositoryUtil.getClinicalDocument(nSourceActID);
                    if (doc != null) {
                        ClinicalDocumentContainer documentContainer = new ClinicalDocumentContainer();
                        documentContainer.setTheClinicalDocumentDT(doc);
                        theClinicalDocumentVOCollection.add(documentContainer);

                    }
                    continue;
                }
                if (strClassCd != null &&
                        strClassCd.compareToIgnoreCase(NEDSSConstant.REFERRAL_CLASS_CODE) == 0
                        && recordStatusCd != null &&
                        recordStatusCd.equals(NEDSSConstant.RECORD_STATUS_ACTIVE)) {
                    var ref = publicHealthCaseRepositoryUtil.getReferral(nSourceActID);
                    if (ref != null) {
                        ReferralContainer referralContainer = new ReferralContainer();
                        referralContainer.setTheReferralDT(ref);
                        theReferralVOCollection.add(referralContainer);
                    }
                    continue;
                }
                if (strClassCd != null &&
                        strClassCd.compareToIgnoreCase(NEDSSConstant.
                                PATIENT_ENCOUNTER_CLASS_CODE) == 0
                        && recordStatusCd != null &&
                        recordStatusCd.equals(NEDSSConstant.RECORD_STATUS_ACTIVE)) {
                    var encounter = publicHealthCaseRepositoryUtil.getPatientEncounter(nSourceActID);
                    if (encounter != null) {
                        PatientEncounterContainer patientEncounterContainer = new PatientEncounterContainer();
                        patientEncounterContainer.setThePatientEncounterDT(encounter);
                        thePatientEncounterVOCollection.add(patientEncounterContainer);

                    }
                    continue;
                }

                if (strClassCd != null &&
                        strClassCd.compareToIgnoreCase(NEDSSConstant.
                                OBSERVATION_CLASS_CODE) ==
                                0 && strTypeCd != null &&
                        strTypeCd.equals(NEDSSConstant.PHC_INV_FORM)) {
                    ObservationContainer parentObservationVO = observationRepositoryUtil.loadObject(nSourceActID);
                    theObservationVOCollection = (ArrayList<ObservationContainer>) observationRepositoryUtil.retrieveObservationQuestion(nSourceActID);
                    theObservationVOCollection.add(parentObservationVO);
                }
            }

            investigationProxyVO.setThePublicHealthCaseContainer(thePublicHealthCaseContainer);
            investigationProxyVO.setThePlaceVOCollection(thePlaceVOCollection);
            investigationProxyVO.setTheNonPersonLivingSubjectVOCollection(
                    theNonPersonLivingSubjectVOCollection);
            investigationProxyVO.setTheOrganizationVOCollection(
                    theOrganizationVOCollection);
            investigationProxyVO.setThePersonVOCollection(thePersonVOCollection);
            investigationProxyVO.setTheMaterialVOCollection(theMaterialVOCollection);
            investigationProxyVO.setTheEntityGroupVOCollection(
                    theEntityGroupVOCollection);
            investigationProxyVO.setTheInterventionVOCollection(
                    theInterventionVOCollection);
            investigationProxyVO.setTheClinicalDocumentVOCollection(
                    theClinicalDocumentVOCollection);
            investigationProxyVO.setTheReferralVOCollection(theReferralVOCollection);
            investigationProxyVO.setThePatientEncounterVOCollection(
                    thePatientEncounterVOCollection);
            investigationProxyVO.setTheObservationVOCollection(
                    theObservationVOCollection);

            try {
                if(!lite) {
                    theStateDefinedFieldDTCollection  = new ArrayList<>(ldfService.getLDFCollection(publicHealthCaseUID, investigationProxyVO.getBusinessObjectName()));
                }
            }
            catch (Exception e) {
                logger.error("Exception occured while retrieving LDFCollection<Object>  = {}", e.getMessage()); //NOSONAR
            }

            if (theStateDefinedFieldDTCollection  != null) {
                investigationProxyVO.setTheStateDefinedFieldDataDTCollection(theStateDefinedFieldDTCollection);
            }

            Collection<Object>  labSumVOCol = new ArrayList<> ();
            HashMap<Object,Object> labSumVOMap;
            if (!lite)
            {
                String labReportViewClause = queryHelper.getDataAccessWhereClause(NBSBOLookup.OBSERVATIONLABREPORT, "VIEW", "obs");
                labReportViewClause = labReportViewClause != null? AND_UPPERCASE + labReportViewClause:"";

                Collection<UidSummaryContainer>  LabReportUidSummarVOs = observationSummaryService.findAllActiveLabReportUidListForManage(publicHealthCaseUID,labReportViewClause);
                String uidType = LABORATORY_UID;
                Collection<?>  labReportSummaryVOCollection;
                LabReportSummaryContainer labReportSummaryVOs;

                if(LabReportUidSummarVOs != null && !LabReportUidSummarVOs.isEmpty())
                {
                    labSumVOMap = retrieveLabReportSummaryRevisited(LabReportUidSummarVOs,false, uidType);
                    if(labSumVOMap.containsKey(LAB_EVENT_LIST))
                    {
                        labReportSummaryVOCollection  = (ArrayList<?> )labSumVOMap.get(LAB_EVENT_LIST);
                        for (Object o : labReportSummaryVOCollection) {
                            labReportSummaryVOs = (LabReportSummaryContainer) o;
                            labSumVOCol.add(labReportSummaryVOs);

                        }
                    }
                    logger.debug("Size of labreport Collection<Object>  : {}", labSumVOCol.size());
                }
            }
            else {
                logger.debug("user has no permission to view ObservationSummaryVO collection");
            }

            if (labSumVOCol != null) {
                investigationProxyVO.setTheLabReportSummaryVOCollection(labSumVOCol);

            }

            processingInvestigationSummary( investigationProxyVO,
                     thePublicHealthCaseContainer, lite);

            if (!lite)
            {
                logger.debug("About to get TreatmentSummaryList for Investigation");
                theTreatmentSummaryVOCollection  = new ArrayList<> ((retrieveSummaryService.retrieveTreatmentSummaryVOForInv(publicHealthCaseUID)).values());
                logger.debug("Number of treatments found: {}", theTreatmentSummaryVOCollection.size());
                investigationProxyVO.setTheTreatmentSummaryVOCollection(
                        theTreatmentSummaryVOCollection);
            }

            if (!lite)
            {
                theDocumentSummaryVOCollection  = new ArrayList<> (retrieveSummaryService.retrieveDocumentSummaryVOForInv(publicHealthCaseUID).values());
                investigationProxyVO.setTheDocumentSummaryVOCollection(theDocumentSummaryVOCollection);
            }

            if (!lite)
            {
                Collection<Object> contactCollection= contactSummaryService.getContactListForInvestigation(publicHealthCaseUID);

                investigationProxyVO.setTheCTContactSummaryDTCollection(contactCollection);
            }

        }
        catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }

        return investigationProxyVO;
    }

    private HashMap<Object, Object> retrieveLabReportSummaryRevisited(Collection<UidSummaryContainer> labReportUids, boolean isCDCFormPrintCase, String uidType) throws DataProcessingException {
        return getObservationSummaryListForWorkupRevisited(labReportUids, isCDCFormPrintCase, uidType);
    }
    @SuppressWarnings("java:S3776")
    private HashMap<Object, Object> getObservationSummaryListForWorkupRevisited(Collection<UidSummaryContainer> uidList,boolean isCDCFormPrintCase, String uidType) throws DataProcessingException {
        ArrayList<Object>  labSummList = new ArrayList<> ();
        ArrayList<Object>  labEventList = new ArrayList<> ();
        int count = 0;


        Long providerUid;

        if (uidList != null) {
            String dataAccessWhereClause = queryHelper.getDataAccessWhereClause(NBSBOLookup.OBSERVATIONLABREPORT, "VIEW", "");
            if (dataAccessWhereClause == null) {
                dataAccessWhereClause = "";
            }
            else {
                dataAccessWhereClause = AND_UPPERCASE + dataAccessWhereClause;
            }


            Collection<Observation_Lab_Summary_ForWorkUp_New> labList = new ArrayList<> ();
            Long LabAsSourceForInvestigation = null;
            try {

                Timestamp fromTime = null;
                Iterator<UidSummaryContainer> itLabId = uidList.iterator();
                while (itLabId.hasNext()) {
                    if(uidType.equals("PERSON_PARENT_UID")){
                        Long uid = itLabId.next().getUid();
                        var res = observationSummaryRepository.findLabSummaryForWorkupNew(uid, dataAccessWhereClause);
                        if (res.isPresent()) {
                            labList = res.get();
                            count = count + 1;

                        }
                    }
                    else if(uidType.equals(LABORATORY_UID))
                    {
                        UidSummaryContainer vo =  itLabId.next();
                        Long observationUid = vo.getUid();
                        fromTime = vo.getAddTime();
                        if(vo.getStatusTime()!=null && vo.getStatusTime().compareTo(fromTime)==0){
                            LabAsSourceForInvestigation=vo.getUid();
                        }

                        var res = observationRepository.findById(observationUid);
                        if (res.isPresent()) {
                            var sum = new Observation_Lab_Summary_ForWorkUp_New(res.get());
                            labList.add(sum);
                            count = count + 1;
                        }
                    }
                    if(labList != null) {
                        for (Observation_Lab_Summary_ForWorkUp_New observationLabSummaryForWorkUpNew : labList) {
                            LabReportSummaryContainer labRepVO = new LabReportSummaryContainer(observationLabSummaryForWorkUpNew);
                            labRepVO.setActivityFromTime(fromTime);
                            LabReportSummaryContainer labRepSumm = null;
                            LabReportSummaryContainer labRepEvent = null;
                            Map<Object, Object> uidMap = observationSummaryService.getLabParticipations(labRepVO.getObservationUid());
                            if (uidMap != null && uidMap.containsKey(NEDSSConstant.PAR110_TYP_CD)) {
                                if (labRepVO.getRecordStatusCd() != null && (labRepVO.getRecordStatusCd().equals("UNPROCESSED"))) {
                                    labRepSumm = labRepVO;
                                    labRepSumm.setMPRUid((Long) uidMap.get(NEDSSConstant.PAR110_TYP_CD));
                                }
                                if (labRepVO.getRecordStatusCd() != null && !labRepVO.getRecordStatusCd().equals("LOG_DEL")) {
                                    labRepEvent = labRepVO;
                                    labRepEvent.setMPRUid((Long) uidMap.get(NEDSSConstant.PAR110_TYP_CD));
                                }
                            }

                            ArrayList<Object> providerDetails = observationSummaryService.getProviderInfo(labRepVO.getObservationUid(), "ORD");
                            ArrayList<Object> actIdDetails = observationSummaryService.getActIdDetails(labRepVO.getObservationUid());
                            Map<Object, Object> associationsMap = observationSummaryService.getAssociatedInvList(labRepVO.getObservationUid(), "OBS");
                            if (labRepEvent != null) {
                                labRepEvent.setAssociationsMap(associationsMap);
                            }
                            if (labRepSumm != null) {
                                labRepSumm.setAssociationsMap(associationsMap);
                            }
                            if (uidMap != null && uidMap.containsKey(NEDSSConstant.PAR111_TYP_CD) && labRepEvent != null) {
                                labRepEvent.setReportingFacility(observationSummaryService.getReportingFacilityName((Long) uidMap.get(NEDSSConstant.PAR111_TYP_CD)));
                            }
                            if (uidMap != null && uidMap.containsKey(NEDSSConstant.PAR111_TYP_CD) && labRepSumm != null) {
                                labRepSumm.setReportingFacility(observationSummaryService.getReportingFacilityName((Long) uidMap.get(NEDSSConstant.PAR111_TYP_CD)));
                            }

                            if (uidMap != null && uidMap.containsKey(NEDSSConstant.PAR101_TYP_CD) && labRepEvent != null) {
                                labRepEvent.setOrderingFacility(observationSummaryService.getReportingFacilityName((Long) uidMap.get(NEDSSConstant.PAR101_TYP_CD)));
                            }
                            if (uidMap != null && uidMap.containsKey(NEDSSConstant.PAR101_TYP_CD) && labRepSumm != null) {
                                labRepSumm.setOrderingFacility(observationSummaryService.getReportingFacilityName((Long) uidMap.get(NEDSSConstant.PAR101_TYP_CD)));
                            }

                            if (uidMap != null && uidMap.containsKey(NEDSSConstant.PAR104_TYP_CD) && labRepEvent != null) {
                                var code = observationSummaryService.getSpecimanSource((Long) uidMap.get(NEDSSConstant.PAR104_TYP_CD));
                                labRepEvent.setSpecimenSource(catchingValueService.getCodedValue("SPECMN_SRC", code));
                            }
                            if (uidMap != null && uidMap.containsKey(NEDSSConstant.PAR104_TYP_CD) && labRepSumm != null) {
                                var code = observationSummaryService.getSpecimanSource((Long) uidMap.get(NEDSSConstant.PAR104_TYP_CD));
                                labRepSumm.setSpecimenSource(catchingValueService.getCodedValue("SPECMN_SRC", code));
                            }

                            providerUid = observationSummaryService.getProviderInformation(providerDetails, labRepEvent);

                            if (isCDCFormPrintCase && providerUid != null && LabAsSourceForInvestigation != null) {
                                ProviderDataForPrintContainer providerDataForPrintVO = null;
                                if (labRepEvent != null && labRepEvent.getProviderDataForPrintVO() == null) {
                                    providerDataForPrintVO = new ProviderDataForPrintContainer();
                                    labRepEvent.setProviderDataForPrintVO(providerDataForPrintVO);
                                }
                                Long orderingFacilityUid = null;
                                if (uidMap != null && uidMap.get(NEDSSConstant.PAR101_TYP_CD) != null) {
                                    orderingFacilityUid = (Long) uidMap.get(NEDSSConstant.PAR101_TYP_CD);
                                }
                                if (orderingFacilityUid != null) {
                                    var org = organizationRepositoryUtil.loadObject(orderingFacilityUid, null);
                                    if (org != null && !org.getTheOrganizationNameDtoCollection().isEmpty()) {
                                        OrganizationNameDto dt = null;
                                        dt = org.getTheOrganizationNameDtoCollection().stream().findFirst().get();
                                        providerDataForPrintVO.setFacilityName(dt.getNmTxt());
                                    }
                                    observationSummaryService.getOrderingFacilityAddress(providerDataForPrintVO, orderingFacilityUid);
                                    observationSummaryService.getOrderingFacilityPhone(providerDataForPrintVO, orderingFacilityUid);
                                }
                                if (providerUid != null) {
                                    observationSummaryService.getOrderingPersonAddress(providerDataForPrintVO, providerUid);
                                    observationSummaryService.getOrderingPersonPhone(providerDataForPrintVO, providerUid);
                                }
                            }

                            observationSummaryService.getProviderInformation(providerDetails, labRepSumm);


                            if (actIdDetails != null && !actIdDetails.isEmpty() && labRepEvent != null) {
                                Object[] accessionNumber = actIdDetails.toArray();
                                if (accessionNumber[0] != null) {
                                    labRepEvent.setAccessionNumber((String) accessionNumber[0]);
                                }
                            }
                            if (actIdDetails != null && !actIdDetails.isEmpty() && labRepSumm != null) {
                                Object[] accessionNumber = actIdDetails.toArray();
                                if (accessionNumber[0] != null) {
                                    labRepSumm.setAccessionNumber((String) accessionNumber[0]);
                                }
                            }

                            if (labRepEvent != null)
                                labEventList.add(labRepEvent);
                            if (labRepSumm != null)
                                labSummList.add(labRepSumm);

                            Long ObservationUID = labRepVO.getObservationUid();
                            observationSummaryService.getTestAndSusceptibilities("COMP", ObservationUID, labRepEvent, labRepSumm);
                        }

                    }
                }
            }
            catch (Exception ex) {
                throw new DataProcessingException(ex.getMessage(), ex);
            }
        }


        this.populateDescTxtFromCachedValues(labSummList);
        this.populateDescTxtFromCachedValues(labEventList);
        HashMap<Object, Object> returnMap = new HashMap<>();
        returnMap.put("labSummList", labSummList);
        returnMap.put(LAB_EVENT_LIST, labEventList);
        return returnMap;
    } //end of getObservationSummaryVOCollectionForWorkup()

    @SuppressWarnings("java:S3776")
    protected void processingInvestigationSummary(InvestigationContainer investigationProxyVO,
                                                     PublicHealthCaseContainer thePublicHealthCaseContainer,
                                                     boolean lite) throws DataProcessingException {
        if(!lite) {
            investigationProxyVO.setTheNotificationSummaryVOCollection(retrieveSummaryService.notificationSummaryOnInvestigation(thePublicHealthCaseContainer, investigationProxyVO));

            if(investigationProxyVO.getTheNotificationSummaryVOCollection()!=null){
                for (Object o : investigationProxyVO.getTheNotificationSummaryVOCollection()) {
                    NotificationSummaryContainer notifVO = (NotificationSummaryContainer) o;
                    for (ActRelationshipDto actRelationDT : investigationProxyVO.getThePublicHealthCaseContainer().getTheActRelationshipDTCollection()) {
                        if ((notifVO.getCdNotif().equalsIgnoreCase(NEDSSConstant.CLASS_CD_SHARE_NOTF) ||
                                notifVO.getCdNotif().equalsIgnoreCase(NEDSSConstant.CLASS_CD_SHARE_NOTF_PHDC))
                                && notifVO.getNotificationUid().compareTo(actRelationDT.getSourceActUid()) == 0) {
                            actRelationDT.setShareInd(true);
                        }
                        if ((notifVO.getCdNotif().equalsIgnoreCase(NEDSSConstant.CLASS_CD_EXP_NOTF) ||
                                notifVO.getCdNotif().equalsIgnoreCase(NEDSSConstant.CLASS_CD_EXP_NOTF_PHDC)) &&
                                notifVO.getNotificationUid().compareTo(actRelationDT.getSourceActUid()) == 0) {
                            actRelationDT.setExportInd(true);
                        }
                        if (notifVO.getCdNotif().equalsIgnoreCase(NEDSSConstant.CLASS_CD_NOTF) && notifVO.getNotificationUid().compareTo(actRelationDT.getSourceActUid()) == 0) {
                            actRelationDT.setNNDInd(true);
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings({"java:S3776","java:S6541", "java:S1066"})
    protected void populateDescTxtFromCachedValues(Collection<Object> reportSummaryVOCollection) throws DataProcessingException {
        ReportSummaryInterface sumVO ;
        LabReportSummaryContainer labVO;
        ResultedTestSummaryContainer resVO;
        Iterator<ResultedTestSummaryContainer> resItor;
        ResultedTestSummaryContainer susVO;
        Iterator<Object> susItor;
        Collection<Object> susColl ;
        String tempStr = null;

        for (Object o : reportSummaryVOCollection) {
            sumVO = (LabReportSummaryContainer) o;
            if (sumVO instanceof LabReportSummaryContainer) {
                labVO = (LabReportSummaryContainer) sumVO;
                labVO.setType(NEDSSConstant.LAB_REPORT_DESC);
                if (labVO.getProgramArea() != null) {
                    tempStr = cacheApiService.getSrteCacheString(ObjectName.PROGRAM_AREA_CODES.name(), labVO.getProgramArea());
                    labVO.setProgramArea(tempStr);
                }
                if (labVO.getJurisdiction() != null) {
                    tempStr = cacheApiService.getSrteCacheString(ObjectName.JURISDICTION_CODES.name(), labVO.getJurisdiction());
                    if (!tempStr.isEmpty())
                        labVO.setJurisdiction(tempStr);
                }
                if (labVO.getStatus() != null) {
                    tempStr = catchingValueService.getCodeDescTxtForCd("ACT_OBJ_ST", labVO.getStatus());
                    if (tempStr != null)
                        labVO.setStatus(tempStr);
                }
                if (labVO.getTheResultedTestSummaryVOCollection() != null &&
                        !labVO.getTheResultedTestSummaryVOCollection().isEmpty()) {
                    resItor = labVO.getTheResultedTestSummaryVOCollection().iterator();
                    while (resItor.hasNext()) {
                        resVO =  resItor.next();


                        if (resVO.getCtrlCdUserDefined1() != null) {
                            if (resVO.getCtrlCdUserDefined1().equals("N") &&
                                    resVO.getCodedResultValue() != null &&
                                    !resVO.getCodedResultValue().equals("")) {
                                tempStr = cacheApiService.getSrteCacheString(ObjectName.LAB_RESULT_DESC.name(),resVO.getCodedResultValue());
                                resVO.setCodedResultValue(tempStr);
                            } else if (resVO.getCtrlCdUserDefined1().equals("Y") &&
                                    resVO.getOrganismName() != null && resVO.getOrganismCodeSystemCd() != null) {
                                if (resVO.getOrganismCodeSystemCd().equals("SNM")) {
                                    tempStr = cacheApiService.getSrteCacheString(ObjectName.SNOMED_CODE_BY_DESC.name(),resVO.getCodedResultValue());
                                    resVO.setOrganismName(tempStr);
                                } else {
                                    tempStr = cacheApiService.getSrteCacheString(ObjectName.LAB_RESULT_DESC_WITH_ORGANISM_NAME.name(),resVO.getCodedResultValue());
                                    resVO.setOrganismName(tempStr);
                                }
                            }
                        }
                        else {
                            if (resVO.getOrganismName() != null) {
                                if (resVO.getOrganismCodeSystemCd() != null) {
                                    if (resVO.getOrganismCodeSystemCd().equals("SNM")) {
                                        tempStr = cacheApiService.getSrteCacheString(ObjectName.SNOMED_CODE_BY_DESC.name(),resVO.getCodedResultValue());
                                        if (tempStr == null) {
                                            resVO.setOrganismName(resVO.getOrganismName());
                                        } else {
                                            resVO.setOrganismName(tempStr);
                                        }
                                    } else {
                                        tempStr = cacheApiService.getSrteCacheString(ObjectName.LAB_RESULT_DESC_WITH_ORGANISM_NAME.name(),resVO.getCodedResultValue());
                                        if (tempStr == null) {
                                            resVO.setOrganismName(resVO.getOrganismName());
                                        } else {
                                            resVO.setOrganismName(tempStr);
                                        }

                                    }
                                } else {
                                    resVO.setOrganismName(resVO.getOrganismName());
                                }
                            } else {
                                tempStr = cacheApiService.getSrteCacheString(ObjectName.LAB_RESULT_DESC_WITH_ORGANISM_NAME.name(),resVO.getCodedResultValue());
                                if (tempStr == null) {
                                    resVO.setOrganismName(resVO.getCodedResultValue());
                                } else {
                                    resVO.setOrganismName(tempStr);
                                }
                            }
                        }

                        if ((resVO.getCdSystemCd() != null) &&
                                (!(resVO.getCdSystemCd().equals("")))) {
                            if (resVO.getCdSystemCd().equals("LN")) {
                                if (resVO.getResultedTestCd() != null &&
                                        !resVO.getResultedTestCd().equals("")) {
                                    tempStr = cacheApiService.getSrteCacheString(ObjectName.LOIN_CODE_WITH_COMPONENT_NAME.name(),resVO.getResultedTestCd());
                                    if (tempStr != null && !tempStr.equals(""))
                                        resVO.setResultedTest(tempStr);
                                }
                            } else if (!resVO.getCdSystemCd().equals("LN")) {
                                if (resVO.getResultedTestCd() != null &&
                                        !resVO.getResultedTestCd().equals("")) {
                                    var res = labTestRepository.findLabTestByLabIdAndLabTestCode(resVO.getCdSystemCd(), resVO.getResultedTestCd());
                                    if (res.isPresent()) {
                                        tempStr = res.get().get(0).getLabResultDescTxt();
                                    }
                                    if (tempStr != null && !tempStr.equals(""))
                                        resVO.setResultedTest(tempStr);

                                }
                            }
                        }
                        // Added this for ER16368
                        if ((resVO.getResultedTestStatusCd() != null) && (!(resVO.getResultedTestStatusCd().equals("")))) {
                            tempStr = catchingValueService.getCodeDescTxtForCd("ACT_OBJ_ST", resVO.getResultedTestStatusCd());
                            if (tempStr != null && !tempStr.equals(""))
                                resVO.setResultedTestStatus(tempStr);
                        }
                        // End  ER16368
                        susColl = resVO.getTheSusTestSummaryVOColl();
                        if (susColl != null && !susColl.isEmpty()) {
                            susItor = susColl.iterator();
                            while (susItor.hasNext()) {
                                susVO = (ResultedTestSummaryContainer) susItor.next();

                                if (susVO.getCodedResultValue() != null &&
                                        !susVO.getCodedResultValue().equals("")) {
                                    tempStr = cacheApiService.getSrteCacheString(ObjectName.LAB_RESULT_DESC.name(),susVO.getCodedResultValue());
                                    if (tempStr != null && !tempStr.equals(""))
                                        susVO.setCodedResultValue(tempStr);
                                }
                                if (susVO.getCdSystemCd() != null &&
                                        !susVO.getCdSystemCd().equals("")) {
                                    if (susVO.getCdSystemCd().equals("LN")) {
                                        if (susVO.getResultedTestCd() != null &&
                                                !susVO.getResultedTestCd().equals("")) {
                                            tempStr = cacheApiService.getSrteCacheString(ObjectName.LOIN_CODE_WITH_COMPONENT_NAME.name(),susVO.getResultedTestCd());
                                            if (tempStr != null && !tempStr.equals("")) {
                                                susVO.setResultedTest(tempStr);
                                            }
                                        }
                                    } else {
                                        if (susVO.getResultedTestCd() != null && !susVO.getResultedTestCd().equals(""))
                                        {
                                            var res = labTestRepository.findLabTestByLabIdAndLabTestCode(resVO.getCdSystemCd(), resVO.getResultedTestCd());
                                            if (res.isPresent()) {
                                                tempStr = res.get().get(0).getLabResultDescTxt();
                                            }

                                            if (tempStr != null && !tempStr.equals("")) {
                                                susVO.setResultedTest(tempStr);
                                            }
                                        }
                                    }
                                }

                            } // inner while
                        }
                    } //outer while
                } //if
            }
        }
    }


}
