package gov.cdc.dataprocessing.service.implementation.investigation;

import gov.cdc.dataprocessing.cache.SrteCache;
import gov.cdc.dataprocessing.constant.DecisionSupportConstants;
import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.PublicHealthCaseDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.PageActProxyVO;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.PublicHealthCaseVO;
import gov.cdc.dataprocessing.model.container.*;
import gov.cdc.dataprocessing.model.dsma_algorithm.*;
import gov.cdc.dataprocessing.model.dto.DSMAlgorithmDto;
import gov.cdc.dataprocessing.model.dto.EdxRuleManageDto;
import gov.cdc.dataprocessing.model.dto.NbsQuestionMetadata;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.DsmAlgorithm;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.stored_proc.PublicHealthCaseStoredProcRepository;
import gov.cdc.dataprocessing.service.interfaces.IAutoInvestigationService;
import gov.cdc.dataprocessing.service.interfaces.IDecisionSupportService;
import gov.cdc.dataprocessing.service.model.WdsReport;
import gov.cdc.dataprocessing.service.model.decision_support.DsmLabMatchHelper;
import gov.cdc.dataprocessing.utilities.component.EdxPhcrDocumentUtil;
import gov.cdc.dataprocessing.utilities.component.ValidateDecisionSupport;
import jakarta.transaction.Transactional;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.*;

@Service
public class DecisionSupportService implements IDecisionSupportService {
    static final String ApplyToAllSystems = "ALL";
    // private static List<DsmLabMatchHelper> activeElrAlgorithmList = new ArrayList<DsmLabMatchHelper>();
    // private static Boolean elrAlgorithmsPresent = true;  //any active algorithms?
    private final EdxPhcrDocumentUtil edxPhcrDocumentUtil;
    private final IAutoInvestigationService autoInvestigationService;
    private final ValidateDecisionSupport validateDecisionSupport;
    private final PublicHealthCaseStoredProcRepository publicHealthCaseStoredProcRepository;
    private final DsmAlgorithmService dsmAlgorithmService;

    public DecisionSupportService(EdxPhcrDocumentUtil edxPhcrDocumentUtil,
                                  IAutoInvestigationService autoInvestigationService,
                                  ValidateDecisionSupport validateDecisionSupport,
                                  PublicHealthCaseStoredProcRepository publicHealthCaseStoredProcRepository,
                                  DsmAlgorithmService dsmAlgorithmService) {
        this.edxPhcrDocumentUtil = edxPhcrDocumentUtil;
        this.autoInvestigationService = autoInvestigationService;
        this.validateDecisionSupport = validateDecisionSupport;
        this.publicHealthCaseStoredProcRepository = publicHealthCaseStoredProcRepository;
        this.dsmAlgorithmService = dsmAlgorithmService;
    }
    /*sort PublicHealthCaseDTs by add_time descending*/
    final Comparator<PublicHealthCaseDT> ADDTIME_ORDER = new Comparator<PublicHealthCaseDT>() {
        public int compare(PublicHealthCaseDT e1, PublicHealthCaseDT e2) {
            return e2.getAddTime().compareTo(e1.getAddTime());
        }
    };
    final Comparator<DsmLabMatchHelper> AlGORITHM_NM_ORDER = new Comparator<DsmLabMatchHelper>() {
        public int compare(DsmLabMatchHelper e1, DsmLabMatchHelper e2) {
            return e1.getAlgorithmNm().compareToIgnoreCase(e2.getAlgorithmNm());
        }
    };


