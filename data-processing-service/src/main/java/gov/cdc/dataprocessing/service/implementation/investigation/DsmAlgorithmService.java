package gov.cdc.dataprocessing.service.implementation.investigation;

import gov.cdc.dataprocessing.repository.nbs.odse.model.dsm.DsmAlgorithm;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.dsm.DsmAlgorithmRepository;
import java.util.ArrayList;
import java.util.Collection;
import org.springframework.stereotype.Service;

@Service
public class DsmAlgorithmService {
  private final DsmAlgorithmRepository dsmAlgorithmRepository;

  public DsmAlgorithmService(DsmAlgorithmRepository dsmAlgorithmRepository) {
    this.dsmAlgorithmRepository = dsmAlgorithmRepository;
  }

  Collection<DsmAlgorithm> findActiveDsmAlgorithm() {
    Collection<DsmAlgorithm> col = new ArrayList<>();
    var results = dsmAlgorithmRepository.findDsmAlgorithmByStatusCode("A");
    if (results.isPresent()) {
      col = results.get();
    }
    return col;
  }
}
