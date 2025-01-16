package gov.cdc.dataprocessing.service.implementation.person;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityDetailLogDto;
import gov.cdc.dataprocessing.model.dto.matching.EdxPatientMatchDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.model.dto.person.PersonEthnicGroupDto;
import gov.cdc.dataprocessing.model.dto.person.PersonNameDto;
import gov.cdc.dataprocessing.model.dto.person.PersonRaceDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.PersonName;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.PersonRace;
import gov.cdc.dataprocessing.service.interfaces.person.INokMatchingService;
import gov.cdc.dataprocessing.service.interfaces.person.IPatientMatchingService;
import gov.cdc.dataprocessing.service.interfaces.person.IPersonService;
import gov.cdc.dataprocessing.service.interfaces.person.IProviderMatchingService;
import gov.cdc.dataprocessing.service.interfaces.uid_generator.IUidService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.parser.Entity;
import java.util.*;

@Service
@Slf4j
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
 1192 - duplicate literal
 135 - for loop
 117 - naming
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192", "java:S135", "java:S117"})
public class PersonService implements IPersonService {
    private final IPatientMatchingService patientMatchingService;
    private final INokMatchingService nokMatchingService;
    private final IProviderMatchingService providerMatchingService;
    private final IUidService uidService;
    public PersonService(
            PatientMatchingService patientMatchingService,
            INokMatchingService nokMatchingService,
            IProviderMatchingService providerMatchingService,
            IUidService uidService) {

        this.patientMatchingService = patientMatchingService;
        this.nokMatchingService = nokMatchingService;
        this.providerMatchingService = providerMatchingService;
        this.uidService = uidService;
    }

