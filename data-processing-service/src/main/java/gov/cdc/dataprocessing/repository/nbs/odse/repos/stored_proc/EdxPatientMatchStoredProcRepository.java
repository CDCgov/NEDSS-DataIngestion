package gov.cdc.dataprocessing.repository.nbs.odse.repos.stored_proc;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.matching.EdxEntityMatchDto;
import gov.cdc.dataprocessing.model.dto.matching.EdxPatientMatchDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
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
            throw new DataProcessingException(e.getMessage(), e);
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
            throw new DataProcessingException(e.getMessage(), e);
        }
        return edxEntityMatchDto;

    }
}
