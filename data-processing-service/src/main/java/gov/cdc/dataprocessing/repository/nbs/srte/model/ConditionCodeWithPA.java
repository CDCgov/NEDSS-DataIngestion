package gov.cdc.dataprocessing.repository.nbs.srte.model;


import gov.cdc.dataprocessing.model.container.ProgramAreaContainer;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "Condition_code")
public class ConditionCodeWithPA extends ConditionCode implements Serializable, Comparable {

    // Constructors, getters, and setters
    private String stateProgAreaCode;
    private String stateProgAreaCdDesc;

    @Override
    public int compareTo(Object o) {
        return getConditionShortNm().compareTo( ((ProgramAreaContainer) o).getConditionShortNm() );
    }
}