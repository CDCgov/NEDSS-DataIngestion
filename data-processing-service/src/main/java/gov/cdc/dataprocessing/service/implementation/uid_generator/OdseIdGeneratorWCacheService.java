package gov.cdc.dataprocessing.service.implementation.uid_generator;

import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.uid.LocalUidCacheModel;
import gov.cdc.dataprocessing.model.dto.uid.LocalUidGeneratorDto;
import gov.cdc.dataprocessing.model.dto.uid.LocalUidModel;
import gov.cdc.dataprocessing.repository.nbs.odse.model.generic_helper.LocalUidGenerator;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.locator.LocalUidGeneratorRepository;
import gov.cdc.dataprocessing.service.interfaces.uid_generator.IOdseIdGeneratorWCacheService;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;
import java.util.TreeMap;

import static gov.cdc.dataprocessing.constant.enums.LocalIdClass.GA;

@Service
public class OdseIdGeneratorWCacheService implements IOdseIdGeneratorWCacheService {
    private final LocalUidGeneratorRepository localUidGeneratorRepository;

    public OdseIdGeneratorWCacheService(LocalUidGeneratorRepository localUidGeneratorRepository, CacheManager cacheManager) {
        this.localUidGeneratorRepository = localUidGeneratorRepository;
    }

    public LocalUidModel getValidLocalUid (LocalIdClass localIdClass, boolean gaApplied) throws DataProcessingException {
        LocalUidModel uid = new LocalUidModel();
        boolean newKeyRequired = false;
        if (LocalUidCacheModel.localUidMap.containsKey(localIdClass.name())) {
            var localUidModel = LocalUidCacheModel.localUidMap.get(localIdClass.name());
            if (localUidModel.getClassTypeUid().getUsedCounter() <
                    localUidModel.getClassTypeUid().getCounter()
            ) {
                if (localUidModel.getGaTypeUid() != null
                    && localUidModel.getGaTypeUid().getUsedCounter() < localUidModel.getGaTypeUid().getCounter())
                {
                    // Scene where Ga valid
                    var classUsedCounter = localUidModel.getClassTypeUid().getUsedCounter();
                    var classSeed = localUidModel.getClassTypeUid().getSeedValueNbr();
                    localUidModel.getClassTypeUid().setUsedCounter(++classUsedCounter);
                    localUidModel.getClassTypeUid().setSeedValueNbr(++classSeed);

                    var gaUsedCounter = localUidModel.getGaTypeUid().getUsedCounter();
                    var gaSeed = localUidModel.getGaTypeUid().getSeedValueNbr();
                    localUidModel.getGaTypeUid().setUsedCounter(++gaUsedCounter);
                    localUidModel.getGaTypeUid().setSeedValueNbr(++gaSeed);
                    uid = localUidModel;


                }
                else if (localUidModel.getGaTypeUid() == null)
                {
                    // Scene where Ga not valid
                    var classUsedCounter = localUidModel.getClassTypeUid().getUsedCounter();
                    var classSeed = localUidModel.getClassTypeUid().getSeedValueNbr();
                    localUidModel.getClassTypeUid().setUsedCounter(++classUsedCounter);
                    localUidModel.getClassTypeUid().setSeedValueNbr(++classSeed);
                    uid = localUidModel;
                }
                else
                {
                    newKeyRequired = true;
                }
            }
        } else {
            newKeyRequired = true;
        }

        // New Key and Batch
        if (newKeyRequired) {
            var newkey = getLocalId(localIdClass, gaApplied);
            uid = newkey;
            LocalUidCacheModel.localUidMap.put(newkey.getPrimaryClassName(), newkey);
        }
        return uid;
    }

    protected LocalUidModel getLocalId(LocalIdClass localIdClass, boolean gaApplied) throws DataProcessingException {
        try {
            Optional<LocalUidGenerator> localUidOpt = this.localUidGeneratorRepository.findById(localIdClass.name());
            LocalUidGeneratorDto localId = null;
            if (localUidOpt.isPresent()) {
                localId = new LocalUidGeneratorDto(localUidOpt.get());
                localId.setCounter(LocalUidCacheModel.SEED_COUNTER);
                localId.setUsedCounter(1);
            }

            if (localId != null) {
                long seed = localId.getSeedValueNbr();
                LocalUidGenerator newLocalId = new LocalUidGenerator();
                newLocalId.setUidSuffixCd(localId.getUidSuffixCd());
                newLocalId.setUidPrefixCd(localId.getUidPrefixCd());
                newLocalId.setTypeCd(localId.getTypeCd());
                newLocalId.setClassNameCd(localId.getClassNameCd());
                newLocalId.setSeedValueNbr(seed + LocalUidCacheModel.SEED_COUNTER + 1);
                this.localUidGeneratorRepository.save(newLocalId);
            }

            LocalUidGeneratorDto gaLocalId = null;
            if (gaApplied) {
                Optional<LocalUidGenerator> gaLocalUidOpt = this.localUidGeneratorRepository.findById(GA.name());

                if (gaLocalUidOpt.isPresent()) {
                    gaLocalId = new LocalUidGeneratorDto(gaLocalUidOpt.get());
                    gaLocalId.setCounter(LocalUidCacheModel.SEED_COUNTER);
                    gaLocalId.setUsedCounter(1);
                }

                if (gaLocalId != null) {
                    long seed = gaLocalId.getSeedValueNbr();
                    LocalUidGenerator newLocalId = new LocalUidGenerator();
                    newLocalId.setUidSuffixCd(gaLocalId.getUidSuffixCd());
                    newLocalId.setUidPrefixCd(gaLocalId.getUidPrefixCd());
                    newLocalId.setTypeCd(gaLocalId.getTypeCd());
                    newLocalId.setClassNameCd(gaLocalId.getClassNameCd());
                    newLocalId.setSeedValueNbr(seed + LocalUidCacheModel.SEED_COUNTER + 1);
                    this.localUidGeneratorRepository.save(newLocalId);
                }

            }

            var localUidModel = new LocalUidModel();
            localUidModel.setClassTypeUid(localId);
            localUidModel.setGaTypeUid(gaLocalId);
            localUidModel.setPrimaryClassName(localIdClass.name());
            return localUidModel;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }
}