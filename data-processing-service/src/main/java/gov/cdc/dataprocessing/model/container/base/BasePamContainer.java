package gov.cdc.dataprocessing.model.container.base;

import gov.cdc.dataprocessing.model.dto.nbs.NbsActEntityDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsAnswerDto;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186"})
public class BasePamContainer implements Serializable {
    private static final long serialVersionUID = 1L;
    private Map<Object, Object> pamAnswerDTMap = new HashMap<>();
    private Collection<NbsActEntityDto> actEntityDTCollection;
    private Map<Object, Object> pageRepeatingAnswerDTMap = new HashMap<>();;
    private Map<Object, NbsAnswerDto>  answerDTMap;

}
