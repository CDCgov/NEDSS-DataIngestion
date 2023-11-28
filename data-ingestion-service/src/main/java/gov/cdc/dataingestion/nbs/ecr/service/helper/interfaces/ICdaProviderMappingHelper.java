package gov.cdc.dataingestion.nbs.ecr.service.helper.interfaces;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.ecr.model.CdaProviderMapper;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedRecord;
import gov.cdc.nedss.phdc.cda.POCDMT000040Section;

public interface ICdaProviderMappingHelper {
    CdaProviderMapper mapToProviderTop(EcrSelectedRecord input, POCDMT000040Section clinicalDocument,
                                       String inv168, int performerComponentCounter, int componentCounter,
                                       int performerSectionCounter) throws EcrCdaXmlException;
}
