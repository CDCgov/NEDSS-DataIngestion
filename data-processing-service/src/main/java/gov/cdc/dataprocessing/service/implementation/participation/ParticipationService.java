package gov.cdc.dataprocessing.service.implementation.participation;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.id_class.ParticipationHistId;
import gov.cdc.dataprocessing.repository.nbs.odse.model.participation.Participation;
import gov.cdc.dataprocessing.repository.nbs.odse.model.participation.ParticipationHist;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.stored_proc.ParticipationStoredProcRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.entity.EntityRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.participation.ParticipationHistRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.participation.ParticipationRepository;
import gov.cdc.dataprocessing.service.interfaces.paticipation.IParticipationService;
import jakarta.transaction.Transactional;
import org.hibernate.mapping.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
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
