package gov.cdc.dataprocessing.utilities.component.patient;

import gov.cdc.dataprocessing.model.classic_model.dto.EdxPatientMatchDT;
import gov.cdc.dataprocessing.repository.nbs.odse.EdxPatientMatchRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.model.EdxPatientMatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EdxPatientMatchRepositoryUtil {
    private static final Logger logger = LoggerFactory.getLogger(EdxPatientMatchRepositoryUtil.class);
    private final EdxPatientMatchRepository edxPatientMatchRepository;

    public EdxPatientMatchRepositoryUtil(EdxPatientMatchRepository edxPatientMatchRepository) {
        this.edxPatientMatchRepository = edxPatientMatchRepository;
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
