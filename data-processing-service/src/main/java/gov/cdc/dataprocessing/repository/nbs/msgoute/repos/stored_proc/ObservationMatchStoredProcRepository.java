package gov.cdc.dataprocessing.repository.nbs.msgoute.repos.stored_proc;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.ObservationContainer;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public class ObservationMatchStoredProcRepository {
    private static final Logger logger = LoggerFactory.getLogger(ObservationMatchStoredProcRepository.class);

    @PersistenceContext(unitName = "nbsEntityManagerFactory") // Specify the persistence unit name
    private EntityManager entityManager;

    @Transactional
    public Long getMatchedObservation(EdxLabInformationDto edxLabInformationDto) throws DataProcessingException {
        ObservationContainer observationContainer = edxLabInformationDto.getRootObservationContainer();
        String clia = edxLabInformationDto.getSendingFacilityClia();
        String fillerNumber = edxLabInformationDto.getFillerNumber();
        Timestamp specimenCollectionDate = observationContainer.getTheObservationDto().getEffectiveFromTime();
        String orderedTestCode= observationContainer.getTheObservationDto().getCd();
        if (fillerNumber == null || specimenCollectionDate==null || orderedTestCode==null || clia==null )
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
            throw new DataProcessingException(e.getMessage());
        }
        return matchedUID;

    }

}
