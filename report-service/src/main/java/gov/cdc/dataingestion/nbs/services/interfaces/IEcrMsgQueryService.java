package gov.cdc.dataingestion.nbs.services.interfaces;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedRecord;
import org.springframework.stereotype.Service;

@Service
public interface IEcrMsgQueryService {
    EcrSelectedRecord GetSelectedEcrRecord() throws EcrCdaXmlException;
    EcrSelectedRecord getSelectedEcrFromJson() throws EcrCdaXmlException;
}
