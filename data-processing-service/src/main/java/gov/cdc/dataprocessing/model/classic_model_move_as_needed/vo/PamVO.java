package gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.NbsActEntityDT;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.Map;

@Getter
@Setter
public class PamVO {
    private static final long serialVersionUID = 1L;
    private Map<Object, Object> pamAnswerDTMap;
    private Collection<NbsActEntityDT> actEntityDTCollection;
    private Map<Object, Object> pageRepeatingAnswerDTMap;
    private Map<Object, Object>  answerDTMap;

}
