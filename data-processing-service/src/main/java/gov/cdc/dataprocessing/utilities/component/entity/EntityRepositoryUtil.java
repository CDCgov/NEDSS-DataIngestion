package gov.cdc.dataprocessing.utilities.component.entity;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityODSE;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.entity.EntityRepository;
import gov.cdc.dataprocessing.service.model.decision_support.DsmLabMatchHelper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Slf4j
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
public class EntityRepositoryUtil {
    private final EntityRepository entityRepository;

    private static final Logger logger = LoggerFactory.getLogger(EntityRepositoryUtil.class); // NOSONAR

    public EntityRepositoryUtil(EntityRepository entityRepository) {
        this.entityRepository = entityRepository;
    }

    @SuppressWarnings("java:S3923")
    public EntityODSE preparingEntityReposCallForPerson(PersonDto personDto, Long entityId, Object entityValue, String event) throws DataProcessingException {
        EntityODSE entityODSE = null;
        if (entityValue.getClass().toString().equals("class java.lang.String")) {
            entityODSE = new EntityODSE();
            entityODSE.setEntityUid(entityId);
            entityODSE.setClassCd((String) entityValue);
            try {
                entityRepository.save(entityODSE);

            } catch (Exception e) {
                if (e instanceof DataIntegrityViolationException) {
                    logger.error(e.getMessage());
                }
                else {
                    throw new DataProcessingException("Error at preparingEntityReposCallForPerson {}", e);
                }
            }
        } else {
            if (entityValue.getClass().toString().equals("class java.sql.Timestamp")) {
                //TODO: To be implemented
                logger.info("preparingEntityReposCallForPerson Timestamp");
            }
            else {
                //TODO: To be implemented
                logger.info("preparingEntityReposCallForPerson Timestamp Else");

            }
        }

        if (event.equals(NEDSSConstant.SELECT)) {
                //TODO: To be implemented
            logger.info("preparingEntityReposCallForPerson SELECT");

        }
        else if (event.equals(NEDSSConstant.SELECT_COUNT)) {
                //TODO: To be implemented
            logger.info("preparingEntityReposCallForPerson SELECT COUNT");

        }
        else {
            return entityODSE;
        }

        return entityODSE;
    }
}
