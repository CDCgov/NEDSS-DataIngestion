package gov.cdc.dataprocessing.repository.nbs.odse.repos.observation;

import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.Observation_Lab_Summary_ForWorkUp_New;
import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.Observation_Summary;

import java.util.Collection;
import java.util.Optional;



public interface Observation_SummaryRepository // NOSONAR
{
   Collection<Observation_Summary> findAllActiveLabReportUidListForManage(Long investigationUid, String whereClause);

   Optional<Collection<Observation_Lab_Summary_ForWorkUp_New>> findLabSummaryForWorkupNew(Long personParentUid, String whereClause);
}
