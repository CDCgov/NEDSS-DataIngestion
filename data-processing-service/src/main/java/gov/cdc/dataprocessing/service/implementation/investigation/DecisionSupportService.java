package gov.cdc.dataprocessing.service.implementation.investigation;

import gov.cdc.dataprocessing.constant.DecisionSupportConstants;
import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.ObjectName;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.base.BasePamContainer;
import gov.cdc.dataprocessing.model.container.model.*;
import gov.cdc.dataprocessing.model.dsma_algorithm.*;
import gov.cdc.dataprocessing.model.dto.dsm.DSMAlgorithmDto;
import gov.cdc.dataprocessing.model.dto.edx.EdxRuleManageDto;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsQuestionMetadata;
import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.dsm.DsmAlgorithm;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.stored_proc.PublicHealthCaseStoredProcRepository;
import gov.cdc.dataprocessing.repository.nbs.srte.model.ConditionCode;
import gov.cdc.dataprocessing.service.interfaces.cache.ICacheApiService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IAutoInvestigationService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IDecisionSupportService;
import gov.cdc.dataprocessing.service.model.decision_support.DsmLabMatchHelper;
import gov.cdc.dataprocessing.service.model.wds.WdsReport;
import gov.cdc.dataprocessing.utilities.GsonUtil;
import gov.cdc.dataprocessing.utilities.component.edx.EdxPhcrDocumentUtil;
import gov.cdc.dataprocessing.utilities.component.public_health_case.AdvancedCriteria;
import gov.cdc.dataprocessing.utilities.component.wds.ValidateDecisionSupport;
import gov.cdc.dataprocessing.utilities.component.wds.WdsObjectChecker;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.*;

import static gov.cdc.dataprocessing.utilities.component.edx.EdxPhcrDocumentUtil.REQUIRED;

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
public class DecisionSupportService implements IDecisionSupportService {
    private static final Logger logger = LoggerFactory.getLogger(DecisionSupportService.class);

    private final EdxPhcrDocumentUtil edxPhcrDocumentUtil;
    private final IAutoInvestigationService autoInvestigationService;
    private final ValidateDecisionSupport validateDecisionSupport;
    private final PublicHealthCaseStoredProcRepository publicHealthCaseStoredProcRepository;
    private final DsmAlgorithmService dsmAlgorithmService;
    private final AdvancedCriteria advancedCriteria;
    private final WdsObjectChecker wdsObjectChecker;
    private final ICacheApiService cacheApiService;

    public DecisionSupportService(EdxPhcrDocumentUtil edxPhcrDocumentUtil,
                                  IAutoInvestigationService autoInvestigationService,
                                  ValidateDecisionSupport validateDecisionSupport,
                                  PublicHealthCaseStoredProcRepository publicHealthCaseStoredProcRepository,
                                  DsmAlgorithmService dsmAlgorithmService, AdvancedCriteria advancedCriteria,
                                  WdsObjectChecker wdsObjectChecker, ICacheApiService cacheApiService) {
        this.edxPhcrDocumentUtil = edxPhcrDocumentUtil;
        this.autoInvestigationService = autoInvestigationService;
        this.validateDecisionSupport = validateDecisionSupport;
        this.publicHealthCaseStoredProcRepository = publicHealthCaseStoredProcRepository;
        this.dsmAlgorithmService = dsmAlgorithmService;
        this.advancedCriteria = advancedCriteria;
        this.wdsObjectChecker = wdsObjectChecker;
        this.cacheApiService = cacheApiService;
    }
    /*sort PublicHealthCaseDTs by add_time descending*/
    final Comparator<PublicHealthCaseDto> ADDTIME_ORDER = (e1, e2) -> e2.getAddTime().compareTo(e1.getAddTime()); //NOSONAR
    final Comparator<DsmLabMatchHelper> AlGORITHM_NM_ORDER = (e1, e2) -> e1.getAlgorithmNm().compareToIgnoreCase(e2.getAlgorithmNm()); //NOSONAR

