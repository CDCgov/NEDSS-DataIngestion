package gov.cdc.srtedataservice.repository.nbs.srte.repository;

import gov.cdc.srtedataservice.repository.nbs.srte.model.SnomedCode;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SnomedCodeRepository extends JpaRepository<SnomedCode, String> {
  @Query("SELECT sc FROM SnomedCode sc WHERE sc.snomedCd = :snomedCd")
  Optional<List<SnomedCode>> findSnomedProgramAreaExclusion(@Param("snomedCd") String snomedCd);
}
