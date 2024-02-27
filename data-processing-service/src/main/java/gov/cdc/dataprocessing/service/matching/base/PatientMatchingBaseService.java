package gov.cdc.dataprocessing.service.matching.base;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model.dto.*;
import gov.cdc.dataprocessing.model.classic_model.vo.PersonVO;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.Person;
import gov.cdc.dataprocessing.service.core.CheckingValueService;
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
            CheckingValueService checkingValueService) {
        super(edxPatientMatchRepositoryUtil, entityHelper, patientRepositoryUtil, checkingValueService);
    }

    protected PersonId updateExistingPerson(PersonVO personVO, String businessTriggerCd, Long personParentUid) throws DataProcessingException {
        PersonId personId = new PersonId();
        PersonVO personObj = personVO.deepClone();
        if (businessTriggerCd != null && (businessTriggerCd.equals("PAT_CR") || businessTriggerCd.equals("PAT_EDIT"))) {
            personId = getPersonInternalV2(personParentUid);

            personObj.setMPRUpdateValid(true);

            personObj.getThePersonDT().setPersonUid(personId.getPersonId());
            personObj.getThePersonDT().setPersonParentUid(personId.getPersonParentId());
            personObj.getThePersonDT().setLocalId(personId.getLocalId());


            prepUpdatingExistingPerson(personObj);
        }
        return personId;
    }
    protected String getLNmFnmDobCurSexStr(PersonVO personVO) {
        String namedobcursexStr = null;
        String carrot = "^";
        if (personVO.getThePersonDT() != null) {
            PersonDT personDT = personVO.getThePersonDT();
            if (personDT.getCd() != null
                    && personDT.getCd().equals(NEDSSConstant.PAT)) {
                if (personVO.getThePersonNameDTCollection() != null
                        && personVO.getThePersonNameDTCollection().size() > 0) {
                    Collection<PersonNameDT> personNameDTColl = personVO
                            .getThePersonNameDTCollection();
                    Iterator personNameIterator = personNameDTColl.iterator();
                    Timestamp asofDate = null;
                    while (personNameIterator.hasNext()) {
                        PersonNameDT personNameDT = (PersonNameDT) personNameIterator
                                .next();
                        if (personNameDT.getNmUseCd() == null)
                        {
                            String Message = "personNameDT.getNmUseCd() is null";
                            logger.debug(Message);
                        }
                        if (personNameDT.getNmUseCd() != null
                                && personNameDT.getNmUseCd().equalsIgnoreCase("L")
                                && personNameDT.getRecordStatusCd() != null
                                && personNameDT.getRecordStatusCd().equals(
                                NEDSSConstant.RECORD_STATUS_ACTIVE)) {
                            if (asofDate == null
                                    || (asofDate.getTime() < personNameDT
                                    .getAsOfDate().getTime())) {
                                if ((personNameDT.getLastNm() != null)
                                        && (!personNameDT.getLastNm().trim()
                                        .equals(""))
                                        && (personNameDT.getFirstNm() != null)
                                        && (!personNameDT.getFirstNm().trim()
                                        .equals(""))
                                        && (personDT.getBirthTime() != null)
                                        && (personDT.getCurrSexCd() != null)
                                        && (!personDT.getCurrSexCd().trim()
                                        .equals(""))) {
                                    namedobcursexStr = personNameDT.getLastNm()
                                            + carrot
                                            + personNameDT.getFirstNm()
                                            + carrot + personDT.getBirthTime()
                                            + carrot + personDT.getCurrSexCd();
                                    asofDate = personNameDT.getAsOfDate();
                                }
                            } else if (asofDate.before(personNameDT
                                    .getAsOfDate())) {
                                if ((personNameDT.getLastNm() != null)
                                        && (!personNameDT.getLastNm().trim()
                                        .equals(""))
                                        && (personNameDT.getFirstNm() != null)
                                        && (!personNameDT.getFirstNm().trim()
                                        .equals(""))
                                        && (personDT.getBirthTime() != null)
                                        && (personDT.getCurrSexCd() != null)
                                        && (!personDT.getCurrSexCd().trim()
                                        .equals(""))) {
                                    namedobcursexStr = personNameDT.getLastNm()
                                            + carrot
                                            + personNameDT.getFirstNm()
                                            + carrot + personDT.getBirthTime()
                                            + carrot + personDT.getCurrSexCd();
                                    asofDate = personNameDT.getAsOfDate();

                                }

                            }

                        }
                    }
                }
            }
        }
        return namedobcursexStr;
    }
    protected void setPersonHashCdPatient(PersonVO personVO) throws DataProcessingException {
        try {
            long personUid = personVO.getThePersonDT().getPersonParentUid();

            // DELETE Patient Matching Hash String
            getEdxPatientMatchRepositoryUtil().deleteEdxPatientMatchDTColl(personUid);
            try {
                if(personVO.getThePersonDT().getRecordStatusCd().equalsIgnoreCase(NEDSSConstant.RECORD_STATUS_ACTIVE)){
                    personVO.getThePersonDT().setPersonUid(personUid);
                    // INSERTING Patient Matching Hash String
                    setPersonToMatchEntityPatient(personVO);
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
    protected PersonId setAndCreateNewPerson(PersonVO psn) throws DataProcessingException {
        PersonId personUID = new PersonId();
        PersonVO personVO = psn.deepClone();
        Person person = null;
        Collection<EntityLocatorParticipationDT> elpDTCol = personVO.getTheEntityLocatorParticipationDTCollection();
        Collection<RoleDT> rDTCol = personVO.getTheRoleDTCollection();
        Collection<ParticipationDT> pDTCol = personVO.getTheParticipationDTCollection();
        Collection<EntityLocatorParticipationDT> colEntityLocatorParticipation = null;
        Collection<RoleDT> colRole = null;
        Collection<ParticipationDT> colParticipation = null;
        // NOTE: Sorting out Collection such as: Entity Locator Participation, Role, Participation
        if (elpDTCol != null) {
            colEntityLocatorParticipation = getEntityHelper().iterateELPDTForEntityLocatorParticipation(elpDTCol);
            personVO.setTheEntityLocatorParticipationDTCollection(colEntityLocatorParticipation);
        }
        if (rDTCol != null) {
            colRole = getEntityHelper().iterateRDT(rDTCol);
            personVO.setTheRoleDTCollection(colRole);
        }
        if (pDTCol != null) {
            colParticipation = getEntityHelper().iteratePDTForParticipation(pDTCol);
            personVO.setTheParticipationDTCollection(colParticipation);
        }
        //TODO: Patient Creation
        person = getPatientRepositoryUtil().createPerson(personVO);
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
    private void prepUpdatingExistingPerson(PersonVO personVO) throws DataProcessingException {
        PersonDT personDT = personVO.getThePersonDT();

        personVO.setThePersonDT(personDT);
        Collection<EntityLocatorParticipationDT> collEntityLocatorPar;
        Collection<RoleDT> colRole;
        Collection<ParticipationDT> colParticipation;

        collEntityLocatorPar = personVO.getTheEntityLocatorParticipationDTCollection();
        colRole = personVO.getTheRoleDTCollection();
        colParticipation = personVO.getTheParticipationDTCollection();

        if (collEntityLocatorPar != null) {
            getEntityHelper().iterateELPDTForEntityLocatorParticipation(collEntityLocatorPar);
            personVO.setTheEntityLocatorParticipationDTCollection(collEntityLocatorPar);
        }

        if (colRole != null) {
            getEntityHelper().iterateRDT(colRole);
            personVO.setTheRoleDTCollection(colRole);
        }

        if (colParticipation != null) {
            getEntityHelper().iteratePDTForParticipation(colParticipation);
            personVO.setTheParticipationDTCollection(colParticipation);
        }

        personVO = getPatientRepositoryUtil().preparePersonNameBeforePersistence(personVO);

        //TODO: Change this to Update Eixsting Patient
        getPatientRepositoryUtil().updateExistingPerson(personVO);

    }
    private void setPersonToMatchEntityPatient(PersonVO personVO) throws DataProcessingException {
        Long patientUid = personVO.getThePersonDT().getPersonUid();
        EdxPatientMatchDT edxPatientMatchDT = new EdxPatientMatchDT();
        String cdDescTxt = personVO.thePersonDT.getCdDescTxt();
        if (cdDescTxt == null || cdDescTxt.equalsIgnoreCase("") || !cdDescTxt.equalsIgnoreCase(EdxELRConstant.ELR_NOK_DESC)) {
            String identifierStr = null;
            int identifierStrhshCd = 0;
            List identifierStrList = getIdentifier(personVO);
            if (identifierStrList != null && !identifierStrList.isEmpty()) {
                for (int k = 0; k < identifierStrList.size(); k++) {
                    identifierStr = (String) identifierStrList.get(k);
                    if (identifierStr != null) {
                        identifierStr = identifierStr.toUpperCase();
                        identifierStrhshCd = identifierStr.hashCode();
                    }

                    if (identifierStr != null) {
                        edxPatientMatchDT = new EdxPatientMatchDT();
                        edxPatientMatchDT.setPatientUid(patientUid);
                        edxPatientMatchDT.setTypeCd(NEDSSConstant.PAT);
                        edxPatientMatchDT.setMatchString(identifierStr);
                        edxPatientMatchDT.setMatchStringHashCode((long) identifierStrhshCd);
                        try {
                            getEdxPatientMatchRepositoryUtil().setEdxPatientMatchDT(edxPatientMatchDT);
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
            namesdobcursexStr = getLNmFnmDobCurSexStr(personVO);
            if (namesdobcursexStr != null) {
                namesdobcursexStr = namesdobcursexStr.toUpperCase();
                namesdobcursexStrhshCd = namesdobcursexStr.hashCode();
            }

            if (namesdobcursexStr != null) {
                edxPatientMatchDT = new EdxPatientMatchDT();
                edxPatientMatchDT.setPatientUid(patientUid);
                edxPatientMatchDT.setTypeCd(NEDSSConstant.PAT);
                edxPatientMatchDT.setMatchString(namesdobcursexStr);
                edxPatientMatchDT.setMatchStringHashCode((long) namesdobcursexStrhshCd);
                try {
                    getEdxPatientMatchRepositoryUtil().setEdxPatientMatchDT(edxPatientMatchDT);
                } catch (Exception e) {
                    logger.error("Error in creating the setEdxPatientMatchDT with namesdobcursexStr:" + namesdobcursexStr + " " + e.getMessage());
                    throw new DataProcessingException(e.getMessage(), e);
                }

            }
        }
    }






}
