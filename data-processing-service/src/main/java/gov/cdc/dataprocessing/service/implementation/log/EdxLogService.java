package gov.cdc.dataprocessing.service.implementation.log;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.model.dto.edx.EdxRuleAlgorothmManagerDto;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityDetailLogDto;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityLogDto;
import gov.cdc.dataprocessing.repository.nbs.msgoute.model.NbsInterfaceModel;
import gov.cdc.dataprocessing.repository.nbs.odse.model.log.EdxActivityDetailLog;
import gov.cdc.dataprocessing.repository.nbs.odse.model.log.EdxActivityLog;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.log.EdxActivityDetailLogRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.log.EdxActivityLogRepository;
import gov.cdc.dataprocessing.service.interfaces.log.IEdxLogService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
public class EdxLogService implements IEdxLogService {
    private final EdxActivityLogRepository edxActivityLogRepository;
    private final EdxActivityDetailLogRepository edxActivityDetailLogRepository;

    public EdxLogService(EdxActivityLogRepository edxActivityLogRepository,
                         EdxActivityDetailLogRepository edxActivityDetailLogRepository) {
        this.edxActivityLogRepository = edxActivityLogRepository;
        this.edxActivityDetailLogRepository = edxActivityDetailLogRepository;
    }

    @Transactional
    @Override
    public EdxActivityDetailLog saveEdxActivityDetailLog(EDXActivityDetailLogDto detailLogDto) {
        EdxActivityDetailLog edxActivityDetailLog = new EdxActivityDetailLog(detailLogDto);
        EdxActivityDetailLog edxActivityDetailLogResult = edxActivityDetailLogRepository.save(edxActivityDetailLog);
        return edxActivityDetailLogResult;
    }
    @Transactional(Transactional.TxType.REQUIRED)
    public void saveEdxActivityLogs(EDXActivityLogDto edxActivityLogDto) {
        EdxActivityLog edxActivityLog = new EdxActivityLog(edxActivityLogDto);
        //Check if the activity log has already been created for the source.
        Long activityLogId = 0L;
        Optional<EdxActivityLog> dbActivityLogOptional = edxActivityLogRepository.findBySourceUid(edxActivityLog.getSourceUid());
        if (dbActivityLogOptional.isPresent()) {
            EdxActivityLog dbActivityLog = dbActivityLogOptional.get();
            dbActivityLog.setExceptionTxt(edxActivityLogDto.getExceptionTxt());
            edxActivityLogRepository.save(dbActivityLog);
            activityLogId=dbActivityLog.getId();
        }else {
            EdxActivityLog edxActivityLogNew = edxActivityLogRepository.save(edxActivityLog);
            activityLogId=edxActivityLogNew.getId();
        }

        if (edxActivityLogDto.getEDXActivityLogDTWithVocabDetails() != null) {
            Collection<EDXActivityDetailLogDto> edxActivityDetailLogsList= edxActivityLogDto.getEDXActivityLogDTWithVocabDetails();
            for (EDXActivityDetailLogDto eDXActivityDetailLogDto: edxActivityDetailLogsList) {
                eDXActivityDetailLogDto.setEdxActivityLogUid(activityLogId);
                saveEdxActivityDetailLog(eDXActivityDetailLogDto);
            }
        }

    }

