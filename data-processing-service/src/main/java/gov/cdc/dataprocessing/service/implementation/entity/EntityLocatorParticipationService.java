package gov.cdc.dataprocessing.service.implementation.entity;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.EntityLocatorJdbcRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityLocatorParticipation;
import gov.cdc.dataprocessing.repository.nbs.odse.model.locator.PhysicalLocator;
import gov.cdc.dataprocessing.repository.nbs.odse.model.locator.PostalLocator;
import gov.cdc.dataprocessing.repository.nbs.odse.model.locator.TeleLocator;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.entity.EntityLocatorParticipationRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.locator.PhysicalLocatorRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.locator.PostalLocatorRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.locator.TeleLocatorRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.person.PersonRepository;
import gov.cdc.dataprocessing.service.implementation.uid_generator.UidPoolManager;
import gov.cdc.dataprocessing.service.interfaces.entity.IEntityLocatorParticipationService;
import gov.cdc.dataprocessing.utilities.component.jdbc.DataModifierReposJdbc;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service

public class EntityLocatorParticipationService implements IEntityLocatorParticipationService {
    @Value("${service.timezone}")
    private String tz = "UTC";

    private final UidPoolManager uidPoolManager;

    private final PersonRepository personRepository;
    private final TeleLocatorRepository teleLocatorRepository;
    private final PostalLocatorRepository postalLocatorRepository;
    private final PhysicalLocatorRepository physicalLocatorRepository;
    private final DataModifierReposJdbc dataModifierReposJdbc;

    private final EntityLocatorJdbcRepository entityLocatorJdbcRepository;
    private final EntityLocatorParticipationRepository entityLocatorParticipationRepository;


    public EntityLocatorParticipationService(UidPoolManager uidPoolManager, PersonRepository personRepository,
                                             EntityLocatorParticipationRepository entityLocatorParticipationRepository,
                                             TeleLocatorRepository teleLocatorRepository,
                                             PostalLocatorRepository postalLocatorRepository,
                                             PhysicalLocatorRepository physicalLocatorRepository,
                                             DataModifierReposJdbc dataModifierReposJdbc,
                                             EntityLocatorJdbcRepository entityLocatorJdbcRepository) {
        this.uidPoolManager = uidPoolManager;
        this.personRepository = personRepository;
        this.entityLocatorParticipationRepository = entityLocatorParticipationRepository;
        this.teleLocatorRepository = teleLocatorRepository;
        this.postalLocatorRepository = postalLocatorRepository;
        this.physicalLocatorRepository = physicalLocatorRepository;
        this.dataModifierReposJdbc = dataModifierReposJdbc;
        this.entityLocatorJdbcRepository = entityLocatorJdbcRepository;
    }