    @SuppressWarnings({"java:S3776", "java:S135"})
    @Transactional
    // Was: validateProxyVO
    public EdxLabInformationDto validateProxyContainer(LabResultProxyContainer labResultProxyVO,
                                                       EdxLabInformationDto edxLabInformationDT) throws DataProcessingException {
        List<DsmLabMatchHelper> activeElrAlgorithmList = new ArrayList<>();
        var wdsExist = checkActiveWdsAlgorithm( edxLabInformationDT, activeElrAlgorithmList );
        if (!wdsExist) {
            return edxLabInformationDT;

        }
        Collection<ObservationContainer>  resultedTestColl=new ArrayList<>();
        Collection<String> resultedTestCodeColl =  new ArrayList<>();
        ObservationContainer orderedTestObservationVO;

        Map<Object, Object> questionIdentifierMap = null;
        try
        {
            Collection<PersonContainer> personVOCollection=new ArrayList<>();
            if (labResultProxyVO.getThePersonContainerCollection() != null)
            {
                personVOCollection=labResultProxyVO.getThePersonContainerCollection();
            }
            orderedTestObservationVO = setupObservationValuesForWds(edxLabInformationDT, labResultProxyVO, resultedTestColl, resultedTestCodeColl);

            //See if we have a matching algorithm for this lab in the order of Algorithm Names
            activeElrAlgorithmList.sort(AlGORITHM_NM_ORDER);


            List<WdsReport> wdsReports = new ArrayList<>();

            for (DsmLabMatchHelper dsmLabMatchHelper : activeElrAlgorithmList) {
                boolean criteriaMatch;


                //Was AlgorithmDocument
                Algorithm algorithmDocument;
                //reset for every algorithm processing
                edxLabInformationDT.setAssociatedPublicHealthCaseUid(-1L);
                edxLabInformationDT.setMatchingPublicHealthCaseDtoColl(null);
                //if returns true, lab matched algorithm, continue with the investigation criteria match is one exists.

                WdsReport wdsReport = dsmLabMatchHelper.isThisLabAMatch(
                        resultedTestCodeColl,
                        resultedTestColl,
                        edxLabInformationDT.getSendingFacilityClia(),
                        edxLabInformationDT.getSendingFacilityName()
                );
                boolean isLabMatched = wdsReport.isAlgorithmMatched();
                if (isLabMatched)
                {
                    algorithmDocument = dsmLabMatchHelper.getAlgorithmDocument();
                    criteriaMatch = true;
                }
                else
                {
                    // IF NOT MATCH FOUND CONTINUE and skip the comparing logic
                    wdsReports.add(wdsReport);
                    continue;
                }

                String conditionCode = null;
                if (algorithmDocument != null && algorithmDocument.getApplyToConditions() != null)
                {
                    List<CodedType> conditionArray = algorithmDocument.getApplyToConditions().getCondition();
                    for (CodedType codeType : conditionArray) {
                        conditionCode = codeType.getCode();
                    }
                }


                // Determine next step based on ACTION
                updateObservationBasedOnAction(
                        algorithmDocument,
                        criteriaMatch,
                        conditionCode,
                        orderedTestObservationVO,
                        personVOCollection,
                        edxLabInformationDT,
                        wdsReport,
                        questionIdentifierMap
                );

                if (edxLabInformationDT.isMatchingAlgorithm() && algorithmDocument != null)
                {
                    wdsReports.add(wdsReport);
                    edxLabInformationDT.setDsmAlgorithmName(algorithmDocument.getAlgorithmName());
                    break;
                }

                wdsReports.add(wdsReport);

            }

            edxLabInformationDT.getWdsReports().addAll(wdsReports);
            return edxLabInformationDT;

        } catch (Exception e) {
            throw new DataProcessingException("ERROR:-ValidateDecisionSupport.validateProxyVO No action has been specified. Please check." + e, e);
        }
    }

    @SuppressWarnings("java:S3776")
    protected boolean checkActiveWdsAlgorithm(EdxLabInformationDto edxLabInformationDT,
                                                             List<DsmLabMatchHelper> activeElrAlgorithmList ) throws DataProcessingException {
        boolean elrAlgorithmsPresent;
        // Validating existing WDS Algorithm
        try {
            WdsReport report = new WdsReport();
            Collection<DsmAlgorithm> algorithmCollection = selectDSMAlgorithmDTCollection();
            if (algorithmCollection == null || algorithmCollection.isEmpty())  {
                //no algorithms defined
                report.setAlgorithmMatched(false);
                report.setMessage("No WDS Algorithm found");
                edxLabInformationDT.getWdsReports().add(report);
                return false;
            }

            elrAlgorithmsPresent = false; //could be only inactive algorithms or only Case reports
            for (DsmAlgorithm dsmAlgorithm : algorithmCollection)
            {
                DSMAlgorithmDto algorithmDT = new DSMAlgorithmDto(dsmAlgorithm);
                String algorithmString = algorithmDT.getAlgorithmPayload();
                //skip inactive and case reports
                if (algorithmDT.getStatusCd() != null
                        && algorithmDT.getStatusCd().equals(NEDSSConstant.INACTIVE) ||
                        algorithmDT.getEventType() != null
                                && algorithmDT.getEventType().equals(NEDSSConstant.PHC_236))
                {
                    continue; //skip inactive
                }

                // Suppose to be Algorithm
                Algorithm algorithmDocument = null;

                if (algorithmString != null) {
                    algorithmDocument = parseAlgorithmXml(algorithmString);
                }
                //helper class DSMLabMatchHelper will assist with algorithm matching
                DsmLabMatchHelper dsmLabMatchHelper = null;
                try {
                    if (algorithmDocument != null)
                    {
                        dsmLabMatchHelper = new DsmLabMatchHelper(algorithmDocument);
                    }
                } catch (Exception e) {
                    //if one fails to parse - continue processing with error
                    logger.info(e.getMessage());
                }


                if (dsmLabMatchHelper != null) {
                    activeElrAlgorithmList.add(dsmLabMatchHelper);
                    elrAlgorithmsPresent = true;
                }
                //parseXmDocument(algorithmDocument);
            } //hasNext
            //didn't find any?

            if (!elrAlgorithmsPresent) {
                report.setAlgorithmMatched(false);
                report.setMessage("No active WDS Algorithm found");
                edxLabInformationDT.getWdsReports().add(report);
                return false;
            }

        } catch (Exception e1) {
            throw new DataProcessingException("ERROR:-ValidateDecisionSupport.validateProxyVO unable to process algorithm as NEDSSAppException . Please check."+e1);
        }
        return true;
    }


