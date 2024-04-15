package gov.cdc.dataprocessing.model.container;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.InterventionVO;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.InterviewVO;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.PublicHealthCaseVO;

public interface PageProxyContainer {
    public static final long serialVersionUID = 1L;

    public String getPageProxyTypeCd();
    public void setPageProxyTypeCd(String pageProxyTypeCd);
    public PublicHealthCaseVO getPublicHealthCaseVO();
    public void setPublicHealthCaseVO(PublicHealthCaseVO publicHealthCaseVO);
    public InterviewVO getInterviewVO();
    public void setInterviewVO(InterviewVO interviewVO);
    public InterventionVO getInterventionVO();
    public void setInterventionVO(InterventionVO interventionVO);
}
