package gov.cdc.dataprocessing.repository.nbs.srte.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "Condition_code")
public class ConditionCode extends BaseConditionCode {
}
