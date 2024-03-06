package gov.cdc.dataprocessing.service.interfaces;

public interface IParticipationService {
    Long findPatientMprUidByObservationUid(String classCode, String typeCode, Long actUid);
}
