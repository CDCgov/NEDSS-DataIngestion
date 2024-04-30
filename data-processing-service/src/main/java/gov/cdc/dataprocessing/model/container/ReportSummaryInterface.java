package gov.cdc.dataprocessing.model.container;

import java.sql.Timestamp;

public interface ReportSummaryInterface {
    public boolean getIsTouched();

    public void setItTouched(boolean touched);

    public boolean getIsAssociated();

    public void setItAssociated(boolean associated);

    public Long getObservationUid();

    public void setObservationUid(Long observationUid);

    public Timestamp getActivityFromTime();

    public void setActivityFromTime(Timestamp aActivityFromTime);
}
