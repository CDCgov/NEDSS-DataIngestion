package gov.cdc.dataprocessing.repository.nbs.msgoute;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.matching.EdxEntityMatchDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Repository

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
            throw new DataProcessingException(e.getMessage());
        }

    }
}
