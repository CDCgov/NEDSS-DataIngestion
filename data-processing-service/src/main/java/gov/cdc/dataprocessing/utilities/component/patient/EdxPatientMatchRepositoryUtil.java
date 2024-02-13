package gov.cdc.dataprocessing.utilities.component.patient;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model.dto.EdxPatientMatchDT;
import gov.cdc.dataprocessing.repository.nbs.odse.EdxPatientMatchRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.EdxPatientMatchStoredProcRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.model.EdxPatientMatch;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EdxPatientMatchRepositoryUtil {
    private static final Logger logger = LoggerFactory.getLogger(EdxPatientMatchRepositoryUtil.class);
    private final EdxPatientMatchRepository edxPatientMatchRepository;
    private final EdxPatientMatchStoredProcRepository edxPatientMatchStoreProcRepository;


    public EdxPatientMatchRepositoryUtil(EdxPatientMatchRepository edxPatientMatchRepository,
                                         EdxPatientMatchStoredProcRepository edxPatientMatchStoreProcRepository) {
        this.edxPatientMatchRepository = edxPatientMatchRepository;
        this.edxPatientMatchStoreProcRepository = edxPatientMatchStoreProcRepository;
    }

    @Transactional
    public EdxPatientMatchDT getEdxPatientMatchOnMatchString(String typeCd, String matchString) throws DataProcessingException {
        if (typeCd == null || matchString == null) {
            return new EdxPatientMatchDT();
        }
        try {
            return edxPatientMatchStoreProcRepository.getEdxPatientMatch(typeCd, matchString);

        } catch (Exception ex) {
            logger.error("Exception in EdxPatientMatchDAO.getEdxPatientMatchOnMatchString for typeCd=" + typeCd + " match string=" + matchString + ": ERROR = " + ex);
            throw new DataProcessingException(ex.toString(), ex);
        }
    }

    public EdxPatientMatch setEdxPatientMatchDT(EdxPatientMatchDT edxPatientMatchDT) {
        EdxPatientMatch edxPatientMatch = new EdxPatientMatch(edxPatientMatchDT);
        edxPatientMatchRepository.save(edxPatientMatch);
        return edxPatientMatch;
    }

    public void deleteEdxPatientMatchDTColl(Long patientUid) {
        edxPatientMatchRepository.deleteByPatientUidAndMatchStringNotLike(patientUid);
    }
}
