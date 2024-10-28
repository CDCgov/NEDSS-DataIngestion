package gov.cdc.dataingestion.nbs.repository.model.dao.lookup;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
/**
 1118 - require constructor complaint
 * */
@SuppressWarnings({"java:S1118",""})
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
