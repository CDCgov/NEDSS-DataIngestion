package gov.cdc.dataprocessing.utilities.component.patient;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model.dto.EdxPatientMatchDT;
import gov.cdc.dataprocessing.repository.nbs.odse.EdxPatientMatchStoredProcRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EdxPatientMatchingHelper {
    private static final Logger logger = LoggerFactory.getLogger(EdxPatientMatchingHelper.class);
    private final EdxPatientMatchStoredProcRepository edxPatientMatchRepository;

    public EdxPatientMatchingHelper(EdxPatientMatchStoredProcRepository edxPatientMatchRepository) {
        this.edxPatientMatchRepository = edxPatientMatchRepository;
    }

    public EdxPatientMatchDT getEdxPatientMatchOnMatchString(String typeCd, String matchString) throws DataProcessingException {
        if (typeCd == null || matchString == null) {
            return new EdxPatientMatchDT();
        }
        try {
            return edxPatientMatchRepository.getEdxPatientMatch(typeCd, matchString);

        } catch (Exception ex) {
            logger.error("Exception in EdxPatientMatchDAO.getEdxPatientMatchOnMatchString for typeCd=" + typeCd + " match string=" + matchString + ": ERROR = " + ex);
            throw new DataProcessingException(ex.toString(), ex);
        }
    }
}
