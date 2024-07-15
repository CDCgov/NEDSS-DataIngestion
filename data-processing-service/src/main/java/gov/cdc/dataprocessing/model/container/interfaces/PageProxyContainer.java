package gov.cdc.dataprocessing.model.container.interfaces;

import gov.cdc.dataprocessing.model.container.model.InterventionContainer;
import gov.cdc.dataprocessing.model.container.model.PublicHealthCaseContainer;

public interface PageProxyContainer {
    long serialVersionUID = 1L;

    String getPageProxyTypeCd();

    void setPageProxyTypeCd(String pageProxyTypeCd);

    PublicHealthCaseContainer getPublicHealthCaseVO();

    void setPublicHealthCaseVO(PublicHealthCaseContainer publicHealthCaseContainer);

    InterviewContainer getInterviewVO();

    void setInterviewVO(InterviewContainer interviewContainer);

    InterventionContainer getInterventionVO();

    void setInterventionVO(InterventionContainer interventionContainer);
}
