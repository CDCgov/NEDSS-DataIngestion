package gov.cdc.dataprocessing.repository.nbs.srte.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "Condition_code")
public class ConditionCode extends BaseConditionCode {
}
