package gov.cdc.dataprocessing.repository.nbs.odse.repos.stored_proc;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.repository.nbs.odse.model.generic_helper.PrepareEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 6541 - brain method complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541"})
public class PrepareEntityStoredProcRepository {
    @PersistenceContext(unitName = "odseEntityManagerFactory") // Specify the persistence unit name
    private EntityManager entityManager;

    public PrepareEntity getPrepareEntity(String businessTriggerCd, String moduleCd, Long uid, String tableName) throws DataProcessingException {
        PrepareEntity entity = new PrepareEntity();
        try {

            StoredProcedureQuery storedProcedure = entityManager.createStoredProcedureQuery("GetNextState_sp");


            // Register the parameters
            storedProcedure.registerStoredProcedureParameter("businessTriggerCode", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("moduleCd", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("objectUid", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("className", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("localId", String.class, ParameterMode.OUT);
            storedProcedure.registerStoredProcedureParameter("addUserId", String.class, ParameterMode.OUT);
            storedProcedure.registerStoredProcedureParameter("addUserTime", Timestamp.class, ParameterMode.OUT);
            storedProcedure.registerStoredProcedureParameter("recordStatusState", String.class, ParameterMode.OUT);
            storedProcedure.registerStoredProcedureParameter("objectStatusState", String.class, ParameterMode.OUT);

            // Set the parameter values
            storedProcedure.setParameter("businessTriggerCode", businessTriggerCd);
            storedProcedure.setParameter("moduleCd", moduleCd);
            storedProcedure.setParameter("objectUid", uid.toString());
            storedProcedure.setParameter("className", tableName);

            // Execute the stored procedure
            storedProcedure.execute();

            // Get the output parameters
            String localId = (String) storedProcedure.getOutputParameterValue("localId");
            String addUserId = (String) storedProcedure.getOutputParameterValue("addUserId");
            Timestamp addUserTime = (Timestamp) storedProcedure.getOutputParameterValue("addUserTime");
            String recordStatusState = (String) storedProcedure.getOutputParameterValue("recordStatusState");
            String objectStatusState = (String) storedProcedure.getOutputParameterValue("objectStatusState");
            entity.setLocalId(localId);

            if (addUserId != null) {
                entity.setAddUserId(Long.parseLong(addUserId));
            }
            entity.setAddUserTime(addUserTime);
            entity.setRecordStatusState(recordStatusState);
            entity.setObjectStatusState(objectStatusState);
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
        return entity;

    }

}