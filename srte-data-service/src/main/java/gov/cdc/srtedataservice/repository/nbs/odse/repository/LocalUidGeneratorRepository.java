package gov.cdc.srtedataservice.repository.nbs.odse.repository;

import gov.cdc.srtedataservice.repository.nbs.odse.model.LocalUidGenerator;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocalUidGeneratorRepository extends JpaRepository<LocalUidGenerator, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select lug from LocalUidGenerator lug where lug.classNameCd = :classNameCd")
    Optional<LocalUidGenerator> findByIdForUpdate(String classNameCd);
}
