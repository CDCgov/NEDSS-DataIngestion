package gov.cdc.dataprocessing.service.implementation;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ParticipationDT;
import gov.cdc.dataprocessing.repository.nbs.odse.model.participation.Participation;
import gov.cdc.dataprocessing.repository.nbs.odse.model.participation.ParticipationHist;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.participation.ParticipationHistRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.participation.ParticipationRepository;
import gov.cdc.dataprocessing.service.interfaces.IParticipationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ParticipationService implements IParticipationService {
    private static final Logger logger = LoggerFactory.getLogger(ParticipationService.class);
    private final ParticipationRepository participationRepository;
    private final ParticipationHistRepository participationHistRepository;

    public ParticipationService(ParticipationRepository participationRepository,
                                ParticipationHistRepository participationHistRepository) {
        this.participationRepository = participationRepository;
        this.participationHistRepository = participationHistRepository;
    }

    public Long findPatientMprUidByObservationUid(String classCode, String typeCode, Long actUid) {
        var result = participationRepository.findPatientMprUidByObservationUid(classCode, typeCode, actUid);
        return result.map(longs -> longs.get(0)).orElse(null);
    }

    public void saveParticipationHist(ParticipationDT participationDT) {
        var patHist = new ParticipationHist(participationDT);
        participationHistRepository.save(patHist);
        participationDT.setItNew(false);
    }

    public void saveParticipation(ParticipationDT participationDT) {
        if (participationDT.isItNew() || participationDT.isItDirty()) {
            persistingParticipation(participationDT);
        } else if (participationDT.isItDelete()) {
            deleteParticipationByPk(participationDT.getSubjectEntityUid(), participationDT.getActUid(), participationDT.getActClassCd());
        }
    }
    private void persistingParticipation(ParticipationDT participationDT) {
        if (participationDT.getSubjectEntityUid() != null && participationDT.getActUid() != null) {
            var data = new Participation(participationDT);
            participationRepository.save(data);
        }
    }

    private void deleteParticipationByPk(Long subjectId, Long actId, String classCode) {
        participationRepository.deleteParticipationByPk(subjectId, actId, classCode);
    }

}