    protected ObservationContainer setupObservationValuesForWds(
                        EdxLabInformationDto edxLabInformationDT,
                        LabResultProxyContainer labResultProxyVO,
                        Collection<ObservationContainer>  resultedTestColl,
                        Collection<String> resultedTestCodeColl
    ) {
        ObservationContainer orderedTestObservationVO=null;

        for (ObservationContainer obsVO : labResultProxyVO.getTheObservationContainerCollection()) {

            String obsDomainCdSt1 = obsVO.getTheObservationDto().getObsDomainCdSt1();
            if (obsDomainCdSt1 != null && obsDomainCdSt1.equalsIgnoreCase(EdxELRConstant.ELR_RESULT_CD))
            {
                resultedTestColl.add(obsVO);
                String labResultedTestCd = obsVO.getTheObservationDto().getCd();
                if (obsVO.getTheObservationDto().getCd() == null)
                {
                    labResultedTestCd = obsVO.getTheObservationDto().getAltCd();
                }
                if (labResultedTestCd != null && !resultedTestCodeColl.contains(labResultedTestCd))
                {
                    resultedTestCodeColl.add(labResultedTestCd);
                }
            }
            else if (obsDomainCdSt1 != null && obsDomainCdSt1.equalsIgnoreCase(EdxELRConstant.ELR_ORDER_CD))
            {
                orderedTestObservationVO = obsVO;
                orderedTestObservationVO.getTheObservationDto().setObservationUid(edxLabInformationDT.getRootObserbationUid());
                orderedTestObservationVO.setTheParticipationDtoCollection(labResultProxyVO.getTheParticipationDtoCollection());
            }
        }
        return orderedTestObservationVO;
    }


    /**
     * Description:
     *  this return true, if Action is Review, Investigation, and Investigation with Notification
     * */
    protected boolean checkActionInvalid(Algorithm algorithmDocument, boolean criteriaMatch) {
        boolean result = false;
        if (!criteriaMatch || algorithmDocument == null || algorithmDocument.getAction() == null) {
            return false;
        }

        String code = "";

        if (algorithmDocument.getAction().getCreateInvestigation() != null)
        {
            code = algorithmDocument.getAction().getCreateInvestigation().getOnFailureToCreateInvestigation().getCode();
        }
        else if (algorithmDocument.getAction().getCreateInvestigationWithNND() != null)
        {
            code = algorithmDocument.getAction().getCreateInvestigationWithNND().getOnFailureToCreateInvestigation().getCode();
        }
        else if (algorithmDocument.getAction().getMarkAsReviewed() != null)
        {
            code = algorithmDocument.getAction().getMarkAsReviewed().getOnFailureToMarkAsReviewed().getCode();
        }

        if (code.equals("2") || code.equals("1")) {
            result = true;
        }

        return result;
    }


    /**
     * Description: True if action is not Reviewed
     * */
    private boolean checkActionNotMarkedAsReviewed(Algorithm algorithmDocument) {
        return (
                algorithmDocument.getAction() != null
                        && algorithmDocument.getAction().getMarkAsReviewed() == null
        )
                || (
                    algorithmDocument.getAction() != null
                            && algorithmDocument.getAction().getMarkAsReviewed() != null
                            && !algorithmDocument.getAction().getMarkAsReviewed().getOnFailureToMarkAsReviewed().getCode().equals("2")
        );
    }
    /**
     * Description: true if Action is not REVIEW and Adv Criteria is Applied
     * */
    private boolean checkActionNotMarkedAsReviewedAndAdvCriteriaApplied(Algorithm algorithmDocument, boolean applyAdvInvLogic) {
        return (
                checkActionNotMarkedAsReviewed(algorithmDocument)
        ) && applyAdvInvLogic;
    }

