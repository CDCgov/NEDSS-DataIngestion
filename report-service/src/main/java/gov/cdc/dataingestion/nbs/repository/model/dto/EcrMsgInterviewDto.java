package gov.cdc.dataingestion.nbs.repository.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@NoArgsConstructor
@Getter
@Setter
public class EcrMsgInterviewDto {
    private Integer msgContainerUid;
    private String ixsLocalId;
    private String ixsIntervieweeId;
    private String ixsAuthorId;
    private Timestamp ixsEffectiveTime;
    private Timestamp ixsInterviewDt;
    private String ixsInterviewLocCd;
    private String ixsIntervieweeRoleCd;
    private String ixsInterviewTypeCd;
    private String ixsStatusCd;

}
