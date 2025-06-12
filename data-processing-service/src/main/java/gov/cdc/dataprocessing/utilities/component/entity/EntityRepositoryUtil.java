package gov.cdc.dataprocessing.utilities.component.entity;

import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.EntityJdbcRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityODSE;
import org.springframework.stereotype.Component;

@Component

public class EntityRepositoryUtil {
    private final EntityJdbcRepository entityJdbcRepository;


    public EntityRepositoryUtil(EntityJdbcRepository entityJdbcRepository) {
        this.entityJdbcRepository = entityJdbcRepository;
    }

    @SuppressWarnings("java:S3923")
    public EntityODSE preparingEntityReposCallForPerson(PersonDto personDto, Long entityId, Object entityValue, String event) {
        if (!(entityValue instanceof String stringValue)) {
            return null; // or handle unsupported types later
        }

        EntityODSE entityODSE = new EntityODSE();
        entityODSE.setEntityUid(entityId);
        entityODSE.setClassCd(stringValue);

        entityJdbcRepository.createEntity(entityODSE); // Consider batching if called repeatedly

        return entityODSE;

    }
}