    @SuppressWarnings({"java:S3776", "java:S125"})
    protected void deleteEntityLocatorParticipation(Collection<EntityLocatorParticipationDto> locatorCollection, Long patientUid) {
        var deletePostal = locatorCollection.stream().filter(x -> x.getClassCd().equalsIgnoreCase(NEDSSConstant.POSTAL) && x.isItDelete()).toList();
        StringBuilder comparingString = new StringBuilder();
        ArrayList<String> comparingStrList = new ArrayList<>();
        if (!deletePostal.isEmpty()) {


            var personRes = personRepository.findByPersonUid(patientUid);
            Long parentUid = null;
            if (personRes.isPresent()) {
                parentUid = personRes.get().get(0).getPersonParentUid();
            }
            var postalRevision = postalLocatorRepository.findByPostalLocatorUids(deletePostal.stream().map(EntityLocatorParticipationDto::getLocatorUid).toList());
            if (parentUid != null && postalRevision.isPresent()) {
                var entityMprEntityRes = entityLocatorJdbcRepository.findByEntityUid(parentUid);
                var entityMprRes = entityLocatorParticipationRepository.findLocatorUidsByEntityUid(parentUid);
                if (entityMprRes.isPresent()) {
                    var postalMpr = postalLocatorRepository.findByPostalLocatorUids(entityMprRes.get());
                    if (postalMpr.isPresent()) {
                        for(var revision : postalRevision.get()) {
                            comparingString.setLength(0);
                            comparingString.append(revision.getCityCd());
                            comparingString.append(revision.getCityDescTxt());
                            comparingString.append(revision.getCntryCd());
                            comparingString.append(revision.getCntryDescTxt());
                            comparingString.append(revision.getCntyCd());
                            comparingString.append(revision.getCntyDescTxt());
                            comparingString.append(revision.getStateCd());
                            comparingString.append(revision.getStreetAddr1());
                            comparingString.append(revision.getStreetAddr2());
                            comparingString.append(revision.getZipCd());
                            comparingStrList.add(comparingString.toString().toUpperCase());
                        }




                        for(var mpr : postalMpr.get()) {
                            comparingString.setLength(0);
                            comparingString.append(mpr.getCityCd());
                            comparingString.append(mpr.getCityDescTxt());
                            comparingString.append(mpr.getCntryCd());
                            comparingString.append(mpr.getCntryDescTxt());
                            comparingString.append(mpr.getCntyCd());
                            comparingString.append(mpr.getCntyDescTxt());
                            comparingString.append(mpr.getStateCd());
                            comparingString.append(mpr.getStreetAddr1());
                            comparingString.append(mpr.getStreetAddr2());
                            comparingString.append(mpr.getZipCd());

                            var birCheck = entityMprEntityRes.stream().filter(x -> Objects.equals(x.getLocatorUid(), mpr.getPostalLocatorUid())
                            && x.getUseCd().equalsIgnoreCase("BIR")).toList();
                            // Comparing String of MPR matched with Incoming Revision Then Do Delete
                            if (comparingStrList.contains(comparingString.toString().toUpperCase()) && !birCheck.isEmpty()) {
                                dataModifierReposJdbc.deletePostalLocatorById(mpr.getPostalLocatorUid());
                                dataModifierReposJdbc.deleteLocatorById(parentUid, mpr.getPostalLocatorUid());
                            }
                        }
                    }
                }
            }


            for(var deleteRevision: deletePostal) {

                if (deleteRevision.getUseCd().equalsIgnoreCase("BIR")) {
                    dataModifierReposJdbc.deletePostalLocatorById(deleteRevision.getLocatorUid());
                    dataModifierReposJdbc.deleteLocatorById(deleteRevision.getEntityUid(), deleteRevision.getLocatorUid());
                }
            }
        }

        locatorCollection.removeIf(x -> x.getClassCd().equalsIgnoreCase(NEDSSConstant.POSTAL) && x.isItDelete() && x.getUseCd().equalsIgnoreCase("BIR"));

    }

    @SuppressWarnings({"java:S6541", "java:S3776", "java:S6204"})
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
                    .toList();
            postalLocators = entityLocatorParticipations.stream().filter(x -> x.getClassCd()
                            .equalsIgnoreCase(NEDSSConstant.POSTAL))
                    .sorted(Comparator.comparing(EntityLocatorParticipation::getRecordStatusTime).reversed())
                    .toList();
            teleLocators = entityLocatorParticipations.stream().filter(x -> x.getClassCd()
                            .equalsIgnoreCase(NEDSSConstant.TELE))
                    .sorted(Comparator.comparing(EntityLocatorParticipation::getRecordStatusTime).reversed())
                    .toList();
            // This remove to be deleted entity from the Participation
            deleteEntityLocatorParticipation(locatorCollection, patientUid);
            StringBuilder comparingString = new StringBuilder();
            for (EntityLocatorParticipationDto entityLocatorParticipationDto : personList) {
                var localUid = uidPoolManager.getNextUid(LocalIdClass.PERSON,true);
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
                                entityLocatorParticipationDto.getThePhysicalLocatorDto().setPhysicalLocatorUid(localUid.getGaTypeUid().getSeedValueNbr());
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
                            entityLocatorParticipationDto.getThePhysicalLocatorDto().setPhysicalLocatorUid(localUid.getGaTypeUid().getSeedValueNbr());
                            physicalLocatorRepository.save(new PhysicalLocator(entityLocatorParticipationDto.getThePhysicalLocatorDto()));
                        }

