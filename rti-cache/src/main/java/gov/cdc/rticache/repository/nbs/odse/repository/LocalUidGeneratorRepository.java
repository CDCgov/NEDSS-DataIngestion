package gov.cdc.rticache.repository.nbs.odse.repository;

import gov.cdc.rticache.repository.nbs.odse.model.LocalUidGenerator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocalUidGeneratorRepository extends JpaRepository<LocalUidGenerator, String> {
    @Query("select lug from LocalUidGenerator lug where lug.classNameCd = :id")
    Optional<LocalUidGenerator> findByIdForUpdate(String id);
}
