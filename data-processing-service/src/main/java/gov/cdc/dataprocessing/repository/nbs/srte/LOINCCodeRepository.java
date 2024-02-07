package gov.cdc.dataprocessing.repository.nbs.srte;

import gov.cdc.dataprocessing.repository.nbs.srte.model.LOINCCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LOINCCodeRepository  extends JpaRepository<LOINCCode, String> {

    @Query(value = "select * from LOINCCode where time_aspect = 'Pt' and system_cd = '^Patient'", nativeQuery = true)
    Optional<List<LOINCCode>> findLoincCodes();
}