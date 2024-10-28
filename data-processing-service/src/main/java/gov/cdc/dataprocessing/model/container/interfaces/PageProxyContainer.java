package gov.cdc.dataprocessing.model.container.interfaces;

import gov.cdc.dataprocessing.model.container.model.InterventionContainer;
import gov.cdc.dataprocessing.model.container.model.PublicHealthCaseContainer;

/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809"})
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
