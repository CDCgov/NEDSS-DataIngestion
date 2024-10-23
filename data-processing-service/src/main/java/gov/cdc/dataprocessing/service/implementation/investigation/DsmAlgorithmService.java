package gov.cdc.dataprocessing.service.implementation.investigation;

import gov.cdc.dataprocessing.repository.nbs.odse.model.dsm.DsmAlgorithm;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.dsm.DsmAlgorithmRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 6809 - TEST
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S6809"})
public class DsmAlgorithmService {
    private final DsmAlgorithmRepository dsmAlgorithmRepository;

    public DsmAlgorithmService(DsmAlgorithmRepository dsmAlgorithmRepository) {
        this.dsmAlgorithmRepository = dsmAlgorithmRepository;
    }

    Collection<DsmAlgorithm> findActiveDsmAlgorithm() {
        Collection<DsmAlgorithm> col  = new ArrayList<>();
        var results = dsmAlgorithmRepository.findDsmAlgorithmByStatusCode("A");
        if (results.isPresent()) {
            col = results.get();
        }
        return col;
    }
}
