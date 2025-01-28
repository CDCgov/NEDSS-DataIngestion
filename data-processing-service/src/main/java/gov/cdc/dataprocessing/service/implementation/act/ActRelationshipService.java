package gov.cdc.dataprocessing.service.implementation.act;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActRelationship;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActRelationshipRepository;
import gov.cdc.dataprocessing.service.interfaces.act.IActRelationshipService;
import gov.cdc.dataprocessing.utilities.component.jdbc.DataModifierReposJdbc;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;

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
public class ActRelationshipService implements IActRelationshipService {
    private final ActRelationshipRepository actRelationshipRepository;
    private final DataModifierReposJdbc dataModifierReposJdbc;

    public ActRelationshipService(ActRelationshipRepository actRelationshipRepository, DataModifierReposJdbc dataModifierReposJdbc) {
        this.actRelationshipRepository = actRelationshipRepository;
        this.dataModifierReposJdbc = dataModifierReposJdbc;
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
            dataModifierReposJdbc.deleteActRelationshipByPk(actRelationshipDto.getTargetActUid(), actRelationshipDto.getSourceActUid(), actRelationshipDto.getTypeCd());
        }
    }
}
