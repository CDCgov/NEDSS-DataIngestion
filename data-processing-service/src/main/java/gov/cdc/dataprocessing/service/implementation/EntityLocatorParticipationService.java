package gov.cdc.dataprocessing.service.implementation;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.PersonContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityLocatorParticipation;
import gov.cdc.dataprocessing.repository.nbs.odse.model.locator.PhysicalLocator;
import gov.cdc.dataprocessing.repository.nbs.odse.model.locator.PostalLocator;
import gov.cdc.dataprocessing.repository.nbs.odse.model.locator.TeleLocator;
import gov.cdc.dataprocessing.repository.nbs.odse.model.other_move_as_needed.LocalUidGenerator;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.entity.EntityLocatorParticipationRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.locator.PhysicalLocatorRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.locator.PostalLocatorRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.locator.TeleLocatorRepository;
import gov.cdc.dataprocessing.service.implementation.core.OdseIdGeneratorService;
import gov.cdc.dataprocessing.service.interfaces.IEntityLocatorParticipationService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EntityLocatorParticipationService implements IEntityLocatorParticipationService {
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

    @Transactional
    public void updateEntityLocatorParticipation(Collection<EntityLocatorParticipationDto> locatorCollection, Long uid) throws DataProcessingException {
        ArrayList<EntityLocatorParticipationDto> personList = (ArrayList<EntityLocatorParticipationDto> ) locatorCollection;
        List<EntityLocatorParticipation> entityLocatorParticipations = entityLocatorParticipationRepository.findByParentUid(uid).get();

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


            EntityLocatorParticipation physicalLocator;
            EntityLocatorParticipation postalLocator;
            EntityLocatorParticipation teleLocator;

            StringBuilder comparingString = new StringBuilder();
            for(int i = 0; i < personList.size(); i++) {

                LocalUidGenerator localUid = odseIdGeneratorService.getLocalIdAndUpdateSeed(LocalIdClass.PERSON);
                boolean newLocator = true;
                if (personList.get(i).getClassCd().equals(NEDSSConstant.PHYSICAL) && personList.get(i).getThePhysicalLocatorDto() != null) {
                    newLocator = true;
                    if (!physicalLocators.isEmpty()) {
                        var existingLocator = physicalLocatorRepository.findByPhysicalLocatorUids(
                                physicalLocators.stream()
                                        .map(x -> x.getLocatorUid())
                                        .collect(Collectors.toList()));

                        List<String> compareStringList = new ArrayList<>();

                        if (existingLocator.isPresent()) {
                            for(int j = 0; j < existingLocator.get().size(); j++) {
                                comparingString.setLength(0);
                                comparingString.append(existingLocator.get().get(j).getImageTxt());
                                compareStringList.add(comparingString.toString().toUpperCase());
                            }


                            if (!compareStringList.contains(personList.get(i).getThePhysicalLocatorDto().getImageTxt().toString().toUpperCase())) {
                                personList.get(i).getThePhysicalLocatorDto().setPhysicalLocatorUid(localUid.getSeedValueNbr());
                                physicalLocatorRepository.save(new PhysicalLocator(personList.get(i).getThePhysicalLocatorDto()));
                            }
                            else {
                                newLocator = false;
                            }
                        }
                        else {
                            personList.get(i).getThePhysicalLocatorDto().setPhysicalLocatorUid(localUid.getSeedValueNbr());
                            physicalLocatorRepository.save(new PhysicalLocator(personList.get(i).getThePhysicalLocatorDto()));
                        }

                        comparingString.setLength(0);
                    }
                    else {
                        personList.get(i).getThePhysicalLocatorDto().setPhysicalLocatorUid(localUid.getSeedValueNbr());
                        physicalLocatorRepository.save(new PhysicalLocator(personList.get(i).getThePhysicalLocatorDto()));
                    }
                }
                else if (personList.get(i).getClassCd().equals(NEDSSConstant.POSTAL) && personList.get(i).getThePostalLocatorDto() != null) {
                    newLocator = true;
                    if (!postalLocators.isEmpty()) {
                        var existingLocator = postalLocatorRepository.findByPostalLocatorUids(
                                postalLocators.stream()
                                        .map(x -> x.getLocatorUid())
                                        .collect(Collectors.toList()));

                        List<String> compareStringList = new ArrayList<>();
                        if (existingLocator.isPresent()) {
                            for(int j = 0; j < existingLocator.get().size(); j++) {
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
                            existComparingLocator.append(personList.get(i).getThePostalLocatorDto().getCityCd());
                            existComparingLocator.append(personList.get(i).getThePostalLocatorDto().getCityDescTxt());
                            existComparingLocator.append(personList.get(i).getThePostalLocatorDto().getCntryCd());
                            existComparingLocator.append(personList.get(i).getThePostalLocatorDto().getCntryDescTxt());
                            existComparingLocator.append(personList.get(i).getThePostalLocatorDto().getCntyCd());
                            existComparingLocator.append(personList.get(i).getThePostalLocatorDto().getCntyDescTxt());
                            existComparingLocator.append(personList.get(i).getThePostalLocatorDto().getStateCd());
                            existComparingLocator.append(personList.get(i).getThePostalLocatorDto().getStreetAddr1());
                            existComparingLocator.append(personList.get(i).getThePostalLocatorDto().getStreetAddr2());
                            existComparingLocator.append(personList.get(i).getThePostalLocatorDto().getZipCd());


                            if (!compareStringList.contains(existComparingLocator.toString().toUpperCase())) {
                                personList.get(i).getThePostalLocatorDto().setPostalLocatorUid(localUid.getSeedValueNbr());
                                postalLocatorRepository.save(new PostalLocator(personList.get(i).getThePostalLocatorDto()));
                            }
                            else {
                                newLocator = false;
                            }
                        }
                        else {
                            personList.get(i).getThePostalLocatorDto().setPostalLocatorUid(localUid.getSeedValueNbr());
                            postalLocatorRepository.save(new PostalLocator(personList.get(i).getThePostalLocatorDto()));
                        }
                        comparingString.setLength(0);
                    }
                    else {
                        personList.get(i).getThePostalLocatorDto().setPostalLocatorUid(localUid.getSeedValueNbr());
                        postalLocatorRepository.save(new PostalLocator(personList.get(i).getThePostalLocatorDto()));
                    }
                }
                else if (personList.get(i).getClassCd().equals(NEDSSConstant.TELE) && personList.get(i).getTheTeleLocatorDto() != null) {
                    newLocator = true;
                    if (!teleLocators.isEmpty()) {
                        var existingLocator = teleLocatorRepository.findByTeleLocatorUids(
                                teleLocators.stream()
                                        .map(x -> x.getLocatorUid())
                                        .collect(Collectors.toList()));
                        List<String> compareStringList = new ArrayList<>();

                        if (existingLocator.isPresent()) {
                            for(int j = 0; j < existingLocator.get().size(); j++) {
                                comparingString.setLength(0);
                                comparingString.append(existingLocator.get().get(j).getCntryCd());
                                comparingString.append(existingLocator.get().get(j).getEmailAddress());
                                comparingString.append(existingLocator.get().get(j).getExtensionTxt());
                                comparingString.append(existingLocator.get().get(j).getPhoneNbrTxt());
                                comparingString.append(existingLocator.get().get(j).getUrlAddress());
                                compareStringList.add(comparingString.toString().toUpperCase());
                            }

                            StringBuilder existComparingLocator = new StringBuilder();
                            existComparingLocator.append(personList.get(i).getTheTeleLocatorDto().getCntryCd());
                            existComparingLocator.append(personList.get(i).getTheTeleLocatorDto().getEmailAddress());
                            existComparingLocator.append(personList.get(i).getTheTeleLocatorDto().getExtensionTxt());
                            existComparingLocator.append(personList.get(i).getTheTeleLocatorDto().getPhoneNbrTxt());
                            existComparingLocator.append(personList.get(i).getTheTeleLocatorDto().getUrlAddress());

                            if (!compareStringList.contains(existComparingLocator.toString().toUpperCase())) {
                                personList.get(i).getTheTeleLocatorDto().setTeleLocatorUid(localUid.getSeedValueNbr());
                                teleLocatorRepository.save(new TeleLocator(personList.get(i).getTheTeleLocatorDto()));
                            }
                            else {
                                newLocator = false;
                            }
                        }
                        else {
                            personList.get(i).getTheTeleLocatorDto().setTeleLocatorUid(localUid.getSeedValueNbr());
                            teleLocatorRepository.save(new TeleLocator(personList.get(i).getTheTeleLocatorDto()));
                        }

                        comparingString.setLength(0);
                    }
                    else {
                        personList.get(i).getTheTeleLocatorDto().setTeleLocatorUid(localUid.getSeedValueNbr());
                        teleLocatorRepository.save(new TeleLocator(personList.get(i).getTheTeleLocatorDto()));
                    }
                }

                // ONLY persist new participation locator if new locator actually exist
                if (newLocator) {
                    personList.get(i).setEntityUid(uid);
                    personList.get(i).setLocatorUid(localUid.getSeedValueNbr());

                    if (personList.get(i).getVersionCtrlNbr() == null) {
                        personList.get(i).setVersionCtrlNbr(1);
                    }
                    entityLocatorParticipationRepository.save(new EntityLocatorParticipation(personList.get(i)));
                }

            }
        }
    }

    @Transactional
    public void createEntityLocatorParticipation(Collection<EntityLocatorParticipationDto> locatorCollection, Long uid) throws DataProcessingException {
        ArrayList<EntityLocatorParticipationDto>  personList = (ArrayList<EntityLocatorParticipationDto> ) locatorCollection;
        try {
            for(int i = 0; i < personList.size(); i++) {
                boolean inserted = false;
                LocalUidGenerator localUid = odseIdGeneratorService.getLocalIdAndUpdateSeed(LocalIdClass.PERSON);
                if (personList.get(i).getClassCd().equals(NEDSSConstant.PHYSICAL) && personList.get(i).getThePhysicalLocatorDto() != null) {
                    personList.get(i).getThePhysicalLocatorDto().setPhysicalLocatorUid(localUid.getSeedValueNbr());
                    physicalLocatorRepository.save(new PhysicalLocator(personList.get(i).getThePhysicalLocatorDto()));
                    inserted = true;
                }
                else if (personList.get(i).getClassCd().equals(NEDSSConstant.POSTAL) && personList.get(i).getThePostalLocatorDto() != null) {
                    personList.get(i).getThePostalLocatorDto().setPostalLocatorUid(localUid.getSeedValueNbr());
                    postalLocatorRepository.save(new PostalLocator(personList.get(i).getThePostalLocatorDto()));
                    inserted = true;
                }
                else if (personList.get(i).getClassCd().equals(NEDSSConstant.TELE) && personList.get(i).getTheTeleLocatorDto() != null) {
                    personList.get(i).getTheTeleLocatorDto().setTeleLocatorUid(localUid.getSeedValueNbr());
                    teleLocatorRepository.save(new TeleLocator(personList.get(i).getTheTeleLocatorDto()));
                    inserted = true;
                }

                if (inserted) {
                    personList.get(i).setEntityUid(uid);
                    personList.get(i).setLocatorUid(localUid.getSeedValueNbr());

                    if (personList.get(i).getVersionCtrlNbr() == null) {
                        personList.get(i).setVersionCtrlNbr(1);
                    }
                    entityLocatorParticipationRepository.save(new EntityLocatorParticipation(personList.get(i)));
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
