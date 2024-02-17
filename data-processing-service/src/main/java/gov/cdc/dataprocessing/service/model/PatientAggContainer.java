package gov.cdc.dataprocessing.service.model;

import gov.cdc.dataprocessing.model.classic_model.vo.PersonVO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientAggContainer {
    PersonVO personVO;
    PersonVO providerVO;
}
