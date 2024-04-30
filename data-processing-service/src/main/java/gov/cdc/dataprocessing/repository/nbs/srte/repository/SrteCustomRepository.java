package gov.cdc.dataprocessing.repository.nbs.srte.repository;

import gov.cdc.dataprocessing.repository.nbs.srte.model.LabResult;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface SrteCustomRepository {
    List<LabResult> getAllLabResultJoinWithLabCodingSystemWithOrganismNameInd();


}
