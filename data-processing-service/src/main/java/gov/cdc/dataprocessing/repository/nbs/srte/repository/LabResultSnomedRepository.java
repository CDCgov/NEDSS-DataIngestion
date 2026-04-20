package gov.cdc.dataprocessing.repository.nbs.srte.repository;

import gov.cdc.dataprocessing.repository.nbs.srte.model.LabResultSnomed;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LabResultSnomedRepository extends JpaRepository<LabResultSnomed, String> {
  @Query(
      value =
          "SELECT lr.snomedCd FROM LabResultSnomed lr WHERE lr.laboratoryId = :laboratoryId AND lr.labResultCd = :labResultCd")
  Optional<List<String>> findSnomedCds(
      @Param("laboratoryId") String laboratoryId, @Param("labResultCd") String labResultCd);
}
