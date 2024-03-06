package gov.cdc.dataprocessing.repository.nbs.srte;

import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.Role;
import gov.cdc.dataprocessing.repository.nbs.srte.model.RaceCode;
import gov.cdc.dataprocessing.repository.nbs.srte.model.SnomedCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface SnomedConditionRepository extends JpaRepository<SnomedCondition, String> {
    @Query("SELECT DISTINCT data.conditionCd FROM SnomedCondition data WHERE data.snomedCd = :cd")
    Optional<List<String>> getConditionForSnomedCode(@Param("cd") String cd);
}