    public void updateActivityLogDT(NbsInterfaceModel nbsInterfaceModel, EdxLabInformationDto edxLabInformationDto) {
        EDXActivityLogDto edxActivityLogDto = edxLabInformationDto.getEdxActivityLogDto();
        Date dateTime = new Date();
        Timestamp time = new Timestamp(dateTime.getTime());
        nbsInterfaceModel.setRecordStatusTime(time);

        edxActivityLogDto.setLogDetailAllStatus(true);
        edxActivityLogDto.setSourceUid(Long.valueOf(nbsInterfaceModel.getNbsInterfaceUid()));
        edxActivityLogDto.setTargetUid(edxLabInformationDto.getRootObserbationUid());

        if (edxLabInformationDto.getErrorText() == null) {
            edxLabInformationDto.setErrorText("");
        }
        setActivityLogExceptionTxt(edxActivityLogDto, edxLabInformationDto.getErrorText());

        edxActivityLogDto.setImpExpIndCd("I");
        edxActivityLogDto.setRecordStatusTime(time);
        edxActivityLogDto.setSourceTypeCd("INT");
        edxActivityLogDto.setTargetTypeCd("LAB");
        edxActivityLogDto.setDocType(EdxELRConstant.ELR_DOC_TYPE_CD);
        edxActivityLogDto.setRecordStatusCd(edxLabInformationDto.getStatus().toString());

        if (edxLabInformationDto.getFillerNumber() != null && edxLabInformationDto.getFillerNumber().length() > 100) {
            edxActivityLogDto.setAccessionNbr(edxLabInformationDto.getFillerNumber().substring(0, 100));
        } else {
            edxActivityLogDto.setAccessionNbr(edxLabInformationDto.getFillerNumber());
        }

        edxActivityLogDto.setMessageId(edxLabInformationDto.getMessageControlID());
        edxActivityLogDto.setEntityNm(edxLabInformationDto.getEntityName());

        edxActivityLogDto.setSrcName(edxLabInformationDto.getSendingFacilityName());
        edxActivityLogDto.setBusinessObjLocalId(edxLabInformationDto.getLocalId());
        edxActivityLogDto.setAlgorithmName(edxLabInformationDto.getDsmAlgorithmName());
        edxActivityLogDto.setAlgorithmAction(edxLabInformationDto.getAction());

    }

    private void setActivityLogExceptionTxt(EDXActivityLogDto edxActivityLogDto, String errorText) {
        switch (errorText) {
            case EdxELRConstant.ELR_MASTER_LOG_ID_1:
                edxActivityLogDto.setExceptionTxt(EdxELRConstant.ELR_MASTER_MSG_ID_1);
            case EdxELRConstant.ELR_MASTER_LOG_ID_2:
                edxActivityLogDto.setExceptionTxt(EdxELRConstant.ELR_MASTER_MSG_ID_2);
            case EdxELRConstant.ELR_MASTER_LOG_ID_3:
                edxActivityLogDto.setExceptionTxt(EdxELRConstant.ELR_MASTER_MSG_ID_3);
            case EdxELRConstant.ELR_MASTER_LOG_ID_4:
                edxActivityLogDto.setExceptionTxt(EdxELRConstant.ELR_MASTER_MSG_ID_4);
            case EdxELRConstant.ELR_MASTER_LOG_ID_5:
                edxActivityLogDto.setExceptionTxt(EdxELRConstant.ELR_MASTER_MSG_ID_5);
            case EdxELRConstant.ELR_MASTER_LOG_ID_6:
                edxActivityLogDto.setExceptionTxt(EdxELRConstant.ELR_MASTER_MSG_ID_6);
            case EdxELRConstant.ELR_MASTER_LOG_ID_7:
                edxActivityLogDto.setExceptionTxt(EdxELRConstant.ELR_MASTER_MSG_ID_7);
            case EdxELRConstant.ELR_MASTER_LOG_ID_8:
                edxActivityLogDto.setExceptionTxt(EdxELRConstant.ELR_MASTER_MSG_ID_8);
            case EdxELRConstant.ELR_MASTER_LOG_ID_9:
                edxActivityLogDto.setExceptionTxt(EdxELRConstant.ELR_MASTER_MSG_ID_9);
            case EdxELRConstant.ELR_MASTER_LOG_ID_10:
                edxActivityLogDto.setExceptionTxt(EdxELRConstant.ELR_MASTER_MSG_ID_10);
            case EdxELRConstant.ELR_MASTER_LOG_ID_11:
                edxActivityLogDto.setExceptionTxt(EdxELRConstant.ELR_MASTER_MSG_ID_11);
            case EdxELRConstant.ELR_MASTER_LOG_ID_12:
                edxActivityLogDto.setExceptionTxt(EdxELRConstant.ELR_MASTER_MSG_ID_12);
            case EdxELRConstant.ELR_MASTER_LOG_ID_13:
                edxActivityLogDto.setExceptionTxt(EdxELRConstant.ELR_MASTER_MSG_ID_13);
            case EdxELRConstant.ELR_MASTER_LOG_ID_14:
                edxActivityLogDto.setExceptionTxt(EdxELRConstant.ELR_MASTER_MSG_ID_14);
            case EdxELRConstant.ELR_MASTER_LOG_ID_15:
                edxActivityLogDto.setExceptionTxt(EdxELRConstant.ELR_MASTER_MSG_ID_15);
            case EdxELRConstant.ELR_MASTER_LOG_ID_16:
                edxActivityLogDto.setExceptionTxt(EdxELRConstant.ELR_MASTER_MSG_ID_16);
            case EdxELRConstant.ELR_MASTER_LOG_ID_17:
                edxActivityLogDto.setExceptionTxt(EdxELRConstant.ELR_MASTER_MSG_ID_17);
            case EdxELRConstant.ELR_MASTER_LOG_ID_18:
                edxActivityLogDto.setExceptionTxt(EdxELRConstant.ELR_MASTER_MSG_ID_18);
            case EdxELRConstant.ELR_MASTER_LOG_ID_19:
                edxActivityLogDto.setExceptionTxt(EdxELRConstant.ELR_MASTER_MSG_ID_19);
            case EdxELRConstant.ELR_MASTER_LOG_ID_20:
                edxActivityLogDto.setExceptionTxt(EdxELRConstant.ELR_MASTER_MSG_ID_20);
            case EdxELRConstant.ELR_MASTER_LOG_ID_21:
                edxActivityLogDto.setExceptionTxt(EdxELRConstant.ELR_MASTER_MSG_ID_21);
            case EdxELRConstant.ELR_MASTER_LOG_ID_22:
                edxActivityLogDto.setExceptionTxt(EdxELRConstant.ELR_MASTER_MSG_ID_22);
            default:
                //return;
        }
    }

