package gov.cdc.dataprocessing.service.implementation.core;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.*;
import gov.cdc.dataprocessing.model.container.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.PersonContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.matching.EdxPatientMatchDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.model.dto.person.PersonEthnicGroupDto;
import gov.cdc.dataprocessing.model.dto.person.PersonNameDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.dto.person.PersonRaceDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.Person;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.PersonName;
import gov.cdc.dataprocessing.service.interfaces.matching.INokMatchingService;
import gov.cdc.dataprocessing.service.interfaces.matching.IPatientMatchingService;
import gov.cdc.dataprocessing.service.interfaces.core.IPatientService;
import gov.cdc.dataprocessing.service.interfaces.matching.IProviderMatchingService;
import gov.cdc.dataprocessing.service.implementation.matching.PatientMatchingService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.swing.text.html.parser.Entity;
import java.util.*;

@Service
@Slf4j
public class PatientService implements IPatientService {
    private static final Logger logger = LoggerFactory.getLogger(PatientService.class);

    private final IPatientMatchingService patientMatchingService;
    private final INokMatchingService nokMatchingService;
    private final IProviderMatchingService providerMatchingService;

    public PatientService(
            PatientMatchingService patientMatchingService,
            INokMatchingService nokMatchingService,
            IProviderMatchingService providerMatchingService) {

        this.patientMatchingService = patientMatchingService;
        this.nokMatchingService = nokMatchingService;
        this.providerMatchingService = providerMatchingService;
    }

