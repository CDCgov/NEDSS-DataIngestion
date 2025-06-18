package gov.cdc.dataprocessing.service.interfaces.public_health_case;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PublicHealthCaseContainer;


public interface IPublicHealthCaseService {
    Long setPublicHealthCase(PublicHealthCaseContainer publicHealthCaseContainer) throws DataProcessingException;
}
