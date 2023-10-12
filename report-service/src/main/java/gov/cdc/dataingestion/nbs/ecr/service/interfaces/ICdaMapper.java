package gov.cdc.dataingestion.nbs.ecr.service.interfaces;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedRecord;

public interface ICdaMapper {
    String tranformSelectedEcrToCDAXml(EcrSelectedRecord input) throws EcrCdaXmlException;
}
