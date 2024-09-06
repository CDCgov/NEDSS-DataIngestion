package gov.cdc.nbsDedup.utilities.component.entity;


import gov.cdc.nbsDedup.constant.elr.NEDSSConstant;
import gov.cdc.nbsDedup.model.dto.person.PersonDto;
import gov.cdc.nbsDedup.nbs.odse.model.entity.EntityODSE;
import gov.cdc.nbsDedup.nbs.odse.repos.entity.EntityRepository;
import org.springframework.stereotype.Component;

@Component
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
