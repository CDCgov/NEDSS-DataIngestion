package gov.cdc.dataingestion.nbs.ecr.service.helper.interfaces;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.ecr.model.CdaOrganizationMapper;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedRecord;
import gov.cdc.nedss.phdc.cda.POCDMT000040Section;

public interface ICdaOrgMappingHelper {
    CdaOrganizationMapper mapToOrganizationTop(EcrSelectedRecord input, POCDMT000040Section clinicalDocument,
                                               int performerComponentCounter, int componentCounter,
                                               int performerSectionCounter)
            throws EcrCdaXmlException;
}
