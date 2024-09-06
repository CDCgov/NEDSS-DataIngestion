package gov.cdc.nbsDedup.nbs.srte.model;


import gov.cdc.nbsDedup.model.ProgramAreaContainer;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Getter
@Setter
public class ConditionCodeWithPA extends BaseConditionCode implements Serializable, Comparable {

    // Constructors, getters, and setters
    private String stateProgAreaCode;
    private String stateProgAreaCdDesc;

    @Override
    public int compareTo(Object o) {
        return getConditionShortNm().compareTo( ((ProgramAreaContainer) o).getConditionShortNm() );
    }
}
