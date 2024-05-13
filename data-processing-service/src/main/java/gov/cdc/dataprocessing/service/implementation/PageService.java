package gov.cdc.dataprocessing.service.implementation;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.CaseManagementDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.PageActProxyVO;
import gov.cdc.dataprocessing.model.container.LabReportSummaryContainer;
import gov.cdc.dataprocessing.service.interfaces.IInvestigationService;
import gov.cdc.dataprocessing.service.interfaces.IPageService;
import gov.cdc.dataprocessing.utilities.component.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

@Service
public class PageService implements IPageService {
    private static final Logger logger = LoggerFactory.getLogger(PageService.class);
    private final IInvestigationService investigationService;

    private final PageRepositoryUtil pageRepositoryUtil;


    public PageService(IInvestigationService investigationService, PageRepositoryUtil pageRepositoryUtil) {
        this.investigationService = investigationService;

        this.pageRepositoryUtil = pageRepositoryUtil;
    }

    public Long setPageProxyWithAutoAssoc(String typeCd, PageActProxyVO pageProxyVO, Long observationUid,
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

    public Long setPageProxyWithAutoAssoc(PageActProxyVO pageProxyVO, Long observationUid, 
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
                if(pageProxyVO.getPublicHealthCaseVO().getTheCaseManagementDT()!=null && pageProxyVO.getPublicHealthCaseVO().getTheCaseManagementDT().getInitFollUp()!=null)
                {
                    labSumVO.setProcessingDecisionCd(pageProxyVO.getPublicHealthCaseVO().getTheCaseManagementDT().getInitFollUp());
                }
                else
                {
                    labSumVO.setProcessingDecisionCd(processingDecision);
                }
                observationColl.add(labSumVO);

            }
            // TODO: MORBIDITY
            else
            {
//                MorbReportSummaryVO morbSumVO = new MorbReportSummaryVO();
//                morbSumVO.setItTouched(true);
//                morbSumVO.setItAssociated(true);
//                morbSumVO.setObservationUid(observationUid);
//                //set the add_reason_code(processing decision) for act_relationship  from initial follow-up(pre-populated from Morb report processing decision) field in case management
//                if(pageProxyVO.getPublicHealthCaseVO().getTheCaseManagementDT()!=null && pageProxyVO.getPublicHealthCaseVO().getTheCaseManagementDT().getInitFollUp()!=null)
//                    morbSumVO.setProcessingDecisionCd(pageProxyVO.getPublicHealthCaseVO().getTheCaseManagementDT().getInitFollUp());
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


    private void updateNamedAsContactDisposition(CaseManagementDT caseManagementDT) throws DataProcessingException {
        if (caseManagementDT.getPublicHealthCaseUid() == null)  //auto field followup create in progress..
            return;
        try {

            String dispositionCd =caseManagementDT.getFldFollUpDispo();
            if(dispositionCd!=null && dispositionCd.equalsIgnoreCase(NEDSSConstant.FROM1_A_PREVENTATIVE_TREATMENT)) {
                dispositionCd = NEDSSConstant.TO1_Z_PREVIOUS_PREVENTATIVE_TREATMENT;
            }
            else if(dispositionCd!=null && dispositionCd.equalsIgnoreCase(NEDSSConstant.FROM2_C_INFECTED_BROUGHT_TO_TREATMENT)) {
                dispositionCd = NEDSSConstant.TO2_E_PREVIOUSLY_TREATED_FOR_THIS_INFECTION;
            }
            Timestamp fldFollowUpDispDate=caseManagementDT.getFldFollUpDispoDate();


            //TODO: CONTACT DIPOSITION
//            int numbersOfAssociatedContactRecords= ctContactDAO.countNamedAsContactDispoInvestigations(caseManagementDT.getPublicHealthCaseUid());
//            logger.debug("numbersOfAssociatedContactRecords is "+numbersOfAssociatedContactRecords);
//
//            if(numbersOfAssociatedContactRecords>0) {
//                ctContactDAO.updateNamedAsContactDispoInvestigation(dispositionCd,fldFollowUpDispDate, caseManagementDT.getPublicHealthCaseUid());
//                logger.debug("updateNamedAsContactDisposition update was successful for "+numbersOfAssociatedContactRecords+" numbers of associated investigations.");
//            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(),e);
        }
    }





}


