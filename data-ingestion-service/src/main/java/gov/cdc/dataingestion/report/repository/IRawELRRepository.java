package gov.cdc.dataingestion.report.repository;

import gov.cdc.dataingestion.report.repository.model.RawERLModel;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IRawELRRepository extends JpaRepository<RawERLModel, String> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE elr_raw SET status = :status WHERE id = :id", nativeQuery = true)
    void updateRawMessageWithNewVersion(String id, String status);
}