    @Transactional
    public PersonContainer processingNextOfKin(LabResultProxyContainer labResultProxyContainer, PersonContainer personContainer) throws DataProcessingException {
        try {
            long falseUid = personContainer.thePersonDto.getPersonUid();
            nokMatchingService.getMatchingNextOfKin(personContainer);

            if (personContainer.getThePersonDto().getPersonUid() != null) {

                uidService.setFalseToNewPersonAndOrganization(labResultProxyContainer, falseUid, personContainer.getThePersonDto().getPersonUid());
                personContainer.setItNew(false);
                personContainer.setItDirty(false);
                personContainer.getThePersonDto().setItNew(false);
                personContainer.getThePersonDto().setItDirty(false);

            }
            return personContainer;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    @Transactional
    public PersonContainer processingPatient(LabResultProxyContainer labResultProxyContainer, EdxLabInformationDto edxLabInformationDto, PersonContainer personContainer) throws DataProcessingException {
        try {
            long falseUid = personContainer.thePersonDto.getPersonUid();
            Long personUid;
            EdxPatientMatchDto edxPatientMatchFoundDT = null;

            personContainer.setRole(EdxELRConstant.ELR_PATIENT_CD);

            if(edxLabInformationDto.getPatientUid()>0){
                personUid= edxLabInformationDto.getPatientUid();
            }
            else{
                //NOTE: Mathing Patient
                //NOTE: This matching also persist patient accordingly
                //NOTE: Either new or existing patient, it will be processed within this method
                edxPatientMatchFoundDT = patientMatchingService.getMatchingPatient(personContainer);
                if (edxPatientMatchFoundDT != null) {
                  edxLabInformationDto.setMultipleSubjectMatch(edxPatientMatchFoundDT.isMultipleMatch());
                } else {
                  edxLabInformationDto.setMultipleSubjectMatch(false);
                }
                personUid = personContainer.getThePersonDto().getPersonUid();
            }

            if (personUid != null) {
                uidService.setFalseToNewPersonAndOrganization(labResultProxyContainer, falseUid, personUid);
                personContainer.setItNew(false);
                personContainer.setItDirty(false);
                personContainer.getThePersonDto().setItNew(false);
                personContainer.getThePersonDto().setItDirty(false);
                PersonNameDto personName = parsingPersonName(personContainer);
                String lastName = personName.getLastNm();
                String firstName = personName.getFirstNm();
                edxLabInformationDto.setEntityName(firstName + " " + lastName);
            }

            if(edxPatientMatchFoundDT!=null && !edxPatientMatchFoundDT.isMultipleMatch() && personContainer.getPatientMatchedFound()) // NOSONAR
            {
                edxLabInformationDto.setPatientMatch(true);
            }
            if(personContainer.getThePersonDto().getPersonParentUid()!=null){
                edxLabInformationDto.setPersonParentUid(personContainer.getThePersonDto().getPersonParentUid());
            }

            return personContainer;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    @Transactional
    public PersonContainer processingProvider(LabResultProxyContainer labResultProxyContainer, EdxLabInformationDto edxLabInformationDto, PersonContainer personContainer, boolean orderingProviderIndicator) throws DataProcessingException {
        try {
            long falseUid = personContainer.thePersonDto.getPersonUid();

            if (personContainer.getRole() != null && personContainer.getRole().equalsIgnoreCase(EdxELRConstant.ELR_OP_CD)) {
                orderingProviderIndicator = true;
            }

            personContainer.setRole(EdxELRConstant.ELR_PROV_CD);
            EDXActivityDetailLogDto eDXActivityDetailLogDto;
            eDXActivityDetailLogDto = providerMatchingService.getMatchingProvider(personContainer);
            String personUId;
            personUId = eDXActivityDetailLogDto.getRecordId();
            if (personUId != null) {
                long uid = Long.parseLong(personUId);
                uidService.setFalseToNewPersonAndOrganization(labResultProxyContainer, falseUid,uid);
                personContainer.setItNew(false);
                personContainer.setItDirty(false);
                personContainer.getThePersonDto().setItNew(false);
                personContainer.getThePersonDto().setItDirty(false);
            }
            if (orderingProviderIndicator)
            {
                edxLabInformationDto.setProvider(true);
                return personContainer;
            }

        } catch (Exception e) {
            edxLabInformationDto.setProvider(false);
            throw new DataProcessingException(e.getMessage(), e);
        }
        return null;
    }

    private PersonNameDto parsingPersonName(PersonContainer personContainer) throws DataProcessingException {
        Collection<PersonNameDto> personNames = personContainer.getThePersonNameDtoCollection();
        for (PersonNameDto personName : personNames) {
            if (personName.getNmUseCd().equals("L")) {
                return personName;
            }
        }
        throw new DataProcessingException("No name use code \"L\" in PersonVO");
    }

    public Long getMatchedPersonUID(LabResultProxyContainer matchedlabResultProxyVO) {
        Long matchedPersonUid = null;
        Collection<PersonContainer> personCollection = matchedlabResultProxyVO.getThePersonContainerCollection();
        if(personCollection!=null){

            for (PersonContainer personVO : personCollection) {
                String perDomainCdStr = personVO.getThePersonDto().getCdDescTxt();
                if (perDomainCdStr != null && perDomainCdStr.equalsIgnoreCase(EdxELRConstant.ELR_PATIENT_DESC)) {
                    matchedPersonUid = personVO.getThePersonDto().getPersonUid();
                }
            }
        }
        return matchedPersonUid;
    }
    @SuppressWarnings("java:S3776")
    public void updatePersonELRUpdate(LabResultProxyContainer labResultProxyVO, LabResultProxyContainer matchedLabResultProxyVO){
        PersonDto matchedPersonDT;
        Long matchedPersonUid = null;
        Long matchedPersonParentUid = null;
        String matchedLocalId = null;
        Integer matchedVersionCtNo = null;
        Collection<PersonNameDto> updatedPersonNameCollection = new ArrayList<>();
        Collection<PersonRaceDto> updatedPersonRaceCollection = new ArrayList<>();
        Collection<PersonEthnicGroupDto> updatedPersonEthnicGroupCollection = new ArrayList<>();
        Collection<EntityLocatorParticipationDto> updatedtheEntityLocatorParticipationDTCollection  = new ArrayList<>();
        Collection<EntityIdDto> updatedtheEntityIdDTCollection = new ArrayList<>();
        HashMap<Object,Object> hm = new HashMap<>();
        HashMap<Object,Object> ethnicGroupHm = new HashMap<>();
        int nameSeq=0;
        int entityIdSeq=0;


        Collection<PersonContainer> personCollection = matchedLabResultProxyVO.getThePersonContainerCollection();
        if(personCollection!=null){
            for (PersonContainer personVO : personCollection) {
                String perDomainCdStr = personVO.getThePersonDto().getCdDescTxt();
                if (perDomainCdStr != null && perDomainCdStr.equalsIgnoreCase(EdxELRConstant.ELR_PATIENT_DESC)) {
                    matchedPersonDT = personVO.getThePersonDto();
                    matchedPersonUid = matchedPersonDT.getPersonUid();
                    matchedPersonParentUid = matchedPersonDT.getPersonParentUid();
                    matchedLocalId = matchedPersonDT.getLocalId();
                    matchedVersionCtNo = matchedPersonDT.getVersionCtrlNbr();
                }
                if (perDomainCdStr != null && perDomainCdStr.equalsIgnoreCase(EdxELRConstant.ELR_PATIENT_DESC)) {
                    if (personVO.getThePersonNameDtoCollection() != null && !personVO.getThePersonNameDtoCollection().isEmpty()) {
                        for (PersonNameDto personNameDT : personVO.getThePersonNameDtoCollection()) {
                            personNameDT.setItDelete(true);
                            personNameDT.setItDirty(false);
                            personNameDT.setItNew(false);
                            if (personNameDT.getPersonNameSeq() > nameSeq) {
                                nameSeq = personNameDT.getPersonNameSeq();
                            }
                            updatedPersonNameCollection.add(personNameDT);
                        }
                    }
                    if (personVO.getThePersonRaceDtoCollection() != null && !personVO.getThePersonRaceDtoCollection().isEmpty()) {
                        for (PersonRaceDto personRaceDT : personVO.getThePersonRaceDtoCollection()) {
                            personRaceDT.setItDelete(true);
                            personRaceDT.setItDirty(false);
                            personRaceDT.setItNew(false);
                            hm.put(personRaceDT.getRaceCd(), personRaceDT);
                            updatedPersonRaceCollection.add(personRaceDT);
                        }
                    }




                    if (personVO.getThePersonEthnicGroupDtoCollection() != null && !personVO.getThePersonEthnicGroupDtoCollection().isEmpty()) {
                        for (PersonEthnicGroupDto personEthnicGroupDT : personVO.getThePersonEthnicGroupDtoCollection()) {
                            personEthnicGroupDT.setItDelete(true);
                            personEthnicGroupDT.setItDirty(false);
                            personEthnicGroupDT.setItNew(false);
                            ethnicGroupHm.put(personEthnicGroupDT.getEthnicGroupCd(), personEthnicGroupDT);
                            updatedPersonEthnicGroupCollection.add(personEthnicGroupDT);
                        }
                    }
                    if (personVO.getTheEntityIdDtoCollection() != null && !personVO.getTheEntityIdDtoCollection().isEmpty()) {
                        for (EntityIdDto entityIDDT : personVO.getTheEntityIdDtoCollection()) {
                            entityIDDT.setItDelete(true);
                            entityIDDT.setItDirty(false);
                            entityIDDT.setItNew(false);
                            if (entityIDDT.getEntityIdSeq() > entityIdSeq) {
                                entityIdSeq = entityIDDT.getEntityIdSeq();
                            }
                            updatedtheEntityIdDTCollection.add(entityIDDT);
                        }
                    }
                    if (personVO.getTheEntityLocatorParticipationDtoCollection() != null && !personVO.getTheEntityLocatorParticipationDtoCollection().isEmpty()) {
                        for (EntityLocatorParticipationDto entityLocPartDT : personVO.getTheEntityLocatorParticipationDtoCollection()) {
                            entityLocPartDT.setItDelete(true);
                            entityLocPartDT.setItDirty(false);
                            entityLocPartDT.setItNew(false);

                            if (entityLocPartDT.getThePostalLocatorDto() != null) {
                                entityLocPartDT.getThePostalLocatorDto().setItDelete(true);
                                entityLocPartDT.getThePostalLocatorDto().setItDirty(false);
                                entityLocPartDT.getThePostalLocatorDto().setItNew(false);
                            }
                            if (entityLocPartDT.getTheTeleLocatorDto() != null) {
                                entityLocPartDT.getTheTeleLocatorDto().setItDelete(true);
                                entityLocPartDT.getTheTeleLocatorDto().setItDirty(false);
                                entityLocPartDT.getTheTeleLocatorDto().setItNew(false);
                            }
                            if (entityLocPartDT.getThePhysicalLocatorDto() != null) {
                                entityLocPartDT.getThePhysicalLocatorDto().setItDelete(true);
                                entityLocPartDT.getThePhysicalLocatorDto().setItDirty(false);
                                entityLocPartDT.getThePhysicalLocatorDto().setItNew(false);
                            }
                            updatedtheEntityLocatorParticipationDTCollection.add(entityLocPartDT);
                        }
                    }
                }
            }
        }


        if(labResultProxyVO.getThePersonContainerCollection()!=null){
            for (PersonContainer personVO : labResultProxyVO.getThePersonContainerCollection()) {

                String perDomainCdStr = personVO.getThePersonDto().getCdDescTxt();
                if (perDomainCdStr != null && perDomainCdStr.equalsIgnoreCase(EdxELRConstant.ELR_PATIENT_DESC)) {

                    personVO.getThePersonDto().setPersonParentUid(matchedPersonParentUid);
                    personVO.getThePersonDto().setLocalId(matchedLocalId);
                    personVO.getThePersonDto().setVersionCtrlNbr(matchedVersionCtNo);
                    personVO.getThePersonDto().setItDirty(true);
                    personVO.getThePersonDto().setItNew(false);
                    personVO.setItNew(false);
                    personVO.setItDirty(true);

                    if (perDomainCdStr != null && perDomainCdStr.equalsIgnoreCase(EdxELRConstant.ELR_PATIENT_DESC)) {
                        if (personVO.getThePersonNameDtoCollection() != null && !personVO.getThePersonNameDtoCollection().isEmpty()) {
                            for (PersonNameDto personNameDT : personVO.getThePersonNameDtoCollection()) {
                                personNameDT.setItNew(true);
                                personNameDT.setItDirty(false);
                                personNameDT.setItDelete(false);
                                personNameDT.setPersonUid(matchedPersonUid);
                                personNameDT.setPersonNameSeq(++nameSeq);
                            }
                        }
                        if (personVO.getThePersonNameDtoCollection() == null) {
                            personVO.setThePersonNameDtoCollection(new ArrayList<>());
                        }
                        personVO.getThePersonNameDtoCollection().addAll(updatedPersonNameCollection);

                        if (personVO.getThePersonRaceDtoCollection() != null && !personVO.getThePersonRaceDtoCollection().isEmpty()) {
                            for (PersonRaceDto personRaceDT : personVO.getThePersonRaceDtoCollection()) {
                                if (hm.get(personRaceDT.getRaceCd()) != null) {
                                    personRaceDT.setItDirty(true);
                                    personRaceDT.setItNew(false);
                                    personRaceDT.setItDelete(false);
                                    personRaceDT.setPersonUid(matchedPersonUid);
                                } else {
                                    personRaceDT.setItNew(true);
                                    personRaceDT.setItDirty(false);
                                    personRaceDT.setItDelete(false);
                                    personRaceDT.setPersonUid(matchedPersonUid);

                                }
                            }
                        }
                        Collection<PersonRaceDto> personRaceCollection = personVO.getThePersonRaceDtoCollection();
                        Iterator it2 = hm.entrySet().iterator();
                        boolean found = false;
                        while (it2.hasNext()) {
                            Map.Entry pair = (Map.Entry) it2.next();
                            PersonRaceDto personRaceDT2 = (PersonRaceDto) pair.getValue();
                            if (personRaceCollection != null && !personRaceCollection.isEmpty()) {
                                for (PersonRaceDto personRaceDT : personRaceCollection) {
                                    if (personRaceDT2.getRaceCd().equals(personRaceDT.getRaceCd())) {
                                        found = true;
                                        break;
                                    }
                                }
                            }
                            if (!found) {
                                personRaceDT2.setItDelete(true);
                                personRaceDT2.setItDirty(false);
                                personRaceDT2.setItNew(false);
                                personVO.getThePersonRaceDtoCollection().add(personRaceDT2);
                            }
                        }


                        if (personVO.getThePersonRaceDtoCollection() == null || personVO.getThePersonRaceDtoCollection().isEmpty()
                        ) {
                            personVO.setThePersonRaceDtoCollection(new ArrayList<>());
                            personVO.getThePersonRaceDtoCollection().addAll(updatedPersonRaceCollection);
                        }
                        if (personVO.getThePersonEthnicGroupDtoCollection() != null && !personVO.getThePersonEthnicGroupDtoCollection().isEmpty()) {
                            for (PersonEthnicGroupDto personEthnicGroupDT : personVO.getThePersonEthnicGroupDtoCollection()) {
                                if (ethnicGroupHm.get(personEthnicGroupDT.getEthnicGroupCd()) != null) {
                                    personEthnicGroupDT.setItDirty(true);
                                    personEthnicGroupDT.setItNew(false);
                                    personEthnicGroupDT.setItDelete(false);
                                    personEthnicGroupDT.setPersonUid(matchedPersonUid);
                                } else {
                                    personEthnicGroupDT.setItNew(true);
                                    personEthnicGroupDT.setItDirty(false);
                                    personEthnicGroupDT.setItDelete(false);
                                    personEthnicGroupDT.setPersonUid(matchedPersonUid);

                                }
                            }
                        }

                        if (personVO.getThePersonEthnicGroupDtoCollection() == null || personVO.getThePersonEthnicGroupDtoCollection().isEmpty()
                        ) {
                            personVO.setThePersonEthnicGroupDtoCollection(new ArrayList<>());
                            personVO.getThePersonEthnicGroupDtoCollection().addAll(updatedPersonEthnicGroupCollection);
                        }
                        if (personVO.getTheEntityIdDtoCollection() != null && !personVO.getTheEntityIdDtoCollection().isEmpty()) {
                            for (EntityIdDto entityIDDT : personVO.getTheEntityIdDtoCollection()) {
                                entityIDDT.setItNew(true);
                                entityIDDT.setItDirty(false);
                                entityIDDT.setItDelete(false);
                                entityIDDT.setEntityUid(matchedPersonUid);
                                entityIDDT.setEntityIdSeq(++entityIdSeq);
                            }
                        }
                        if (personVO.getTheEntityIdDtoCollection() == null) {
                            personVO.setTheEntityIdDtoCollection(new ArrayList<>());
                        }
                        personVO.getTheEntityIdDtoCollection().addAll(updatedtheEntityIdDTCollection);


                        var cloneEntityLocatorForParentUid = new ArrayList<EntityLocatorParticipationDto>();
                        if (personVO.getTheEntityLocatorParticipationDtoCollection() != null
                            && !personVO.getTheEntityLocatorParticipationDtoCollection().isEmpty()
                        ) {
                            for (EntityLocatorParticipationDto entityLocPartDT : personVO.getTheEntityLocatorParticipationDtoCollection()) {
                                entityLocPartDT.setItNew(true);
                                entityLocPartDT.setItDirty(false);
                                entityLocPartDT.setItDelete(false);
                                entityLocPartDT.setEntityUid(matchedPersonUid);

                                if (entityLocPartDT.getThePostalLocatorDto() != null) {
                                    entityLocPartDT.getThePostalLocatorDto().setItNew(true);
                                    entityLocPartDT.getThePostalLocatorDto().setItDirty(false);
                                    entityLocPartDT.getThePostalLocatorDto().setItDelete(false);
                                }
                                if (entityLocPartDT.getTheTeleLocatorDto() != null) {
                                    entityLocPartDT.getTheTeleLocatorDto().setItNew(true);
                                    entityLocPartDT.getTheTeleLocatorDto().setItDirty(false);
                                    entityLocPartDT.getTheTeleLocatorDto().setItDelete(false);
                                }
                                if (entityLocPartDT.getThePhysicalLocatorDto() != null) {
                                    entityLocPartDT.getThePhysicalLocatorDto().setItNew(true);
                                    entityLocPartDT.getThePhysicalLocatorDto().setItDirty(false);
                                    entityLocPartDT.getThePhysicalLocatorDto().setItDelete(false);
                                }

                                if (!Objects.equals(matchedPersonParentUid, matchedPersonUid)) {
                                    var mprRecord =  SerializationUtils.clone(entityLocPartDT);
                                    mprRecord.setEntityUid(matchedPersonParentUid);
                                    cloneEntityLocatorForParentUid.add(mprRecord);
                                }

                            }
                        }
                        if (personVO.getTheEntityLocatorParticipationDtoCollection() == null) {
                            personVO.setTheEntityLocatorParticipationDtoCollection(new ArrayList<>());
                        }
                        personVO.getTheEntityLocatorParticipationDtoCollection().addAll(updatedtheEntityLocatorParticipationDTCollection);

                        if (!cloneEntityLocatorForParentUid.isEmpty()) {
                            personVO.getTheEntityLocatorParticipationDtoCollection().addAll(cloneEntityLocatorForParentUid);
                        }

                    }
                    personVO.setRole(null);
                }
            }
        }

    }


    public void updatePersonELRUpdateV2(LabResultProxyContainer labResultProxyVO, LabResultProxyContainer matchedLabResultProxyVO){
        Collection<PersonContainer> personCollection = matchedLabResultProxyVO.getThePersonContainerCollection();
        PersonContainer personPatientMatchedVO = null;
        if(personCollection!=null){
            for (PersonContainer personVO : personCollection) {
                String perDomainCdStr = personVO.getThePersonDto().getCdDescTxt();
                if (perDomainCdStr != null && perDomainCdStr.equalsIgnoreCase(EdxELRConstant.ELR_PATIENT_DESC)) {
                    personPatientMatchedVO = personVO;
                }
            }
        }
        PersonContainer personPatientLabResultVO = null;
        if(labResultProxyVO.getThePersonContainerCollection()!=null){
            for (PersonContainer personVO : labResultProxyVO.getThePersonContainerCollection()) {
                String perDomainCdStr = personVO.getThePersonDto().getCdDescTxt();
                if (perDomainCdStr != null && perDomainCdStr.equalsIgnoreCase(EdxELRConstant.ELR_PATIENT_DESC)) {
                    personPatientLabResultVO = personVO;
                }
            }
        }
        updatePersonELRPHDCUpdate(personPatientLabResultVO, personPatientMatchedVO);

    }

    public void updatePersonELRPHDCUpdate(PersonContainer currentPersonPatientVO, PersonContainer personPatientMatchedVO){
        PersonDto matchedPersonDT = null;
        Long matchedPersonUid = null;
        Long matchedPersonParentUid = null;
        String matchedLocalId = null;
        Integer matchedVersionCtNo = null;
        Collection<PersonNameDto> updatedPersonNameCollection = new ArrayList<>();
        Collection<PersonRaceDto> updatedPersonRaceCollection = new ArrayList<>();
        Collection<PersonEthnicGroupDto> updatedPersonEthnicGroupCollection = new ArrayList<>();
        Collection<EntityLocatorParticipationDto> updatedtheEntityLocatorParticipationDTCollection  = new ArrayList<>();
        Collection<EntityIdDto> updatedtheEntityIdDTCollection = new ArrayList<>();
        HashMap<Object,Object> hm = new HashMap<>();
        HashMap<Object,Object> ethnicGroupHm = new HashMap<>();
        int nameSeq=0;
        int entityIdSeq=0;




        if(personPatientMatchedVO!=null){
            matchedPersonDT = personPatientMatchedVO.getThePersonDto();

            matchedPersonUid=matchedPersonDT.getPersonUid();
            matchedPersonParentUid = matchedPersonDT.getPersonParentUid();
            matchedLocalId = matchedPersonDT.getLocalId();
            matchedVersionCtNo = matchedPersonDT.getVersionCtrlNbr();

        }

        if(personPatientMatchedVO!=null){
            if(personPatientMatchedVO.getThePersonNameDtoCollection()!=null && personPatientMatchedVO.getThePersonNameDtoCollection().size()>0){
                for (Iterator<PersonNameDto> it = personPatientMatchedVO.getThePersonNameDtoCollection().iterator(); it.hasNext();)
                {
                    PersonNameDto personNameDT = (PersonNameDto)it.next();
                    personNameDT.setItDelete(true);
                    personNameDT.setItDirty(false);
                    personNameDT.setItNew(false);
                    if(personNameDT.getPersonNameSeq()>nameSeq)
                        nameSeq = personNameDT.getPersonNameSeq();
                    updatedPersonNameCollection.add(personNameDT);
                }
            }
            if(personPatientMatchedVO.getThePersonRaceDtoCollection()!=null && personPatientMatchedVO.getThePersonRaceDtoCollection().size()>0){
                for (Iterator<PersonRaceDto> it = personPatientMatchedVO.getThePersonRaceDtoCollection().iterator(); it.hasNext();)
                {
                    PersonRaceDto personRaceDT = (PersonRaceDto)it.next();
                    personRaceDT.setItDelete(true);
                    personRaceDT.setItDirty(false);
                    personRaceDT.setItNew(false);
                    hm.put(personRaceDT.getRaceCd(), personRaceDT);
                    updatedPersonRaceCollection.add(personRaceDT);
                }
            }
            if(personPatientMatchedVO.getThePersonEthnicGroupDtoCollection()!=null && personPatientMatchedVO.getThePersonEthnicGroupDtoCollection().size()>0){
                for (Iterator<PersonEthnicGroupDto> it = personPatientMatchedVO.getThePersonEthnicGroupDtoCollection().iterator(); it
                        .hasNext();) {
                    PersonEthnicGroupDto personEthnicGroupDT = (PersonEthnicGroupDto) it.next();
                    personEthnicGroupDT.setItDelete(true);
                    personEthnicGroupDT.setItDirty(false);
                    personEthnicGroupDT.setItNew(false);
                    ethnicGroupHm.put(personEthnicGroupDT.getEthnicGroupCd(), personEthnicGroupDT);
                    updatedPersonEthnicGroupCollection.add(personEthnicGroupDT);

                }
            }
            if(personPatientMatchedVO.getTheEntityIdDtoCollection()!=null && personPatientMatchedVO.getTheEntityIdDtoCollection().size()>0){
                for (Iterator<EntityIdDto> it = personPatientMatchedVO.getTheEntityIdDtoCollection().iterator(); it.hasNext();)
                {
                    EntityIdDto entityIDDT = (EntityIdDto)it.next();

                    entityIDDT.setItDelete(true);
                    entityIDDT.setItDirty(false);
                    entityIDDT.setItNew(false);
                    if(entityIDDT.getEntityIdSeq()>entityIdSeq)
                        entityIdSeq = entityIDDT.getEntityIdSeq();
                    updatedtheEntityIdDTCollection.add(entityIDDT);

                }
            }
            if(personPatientMatchedVO.getTheEntityLocatorParticipationDtoCollection()!=null && personPatientMatchedVO.getTheEntityLocatorParticipationDtoCollection().size()>0){
                for (Iterator<EntityLocatorParticipationDto> it = personPatientMatchedVO.getTheEntityLocatorParticipationDtoCollection().iterator(); it.hasNext();)
                {
                    EntityLocatorParticipationDto entityLocPartDT = (EntityLocatorParticipationDto)it.next();

                    entityLocPartDT.setItDelete(true);
                    entityLocPartDT.setItDirty(false);
                    entityLocPartDT.setItNew(false);

                    if(entityLocPartDT.getThePostalLocatorDto()!= null){
                        entityLocPartDT.getThePostalLocatorDto().setItDelete(true);
                        entityLocPartDT.getThePostalLocatorDto().setItDirty(false);
                        entityLocPartDT.getThePostalLocatorDto().setItNew(false);
                    }if(entityLocPartDT.getTheTeleLocatorDto()!= null){
                    entityLocPartDT.getTheTeleLocatorDto().setItDelete(true);
                    entityLocPartDT.getTheTeleLocatorDto().setItDirty(false);
                    entityLocPartDT.getTheTeleLocatorDto().setItNew(false);
                }if(entityLocPartDT.getThePostalLocatorDto()!= null){
                    entityLocPartDT.getThePostalLocatorDto().setItDelete(true);
                    entityLocPartDT.getThePostalLocatorDto().setItDirty(false);
                    entityLocPartDT.getThePostalLocatorDto().setItNew(false);
                }
                    updatedtheEntityLocatorParticipationDTCollection.add(entityLocPartDT);

                }
            }
        }




        if(currentPersonPatientVO!=null){



            //	personVO.getThePersonDT().setPersonUid(matchedPersonUid);
            currentPersonPatientVO.getThePersonDto().setPersonParentUid(matchedPersonParentUid);
            currentPersonPatientVO.getThePersonDto().setLocalId(matchedLocalId);
            currentPersonPatientVO.getThePersonDto().setVersionCtrlNbr(matchedVersionCtNo);
            currentPersonPatientVO.getThePersonDto().setItDirty(true);
            currentPersonPatientVO.getThePersonDto().setItNew(false);
            //personVO.getThePersonDT().setFirstNm(updatedFirstNm);
            //personVO.getThePersonDT().setLastNm(updatedLastNm);
            //	personVO.setIsExistingPatient(true);
            currentPersonPatientVO.setItNew(false);
            currentPersonPatientVO.setItDirty(true);
            //labResultProxyVO.setItDirty(true);
            //	labResultProxyVO.setItNew(false);


            if(currentPersonPatientVO.getThePersonNameDtoCollection()!=null && currentPersonPatientVO.getThePersonNameDtoCollection().size()>0){
                for (Iterator<PersonNameDto> it = currentPersonPatientVO.getThePersonNameDtoCollection().iterator(); it.hasNext();)
                {
                    PersonNameDto personNameDT = (PersonNameDto)it.next();
                    personNameDT.setItNew(true);
                    personNameDT.setItDirty(false);
                    personNameDT.setItDelete(false);
                    personNameDT.setPersonUid(matchedPersonUid);
                    personNameDT.setPersonNameSeq(++nameSeq);
                }
            }
            if(	currentPersonPatientVO.getThePersonNameDtoCollection() == null)
            {
                currentPersonPatientVO.setThePersonNameDtoCollection(new ArrayList<PersonNameDto>());
            }
            currentPersonPatientVO.getThePersonNameDtoCollection().addAll(updatedPersonNameCollection);

            if(currentPersonPatientVO.getThePersonRaceDtoCollection()!=null && currentPersonPatientVO.getThePersonRaceDtoCollection().size()>0){
                for (Iterator<PersonRaceDto> it = currentPersonPatientVO.getThePersonRaceDtoCollection().iterator(); it.hasNext();) {
                    PersonRaceDto personRaceDT = (PersonRaceDto) it.next();
                    if (hm.get(personRaceDT.getRaceCd()) != null) {
                        personRaceDT.setItDirty(true);
                        personRaceDT.setItNew(false);
                        personRaceDT.setItDelete(false);
                        personRaceDT.setPersonUid(matchedPersonUid);
                    } else {
                        personRaceDT.setItNew(true);
                        personRaceDT.setItDirty(false);
                        personRaceDT.setItDelete(false);
                        personRaceDT.setPersonUid(matchedPersonUid);

                    }
                }
            }
            Collection<PersonRaceDto> personRaceCollection = currentPersonPatientVO.getThePersonRaceDtoCollection();
            Iterator it2 = hm.entrySet().iterator();
            boolean found = false;
            while (it2.hasNext()) {
                Map.Entry pair = (Map.Entry)it2.next();
                PersonRaceDto personRaceDT2 = (PersonRaceDto) pair.getValue();
                if(personRaceCollection!=null && personRaceCollection.size()>0){
                    for (Iterator<PersonRaceDto> it = personRaceCollection.iterator(); it.hasNext();) {
                        PersonRaceDto personRaceDT = (PersonRaceDto) it.next();
                        if(personRaceDT2.getRaceCd().equals(personRaceDT.getRaceCd())){
                            found = true;
                            break;
                        }
                    }
                }
                if(!found){
                    personRaceDT2.setItDelete(true);
                    personRaceDT2.setItDirty(false);
                    personRaceDT2.setItNew(false);
                    //Updated to cover the scenario that getThePersonRaceDTCollection is null
                    if(currentPersonPatientVO.getThePersonRaceDtoCollection()!=null)
                        currentPersonPatientVO.getThePersonRaceDtoCollection().add(personRaceDT2);
                }
            }


            if(	currentPersonPatientVO.getThePersonRaceDtoCollection() == null || (	currentPersonPatientVO.getThePersonRaceDtoCollection() != null
                    && 	currentPersonPatientVO.getThePersonRaceDtoCollection().size() == 0)){
                currentPersonPatientVO.setThePersonNameDtoCollection(new ArrayList<>());
                currentPersonPatientVO.getThePersonRaceDtoCollection().addAll(updatedPersonRaceCollection);
            }
            if(currentPersonPatientVO.getThePersonEthnicGroupDtoCollection()!=null && currentPersonPatientVO.getThePersonEthnicGroupDtoCollection().size()>0){
                for (Iterator<PersonEthnicGroupDto> it = currentPersonPatientVO.getThePersonEthnicGroupDtoCollection().iterator(); it
                        .hasNext();) {
                    PersonEthnicGroupDto personEthnicGroupDT = (PersonEthnicGroupDto) it.next();

                    if (ethnicGroupHm.get(personEthnicGroupDT.getEthnicGroupCd()) != null) {
                        personEthnicGroupDT.setItDirty(true);
                        personEthnicGroupDT.setItNew(false);
                        personEthnicGroupDT.setItDelete(false);
                        personEthnicGroupDT.setPersonUid(matchedPersonUid);
                    } else {
                        personEthnicGroupDT.setItNew(true);
                        personEthnicGroupDT.setItDirty(false);
                        personEthnicGroupDT.setItDelete(false);
                        personEthnicGroupDT.setPersonUid(matchedPersonUid);

                    }
                }
            }
            if(	currentPersonPatientVO.getThePersonEthnicGroupDtoCollection() == null || (	currentPersonPatientVO.getThePersonEthnicGroupDtoCollection() != null
                    && 	currentPersonPatientVO.getThePersonEthnicGroupDtoCollection().size() == 0)){
                currentPersonPatientVO.setThePersonEthnicGroupDtoCollection(new ArrayList<>());
                currentPersonPatientVO.getThePersonEthnicGroupDtoCollection().addAll(updatedPersonEthnicGroupCollection);
            }
            if(currentPersonPatientVO.getTheEntityIdDtoCollection()!=null && currentPersonPatientVO.getTheEntityIdDtoCollection().size()>0){
                for (Iterator<EntityIdDto> it = currentPersonPatientVO.getTheEntityIdDtoCollection().iterator(); it.hasNext();)
                {
                    EntityIdDto entityIDDT = (EntityIdDto)it.next();

                    entityIDDT.setItNew(true);
                    entityIDDT.setItDirty(false);
                    entityIDDT.setItDelete(false);
                    entityIDDT.setEntityUid(matchedPersonUid);
                    entityIDDT.setEntityIdSeq(++entityIdSeq);

                }
            }
            if(	currentPersonPatientVO.getTheEntityIdDtoCollection() == null)
                currentPersonPatientVO.setTheEntityIdDtoCollection(new ArrayList<>());
            currentPersonPatientVO.getTheEntityIdDtoCollection().addAll(updatedtheEntityIdDTCollection);


            if(currentPersonPatientVO.getTheEntityLocatorParticipationDtoCollection()!=null
                    && currentPersonPatientVO.getTheEntityLocatorParticipationDtoCollection().size()>0){
                for (Iterator<EntityLocatorParticipationDto> it = currentPersonPatientVO.getTheEntityLocatorParticipationDtoCollection().iterator(); it.hasNext();)
                {
                    EntityLocatorParticipationDto entityLocPartDT = (EntityLocatorParticipationDto)it.next();

                    entityLocPartDT.setItNew(true);
                    entityLocPartDT.setItDirty(false);
                    entityLocPartDT.setItDelete(false);
                    entityLocPartDT.setEntityUid(matchedPersonUid);

                    if(entityLocPartDT.getThePostalLocatorDto()!= null){
                        entityLocPartDT.getThePostalLocatorDto().setItNew(true);
                        entityLocPartDT.getThePostalLocatorDto().setItDirty(false);
                        entityLocPartDT.getThePostalLocatorDto().setItDelete(false);
                    }if(entityLocPartDT.getTheTeleLocatorDto()!= null){
                    entityLocPartDT.getTheTeleLocatorDto().setItNew(true);
                    entityLocPartDT.getTheTeleLocatorDto().setItDirty(false);
                    entityLocPartDT.getTheTeleLocatorDto().setItDelete(false);
                }if(entityLocPartDT.getThePhysicalLocatorDto()!= null){
                    entityLocPartDT.getThePhysicalLocatorDto().setItNew(true);
                    entityLocPartDT.getThePhysicalLocatorDto().setItDirty(false);
                    entityLocPartDT.getThePhysicalLocatorDto().setItDelete(false);
                }

                }
            }
            if(	currentPersonPatientVO.getTheEntityLocatorParticipationDtoCollection() == null)
                currentPersonPatientVO.setTheEntityLocatorParticipationDtoCollection(new ArrayList<>());
            currentPersonPatientVO.getTheEntityLocatorParticipationDtoCollection().addAll(updatedtheEntityLocatorParticipationDTCollection);



            currentPersonPatientVO.setRole(null);



        }


    }
}
