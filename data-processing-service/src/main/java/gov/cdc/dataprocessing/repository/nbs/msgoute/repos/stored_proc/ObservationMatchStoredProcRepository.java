package gov.cdc.dataprocessing.repository.nbs.msgoute.repos.stored_proc;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.ObservationContainer;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 1192 - duplicate literal
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192"})
public class ObservationMatchStoredProcRepository {
    private static final Logger logger = LoggerFactory.getLogger(ObservationMatchStoredProcRepository.class); // NOSONAR

    @PersistenceContext(unitName = "nbsEntityManagerFactory") // Specify the persistence unit name
    private EntityManager entityManager;

    @Transactional
    public Long getMatchedObservation(EdxLabInformationDto edxLabInformationDto) throws DataProcessingException {
        ObservationContainer observationContainer = edxLabInformationDto.getRootObservationContainer();
        String clia = edxLabInformationDto.getSendingFacilityClia();
        String fillerNumber = edxLabInformationDto.getFillerNumber();
        Timestamp specimenCollectionDate = observationContainer.getTheObservationDto().getEffectiveFromTime();
        String orderedTestCode= observationContainer.getTheObservationDto().getCd();
        if (checkInvalidFillerSpecimenAndOrderedTest(fillerNumber, specimenCollectionDate, orderedTestCode, clia))
        {
            return null; // no match
        }

        // try to find a match
        Long matchedUID = null;

        try {

            //Number of years, indicate the years to go back to search for existing observation
            String numberOfYears = "2";
            int numberOfNumberInt = Integer.parseInt(numberOfYears);

            StoredProcedureQuery storedProcedure = entityManager.createStoredProcedureQuery("GetObservationMatch_SP");

            // Register the parameters
            storedProcedure.registerStoredProcedureParameter("fillerNbr", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("labCLIA", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("orderedTestCd", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("specimenCollectionDate", Timestamp.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("numberOfGoBackYears", Integer.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("Observation_uid", Long.class, ParameterMode.OUT);

            // Set the parameter values
            storedProcedure.setParameter("fillerNbr", fillerNumber);
            storedProcedure.setParameter("labCLIA", clia);
            storedProcedure.setParameter("orderedTestCd", orderedTestCode);
            storedProcedure.setParameter("specimenCollectionDate", specimenCollectionDate);
            storedProcedure.setParameter("numberOfGoBackYears", numberOfNumberInt);

            // Execute the stored procedure
            storedProcedure.execute();

            // Get the output parameters
            Long observationUid = (Long) storedProcedure.getOutputParameterValue("Observation_uid");


            if (observationUid != null && observationUid > 0) {
                matchedUID = observationUid;
            }

        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
        return matchedUID;

    }

    protected  boolean checkInvalidFillerSpecimenAndOrderedTest(
            String fillerNumber, Timestamp specimenCollectionDate, String orderedTestCode,
            String clia
    ) {
       return fillerNumber == null || specimenCollectionDate==null || orderedTestCode==null || clia==null;
    }

}
