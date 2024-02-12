package gov.cdc.dataprocessing.service.matching;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NBSBOLookup;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model.dto.*;
import gov.cdc.dataprocessing.model.classic_model.vo.PersonVO;
import gov.cdc.dataprocessing.repository.nbs.odse.model.Person;
import gov.cdc.dataprocessing.service.CheckingValueService;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import gov.cdc.dataprocessing.utilities.component.patient.EdxPatientMatchRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PreparingPersonUtil;
import gov.cdc.dataprocessing.utilities.model.Coded;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Timestamp;
import java.util.*;

@Service
public class PatientMatchingService {
    private static final Logger logger = LoggerFactory.getLogger(PatientMatchingService.class);
    public boolean multipleMatchFound = false;

    private final EdxPatientMatchRepositoryUtil edxPatientMatchRepositoryUtil;
    private final EntityHelper entityHelper;
    private final PatientRepositoryUtil patientRepositoryUtil;

    private final CheckingValueService checkingValueService;
    private final PreparingPersonUtil preparingPersonUtil;


    public PatientMatchingService(
            EdxPatientMatchRepositoryUtil edxPatientMatchRepositoryUtil,
            EntityHelper entityHelper,
            PatientRepositoryUtil patientRepositoryUtil,
            CheckingValueService checkingValueService,
            PreparingPersonUtil preparingPersonUtil) {
        this.edxPatientMatchRepositoryUtil = edxPatientMatchRepositoryUtil;
        this.entityHelper = entityHelper;
        this.patientRepositoryUtil = patientRepositoryUtil;
        this.checkingValueService = checkingValueService;
        this.preparingPersonUtil = preparingPersonUtil;
    }


