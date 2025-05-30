package gov.cdc.dataprocessing.utilities.component.act;

import gov.cdc.dataprocessing.model.dto.act.ActIdDto;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.ActIdJdbcRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActId;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

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
 1192 - duplicate literal
 135 - for loop
 117 - naming
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192", "java:S135", "java:S117"})
public class ActIdRepositoryUtil {
    private final ActIdJdbcRepository actIdJdbcRepository;

    public ActIdRepositoryUtil(
                               ActIdJdbcRepository actIdJdbcRepository) {
        this.actIdJdbcRepository = actIdJdbcRepository;
    }

    public Collection<ActIdDto> getActIdCollection(Long actUid) {
//        var actIds = actIdRepository.findRecordsById(actUid);
        var actIds = actIdJdbcRepository.findRecordsByActUid(actUid);
        Collection<ActIdDto> actIdCollection = new ArrayList<>();
        if (actIds != null && !actIds.isEmpty()) {
            for(var item : actIds) {
                var dto  = new ActIdDto(item);
                dto.setItNew(false);
                dto.setItDirty(false);
                actIdCollection.add(dto);
            }
        }
        return actIdCollection;
    }


    public void insertActIdCollection(Long uid, Collection<ActIdDto> actIdDtoCollection) {
        for(var item: actIdDtoCollection){
            ActId data = new ActId(item);
            data.setActUid(uid);
            actIdJdbcRepository.mergeActId(data);
//            actIdRepository.save(data);
            item.setItDirty(false);
            item.setItNew(false);
            item.setItDelete(false);
        }
    }


}