    @SuppressWarnings("java:S6541")
    public void addActivityDetailLogs(EdxLabInformationDto edxLabInformationDto, String detailedMsg) {
        try{
            ArrayList<EDXActivityDetailLogDto> detailList =
                    (ArrayList<EDXActivityDetailLogDto>) edxLabInformationDto.getEdxActivityLogDto().getEDXActivityLogDTWithVocabDetails();
            if (detailList == null) {
                detailList = new ArrayList<>();
            }
            String id = String.valueOf(edxLabInformationDto.getLocalId());
            boolean errorReturned = false;


            if (edxLabInformationDto.isInvalidXML()) {
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure, EdxELRConstant.INVALID_XML);
                errorReturned = true;
            } else if (edxLabInformationDto.isMultipleOBR()) {
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure,
                        EdxELRConstant.MULTIPLE_OBR);
                errorReturned = true;
            } else if (!edxLabInformationDto.isFillerNumberPresent()) {
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure,
                        EdxELRConstant.FILLER_FAIL);
                errorReturned = true;
            } else if (edxLabInformationDto.isOrderTestNameMissing()) {
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure,
                        EdxELRConstant.NO_ORDTEST_NAME + " " + detailedMsg);
                errorReturned = true;
            } else if (edxLabInformationDto.isReflexOrderedTestCdMissing()) {
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure,
                        EdxELRConstant.NO_REFLEX_ORDERED_NM);
                errorReturned = true;
            } else if (edxLabInformationDto.isReflexResultedTestCdMissing()) {
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure,
                        EdxELRConstant.NO_REFLEX_RESULT_NM + " " + detailedMsg);
                errorReturned = true;
            } else if (edxLabInformationDto.isResultedTestNameMissing()) {
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure,
                        EdxELRConstant.NO_RESULT_NAME + " " + detailedMsg);
                errorReturned = true;
            } else if (edxLabInformationDto.isReasonforStudyCdMissing()) {
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure,
                        EdxELRConstant.NO_REASON_FOR_STUDY + " " + detailedMsg);
                errorReturned = true;
            } else if (edxLabInformationDto.isDrugNameMissing()) {
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure,
                        EdxELRConstant.NO_DRUG_NAME);
                errorReturned = true;
            } else if (edxLabInformationDto.isMultipleSubject()) {
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure,
                        EdxELRConstant.MULTIPLE_SUBJECT);
                errorReturned = true;
            } else if (edxLabInformationDto.isNoSubject()) {
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure,
                        EdxELRConstant.NO_SUBJECT);
                errorReturned = true;
            } else if (edxLabInformationDto.isChildOBRWithoutParent()) {
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure,
                        EdxELRConstant.CHILD_OBR_WITHOUT_PARENT);
                errorReturned = true;
            } else if (edxLabInformationDto.isOrderOBRWithParent()) {
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure,
                        EdxELRConstant.ORDER_OBR_WITH_PARENT);
                errorReturned = true;
            } else if (!edxLabInformationDto.isObsStatusTranslated()) {
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure,
                        EdxELRConstant.TRANSLATE_OBS_STATUS);
                errorReturned = true;
            } else if (edxLabInformationDto.isUniversalServiceIdMissing()) {
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure,
                        EdxELRConstant.UNIVSRVCID);
                errorReturned = true;
            } else if (edxLabInformationDto.isActivityToTimeMissing()) {
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure,
                        EdxELRConstant.ODSACTIVTOTIME_FAIL);
                errorReturned = true;
            } else if (edxLabInformationDto.isActivityTimeOutOfSequence()) {
                String msg = EdxELRConstant.LABTEST_SEQUENCE.replace("%1", edxLabInformationDto.getFillerNumber());
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure, msg);
                errorReturned = true;
            } else if (edxLabInformationDto.isFinalPostCorrected()) {
                String msg = EdxELRConstant.FINAL_POST_CORRECTED.replace("%1",
                        edxLabInformationDto.getLocalId()).replace("%2",
                        edxLabInformationDto.getFillerNumber());
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure, msg);
                errorReturned = true;
            } else if (edxLabInformationDto.isPreliminaryPostFinal()) {
                String msg = EdxELRConstant.PRELIMINARY_POST_FINAL.replace("%1",
                        edxLabInformationDto.getLocalId()).replace("%2",
                        edxLabInformationDto.getFillerNumber());
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure, msg);
                errorReturned = true;
            } else if (edxLabInformationDto.isPreliminaryPostCorrected()) {
                String msg = EdxELRConstant.PRELIMINARY_POST_CORRECTED.replace("%1",
                        edxLabInformationDto.getLocalId()).replace("%2",
                        edxLabInformationDto.getFillerNumber());
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure, msg);
                errorReturned = true;
            } else if (edxLabInformationDto.isMissingOrderingProviderandFacility()) {
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure,
                        EdxELRConstant.NO_ORDERINGPROVIDER);
                errorReturned = true;
            } else if (edxLabInformationDto.isUnexpectedResultType()) {
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure,
                        EdxELRConstant.UNEXPECTED_RESULT_TYPE);
                errorReturned = true;
            } else if (edxLabInformationDto.isChildSuscWithoutParentResult()) {
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure,
                        EdxELRConstant.CHILD_SUSC_WITH_NO_PARENT_RESULT);
                errorReturned = true;
            } else if (!edxLabInformationDto.isCreateLabPermission()) {
                String msg = EdxELRConstant.NO_LAB_CREATE_PERMISSION.replace("%1", edxLabInformationDto.getUserName());
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure, msg);
                errorReturned = true;
            } else if (!edxLabInformationDto.isUpdateLabPermission()) {
                String msg = EdxELRConstant.NO_LAB_UPDATE_PERMISSION.replace("%1", edxLabInformationDto.getUserName());
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure, msg);
                errorReturned = true;
            } else if (!edxLabInformationDto.isMarkAsReviewPermission()) {
                String msg = EdxELRConstant.NO_LAB_MARK_REVIEW_PERMISSION.replace("%1", edxLabInformationDto.getUserName());
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure, msg);
            } else if (!edxLabInformationDto.isCreateInvestigationPermission()) {
                String msg = EdxELRConstant.NO_INV_PERMISSION.replace("%1", edxLabInformationDto.getUserName());
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure, msg);
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Success,
                        EdxELRConstant.OFCI);
            } else if (!edxLabInformationDto.isCreateNotificationPermission()) {
                String msg = EdxELRConstant.NO_NOT_PERMISSION.replace("%1", edxLabInformationDto.getUserName());
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure, msg);
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Success,
                        EdxELRConstant.OFCN);
            } else if (edxLabInformationDto.isFieldTruncationError()) {
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure, detailedMsg);
            } else if (edxLabInformationDto.isInvalidDateError()) {
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure, detailedMsg);
                errorReturned = true;
            } else if (!errorReturned && edxLabInformationDto.isSystemException()) {
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure, detailedMsg);
                if (edxLabInformationDto.getLabResultProxyContainer() == null)
                    errorReturned = true;
                if (edxLabInformationDto.isLabIsCreate() && !edxLabInformationDto.isLabIsCreateSuccess())
                    errorReturned = true;
                else if ((edxLabInformationDto.isLabIsUpdateDRRQ() || edxLabInformationDto.isLabIsUpdateDRSA()) && !edxLabInformationDto.isLabIsUpdateSuccess())
                    errorReturned = true;
                if (edxLabInformationDto.isInvestigationSuccessfullyCreated())
                    setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Success,
                            EdxELRConstant.OFCN);
                else if (edxLabInformationDto.isLabIsCreateSuccess())
                    setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Success,
                            EdxELRConstant.OFCI);
            }
            if (errorReturned) {
                edxLabInformationDto.getEdxActivityLogDto().setEDXActivityLogDTWithVocabDetails(detailList);
                return;
            }
            if (edxLabInformationDto.isMultipleSubjectMatch()) {
                String msg = EdxELRConstant.SUBJECTMATCH_MULT.replace("%1",
                        edxLabInformationDto.getEntityName()).replace("%2",
                        String.valueOf(edxLabInformationDto.getPersonParentUid()));
                setActivityDetailLog(detailList,
                        String.valueOf(edxLabInformationDto.getPersonParentUid()),
                        EdxRuleAlgorothmManagerDto.STATUS_VAL.Success, msg);
            }
            if (edxLabInformationDto.isPatientMatch()) {
                String msg = EdxELRConstant.SUBJECT_MATCH_FOUND.replace("%1",
                        String.valueOf(edxLabInformationDto.getPatientUid())).replace("%2",
                        String.valueOf(edxLabInformationDto.getPersonParentUid()));
                setActivityDetailLog(detailList,
                        String.valueOf(edxLabInformationDto.getPersonParentUid()),
                        EdxRuleAlgorothmManagerDto.STATUS_VAL.Success, msg);
            }
            if (!edxLabInformationDto.isMultipleSubjectMatch() && !edxLabInformationDto.isPatientMatch() && edxLabInformationDto.getPersonParentUid() != 0) {
                String msg = EdxELRConstant.SUJBECTMATCH_NO.replace("%1",
                        String.valueOf(edxLabInformationDto.getPersonParentUid())).replace("%2",
                        String.valueOf(edxLabInformationDto.getPersonParentUid()));
                setActivityDetailLog(detailList,
                        String.valueOf(edxLabInformationDto.getPersonParentUid()),
                        EdxRuleAlgorothmManagerDto.STATUS_VAL.Success, msg);
            }
            if(edxLabInformationDto.isNextOfKin()) {

                var nokInfo = edxLabInformationDto.getLabResultProxyContainer().getThePersonContainerCollection()
                                .stream().filter(nok -> nok.getRole().equals(NEDSSConstant.NOK)).findFirst();
                String nokUid;
                String nokParentUid;
                String message = EdxELRConstant.NEXT_OF_KIN;
                if (nokInfo.isPresent()) {
                    nokUid = nokInfo.get().getThePersonDto().getUid().toString();
                    nokParentUid = nokInfo.get().getThePersonDto().getPersonParentUid().toString();
                    message = message + ". (UID: " + nokUid + ", PUID: " + nokParentUid + ")";
                }
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Success, message);
            }else{
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure, EdxELRConstant.NO_NEXT_OF_KIN);
            }
            if(edxLabInformationDto.isProvider()) {
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Success, EdxELRConstant.IS_PROVIDER);
            }else{
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure, EdxELRConstant.IS_NOT_PROVIDER);
            }
            if (edxLabInformationDto.isLabIsCreateSuccess() && edxLabInformationDto.getJurisdictionName() != null) {
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Success,
                        EdxELRConstant.JURISDICTION_DERIVED);
            }
            if (edxLabInformationDto.isLabIsCreateSuccess() && edxLabInformationDto.getJurisdictionName() == null) {
                edxLabInformationDto.setSystemException(false);
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure,
                        EdxELRConstant.NO_JURISDICTION_DERIVED);
            }
            if (edxLabInformationDto.isLabIsCreateSuccess() && edxLabInformationDto.getProgramAreaName() != null) {
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Success,
                        EdxELRConstant.PROG_AREA_DERIVED);
            }
            if (edxLabInformationDto.isLabIsCreateSuccess() && edxLabInformationDto.getProgramAreaName() == null) {
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure,
                        EdxELRConstant.NO_PROG_AREA_DERIVED);
            }
            if (!edxLabInformationDto.isMatchingAlgorithm()) {
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure,
                        EdxELRConstant.NO_MATCHING_ALGORITHM);
            }
            if (edxLabInformationDto.isLabIsCreateSuccess()) {
                String msg = EdxELRConstant.LAB_CREATE_SUCCESS.replace("%1", id);
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Success, msg);
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Success, EdxELRConstant.DOC_CREATE_SUCCESS);
            }
            if(edxLabInformationDto.isObservationMatch()) {
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Success, EdxELRConstant.OBSERVATION_MATCH);
            }else{
                String msg = EdxELRConstant.OBSERVATION_NOT_MATCH.replace("%1", Long.toString(edxLabInformationDto.getRootObserbationUid()));
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Success, msg);
            }
            if (edxLabInformationDto.isLabIsUpdateSuccess()) {
                String msg = null;
                if (edxLabInformationDto.isLabIsUpdateDRRQ())
                    msg = EdxELRConstant.LAB_UPDATE_SUCCESS_DRRQ.replace("%1", id);
                else if (edxLabInformationDto.isLabIsUpdateDRSA())
                    msg = EdxELRConstant.LAB_UPDATE_SUCCESS_DRSA.replace("%1", id);
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Success, msg);
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Success, EdxELRConstant.DOC_CREATE_SUCCESS);
            }
            if (!edxLabInformationDto.isMissingOrderingProviderandFacility() && edxLabInformationDto.isMissingOrderingProvider()) {
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Success,
                        EdxELRConstant.MISSING_ORD_PROV);
            }
            if (!edxLabInformationDto.isMissingOrderingProviderandFacility() && edxLabInformationDto.isMissingOrderingFacility()) {
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Success,
                        EdxELRConstant.MISSING_ORD_FAC);
            }
            if (edxLabInformationDto.isMultipleOrderingProvider()) {
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Success,
                        EdxELRConstant.MULTIPLE_PROVIDER);
            }
            if (edxLabInformationDto.isMultipleCollector()) {
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Success,
                        EdxELRConstant.MULTIPLE_COLLECTOR);
            }
            if (edxLabInformationDto.isMultiplePrincipalInterpreter()) {
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Success,
                        EdxELRConstant.MULTIPLE_INTERP);
            }
            if (edxLabInformationDto.isMultipleOrderingFacility()) {
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Success,
                        EdxELRConstant.MULTIPLE_ORDERFAC);
            }
            if (edxLabInformationDto.isMultipleReceivingFacility()) {
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Success,
                        EdxELRConstant.MULTIPLE_RECEIVEFAC);
            }
            if (edxLabInformationDto.isMultipleSpecimen()) {
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Success,
                        EdxELRConstant.MULTIPLE_SPECIMEN);
            }
            if (!edxLabInformationDto.isEthnicityCodeTranslated()) {
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Success,
                        EdxELRConstant.TRANSLATE_ETHN_GRP);
            }
            if (!edxLabInformationDto.isObsMethodTranslated()) {
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Success,
                        EdxELRConstant.TRANSLATE_OBS_METH);
            }
            if (!edxLabInformationDto.isRaceTranslated()) {
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Success,
                        EdxELRConstant.TRANSLATE_RACE);
            }
            if (!edxLabInformationDto.isSexTranslated()) {
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Success,
                        EdxELRConstant.TRANSLATE_SEX);
            }
            if (edxLabInformationDto.isSsnInvalid()) {
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Success,
                        EdxELRConstant.INFO_SSN_INVALID);
            }
            if (edxLabInformationDto.isNullClia()) {
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Success,
                        EdxELRConstant.NULL_CLIA);
            }
            if (!edxLabInformationDto.getWdsReports().isEmpty()) {
                String msg = EdxELRConstant.WDS_REPORT;
                String action = "";
                StringBuilder sb = new StringBuilder();
                var item = edxLabInformationDto.getWdsReports().get(0);

                action = item.getAction();
                sb.append(action).append(" Action. ");
                String underCond = "Under condition ";
                if (!item.getWdsValueNumericReportList().isEmpty()) {
                    var wdsNumeric = item.getWdsValueNumericReportList().get(0);
                    sb.append("Matched on Numeric type. ").append(underCond).append(wdsNumeric.getWdsCode())
                            .append("(WDS value) ").append(wdsNumeric.getOperator()).append(" ")
                            .append(wdsNumeric.getInputCode1()).append(" (Input value 1) & ")
                            .append(wdsNumeric.getInputCode2()).append(" (Input value 2)");
                }
                else if (!item.getWdsValueTextReportList().isEmpty()) {
                    var wdsText = item.getWdsValueTextReportList().get(0);
                    sb.append("Matched on Text Value type. ").append(underCond).append(wdsText.getWdsCode())
                            .append("(WDS value) matching with ")
                            .append(wdsText.getInputCode());
                }
                else if (item.getWdsValueCodedReport() != null ) {
                    sb.append("Matched on Coded Value type. ").append(underCond).append(item.getWdsValueCodedReport().getWdsCode())
                            .append("(WDS value) matching with ")
                            .append(item.getWdsValueCodedReport().getInputCode());
                }

                msg = msg + sb;
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Success, msg);
            }
