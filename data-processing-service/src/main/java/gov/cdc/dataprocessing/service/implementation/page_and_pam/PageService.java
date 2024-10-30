package gov.cdc.dataprocessing.service.implementation.page_and_pam;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.LabReportSummaryContainer;
import gov.cdc.dataprocessing.model.container.model.PageActProxyContainer;
import gov.cdc.dataprocessing.service.interfaces.page_and_pam.IPageService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IInvestigationService;
import gov.cdc.dataprocessing.utilities.component.page_and_pam.PageRepositoryUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

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