    @SuppressWarnings({"java:S107", "java:S6541", "java:S6541", "java:S3776"})
    protected void updateObservationBasedOnAction(Algorithm algorithmDocument,
                                                boolean criteriaMatch,
                                                String conditionCode,
                                                ObservationContainer orderedTestObservationVO,
                                                Collection<PersonContainer> personVOCollection,
                                                EdxLabInformationDto edxLabInformationDT,
                                                WdsReport wdsReport,
                                                Map<Object, Object> questionIdentifierMap) throws DataProcessingException {
        PageActProxyContainer pageActProxyContainer = null;
        PamProxyContainer pamProxyVO = null;
        PublicHealthCaseContainer publicHealthCaseContainer;

        var isActionValid = checkActionInvalid(algorithmDocument, criteriaMatch);
        if (isActionValid)
        {
            if (conditionCode != null)
            {
                questionIdentifierMap = edxPhcrDocumentUtil.loadQuestions(conditionCode);
            }
            edxLabInformationDT.setConditionCode(conditionCode);
            boolean isdateLogicValidForNewInv;
            boolean applyAdvInvLogic = false;

            boolean invLogicApplied =  algorithmDocument.getElrAdvancedCriteria().getInvLogic() != null
                    && algorithmDocument.getElrAdvancedCriteria().getInvLogic().getInvLogicInd().getCode().equals(NEDSSConstant.YES);
            // ADVANCE WDS check
            if (invLogicApplied) {
                applyAdvInvLogic = true;
            }

            EventDateLogicType eventDateLogicType = algorithmDocument.getElrAdvancedCriteria().getEventDateLogic();

            if (applyAdvInvLogic && eventDateLogicType != null)
            {
                // This check for matched Public Health Case in Investigation QUEUE
                isdateLogicValidForNewInv = specimenCollectionDateCriteria(eventDateLogicType, edxLabInformationDT);
            }
            else
            {
                isdateLogicValidForNewInv = true;
            }

            boolean isAdvancedInvCriteriaValid = false;
            boolean reviewActionApplied =  algorithmDocument.getAction() != null && algorithmDocument.getAction().getMarkAsReviewed() != null
                    && algorithmDocument.getAction().getMarkAsReviewed().getOnFailureToMarkAsReviewed().getCode().equals("2");
            if (reviewActionApplied && applyAdvInvLogic)
            {
                wdsReport.setAction("MARK_AS_REVIEWED");
                isAdvancedInvCriteriaValid = checkAdvancedInvCriteria(algorithmDocument, edxLabInformationDT, questionIdentifierMap);
            }

            else if (checkActionNotMarkedAsReviewedAndAdvCriteriaApplied(algorithmDocument, applyAdvInvLogic))
            {
                isAdvancedInvCriteriaValid = checkAdvancedInvCriteriaForCreateInvNoti(algorithmDocument, edxLabInformationDT, questionIdentifierMap);
            }

            if (
                    reviewActionApplied
                    && (
                        !applyAdvInvLogic
                        ||
                        (applyAdvInvLogic && !isdateLogicValidForNewInv && isAdvancedInvCriteriaValid)
                    )

            )
            {
                wdsReport.setAction("MARK_AS_REVIEWED");
                edxLabInformationDT.setDsmAlgorithmName(algorithmDocument.getAlgorithmName());
                if (conditionCode != null)
                {

                    ConditionCode condCode = GsonUtil.GSON.fromJson(cacheApiService.getSrteCacheObject(ObjectName.CONDITION_CODE.name(), conditionCode), ConditionCode.class);
                    if (condCode == null) {
                        condCode = new ConditionCode();
                    }
                    edxLabInformationDT.setConditionName(condCode.getConditionShortNm());
                }
                edxLabInformationDT.setMatchingAlgorithm(true);
                if (algorithmDocument.getAction() != null && algorithmDocument.getAction().getMarkAsReviewed() != null)
                {
                    edxLabInformationDT.setAction(DecisionSupportConstants.MARK_AS_REVIEWED);
                }
            }
            else if (
                        (checkActionNotMarkedAsReviewed(algorithmDocument))
                        && (
                            !applyAdvInvLogic
                            || (applyAdvInvLogic && isdateLogicValidForNewInv)
                            || (applyAdvInvLogic && !isdateLogicValidForNewInv && isAdvancedInvCriteriaValid)
                        )
            )
            {
                edxLabInformationDT.setMatchingAlgorithm(true);
                if (algorithmDocument.getAction() != null && algorithmDocument.getAction().getCreateInvestigation() != null)
                {
                    wdsReport.setAction("CREATE_INVESTIGATION");
                    edxLabInformationDT.setAction(DecisionSupportConstants.CREATE_INVESTIGATION_VALUE);
                }
                else if (algorithmDocument.getAction() != null && algorithmDocument.getAction().getCreateInvestigationWithNND() != null)
                {
                    wdsReport.setAction("CREATE_INVESTIGATION_WITH_NOTIFICATION");
                    edxLabInformationDT.setAction(DecisionSupportConstants.CREATE_INVESTIGATION_WITH_NND_VALUE);
                }

                edxLabInformationDT.setInvestigationType(algorithmDocument.getInvestigationType());
                //AUTO INVESTIGATION
                Object obj = autoInvestigationService.autoCreateInvestigation(orderedTestObservationVO, edxLabInformationDT);
                BasePamContainer pamVO;
                if (obj instanceof PageActProxyContainer)
                {
                    pageActProxyContainer = (PageActProxyContainer) obj;
                    publicHealthCaseContainer = pageActProxyContainer.getPublicHealthCaseContainer();
                    pamVO = pageActProxyContainer.getPageVO();
                }
                else
                {
                    pamProxyVO = (PamProxyContainer) obj;
                    publicHealthCaseContainer = pamProxyVO.getPublicHealthCaseContainer();
                    pamVO = pamProxyVO.getPamVO();
                }

                processAction(edxLabInformationDT, algorithmDocument);

                Map<Object, Object> applyMap = edxLabInformationDT.getEdxRuleApplyDTMap();
                Collection<Object> entityMapCollection = new ArrayList<>();
                if (applyMap != null && !applyMap.isEmpty() && questionIdentifierMap != null) {
                    Set<Object> set = applyMap.keySet();
                    for (Object o : set)
                    {
                        String questionId = (String) o;
                        EdxRuleManageDto edxRuleManageDT = (EdxRuleManageDto) applyMap.get(questionId);
                        NbsQuestionMetadata metaData = (NbsQuestionMetadata) questionIdentifierMap.get(questionId);
                        if (metaData == null) {
                            metaData = new NbsQuestionMetadata();
                        }
                        else {
                            logger.error("DecisionSupportService.updateObservationBasedOnAction: metaData is Null");
                        }
                        try {
                            if (metaData.getDataLocation() != null
                                    && metaData.getDataLocation().trim().toUpperCase().startsWith("PUBLIC_HEALTH_CASE"))
                            {
                                validateDecisionSupport.processNbsObject(edxRuleManageDT, publicHealthCaseContainer, metaData);
                            }
                            else if (metaData.getDataLocation() != null
                                    && metaData.getDataLocation().trim().toUpperCase().startsWith("NBS_CASE_ANSWER"))
                            {
                                validateDecisionSupport.processNBSCaseAnswerDT(edxRuleManageDT, publicHealthCaseContainer, pamVO, metaData);
                            }
                            else if (metaData.getDataLocation() != null
                                    && metaData.getDataLocation().trim().toUpperCase().startsWith("CONFIRMATION_METHOD.CONFIRMATION_METHOD_CD"))
                            {
                                validateDecisionSupport.processConfirmationMethodCodeDT(edxRuleManageDT, publicHealthCaseContainer, metaData);
                            }
                            else if (metaData.getDataLocation() != null
                                    && metaData.getDataLocation().trim().toUpperCase().startsWith("CONFIRMATION_METHOD.CONFIRMATION_METHOD_TIME"))
                            {
                                validateDecisionSupport.processConfirmationMethodTimeDT(edxRuleManageDT, publicHealthCaseContainer, metaData);
                            }
                            else if (metaData.getDataLocation() != null
                                    && metaData.getDataLocation().trim().toUpperCase().startsWith("ACT_ID.ROOT_EXTENSION_TXT"))
                            {
                                validateDecisionSupport.processActIds(edxRuleManageDT, publicHealthCaseContainer, metaData);
                            }
                            else if (metaData.getDataLocation() != null
                                    && metaData.getDataLocation().trim().toUpperCase().startsWith("CASE_MANAGEMENT")
                                    && obj instanceof PageActProxyContainer)
                            {
                                validateDecisionSupport.processNBSCaseManagementDT(edxRuleManageDT, publicHealthCaseContainer, metaData);
                            }
                            else if (metaData.getDataLocation() != null
                                    && metaData.getDataType().toUpperCase().startsWith("PART"))
                            {
                                entityMapCollection.add(edxRuleManageDT);
                                if (edxRuleManageDT.getParticipationTypeCode() == null
                                        || edxRuleManageDT.getParticipationUid() == null
                                        || edxRuleManageDT.getParticipationClassCode() == null) {
                                    throw new DataProcessingException("ValidateDecisionSupport.validateProxyVO Exception thrown for edxRuleManageDT:-" + edxRuleManageDT);
                                }
                            }
                        } catch (Exception e) {
                            throw new DataProcessingException("ERROR:-ValidateDecisionSupport.validateProxyVO Exception thrown Please check." + e, e);
                        }

                    }
                    validateDecisionSupport.processConfirmationMethodCodeDTRequired(publicHealthCaseContainer);
                }

                autoInvestigationService.transferValuesTOActProxyVO(pageActProxyContainer, pamProxyVO, personVOCollection, orderedTestObservationVO, entityMapCollection, questionIdentifierMap);

                if (questionIdentifierMap != null
                        && questionIdentifierMap.get(REQUIRED) != null)
                {
                    Map<Object, Object> nbsAnswerMap = pamVO.getPamAnswerDTMap();
                    Map<Object, Object> requireMap = (Map<Object, Object>) questionIdentifierMap.get(REQUIRED);
                    String errorText = edxPhcrDocumentUtil.requiredFieldCheck(requireMap, nbsAnswerMap);
                    publicHealthCaseContainer.setErrorText(errorText);
                }
                if (obj instanceof PageActProxyContainer) {
                    edxLabInformationDT.setPageActContainer((PageActProxyContainer) obj);
                }
                else
                {
                    edxLabInformationDT.setPamContainer((PamProxyContainer) obj);
                }

                ConditionCode condCode = GsonUtil.GSON.fromJson(cacheApiService.getSrteCacheObject(ObjectName.CONDITION_CODE.name(), conditionCode), ConditionCode.class);
                if (condCode == null) {
                    condCode = new ConditionCode();
                }
                edxLabInformationDT.setConditionName(condCode.getConditionShortNm());

            } else {
                edxLabInformationDT.setMatchingAlgorithm(false);
            }

        }
        else
        {
            wdsReport.setAction("NO_ACTION_FOUND");
            edxLabInformationDT.setMatchingAlgorithm(false);
        }
    }


