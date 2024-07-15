package gov.cdc.dataprocessing.repository.nbs.odse.model.observation;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class Observation_Summary {
    private Long uid;
    private Timestamp addTime;
    private String addReasonCd;

}