    public EdxPatientMatchDT getMatchingPatient(PersonVO personVO) throws DataProcessingException {
        Long patientUid = personVO.getThePersonDT().getPersonUid();
        String cd = personVO.getThePersonDT().getCd();
        String patientRole = personVO.getRole();
        EdxPatientMatchDT edxPatientFoundDT = null;
        EdxPatientMatchDT edxPatientMatchFoundDT = null;
        Long patientPersonUid = null;
        boolean matchFound = false;
        boolean lrIDExists = true;

        if (patientRole == null || patientRole.isEmpty() || patientRole.equalsIgnoreCase(EdxELRConstant.ELR_PATIENT_ROLE_CD)) {
            EdxPatientMatchDT localIdHashCode = null;
            String localId = null;
            int localIdhshCd = 0;
            localId = getLocalId(personVO);
            if (localId != null) {
                localId = localId.toUpperCase();
                localIdhshCd = localId.hashCode();
            }
            //NOTE: Matching Start here
            try {
                // Try to get the matching with the match string
                //	(was hash code but hash code had dups on rare occasions)
                edxPatientMatchFoundDT = edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(cd, localId);
                if (edxPatientMatchFoundDT.isMultipleMatch()){
                    multipleMatchFound = true;
                    matchFound = false;
                }
                else if (edxPatientMatchFoundDT != null && edxPatientMatchFoundDT.getPatientUid() != null) {
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

            // NOTE: Matching by Identifier
            if (!matchFound) {
                String IdentifierStr = null;
                int identifierStrhshCd = 0;

                List identifierStrList = getIdentifier(personVO);
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
                            edxPatientMatchFoundDT = edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(cd, IdentifierStr);

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

            // NOTE: Matching with last name ,first name ,date of birth and current sex
            if (!matchFound) {
                String namesdobcursexStr = null;
                int namesdobcursexStrhshCd = 0;
                namesdobcursexStr = getLNmFnmDobCurSexStr(personVO);
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
                        edxPatientMatchFoundDT = edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(cd, namesdobcursexStr);
                        if (edxPatientMatchFoundDT.isMultipleMatch()){
                            multipleMatchFound = true;
                            matchFound = false;
                        } else if (edxPatientMatchFoundDT.getPatientUid() == null || (edxPatientMatchFoundDT.getPatientUid() != null && edxPatientMatchFoundDT.getPatientUid() <= 0)) {
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

            // NOTE: Decision, Match Not Found, Start Person Creation
            if (!matchFound) {
                if (personVO.getTheEntityIdDTCollection() != null) {
                    //SORTING out existing EntityId
                    Collection<EntityIdDT> newEntityIdDTColl = new ArrayList<>();
                    Iterator<EntityIdDT> iter = personVO.getTheEntityIdDTCollection().iterator();
                    while (iter.hasNext()) {
                        EntityIdDT entityIdDT = iter.next();
                        if (entityIdDT.getTypeCd() != null && !entityIdDT.getTypeCd().equalsIgnoreCase("LR")) {
                            newEntityIdDTColl.add(entityIdDT);
                        }
                    }
                    personVO.setTheEntityIdDTCollection(newEntityIdDTColl);
                }
                try {
                    // NOTE: IF new patient then create
                    // IF existing patient, then query find it, then Get Parent Patient ID
                    if (personVO.getThePersonDT().getCd().equals(NEDSSConstant.PAT)) { // Patient
                        // THIS setPerson handle db persistence
                        patientPersonUid = setNewPerson(personVO);
                        personVO.getThePersonDT().setPersonParentUid(patientPersonUid);
                    }
                } catch (Exception e) {
                    logger.error("Error in getting the entity Controller or Setting the Patient" + e.getMessage(), e);
                    throw new DataProcessingException("Error in getting the entity Controller or Setting the Patient" + e.getMessage(), e);
                }
                //personVO.setIsExistingPatient(false);
                personVO.setPatientMatchedFound(false);
            }
            else {
                personVO.setPatientMatchedFound(true);
            }

            //NOTE: In this flow, if new patient, revision record is still get inserted
            // if existing pateint, revision also insrted
            try {

                // IF MATCHED FOUND, patient uid from matched  will be set to parent uid
                if (patientPersonUid == null && personVO.getPatientMatchedFound())
                {
                    personVO.getThePersonDT().setPersonParentUid(edxPatientMatchFoundDT.getPatientUid());
                }
                else {
                    personVO.getThePersonDT().setPersonParentUid(patientPersonUid);
                }
                // TODO: Call out to Person Repos do update
                //NOTE: Persisting EDX Patient Matching Hash Is Also Happened in this REVISION method
                patientUid = setPatientRevision(personVO,NEDSSConstant.PAT_CR);
                personVO.getThePersonDT().setPersonUid(patientUid);
            } catch (Exception e) {
                logger.error("Error in getting the entity Controller or Setting the Patient" + e.getMessage());
                throw new DataProcessingException("Error in getting the entity Controller or Setting the Patient" + e.getMessage(), e);
            }

            // if LocalId not exists/match inserting the new record.
            // NOTE electronic ELR: NEVER HIT  with NEW PATIENT
            if (!lrIDExists && localIdHashCode != null) {
                localIdHashCode.setPatientUid(personVO.getThePersonDT().getPersonParentUid());
                //TODO: This one call insert query to Edx Patient
                edxPatientMatchRepositoryUtil.setEdxPatientMatchDT(localIdHashCode);
            }

        }
        else if (patientRole.equalsIgnoreCase(EdxELRConstant.ELR_NEXT_F_KIN_ROLE_CD)) {
            //TODO: NEXT OF KIN
        }
        return edxPatientMatchFoundDT;
    }

    private Long setNewPerson(PersonVO psn) throws DataProcessingException {
        Long personUID;
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
        //TODO: Patient Creation
        //person = home.create(personVO);
        //personUID = person.getPersonVO().getThePersonDT().getPersonUid();
        person = patientRepositoryUtil.createPerson(personVO);
        personUID = person.getPersonUid();
        logger.debug(" EntityControllerEJB.setPerson() Person Created");
        return personUID;

    }

    private Long setPerson(PersonVO personVO) throws DataProcessingException {
        Long personUID = -1L;

        try {
            Person person = null;
            Collection<EntityLocatorParticipationDT> elpDTCol = personVO.getTheEntityLocatorParticipationDTCollection();
            Collection<RoleDT> rDTCol = personVO.getTheRoleDTCollection();
            Collection<ParticipationDT> pDTCol = personVO.getTheParticipationDTCollection();
            Collection<EntityLocatorParticipationDT> colEntityLocatorParticipation = null;
            Collection<RoleDT> colRole = null;
            Collection<ParticipationDT> colParticipation = null;

            // NOTE: Sorting out Collection such as: Entity Locator Participation, Role, Participation
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
                //
                person = patientRepositoryUtil.findExistingPersonByUid(personVO);
                personUID  = person.getPersonUid();
                logger.debug(" EntityControllerEJB.setPerson() Person Updated");
                if(personVO.getThePersonDT().getRecordStatusCd().equalsIgnoreCase(NEDSSConstant.RECORD_STATUS_SUPERCEDED)){
                    //NOTE: Some sort of deleteion code goes here
                    edxPatientMatchRepositoryUtil.deleteEdxPatientMatchDTColl(personUID);
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
     */
    private Long setPatientRevision(PersonVO personVO, String businessTriggerCd) throws DataProcessingException {
        PersonVO mprPersonVO = null;
        Long mprPersonUid = null;
        Long personUid = null;

        boolean newPersonFlow = false;
        try {
            PersonDT personDT = personVO.getThePersonDT();
            //NOTE: Never hit for both New and Existing Patient
            if (personDT.getPersonParentUid() == null) {
                try {
                    mprPersonVO = this.cloneVO(personVO);
                    mprPersonVO.getThePersonDT().setDescription(null);
                    mprPersonVO.getThePersonDT().setAsOfDateAdmin(null);
                    mprPersonVO.getThePersonDT().setAgeReported(null);
                    mprPersonVO.getThePersonDT().setAgeReportedUnitCd(null);
                    if (mprPersonVO.getThePersonDT().getCurrSexCd() == null || mprPersonVO.getThePersonDT().getCurrSexCd().trim().length() == 0) {
                        mprPersonVO.getThePersonDT().setAsOfDateSex(null);
                    }

                } catch (Exception e) {
                    logger.debug(e.getMessage());
                }
                mprPersonUid = setPersonInternal(mprPersonVO, NBSBOLookup.PATIENT, "PAT_CR");

                mprPersonVO = getPersonInternal(mprPersonUid, personVO);
                personVO.getThePersonDT().setPersonParentUid(mprPersonUid);
                personVO.getThePersonDT().setLocalId(mprPersonVO.getThePersonDT().getLocalId());
            }

            else {
                if (businessTriggerCd != null && (businessTriggerCd.equals("PAT_CR") || businessTriggerCd.equals("PAT_EDIT"))) {
                    newPersonFlow = true;
                    this.updateWithRevision(personVO);
                }
                if (personVO.getThePersonDT().getLocalId() == null || personVO.getThePersonDT().getLocalId().trim().length() == 0) {
                    mprPersonUid = personVO.getThePersonDT().getPersonParentUid();
                    mprPersonVO = getPersonInternal(mprPersonUid, personVO);
                    personVO.getThePersonDT().setLocalId(mprPersonVO.getThePersonDT().getLocalId());
                }
            }


            //TWEAK
            if (newPersonFlow) {
                personVO.setItDirty(true);
                personVO.setItNew(true);
            }
            //END TWEAK
            personUid = setPersonInternal(personVO, NBSBOLookup.PATIENT,businessTriggerCd);

            if (personVO.getThePersonDT() != null && (personVO.getThePersonDT().getElectronicInd() != null
                    && !personVO.getThePersonDT().getElectronicInd().equals(EdxELRConstant.ELECTRONIC_IND_ELR))) {// ldf code
                //TODO: THis seem related to version control
                // LDFHelper ldfHelper = LDFHelper.getInstance();
                // ldfHelper.setLDFCollection(personVO.getTheStateDefinedFieldDataDTCollection(), personVO.getLdfUids(), EdxELRConstant.PATIENT_LDF, null, personUid);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error("EntityControllerEJB.setPatientRevision: " + e.getMessage(), e);
            throw new DataProcessingException(e.getMessage(), e);
        }
        // ldf code end
        return personUid;
    }
    private PersonVO cloneVO(PersonVO personVO)
            throws DataProcessingException {
        try {
            if (personVO != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(personVO);
                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                ObjectInputStream ois = new ObjectInputStream(bais);
                Object clonePersonVO = ois.readObject();
                return (PersonVO) clonePersonVO;
            } else
                return personVO;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error("EntityControllerEJB.cloneVO: " + e.getMessage(), e);
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    // getting local Id string from person VO
    private String getLocalId(PersonVO personVO) {
        String localId = null;
        if (personVO.getLocalIdentifier() != null) {
            localId = personVO.getLocalIdentifier();
        }
        return localId;
    }

    private List<String> getIdentifier(PersonVO personVO) throws DataProcessingException {
        String carrot = "^";
        List<String> returnList =new ArrayList<String>();
        List<String> identifierList = new ArrayList<String>();
        String identifier = null;
        try{
            if (personVO.getTheEntityIdDTCollection() != null
                    && personVO.getTheEntityIdDTCollection().size() > 0) {
                Collection<EntityIdDT> entityIdDTColl = personVO.getTheEntityIdDTCollection();
                Iterator<EntityIdDT> entityIdIterator = entityIdDTColl.iterator();
                while (entityIdIterator.hasNext()) {
                    identifier= null;
                    EntityIdDT entityIdDT = entityIdIterator.next();
                    if (((entityIdDT.getStatusCd() != null && entityIdDT
                            .getStatusCd().equalsIgnoreCase(NEDSSConstant.STATUS_ACTIVE))
                            && entityIdDT.getRecordStatusCd() != null
                            && (entityIdDT.getRecordStatusCd().equalsIgnoreCase(NEDSSConstant.RECORD_STATUS_ACTIVE)))
                            || (entityIdDT.getRecordStatusCd() != null
                            && entityIdDT.getTypeCd()!=null
                            && entityIdDT.getTypeCd().equalsIgnoreCase(EdxELRConstant.ELR_SS_TYPE)
                            && (entityIdDT.getRecordStatusCd().equalsIgnoreCase(NEDSSConstant.RECORD_STATUS_ACTIVE)))
                    ) {


                        if ((entityIdDT.getRootExtensionTxt() != null)
                                && (entityIdDT.getTypeCd() != null)
                                && (entityIdDT.getAssigningAuthorityCd() != null)
                                && (entityIdDT.getAssigningAuthorityDescTxt() !=null)
                                && (entityIdDT.getAssigningAuthorityIdType() != null)) {
                            identifier = entityIdDT.getRootExtensionTxt()
                                    + carrot + entityIdDT.getTypeCd() + carrot
                                    + entityIdDT.getAssigningAuthorityCd()
                                    + carrot
                                    + entityIdDT.getAssigningAuthorityDescTxt()
                                    + carrot + entityIdDT.getAssigningAuthorityIdType();
                        }else {
                            try {
                                Coded coded = new Coded();
                                coded.setCode(entityIdDT.getAssigningAuthorityCd());
                                coded.setCodesetName(NEDSSConstant.EI_AUTH);

                                //TODO: This call out to code value general Repos and Caching the recrod
//                                NotificationSRTCodeLookupTranslationDAOImpl lookupDAO = new NotificationSRTCodeLookupTranslationDAOImpl();
//                                lookupDAO.retrieveSRTCodeInfo(coded);

                                var codedValueGenralList = checkingValueService.findCodeValuesByCodeSetNmAndCode(coded.getCodesetName(), coded.getCode());

                                if (entityIdDT.getRootExtensionTxt() != null
                                        && entityIdDT.getTypeCd() != null
                                        && coded.getCode()!=null
                                        && coded.getCodeDescription()!=null
                                        && coded.getCodeSystemCd()!=null){
                                    identifier = entityIdDT.getRootExtensionTxt()
                                            + carrot + entityIdDT.getTypeCd() + carrot
                                            + coded.getCode() + carrot
                                            + coded.getCodeDescription() + carrot
                                            + coded.getCodeSystemCd();
                                }
                            }catch (Exception ex) {
                                String errorMessage = "The assigning authority " + entityIdDT.getAssigningAuthorityCd() + " does not exists in the system. ";
                                logger.debug(ex.getMessage() + errorMessage);
                            }
                        }

                        if (identifier != null) {
                            if (getNamesStr(personVO) != null) {
                                identifier = identifier + carrot + getNamesStr(personVO);
                                identifierList.add(identifier);
                            }
                        }

                    }
                }
            }
            HashSet<String> hashSet = new HashSet<String>(identifierList);
            returnList = new ArrayList<String>(hashSet) ;
        }
        catch (Exception ex) {
            String errorMessage = "Exception while creating hashcode for patient entity IDs . ";
            logger.debug(ex.getMessage() + errorMessage);
            throw new DataProcessingException(errorMessage, ex);
        }
        return returnList;
    }

    private void setPatientHashCd(PersonVO personVO) throws DataProcessingException {

        try {
            long personUid = personVO.getThePersonDT().getPersonParentUid();

            // DELETE Patient Matching Hash String
            edxPatientMatchRepositoryUtil.deleteEdxPatientMatchDTColl(personUid);
            try {
                if(personVO.getThePersonDT().getRecordStatusCd().equalsIgnoreCase(NEDSSConstant.RECORD_STATUS_ACTIVE)){
                    personVO.getThePersonDT().setPersonUid(personUid);
                    // INSERTING Patient Matching Hash String
                    setPatientToEntityMatch(personVO);
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

    /**
     * @roseuid 3E7B380C036B
     * @J2EE_METHOD -- setPersonInternal
     */
    private Long setPersonInternal(PersonVO personVO, String businessObjLookupName, String businessTriggerCd) throws  DataProcessingException {
        Long personUID = -1L;
        String localId = "";
        boolean isELRCase = false;
        try {
            //NOTE: ELR New Patient, create matching hash cycle, patient should be Dirty and not NEW
            //NOTE: ELR New Patient, create revision cycle, patient should be dirty and new (for revision insertion)
            if (personVO.isItNew() || personVO.isItDirty()) {

                // NOTE: NEW patient would not hit his (revision cycle)
                // NOTE: New Patient last cycle will this his
                if (personVO.getThePersonDT().isItNew() && !(businessObjLookupName.equalsIgnoreCase(NEDSSConstant.businessObjLookupNamePROVIDER))) {
                    localId = personVO.getThePersonDT().getLocalId();
                }

                // NOTE: NEW patient would not hit his
                if(localId==null){
                    personVO.getThePersonDT().setEdxInd("Y");
                    isELRCase= true;
                }

                //TODO: Check this prep function out
             //   PersonDT personDT = preparingPersonUtil.prepareVO(personVO.getThePersonDT(), businessObjLookupName, businessTriggerCd, "PERSON", "BASE");
                PersonDT personDT = personVO.getThePersonDT();

                // NOTE: NEW patient would not hit his (Revision cycle)
                // NOTE: NEW patient would will hit his (Last cycle)
                if (personVO.getThePersonDT().isItNew() && !(businessObjLookupName.equalsIgnoreCase(NEDSSConstant.businessObjLookupNamePROVIDER)))
                {
                    personDT.setLocalId(localId);
                }

                personVO.setThePersonDT(personDT);
                Collection<EntityLocatorParticipationDT> collEntityLocatorPar = null;
                Collection<RoleDT> colRole= null;
                Collection<ParticipationDT> colParticipation= null;


                collEntityLocatorPar = personVO.getTheEntityLocatorParticipationDTCollection();
                colRole = personVO.getTheRoleDTCollection();
                colParticipation = personVO.getTheParticipationDTCollection();

                if (collEntityLocatorPar != null) {
                    entityHelper.iterateELPDTForEntityLocatorParticipation(collEntityLocatorPar);
                    personVO.setTheEntityLocatorParticipationDTCollection(collEntityLocatorPar);
                }

                if (colRole != null) {
                    entityHelper.iterateRDT(colRole);
                    personVO.setTheRoleDTCollection(colRole);
                }

                if (colParticipation != null) {
                    entityHelper.iteratePDTForParticipation(colParticipation);
                    personVO.setTheParticipationDTCollection(colParticipation);
                }

                personVO = patientRepositoryUtil.preparePersonNameBeforePersistence(personVO);

                // NOTE: NEW patient would not hit his
                if (personVO.isItNew()) {
                    Person person = patientRepositoryUtil.createPerson(personVO);
                    personUID = person.getPersonUid();
                    logger.debug(" EntityControllerEJB.setProvider() Person Created");
                } else {
                    // TODO: Check this Update DB
                    //TODO: Check Legacy - DOES THIS DO ANYTHING?
                    //                person.setPersonVO(personVO);
//                    Person person = home.findByPrimaryKey(personVO
//                            .getThePersonDT().getPersonUid());
//
                    Person person = patientRepositoryUtil.findExistingPersonByUid(personVO.getThePersonDT().getPersonUid());
                    // person.setPersonVO(personVO);
//                    personUID = person.getPersonVO().getThePersonDT()
//                            .getPersonUid();

                    // NOTE: This suppose to be person parent Uid
                    personUID = person.getPersonUid();
                    logger.debug(" EntityControllerEJB.setProvider() Person Updated");
                }
                // NOTE: NEW patient would not hit his
                if(isELRCase){
                    try {
                        personVO.getThePersonDT().setPersonUid(personUID);
                        personVO.getThePersonDT().setPersonParentUid(personUID);
                        setPatientHashCd(personVO);
                    } catch (Exception e) {
                        logger.error("RemoteException thrown while creating hashcode for the ELR patient."+e);
                        throw new DataProcessingException(e.getMessage(), e);

                    }
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(),e);
        }
        return personUID;
    }

    private Long setMPR(PersonVO personVO,  String businessTriggerCd) throws DataProcessingException {
        try {
            Long personUID = null;
            personVO.getThePersonDT().setEdxInd(NEDSSConstant.EDX_IND);
            //NOTE: Elec ELR, new patient, would not hit this block
            if(personVO.isExt()){
                personVO.setItNew(false);
                personVO.setItDirty(false);
                if(personVO.isExt()){
                    if(personVO.getThePersonNameDTCollection()!=null){
                        Collection<PersonNameDT> coll = personVO.getThePersonNameDTCollection();
                        Iterator<PersonNameDT> itName = coll.iterator();
                        while (itName.hasNext()) {
                            PersonNameDT personNameDT = (PersonNameDT) itName.next();
                            personNameDT.setItDirty(true);
                            personNameDT.setItNew(false);

                        }
                    }
                }
            }

            //NOTE: Elec ELR, new patient, WILL hit this block
            if(personVO.isMPRUpdateValid()){
                personUID = setPersonInternal(personVO, NBSBOLookup.PATIENT, businessTriggerCd);

                try {

                    if(personVO.getThePersonDT().getPersonParentUid()==null)
                    {
                        personVO.getThePersonDT().setPersonParentUid(personUID);
                    }
                    //NOTE: in legacy this one will update patient hashing code
                    setPatientHashCd(personVO);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return personUID;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error("EntityControllerEJB.setMPR: " + e.getMessage(), e);
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    private void setPatientToEntityMatch(PersonVO personVO) throws DataProcessingException {

        Long patientUid = personVO.getThePersonDT().getPersonUid();
        EdxPatientMatchDT edxPatientMatchDT = new EdxPatientMatchDT();
        String patientRole = personVO.getRole();
        String cdDescTxt = personVO.thePersonDT.getCdDescTxt();
        // Matching with IDValue,IDType Code,Assigning authority,Legal Last name
        // ,Legal First name-Identifier
        // if (patientRole == null ||
        // patientRole.equalsIgnoreCase(EdxELRConstants.ELR_PATIENT_ROLE_CD)||
        // patientRole.equalsIgnoreCase("")) {
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
                            edxPatientMatchRepositoryUtil.setEdxPatientMatchDT(edxPatientMatchDT);
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
                    edxPatientMatchRepositoryUtil.setEdxPatientMatchDT(edxPatientMatchDT);
                } catch (Exception e) {
                    logger.error("Error in creating the setEdxPatientMatchDT with namesdobcursexStr:" + namesdobcursexStr + " " + e.getMessage());
                    throw new DataProcessingException(e.getMessage(), e);
                }

            }
        }
        // else if
        // (patientRole.equalsIgnoreCase(EdxELRConstants.ELR_NEXT_F_KIN_ROLE_CD))
        // {
        if (cdDescTxt != null && cdDescTxt.equalsIgnoreCase(EdxELRConstant.ELR_NOK_DESC)) {
            //TODO: Next of KIN Code

        }// end of method
    }

    private String getNamesStr(PersonVO personVO) {
        String namesStr = null;
        String carrot = "^";
        if (personVO.getThePersonDT() != null) {
            PersonDT personDT = personVO.getThePersonDT();
            if (personDT.getCd() != null
                    && personDT.getCd().equals(NEDSSConstant.PAT)) {
                if (personVO.getThePersonNameDTCollection() != null
                        && personVO.getThePersonNameDTCollection().size() > 0) {
                    Collection<PersonNameDT> personNameDTColl = personVO.getThePersonNameDTCollection();
                    Iterator<PersonNameDT> personNameIterator = personNameDTColl.iterator();
                    Timestamp asofDate = null;
                    while (personNameIterator.hasNext()) {
                        PersonNameDT personNameDT = (PersonNameDT) personNameIterator
                                .next();
                        if (personNameDT.getNmUseCd() != null
                                && personNameDT.getNmUseCd().equalsIgnoreCase(
                                "L")
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
                                        .equals(""))) {
                                    namesStr = personNameDT.getLastNm()
                                            + carrot
                                            + personNameDT.getFirstNm();
                                    asofDate = personNameDT.getAsOfDate();

                                }
                            } else if (asofDate.before(personNameDT
                                    .getAsOfDate())) {
                                if ((personNameDT.getLastNm() != null)
                                        && (!personNameDT.getLastNm().trim()
                                        .equals(""))
                                        && (personNameDT.getFirstNm() != null)
                                        && (!personNameDT.getFirstNm().trim()
                                        .equals(""))) {
                                    namesStr = personNameDT.getLastNm()
                                            + carrot
                                            + personNameDT.getFirstNm();
                                    asofDate = personNameDT.getAsOfDate();
                                }
                            }
                        }
                    }
                }
            }
        }

        return namesStr;
    }

    private String getLNmFnmDobCurSexStr(PersonVO personVO) {
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

    private boolean saveMPR(PersonVO mpr) throws DataProcessingException {
        return storeMPR(mpr, NEDSSConstant.PAT_EDIT);
    }

    /**
     * MPR: is a existing object pull from database, it should have all the unique ID
     * newRevision: is object from memory, should not have ids
     * */
    private boolean update(PersonVO mpr, PersonVO newRevision)
            throws DataProcessingException
    {
        try {
            logger.debug("Starts update mpr, person uid = " + mpr.getThePersonDT().getPersonUid());
            logger.debug("Starts update mpr, person parent uid = " + mpr.getThePersonDT().getPersonParentUid());
            Collection<PersonVO> aNewRevisionList = new ArrayList<> ();
            aNewRevisionList.add(newRevision);

//            MPRUpdateVO mprUpdateVO = new MPRUpdateVO(mpr, aNewRevisionList);
//            logger.debug("Try to get a " + MPRUpdateEngineConstants.DEFAULT_HANDLER + " handler.");
//            MPRUpdateHandler handler = getHandler(MPRUpdateEngineConstants.DEFAULT_HANDLER);
//            logger.debug("The handler is: " + handler);

            //NOTE: even in Legacy this process method always return TRUE
            if(process(mpr))
            {
                // NOTE: legacy param is MPR, but we need to look in to this
                // NOTE: set it to new Revision for now

                // NEW TWEAK
                //revision should not be new in this flow
                mpr.setItDirty(true);
                mpr.setItNew(false);
                // END TWEAK
                return saveMPR(mpr);
            }else
            {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    /**
     * Process MPR
     * */
    private boolean process(PersonVO personVO) {

        //NOTE: Ignore this logic
        //NOTE: let return this as true by default
//        PersonVO mpr = personVO;
//        Collection<EntityLocatorParticipationDT>  col = mpr.getTheEntityLocatorParticipationDTCollection();
//        if(col!=null && col.size()>0)
//        {
//            Iterator<EntityLocatorParticipationDT>  ite = col.iterator();
//            while (ite.hasNext())
//            {
//                EntityLocatorParticipationDT entityLocatorParticipationDT = (EntityLocatorParticipationDT) ite.next();
//                if((entityLocatorParticipationDT.getThePhysicalLocatorDT()!=null && entityLocatorParticipationDT.getThePhysicalLocatorDT().isItDirty())||
//                        (entityLocatorParticipationDT.getTheTeleLocatorDT()!=null && entityLocatorParticipationDT.getTheTeleLocatorDT().isItDirty())||
//                        (entityLocatorParticipationDT.getThePostalLocatorDT()!=null && entityLocatorParticipationDT.getThePostalLocatorDT().isItDirty()))
//                    entityLocatorParticipationDT.setItDirty(true);
//            }
//        }
//        mpr.setItDelete(false);
//        mpr.setItNew(false);
//        mpr.setItDirty(true);
//        mpr.getThePersonDT().setItDirty(true);

        return true;
    }

    private boolean updateWithRevision(PersonVO newRevision) throws DataProcessingException {
        //TODO: Logic to update revison

        if (!newRevision.getThePersonDT().isReentrant()) {
            Long mprUID = newRevision.getThePersonDT().getPersonParentUid();;
            PersonVO mpr = getMPR(mprUID, newRevision);
            mpr.setMPRUpdateValid(newRevision.isMPRUpdateValid());
            if(mpr != null) //With the MPR, update...
            {
                //localId need to be same for MPR and Revision and it need to be set at backend
                newRevision.getThePersonDT().setLocalId(mpr.getThePersonDT().getLocalId());
                return update(mpr, newRevision);
            }
            else //No MPR.
            {
                throw new DataProcessingException("Cannot get a mpr for this person parent uid: "+ mprUID);
            }
        }
        return false;

    }

    private PersonVO getMPR(Long personUid, PersonVO personVOFromPayload) throws DataProcessingException {
        try
        {
            var mpr = getPersonInternal(personUid, personVOFromPayload);
            return mpr;
        }
        catch(Exception e)
        {
            logger.error("CreateException: cannot create an EntityController object."+e.getMessage(),e);
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    /**
     * Retriving existing person
     */
    private PersonVO getPersonInternal(Long personUID, PersonVO personVOFromPayload) throws DataProcessingException {
        PersonVO personClone = personVOFromPayload.deepClone();
        try {
            Person person = null;
            if (personUID != null)
            {
                //TODO: Perhaps this one is looking for all patient info along with assoc tables
                person = patientRepositoryUtil.findExistingPersonByUid(personUID);
            }
            if (person != null)
            {
                //TODO: Update this to return person objects
                //NOTE: This one magically return the entire personVO object, this is including collection like Person Name etc....
                personClone.getThePersonDT().setPersonParentUid(person.getPersonParentUid());
                personClone.getThePersonDT().setPersonUid(person.getPersonUid());
                personClone.getThePersonDT().setLocalId(person.getLocalId());
            }
            // NOTE: Not sure what this for, but this block is NOT for Electronic ELR
            if (person != null && (person.getElectronicInd() != null && !personClone.getThePersonDT().getElectronicInd().equals(NEDSSConstant.ELECTRONIC_IND_ELR))) {
                ArrayList<Object> ldfList = new ArrayList<Object>();
                try {
                    //TODO: THis seem related to version control
//                    LDFHelper ldfHelper = LDFHelper.getInstance();
//                    ldfList = (ArrayList<Object>) ldfHelper.getLDFCollection(personUID, null, nbsSecurityObj);
                } catch (Exception e) {
                    logger.error("Exception occured while retrieving LDFCollection<Object>  = "
                            + e.toString());
                }

                if (ldfList != null) {
                    logger.debug("Before setting LDFCollection<Object>  = " + ldfList.size());
                    personClone.setTheStateDefinedFieldDataDTCollection(ldfList);
                }
            }

            logger.debug("Ent Controller past the find - person = " + person.toString());
            logger.debug("Ent Controllerpast the find - person.getPrimaryKey = " + person.getPersonUid());

        } catch (Exception e) {
            logger.error("EntityControllerEJB.getPersonInternal: " + e.getMessage(), e);
            throw new DataProcessingException(e.getMessage(), e);
        }
        return personClone;

    }

    private boolean storeMPR(PersonVO mpr, String businessTriggerCd) throws DataProcessingException {
        if(setMPR(mpr, businessTriggerCd) != null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

}
