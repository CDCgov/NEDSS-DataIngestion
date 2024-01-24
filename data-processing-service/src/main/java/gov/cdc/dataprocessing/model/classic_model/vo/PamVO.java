package gov.cdc.dataprocessing.model.classic_model.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.Map;

@Getter
@Setter
public class PamVO {
    private static final long serialVersionUID = 1L;
    private Map<Object, Object> pamAnswerDTMap;
    private Collection<Object> actEntityDTCollection;
    private Map<Object, Object> pageRepeatingAnswerDTMap;
    private Map<Object, Object>  answerDTMap;

}