    @Transactional
    public PersonContainer processingNextOfKin(LabResultProxyContainer labResultProxyContainer, PersonContainer personContainer) throws DataProcessingException {
        try {
            long falseUid = personContainer.thePersonDto.getPersonUid();
            nokMatchingService.getMatchingNextOfKin(personContainer);

            if (personContainer.getThePersonDto().getPersonUid() != null) {

                setFalseToNew(labResultProxyContainer, falseUid, personContainer.getThePersonDto().getPersonUid());
                personContainer.setItNew(false);
                personContainer.setItDirty(false);
                personContainer.getThePersonDto().setItNew(false);
                personContainer.getThePersonDto().setItDirty(false);

            }
            System.out.print("NOK ID: " + personContainer.getThePersonDto().getPersonUid());
            return personContainer;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage());
        }
    }

    @Transactional
    public PersonContainer processingPatient(LabResultProxyContainer labResultProxyContainer, EdxLabInformationDto edxLabInformationDto, PersonContainer personContainer) throws DataProcessingException {
        //TODO: Adding Logic Here
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
                edxLabInformationDto.setMultipleSubjectMatch(patientMatchingService.getMultipleMatchFound());
                personUid = personContainer.getThePersonDto().getPersonUid();
            }

            if (personUid != null) {
                setFalseToNew(labResultProxyContainer, falseUid, personUid);
                personContainer.setItNew(false);
                personContainer.setItDirty(false);
                personContainer.getThePersonDto().setItNew(false);
                personContainer.getThePersonDto().setItDirty(false);
                PersonNameDto personName = parsingPersonName(personContainer);
                String lastName = personName.getLastNm();
                String firstName = personName.getFirstNm();
                edxLabInformationDto.setEntityName(firstName + " " + lastName);
            }

            if(edxPatientMatchFoundDT!=null && !edxPatientMatchFoundDT.isMultipleMatch() && personContainer.getPatientMatchedFound()) {
                edxLabInformationDto.setPatientMatch(true);
            }
            if(personContainer.getThePersonDto().getPersonParentUid()!=null){
                edxLabInformationDto.setPersonParentUid(personContainer.getThePersonDto().getPersonParentUid());
            }

            return personContainer;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage());
        }
    }

    @Transactional
    public PersonContainer processingProvider(LabResultProxyContainer labResultProxyContainer, EdxLabInformationDto edxLabInformationDto, PersonContainer personContainer, boolean orderingProviderIndicator) throws DataProcessingException {
        //TODO: Adding Logic Here
        try {
            long falseUid = personContainer.thePersonDto.getPersonUid();
            Long personUid;
            EdxPatientMatchDto edxPatientMatchFoundDT = null;


            if (personContainer.getRole() != null && personContainer.getRole().equalsIgnoreCase(EdxELRConstant.ELR_OP_CD)) {
                orderingProviderIndicator = true;
            }

            personContainer.setRole(EdxELRConstant.ELR_PROV_CD);
            EDXActivityDetailLogDT eDXActivityDetailLogDT = new EDXActivityDetailLogDT();
            eDXActivityDetailLogDT = providerMatchingService.getMatchingProvider(personContainer);
            String personUId;
            personUId = eDXActivityDetailLogDT.getRecordId();
            if (personUId != null) {
                long uid = Long.parseLong(personUId);
                setFalseToNew(labResultProxyContainer, falseUid,uid);
                personContainer.setItNew(false);
                personContainer.setItDirty(false);
                personContainer.getThePersonDto().setItNew(false);
                personContainer.getThePersonDto().setItDirty(false);
            }
            if (orderingProviderIndicator)
            {
                return personContainer;
            }
            orderingProviderIndicator= false;

        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage());
        }
        return null;
    }

    private PersonNameDto parsingPersonName(PersonContainer personContainer) throws DataProcessingException {
        Collection<PersonNameDto> personNames = personContainer.getThePersonNameDtoCollection();
        Iterator<PersonNameDto> pnIter = personNames.iterator();
        while (pnIter.hasNext()) {
            PersonNameDto personName = pnIter.next();
            if (personName.getNmUseCd().equals("L")) {
                return personName;
            }
        }
        throw new DataProcessingException("No name use code \"L\" in PersonVO");
    }

    /**
     * TODO: Evaluation needed
     * NOTE: Not sure what this for
     * */
    private void setFalseToNew(LabResultProxyContainer labResultProxyContainer, Long falseUid, Long actualUid) throws DataProcessingException {

        try {
            Iterator<ParticipationDT> participationIterator = null;
            Iterator<ActRelationshipDT> actRelationshipIterator = null;
            Iterator<RoleDto> roleIterator = null;


            ParticipationDT participationDT = null;
            ActRelationshipDT actRelationshipDT = null;
            RoleDto roleDto = null;

            Collection<ParticipationDT> participationColl = labResultProxyContainer.getTheParticipationDTCollection();
            Collection<ActRelationshipDT> actRelationShipColl = labResultProxyContainer.getTheActRelationshipDTCollection();
            Collection<RoleDto> roleColl = labResultProxyContainer.getTheRoleDtoCollection();

            if (participationColl != null) {
                for (participationIterator = participationColl.iterator(); participationIterator.hasNext();) {
                    participationDT = (ParticipationDT) participationIterator.next();
                    logger.debug("(participationDT.getAct() comparedTo falseUid)"
                            + (participationDT.getActUid().compareTo(falseUid)));
                    if (participationDT.getActUid().compareTo(falseUid) == 0) {
                        participationDT.setActUid(actualUid);
                    }

                    if (participationDT.getSubjectEntityUid().compareTo(falseUid) == 0) {
                        participationDT.setSubjectEntityUid(actualUid);
                    }
                }
                logger.debug("participationDT.getSubjectEntityUid()"
                        + participationDT.getSubjectEntityUid());
            }

            if (actRelationShipColl != null) {
                for (actRelationshipIterator = actRelationShipColl.iterator(); actRelationshipIterator
                        .hasNext();) {
                    actRelationshipDT = (ActRelationshipDT) actRelationshipIterator.next();

                    if (actRelationshipDT.getTargetActUid().compareTo(falseUid) == 0) {
                        actRelationshipDT.setTargetActUid(actualUid);
                    }
                    if (actRelationshipDT.getSourceActUid().compareTo(falseUid) == 0) {
                        actRelationshipDT.setSourceActUid(actualUid);
                    }
                    logger.debug("ActRelationShipDT: falseUid "
                            + falseUid.toString() + " actualUid: " + actualUid);
                }
            }

            if (roleColl != null) {
                for (roleIterator = roleColl.iterator(); roleIterator.hasNext();) {
                    roleDto = (RoleDto) roleIterator.next();
                    if (roleDto.getSubjectEntityUid().compareTo(falseUid) == 0) {
                        roleDto.setSubjectEntityUid(actualUid);
                    }
                    if (roleDto.getScopingEntityUid() != null) {
                        if (roleDto.getScopingEntityUid().compareTo(falseUid) == 0) {
                            roleDto.setScopingEntityUid(actualUid);
                        }
                        logger.debug("\n\n\n(roleDT.getSubjectEntityUid() compared to falseUid)  "
                                + roleDto.getSubjectEntityUid().compareTo(
                                falseUid));
                        logger.debug("\n\n\n(roleDT.getScopingEntityUid() compared to falseUid)  "
                                + roleDto.getScopingEntityUid().compareTo(
                                falseUid));
                    }

                }
            }

        } catch (Exception e) {
            logger.error("HL7CommonLabUtil.setFalseToNew thrown for falseUid:"
                    + falseUid + "For actualUid :" + actualUid);
            throw new DataProcessingException("HL7CommonLabUtil.setFalseToNew thrown for falseUid:" + falseUid + "For actualUid :" + actualUid);
        }
    }
    
    public Long getMatchedPersonUID(LabResultProxyContainer matchedlabResultProxyVO) {

        // TODO Auto-generated method stub
        Long matchedPersonUid = null;
        Collection<PersonContainer> personCollection = matchedlabResultProxyVO.getThePersonContainerCollection();
        if(personCollection!=null){
            Iterator<PersonContainer> iterator = personCollection.iterator();

            while(iterator.hasNext()){
                PersonContainer personVO = iterator.next();
                String perDomainCdStr = personVO.getThePersonDto().getCdDescTxt();
                if(perDomainCdStr!= null && perDomainCdStr.equalsIgnoreCase(EdxELRConstant.ELR_PATIENT_DESC)){
                    matchedPersonUid = personVO.getThePersonDto().getPersonUid();
                }
            }
        }
        return matchedPersonUid;
    }

    public void updatePersonELRUpdate(LabResultProxyContainer labResultProxyVO, LabResultProxyContainer matchedLabResultProxyVO){
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


        Collection<PersonContainer> personCollection = matchedLabResultProxyVO.getThePersonContainerCollection();
        if(personCollection!=null){
            Iterator<PersonContainer> iterator = personCollection.iterator();

            while(iterator.hasNext()){
                PersonContainer personVO = iterator.next();
                String perDomainCdStr = personVO.getThePersonDto().getCdDescTxt();
                if(perDomainCdStr!= null && perDomainCdStr.equalsIgnoreCase(EdxELRConstant.ELR_PATIENT_DESC)){
                    matchedPersonDT = personVO.getThePersonDto();

                    matchedPersonUid=matchedPersonDT.getPersonUid();
                    matchedPersonParentUid = matchedPersonDT.getPersonParentUid();
                    matchedLocalId = matchedPersonDT.getLocalId();
                    matchedVersionCtNo = matchedPersonDT.getVersionCtrlNbr();


                }
                if(perDomainCdStr!= null && perDomainCdStr.equalsIgnoreCase(EdxELRConstant.ELR_PATIENT_DESC)){
                    if(personVO.getThePersonNameDtoCollection()!=null && personVO.getThePersonNameDtoCollection().size()>0){
                        for (Iterator<PersonNameDto> it = personVO.getThePersonNameDtoCollection().iterator(); it.hasNext();)
                        {
                            PersonNameDto personNameDT = it.next();
                            personNameDT.setItDelete(true);
                            personNameDT.setItDirty(false);
                            personNameDT.setItNew(false);
                            if(personNameDT.getPersonNameSeq()>nameSeq)
                            {
                                nameSeq = personNameDT.getPersonNameSeq();
                            }
                            updatedPersonNameCollection.add(personNameDT);
                        }
                    }
                    if(personVO.getThePersonRaceDtoCollection()!=null && personVO.getThePersonRaceDtoCollection().size()>0){
                        for (Iterator<PersonRaceDto> it = personVO.getThePersonRaceDtoCollection().iterator(); it.hasNext();)
                        {
                            PersonRaceDto personRaceDT = it.next();
                            personRaceDT.setItDelete(true);
                            personRaceDT.setItDirty(false);
                            personRaceDT.setItNew(false);
                            hm.put(personRaceDT.getRaceCd(), personRaceDT);
                            updatedPersonRaceCollection.add(personRaceDT);
                        }
                    }
                    if(personVO.getThePersonEthnicGroupDtoCollection()!=null && personVO.getThePersonEthnicGroupDtoCollection().size()>0){
                        for (Iterator<PersonEthnicGroupDto> it = personVO.getThePersonEthnicGroupDtoCollection().iterator(); it.hasNext();) {
                            PersonEthnicGroupDto personEthnicGroupDT =  it.next();
                            personEthnicGroupDT.setItDelete(true);
                            personEthnicGroupDT.setItDirty(false);
                            personEthnicGroupDT.setItNew(false);
                            ethnicGroupHm.put(personEthnicGroupDT.getEthnicGroupCd(), personEthnicGroupDT);
                            updatedPersonEthnicGroupCollection.add(personEthnicGroupDT);

                        }
                    }
                    if(personVO.getTheEntityIdDtoCollection()!=null && personVO.getTheEntityIdDtoCollection().size()>0){
                        for (Iterator<EntityIdDto> it = personVO.getTheEntityIdDtoCollection().iterator(); it.hasNext();)
                        {
                            EntityIdDto entityIDDT = it.next();

                            entityIDDT.setItDelete(true);
                            entityIDDT.setItDirty(false);
                            entityIDDT.setItNew(false);
                            if(entityIDDT.getEntityIdSeq()>entityIdSeq)
                            {
                                entityIdSeq = entityIDDT.getEntityIdSeq();
                            }
                            updatedtheEntityIdDTCollection.add(entityIDDT);

                        }
                    }
                    if(personVO.getTheEntityLocatorParticipationDtoCollection()!=null && personVO.getTheEntityLocatorParticipationDtoCollection().size()>0){
                        for (Iterator<EntityLocatorParticipationDto> it = personVO.getTheEntityLocatorParticipationDtoCollection().iterator(); it.hasNext();)
                        {
                            EntityLocatorParticipationDto entityLocPartDT = it.next();

                            entityLocPartDT.setItDelete(true);
                            entityLocPartDT.setItDirty(false);
                            entityLocPartDT.setItNew(false);

                            if(entityLocPartDT.getThePostalLocatorDto()!= null)
                            {
                                entityLocPartDT.getThePostalLocatorDto().setItDelete(true);
                                entityLocPartDT.getThePostalLocatorDto().setItDirty(false);
                                entityLocPartDT.getThePostalLocatorDto().setItNew(false);
                            }
                            if(entityLocPartDT.getTheTeleLocatorDto()!= null)
                            {
                            entityLocPartDT.getTheTeleLocatorDto().setItDelete(true);
                            entityLocPartDT.getTheTeleLocatorDto().setItDirty(false);
                            entityLocPartDT.getTheTeleLocatorDto().setItNew(false);
                            }
                            if(entityLocPartDT.getThePhysicalLocatorDto()!= null)
                            {
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
            Iterator<PersonContainer> iter = labResultProxyVO.getThePersonContainerCollection().iterator();
            while(iter.hasNext()){
                PersonContainer personVO =iter.next();
                String perDomainCdStr = personVO.getThePersonDto().getCdDescTxt();
                if(perDomainCdStr!= null && perDomainCdStr.equalsIgnoreCase(EdxELRConstant.ELR_PATIENT_DESC)){

                    //	personVO.getThePersonDT().setPersonUid(matchedPersonUid);
                    personVO.getThePersonDto().setPersonParentUid(matchedPersonParentUid);
                    personVO.getThePersonDto().setLocalId(matchedLocalId);
                    personVO.getThePersonDto().setVersionCtrlNbr(matchedVersionCtNo);
                    personVO.getThePersonDto().setItDirty(true);
                    personVO.getThePersonDto().setItNew(false);
                    //personVO.getThePersonDT().setFirstNm(updatedFirstNm);
                    //personVO.getThePersonDT().setLastNm(updatedLastNm);
                    //	personVO.setIsExistingPatient(true);
                    personVO.setItNew(false);
                    personVO.setItDirty(true);
                    //labResultProxyVO.setItDirty(true);
                    //	labResultProxyVO.setItNew(false);

                    if(perDomainCdStr!= null && perDomainCdStr.equalsIgnoreCase(EdxELRConstant.ELR_PATIENT_DESC)){
                        if(personVO.getThePersonNameDtoCollection()!=null && personVO.getThePersonNameDtoCollection().size()>0){
                            for (Iterator<PersonNameDto> it = personVO.getThePersonNameDtoCollection().iterator(); it.hasNext();)
                            {
                                PersonNameDto personNameDT = it.next();
                                personNameDT.setItNew(true);
                                personNameDT.setItDirty(false);
                                personNameDT.setItDelete(false);
                                personNameDT.setPersonUid(matchedPersonUid);
                                personNameDT.setPersonNameSeq(++nameSeq);
                            }
                        }
                        if(	personVO.getThePersonNameDtoCollection() == null)
                        {
                            personVO.setThePersonNameDtoCollection(new ArrayList<PersonNameDto>());
                        }
                        personVO.getThePersonNameDtoCollection().addAll(updatedPersonNameCollection);

                        if(personVO.getThePersonRaceDtoCollection()!=null && personVO.getThePersonRaceDtoCollection().size()>0){
                            for (Iterator<PersonRaceDto> it = personVO.getThePersonRaceDtoCollection().iterator(); it.hasNext();) {
                                PersonRaceDto personRaceDT =  it.next();
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
                            Map.Entry pair = (Map.Entry)it2.next();
                            PersonRaceDto personRaceDT2 =  (PersonRaceDto) pair.getValue();
                            if(personRaceCollection!=null && personRaceCollection.size()>0){
                                for (Iterator<PersonRaceDto> it = personRaceCollection.iterator(); it.hasNext();) {
                                    PersonRaceDto personRaceDT =  it.next();
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
                                personVO.getThePersonRaceDtoCollection().add(personRaceDT2);
                            }
                        }


                        if(	personVO.getThePersonRaceDtoCollection() == null || (	personVO.getThePersonRaceDtoCollection() != null
                                && personVO.getThePersonRaceDtoCollection().size() == 0)){
                            personVO.setThePersonRaceDtoCollection(new ArrayList<>());
                            personVO.getThePersonRaceDtoCollection().addAll(updatedPersonRaceCollection);
                        }
                        if(personVO.getThePersonEthnicGroupDtoCollection()!=null && personVO.getThePersonEthnicGroupDtoCollection().size()>0){
                            for (Iterator<PersonEthnicGroupDto> it = personVO.getThePersonEthnicGroupDtoCollection().iterator(); it.hasNext();) {
                                PersonEthnicGroupDto personEthnicGroupDT = it.next();

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
                        if(	personVO.getThePersonEthnicGroupDtoCollection() == null
                                || (	personVO.getThePersonEthnicGroupDtoCollection() != null
                                && 	personVO.getThePersonEthnicGroupDtoCollection().size() == 0)){
                            personVO.setThePersonEthnicGroupDtoCollection(new ArrayList<>());
                            personVO.getThePersonEthnicGroupDtoCollection().addAll(updatedPersonEthnicGroupCollection);
                        }
                        if(personVO.getTheEntityIdDtoCollection()!=null && personVO.getTheEntityIdDtoCollection().size()>0){
                            for (Iterator<EntityIdDto> it = personVO.getTheEntityIdDtoCollection().iterator(); it.hasNext();)
                            {
                                EntityIdDto entityIDDT = it.next();

                                entityIDDT.setItNew(true);
                                entityIDDT.setItDirty(false);
                                entityIDDT.setItDelete(false);
                                entityIDDT.setEntityUid(matchedPersonUid);
                                entityIDDT.setEntityIdSeq(++entityIdSeq);

                            }
                        }
                        if(	personVO.getTheEntityIdDtoCollection() == null)
                        {
                            personVO.setTheEntityIdDtoCollection(new ArrayList<>());
                        }
                        personVO.getTheEntityIdDtoCollection().addAll(updatedtheEntityIdDTCollection);


                        if(personVO.getTheEntityLocatorParticipationDtoCollection()!=null
                                && personVO.getTheEntityLocatorParticipationDtoCollection().size()>0){
                            for (Iterator<EntityLocatorParticipationDto> it = personVO.getTheEntityLocatorParticipationDtoCollection().iterator(); it.hasNext();)
                            {
                                EntityLocatorParticipationDto entityLocPartDT = it.next();

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
                        if(	personVO.getTheEntityLocatorParticipationDtoCollection() == null)
                        {
                            personVO.setTheEntityLocatorParticipationDtoCollection(new ArrayList<>());
                        }
                        personVO.getTheEntityLocatorParticipationDtoCollection().addAll(updatedtheEntityLocatorParticipationDTCollection);


                    }
                    personVO.setRole(null);
                }


            }


        }

    }





}
