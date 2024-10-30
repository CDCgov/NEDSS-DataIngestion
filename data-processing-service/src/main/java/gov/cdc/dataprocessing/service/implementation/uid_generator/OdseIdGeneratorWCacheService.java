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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static gov.cdc.dataprocessing.constant.enums.LocalIdClass.GA;

@Service
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201"})
public class OdseIdGeneratorWCacheService implements IOdseIdGeneratorWCacheService {

    private static final Logger logger = LoggerFactory.getLogger(OdseIdGeneratorWCacheService.class); // NOSONAR

    private final LocalUidGeneratorRepository localUidGeneratorRepository;

    public OdseIdGeneratorWCacheService(LocalUidGeneratorRepository localUidGeneratorRepository) {
        this.localUidGeneratorRepository = localUidGeneratorRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public LocalUidModel getValidLocalUid(LocalIdClass localIdClass, boolean gaApplied) throws DataProcessingException {
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
