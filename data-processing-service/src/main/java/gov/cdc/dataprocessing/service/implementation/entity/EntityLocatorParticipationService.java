package gov.cdc.dataprocessing.service.implementation.entity;

import com.google.gson.Gson;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.kafka.consumer.KafkaEdxLogConsumer;
import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityLocatorParticipation;
import gov.cdc.dataprocessing.repository.nbs.odse.model.generic_helper.LocalUidGenerator;
import gov.cdc.dataprocessing.repository.nbs.odse.model.locator.PhysicalLocator;
import gov.cdc.dataprocessing.repository.nbs.odse.model.locator.PostalLocator;
import gov.cdc.dataprocessing.repository.nbs.odse.model.locator.TeleLocator;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.entity.EntityLocatorParticipationRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.locator.PhysicalLocatorRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.locator.PostalLocatorRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.locator.TeleLocatorRepository;
import gov.cdc.dataprocessing.service.implementation.uid_generator.OdseIdGeneratorService;
import gov.cdc.dataprocessing.service.interfaces.entity.IEntityLocatorParticipationService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EntityLocatorParticipationService implements IEntityLocatorParticipationService {
    private static final Logger logger = LoggerFactory.getLogger(EntityLocatorParticipationService.class); // NOSONAR

    private final EntityLocatorParticipationRepository entityLocatorParticipationRepository;
    private final TeleLocatorRepository teleLocatorRepository;
    private final PostalLocatorRepository postalLocatorRepository;
    private final PhysicalLocatorRepository physicalLocatorRepository;
    private final OdseIdGeneratorService odseIdGeneratorService;
    public EntityLocatorParticipationService(EntityLocatorParticipationRepository entityLocatorParticipationRepository,
                                             TeleLocatorRepository teleLocatorRepository,
                                             PostalLocatorRepository postalLocatorRepository,
                                             PhysicalLocatorRepository physicalLocatorRepository,
                                             OdseIdGeneratorService odseIdGeneratorService) {
        this.entityLocatorParticipationRepository = entityLocatorParticipationRepository;
        this.teleLocatorRepository = teleLocatorRepository;
        this.postalLocatorRepository = postalLocatorRepository;
        this.physicalLocatorRepository = physicalLocatorRepository;
        this.odseIdGeneratorService = odseIdGeneratorService;
    }

//    public void updateEntityLocatorParticipationV2(Collection<EntityLocatorParticipationDto> locatorCollection,
//                                                   Long patientUid, Long parentUid) {
//
//        var mprRecord = entityLocatorParticipationRepository.findByParentUid(parentUid);
//        if (mprRecord.isPresent()) {
//            var mprPostal = mprRecord.get().stream().filter(x -> x.getClassCd().equals(NEDSSConstant.POSTAL)).toList();
//            var mprPhy = mprRecord.get().stream().filter(x -> x.getClassCd().equals(NEDSSConstant.PHYSICAL)).toList();
//            var mprTel = mprRecord.get().stream().filter(x -> x.getClassCd().equals(NEDSSConstant.TELE)).toList();
//
//            var incomingPostals = locatorCollection.stream().filter(x -> x.getClassCd().equals(NEDSSConstant.POSTAL) &&
//                    x.getLocatorUid() == null).toList();
//            var existingPostals = locatorCollection.stream().filter(x -> x.getClassCd().equals(NEDSSConstant.POSTAL) &&
//                    x.getLocatorUid() != null).toList();
//
//            if (!existingPostals.isEmpty()) {
//                var deletePostals = existingPostals.stream().filter(BaseContainer::isItDelete).toList();
//                for (EntityLocatorParticipationDto entityLocatorParticipationDto : deletePostals) {
//                    try {
//                        postalLocatorRepository.deletePostalById(entityLocatorParticipationDto.getLocatorUid());
//                        entityLocatorParticipationRepository.deleteEntityLocatorById(entityLocatorParticipationDto.getEntityUid(),
//                                entityLocatorParticipationDto.getLocatorUid());
//
//                        postalLocatorRepository.deletePostalById(entityLocatorParticipationDto.getLocatorUid());
//                        entityLocatorParticipationRepository.deleteEntityLocatorById(entityLocatorParticipationDto.getEntityUid(),
//                                entityLocatorParticipationDto.getLocatorUid());
//                    } catch (Exception e) {
//                        logger.info("ERROR DELETE LOCATOR: " + e.getMessage());
//                    }
//                }
//            }
//            if (!incomingPostals.isEmpty()) {
//                for (EntityLocatorParticipationDto entityLocatorParticipationDto : incomingPostals) {
//                    try {
//                        LocalUidGenerator localUid = odseIdGeneratorService.getLocalIdAndUpdateSeed(LocalIdClass.PERSON);
//                        entityLocatorParticipationDto.getThePostalLocatorDto().setPostalLocatorUid(localUid.getSeedValueNbr());
//                        postalLocatorRepository.save(new PostalLocator(entityLocatorParticipationDto.getThePostalLocatorDto()));
//                        entityLocatorParticipationDto.setLocatorUid(localUid.getSeedValueNbr());
//                        if (entityLocatorParticipationDto.getVersionCtrlNbr() == null) {
//                            entityLocatorParticipationDto.setVersionCtrlNbr(1);
//                        }
//                        entityLocatorParticipationRepository.save(new EntityLocatorParticipation(entityLocatorParticipationDto));
//                    } catch (Exception e) {
//                        logger.info("ERROR DELETE LOCATOR: " + e.getMessage());
//                    }
//                }
//            }
//
//
//            var incomingPhysicals = locatorCollection.stream().filter(x -> x.getClassCd().equals(NEDSSConstant.PHYSICAL) &&
//                    x.getLocatorUid() == null).toList();
//            var existingPhysicals = locatorCollection.stream().filter(x -> x.getClassCd().equals(NEDSSConstant.PHYSICAL) &&
//                    x.getLocatorUid() != null).toList();
//
//            var incomingTeles = locatorCollection.stream().filter(x -> x.getClassCd().equals(NEDSSConstant.TELE) &&
//                    x.getLocatorUid() == null).toList();
//            var existingTeles = locatorCollection.stream().filter(x -> x.getClassCd().equals(NEDSSConstant.TELE) &&
//                    x.getLocatorUid() != null).toList();
//
//        }
//    }
//
//
    @SuppressWarnings({"java:S6541", "java:S3776"})
    @Transactional
    public void updateEntityLocatorParticipation(Collection<EntityLocatorParticipationDto> locatorCollection, Long patientUid) throws DataProcessingException {
        ArrayList<EntityLocatorParticipationDto> personList = (ArrayList<EntityLocatorParticipationDto> ) locatorCollection;
        var uid = patientUid;
        var locatorData = entityLocatorParticipationRepository.findByParentUid(uid);
        List<EntityLocatorParticipation> entityLocatorParticipations = new ArrayList<>();
        if (locatorData.isPresent()) {
            entityLocatorParticipations = locatorData.get();
        }

        if (!entityLocatorParticipations.isEmpty()) {
            List<EntityLocatorParticipation> physicalLocators;
            List<EntityLocatorParticipation> postalLocators;
            List<EntityLocatorParticipation> teleLocators;

            physicalLocators = entityLocatorParticipations.stream().filter(x -> x.getClassCd()
                            .equalsIgnoreCase(NEDSSConstant.PHYSICAL))
                    .sorted(Comparator.comparing(EntityLocatorParticipation::getRecordStatusTime).reversed())
                    .collect(Collectors.toList());
            postalLocators = entityLocatorParticipations.stream().filter(x -> x.getClassCd()
                            .equalsIgnoreCase(NEDSSConstant.POSTAL))
                    .sorted(Comparator.comparing(EntityLocatorParticipation::getRecordStatusTime).reversed())
                    .collect(Collectors.toList());
            teleLocators = entityLocatorParticipations.stream().filter(x -> x.getClassCd()
                            .equalsIgnoreCase(NEDSSConstant.TELE))
                    .sorted(Comparator.comparing(EntityLocatorParticipation::getRecordStatusTime).reversed())
                    .collect(Collectors.toList());



            StringBuilder comparingString = new StringBuilder();
            for (EntityLocatorParticipationDto entityLocatorParticipationDto : personList) {

                LocalUidGenerator localUid = odseIdGeneratorService.getLocalIdAndUpdateSeed(LocalIdClass.PERSON);
                boolean newLocator = true;
                if (entityLocatorParticipationDto.getClassCd().equals(NEDSSConstant.PHYSICAL) && entityLocatorParticipationDto.getThePhysicalLocatorDto() != null)
                {
                    if (!physicalLocators.isEmpty()) {
                        var existingLocator = physicalLocatorRepository.findByPhysicalLocatorUids(
                                physicalLocators.stream()
                                        .map(EntityLocatorParticipation::getLocatorUid)
                                        .collect(Collectors.toList()));

                        List<String> compareStringList = new ArrayList<>();

                        if (existingLocator.isPresent()) {
                            for (int j = 0; j < existingLocator.get().size(); j++) {
                                comparingString.setLength(0);
                                comparingString.append(existingLocator.get().get(j).getImageTxt());
                                compareStringList.add(comparingString.toString().toUpperCase());
                            }


                            if (!compareStringList.contains(Arrays.toString(entityLocatorParticipationDto.getThePhysicalLocatorDto().getImageTxt()).toUpperCase()))
                            {
                                uid = entityLocatorParticipationDto.getEntityUid();
                                entityLocatorParticipationDto.getThePhysicalLocatorDto().setPhysicalLocatorUid(localUid.getSeedValueNbr());
                                physicalLocatorRepository.save(new PhysicalLocator(entityLocatorParticipationDto.getThePhysicalLocatorDto()));
                            }
                            else
                            {
                                newLocator = false;
                            }
                        }
                        else
                        {
                            uid = entityLocatorParticipationDto.getEntityUid();
                            entityLocatorParticipationDto.getThePhysicalLocatorDto().setPhysicalLocatorUid(localUid.getSeedValueNbr());
                            physicalLocatorRepository.save(new PhysicalLocator(entityLocatorParticipationDto.getThePhysicalLocatorDto()));
                        }

                        comparingString.setLength(0);
                    }
                    else
                    {
                        uid = entityLocatorParticipationDto.getEntityUid();
                        entityLocatorParticipationDto.getThePhysicalLocatorDto().setPhysicalLocatorUid(localUid.getSeedValueNbr());
                        physicalLocatorRepository.save(new PhysicalLocator(entityLocatorParticipationDto.getThePhysicalLocatorDto()));
                    }
                }
                else if (
                        entityLocatorParticipationDto.getClassCd().equals(NEDSSConstant.POSTAL)
                        && entityLocatorParticipationDto.getThePostalLocatorDto() != null
                )
                {
                    if (!postalLocators.isEmpty())
                    {
                        var existingLocator = postalLocatorRepository.findByPostalLocatorUids(
                                postalLocators.stream()
                                        .map(EntityLocatorParticipation::getLocatorUid)
                                        .collect(Collectors.toList()));

                        List<String> compareStringList = new ArrayList<>();
                        if (existingLocator.isPresent())
                        {
                            for (int j = 0; j < existingLocator.get().size(); j++) {
                                comparingString.setLength(0);
                                comparingString.append(existingLocator.get().get(j).getCityCd());
                                comparingString.append(existingLocator.get().get(j).getCityDescTxt());
                                comparingString.append(existingLocator.get().get(j).getCntryCd());
                                comparingString.append(existingLocator.get().get(j).getCntryDescTxt());
                                comparingString.append(existingLocator.get().get(j).getCntyCd());
                                comparingString.append(existingLocator.get().get(j).getCntyDescTxt());
                                comparingString.append(existingLocator.get().get(j).getStateCd());
                                comparingString.append(existingLocator.get().get(j).getStreetAddr1());
                                comparingString.append(existingLocator.get().get(j).getStreetAddr2());
                                comparingString.append(existingLocator.get().get(j).getZipCd());

                                compareStringList.add(comparingString.toString().toUpperCase());
                            }


                            StringBuilder existComparingLocator = new StringBuilder();
                            existComparingLocator.append(entityLocatorParticipationDto.getThePostalLocatorDto().getCityCd());
                            existComparingLocator.append(entityLocatorParticipationDto.getThePostalLocatorDto().getCityDescTxt());
                            existComparingLocator.append(entityLocatorParticipationDto.getThePostalLocatorDto().getCntryCd());
                            existComparingLocator.append(entityLocatorParticipationDto.getThePostalLocatorDto().getCntryDescTxt());
                            existComparingLocator.append(entityLocatorParticipationDto.getThePostalLocatorDto().getCntyCd());
                            existComparingLocator.append(entityLocatorParticipationDto.getThePostalLocatorDto().getCntyDescTxt());
                            existComparingLocator.append(entityLocatorParticipationDto.getThePostalLocatorDto().getStateCd());
                            existComparingLocator.append(entityLocatorParticipationDto.getThePostalLocatorDto().getStreetAddr1());
                            existComparingLocator.append(entityLocatorParticipationDto.getThePostalLocatorDto().getStreetAddr2());
                            existComparingLocator.append(entityLocatorParticipationDto.getThePostalLocatorDto().getZipCd());


                            if (!compareStringList.contains(existComparingLocator.toString().toUpperCase())) {
                                uid = entityLocatorParticipationDto.getEntityUid();
                                entityLocatorParticipationDto.getThePostalLocatorDto().setPostalLocatorUid(localUid.getSeedValueNbr());
                                postalLocatorRepository.save(new PostalLocator(entityLocatorParticipationDto.getThePostalLocatorDto()));

                            }
                            else
                            {
                                newLocator = false;
                            }
                        }
                        else
                        {
                            uid = entityLocatorParticipationDto.getEntityUid();
                            entityLocatorParticipationDto.getThePostalLocatorDto().setPostalLocatorUid(localUid.getSeedValueNbr());
                            postalLocatorRepository.save(new PostalLocator(entityLocatorParticipationDto.getThePostalLocatorDto()));
                        }
                        comparingString.setLength(0);
                    }
                    else
                    {
                        uid = entityLocatorParticipationDto.getEntityUid();
                        entityLocatorParticipationDto.getThePostalLocatorDto().setPostalLocatorUid(localUid.getSeedValueNbr());
                        postalLocatorRepository.save(new PostalLocator(entityLocatorParticipationDto.getThePostalLocatorDto()));
                    }
                }
                else if (entityLocatorParticipationDto.getClassCd().equals(NEDSSConstant.TELE) && entityLocatorParticipationDto.getTheTeleLocatorDto() != null)
                {
                    if (!teleLocators.isEmpty())
                    {
                        var existingLocator = teleLocatorRepository.findByTeleLocatorUids(
                                teleLocators.stream()
                                        .map(EntityLocatorParticipation::getLocatorUid)
                                        .collect(Collectors.toList()));
                        List<String> compareStringList = new ArrayList<>();

                        if (existingLocator.isPresent())
                        {
                            for (int j = 0; j < existingLocator.get().size(); j++) {
                                comparingString.setLength(0);
                                comparingString.append(existingLocator.get().get(j).getCntryCd());
                                comparingString.append(existingLocator.get().get(j).getEmailAddress());
                                comparingString.append(existingLocator.get().get(j).getExtensionTxt());
                                comparingString.append(existingLocator.get().get(j).getPhoneNbrTxt());
                                comparingString.append(existingLocator.get().get(j).getUrlAddress());
                                compareStringList.add(comparingString.toString().toUpperCase());
                            }

                            StringBuilder existComparingLocator = new StringBuilder();
                            existComparingLocator.append(entityLocatorParticipationDto.getTheTeleLocatorDto().getCntryCd());
                            existComparingLocator.append(entityLocatorParticipationDto.getTheTeleLocatorDto().getEmailAddress());
                            existComparingLocator.append(entityLocatorParticipationDto.getTheTeleLocatorDto().getExtensionTxt());
                            existComparingLocator.append(entityLocatorParticipationDto.getTheTeleLocatorDto().getPhoneNbrTxt());
                            existComparingLocator.append(entityLocatorParticipationDto.getTheTeleLocatorDto().getUrlAddress());

                            if (!compareStringList.contains(existComparingLocator.toString().toUpperCase()))
                            {
                                uid = entityLocatorParticipationDto.getEntityUid();
                                entityLocatorParticipationDto.getTheTeleLocatorDto().setTeleLocatorUid(localUid.getSeedValueNbr());
                                teleLocatorRepository.save(new TeleLocator(entityLocatorParticipationDto.getTheTeleLocatorDto()));
                            }
                            else
                            {
                                newLocator = false;
                            }
                        }
                        else
                        {
                            uid = entityLocatorParticipationDto.getEntityUid();
                            entityLocatorParticipationDto.getTheTeleLocatorDto().setTeleLocatorUid(localUid.getSeedValueNbr());
                            teleLocatorRepository.save(new TeleLocator(entityLocatorParticipationDto.getTheTeleLocatorDto()));
                        }

                        comparingString.setLength(0);
                    }
                    else
                    {
                        uid = entityLocatorParticipationDto.getEntityUid();
                        entityLocatorParticipationDto.getTheTeleLocatorDto().setTeleLocatorUid(localUid.getSeedValueNbr());
                        teleLocatorRepository.save(new TeleLocator(entityLocatorParticipationDto.getTheTeleLocatorDto()));
                    }
                }
                else
                {
                    newLocator = false;
                }

                // ONLY persist new participation locator if new locator actually exist
                if (newLocator) {
                    entityLocatorParticipationDto.setEntityUid(uid);
                    entityLocatorParticipationDto.setLocatorUid(localUid.getSeedValueNbr());

                    if (entityLocatorParticipationDto.getVersionCtrlNbr() == null) {
                        entityLocatorParticipationDto.setVersionCtrlNbr(1);
                    }
                    entityLocatorParticipationRepository.save(new EntityLocatorParticipation(entityLocatorParticipationDto));
                }

            }
        }
    }

    @Transactional
    public void createEntityLocatorParticipation(Collection<EntityLocatorParticipationDto> locatorCollection, Long uid) throws DataProcessingException {
        ArrayList<EntityLocatorParticipationDto>  personList = (ArrayList<EntityLocatorParticipationDto> ) locatorCollection;
        try {
            for (EntityLocatorParticipationDto entityLocatorParticipationDto : personList) {
                boolean inserted = false;
                LocalUidGenerator localUid = odseIdGeneratorService.getLocalIdAndUpdateSeed(LocalIdClass.PERSON);
                if (entityLocatorParticipationDto.getClassCd().equals(NEDSSConstant.PHYSICAL) && entityLocatorParticipationDto.getThePhysicalLocatorDto() != null) {
                    entityLocatorParticipationDto.getThePhysicalLocatorDto().setPhysicalLocatorUid(localUid.getSeedValueNbr());
                    physicalLocatorRepository.save(new PhysicalLocator(entityLocatorParticipationDto.getThePhysicalLocatorDto()));
                    inserted = true;
                } else if (entityLocatorParticipationDto.getClassCd().equals(NEDSSConstant.POSTAL)
                        && entityLocatorParticipationDto.getThePostalLocatorDto() != null
//                        && entityLocatorParticipationDto.getThePostalLocatorDto().getStreetAddr1() != null
                ) {
                    entityLocatorParticipationDto.getThePostalLocatorDto().setPostalLocatorUid(localUid.getSeedValueNbr());
                    postalLocatorRepository.save(new PostalLocator(entityLocatorParticipationDto.getThePostalLocatorDto()));
                    inserted = true;
                } else if (entityLocatorParticipationDto.getClassCd().equals(NEDSSConstant.TELE)
                        && entityLocatorParticipationDto.getTheTeleLocatorDto() != null
                && entityLocatorParticipationDto.getTheTeleLocatorDto().getPhoneNbrTxt() != null) {
                    entityLocatorParticipationDto.getTheTeleLocatorDto().setTeleLocatorUid(localUid.getSeedValueNbr());
                    teleLocatorRepository.save(new TeleLocator(entityLocatorParticipationDto.getTheTeleLocatorDto()));
                    inserted = true;
                }

                if (inserted) {
                    entityLocatorParticipationDto.setEntityUid(uid);
                    entityLocatorParticipationDto.setLocatorUid(localUid.getSeedValueNbr());

                    if (entityLocatorParticipationDto.getVersionCtrlNbr() == null) {
                        entityLocatorParticipationDto.setVersionCtrlNbr(1);
                    }
                    entityLocatorParticipationRepository.save(new EntityLocatorParticipation(entityLocatorParticipationDto));
                }

            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }


    public List<EntityLocatorParticipation> findEntityLocatorById(Long uid) {
       var result = entityLocatorParticipationRepository.findByParentUid(uid);
        return result.orElseGet(ArrayList::new);
    }
}
