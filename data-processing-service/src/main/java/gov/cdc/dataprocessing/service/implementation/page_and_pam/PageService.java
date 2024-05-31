package gov.cdc.dataprocessing.service.implementation.page_and_pam;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.LabReportSummaryContainer;
import gov.cdc.dataprocessing.model.container.model.PageActProxyContainer;
import gov.cdc.dataprocessing.model.dto.phc.CaseManagementDto;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IInvestigationService;
import gov.cdc.dataprocessing.service.interfaces.page_and_pam.IPageService;
import gov.cdc.dataprocessing.utilities.component.page_and_pam.PageRepositoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        try {
            if(typeCd.equalsIgnoreCase(NEDSSConstant.CASE)){
                publicHealthCaseUID= setPageProxyWithAutoAssoc(pageProxyVO,observationUid,observationTypeCd, processingDecision);
            }
        }
        catch (Exception re) {
            throw new DataProcessingException(re.getMessage());
        }
        return publicHealthCaseUID;
    }

    public Long setPageProxyWithAutoAssoc(PageActProxyContainer pageProxyVO, Long observationUid,
                                          String observationTypeCd, String processingDecision) throws  DataProcessingException {
        Long publicHealthCaseUID;
        try {
            publicHealthCaseUID = pageRepositoryUtil.setPageActProxyVO(pageProxyVO);
            Collection<LabReportSummaryContainer> observationColl = new ArrayList<>();
            if (observationTypeCd.equalsIgnoreCase(NEDSSConstant.LAB_DISPALY_FORM))
            {
                LabReportSummaryContainer labSumVO = new LabReportSummaryContainer();
                labSumVO.setItTouched(true);
                labSumVO.setItAssociated(true);
                labSumVO.setObservationUid(observationUid);
                //set the add_reason_code(processing decision) for act_relationship  from initial follow-up(pre-populated from Lab report processing decision) field in case management
                if(pageProxyVO.getPublicHealthCaseContainer().getTheCaseManagementDto()!=null && pageProxyVO.getPublicHealthCaseContainer().getTheCaseManagementDto().getInitFollUp()!=null)
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
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(),e);
        }
        return publicHealthCaseUID;
    }


    private void updateNamedAsContactDisposition(CaseManagementDto caseManagementDto) throws DataProcessingException {
        if (caseManagementDto.getPublicHealthCaseUid() == null)  //auto field followup create in progress..
            return;
        try {

            String dispositionCd = caseManagementDto.getFldFollUpDispo();
            if(dispositionCd!=null && dispositionCd.equalsIgnoreCase(NEDSSConstant.FROM1_A_PREVENTATIVE_TREATMENT)) {
                dispositionCd = NEDSSConstant.TO1_Z_PREVIOUS_PREVENTATIVE_TREATMENT;
            }
            else if(dispositionCd!=null && dispositionCd.equalsIgnoreCase(NEDSSConstant.FROM2_C_INFECTED_BROUGHT_TO_TREATMENT)) {
                dispositionCd = NEDSSConstant.TO2_E_PREVIOUSLY_TREATED_FOR_THIS_INFECTION;
            }
            Timestamp fldFollowUpDispDate= caseManagementDto.getFldFollUpDispoDate();


//            int numbersOfAssociatedContactRecords= ctContactDAO.countNamedAsContactDispoInvestigations(caseManagementDto.getPublicHealthCaseUid());
//            logger.debug("numbersOfAssociatedContactRecords is "+numbersOfAssociatedContactRecords);
//
//            if(numbersOfAssociatedContactRecords>0) {
//                ctContactDAO.updateNamedAsContactDispoInvestigation(dispositionCd,fldFollowUpDispDate, caseManagementDto.getPublicHealthCaseUid());
//                logger.debug("updateNamedAsContactDisposition update was successful for "+numbersOfAssociatedContactRecords+" numbers of associated investigations.");
//            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(),e);
        }
    }





}