    private Collection<DsmAlgorithm> selectDSMAlgorithmDTCollection() throws DataProcessingException {
        Collection<DsmAlgorithm> algorithmList;
        try
        {
            algorithmList = dsmAlgorithmService.findActiveDsmAlgorithm();
        } catch(Exception se2) {
            throw new DataProcessingException(se2.getMessage(), se2);
        }
        return algorithmList;
    }


    private Algorithm parseAlgorithmXml(String xmlPayLoadContent)
            throws DataProcessingException {
        Algorithm algorithmDocument;
        try {

            JAXBContext contextObj = JAXBContext.newInstance("gov.cdc.dataprocessing.model.dsma_algorithm");
            Unmarshaller unmarshaller = contextObj.createUnmarshaller();

            InputStream inputStream = new ByteArrayInputStream(xmlPayLoadContent.getBytes(StandardCharsets.UTF_8));
            algorithmDocument = (Algorithm) unmarshaller.unmarshal(inputStream);

        } catch (Exception e) {
            throw new DataProcessingException("HL7ELRValidateDecisionSupport.parseAlgorithmXml Invalid XML "+e);
        }

        return algorithmDocument;
    }

    /**
     * Execute when action in available
     * */
    @SuppressWarnings({"java:S6541", "java:S3776", "javaS3740"})
    protected boolean specimenCollectionDateCriteria(EventDateLogicType eventDateLogicType,
                                                     EdxLabInformationDto edxLabInformationDT) throws DataProcessingException {
        boolean isdateLogicValidForNewInv;
        String comparatorCode="";
        int value=0;
        Long associatedPHCUid= -1L;
        long mprUid= -1L;
        if(edxLabInformationDT.getPersonParentUid()>0)
        {
            mprUid =edxLabInformationDT.getPersonParentUid();
        }
        //see if the selection is no for time period, check for all any investigation with the desired condition code
        if(eventDateLogicType.getElrTimeLogic()!=null
                && eventDateLogicType.getElrTimeLogic().getElrTimeLogicInd()!=null
                && eventDateLogicType.getElrTimeLogic().getElrTimeLogicInd().getCode()!=null
                && eventDateLogicType.getElrTimeLogic().getElrTimeLogicInd().getCode().equals(NEDSSConstant.NO)
        ){
            var assocExistPhcWithPid = publicHealthCaseStoredProcRepository.associatedPublicHealthCaseForMprForCondCd(mprUid, edxLabInformationDT.getConditionCode());
            edxLabInformationDT.setMatchingPublicHealthCaseDtoColl(assocExistPhcWithPid);
        }
        //see if the selection is yes for time period, check for all any investigation with the desired condition code
        else if(eventDateLogicType.getElrTimeLogic()!=null
                && eventDateLogicType.getElrTimeLogic().getElrTimeLogicInd()!=null
                && eventDateLogicType.getElrTimeLogic().getElrTimeLogicInd().getCode()!=null
                && eventDateLogicType.getElrTimeLogic().getElrTimeLogicInd().getCode().equals(NEDSSConstant.YES))
        {
            if(eventDateLogicType.getWithinTimePeriod()!=null
                    && eventDateLogicType.getWithinTimePeriod().getComparatorCode()!=null
                    && eventDateLogicType.getWithinTimePeriod().getComparatorCode().getCode()!=null)
            {
                comparatorCode= eventDateLogicType.getWithinTimePeriod().getComparatorCode().getCode();
            }
            if(eventDateLogicType.getWithinTimePeriod()!=null
                    && eventDateLogicType.getWithinTimePeriod().getUnit()!=null
                    && eventDateLogicType.getWithinTimePeriod().getValue1()!=null)
            {
                value=eventDateLogicType.getWithinTimePeriod().getValue1().intValue();
            }

            Timestamp specimenCollectionDate=new Timestamp(edxLabInformationDT.getRootObservationContainer().getTheObservationDto().getEffectiveFromTime().getTime());
            long specimenCollectionDays = specimenCollectionDate.getTime()/(1000 * 60 * 60 * 24);

            if(comparatorCode.length() > 0 && mprUid > 0)
            {
                Collection<PublicHealthCaseDto> associatedPhcDTCollection = publicHealthCaseStoredProcRepository
                        .associatedPublicHealthCaseForMprForCondCd(mprUid, edxLabInformationDT.getConditionCode());

                if(associatedPhcDTCollection!=null && !associatedPhcDTCollection.isEmpty()){
                    for (PublicHealthCaseDto publicHealthCaseDto : associatedPhcDTCollection) {
                        boolean isdateLogicValidWithThisInv = true;
                        long dateCompare;
                        if (publicHealthCaseDto.getAssociatedSpecimenCollDate() != null)
                        {
                            dateCompare = publicHealthCaseDto.getAssociatedSpecimenCollDate().getTime() / (1000 * 60 * 60 * 24);
                        }
                        else
                        {
                            dateCompare = publicHealthCaseDto.getAddTime().getTime() / (1000 * 60 * 60 * 24);
                            publicHealthCaseDto.setAssociatedSpecimenCollDate(publicHealthCaseDto.getAddTime());
                        }
                        int daysDifference = (int) specimenCollectionDays - (int) dateCompare;

                        if (publicHealthCaseDto.getAssociatedSpecimenCollDate() != null) {
                            isdateLogicValidWithThisInv = specimenDateTimeCheck(
                                    comparatorCode, daysDifference,
                                    value, isdateLogicValidWithThisInv
                            );
                        }
                        if (isdateLogicValidWithThisInv) {
                            if (edxLabInformationDT.getMatchingPublicHealthCaseDtoColl() == null)
                            {
                                edxLabInformationDT.setMatchingPublicHealthCaseDtoColl(new ArrayList<>());
                            }
                            edxLabInformationDT.getMatchingPublicHealthCaseDtoColl().add(publicHealthCaseDto);
                        }
                    }
                }else{
                    isdateLogicValidForNewInv= true; //NOSONAR
                }
            }
        }
        if (edxLabInformationDT.getMatchingPublicHealthCaseDtoColl() != null
                && !edxLabInformationDT.getMatchingPublicHealthCaseDtoColl().isEmpty())
        {
            List phclist = new ArrayList<Object>(edxLabInformationDT.getMatchingPublicHealthCaseDtoColl());
            phclist.sort(ADDTIME_ORDER);
            associatedPHCUid = ((PublicHealthCaseDto)phclist.get(0)).getPublicHealthCaseUid();
            isdateLogicValidForNewInv= false;
        }
        else
        {
            isdateLogicValidForNewInv= true;
        }
        edxLabInformationDT.setAssociatedPublicHealthCaseUid(associatedPHCUid);
        return isdateLogicValidForNewInv;
    }

