package gov.cdc.dataprocessing.repository.nbs.odse.repos.locator;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.repository.nbs.odse.model.generic_helper.LocalUidGenerator;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository


public interface LocalUidGeneratorRepository extends JpaRepository<LocalUidGenerator, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select lug from LocalUidGenerator lug where lug.classNameCd = :id")
    Optional<LocalUidGenerator> findByIdForUpdate(String id);

    @Transactional
    default LocalUidGenerator reserveBatchAndGetStartSeed(String classNameCd, int batchSize) throws DataProcessingException {
        Optional<LocalUidGenerator> opt = findByIdForUpdate(classNameCd);
        if (opt.isEmpty()) {
            throw new DataProcessingException("Local UID not found for class: " + classNameCd);
        }

        LocalUidGenerator generator = opt.get();
        long currentSeed = generator.getSeedValueNbr();
        generator.setSeedValueNbr(currentSeed + batchSize);
        save(generator); // single update call

        return opt.get();
    }
}
