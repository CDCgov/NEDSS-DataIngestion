package gov.cdc.srtedataservice.repository.nbs.srte.repository.custom;

import gov.cdc.srtedataservice.repository.nbs.srte.model.LabResult;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface SrteCustomRepository {
  List<LabResult> getAllLabResultJoinWithLabCodingSystemWithOrganismNameInd();
}
