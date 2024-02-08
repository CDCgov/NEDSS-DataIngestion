package gov.cdc.dataprocessing.service;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model.dt.EdxLabInformationDT;
import gov.cdc.dataprocessing.model.classic_model.dto.*;
import gov.cdc.dataprocessing.model.classic_model.vo.LabResultProxyVO;
import gov.cdc.dataprocessing.model.classic_model.vo.PersonVO;
import gov.cdc.dataprocessing.repository.nbs.odse.model.Person;
import gov.cdc.dataprocessing.service.interfaces.IPatientService;
import gov.cdc.dataprocessing.utilities.component.EdxPatientMatchingCriteriaUtil;
import gov.cdc.dataprocessing.utilities.component.EdxPatientMatchingHelper;
import gov.cdc.dataprocessing.utilities.component.EntityHelper;
import gov.cdc.dataprocessing.utilities.component.PatientRepositoryUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Service
@Slf4j
public class PatientService implements IPatientService {
    private static final Logger logger = LoggerFactory.getLogger(PatientService.class);
    public boolean multipleMatchFound = false;

    private final EdxPatientMatchingCriteriaUtil edxPatientMatchingCriteriaUtil;
    private final EdxPatientMatchingHelper edxPatientMatchingHelper;

    private final EntityHelper entityHelper;
    private final PatientRepositoryUtil patientRepositoryUtil;

    public PatientService(
            EdxPatientMatchingCriteriaUtil edxPatientMatchingCriteriaUtil,
            EdxPatientMatchingHelper edxPatientMatchingHelper,
            EntityHelper entityHelper,
            PatientRepositoryUtil patientRepositoryUtil) {

        this.edxPatientMatchingCriteriaUtil = edxPatientMatchingCriteriaUtil;
        this.edxPatientMatchingHelper = edxPatientMatchingHelper;
        this.entityHelper = entityHelper;
        this.patientRepositoryUtil = patientRepositoryUtil;
    }

