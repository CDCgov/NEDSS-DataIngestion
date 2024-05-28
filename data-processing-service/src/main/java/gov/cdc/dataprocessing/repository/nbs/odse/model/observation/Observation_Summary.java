package gov.cdc.dataprocessing.repository.nbs.odse.model.observation;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Timestamp;

@Data
public class Observation_Summary {
    private Long uid;
    private Timestamp addTime;
    private String addReasonCd;

}
