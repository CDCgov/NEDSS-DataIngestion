package gov.cdc.dataprocessing.repository.nbs.odse.repos.phc;

import gov.cdc.dataprocessing.repository.nbs.odse.model.phc.CaseManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface CaseManagementRepository extends JpaRepository<CaseManagement, Long> {
    @Query("SELECT data FROM CaseManagement data WHERE data.publicHealthCaseUid = :uid")
    Optional<Collection<CaseManagement>> findRecordsByPhcUid(@Param("uid") Long phcUid);
}
