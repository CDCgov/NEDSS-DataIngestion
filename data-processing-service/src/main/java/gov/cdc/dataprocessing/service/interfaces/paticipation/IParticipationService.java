package gov.cdc.dataprocessing.service.interfaces.paticipation;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;

public interface IParticipationService {
    Long findPatientMprUidByObservationUid(String classCode, String typeCode, Long actUid);

    void saveParticipation(ParticipationDto participationDto) throws DataProcessingException;

    void saveParticipationHist(ParticipationDto participationDto) throws DataProcessingException;
}
