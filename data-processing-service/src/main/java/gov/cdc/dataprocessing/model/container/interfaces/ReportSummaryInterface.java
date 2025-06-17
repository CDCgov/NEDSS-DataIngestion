package gov.cdc.dataprocessing.model.container.interfaces;

import java.sql.Timestamp;

public interface ReportSummaryInterface {
    boolean getIsTouched();

    void setItTouched(boolean touched);

    boolean getIsAssociated();

    void setItAssociated(boolean associated);

    Long getObservationUid();

    void setObservationUid(Long observationUid);

    Timestamp getActivityFromTime();

    void setActivityFromTime(Timestamp aActivityFromTime);
}