    private boolean checkActiveWdsAlgorithm(EdxLabInformationDto edxLabInformationDT,
                                                             List<DsmLabMatchHelper> activeElrAlgorithmList ) throws DataProcessingException {
        boolean elrAlgorithmsPresent = true;
        // Validating existing WDS Algorithm
        try {
            List<WdsReport> wdsReportList = new ArrayList<>();
            WdsReport report = new WdsReport();
            Collection<DsmAlgorithm> algorithmCollection = selectDSMAlgorithmDTCollection();

            if (algorithmCollection == null || algorithmCollection.isEmpty())  {
                //no algorithms defined
                elrAlgorithmsPresent = false;
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
                if (algorithmDT.getStatusCd() != null && algorithmDT.getStatusCd().contentEquals(NEDSSConstant.INACTIVE) ||
                        algorithmDT.getEventType() != null && algorithmDT.getEventType().equals(NEDSSConstant.PHC_236))
                {
                    continue; //skip inactive
                }

                // Suppose to be Algorithm
                Algorithm algorithmDocument = parseAlgorithmXml(algorithmString);
                //helper class DSMLabMatchHelper will assist with algorithm matching
                DsmLabMatchHelper dsmLabMatchHelper = null;
                try {
                    if (algorithmDocument != null)
                    {
                        dsmLabMatchHelper = new DsmLabMatchHelper(algorithmDocument);
                    }
                } catch (Exception e) {
                    //if one fails to parse - continue processing with error
                    e.printStackTrace();
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


    private ObservationContainer setupObservationValuesForWds(
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
                boolean criteriaMatch = false;


                //Was AlgorithmDocument
                Algorithm algorithmDocument = null;
                //reset for every algorithm processing
                edxLabInformationDT.setAssociatedPublicHealthCaseUid(-1L);
                edxLabInformationDT.setMatchingPublicHealthCaseDTColl(null);
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

                if (edxLabInformationDT.isMatchingAlgorithm())
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

    /**
     * Description:
     *  this return true, if Action is Review, Investigation, and Investigation with Notification
     * */
    private boolean checkActionInvalid(Algorithm algorithmDocument, boolean criteriaMatch) {
        return algorithmDocument != null && criteriaMatch && algorithmDocument.getAction() != null
                &&
                (
                        (algorithmDocument.getAction().getCreateInvestigation() != null
                                && algorithmDocument.getAction().getCreateInvestigation().getOnFailureToCreateInvestigation().getCode().equals("2"))

                                ||
                                (algorithmDocument.getAction().getCreateInvestigationWithNND() != null
                                        && algorithmDocument.getAction().getCreateInvestigationWithNND().getOnFailureToCreateNND().getCode().equals("2"))
                                ||
                                (algorithmDocument.getAction().getMarkAsReviewed() != null
                                        && algorithmDocument.getAction().getMarkAsReviewed().getOnFailureToMarkAsReviewed().getCode().equals("2"))
                );
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
                (
                        algorithmDocument.getAction() != null
                                && algorithmDocument.getAction().getMarkAsReviewed() != null
                                && !algorithmDocument.getAction().getMarkAsReviewed().getOnFailureToMarkAsReviewed().getCode().equals("2")
                )
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
    private void updateObservationBasedOnAction(Algorithm algorithmDocument,
                                                boolean criteriaMatch,
                                                String conditionCode,
                                                ObservationContainer orderedTestObservationVO,
                                                Collection<PersonContainer> personVOCollection,
                                                EdxLabInformationDto edxLabInformationDT,
                                                WdsReport wdsReport,
                                                Map<Object, Object> questionIdentifierMap) throws DataProcessingException {
        PageActProxyVO pageActProxyVO = null;
        PamProxyContainer pamProxyVO = null;
        PublicHealthCaseVO publicHealthCaseVO;

        var isActionValid = checkActionInvalid(algorithmDocument, criteriaMatch);
        if (isActionValid)
        {
            if (conditionCode != null)
            {
                questionIdentifierMap = edxPhcrDocumentUtil.loadQuestions(conditionCode);
            }
            edxLabInformationDT.setConditionCode(conditionCode);
            boolean isdateLogicValidForNewInv = false;
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
                    var condCode = SrteCache.findConditionCodeByDescription(conditionCode);
                    if (condCode.isPresent())
                    {
                        edxLabInformationDT.setConditionName(condCode.get().getConditionShortNm());
                    }
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
                BasePamContainer pamVO = null;
                if (obj instanceof PageActProxyVO)
                {
                    pageActProxyVO = (PageActProxyVO) obj;
                    publicHealthCaseVO = pageActProxyVO.getPublicHealthCaseVO();
                    pamVO = pageActProxyVO.getPageVO();
                }
                else
                {
                    pamProxyVO = (PamProxyContainer) obj;
                    publicHealthCaseVO = pamProxyVO.getPublicHealthCaseVO();
                    pamVO = pamProxyVO.getPamVO();
                }

                processAction(edxLabInformationDT, algorithmDocument);

                Map<Object, Object> applyMap = edxLabInformationDT.getEdxRuleApplyDTMap();
                Collection<Object> entityMapCollection = new ArrayList<>();
                if (applyMap != null && applyMap.size() > 0 && questionIdentifierMap != null) {
                    Set<Object> set = applyMap.keySet();
                    for (Object o : set)
                    {
                        String questionId = (String) o;
                        EdxRuleManageDto edxRuleManageDT = (EdxRuleManageDto) applyMap.get(questionId);
                        NbsQuestionMetadata metaData = (NbsQuestionMetadata) questionIdentifierMap.get(questionId);
                        try {
                            if (metaData.getDataLocation() != null
                                    && metaData.getDataLocation().trim().toUpperCase().startsWith("PUBLIC_HEALTH_CASE"))
                            {
                                validateDecisionSupport.processNbsObject(edxRuleManageDT, publicHealthCaseVO, metaData);
                            }
                            else if (metaData.getDataLocation() != null
                                    && metaData.getDataLocation().trim().toUpperCase().startsWith("NBS_CASE_ANSWER"))
                            {
                                validateDecisionSupport.processNBSCaseAnswerDT(edxRuleManageDT, publicHealthCaseVO, pamVO, metaData);
                            }
                            else if (metaData.getDataLocation() != null
                                    && metaData.getDataLocation().trim().toUpperCase().startsWith("CONFIRMATION_METHOD.CONFIRMATION_METHOD_CD"))
                            {
                                validateDecisionSupport.processConfirmationMethodCodeDT(edxRuleManageDT, publicHealthCaseVO, metaData);
                            }
                            else if (metaData.getDataLocation() != null
                                    && metaData.getDataLocation().trim().toUpperCase().startsWith("CONFIRMATION_METHOD.CONFIRMATION_METHOD_TIME"))
                            {
                                validateDecisionSupport.processConfirmationMethodTimeDT(edxRuleManageDT, publicHealthCaseVO, metaData);
                            }
                            else if (metaData.getDataLocation() != null
                                    && metaData.getDataLocation().trim().toUpperCase().startsWith("ACT_ID.ROOT_EXTENSION_TXT"))
                            {
                                validateDecisionSupport.processActIds(edxRuleManageDT, publicHealthCaseVO, metaData);
                            }
                            else if (metaData.getDataLocation() != null
                                    && metaData.getDataLocation().trim().toUpperCase().startsWith("CASE_MANAGEMENT")
                                    && obj instanceof PageActProxyVO)
                            {
                                validateDecisionSupport.processNBSCaseManagementDT(edxRuleManageDT, publicHealthCaseVO, metaData);
                            }
                            else if (metaData.getDataLocation() != null
                                    && metaData.getDataType().toUpperCase().startsWith("PART"))
                            {
                                entityMapCollection.add(edxRuleManageDT);
                                if (edxRuleManageDT.getParticipationTypeCode() == null || edxRuleManageDT.getParticipationUid() == null || edxRuleManageDT.getParticipationClassCode() == null) {
                                    throw new Exception("ValidateDecisionSupport.validateProxyVO Exception thrown for edxRuleManageDT:-" + edxRuleManageDT);
                                }
                            }
                        } catch (Exception e) {
                            throw new DataProcessingException("ERROR:-ValidateDecisionSupport.validateProxyVO Exception thrown Please check." + e, e);
                        }

                    }
                    validateDecisionSupport.processConfirmationMethodCodeDTRequired(publicHealthCaseVO);
                }

                autoInvestigationService.transferValuesTOActProxyVO(pageActProxyVO, pamProxyVO, personVOCollection, orderedTestObservationVO, entityMapCollection, questionIdentifierMap);

                if (questionIdentifierMap != null
                        && questionIdentifierMap.get(edxPhcrDocumentUtil._REQUIRED) != null)
                {
                    Map<Object, Object> nbsAnswerMap = pamVO.getPamAnswerDTMap();
                    Map<Object, Object> requireMap = (Map<Object, Object>) questionIdentifierMap.get(edxPhcrDocumentUtil._REQUIRED);
                    String errorText = edxPhcrDocumentUtil.requiredFieldCheck(requireMap, nbsAnswerMap);
                    publicHealthCaseVO.setErrorText(errorText);
                }
                if (obj instanceof PageActProxyVO) {
                    edxLabInformationDT.setPageActContainer((PageActProxyVO) obj);
                }
                else
                {
                    edxLabInformationDT.setPamContainer((PamProxyContainer) obj);
                }

                var condCode = SrteCache.findConditionCodeByDescription(conditionCode);
                condCode.ifPresent(code -> edxLabInformationDT.setConditionName(code.getConditionShortNm()));

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
        Collection<DsmAlgorithm> algorithmList = new ArrayList<> ();

        try
        {
            algorithmList = dsmAlgorithmService.findActiveDsmAlgorithm();
        } catch(Exception se2) {
            throw new DataProcessingException(se2.getMessage(), se2);
        }
        return algorithmList; //return all recs
    } //selectDSMAlgorithmDTCollection


    private Algorithm parseAlgorithmXml(String xmlPayLoadContent)
            throws Exception {
        Algorithm algorithmDocument = null;
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
    private boolean specimenCollectionDateCriteria(EventDateLogicType eventDateLogicType,EdxLabInformationDto edxLabInformationDT) throws DataProcessingException {
        boolean isdateLogicValidForNewInv = false;
        String comparatorCode="";
        int value=0;
        Long associatedPHCUid= -1L;
        Long mprUid= -1L;
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
            edxLabInformationDT.setMatchingPublicHealthCaseDTColl(assocExistPhcWithPid);
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

            if(specimenCollectionDate!=null && comparatorCode.length()>0  && mprUid>0)
            {
                Collection<PublicHealthCaseDT> associatedPhcDTCollection = publicHealthCaseStoredProcRepository.associatedPublicHealthCaseForMprForCondCd(mprUid, edxLabInformationDT.getConditionCode());

                if(associatedPhcDTCollection!=null && associatedPhcDTCollection.size()>0){
                    for (PublicHealthCaseDT publicHealthCaseDT : associatedPhcDTCollection) {
                        boolean isdateLogicValidWithThisInv = true;
                        PublicHealthCaseDT phcDT = publicHealthCaseDT;
                        Long dateCompare = null;
                        if (phcDT.getAssociatedSpecimenCollDate() != null)
                        {
                            dateCompare = phcDT.getAssociatedSpecimenCollDate().getTime() / (1000 * 60 * 60 * 24);
                        }
                        else
                        {
                            dateCompare = phcDT.getAddTime().getTime() / (1000 * 60 * 60 * 24);
                            phcDT.setAssociatedSpecimenCollDate(phcDT.getAddTime());
                        }
                        int daysDifference = (int) specimenCollectionDays - dateCompare.intValue();

                        if (phcDT.getAssociatedSpecimenCollDate() != null) {
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
                        }
                        if (isdateLogicValidWithThisInv) {
                            if (edxLabInformationDT.getMatchingPublicHealthCaseDTColl() == null)
                            {
                                edxLabInformationDT.setMatchingPublicHealthCaseDTColl(new ArrayList<PublicHealthCaseDT>());
                            }
                            edxLabInformationDT.getMatchingPublicHealthCaseDTColl().add(phcDT);
                        }
                    }
                }else{
                    isdateLogicValidForNewInv= true;
                }
            }
        }
        if (edxLabInformationDT.getMatchingPublicHealthCaseDTColl() != null
                && edxLabInformationDT.getMatchingPublicHealthCaseDTColl().size() > 0)
        {
            List phclist = new ArrayList<Object>(edxLabInformationDT.getMatchingPublicHealthCaseDTColl());
            Collections.sort(phclist, ADDTIME_ORDER);
            associatedPHCUid = ((PublicHealthCaseDT)phclist.get(0)).getPublicHealthCaseUid();
            isdateLogicValidForNewInv= false;
        }
        else
        {
            isdateLogicValidForNewInv= true;
        }
        edxLabInformationDT.setAssociatedPublicHealthCaseUid(associatedPHCUid);
        return isdateLogicValidForNewInv;
    }

    /**
     * Execute when action is review
     * */
    private boolean checkAdvancedInvCriteria(Algorithm algorithmDocument,
                                             EdxLabInformationDto edxLabInformationDT,
                                             Map<Object, Object> questionIdentifierMap) throws DataProcessingException {
        boolean isAdvancedInvCriteriaMet = false;

        try{

            Map<String, Object> advanceInvCriteriaMap = getAdvancedInvCriteriaMap(algorithmDocument);
            /*
             * return match as true if there is no investigation is compare and
             * advanceInvCriteriaMap is empty
             */
            if ((edxLabInformationDT.getMatchingPublicHealthCaseDTColl() == null
                    || edxLabInformationDT.getMatchingPublicHealthCaseDTColl().size() == 0)
                    && advanceInvCriteriaMap == null
                    || advanceInvCriteriaMap.size() == 0)
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
            if(edxLabInformationDT.getMatchingPublicHealthCaseDTColl()!=null
                    && edxLabInformationDT.getMatchingPublicHealthCaseDTColl().size()>0)
            {
                for (Object phcDT : edxLabInformationDT.getMatchingPublicHealthCaseDTColl()) {

                    if (advanceInvCriteriaMap != null
                            && advanceInvCriteriaMap.size() > 0)
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
                                    isAdvancedInvCriteriaMet = validateDecisionSupport.checkNbsObject(edxRuleManageDT, phcDT, criteriaMetaData);
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
                                        isAdvancedInvCriteriaMet = validateDecisionSupport.checkNbsObject(edxRuleManageDT, phcDT, criteriaMetaData);
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
                            edxLabInformationDT.setAssociatedPublicHealthCaseUid(((PublicHealthCaseDT)phcDT).getPublicHealthCaseUid());
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
            if(!isAdvancedInvCriteriaMet && edxLabInformationDT.getMatchingPublicHealthCaseDTColl()!=null
                    && edxLabInformationDT.getMatchingPublicHealthCaseDTColl().size()>0)
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
                DeleteDocumentType specificActionType = actionType.getDeleteDocument();
                specificActionType.getAlert();
                specificActionType.getComment();
                specificActionType.getReasonForDeletion();
            }

            if (applicationMap != null && applicationMap.size() > 0)
                edxRuleAlgorothmManagerDT.setEdxRuleApplyDTMap(applicationMap);

        } catch (Exception e) {
            throw new DataProcessingException("HL7ELRValidateDecisionSupport.processAction: exception caught. "+e);
        }
    }


    private boolean checkAdvancedInvCriteriaForCreateInvNoti(
            Algorithm algorithmDocument,
            EdxLabInformationDto edxLabInformationDT,
            Map<Object, Object> questionIdentifierMap) throws DataProcessingException {

        try{
            Map<String, Object> advanceInvCriteriaMap = getAdvancedInvCriteriaMap(algorithmDocument);

            /*
             * return match as true if there is no investigation to compare and
             * advanceInvCriteriaMap is empty
             */
            if (
                    (edxLabInformationDT.getMatchingPublicHealthCaseDTColl() == null
                            || edxLabInformationDT.getMatchingPublicHealthCaseDTColl().size() == 0)
                    && (advanceInvCriteriaMap == null
                            || advanceInvCriteriaMap.size() == 0)
            )
            {
                return true;
            }

            /*
             * return match as false if there are investigation to compare and
             * advanceInvCriteriaMap is empty
             */
            if (
                    (edxLabInformationDT.getMatchingPublicHealthCaseDTColl() != null && edxLabInformationDT.getMatchingPublicHealthCaseDTColl().size() > 0)
                    && (advanceInvCriteriaMap == null || advanceInvCriteriaMap.size() == 0)
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
            if (edxLabInformationDT.getMatchingPublicHealthCaseDTColl() != null
                    && edxLabInformationDT.getMatchingPublicHealthCaseDTColl().size() > 0
            )
            {
                for (Object phcDT : edxLabInformationDT.getMatchingPublicHealthCaseDTColl()) {

                    if (advanceInvCriteriaMap != null
                            && advanceInvCriteriaMap.size() > 0) {
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
                                isAdvancedInvCriteriaMet = validateDecisionSupport.checkNbsObject(edxRuleManageDT, phcDT, criteriaMetaData);
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

    private Map<String, Object> getAdvancedInvCriteriaMap(Algorithm algorithmDocument) throws DataProcessingException{

        Map<String, Object> advanceInvCriteriaMap = new HashMap<String, Object>();
        try{
            InvCriteriaType advanceInvCriteriaType = algorithmDocument
                    .getElrAdvancedCriteria().getInvCriteria();
            /* Create the advanced Criteria map to compare against matched PHCs */
            if (advanceInvCriteriaType != null) {
                for (int i = 0; i < advanceInvCriteriaType.getInvValue().size(); i++) {
                    InvValueType criteriaType = advanceInvCriteriaType
                            .getInvValue().get(i);
                    CodedType criteriaQuestionType = criteriaType.getInvQuestion();
                    CodedType criteriaLogicType = criteriaType
                            .getInvQuestionLogic();

                    if (criteriaType.getInvStringValue() == null
                            && criteriaType.getInvCodedValue().size() > 0) {
                        String value = null;
                        String[] array = new String[criteriaType
                                .getInvCodedValue().size()];
                        for (int j = 0; j < criteriaType.getInvCodedValue().size(); j++) {
                            array[j] = criteriaType.getInvCodedValue().get(j)
                                    .getCode();
                        }
                        Arrays.sort(array);
                        value = String.join(",", array);
                        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
                        edxRuleManageDT.setQuestionId(criteriaQuestionType
                                .getCode());
                        edxRuleManageDT.setLogic(criteriaLogicType.getCode());
                        edxRuleManageDT.setAdvanceCriteria(true);
                        edxRuleManageDT.setValue(value);
                        advanceInvCriteriaMap.put(criteriaQuestionType.getCode(),
                                edxRuleManageDT);

                    } else {
                        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
                        edxRuleManageDT.setQuestionId(criteriaQuestionType
                                .getCode());
                        edxRuleManageDT.setLogic(criteriaLogicType.getCode());
                        edxRuleManageDT.setAdvanceCriteria(true);
                        edxRuleManageDT.setValue(criteriaType.getInvStringValue());
                        advanceInvCriteriaMap.put(criteriaQuestionType.getCode(),
                                edxRuleManageDT);
                    }
                }
            }
        }catch(Exception ex){
            throw new DataProcessingException ("Exception while creating advanced Investigation Criteria Map: ", ex);
        }
        return advanceInvCriteriaMap;
    }



}
