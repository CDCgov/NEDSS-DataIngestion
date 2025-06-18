package gov.cdc.dataprocessing.service.model.phc;

import gov.cdc.dataprocessing.model.container.model.PublicHealthCaseContainer;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NndKafkaContainer {
    private PublicHealthCaseContainer publicHealthCaseContainer;
    private EdxLabInformationDto edxLabInformationDto;
}
