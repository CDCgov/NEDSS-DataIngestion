package gov.cdc.dataprocessing.service.implementation.participation;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.participation.Participation;
import gov.cdc.dataprocessing.repository.nbs.odse.model.participation.ParticipationHist;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.participation.ParticipationHistRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.participation.ParticipationRepository;
import gov.cdc.dataprocessing.service.interfaces.paticipation.IParticipationService;
import gov.cdc.dataprocessing.utilities.component.jdbc.DataModifierReposJdbc;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service

public class ParticipationService implements IParticipationService {
    private final ParticipationRepository participationRepository;
    private final ParticipationHistRepository participationHistRepository;
    private final DataModifierReposJdbc dataModifierReposJdbc;


    public ParticipationService(ParticipationRepository participationRepository,
                                ParticipationHistRepository participationHistRepository, DataModifierReposJdbc dataModifierReposJdbc
    ) {
        this.participationRepository = participationRepository;
        this.participationHistRepository = participationHistRepository;
        this.dataModifierReposJdbc = dataModifierReposJdbc;
    }

    public Long findPatientMprUidByObservationUid(String classCode, String typeCode, Long actUid) {
        var result = participationRepository.findPatientMprUidByObservationUid(classCode, typeCode, actUid);
        return result.map(List::getFirst).orElse(null);
    }

    public void saveParticipationHist(ParticipationDto participationDto)  {
        var res = participationHistRepository.findVerNumberByKey(participationDto.getSubjectEntityUid(), participationDto.getActUid(), participationDto.getTypeCd());
        Integer ver = 1;
        if (res.isPresent() && !res.get().isEmpty()) {
            ver = Collections.max(res.get());
        }

        var patHist = new ParticipationHist(participationDto);
        patHist.setVersionCtrlNbr(ver);
        participationHistRepository.save(patHist);
        participationDto.setItNew(false);

    }

    public void saveParticipation(ParticipationDto participationDto)   {
        if (participationDto.isItNew() || participationDto.isItDirty()) {
            persistingParticipation(participationDto);
        } else if (participationDto.isItDelete()) {
            deleteParticipationByPk(participationDto.getSubjectEntityUid(), participationDto.getActUid(), participationDto.getActClassCd());
        }
    }
    private void persistingParticipation(ParticipationDto participationDto)  {
        if (participationDto.getSubjectEntityUid() != null && participationDto.getActUid() != null) {
            var data = new Participation(participationDto);
            participationRepository.save(data);
        }
    }

    public void saveParticipationByBatch(List<ParticipationDto> toSave) {
        if (toSave == null || toSave.isEmpty()) return;

        List<Participation> entities = toSave.stream()
                .filter(dto -> dto.getSubjectEntityUid() != null && dto.getActUid() != null)
                .map(Participation::new)
                .collect(Collectors.toList());

        participationRepository.saveAll(entities);
    }

    public void saveParticipationHistBatch(List<ParticipationDto> dtos)   {
        if (dtos == null || dtos.isEmpty()) return;

        List<ParticipationHist> toPersist = new ArrayList<>();

        for (ParticipationDto dto : dtos) {
            Integer ver = 1;

            Optional<List<Integer>> res = participationHistRepository.findVerNumberByKey(
                    dto.getSubjectEntityUid(),
                    dto.getActUid(),
                    dto.getTypeCd()
            );

            if (res.isPresent() && !res.get().isEmpty()) {
                ver = Collections.max(res.get());
            }

            ParticipationHist hist = new ParticipationHist(dto);
            hist.setVersionCtrlNbr(ver);
            toPersist.add(hist);

            dto.setItNew(false);
        }

        participationHistRepository.saveAll(toPersist);
    }

    private void deleteParticipationByPk(Long subjectId, Long actId, String classCode)  {
        dataModifierReposJdbc.deleteParticipationByPk(subjectId, actId, classCode);
    }

}
