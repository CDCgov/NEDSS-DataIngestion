package gov.cdc.dataingestion.nbs.ecr.service.helper.interfaces;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.ecr.model.CdaXmlAnswerMapper;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedRecord;
import gov.cdc.nedss.phdc.cda.POCDMT000040ClinicalDocument1;

public interface ICdaXmlAnswerMappingHelper {
    CdaXmlAnswerMapper mapToXmlAnswerTop(EcrSelectedRecord input,
                                         POCDMT000040ClinicalDocument1 clinicalDocument,
                                         int componentCounter) throws EcrCdaXmlException;
}
