package gov.cdc.dataprocessing.repository.nbs.msgoute.repos.stored_proc;

import gov.cdc.dataprocessing.exception.DataProcessingException;
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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809"})
public class NbsInterfaceStoredProcRepository {
    @PersistenceContext(unitName = "nbsEntityManagerFactory") // Specify the persistence unit name
    private EntityManager entityManager;

    public void updateSpecimenCollDateSP(Long nbsInterfaceUid, Timestamp specimentCollectionDate) throws DataProcessingException {
        try {

            StoredProcedureQuery storedProcedure = entityManager.createStoredProcedureQuery("UpdateSpecimenCollDate_SP");

            // Register the parameters
            storedProcedure.registerStoredProcedureParameter("NBSInterfaceUid", Integer.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("specimentCollectionDate", Timestamp.class, ParameterMode.IN);


            // Set the parameter values
            storedProcedure.setParameter("NBSInterfaceUid", nbsInterfaceUid);
            storedProcedure.setParameter("specimentCollectionDate", specimentCollectionDate);

            // Execute the stored procedure
            storedProcedure.execute();


        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }

    }
}
