package gov.cdc.dataprocessing.service.implementation.participation;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.participation.Participation;
import gov.cdc.dataprocessing.repository.nbs.odse.model.participation.ParticipationHist;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.participation.ParticipationHistRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.participation.ParticipationRepository;
import gov.cdc.dataprocessing.service.interfaces.paticipation.IParticipationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 6541 - brain method complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541"})
public class ParticipationService implements IParticipationService {
    private final ParticipationRepository participationRepository;
    private final ParticipationHistRepository participationHistRepository;


    public ParticipationService(ParticipationRepository participationRepository,
                                ParticipationHistRepository participationHistRepository
    ) {
        this.participationRepository = participationRepository;
        this.participationHistRepository = participationHistRepository;
    }

    public Long findPatientMprUidByObservationUid(String classCode, String typeCode, Long actUid) {
        var result = participationRepository.findPatientMprUidByObservationUid(classCode, typeCode, actUid);
        return result.map(longs -> longs.get(0)).orElse(null);
    }

    @Transactional
    public void saveParticipationHist(ParticipationDto participationDto) throws DataProcessingException {
        try {

            var res = participationHistRepository.findVerNumberByKey(participationDto.getSubjectEntityUid(), participationDto.getActUid(), participationDto.getTypeCd());
            Integer ver = 1;
            if (res.isPresent()) {
                if(!res.get().isEmpty()) {
                    ver = Collections.max(res.get());
                }
            }

            var patHist = new ParticipationHist(participationDto);
            patHist.setVersionCtrlNbr(ver);
            participationHistRepository.save(patHist);
            participationDto.setItNew(false);
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }

    }

    @Transactional
    public void saveParticipation(ParticipationDto participationDto) throws DataProcessingException {
        if (participationDto.isItNew() || participationDto.isItDirty()) {
            persistingParticipation(participationDto);
        } else if (participationDto.isItDelete()) {
            deleteParticipationByPk(participationDto.getSubjectEntityUid(), participationDto.getActUid(), participationDto.getActClassCd());
        }
    }
    private void persistingParticipation(ParticipationDto participationDto) throws DataProcessingException {
        if (participationDto.getSubjectEntityUid() != null && participationDto.getActUid() != null) {
            try {
                var data = new Participation(participationDto);

                participationRepository.save(data);

            } catch (Exception e) {
                throw new DataProcessingException(e.getMessage(), e);
            }

        }
    }

    private void deleteParticipationByPk(Long subjectId, Long actId, String classCode) throws DataProcessingException {
        try {
            participationRepository.deleteParticipationByPk(subjectId, actId, classCode);
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

}
