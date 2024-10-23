package gov.cdc.dataprocessing.repository.nbs.srte.model;


import gov.cdc.dataprocessing.model.container.model.ProgramAreaContainer;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Getter
@Setter
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 6809 - TEST
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S6809"})
public class ConditionCodeWithPA extends BaseConditionCode implements Serializable, Comparable {

    // Constructors, getters, and setters
    private String stateProgAreaCode;
    private String stateProgAreaCdDesc;

    @Override
    public int compareTo(Object o) {
        return getConditionShortNm().compareTo( ((ProgramAreaContainer) o).getConditionShortNm() );
    }
}