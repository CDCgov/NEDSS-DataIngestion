package gov.cdc.dataprocessing.service.interfaces;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ParticipationDT;

public interface IParticipationService {
    Long findPatientMprUidByObservationUid(String classCode, String typeCode, Long actUid);
    void saveParticipation(ParticipationDT participationDT) throws DataProcessingException;
    void saveParticipationHist(ParticipationDT participationDT);
}
