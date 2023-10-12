package gov.cdc.dataingestion.nbs.ecr.service.helper.interfaces;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.ecr.model.CdaTreatmentMapper;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedRecord;
import gov.cdc.nedss.phdc.cda.POCDMT000040ClinicalDocument1;

public interface ICdaTreatmentMappingHelper {
    CdaTreatmentMapper mapToTreatmentTop(EcrSelectedRecord input, POCDMT000040ClinicalDocument1 clinicalDocument,
                                         int treatmentCounter, int componentCounter,
                                         int treatmentSectionCounter)
            throws EcrCdaXmlException;
}
