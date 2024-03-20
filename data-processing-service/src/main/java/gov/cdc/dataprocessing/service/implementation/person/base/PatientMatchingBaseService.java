package gov.cdc.dataprocessing.service.implementation.person.base;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.PersonContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.dto.matching.EdxPatientMatchDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.model.dto.person.PersonNameDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.Person;
import gov.cdc.dataprocessing.service.implementation.other.CachingValueService;
import gov.cdc.dataprocessing.service.model.PersonId;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import gov.cdc.dataprocessing.utilities.component.patient.EdxPatientMatchRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
public class PatientMatchingBaseService extends MatchingBaseService{
    private static final Logger logger = LoggerFactory.getLogger(PatientMatchingBaseService.class);

    public PatientMatchingBaseService(
            EdxPatientMatchRepositoryUtil edxPatientMatchRepositoryUtil,
            EntityHelper entityHelper,
            PatientRepositoryUtil patientRepositoryUtil,
            CachingValueService cachingValueService) {
        super(edxPatientMatchRepositoryUtil, entityHelper, patientRepositoryUtil, cachingValueService);
    }

    protected PersonId updateExistingPerson(PersonContainer personContainer, String businessTriggerCd, Long personParentUid) throws DataProcessingException {
        PersonId personId = new PersonId();
        PersonContainer personObj = personContainer.deepClone();
        if (businessTriggerCd != null && (businessTriggerCd.equals("PAT_CR") || businessTriggerCd.equals("PAT_EDIT"))) {
            personId = getPersonInternalV2(personParentUid);

            personObj.setMPRUpdateValid(true);

            personObj.getThePersonDto().setPersonUid(personId.getPersonId());
            personObj.getThePersonDto().setPersonParentUid(personId.getPersonParentId());
            personObj.getThePersonDto().setLocalId(personId.getLocalId());


            prepUpdatingExistingPerson(personObj);
        }
        return personId;
    }
    protected String getLNmFnmDobCurSexStr(PersonContainer personContainer) {
        String namedobcursexStr = null;
        String carrot = "^";
        if (personContainer.getThePersonDto() != null) {
            PersonDto personDto = personContainer.getThePersonDto();
            if (personDto.getCd() != null
                    && personDto.getCd().equals(NEDSSConstant.PAT)) {
                if (personContainer.getThePersonNameDtoCollection() != null
                        && personContainer.getThePersonNameDtoCollection().size() > 0) {
                    Collection<PersonNameDto> personNameDtoColl = personContainer
                            .getThePersonNameDtoCollection();
                    Iterator personNameIterator = personNameDtoColl.iterator();
                    Timestamp asofDate = null;
                    while (personNameIterator.hasNext()) {
                        PersonNameDto personNameDto = (PersonNameDto) personNameIterator
                                .next();
                        if (personNameDto.getNmUseCd() == null)
                        {
                            String Message = "personNameDT.getNmUseCd() is null";
                            logger.debug(Message);
                        }
                        if (personNameDto.getNmUseCd() != null
                                && personNameDto.getNmUseCd().equalsIgnoreCase("L")
                                && personNameDto.getRecordStatusCd() != null
                                && personNameDto.getRecordStatusCd().equals(
                                NEDSSConstant.RECORD_STATUS_ACTIVE)) {
                            if (asofDate == null
                                    || (asofDate.getTime() < personNameDto
                                    .getAsOfDate().getTime())) {
                                if ((personNameDto.getLastNm() != null)
                                        && (!personNameDto.getLastNm().trim()
                                        .equals(""))
                                        && (personNameDto.getFirstNm() != null)
                                        && (!personNameDto.getFirstNm().trim()
                                        .equals(""))
                                        && (personDto.getBirthTime() != null)
                                        && (personDto.getCurrSexCd() != null)
                                        && (!personDto.getCurrSexCd().trim()
                                        .equals(""))) {
                                    namedobcursexStr = personNameDto.getLastNm()
                                            + carrot
                                            + personNameDto.getFirstNm()
                                            + carrot + personDto.getBirthTime()
                                            + carrot + personDto.getCurrSexCd();
                                    asofDate = personNameDto.getAsOfDate();
                                }
                            } else if (asofDate.before(personNameDto
                                    .getAsOfDate())) {
                                if ((personNameDto.getLastNm() != null)
                                        && (!personNameDto.getLastNm().trim()
                                        .equals(""))
                                        && (personNameDto.getFirstNm() != null)
                                        && (!personNameDto.getFirstNm().trim()
                                        .equals(""))
                                        && (personDto.getBirthTime() != null)
                                        && (personDto.getCurrSexCd() != null)
                                        && (!personDto.getCurrSexCd().trim()
                                        .equals(""))) {
                                    namedobcursexStr = personNameDto.getLastNm()
                                            + carrot
                                            + personNameDto.getFirstNm()
                                            + carrot + personDto.getBirthTime()
                                            + carrot + personDto.getCurrSexCd();
                                    asofDate = personNameDto.getAsOfDate();

                                }

                            }

                        }
                    }
                }
            }
        }
        return namedobcursexStr;
    }
    protected void setPersonHashCdPatient(PersonContainer personContainer) throws DataProcessingException {
        try {
            long personUid = personContainer.getThePersonDto().getPersonParentUid();

            // DELETE Patient Matching Hash String
            getEdxPatientMatchRepositoryUtil().deleteEdxPatientMatchDTColl(personUid);
            try {
                if(personContainer.getThePersonDto().getRecordStatusCd().equalsIgnoreCase(NEDSSConstant.RECORD_STATUS_ACTIVE)){
                    personContainer.getThePersonDto().setPersonUid(personUid);
                    // INSERTING Patient Matching Hash String
                    setPersonToMatchEntityPatient(personContainer);
                }
            } catch (Exception e) {
                //per defect #1836 change to warning..
                logger.warn("Unable to setPatientHashCd for personUid: "+personUid);
                logger.warn("Exception in setPatientToEntityMatch -> unhandled exception: " +e.getMessage());
            }
        } catch (Exception e) {
            logger.error("EntityControllerEJB.setPatientHashCd: " + e.getMessage(), e);
            throw new DataProcessingException(e.getMessage(), e);
        }
    }
    protected PersonId setAndCreateNewPerson(PersonContainer psn) throws DataProcessingException {
        PersonId personUID = new PersonId();
        PersonContainer personContainer = psn.deepClone();
        Person person = null;
        Collection<EntityLocatorParticipationDto> elpDTCol = personContainer.getTheEntityLocatorParticipationDtoCollection();
        Collection<RoleDto> rDTCol = personContainer.getTheRoleDtoCollection();
        Collection<ParticipationDto> pDTCol = personContainer.getTheParticipationDtoCollection();
        Collection<EntityLocatorParticipationDto> colEntityLocatorParticipation = null;
        Collection<RoleDto> colRole = null;
        Collection<ParticipationDto> colParticipation = null;
        // NOTE: Sorting out Collection such as: Entity Locator Participation, Role, Participation
        if (elpDTCol != null) {
            colEntityLocatorParticipation = getEntityHelper().iterateELPDTForEntityLocatorParticipation(elpDTCol);
            personContainer.setTheEntityLocatorParticipationDtoCollection(colEntityLocatorParticipation);
        }
        if (rDTCol != null) {
            colRole = getEntityHelper().iterateRDT(rDTCol);
            personContainer.setTheRoleDtoCollection(colRole);
        }
        if (pDTCol != null) {
            colParticipation = getEntityHelper().iteratePDTForParticipation(pDTCol);
            personContainer.setTheParticipationDtoCollection(colParticipation);
        }
        person = getPatientRepositoryUtil().createPerson(personContainer);
        personUID.setPersonId(person.getPersonUid());
        personUID.setPersonParentId(person.getPersonParentUid());
        personUID.setLocalId(person.getLocalId());

        logger.debug(" EntityControllerEJB.setPerson() Person Created");
        return personUID;

    }

