package gov.cdc.dataprocessing.repository.nbs.odse;

import gov.cdc.dataprocessing.repository.nbs.odse.model.EdxPatientMatch;
import gov.cdc.dataprocessing.repository.nbs.odse.model.Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface EdxPatientMatchRepository extends JpaRepository<EdxPatientMatch, Long> {
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM EdxPatientMatch e WHERE e.patientUid = ?1 AND e.matchString NOT LIKE 'LR^%'", nativeQuery = false)
    void deleteByPatientUidAndMatchStringNotLike(Long patientUid);
}