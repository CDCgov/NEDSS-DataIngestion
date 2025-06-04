package gov.cdc.srtedataservice.service;

import gov.cdc.srtedataservice.cache_model.LocalUidCacheModel;
import gov.cdc.srtedataservice.constant.LocalIdClass;
import gov.cdc.srtedataservice.exception.RtiCacheException;
import gov.cdc.srtedataservice.model.dto.LocalUidGeneratorDto;
import gov.cdc.srtedataservice.model.dto.LocalUidModel;
import gov.cdc.srtedataservice.repository.nbs.odse.model.LocalUidGenerator;
import gov.cdc.srtedataservice.repository.nbs.odse.repository.LocalUidGeneratorRepository;
import gov.cdc.srtedataservice.service.interfaces.IOdseIdGeneratorWCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static gov.cdc.srtedataservice.constant.LocalIdClass.GA;
@Service
public class OdseIdGeneratorWCacheService implements IOdseIdGeneratorWCacheService {
    private static final Logger logger = LoggerFactory.getLogger(OdseIdGeneratorWCacheService.class); // NOSONAR
    private final LocalUidGeneratorRepository localUidGeneratorRepository;
    public OdseIdGeneratorWCacheService(LocalUidGeneratorRepository localUidGeneratorRepository) {
        this.localUidGeneratorRepository = localUidGeneratorRepository;
    }

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
                throw new RtiCacheException("Local UID not found for class: " + localIdClass.name());
            }
        } catch (Exception e) {
            throw new RtiCacheException("Error fetching local UID for class: " + localIdClass.name(), e);
        }
    }
}