//            if (edxLabInformationDto.isInvestigationSuccessfullyCreated()) {
//                String msg = EdxELRConstant.INV_SUCCESS_CREATED.replace("%1", String.valueOf(edxLabInformationDto.getPublicHealthCaseUid()));
//                setActivityDetailLog(detailList, String.valueOf(edxLabInformationDto.getPublicHealthCaseUid()), EdxRuleAlgorothmManagerDto.STATUS_VAL.Success, msg);
//            }
//            if (edxLabInformationDto.isLabAssociatedToInv()) {
//                String msg = EdxELRConstant.LAB_ASSOCIATED_TO_INV.replace("%1", String.valueOf(edxLabInformationDto.getPublicHealthCaseUid()));
//                setActivityDetailLog(detailList, String.valueOf(edxLabInformationDto.getPublicHealthCaseUid()), EdxRuleAlgorothmManagerDto.STATUS_VAL.Success, msg);
//            }
//
//            if (edxLabInformationDto.isNotificationSuccessfullyCreated()) {
//                String msg = EdxELRConstant.NOT_SUCCESS_CREATED.replace("%1", String.valueOf(edxLabInformationDto.getNotificationUid()));
//                setActivityDetailLog(detailList, String.valueOf(edxLabInformationDto.getNotificationUid()), EdxRuleAlgorothmManagerDto.STATUS_VAL.Success, msg);
//            }
//            if (edxLabInformationDto.isInvestigationMissingFields()) {
//                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Success,
//                        EdxELRConstant.OFCI);
//            }
//            if (edxLabInformationDto.isNotificationMissingFields()) {
//                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Success,
//                        EdxELRConstant.OFCN);
//            }
            edxLabInformationDto.getEdxActivityLogDto().setEDXActivityLogDTWithVocabDetails(detailList);
        }
        catch (Exception e) {
            ArrayList<EDXActivityDetailLogDto> delailList = (ArrayList<EDXActivityDetailLogDto>)edxLabInformationDto.getEdxActivityLogDto().getEDXActivityLogDTWithVocabDetails();
            if (delailList == null) {
                delailList = new ArrayList<EDXActivityDetailLogDto>();
            }
        }
    }

    private void setActivityDetailLog(ArrayList<EDXActivityDetailLogDto> detailLogs, String id, EdxRuleAlgorothmManagerDto.STATUS_VAL status, String comment) {
        EDXActivityDetailLogDto edxActivityDetailLogDto = new EDXActivityDetailLogDto();
        edxActivityDetailLogDto.setRecordId(id);
        edxActivityDetailLogDto.setRecordType(EdxELRConstant.ELR_RECORD_TP);
        edxActivityDetailLogDto.setRecordName(EdxELRConstant.ELR_RECORD_NM);
        edxActivityDetailLogDto.setLogType(status.name());
        edxActivityDetailLogDto.setComment(comment);
        detailLogs.add(edxActivityDetailLogDto);
    }
    public void addActivityDetailLogsForWDS(EdxLabInformationDto edxLabInformationDto, String detailedMsg) {
        try{
            ArrayList<EDXActivityDetailLogDto> detailList =
                    (ArrayList<EDXActivityDetailLogDto>) edxLabInformationDto.getEdxActivityLogDto().getEDXActivityLogDTWithVocabDetails();
            if (detailList == null) {
                detailList = new ArrayList<>();
            }
            String id = String.valueOf(edxLabInformationDto.getLocalId());

            if (!edxLabInformationDto.isCreateNotificationPermission()) {
                String msg = EdxELRConstant.NO_NOT_PERMISSION.replace("%1", edxLabInformationDto.getUserName());
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure, msg);
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Success,
                        EdxELRConstant.OFCN);
            }
            if (edxLabInformationDto.isInvestigationSuccessfullyCreated()) {
                String msg = EdxELRConstant.INV_SUCCESS_CREATED.replace("%1", String.valueOf(edxLabInformationDto.getPublicHealthCaseUid()));
                setActivityDetailLog(detailList, String.valueOf(edxLabInformationDto.getPublicHealthCaseUid()), EdxRuleAlgorothmManagerDto.STATUS_VAL.Success, msg);
            }
            if (edxLabInformationDto.isLabAssociatedToInv()) {
                String msg = EdxELRConstant.LAB_ASSOCIATED_TO_INV.replace("%1", String.valueOf(edxLabInformationDto.getPublicHealthCaseUid()));
                setActivityDetailLog(detailList, String.valueOf(edxLabInformationDto.getPublicHealthCaseUid()), EdxRuleAlgorothmManagerDto.STATUS_VAL.Success, msg);
            }

            if (edxLabInformationDto.isNotificationSuccessfullyCreated()) {
                String msg = EdxELRConstant.NOT_SUCCESS_CREATED.replace("%1", String.valueOf(edxLabInformationDto.getNotificationUid()));
                setActivityDetailLog(detailList, String.valueOf(edxLabInformationDto.getNotificationUid()), EdxRuleAlgorothmManagerDto.STATUS_VAL.Success, msg);
            }
            if (edxLabInformationDto.isInvestigationMissingFields()) {
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Success,
                        EdxELRConstant.OFCI);
            }
            if (edxLabInformationDto.isNotificationMissingFields()) {
                setActivityDetailLog(detailList, id, EdxRuleAlgorothmManagerDto.STATUS_VAL.Success,
                        EdxELRConstant.OFCN);
            }
            edxLabInformationDto.getEdxActivityLogDto().setEDXActivityLogDTWithVocabDetails(detailList);
        }
        catch (Exception e) {
            log.error("Error while adding activity detail log.", e);
        }
    }

}
