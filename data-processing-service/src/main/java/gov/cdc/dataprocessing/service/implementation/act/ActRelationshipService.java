package gov.cdc.dataprocessing.service.implementation.act;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.ActRelationshipJdbcRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActRelationship;
import gov.cdc.dataprocessing.service.interfaces.act.IActRelationshipService;
import gov.cdc.dataprocessing.utilities.component.jdbc.DataModifierReposJdbc;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service

public class ActRelationshipService implements IActRelationshipService {

    private final ActRelationshipJdbcRepository actRelationshipJdbcRepository;
    private final DataModifierReposJdbc dataModifierReposJdbc;

    public ActRelationshipService(
                                  ActRelationshipJdbcRepository actRelationshipJdbcRepository,
                                  DataModifierReposJdbc dataModifierReposJdbc) {
        this.actRelationshipJdbcRepository = actRelationshipJdbcRepository;
        this.dataModifierReposJdbc = dataModifierReposJdbc;
    }

    public Collection<ActRelationshipDto> loadActRelationshipBySrcIdAndTypeCode(Long uid, String type) {
        Collection<ActRelationshipDto> actRelationshipDtoCollection = new ArrayList<>();
        var result = actRelationshipJdbcRepository.findBySourceActUidAndTypeCode(uid, type);
        for(var item : result) {
            var elem = new ActRelationshipDto(item);
            actRelationshipDtoCollection.add(elem);
        }
        return actRelationshipDtoCollection;
    }

    public void saveActRelationship(ActRelationshipDto actRelationshipDto) throws DataProcessingException {
        if (actRelationshipDto == null) {
            throw new DataProcessingException("Act Relationship is null");
        }

        if (actRelationshipDto.isItNew() || actRelationshipDto.isItDirty()) {
            var data = new ActRelationship(actRelationshipDto);
            if (actRelationshipDto.isItNew() || (actRelationshipDto.isItDirty() && actRelationshipDto.getTargetActUid() != null && actRelationshipDto.getSourceActUid() != null && actRelationshipDto.getTypeCd() != null)) {
                actRelationshipJdbcRepository.mergeActRelationship(data);
            }
        }
        else if (actRelationshipDto.isItDelete()) {
            dataModifierReposJdbc.deleteActRelationshipByPk(actRelationshipDto.getTargetActUid(), actRelationshipDto.getSourceActUid(), actRelationshipDto.getTypeCd());
        }
    }
}
