package gov.cdc.dataprocessing.repository.nbs.odse.repos.stored_proc;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.matching.EdxEntityMatchDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class ProgAreaSnomeCodeStoredProcRepository {

    @PersistenceContext(unitName = "odseEntityManagerFactory") // Specify the persistence unit name
    private EntityManager entityManager;

    public Map<String, Object> getSnomed(String code, String type, String clia) throws DataProcessingException {
        EdxEntityMatchDto edxEntityMatchDto = new EdxEntityMatchDto();
        Map<String, Object> map = new HashMap<>();
        try {

            StoredProcedureQuery storedProcedure = entityManager.createStoredProcedureQuery("msgProgAreaLoincSnomed_sp");

            // Register the parameters
            storedProcedure.registerStoredProcedureParameter("code", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("type", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("clia", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("loinc_snomed", String.class, ParameterMode.OUT);
            storedProcedure.registerStoredProcedureParameter("count", Integer.class, ParameterMode.OUT);


            // Set the parameter values
            storedProcedure.setParameter("code", code);
            storedProcedure.setParameter("type", type);
            storedProcedure.setParameter("clia", clia);

            // Execute the stored procedure
            storedProcedure.execute();

            // Get the output parameters
            String loincScnome = (String) storedProcedure.getOutputParameterValue("loinc_snomed");
            Integer count = (Integer) storedProcedure.getOutputParameterValue("count");

            map.put("LOINC", loincScnome);
            map.put("COUNT", count);
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage());
        }
        return map;

    }


    public Map<String, Object> getProgAreaCd(String code, String type, String clia) throws DataProcessingException {
        EdxEntityMatchDto edxEntityMatchDto = new EdxEntityMatchDto();
        Map<String, Object> map = new HashMap<>();
        try {

            StoredProcedureQuery storedProcedure = entityManager.createStoredProcedureQuery("msgProgArea_sp");

            // Register the parameters
            storedProcedure.registerStoredProcedureParameter("code", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("type", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("clia", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("prog_area", String.class, ParameterMode.OUT);
            storedProcedure.registerStoredProcedureParameter("count", Integer.class, ParameterMode.OUT);


            // Set the parameter values
            storedProcedure.setParameter("code", code);
            storedProcedure.setParameter("type", type);
            storedProcedure.setParameter("clia", clia);

            // Execute the stored procedure
            storedProcedure.execute();

            // Get the output parameters
            String progArea = (String) storedProcedure.getOutputParameterValue("prog_area");
            Integer count = (Integer) storedProcedure.getOutputParameterValue("count");

            map.put("PROGRAM", progArea);
            map.put("COUNT", count);
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage());
        }
        return map;

    }
}