    @SuppressWarnings("java:S1871")
    protected boolean specimenDateTimeCheck(String comparatorCode, int daysDifference,
                                            int value, boolean isdateLogicValidWithThisInv) {
        if (comparatorCode.contains(NEDSSConstant.LESS_THAN_LOGIC) && daysDifference > value) {
            isdateLogicValidWithThisInv = false;
        }
        else if (comparatorCode.contains(NEDSSConstant.GREATER_THAN_LOGIC) && daysDifference < value) {
            isdateLogicValidWithThisInv = false;
        }
        else if (comparatorCode.equals(NEDSSConstant.EQUAL_LOGIC) && daysDifference != value) {
            isdateLogicValidWithThisInv = false;
        }
        else if (!comparatorCode.contains(NEDSSConstant.EQUAL_LOGIC) && daysDifference == value) {
            isdateLogicValidWithThisInv = false;
        }
        return isdateLogicValidWithThisInv;
    }
    /**
     * Execute when action is review
     * */
    @SuppressWarnings({"java:S6541", "java:S3776"})
    protected boolean checkAdvancedInvCriteria(Algorithm algorithmDocument,
                                             EdxLabInformationDto edxLabInformationDT,
                                             Map<Object, Object> questionIdentifierMap) throws DataProcessingException {
        boolean isAdvancedInvCriteriaMet = false;

        try{

            Map<String, Object> advanceInvCriteriaMap = advancedCriteria.getAdvancedInvCriteriaMap(algorithmDocument);
            /*
             * return match as true if there is no investigation is compare and
             * advanceInvCriteriaMap is empty
             */
            if ((edxLabInformationDT.getMatchingPublicHealthCaseDtoColl() == null
                    || edxLabInformationDT.getMatchingPublicHealthCaseDtoColl().isEmpty())
                    && advanceInvCriteriaMap == null
                    || advanceInvCriteriaMap.isEmpty())
            {
                isAdvancedInvCriteriaMet = true;
            }

            /*
             * for each matched PHC see if the advanced criteria matches. for mark
             * as reviewed, if the match is found break and use that PHC for
             * association and mark the lab as reviewed. for create Investigation
             * and/or Notification actions to succeed, the advanced criteria should
             * return false, so that new investigation can be created.
             */
            if(edxLabInformationDT.getMatchingPublicHealthCaseDtoColl()!=null
                    && !edxLabInformationDT.getMatchingPublicHealthCaseDtoColl().isEmpty())
            {
                for (PublicHealthCaseDto phcDT : edxLabInformationDT.getMatchingPublicHealthCaseDtoColl()) {

                    if (advanceInvCriteriaMap != null
                            && !advanceInvCriteriaMap.isEmpty())
                    {
                        Set<String> criteriaSet = advanceInvCriteriaMap.keySet();

                        for (String questionId : criteriaSet) {
                            Object object = advanceInvCriteriaMap.get(questionId);
                            if (object instanceof EdxRuleManageDto)
                            {
                                EdxRuleManageDto edxRuleManageDT = (EdxRuleManageDto) object;
                                NbsQuestionMetadata criteriaMetaData = (NbsQuestionMetadata) questionIdentifierMap.get(questionId);
                                if (criteriaMetaData != null)
                                {
                                    isAdvancedInvCriteriaMet = wdsObjectChecker.checkNbsObject(edxRuleManageDT, phcDT, criteriaMetaData);
                                }
                                if (!isAdvancedInvCriteriaMet)
                                {
                                    break;
                                }
                            }
                            else if (object instanceof Collection<?>)
                            {
                                Collection<?> collection = (ArrayList<?>) object;
                                for (Object o : collection) {
                                    EdxRuleManageDto edxRuleManageDT = (EdxRuleManageDto) o;
                                    NbsQuestionMetadata criteriaMetaData = (NbsQuestionMetadata) questionIdentifierMap.get(questionId);
                                    if (criteriaMetaData != null)
                                    {
                                        isAdvancedInvCriteriaMet = wdsObjectChecker.checkNbsObject(edxRuleManageDT, phcDT, criteriaMetaData);
                                    }
                                    if (!isAdvancedInvCriteriaMet)
                                    {
                                        break;
                                    }
                                }
                            }
                        }
                        /*
                         * If one of the investigation matches break and get out of the
                         * loop
                         */
                        if (isAdvancedInvCriteriaMet){
                            edxLabInformationDT.setAssociatedPublicHealthCaseUid(phcDT.getPublicHealthCaseUid());
                            break;
                        }
                    } else {// There is no advanced investigation criteria selected to be matched
                        isAdvancedInvCriteriaMet = true;
                    }
                }
            }
            /*
             * if the advanced criteria is not met reset the associated
             * PublicHealthCaseUid to -1, it might have been set by time criteria
             */
            if(!isAdvancedInvCriteriaMet && edxLabInformationDT.getMatchingPublicHealthCaseDtoColl()!=null
                    && !edxLabInformationDT.getMatchingPublicHealthCaseDtoColl().isEmpty())
            {
                edxLabInformationDT.setAssociatedPublicHealthCaseUid(-1L);
            }
        }catch(Exception ex){
            throw new DataProcessingException ("Exception while checking advanced Investigation Criteria for Lab mark as reviewed: ", ex);
        }
        return isAdvancedInvCriteriaMet;
    }

