package gov.cdc.dataprocessing.service.interfaces.other;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.container.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.phdc.Container;
import gov.cdc.dataprocessing.repository.nbs.msgoute.model.NbsInterfaceModel;
import jakarta.xml.bind.JAXBException;

public interface IDataExtractionService {
    LabResultProxyContainer parsingDataToObject(NbsInterfaceModel nbsInterfaceModel, EdxLabInformationDto edxLabInformationDto) throws DataProcessingConsumerException, JAXBException, DataProcessingException;
//    Container parsingElrXmlPayload(String xmlPayload) throws JAXBException;
}
