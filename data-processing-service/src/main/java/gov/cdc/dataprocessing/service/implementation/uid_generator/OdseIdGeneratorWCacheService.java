package gov.cdc.dataprocessing.service.implementation.uid_generator;

import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.uid.LocalUidCacheModel;
import gov.cdc.dataprocessing.model.dto.uid.LocalUidGeneratorDto;
import gov.cdc.dataprocessing.model.dto.uid.LocalUidModel;
import gov.cdc.dataprocessing.repository.nbs.odse.model.generic_helper.LocalUidGenerator;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.locator.LocalUidGeneratorRepository;
import gov.cdc.dataprocessing.service.interfaces.cache.ICacheApiService;
import gov.cdc.dataprocessing.service.interfaces.uid_generator.IOdseIdGeneratorWCacheService;
import gov.cdc.dataprocessing.utilities.GsonUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static gov.cdc.dataprocessing.constant.enums.LocalIdClass.GA;

@Service
public class OdseIdGeneratorWCacheService implements IOdseIdGeneratorWCacheService {
    private final ICacheApiService cacheApiService;
    private final LocalUidGeneratorRepository localUidGeneratorRepository;

    public OdseIdGeneratorWCacheService(ICacheApiService cacheApiService, LocalUidGeneratorRepository localUidGeneratorRepository) {
        this.cacheApiService = cacheApiService;
        this.localUidGeneratorRepository = localUidGeneratorRepository;
    }

    public LocalUidModel getValidLocalUidByApi(LocalIdClass localIdClass, boolean gaApplied) {
        var res = cacheApiService.getOdseLocalId(localIdClass.name(), gaApplied);
        return GsonUtil.GSON.fromJson(res, LocalUidModel.class);
    }

    /**
     * Transaction here for guarantee no race condition
     * */
    @Transactional
    public LocalUidModel getValidLocalUid(LocalIdClass localIdClass, boolean gaApplied) throws DataProcessingException {
        return createNewLocalUid(localIdClass, gaApplied);
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
            Optional<LocalUidGenerator> localUidOpt = localUidGeneratorRepository.findByIdForUpdate(localIdClass.name());
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