    private void processAction(EdxLabInformationDto edxRuleAlgorothmManagerDT, Algorithm algorithm) throws DataProcessingException {
        //applicationMap
        Map<Object, Object> applicationMap= new HashMap<>();
        try {
            ActionType actionType = algorithm.getAction();
            if (actionType.getCreateInvestigation() != null)
            {
                CreateInvestigationType specificActionType = actionType.getCreateInvestigation();
                edxRuleAlgorothmManagerDT.setAction(DecisionSupportConstants.CREATE_INVESTIGATION_VALUE);
                InvestigationDefaultValuesType investigationDefaultValuesType = specificActionType.getInvestigationDefaultValues();
                if (investigationDefaultValuesType != null)
                {
                    validateDecisionSupport.parseInvestigationDefaultValuesType(applicationMap, investigationDefaultValuesType);
                }

                CodedType failureToCreateType = specificActionType.getOnFailureToCreateInvestigation();
                edxRuleAlgorothmManagerDT.setOnFailureToCreateInv(failureToCreateType.getCode());
                if (specificActionType.getUpdateAction() != null)
                {
                    edxRuleAlgorothmManagerDT.setUpdateAction(specificActionType.getUpdateAction().getCode());
                }
            }
            else if (actionType.getCreateInvestigationWithNND() != null)
            {
                CreateInvestigationWithNNDType specificActionType = actionType.getCreateInvestigationWithNND();
                InvestigationDefaultValuesType investigationDefaultValuesType = specificActionType.getInvestigationDefaultValues();
                edxRuleAlgorothmManagerDT.setAction(DecisionSupportConstants.CREATE_INVESTIGATION_WITH_NND_VALUE);
                if (investigationDefaultValuesType != null)
                {
                    validateDecisionSupport.parseInvestigationDefaultValuesType(applicationMap, investigationDefaultValuesType);
                }
                CodedType failureToCreateType = specificActionType.getOnFailureToCreateInvestigation();
                edxRuleAlgorothmManagerDT.setOnFailureToCreateInv(failureToCreateType.getCode());
                if (specificActionType.getUpdateAction() != null)
                {
                    edxRuleAlgorothmManagerDT.setUpdateAction(specificActionType.getUpdateAction().getCode());
                }
                edxRuleAlgorothmManagerDT.setNndComment(specificActionType.getNNDComment());
                if (specificActionType.getOnFailureToCreateNND() != null)
                {
                    edxRuleAlgorothmManagerDT.setOnFailureToCreateNND(specificActionType.getOnFailureToCreateNND().getCode());
                }

            }
            else if (actionType.getDeleteDocument() != null)
            {
                DeleteDocumentType specificActionType = actionType.getDeleteDocument(); //NOSONAR
            }

            if (!applicationMap.isEmpty())
                edxRuleAlgorothmManagerDT.setEdxRuleApplyDTMap(applicationMap);

        } catch (Exception e) {
            throw new DataProcessingException("HL7ELRValidateDecisionSupport.processAction: exception caught. "+e);
        }
    }


