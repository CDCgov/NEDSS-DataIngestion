package gov.cdc.dataprocessing.model.classic_model.vo;

import gov.cdc.dataprocessing.model.classic_model.dt.EDXEventProcessDT;
import gov.cdc.dataprocessing.model.classic_model.dt.InterviewDT;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class InterviewVO extends AbstractVO {
    private static final long serialVersionUID = 1L;
    private InterviewDT theInterviewDT = new InterviewDT();
    private Collection<EDXEventProcessDT> edxEventProcessDTCollection;

}
