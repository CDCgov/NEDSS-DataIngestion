package gov.cdc.dataprocessing.utilities.component.entity;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityODSE;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.entity.EntityRepository;
import org.springframework.stereotype.Component;

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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740"})
public class EntityRepositoryUtil {
    private final EntityRepository entityRepository;


    public EntityRepositoryUtil(EntityRepository entityRepository) {
        this.entityRepository = entityRepository;
    }

    @SuppressWarnings("java:S3923")
    public EntityODSE preparingEntityReposCallForPerson(PersonDto personDto, Long entityId, Object entityValue, String event) {
        EntityODSE entityODSE = null;
        if (entityValue.getClass().toString().equals("class java.lang.String")) {
            entityODSE = new EntityODSE();
            entityODSE.setEntityUid(entityId);
            entityODSE.setClassCd((String) entityValue);
            entityRepository.save(entityODSE);
        } else {
            if (entityValue.getClass().toString().equals("class java.sql.Timestamp")) {
                //TODO: Will get back to this
            }
            else {

            }
        }

        if (event.equals(NEDSSConstant.SELECT)) {

        }
        else if (event.equals(NEDSSConstant.SELECT_COUNT)) {

        }
        else {
            return entityODSE;
        }

        return entityODSE;
    }
}