    @SuppressWarnings("java:S3776")
    protected boolean checkAdvancedInvCriteriaForCreateInvNoti(
            Algorithm algorithmDocument,
            EdxLabInformationDto edxLabInformationDT,
            Map<Object, Object> questionIdentifierMap) throws DataProcessingException {

        try{
            Map<String, Object> advanceInvCriteriaMap = advancedCriteria.getAdvancedInvCriteriaMap(algorithmDocument);

            /*
             * return match as true if there is no investigation to compare and
             * advanceInvCriteriaMap is empty
             */
            if (
                    (edxLabInformationDT.getMatchingPublicHealthCaseDtoColl() == null
                            || edxLabInformationDT.getMatchingPublicHealthCaseDtoColl().isEmpty())
                    && (advanceInvCriteriaMap == null
                            || advanceInvCriteriaMap.isEmpty())
            )
            {
                return true;
            }

            /*
             * return match as false if there are investigation to compare and
             * advanceInvCriteriaMap is empty
             */
            if (
                    (edxLabInformationDT.getMatchingPublicHealthCaseDtoColl() != null
                            && !edxLabInformationDT.getMatchingPublicHealthCaseDtoColl().isEmpty())
                    && (advanceInvCriteriaMap == null || advanceInvCriteriaMap.isEmpty())
            )
            {
                return false;
            }

            /*
             * for each PHC see if the advanced criteria matches. the matching will
             * be exclusive (e.g., if the algorithm says investigation status not
             * equals open and there is investigation with open status, it should
             * return true)
             */
            if (edxLabInformationDT.getMatchingPublicHealthCaseDtoColl() != null
                    && !edxLabInformationDT.getMatchingPublicHealthCaseDtoColl().isEmpty()
            )
            {
                for (Object phcDT : edxLabInformationDT.getMatchingPublicHealthCaseDtoColl()) {

                    if (advanceInvCriteriaMap != null
                            && !advanceInvCriteriaMap.isEmpty()) {
                        Set<String> criteriaSet = advanceInvCriteriaMap.keySet();
                        Iterator<String> criteriaIterator = criteriaSet.iterator();

                        boolean isInvCriteriaValidForAllelements = false;

                        while (criteriaIterator.hasNext()) {

                            String questionId = criteriaIterator.next();
                            Object object = advanceInvCriteriaMap.get(questionId);
                            boolean isAdvancedInvCriteriaMet = false;

                            EdxRuleManageDto edxRuleManageDT = (EdxRuleManageDto) object;
                            NbsQuestionMetadata criteriaMetaData = (NbsQuestionMetadata) questionIdentifierMap.get(questionId);
                            if (criteriaMetaData != null)
                            {
                                isAdvancedInvCriteriaMet = wdsObjectChecker.checkNbsObject(edxRuleManageDT, phcDT, criteriaMetaData);
                            }

                            if (!isAdvancedInvCriteriaMet)
                            {
                                isInvCriteriaValidForAllelements = true;
                            }
                        }

                        if(isInvCriteriaValidForAllelements)
                        {
                            return false;
                        }
                    }
                }
                /*
                 * all the investigations were scanned to match the criteria, no
                 * existing investigation found return true to create new
                 * investigation.
                 */
                return true;
            }
        }catch(Exception ex){
            throw new DataProcessingException ("Exception while checking advanced Investigation Criteria for creating Investigation and/or Notification: ", ex);
        }
        return false;
    }




}
