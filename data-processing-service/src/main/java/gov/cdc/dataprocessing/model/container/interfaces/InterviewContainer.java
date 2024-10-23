package gov.cdc.dataprocessing.model.container.interfaces;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.edx.EDXEventProcessDto;
import gov.cdc.dataprocessing.model.dto.phc.InterviewDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 6809 - TEST
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S6809"})
public class InterviewContainer extends BaseContainer {
    private static final long serialVersionUID = 1L;
    private InterviewDto theInterviewDto = new InterviewDto();
    private Collection<EDXEventProcessDto> edxEventProcessDtoCollection;

}
