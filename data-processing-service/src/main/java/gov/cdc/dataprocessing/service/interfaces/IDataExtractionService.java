package gov.cdc.dataprocessing.service.interfaces;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model.dt.EdxLabInformationDT;
import gov.cdc.dataprocessing.model.classic_model.vo.LabResultProxyVO;
import gov.cdc.dataprocessing.model.phdc.Container;
import gov.cdc.dataprocessing.repository.nbs.msgoute.model.NbsInterfaceModel;
import jakarta.xml.bind.JAXBException;

public interface IDataExtractionService {
    LabResultProxyVO parsingDataToObject(NbsInterfaceModel nbsInterfaceModel, EdxLabInformationDT edxLabInformationDT) throws DataProcessingConsumerException, JAXBException, DataProcessingException;
    Container parsingElrXmlPayload(String xmlPayload) throws JAXBException;
}
