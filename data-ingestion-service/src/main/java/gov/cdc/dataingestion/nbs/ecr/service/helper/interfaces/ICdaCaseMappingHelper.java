package gov.cdc.dataingestion.nbs.ecr.service.helper.interfaces;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.ecr.model.CdaCaseMapper;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedRecord;
import gov.cdc.nedss.phdc.cda.POCDMT000040ClinicalDocument1;

public interface ICdaCaseMappingHelper {
    CdaCaseMapper mapToCaseTop(EcrSelectedRecord input, POCDMT000040ClinicalDocument1 clinicalDocument,
                                      int componentCounter, int clinicalCounter, int componentCaseCounter,
                                      String inv168) throws EcrCdaXmlException;


}