    private PersonId getPersonInternalV2(Long personUID) throws DataProcessingException {
        PersonId personId;
        try {
            Person person = null;
            if (personUID != null)
            {
                person = getPatientRepositoryUtil().findExistingPersonByUid(personUID);
            }
            if (person != null)
            {
                personId = new PersonId();
                personId.setPersonParentId(person.getPersonParentUid());
                personId.setPersonId(person.getPersonUid());
                personId.setLocalId(person.getLocalId());
            }
            else {
                throw new DataProcessingException("Existing Patient Not Found");
            }

            logger.debug("Ent Controller past the find - person = " + person.toString());
            logger.debug("Ent Controllerpast the find - person.getPrimaryKey = " + person.getPersonUid());

        } catch (Exception e) {
            logger.error("EntityControllerEJB.getPersonInternal: " + e.getMessage(), e);
            throw new DataProcessingException(e.getMessage(), e);
        }
        return personId;

    }
    private void prepUpdatingExistingPerson(PersonContainer personContainer) throws DataProcessingException {
        PersonDto personDto = personContainer.getThePersonDto();

        personContainer.setThePersonDto(personDto);
        Collection<EntityLocatorParticipationDto> collEntityLocatorPar;
        Collection<RoleDto> colRole;
        Collection<ParticipationDto> colParticipation;

        collEntityLocatorPar = personContainer.getTheEntityLocatorParticipationDtoCollection();
        colRole = personContainer.getTheRoleDtoCollection();
        colParticipation = personContainer.getTheParticipationDtoCollection();

        if (collEntityLocatorPar != null) {
            getEntityHelper().iterateELPDTForEntityLocatorParticipation(collEntityLocatorPar);
            personContainer.setTheEntityLocatorParticipationDtoCollection(collEntityLocatorPar);
        }

        if (colRole != null) {
            getEntityHelper().iterateRDT(colRole);
            personContainer.setTheRoleDtoCollection(colRole);
        }

        if (colParticipation != null) {
            getEntityHelper().iteratePDTForParticipation(colParticipation);
            personContainer.setTheParticipationDtoCollection(colParticipation);
        }

        personContainer = getPatientRepositoryUtil().preparePersonNameBeforePersistence(personContainer);
        getPatientRepositoryUtil().updateExistingPerson(personContainer);

    }
    private void setPersonToMatchEntityPatient(PersonContainer personContainer) throws DataProcessingException {
        Long patientUid = personContainer.getThePersonDto().getPersonUid();
        EdxPatientMatchDto edxPatientMatchDto = new EdxPatientMatchDto();
        String cdDescTxt = personContainer.thePersonDto.getCdDescTxt();
        if (cdDescTxt == null || cdDescTxt.equalsIgnoreCase("") || !cdDescTxt.equalsIgnoreCase(EdxELRConstant.ELR_NOK_DESC)) {
            String identifierStr = null;
            int identifierStrhshCd = 0;
            List identifierStrList = getIdentifier(personContainer);
            if (identifierStrList != null && !identifierStrList.isEmpty()) {
                for (int k = 0; k < identifierStrList.size(); k++) {
                    identifierStr = (String) identifierStrList.get(k);
                    if (identifierStr != null) {
                        identifierStr = identifierStr.toUpperCase();
                        identifierStrhshCd = identifierStr.hashCode();
                    }

                    if (identifierStr != null) {
                        edxPatientMatchDto = new EdxPatientMatchDto();
                        edxPatientMatchDto.setPatientUid(patientUid);
                        edxPatientMatchDto.setTypeCd(NEDSSConstant.PAT);
                        edxPatientMatchDto.setMatchString(identifierStr);
                        edxPatientMatchDto.setMatchStringHashCode((long) identifierStrhshCd);
                        try {
                            getEdxPatientMatchRepositoryUtil().setEdxPatientMatchDT(edxPatientMatchDto);
                        } catch (Exception e) {
                            logger.error("Error in creating the setEdxPatientMatchDT with identifierStr:"
                                    + identifierStr + " " + e.getMessage());
                            throw new DataProcessingException(e.getMessage(), e);
                        }

                    }
                }
            }

            // Matching with last name ,first name ,date of birth and current
            // sex

            String namesdobcursexStr = null;
            int namesdobcursexStrhshCd = 0;
            namesdobcursexStr = getLNmFnmDobCurSexStr(personContainer);
            if (namesdobcursexStr != null) {
                namesdobcursexStr = namesdobcursexStr.toUpperCase();
                namesdobcursexStrhshCd = namesdobcursexStr.hashCode();
            }

            if (namesdobcursexStr != null) {
                edxPatientMatchDto = new EdxPatientMatchDto();
                edxPatientMatchDto.setPatientUid(patientUid);
                edxPatientMatchDto.setTypeCd(NEDSSConstant.PAT);
                edxPatientMatchDto.setMatchString(namesdobcursexStr);
                edxPatientMatchDto.setMatchStringHashCode((long) namesdobcursexStrhshCd);
                try {
                    getEdxPatientMatchRepositoryUtil().setEdxPatientMatchDT(edxPatientMatchDto);
                } catch (Exception e) {
                    logger.error("Error in creating the setEdxPatientMatchDT with namesdobcursexStr:" + namesdobcursexStr + " " + e.getMessage());
                    throw new DataProcessingException(e.getMessage(), e);
                }

            }
        }
    }






}
