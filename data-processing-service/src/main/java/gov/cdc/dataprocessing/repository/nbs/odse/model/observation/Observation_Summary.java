package gov.cdc.dataprocessing.repository.nbs.odse.model.observation;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Observation_Summary {
    private Long uid;
    private Timestamp addTime;
    private String addReasonCd;

}
