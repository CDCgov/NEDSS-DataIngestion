package gov.cdc.dataprocessing.service.implementation.uid_generator;

import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.uid.LocalUidCacheModel;
import gov.cdc.dataprocessing.model.dto.uid.LocalUidGeneratorDto;
import gov.cdc.dataprocessing.model.dto.uid.LocalUidModel;
import gov.cdc.dataprocessing.repository.nbs.odse.model.generic_helper.LocalUidGenerator;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.locator.LocalUidGeneratorRepository;
import gov.cdc.dataprocessing.service.interfaces.uid_generator.IOdseIdGeneratorWCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static gov.cdc.dataprocessing.constant.enums.LocalIdClass.GA;
import static gov.cdc.dataprocessing.utilities.GsonUtil.GSON;

@Service
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 1118 - Private constructor complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118"})
public class OdseIdGeneratorWCacheService implements IOdseIdGeneratorWCacheService {

    private static final Logger logger = LoggerFactory.getLogger(OdseIdGeneratorWCacheService.class);

    private final LocalUidGeneratorRepository localUidGeneratorRepository;

    public OdseIdGeneratorWCacheService(LocalUidGeneratorRepository localUidGeneratorRepository, CacheManager cacheManager) {
        this.localUidGeneratorRepository = localUidGeneratorRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public LocalUidModel getValidLocalUid(LocalIdClass localIdClass, boolean gaApplied) throws DataProcessingException {
        logger.debug("OdseIdGeneratorWCacheService.getValidLocalUid: {}",  GSON.toJson(LocalUidCacheModel.localUidMap));
        boolean newKeyRequired = false;
        LocalUidModel localUidModel = LocalUidCacheModel.localUidMap.get(localIdClass.name());

        if (localUidModel != null) {
            if (localUidModel.getClassTypeUid().getUsedCounter() < localUidModel.getClassTypeUid().getCounter()) {
                if (localUidModel.getGaTypeUid() != null && localUidModel.getGaTypeUid().getUsedCounter() < localUidModel.getGaTypeUid().getCounter()) {
                    updateCounters(localUidModel, true);
                } else if (localUidModel.getGaTypeUid() == null) {
                    updateCounters(localUidModel, false);
                } else {
                    newKeyRequired = true;
                }
            }
        } else {
            newKeyRequired = true;
        }

        if (newKeyRequired) {
            localUidModel = createNewLocalUid(localIdClass, gaApplied);
            LocalUidCacheModel.localUidMap.put(localUidModel.getPrimaryClassName(), localUidModel);
        }

        return localUidModel;
    }

    private void updateCounters(LocalUidModel localUidModel, boolean gaValid) {
        var classUsedCounter = localUidModel.getClassTypeUid().getUsedCounter();
        var classSeed = localUidModel.getClassTypeUid().getSeedValueNbr();
        localUidModel.getClassTypeUid().setUsedCounter(++classUsedCounter);
        localUidModel.getClassTypeUid().setSeedValueNbr(++classSeed);

        if (gaValid) {
            var gaUsedCounter = localUidModel.getGaTypeUid().getUsedCounter();
            var gaSeed = localUidModel.getGaTypeUid().getSeedValueNbr();
            localUidModel.getGaTypeUid().setUsedCounter(++gaUsedCounter);
            localUidModel.getGaTypeUid().setSeedValueNbr(++gaSeed);
        }
    }

    private LocalUidModel createNewLocalUid(LocalIdClass localIdClass, boolean gaApplied) throws DataProcessingException {
        LocalUidGeneratorDto localId = fetchLocalId(localIdClass);
        LocalUidGeneratorDto gaLocalId = gaApplied ? fetchLocalId(GA) : null;

        var localUidModel = new LocalUidModel();
        localUidModel.setClassTypeUid(localId);
        localUidModel.setGaTypeUid(gaLocalId);
        localUidModel.setPrimaryClassName(localIdClass.name());

        return localUidModel;
    }

    private LocalUidGeneratorDto fetchLocalId(LocalIdClass localIdClass) throws DataProcessingException {
        try {
            Optional<LocalUidGenerator> localUidOpt = localUidGeneratorRepository.findById(localIdClass.name());
            if (localUidOpt.isPresent()) {
                LocalUidGeneratorDto localId = new LocalUidGeneratorDto(localUidOpt.get());
                localId.setCounter(LocalUidCacheModel.SEED_COUNTER);
                localId.setUsedCounter(1);

                long seed = localId.getSeedValueNbr();
                LocalUidGenerator newLocalId = new LocalUidGenerator();
                newLocalId.setUidSuffixCd(localId.getUidSuffixCd());
                newLocalId.setUidPrefixCd(localId.getUidPrefixCd());
                newLocalId.setTypeCd(localId.getTypeCd());
                newLocalId.setClassNameCd(localId.getClassNameCd());
                newLocalId.setSeedValueNbr(seed + LocalUidCacheModel.SEED_COUNTER + 1);
                localUidGeneratorRepository.save(newLocalId);

                return localId;
            } else {
                throw new DataProcessingException("Local UID not found for class: " + localIdClass.name());
            }
        } catch (Exception e) {
            throw new DataProcessingException("Error fetching local UID for class: " + localIdClass.name(), e);
        }
    }
}