    public Object processingPatient(LabResultProxyVO labResultProxyVO, EdxLabInformationDT edxLabInformationDT) throws DataProcessingException {
        //TODO: Adding Logic Here
        PersonVO person = null;
        try {
            Collection<PersonVO> personVOCollection = labResultProxyVO.getThePersonVOCollection();
            if (!personVOCollection.isEmpty()) {
                Iterator<PersonVO> it = personVOCollection.iterator();
                boolean orderingProviderIndicator = false;
                while (it.hasNext()) {
                    PersonVO personVO = it.next();
                    if (personVO.getRole() != null && personVO.getRole().equalsIgnoreCase(EdxELRConstant.ELR_NEXT_OF_KIN)) {
                        //TODO: Logic for Matching Next of kin
                    }
                    else {
                        long falseUid = personVO.thePersonDT.getPersonUid();
                        Long personUid;
                        EdxPatientMatchDT edxPatientMatchFoundDT = null;

                        if (personVO.thePersonDT.getCd().equalsIgnoreCase(EdxELRConstant.ELR_PATIENT_CD)) {
                            personVO.setRole(EdxELRConstant.ELR_PATIENT_CD);

                            if(edxLabInformationDT.getPatientUid()>0){
                                personUid=edxLabInformationDT.getPatientUid();
                            }
                            else{
                                edxPatientMatchFoundDT = getMatchingPatient(personVO);
                                edxLabInformationDT.setMultipleSubjectMatch(multipleMatchFound);
                                personUid = personVO.getThePersonDT().getPersonUid();
                            }

                            if (personUid != null) {
                                setFalseToNew(labResultProxyVO, falseUid, personUid);
                                personVO.setItNew(false);
                                personVO.setItDirty(false);
                                personVO.getThePersonDT().setItNew(false);
                                personVO.getThePersonDT().setItDirty(false);
                                PersonNameDT personName = getPersonNameUseCdL(personVO);
                                String lastName = personName.getLastNm();
                                String firstName = personName.getFirstNm();
                                edxLabInformationDT.setEntityName(firstName
                                        + " " + lastName);
                            }

                            if(edxPatientMatchFoundDT!=null && !edxPatientMatchFoundDT.isMultipleMatch() && personVO.getIsExistingPatient())
                                edxLabInformationDT.setPatientMatch(true);
                            if(personVO.getThePersonDT().getPersonParentUid()!=null){
                                edxLabInformationDT.setPersonParentUid(personVO.getThePersonDT().getPersonParentUid().longValue());
                            }
                            person = personVO;
                        }
                        else if (personVO.thePersonDT.getCd().equalsIgnoreCase(EdxELRConstant.ELR_PROVIDER_CD)) {
                            //TODO: Logic for Matching Provider

                        }
                    }
                }
            }
            return "processing patient";
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage());
        }
    }

    public Object processingNextOfKin() throws DataProcessingConsumerException {
        //TODO: Adding Logic Here
        try {
            return "processing next of kin";
        } catch (Exception e) {
            throw new DataProcessingConsumerException("ERROR", "Data");
        }
    }

    public Object processingProvider() throws DataProcessingConsumerException {
        //TODO: Adding Logic Here
        try {
            return "processing provider";
        } catch (Exception e) {
            throw new DataProcessingConsumerException("ERROR", "Data");
        }
    }

    private PersonNameDT getPersonNameUseCdL(PersonVO personVO) throws DataProcessingException {
        Collection<PersonNameDT> personNames = personVO.getThePersonNameDTCollection();
        Iterator<PersonNameDT> pnIter = personNames.iterator();
        while (pnIter.hasNext()) {
            PersonNameDT personName = (PersonNameDT) pnIter.next();
            if (personName.getNmUseCd().equals("L")) {
                return personName;
            }
        }
        throw new DataProcessingException("No name use code \"L\" in PersonVO");
    }

    private void setFalseToNew(LabResultProxyVO labResultProxyVO, Long falseUid, Long actualUid) throws DataProcessingException {

        try {
            Iterator<ParticipationDT> participationIterator = null;
            Iterator<ActRelationshipDT> actRelationshipIterator = null;
            Iterator<RoleDT> roleIterator = null;


            ParticipationDT participationDT = null;
            ActRelationshipDT actRelationshipDT = null;
            RoleDT roleDT = null;

            Collection<ParticipationDT> participationColl = labResultProxyVO.getTheParticipationDTCollection();
            Collection<ActRelationshipDT> actRelationShipColl = labResultProxyVO.getTheActRelationshipDTCollection();
            Collection<RoleDT> roleColl = labResultProxyVO.getTheRoleDTCollection();

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
                    roleDT = (RoleDT) roleIterator.next();
                    if (roleDT.getSubjectEntityUid().compareTo(falseUid) == 0) {
                        roleDT.setSubjectEntityUid(actualUid);
                    }
                    if (roleDT.getScopingEntityUid() != null) {
                        if (roleDT.getScopingEntityUid().compareTo(falseUid) == 0) {
                            roleDT.setScopingEntityUid(actualUid);
                        }
                        logger.debug("\n\n\n(roleDT.getSubjectEntityUid() compared to falseUid)  "
                                + roleDT.getSubjectEntityUid().compareTo(
                                falseUid));
                        logger.debug("\n\n\n(roleDT.getScopingEntityUid() compared to falseUid)  "
                                + roleDT.getScopingEntityUid().compareTo(
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

    private EdxPatientMatchDT getMatchingPatient(PersonVO personVO) throws DataProcessingException {
        Long patientUid = personVO.getThePersonDT().getPersonUid();
        String cd = personVO.getThePersonDT().getCd();
        String patientRole = personVO.getRole();
        EdxPatientMatchDT edxPatientFoundDT = null;
        EdxPatientMatchDT edxPatientMatchFoundDT = null;
        Long patientPersonUid = null;
        boolean matchFound = false;
        boolean lrIDExists = true;

        if (patientRole == null || patientRole.equalsIgnoreCase("")|| patientRole.equalsIgnoreCase(EdxELRConstant.ELR_PATIENT_ROLE_CD)) {
            EdxPatientMatchDT localIdHashCode = null;
            String localId = null;
            int localIdhshCd = 0;
            localId = edxPatientMatchingCriteriaUtil.getLocalId(personVO);
            if (localId != null) {
                localId = localId.toUpperCase();
                localIdhshCd = localId.hashCode();
            }
            try {
                // Try to get the matching with the match string
                //	(was hash code but hash code had dups on rare occasions)
                edxPatientMatchFoundDT = edxPatientMatchingHelper.getEdxPatientMatchOnMatchString(cd, localId);
                if (edxPatientMatchFoundDT.isMultipleMatch()){
                    multipleMatchFound = true;
                    matchFound = false;
                } else if (edxPatientMatchFoundDT != null
                        && edxPatientMatchFoundDT.getPatientUid() != null) {
                    matchFound = true;

                } else {
                    lrIDExists = false;
                }

            } catch (Exception ex) {
                logger.error("Error in geting the  matching Patient");
                throw new DataProcessingException("Error in geting the  matching Patient" + ex.getMessage(), ex);
            }

            if (localId != null) {
                localIdHashCode = new EdxPatientMatchDT();
                localIdHashCode.setTypeCd(NEDSSConstant.PAT);
                localIdHashCode.setMatchString(localId);
                localIdHashCode.setMatchStringHashCode((long) localIdhshCd);
            }

            // get Identifier
            if (!matchFound) {
                String IdentifierStr = null;
                int identifierStrhshCd = 0;

                List identifierStrList = edxPatientMatchingCriteriaUtil.getIdentifier(personVO);
                if (identifierStrList != null && !identifierStrList.isEmpty()) {
                    for (int k = 0; k < identifierStrList.size(); k++) {
                        edxPatientFoundDT = new EdxPatientMatchDT();
                        IdentifierStr = (String) identifierStrList.get(k);
                        if (IdentifierStr != null) {
                            IdentifierStr = IdentifierStr.toUpperCase();
                            identifierStrhshCd = IdentifierStr.hashCode();
                        }

                        if (IdentifierStr != null) {
                            edxPatientFoundDT = new EdxPatientMatchDT();
                            edxPatientFoundDT.setTypeCd(NEDSSConstant.PAT);
                            edxPatientFoundDT.setMatchString(IdentifierStr);
                            edxPatientFoundDT.setMatchStringHashCode((long) identifierStrhshCd);
                            // Try to get the matching with the hash code
                            edxPatientMatchFoundDT = edxPatientMatchingHelper.getEdxPatientMatchOnMatchString(cd, IdentifierStr);

                            if (edxPatientMatchFoundDT.isMultipleMatch()){
                                matchFound = false;
                                multipleMatchFound = true;
                            } else if (edxPatientMatchFoundDT.getPatientUid() == null || (edxPatientMatchFoundDT.getPatientUid() != null && edxPatientMatchFoundDT.getPatientUid() <= 0)) {
                                matchFound = false;
                            } else {
                                matchFound = true;
                                break;
                            }
                        }
                    }
                }
            }
            // Matching with last name ,first name ,date of birth and current
            // sex
            if (!matchFound) {
                String namesdobcursexStr = null;
                int namesdobcursexStrhshCd = 0;
                namesdobcursexStr = edxPatientMatchingCriteriaUtil.getLNmFnmDobCurSexStr(personVO);
                if (namesdobcursexStr != null) {
                    namesdobcursexStr = namesdobcursexStr.toUpperCase();
                    namesdobcursexStrhshCd = namesdobcursexStr.hashCode();
                    try {
                        if (namesdobcursexStr != null) {
                            edxPatientFoundDT = new EdxPatientMatchDT();
                            edxPatientFoundDT.setPatientUid(patientUid);
                            edxPatientFoundDT.setTypeCd(NEDSSConstant.PAT);
                            edxPatientFoundDT.setMatchString(namesdobcursexStr);
                            edxPatientFoundDT.setMatchStringHashCode((long) namesdobcursexStrhshCd);
                        }
                        // Try to get the matching with the match string
                        // (was hash code but hash code had dups)
                        edxPatientMatchFoundDT = edxPatientMatchingHelper.getEdxPatientMatchOnMatchString(cd, namesdobcursexStr);
                        if (edxPatientMatchFoundDT.isMultipleMatch()){
                            //	&& patientRole !=null && !patientRole.equals("") && patientRole
                            //			.equalsIgnoreCase(EdxELRConstant.ELR_PATIENT_ROLE_CD)) {
                            multipleMatchFound = true;
                            matchFound = false;
                        } else if (edxPatientMatchFoundDT.getPatientUid() == null
                                || (edxPatientMatchFoundDT.getPatientUid() != null && edxPatientMatchFoundDT
                                .getPatientUid() <= 0)) {
                            matchFound = false;
                        } else {
                            matchFound = true;
                        }
                    } catch (Exception ex) {
                        logger.error("Error in geting the  matching Patient");
                        throw new DataProcessingException("Error in geting the  matching Patient" + ex.getMessage(), ex);
                    }
                }
            }

            // Create the patient in case if the patient is not there in the DB
            if (!matchFound) {
                if (personVO.getTheEntityIdDTCollection() != null) {
                    Collection<EntityIdDT> newEntityIdDTColl = new ArrayList<>();
                    Iterator<EntityIdDT> iter = personVO.getTheEntityIdDTCollection().iterator();
                    while (iter.hasNext()) {
                        EntityIdDT entityIdDT = (EntityIdDT) iter.next();
                        if (entityIdDT.getTypeCd() != null
                                && !entityIdDT.getTypeCd().equalsIgnoreCase(
                                "LR")) {
                            newEntityIdDTColl.add(entityIdDT);
                        }
                    }
                    personVO.setTheEntityIdDTCollection(newEntityIdDTColl);
                }
                try {
                    // TODO: Call out to Person Repos do update
                    if (personVO.getThePersonDT().getCd().equals(NEDSSConstant.PAT)) { // Patient
                        patientPersonUid = setPerson(personVO);
                        personVO.getThePersonDT().setPersonParentUid(patientPersonUid);
                    }
                } catch (Exception e) {
                    logger.error("Error in getting the entity Controller or Setting the Patient" + e.getMessage(), e);
                    throw new DataProcessingException("Error in getting the entity Controller or Setting the Patient" + e.getMessage(), e);
                }
                personVO.setIsExistingPatient(false);
            }
            else {
                personVO.setIsExistingPatient(true);
            }

            try {
                if (patientPersonUid == null)
                    personVO.getThePersonDT().setPersonParentUid(edxPatientMatchFoundDT.getPatientUid());
                else {
                    personVO.getThePersonDT().setPersonParentUid(patientPersonUid);
                }
                // TODO: Call out to Person Repos do update
                patientUid = setPatientRevision(personVO,NEDSSConstant.PAT_CR);
                personVO.getThePersonDT().setPersonUid(patientUid);
            } catch (Exception e) {
                logger.error("Error in getting the entity Controller or Setting the Patient"
                        + e.getMessage());
                throw new DataProcessingException("Error in getting the entity Controller or Setting the Patient" + e.getMessage(), e);
            }

            // if LocalId not exists/match inserting the new record.
            if (!lrIDExists && localIdHashCode != null) {
                localIdHashCode.setPatientUid(personVO.getThePersonDT()
                        .getPersonParentUid());
                // TODO: This one call insert query to Edx Patient
                edxPtDao.setEdxPatientMatchDT(localIdHashCode);
            }

        }
        else if (patientRole.equalsIgnoreCase(EdxELRConstant.ELR_NEXT_F_KIN_ROLE_CD)) {
            // TODO: NEXT OF KIN
        }
        return edxPatientMatchFoundDT;
    }

    public Long setPerson(PersonVO personVO) throws DataProcessingException {
        Long personUID = -1L;

        try {
            Person person = null;
            Collection<EntityLocatorParticipationDT> elpDTCol = personVO.getTheEntityLocatorParticipationDTCollection();
            Collection<RoleDT> rDTCol = personVO.getTheRoleDTCollection();
            Collection<ParticipationDT> pDTCol = personVO.getTheParticipationDTCollection();
            Collection<EntityLocatorParticipationDT> colEntityLocatorParticipation = null;
            Collection<RoleDT> colRole = null;
            Collection<ParticipationDT> colParticipation = null;


            if (elpDTCol != null) {
                colEntityLocatorParticipation = entityHelper.iterateELPDTForEntityLocatorParticipation(elpDTCol);
                personVO.setTheEntityLocatorParticipationDTCollection(colEntityLocatorParticipation);
            }
            if (rDTCol != null) {
                colRole = entityHelper.iterateRDT(rDTCol);
                personVO.setTheRoleDTCollection(colRole);
            }
            if (pDTCol != null) {
                colParticipation = entityHelper.iteratePDTForParticipation(pDTCol);
                personVO.setTheParticipationDTCollection(colParticipation);
            }

            if (personVO.isItNew()) {
                //TODO: Patient Creation
                //person = home.create(personVO);
                //personUID = person.getPersonVO().getThePersonDT().getPersonUid();

                person = patientRepositoryUtil.createPerson(personVO);
                personUID = person.getPersonUid();
                logger.debug(" EntityControllerEJB.setPerson() Person Created");
            } else {
                //TODO: Patient Update
                person = home.findByPrimaryKey(personVO.getThePersonDT().getPersonUid());
                person.setPersonVO(personVO);
                personUID = personVO.getThePersonDT().getPersonUid();
                logger.debug(" EntityControllerEJB.setPerson() Person Updated");
                if(personVO.getThePersonDT().getRecordStatusCd().equalsIgnoreCase(NEDSSConstant.RECORD_STATUS_SUPERCEDED)){
                    FindPersonDAOImpl personDao = new FindPersonDAOImpl();
                    personDao.deleteEdxPatientMatchDTColl(personUID);
                }
            }

        }
        catch (Exception e) {
            logger.error("EntityControllerEJB.setPerson: Exception: " + e.getMessage(),e);
            throw new DataProcessingException(e.getMessage(),e);
        }
        return personUID;
    }

    /**
     * if parent uid is null, the person is MPR, make a clone for revision
     *
     * @param personVO
     * @param businessTriggerCd
     * @param nbsSecurityObj
     * @return
     * @throws EJBException
     * @throws NEDSSConcurrentDataException
     * @roseuid 3E7B3806004E
     * @J2EE_METHOD -- setPatientRevision
     */
    public java.lang.Long setPatientRevision(PersonVO personVO, String businessTriggerCd) throws DataProcessingException {
        PersonVO mprPersonVO = null;
        Long mprPersonUid = null;
        Long personUid = null;
        try {
            PersonDT personDT = personVO.getThePersonDT();
            if (personDT.getPersonParentUid() == null) {
                try {
                    mprPersonVO = this.cloneVO(personVO);
                    // as per shannon comments should not reflect on mpr
                    mprPersonVO.getThePersonDT().setDescription(null);
                    mprPersonVO.getThePersonDT().setAsOfDateAdmin(null);
                    mprPersonVO.getThePersonDT().setAgeReported(null);
                    mprPersonVO.getThePersonDT().setAgeReportedUnitCd(null);
                    if (mprPersonVO.getThePersonDT().getCurrSexCd() == null
                            || mprPersonVO.getThePersonDT().getCurrSexCd().trim()
                            .length() == 0)
                        mprPersonVO.getThePersonDT().setAsOfDateSex(null);

                } catch (Exception e) {
                    logger.debug(e.getMessage());
                }
                mprPersonUid = this.setPersonInternal(mprPersonVO,
                        NBSBOLookup.PATIENT, "PAT_CR", nbsSecurityObj);

                mprPersonVO = this.getPersonInternal(mprPersonUid, nbsSecurityObj);
                personVO.getThePersonDT().setPersonParentUid(mprPersonUid);
                personVO.getThePersonDT().setLocalId(
                        mprPersonVO.getThePersonDT().getLocalId());
            }

            else {
                if (businessTriggerCd != null
                        && (businessTriggerCd.equals("PAT_CR") || businessTriggerCd
                        .equals("PAT_EDIT"))) {
                    this.updateWithRevision(personVO, nbsSecurityObj);
                }

                // civil00011674. If we are in this block,
                // personVO.getThePersonDT().getLocalId()
                // should never be null or empty. Somehow, this does happen
                // sporadically. The
                // following code fixed the defect. However, we need to research
                // more why
                // personVO.getThePersonDT().getLocalId() is null or empty in the
                // first place.
                if (personVO.getThePersonDT().getLocalId() == null
                        || personVO.getThePersonDT().getLocalId().trim().length() == 0) {
                    mprPersonUid = personVO.getThePersonDT().getPersonParentUid();

                    mprPersonVO = this.getPersonInternal(mprPersonUid, nbsSecurityObj);

                    personVO.getThePersonDT().setLocalId(mprPersonVO.getThePersonDT().getLocalId());
                }
            }


            personUid = this.setPersonInternal(personVO, NBSBOLookup.PATIENT,businessTriggerCd, nbsSecurityObj);

            if (personVO.getThePersonDT() != null && (personVO.getThePersonDT().getElectronicInd() != null
                    && !personVO.getThePersonDT().getElectronicInd().equals(EdxELRConstant.ELECTRONIC_IND_ELR))) {// ldf code
                // begin
                LDFHelper ldfHelper = LDFHelper.getInstance();
                ldfHelper.setLDFCollection(personVO.getTheStateDefinedFieldDataDTCollection(), personVO.getLdfUids(), EdxELRConstant.PATIENT_LDF, null, personUid, nbsSecurityObj);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error("EntityControllerEJB.setPatientRevision: " + e.getMessage(), e);
            throw new DataProcessingException(e.getMessage(), e);
        }
        // ldf code end
        return personUid;
    }


}
