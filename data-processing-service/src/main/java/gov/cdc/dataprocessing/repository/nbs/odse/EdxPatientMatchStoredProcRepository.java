package gov.cdc.dataprocessing.repository.nbs.odse;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model.dto.EdxPatientMatchDT;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Repository;

@Repository
public class EdxPatientMatchStoredProcRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public EdxPatientMatchDT getEdxPatientMatch(String typeCd, String matchString) throws DataProcessingException {
        EdxPatientMatchDT edxPatientMatchDT = new EdxPatientMatchDT();

        try {

            StoredProcedureQuery storedProcedure = entityManager.createStoredProcedureQuery("GetEdxPatientMatch_SP");

            // Register the parameters
            storedProcedure.registerStoredProcedureParameter("type_cd", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("match_string", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("Patient_uid", Long.class, ParameterMode.OUT);
            storedProcedure.registerStoredProcedureParameter("match_string_hashcode", Long.class, ParameterMode.OUT);

            // Set the parameter values
            storedProcedure.setParameter("type_cd", typeCd);
            storedProcedure.setParameter("match_string", matchString);

            // Execute the stored procedure
            storedProcedure.execute();

            // Get the output parameters
            Long patientUid = (Long) storedProcedure.getOutputParameterValue("Patient_uid");
            Long matchStringHashcode = (Long) storedProcedure.getOutputParameterValue("match_string_hashcode");

            // Do something with the output parameters
            System.out.println("Patient UID: " + patientUid);
            System.out.println("Match String Hashcode: " + matchStringHashcode);

            edxPatientMatchDT.setPatientUid(patientUid);
            edxPatientMatchDT.setMatchStringHashCode(matchStringHashcode);
            edxPatientMatchDT.setTypeCd(typeCd);
            edxPatientMatchDT.setMatchString(matchString);
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage());
        }
        return edxPatientMatchDT;

    }
}
