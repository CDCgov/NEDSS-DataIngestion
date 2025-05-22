package gov.cdc.dataprocessing.service.implementation.manager;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PageActProxyContainer;
import gov.cdc.dataprocessing.model.container.model.PamProxyContainer;
import gov.cdc.dataprocessing.model.container.model.PublicHealthCaseContainer;
import gov.cdc.dataprocessing.model.dto.edx.EdxRuleAlgorothmManagerDto;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityDetailLogDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.service.interfaces.action.ILabReportProcessing;
import gov.cdc.dataprocessing.service.interfaces.page_and_pam.IPageService;
import gov.cdc.dataprocessing.service.interfaces.page_and_pam.IPamService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IInvestigationNotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class LabService {
    private final IPageService pageService;
    private final IPamService pamService;
    private final ILabReportProcessing labReportProcessing;
    private final IInvestigationNotificationService investigationNotificationService;

    public LabService(IPageService pageService, IPamService pamService, ILabReportProcessing labReportProcessing, IInvestigationNotificationService investigationNotificationService) {
        this.pageService = pageService;
        this.pamService = pamService;
        this.labReportProcessing = labReportProcessing;
        this.investigationNotificationService = investigationNotificationService;
    }

    @Transactional
    public Long handlePageContainer(PageActProxyContainer pageAct, EdxLabInformationDto edxDto) throws DataProcessingException {
        return pageService.setPageProxyWithAutoAssoc(
                NEDSSConstant.CASE,
                pageAct,
                edxDto.getRootObserbationUid(),
                NEDSSConstant.LABRESULT_CODE,
                null);
    }

    @Transactional
    public Long handlePamContainer(PamProxyContainer pamProxy, EdxLabInformationDto edxDto) throws DataProcessingException {
        return pamService.setPamProxyWithAutoAssoc(
                pamProxy,
                edxDto.getRootObserbationUid(),
                NEDSSConstant.LABRESULT_CODE);
    }

    @Transactional
    public void handleMarkAsReviewed(ObservationDto obsDto, EdxLabInformationDto edxDto) throws DataProcessingException {
        labReportProcessing.markAsReviewedHandler(obsDto.getObservationUid(), edxDto);
        Long associatedPhcUid = edxDto.getAssociatedPublicHealthCaseUid();
        if (associatedPhcUid != null && associatedPhcUid > 0) {
            edxDto.setPublicHealthCaseUid(associatedPhcUid);
            edxDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_21);
            edxDto.setLabAssociatedToInv(true);
        } else {
            edxDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_11);
        }
    }

//    @Transactional
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleNndNotification(PublicHealthCaseContainer phcContainerModel, EdxLabInformationDto edxDto) throws DataProcessingException {
        EDXActivityDetailLogDto detailLog = investigationNotificationService.sendNotification(phcContainerModel, edxDto.getNndComment());
        detailLog.setRecordType(EdxELRConstant.ELR_RECORD_TP);
        detailLog.setRecordName(EdxELRConstant.ELR_RECORD_NM);

        ArrayList<EDXActivityDetailLogDto> details = (ArrayList<EDXActivityDetailLogDto>) edxDto.getEdxActivityLogDto().getEDXActivityLogDTWithVocabDetails();
        if (details == null) {
            details = new ArrayList<>();
        }
        details.add(detailLog);
        edxDto.getEdxActivityLogDto().setEDXActivityLogDTWithVocabDetails(details);

        if (EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure.name().equals(detailLog.getLogType())) {
            String comment = detailLog.getComment();
            if (comment != null && comment.contains(EdxELRConstant.MISSING_NOTF_REQ_FIELDS)) {
                edxDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_8);
                edxDto.setNotificationMissingFields(true);
            } else {
                edxDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_10);
            }
            throw new DataProcessingException("MISSING NOTI REQUIRED: " + comment);
        } else {
            edxDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_6);
        }
    }

}
