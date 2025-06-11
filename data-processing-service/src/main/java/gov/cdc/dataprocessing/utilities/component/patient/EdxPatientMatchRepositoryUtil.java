package gov.cdc.dataprocessing.utilities.component.patient;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.matching.EdxEntityMatchDto;
import gov.cdc.dataprocessing.model.dto.matching.EdxPatientMatchDto;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.EdxMatchJdbcRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.model.matching.EdxEntityMatch;
import gov.cdc.dataprocessing.repository.nbs.odse.model.matching.EdxPatientMatch;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.stored_proc.EdxPatientMatchStoredProcRepository;
import gov.cdc.dataprocessing.utilities.component.jdbc.DataModifierReposJdbc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component

public class EdxPatientMatchRepositoryUtil {
    private static final Logger logger = LoggerFactory.getLogger(EdxPatientMatchRepositoryUtil.class);
    private final EdxMatchJdbcRepository edxMatchJdbcRepository;
    private final EdxPatientMatchStoredProcRepository edxPatientMatchStoreProcRepository;
    private final DataModifierReposJdbc dataModifierReposJdbc;

    public EdxPatientMatchRepositoryUtil(EdxMatchJdbcRepository edxMatchJdbcRepository,
                                         EdxPatientMatchStoredProcRepository edxPatientMatchStoreProcRepository,
                                         DataModifierReposJdbc dataModifierReposJdbc) {
        this.edxMatchJdbcRepository = edxMatchJdbcRepository;
        this.edxPatientMatchStoreProcRepository = edxPatientMatchStoreProcRepository;
        this.dataModifierReposJdbc = dataModifierReposJdbc;
    }

    public EdxPatientMatchDto getEdxPatientMatchOnMatchString(String typeCd, String matchString) throws DataProcessingException {
        if (typeCd == null || matchString == null) {
            return new EdxPatientMatchDto();
        }
        return edxPatientMatchStoreProcRepository.getEdxPatientMatch(typeCd, matchString);

    }

    public EdxEntityMatchDto getEdxEntityMatchOnMatchString(String typeCd, String matchString) throws DataProcessingException {
        if (typeCd == null || matchString == null) {
            return new EdxEntityMatchDto();
        }
        return edxPatientMatchStoreProcRepository.getEdxEntityMatch(typeCd, matchString);
    }


    public void saveEdxEntityMatch(EdxEntityMatchDto edxEntityMatchDto) {
        EdxEntityMatch model = new EdxEntityMatch(edxEntityMatchDto);
        edxMatchJdbcRepository.mergeEdxEntityMatch(model);
    }


    public EdxPatientMatch setEdxPatientMatchDT(EdxPatientMatchDto edxPatientMatchDto) {
        EdxPatientMatch edxPatientMatch = new EdxPatientMatch(edxPatientMatchDto);
        edxMatchJdbcRepository.mergeEdxPatientMatch(edxPatientMatch);
        return edxPatientMatch;
    }

    public void deleteEdxPatientMatchDTColl(Long patientUid) {
        dataModifierReposJdbc.deleteByPatientUidAndMatchStringNotLike(patientUid);
    }
}
