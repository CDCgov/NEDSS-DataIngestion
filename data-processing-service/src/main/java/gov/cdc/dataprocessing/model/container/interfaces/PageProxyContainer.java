package gov.cdc.dataprocessing.model.container.interfaces;

import gov.cdc.dataprocessing.model.container.interfaces.InterviewContainer;
import gov.cdc.dataprocessing.model.container.model.InterventionContainer;
import gov.cdc.dataprocessing.model.container.model.PublicHealthCaseContainer;

public interface PageProxyContainer {
    public static final long serialVersionUID = 1L;

    public String getPageProxyTypeCd();
    public void setPageProxyTypeCd(String pageProxyTypeCd);
    public PublicHealthCaseContainer getPublicHealthCaseVO();
    public void setPublicHealthCaseVO(PublicHealthCaseContainer publicHealthCaseContainer);
    public InterviewContainer getInterviewVO();
    public void setInterviewVO(InterviewContainer interviewContainer);
    public InterventionContainer getInterventionVO();
    public void setInterventionVO(InterventionContainer interventionContainer);
}
