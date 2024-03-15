package gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.EDXEventProcessDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.InterviewDT;
import gov.cdc.dataprocessing.model.container.BaseContainer;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class InterviewVO extends BaseContainer {
    private static final long serialVersionUID = 1L;
    private InterviewDT theInterviewDT = new InterviewDT();
    private Collection<EDXEventProcessDT> edxEventProcessDTCollection;

}