                        comparingString.setLength(0);
                    }
                    else
                    {
                        uid = entityLocatorParticipationDto.getEntityUid();
                        entityLocatorParticipationDto.getThePhysicalLocatorDto().setPhysicalLocatorUid(localUid.getGaTypeUid().getSeedValueNbr());
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
                                entityLocatorParticipationDto.getThePostalLocatorDto().setPostalLocatorUid(localUid.getGaTypeUid().getSeedValueNbr());
                                entityLocatorParticipationDto.getThePostalLocatorDto().setRecordStatusCd(NEDSSConstant.ACTIVE);
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
                            entityLocatorParticipationDto.getThePostalLocatorDto().setPostalLocatorUid(localUid.getGaTypeUid().getSeedValueNbr());
                            entityLocatorParticipationDto.getThePostalLocatorDto().setRecordStatusCd(NEDSSConstant.ACTIVE);
                            postalLocatorRepository.save(new PostalLocator(entityLocatorParticipationDto.getThePostalLocatorDto()));
                        }
                        comparingString.setLength(0);
                    }
                    else
                    {
                        uid = entityLocatorParticipationDto.getEntityUid();
                        entityLocatorParticipationDto.getThePostalLocatorDto().setPostalLocatorUid(localUid.getGaTypeUid().getSeedValueNbr());
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
                                entityLocatorParticipationDto.getTheTeleLocatorDto().setTeleLocatorUid(localUid.getGaTypeUid().getSeedValueNbr());
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
                            entityLocatorParticipationDto.getTheTeleLocatorDto().setTeleLocatorUid(localUid.getGaTypeUid().getSeedValueNbr());
                            teleLocatorRepository.save(new TeleLocator(entityLocatorParticipationDto.getTheTeleLocatorDto()));
                        }

                        comparingString.setLength(0);
                    }
                    else
                    {
                        uid = entityLocatorParticipationDto.getEntityUid();
                        entityLocatorParticipationDto.getTheTeleLocatorDto().setTeleLocatorUid(localUid.getGaTypeUid().getSeedValueNbr());
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
                    entityLocatorParticipationDto.setLocatorUid(localUid.getGaTypeUid().getSeedValueNbr());

                    if (entityLocatorParticipationDto.getVersionCtrlNbr() == null) {
                        entityLocatorParticipationDto.setVersionCtrlNbr(1);
                    }
                    entityLocatorParticipationDto.setStatusCd("A");
                    entityLocatorParticipationDto.setRecordStatusCd(NEDSSConstant.ACTIVE);
                    entityLocatorParticipationRepository.save(new EntityLocatorParticipation(entityLocatorParticipationDto, tz));
                }

            }
        }
    }

    public void createEntityLocatorParticipation(Collection<EntityLocatorParticipationDto> locatorCollection, Long uid) throws DataProcessingException {
        ArrayList<EntityLocatorParticipationDto>  personList = (ArrayList<EntityLocatorParticipationDto> ) locatorCollection;
        for (EntityLocatorParticipationDto entityLocatorParticipationDto : personList) {
            boolean inserted = false;
            var localUid = uidPoolManager.getNextUid(LocalIdClass.PERSON,true);
            if (entityLocatorParticipationDto.getClassCd().equals(NEDSSConstant.PHYSICAL) && entityLocatorParticipationDto.getThePhysicalLocatorDto() != null) {
                entityLocatorParticipationDto.getThePhysicalLocatorDto().setPhysicalLocatorUid(localUid.getGaTypeUid().getSeedValueNbr());
                entityLocatorJdbcRepository.createPhysicalLocator(new PhysicalLocator(entityLocatorParticipationDto.getThePhysicalLocatorDto()));
                inserted = true;
            } else if (entityLocatorParticipationDto.getClassCd().equals(NEDSSConstant.POSTAL)
                    && entityLocatorParticipationDto.getThePostalLocatorDto() != null
            ) {
                entityLocatorParticipationDto.getThePostalLocatorDto().setPostalLocatorUid(localUid.getGaTypeUid().getSeedValueNbr());
                entityLocatorJdbcRepository.createPostalLocator(new PostalLocator(entityLocatorParticipationDto.getThePostalLocatorDto()));
                inserted = true;
            } else if (entityLocatorParticipationDto.getClassCd().equals(NEDSSConstant.TELE)
                    && entityLocatorParticipationDto.getTheTeleLocatorDto() != null
            && entityLocatorParticipationDto.getTheTeleLocatorDto().getPhoneNbrTxt() != null) {
                entityLocatorParticipationDto.getTheTeleLocatorDto().setTeleLocatorUid(localUid.getGaTypeUid().getSeedValueNbr());
                entityLocatorJdbcRepository.createTeleLocator(new TeleLocator(entityLocatorParticipationDto.getTheTeleLocatorDto()));
                inserted = true;
            }

            if (inserted) {
                entityLocatorParticipationDto.setEntityUid(uid);
                entityLocatorParticipationDto.setLocatorUid(localUid.getGaTypeUid().getSeedValueNbr());

                if (entityLocatorParticipationDto.getVersionCtrlNbr() == null) {
                    entityLocatorParticipationDto.setVersionCtrlNbr(1);
                }
                entityLocatorJdbcRepository.createEntityLocatorParticipation(new EntityLocatorParticipation(entityLocatorParticipationDto, tz));
            }

        }
    }


    public List<EntityLocatorParticipation> findEntityLocatorById(Long uid) {
       var result = entityLocatorJdbcRepository.findEntityLocatorParticipations(uid);
        return Objects.requireNonNullElse(result, Collections.emptyList());
    }
}
