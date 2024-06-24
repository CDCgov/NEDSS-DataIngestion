package gov.cdc.dataprocessing.service.implementation.page_and_pam;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.LabReportSummaryContainer;
import gov.cdc.dataprocessing.model.container.model.PageActProxyContainer;
import gov.cdc.dataprocessing.model.dto.phc.CaseManagementDto;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IInvestigationService;
import gov.cdc.dataprocessing.service.interfaces.page_and_pam.IPageService;
import gov.cdc.dataprocessing.utilities.component.page_and_pam.PageRepositoryUtil;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

@Service
public class PageService implements IPageService {
    private final IInvestigationService investigationService;

    private final PageRepositoryUtil pageRepositoryUtil;


    public PageService(IInvestigationService investigationService, PageRepositoryUtil pageRepositoryUtil) {
        this.investigationService = investigationService;

        this.pageRepositoryUtil = pageRepositoryUtil;
    }

    public Long setPageProxyWithAutoAssoc(String typeCd, PageActProxyContainer pageProxyVO, Long observationUid,
                                          String observationTypeCd, String processingDecision) throws DataProcessingException {
        Long publicHealthCaseUID=null;
        if(typeCd.equalsIgnoreCase(NEDSSConstant.CASE)){
            publicHealthCaseUID= setPageProxyWithAutoAssoc(pageProxyVO,observationUid,observationTypeCd, processingDecision);
        }
        return publicHealthCaseUID;
    }

    private Long setPageProxyWithAutoAssoc(PageActProxyContainer pageProxyVO, Long observationUid,
                                          String observationTypeCd, String processingDecision) throws  DataProcessingException {
        Long publicHealthCaseUID;
        publicHealthCaseUID = pageRepositoryUtil.setPageActProxyVO(pageProxyVO);
        Collection<LabReportSummaryContainer> observationColl = new ArrayList<>();
        if (observationTypeCd.equalsIgnoreCase(NEDSSConstant.LAB_DISPALY_FORM))
        {
            LabReportSummaryContainer labSumVO = new LabReportSummaryContainer();
            labSumVO.setItTouched(true);
            labSumVO.setItAssociated(true);
            labSumVO.setObservationUid(observationUid);
            //set the add_reason_code(processing decision) for act_relationship  from initial follow-up(pre-populated from Lab report processing decision) field in case management
            if(pageProxyVO.getPublicHealthCaseContainer().getTheCaseManagementDto()!=null
                    && pageProxyVO.getPublicHealthCaseContainer().getTheCaseManagementDto().getInitFollUp()!=null)
            {
                labSumVO.setProcessingDecisionCd(pageProxyVO.getPublicHealthCaseContainer().getTheCaseManagementDto().getInitFollUp());
            }
            else
            {
                labSumVO.setProcessingDecisionCd(processingDecision);
            }
            observationColl.add(labSumVO);

        }
        else
        {
//                MorbReportSummaryVO morbSumVO = new MorbReportSummaryVO();
//                morbSumVO.setItTouched(true);
//                morbSumVO.setItAssociated(true);
//                morbSumVO.setObservationUid(observationUid);
//                //set the add_reason_code(processing decision) for act_relationship  from initial follow-up(pre-populated from Morb report processing decision) field in case management
//                if(pageProxyVO.getPublicHealthCaseContainer().getTheCaseManagementDto()!=null && pageProxyVO.getPublicHealthCaseContainer().getTheCaseManagementDto().getInitFollUp()!=null)
//                    morbSumVO.setProcessingDecisionCd(pageProxyVO.getPublicHealthCaseContainer().getTheCaseManagementDto().getInitFollUp());
//                else
//                    morbSumVO.setProcessingDecisionCd(processingDecision);
//                observationColl.add(morbSumVO);
        }

        investigationService.setObservationAssociationsImpl(publicHealthCaseUID, observationColl, true);
        return publicHealthCaseUID;
    }






}


