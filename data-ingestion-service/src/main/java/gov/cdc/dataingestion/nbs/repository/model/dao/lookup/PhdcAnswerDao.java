package gov.cdc.dataingestion.nbs.repository.model.dao.lookup;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
public class PhdcAnswerDao {
    private String code;
    private String codeSystem;
    private String codeSystemName;
    private String displayName;
    private String transCode;
    private String transCodeSystem;
    private String transCodeSystemName;
    private String transDisplayName;
}
