package gov.cdc.dataprocessing.service.implementation.act;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActRelationship;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActRelationshipRepository;
import gov.cdc.dataprocessing.service.interfaces.act.IActRelationshipService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class ActRelationshipService implements IActRelationshipService {
    private final ActRelationshipRepository actRelationshipRepository;

    public ActRelationshipService(ActRelationshipRepository actRelationshipRepository) {
        this.actRelationshipRepository = actRelationshipRepository;
    }

    public Collection<ActRelationshipDto> loadActRelationshipBySrcIdAndTypeCode(Long uid, String type) {
        Collection<ActRelationshipDto> actRelationshipDtoCollection = new ArrayList<>();
        var result = actRelationshipRepository.loadActRelationshipBySrcIdAndTypeCode(uid, type);
        if (result.isPresent()) {
            for(var item : result.get()) {
                var elem = new ActRelationshipDto(item);
                actRelationshipDtoCollection.add(elem);
            }
        }

        return actRelationshipDtoCollection;
    }

    @Transactional
    public void saveActRelationship(ActRelationshipDto actRelationshipDto) throws DataProcessingException {
        if (actRelationshipDto == null) {
            throw new DataProcessingException("Act Relationship is null");
        }

        if (actRelationshipDto.isItNew() || actRelationshipDto.isItDirty()) {
            var data = new ActRelationship(actRelationshipDto);
            if (actRelationshipDto.isItNew() || (actRelationshipDto.isItDirty() && actRelationshipDto.getTargetActUid() != null && actRelationshipDto.getSourceActUid() != null && actRelationshipDto.getTypeCd() != null)) {
                actRelationshipRepository.save(data);
            }
        }
        else if (actRelationshipDto.isItDelete()) {
            actRelationshipRepository.deleteActRelationshipByPk(actRelationshipDto.getTargetActUid(), actRelationshipDto.getSourceActUid(), actRelationshipDto.getTypeCd());
        }
    }
}
