package gov.cdc.nbsDedup.model.container.interfaces;

import java.sql.Timestamp;

public interface ReportSummaryInterface {


    public Long getObservationUid();

    public void setObservationUid(Long observationUid);

    public Timestamp getActivityFromTime();

    public void setActivityFromTime(Timestamp aActivityFromTime);
}
