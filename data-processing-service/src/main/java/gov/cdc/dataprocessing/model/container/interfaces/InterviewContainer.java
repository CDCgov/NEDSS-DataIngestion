package gov.cdc.dataprocessing.model.container.interfaces;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.edx.EDXEventProcessDto;
import gov.cdc.dataprocessing.model.dto.phc.InterviewDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter

public class InterviewContainer extends BaseContainer {
    private static final long serialVersionUID = 1L;
    private InterviewDto theInterviewDto = new InterviewDto();
    private Collection<EDXEventProcessDto> edxEventProcessDtoCollection;

}
