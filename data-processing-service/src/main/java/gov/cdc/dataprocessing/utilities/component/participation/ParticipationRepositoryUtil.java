package gov.cdc.dataprocessing.utilities.component.participation;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.participation.Participation;
import gov.cdc.dataprocessing.repository.nbs.odse.model.participation.ParticipationHist;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.participation.ParticipationHistRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.participation.ParticipationRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@Component
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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201"})
public class ParticipationRepositoryUtil {
    private final ParticipationRepository participationRepository;
    private final ParticipationHistRepository participationHistRepository;

    public ParticipationRepositoryUtil(ParticipationRepository participationRepository,
                                       ParticipationHistRepository participationHistRepository) {
        this.participationRepository = participationRepository;
        this.participationHistRepository = participationHistRepository;
    }

    public Collection<ParticipationDto> getParticipationCollection(Long actUid) {
        var res = participationRepository.findByActUid(actUid);
        Collection<ParticipationDto> dtoCollection = new ArrayList<>();
        if (res.isPresent()) {
            for(var item : res.get()) {
                var dto  = new ParticipationDto(item);
                dto.setItNew(false);
                dto.setItDirty(false);
                dtoCollection.add(dto);
            }
        }
        return dtoCollection;
    }

    public void insertParticipationHist(ParticipationDto participationDto) {
        ParticipationHist hist = new ParticipationHist(participationDto);
        if (hist.getVersionCtrlNbr() != null) {
            var ver = hist.getVersionCtrlNbr();
            hist.setVersionCtrlNbr(++ver);
        } else {
            hist.setVersionCtrlNbr(1);
        }
        participationHistRepository.save(hist);
        participationDto.setItNew(false);
    }

    public void storeParticipation(ParticipationDto dt) throws DataProcessingException {
        try{

            if (dt == null)
                throw new DataProcessingException("Error: try to store null ParticipationDT object.");

            Participation data = new Participation(dt);
            if (dt.isItNew())
                participationRepository.save(data);
            else if (dt.isItDelete())
                participationRepository.delete(data);
            else if (dt.isItDirty())
                participationRepository.save(data);
        }catch(Exception ex){
            throw new DataProcessingException(ex.getMessage(), ex);
        }
    }

    public ParticipationDto getParticipation(Long subjectEntityUid, Long actUid) {
        var items = getParticipations(subjectEntityUid);
        for(var item : items) {
            if (Objects.equals(item.getActUid(), actUid)) {
                return item;
            }
        }
        return  null;
    }

    public Collection<ParticipationDto> getParticipations(Long subjectEntityUid) {
        Collection<ParticipationDto> col = new ArrayList<>();
        var res = participationRepository.findByParentUid(subjectEntityUid);
        if (res.isPresent()) {
            for(var item : res.get()) {
                var pat = new ParticipationDto(item);
                col.add(pat);
            }
        }
        return col;
    }

    public Collection<ParticipationDto> getParticipationsByActUid(Long actUid) {
        Collection<ParticipationDto> col = new ArrayList<>();
        var res = participationRepository.findByActUid(actUid);
        if (res.isPresent()) {
            for(var item : res.get()) {
                var pat = new ParticipationDto(item);
                col.add(pat);
            }
        }
        return col;
    }
}
