package gov.cdc.dataingestion.nbs.ecr.service.helper.interfaces;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.ecr.model.CdaPlaceMapper;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedRecord;
import gov.cdc.nedss.phdc.cda.POCDMT000040Section;

public interface ICdaPlaceMappingHelper {
    CdaPlaceMapper mapToPlaceTop(EcrSelectedRecord input,
                                 int performerComponentCounter, int componentCounter,
                                 int performerSectionCounter,
                                 POCDMT000040Section section) throws EcrCdaXmlException;
}
