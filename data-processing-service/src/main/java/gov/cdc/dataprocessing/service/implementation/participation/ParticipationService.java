package gov.cdc.dataprocessing.service.implementation.participation;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.participation.Participation;
import gov.cdc.dataprocessing.repository.nbs.odse.model.participation.ParticipationHist;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.stored_proc.ParticipationStoredProcRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.entity.EntityRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.participation.ParticipationHistRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.participation.ParticipationRepository;
import gov.cdc.dataprocessing.service.interfaces.paticipation.IParticipationService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ParticipationService implements IParticipationService {
    private static final Logger logger = LoggerFactory.getLogger(ParticipationService.class);
    private final ParticipationRepository participationRepository;
    private final ParticipationHistRepository participationHistRepository;
    private final ParticipationStoredProcRepository participationStoredProcRepository;

    private final EntityRepository entityRepository;

    public ParticipationService(ParticipationRepository participationRepository,
                                ParticipationHistRepository participationHistRepository,
                                ParticipationStoredProcRepository participationStoredProcRepository, EntityRepository entityRepository) {
        this.participationRepository = participationRepository;
        this.participationHistRepository = participationHistRepository;
        this.participationStoredProcRepository = participationStoredProcRepository;
        this.entityRepository = entityRepository;
    }

    public Long findPatientMprUidByObservationUid(String classCode, String typeCode, Long actUid) {
        var result = participationRepository.findPatientMprUidByObservationUid(classCode, typeCode, actUid);
        return result.map(longs -> longs.get(0)).orElse(null);
    }

    @Transactional
    public void saveParticipationHist(ParticipationDto participationDto) {
        var patHist = new ParticipationHist(participationDto);
        participationHistRepository.save(patHist);
        participationDto.setItNew(false);
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

                //TODO: EVALUATE entity check is patch - need to figure out
//                var result = entityRepository.findById(data.getSubjectEntityUid());
//                if (result.isPresent()) {
//
//                }
                participationRepository.save(data);


//                                participationStoredProcRepository.insertParticipation(participationDto);
            } catch (Exception e) {
                throw new DataProcessingException(e.getMessage(), e);
            }

        }
    }

    private void deleteParticipationByPk(Long subjectId, Long actId, String classCode) {
        participationRepository.deleteParticipationByPk(subjectId, actId, classCode);
    }

}
