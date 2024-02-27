package gov.cdc.dataprocessing.repository.nbs.odse;

import gov.cdc.dataprocessing.repository.nbs.odse.model.LocalUidGenerator;
import gov.cdc.dataprocessing.repository.nbs.odse.model.PersonEthnicGroup;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocalUidGeneratorRepository extends JpaRepository<LocalUidGenerator, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select lug from LocalUidGenerator lug where lug.classNameCd = :id")
    Optional<LocalUidGenerator> findByIdForUpdate(String id);
}
