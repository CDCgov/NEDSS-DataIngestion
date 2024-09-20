package gov.cdc.dataprocessing.repository.nbs.odse.repos.stored_proc;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.matching.EdxEntityMatchDto;
import gov.cdc.dataprocessing.model.dto.matching.EdxPatientMatchDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;

@Repository
public class EdxPatientMatchStoredProcRepository {
    @PersistenceContext(unitName = "odseEntityManagerFactory") // Specify the persistence unit name
    private EntityManager entityManager;

    @Transactional
    public EdxPatientMatchDto getEdxPatientMatch(String typeCd, String matchString) throws DataProcessingException {
        EdxPatientMatchDto edxPatientMatchDto = new EdxPatientMatchDto();

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
            edxPatientMatchDto.setPatientUid(patientUid);
            edxPatientMatchDto.setMatchStringHashCode(matchStringHashcode);
            edxPatientMatchDto.setTypeCd(typeCd);
            edxPatientMatchDto.setMatchString(matchString);
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage());
        }
        return edxPatientMatchDto;

    }


    @Transactional
    public EdxEntityMatchDto getEdxEntityMatch(String typeCd, String matchString) throws DataProcessingException {
        EdxEntityMatchDto edxEntityMatchDto = new EdxEntityMatchDto();

        try {

            StoredProcedureQuery storedProcedure = entityManager.createStoredProcedureQuery("GETEDXENTITYMATCH_SP");

            // Register the parameters
            storedProcedure.registerStoredProcedureParameter("in_type_cd", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("in_match_string", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("out_entity_uid", Long.class, ParameterMode.OUT);

            // Set the parameter values
            storedProcedure.setParameter("in_type_cd", typeCd);
            storedProcedure.setParameter("in_match_string", matchString);

            // Execute the stored procedure
            storedProcedure.execute();

            // Get the output parameters
            Long uid = (Long) storedProcedure.getOutputParameterValue("out_entity_uid");


            edxEntityMatchDto.setEntityUid(uid);
            edxEntityMatchDto.setTypeCd(typeCd);
            edxEntityMatchDto.setMatchString(matchString);
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage());
        }
        return edxEntityMatchDto;

    }
}
