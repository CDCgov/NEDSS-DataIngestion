package gov.cdc.dataprocessing.utilities.component.patient;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model.dto.EdxEntityMatchDT;
import gov.cdc.dataprocessing.model.classic_model.dto.EdxPatientMatchDT;
import gov.cdc.dataprocessing.repository.nbs.odse.EdxEntityMatchRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.EdxPatientMatchRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.EdxPatientMatchStoredProcRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.model.matching.EdxEntityMatch;
import gov.cdc.dataprocessing.repository.nbs.odse.model.matching.EdxPatientMatch;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EdxPatientMatchRepositoryUtil {
    private static final Logger logger = LoggerFactory.getLogger(EdxPatientMatchRepositoryUtil.class);
    private final EdxPatientMatchRepository edxPatientMatchRepository;
    private final EdxEntityMatchRepository edxEntityMatchRepository;
    private final EdxPatientMatchStoredProcRepository edxPatientMatchStoreProcRepository;


    public EdxPatientMatchRepositoryUtil(EdxPatientMatchRepository edxPatientMatchRepository,
                                         EdxEntityMatchRepository edxEntityMatchRepository,
                                         EdxPatientMatchStoredProcRepository edxPatientMatchStoreProcRepository) {
        this.edxPatientMatchRepository = edxPatientMatchRepository;
        this.edxEntityMatchRepository = edxEntityMatchRepository;
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

    @Transactional
    public EdxEntityMatchDT getEdxEntityMatchOnMatchString(String typeCd, String matchString) throws DataProcessingException {
        if (typeCd == null || matchString == null) {
            return new EdxEntityMatchDT();
        }
        try {
            return edxPatientMatchStoreProcRepository.getEdxEntityMatch(typeCd, matchString);

        } catch (Exception ex) {
            logger.error("Exception in EdxPatientMatchDAO.getEdxPatientMatchOnMatchString for typeCd=" + typeCd + " match string=" + matchString + ": ERROR = " + ex);
            throw new DataProcessingException(ex.toString(), ex);
        }
    }


    @Transactional
    public void saveEdxEntityMatch(EdxEntityMatchDT edxEntityMatchDT) {
        EdxEntityMatch model = new EdxEntityMatch(edxEntityMatchDT);
        edxEntityMatchRepository.save(model);
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
