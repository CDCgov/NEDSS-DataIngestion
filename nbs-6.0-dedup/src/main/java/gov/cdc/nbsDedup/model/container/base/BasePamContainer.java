package gov.cdc.nbsDedup.model.container.base;


import gov.cdc.nbsDedup.model.dto.nbs.NbsActEntityDto;
import gov.cdc.nbsDedup.model.dto.nbs.NbsAnswerDto;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

@Getter
@Setter
public class BasePamContainer implements Serializable {
    private static final long serialVersionUID = 1L;
    private Map<Object, Object> pamAnswerDTMap;
    private Collection<NbsActEntityDto> actEntityDTCollection;
    private Map<Object, Object> pageRepeatingAnswerDTMap;
    private Map<Object, NbsAnswerDto>  answerDTMap;

}
