package gov.cdc.dataingestion.nbs.ecr.service.helper.interfaces;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.ecr.model.CdaPatientMapper;
import gov.cdc.dataingestion.nbs.ecr.model.ValueMapper;
import gov.cdc.dataingestion.nbs.ecr.model.patient.CdaPatientTelecom;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedRecord;
import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgPatientDto;
import gov.cdc.nedss.phdc.cda.POCDMT000040ClinicalDocument1;

import java.lang.reflect.Field;

public interface ICdaPatientMappingHelper {
    CdaPatientMapper mapToPatient(EcrSelectedRecord input, POCDMT000040ClinicalDocument1 clinicalDocument, int patientComponentCounter, String inv168)
            throws EcrCdaXmlException;
}
