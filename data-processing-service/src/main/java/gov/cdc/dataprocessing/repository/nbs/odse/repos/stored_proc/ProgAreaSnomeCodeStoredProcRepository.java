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

import static gov.cdc.dataprocessing.constant.elr.NEDSSConstant.COUNT_LOWERCASE;
import static gov.cdc.dataprocessing.constant.elr.NEDSSConstant.SELECT_COUNT;

@Repository
public class ProgAreaSnomeCodeStoredProcRepository {

    @PersistenceContext(unitName = "odseEntityManagerFactory") // Specify the persistence unit name
    private EntityManager entityManager;

    public Map<String, Object> getSnomed(String code, String type, String clia) throws DataProcessingException {
        Map<String, Object> map = new HashMap<>();
        try {

            StoredProcedureQuery storedProcedure = entityManager.createStoredProcedureQuery("msgProgAreaLoincSnomed_sp");

            // Register the parameters
            storedProcedure.registerStoredProcedureParameter("code", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("type", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("clia", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("loinc_snomed", String.class, ParameterMode.OUT);
            storedProcedure.registerStoredProcedureParameter(COUNT_LOWERCASE, Integer.class, ParameterMode.OUT);


            // Set the parameter values
            storedProcedure.setParameter("code", code);
            storedProcedure.setParameter("type", type);
            storedProcedure.setParameter("clia", clia);

            // Execute the stored procedure
            storedProcedure.execute();

            // Get the output parameters
            String loincScnome = (String) storedProcedure.getOutputParameterValue("loinc_snomed");
            Integer count = (Integer) storedProcedure.getOutputParameterValue(COUNT_LOWERCASE);

            map.put("LOINC", loincScnome);
            map.put(SELECT_COUNT, count);
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
        return map;

    }


    public Map<String, Object> getProgAreaCd(String code, String type, String clia) throws DataProcessingException {
        Map<String, Object> map = new HashMap<>();
        try {

            StoredProcedureQuery storedProcedure = entityManager.createStoredProcedureQuery("msgProgArea_sp");

            // Register the parameters
            storedProcedure.registerStoredProcedureParameter("code", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("type", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("clia", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("prog_area", String.class, ParameterMode.OUT);
            storedProcedure.registerStoredProcedureParameter(COUNT_LOWERCASE, Integer.class, ParameterMode.OUT);


            // Set the parameter values
            storedProcedure.setParameter("code", code);
            storedProcedure.setParameter("type", type);
            storedProcedure.setParameter("clia", clia);

            // Execute the stored procedure
            storedProcedure.execute();

            // Get the output parameters
            String progArea = (String) storedProcedure.getOutputParameterValue("prog_area");
            Integer count = (Integer) storedProcedure.getOutputParameterValue(COUNT_LOWERCASE);

            map.put("PROGRAM", progArea);
            map.put(SELECT_COUNT, count);
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
        return map;

    }
}
