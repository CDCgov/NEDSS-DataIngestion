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
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 1192 - duplicate literal
 135 - for loop
 117 - naming
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192", "java:S135", "java:S117"})
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
        return result.map(longs -> longs.get(0)).orElse(null);
    }

    public void saveParticipationHist(ParticipationDto participationDto) throws DataProcessingException {
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

    public void saveParticipation(ParticipationDto participationDto) throws DataProcessingException {
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

    public void saveParticipationHistBatch(List<ParticipationDto> dtos) throws DataProcessingException {
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
