package gov.cdc.nbsDedup.nbs.srte.repository.custom;

import gov.cdc.nbsDedup.nbs.srte.model.LabResult;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface SrteCustomRepository {
    List<LabResult> getAllLabResultJoinWithLabCodingSystemWithOrganismNameInd();


}
