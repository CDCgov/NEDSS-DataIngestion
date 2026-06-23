package gov.cdc.dataprocessing.repository.nbs.srte.repository;

import gov.cdc.dataprocessing.repository.nbs.srte.model.LabTestLoinc;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LabTestLoincRepository extends JpaRepository<LabTestLoinc, String> {
  @Query(
      value =
          "SELECT lr.loincCd FROM LabTestLoinc lr WHERE lr.laboratoryId = :laboratoryId AND lr.labTestCd = :labTestCd")
  Optional<List<String>> findLoincCds(
      @Param("laboratoryId") String laboratoryId, @Param("labTestCd") String labTestCd);
}
