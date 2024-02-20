package gov.cdc.dataprocessing.service.matching;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.MsgType;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model.dt.EdxRuleAlgorothmManagerDT;
import gov.cdc.dataprocessing.model.classic_model.dto.*;
import gov.cdc.dataprocessing.model.classic_model.vo.OrganizationVO;
import gov.cdc.dataprocessing.model.classic_model.vo.PersonVO;
import gov.cdc.dataprocessing.repository.nbs.odse.model.Person;
import gov.cdc.dataprocessing.service.CheckingValueService;
import gov.cdc.dataprocessing.service.interfaces.IPatientMatchingService;
import gov.cdc.dataprocessing.service.model.PersonId;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import gov.cdc.dataprocessing.utilities.component.patient.EdxPatientMatchRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
import gov.cdc.dataprocessing.utilities.model.Coded;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
public class PatientMatchingService implements IPatientMatchingService {
    private static final Logger logger = LoggerFactory.getLogger(PatientMatchingService.class);
    private boolean multipleMatchFound = false;

    private final EdxPatientMatchRepositoryUtil edxPatientMatchRepositoryUtil;
    private final EntityHelper entityHelper;
    private final PatientRepositoryUtil patientRepositoryUtil;

    private final CheckingValueService checkingValueService;


    public PatientMatchingService(
            EdxPatientMatchRepositoryUtil edxPatientMatchRepositoryUtil,
            EntityHelper entityHelper,
            PatientRepositoryUtil patientRepositoryUtil,
            CheckingValueService checkingValueService) {
        this.edxPatientMatchRepositoryUtil = edxPatientMatchRepositoryUtil;
        this.entityHelper = entityHelper;
        this.patientRepositoryUtil = patientRepositoryUtil;
        this.checkingValueService = checkingValueService;
    }


    @Transactional
    public EdxPatientMatchDT getMatchingNokPatient(PersonVO personVO) throws DataProcessingException {
        Long patientUid = personVO.getThePersonDT().getPersonUid();
        String patientRole = personVO.getRole();
        EdxPatientMatchDT edxPatientFoundDT = null;
        EdxPatientMatchDT edxPatientMatchFoundDT = null;
        PersonId patientPersonUid = new PersonId();
        boolean matchFound = false;
        boolean newPatientCreationApplied = false;

        if (patientRole.equalsIgnoreCase(EdxELRConstant.ELR_NEXT_F_KIN_ROLE_CD)) {
            String nameAddStrSt1 = null;
            int nameAddStrSt1hshCd = 0;
            List nameAddressStreetOneStrList = nameAddressStreetOneNOK(personVO);

            if (nameAddressStreetOneStrList != null && !nameAddressStreetOneStrList.isEmpty()) {
                for (int k = 0; k < nameAddressStreetOneStrList.size(); k++) {
                    nameAddStrSt1 = (String) nameAddressStreetOneStrList.get(k);
                    if (nameAddStrSt1 != null) {
                        nameAddStrSt1 = nameAddStrSt1.toUpperCase();
                        nameAddStrSt1hshCd = nameAddStrSt1.hashCode();
                        try {
                            if (nameAddStrSt1 != null) {
                                edxPatientFoundDT = new EdxPatientMatchDT();
                                edxPatientFoundDT.setPatientUid(patientUid);
                                edxPatientFoundDT.setTypeCd(NEDSSConstant.NOK);
                                edxPatientFoundDT.setMatchString(nameAddStrSt1);
                                edxPatientFoundDT.setMatchStringHashCode((long)nameAddStrSt1hshCd);
                            }
                            // Try to get the Next of Kin matching with the match string
                            edxPatientMatchFoundDT = edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(edxPatientFoundDT.getTypeCd(), nameAddStrSt1);
                            if (edxPatientMatchFoundDT.getPatientUid() == null || (edxPatientMatchFoundDT.getPatientUid() != null && edxPatientMatchFoundDT.getPatientUid() <= 0)) {
                                matchFound = false;
                            } else {
                                matchFound = true;
                            }
                        } catch (Exception ex) {
                            logger.error("Error in geting the  matching Next of Kin");
                            throw new DataProcessingException("Error in geting the  matching Next of Kin"
                                    + ex.getMessage(), ex);
                        }
                    }
                }
            }

            if (!matchFound) {
                String nameTelePhone = null;
                int nameTelePhonehshCd = 0;
                List nameTelePhoneStrList = telePhoneTxtNOK(personVO);
                if (nameTelePhoneStrList != null && !nameTelePhoneStrList.isEmpty()) {
                    for (int k = 0; k < nameTelePhoneStrList.size(); k++) {
                        nameTelePhone = (String) nameTelePhoneStrList.get(k);
                        if (nameTelePhone != null) {
                            nameTelePhone = nameTelePhone.toUpperCase();
                            nameTelePhonehshCd = nameTelePhone.hashCode();
                            try {
                                if (nameTelePhone != null) {
                                    edxPatientFoundDT = new EdxPatientMatchDT();
                                    edxPatientFoundDT.setPatientUid(patientUid);
                                    edxPatientFoundDT.setTypeCd(NEDSSConstant.NOK);
                                    edxPatientFoundDT.setMatchString(nameTelePhone);
                                    edxPatientFoundDT.setMatchStringHashCode((long)nameTelePhonehshCd);
                                }
                                // Try to get the matching with the match string
                                edxPatientMatchFoundDT = edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(edxPatientFoundDT.getTypeCd(), nameTelePhone);
                                if (edxPatientMatchFoundDT.getPatientUid() == null || (edxPatientMatchFoundDT.getPatientUid() != null && edxPatientMatchFoundDT.getPatientUid() <= 0)) {
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
                }
            }

            if (!matchFound) {
                if (personVO.getTheEntityIdDTCollection() != null) {
                    Collection<EntityIdDT> newEntityIdDTColl = new ArrayList<EntityIdDT>();
                    Iterator<EntityIdDT> iter = personVO.getTheEntityIdDTCollection().iterator();
                    while (iter.hasNext()) {
                        EntityIdDT entityIdDT = (EntityIdDT) iter.next();
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
                        patientPersonUid = setAndCreateNewPerson(personVO);
                        personVO.getThePersonDT().setPersonParentUid(patientPersonUid.getPersonParentId());
                        personVO.getThePersonDT().setLocalId(patientPersonUid.getLocalId());
                        personVO.getThePersonDT().setPersonUid(patientPersonUid.getPersonId());
                        newPatientCreationApplied = true;
                    }
                } catch (Exception e) {
                    logger.error("Error in getting the entity Controller or Setting the Patient" + e.getMessage());
                    throw new DataProcessingException("Error in getting the entity Controller or Setting the Patient" + e.getMessage(), e);
                }
                personVO.setPatientMatchedFound(false);
            }
            else {
                personVO.setPatientMatchedFound(true);
            }

            try {
                if (!newPatientCreationApplied && personVO.getPatientMatchedFound()) {
                    personVO.getThePersonDT().setPersonParentUid(edxPatientMatchFoundDT.getPatientUid());
                    patientPersonUid = updateExistingPatient(personVO, NEDSSConstant.PAT_CR, personVO.getThePersonDT().getPersonParentUid());

                    personVO.getThePersonDT().setPersonParentUid(patientPersonUid.getPersonParentId());
                    personVO.getThePersonDT().setLocalId(patientPersonUid.getLocalId());
                    personVO.getThePersonDT().setPersonUid(patientPersonUid.getPersonId());
                }

            } catch (Exception e) {
                logger.error("Error in getting the entity Controller or Setting the Patient" + e.getMessage());
                throw new DataProcessingException("Error in getting the entity Controller or Setting the Patient" + e.getMessage(), e);
            }
        }

        return edxPatientMatchFoundDT;
    }



    @Transactional
    public EDXActivityDetailLogDT getMatchingProvider(PersonVO personVO) throws DataProcessingException {
        Long entityUid = personVO.getThePersonDT().getPersonUid();
        Collection<EdxEntityMatchDT> coll = new ArrayList<EdxEntityMatchDT>();
        EDXActivityDetailLogDT edxActivityDetailLogDT = new EDXActivityDetailLogDT();
        String DET_MSG_ENTITY_EXISTS_SUCCESS = "Provider entity found with entity uid : ";
        String DET_MSG_ENTITY_EXISTS_FAIL_NEW = "Provider not found. New Provider created with person uid : ";
        // creating new localID DT for
        // local identifier
        EdxEntityMatchDT theEdxEntityMatchDT = null;
        String localId = null;
        int localIdhshCd = 0;
        localId = getLocalId(personVO); // if id = 123
        if (localId != null) {
            localId = localId.toUpperCase();
            localIdhshCd = localId.hashCode();
        }
        try {
            // Try to get the matching with the match string
            EdxEntityMatchDT edxEntityMatchingDT = edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(NEDSSConstant.PRV, localId);
            if (edxEntityMatchingDT != null && edxEntityMatchingDT.getEntityUid() != null) {
                edxActivityDetailLogDT.setRecordId("" + edxEntityMatchingDT.getEntityUid());
                edxActivityDetailLogDT.setComment(DET_MSG_ENTITY_EXISTS_SUCCESS + edxEntityMatchingDT.getEntityUid());
                edxActivityDetailLogDT.setRecordType("" + MsgType.Provider);
                edxActivityDetailLogDT.setRecordName("PHCR_IMPORT");
                edxActivityDetailLogDT.setLogType("" + EdxRuleAlgorothmManagerDT.STATUS_VAL.Success);
                return edxActivityDetailLogDT;
            }
        } catch (Exception ex) {
            logger.error("Error in geting the  matching Provider");
            throw new DataProcessingException("Error in geting the  matching Provider" + ex.getMessage(), ex);
        }
        if (localId != null) {
            theEdxEntityMatchDT = new EdxEntityMatchDT();
            theEdxEntityMatchDT.setTypeCd(NEDSSConstant.PRV);
            theEdxEntityMatchDT.setMatchString(localId);
            theEdxEntityMatchDT.setMatchStringHashCode((long)localIdhshCd);
        }

        // Matching the Identifier (i.e. NPI)
        String identifier = null;
        int identifierHshCd = 0;
        List identifierList = null;
        identifierList = getIdentifier(personVO);
        if (identifierList != null && !identifierList.isEmpty()) {
            for (int k = 0; k < identifierList.size(); k++) {
                identifier = (String) identifierList.get(k);
                if (identifier != null) {
                    identifier = identifier.toUpperCase();
                    identifierHshCd = identifier.hashCode();
                }
                try {
                    // Try to get the matching with the match string
                    EdxEntityMatchDT edxEntityMatchingDT = edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(NEDSSConstant.PRV, identifier);
                    if (edxEntityMatchingDT != null && edxEntityMatchingDT.getEntityUid() != null) {
                        if (theEdxEntityMatchDT != null) {
                            theEdxEntityMatchDT.setEntityUid(edxEntityMatchingDT.getEntityUid());
                            if (personVO.getRole() == null) {
                                edxPatientMatchRepositoryUtil.saveEdxEntityMatch(theEdxEntityMatchDT);
                            }
                        }
                        edxActivityDetailLogDT.setRecordId("" + edxEntityMatchingDT.getEntityUid());
                        edxActivityDetailLogDT.setComment(DET_MSG_ENTITY_EXISTS_SUCCESS + edxEntityMatchingDT.getEntityUid());
                        edxActivityDetailLogDT.setRecordType("" + MsgType.Provider);
                        edxActivityDetailLogDT.setRecordName("PHCR_IMPORT");
                        edxActivityDetailLogDT.setLogType("" + EdxRuleAlgorothmManagerDT.STATUS_VAL.Success);
                        return edxActivityDetailLogDT;
                    }
                } catch (Exception ex) {
                    logger.error("Error in geting the  matching Provider");
                    throw new DataProcessingException("Error in geting the  matching Provider" + ex.getMessage(), ex);
                }
                if (identifier != null) {
                    EdxEntityMatchDT edxEntityMatchDT = new EdxEntityMatchDT();
                    edxEntityMatchDT.setTypeCd(NEDSSConstant.PRV);
                    edxEntityMatchDT.setMatchString(identifier);
                    edxEntityMatchDT.setMatchStringHashCode((long) identifierHshCd);
                    coll.add(edxEntityMatchDT);
                }

            }
        }

        // Matching with name and address with street address1 alone
        String nameAddStrSt1 = null;
        int nameAddStrSt1hshCd = 0;
        nameAddStrSt1 = nameAddressStreetOneProvider(personVO);
        if (nameAddStrSt1 != null) {
            nameAddStrSt1 = nameAddStrSt1.toUpperCase();
            nameAddStrSt1hshCd = nameAddStrSt1.hashCode();
            if (nameAddStrSt1 != null) {
                try {
                    // Try to get the matching with match string
                    EdxEntityMatchDT edxEntityMatchingDT = edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(NEDSSConstant.PRV, nameAddStrSt1);
                    if (edxEntityMatchingDT != null && edxEntityMatchingDT.getEntityUid() != null) {
                        if (theEdxEntityMatchDT != null) {
                            theEdxEntityMatchDT.setEntityUid(edxEntityMatchingDT.getEntityUid());
                            if (personVO.getRole() == null) {
                                edxPatientMatchRepositoryUtil.saveEdxEntityMatch(theEdxEntityMatchDT);
                            }
                        }
                        edxActivityDetailLogDT.setRecordId("" + edxEntityMatchingDT.getEntityUid());
                        edxActivityDetailLogDT.setComment(DET_MSG_ENTITY_EXISTS_SUCCESS + edxEntityMatchingDT.getEntityUid());
                        edxActivityDetailLogDT.setRecordType("" + MsgType.Provider);
                        edxActivityDetailLogDT.setRecordName("PHCR_IMPORT");
                        edxActivityDetailLogDT.setLogType("" + EdxRuleAlgorothmManagerDT.STATUS_VAL.Success);
                        return edxActivityDetailLogDT;
                    }
                } catch (Exception ex) {
                    logger.error("Error in geting the  matching Provider");
                    throw new DataProcessingException("Error in geting the  matching Provider" + ex.getMessage(), ex);
                }
            }
        }

        // Continue for name Telephone with no extension
        String nameTelePhone = null;
        int nameTelePhonehshCd = 0;
        nameTelePhone = telePhoneTxtProvider(personVO);
        if (nameTelePhone != null) {
            nameTelePhone = nameTelePhone.toUpperCase();
            nameTelePhonehshCd = nameTelePhone.hashCode();
            if (nameTelePhone != null) {
                try {
                    // Try to get the matching with the match string
                    EdxEntityMatchDT edxEntityMatchingDT = edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(NEDSSConstant.PRV, nameTelePhone);
                    if (edxEntityMatchingDT != null && edxEntityMatchingDT.getEntityUid() != null) {
                        if (theEdxEntityMatchDT != null) {
                            theEdxEntityMatchDT.setEntityUid(edxEntityMatchingDT.getEntityUid());
                            if (personVO.getRole() == null) {
                                edxPatientMatchRepositoryUtil.saveEdxEntityMatch(theEdxEntityMatchDT);
                            }
                        }
                        edxActivityDetailLogDT.setRecordId("" + edxEntityMatchingDT.getEntityUid());
                        edxActivityDetailLogDT.setComment(DET_MSG_ENTITY_EXISTS_SUCCESS + edxEntityMatchingDT.getEntityUid());
                        edxActivityDetailLogDT.setRecordType("" + MsgType.Provider);
                        edxActivityDetailLogDT.setRecordName("PHCR_IMPORT");
                        edxActivityDetailLogDT.setLogType("" + EdxRuleAlgorothmManagerDT.STATUS_VAL.Success);
                        return edxActivityDetailLogDT;
                    }
                } catch (Exception ex) {
                    logger.error("Error in geting the  matching Provider");
                    throw new DataProcessingException("Error in geting the  matching Provider" + ex.getMessage(), ex);
                }

            }
        }

        // Create the provider in case if the provider is not there in the DB
        try {
            if (personVO.getThePersonDT().getCd().equals(NEDSSConstant.PRV)) { // Provider
                String businessTriggerCd = NEDSSConstant.PRV_CR;
                /**
                 * TODO: PERSISTING PROVIDER HERE
                 * */
                entityUid = processingProvider(personVO, "PROVIDER", businessTriggerCd);
            }
        } catch (Exception e) {
            logger.error("Error in getting the entity Controller or Setting the Organization" + e.getMessage());
            throw new DataProcessingException("Error in getting the entity Controller or Setting the Organization" + e.getMessage(), e);
        }


        // Create the name and address with no street 2(only street1)
        if (nameAddStrSt1 != null) {
            EdxEntityMatchDT edxEntityMatchDT = new EdxEntityMatchDT();
            edxEntityMatchDT.setEntityUid(entityUid);
            edxEntityMatchDT.setTypeCd(NEDSSConstant.PRV);
            edxEntityMatchDT.setMatchString(nameAddStrSt1);
            edxEntityMatchDT.setMatchStringHashCode((long)nameAddStrSt1hshCd);
            try {
                if (personVO.getRole() == null) {
                    edxPatientMatchRepositoryUtil.saveEdxEntityMatch(edxEntityMatchDT);
                }
            } catch (Exception e) {
                logger.error("Error in creating the EdxEntityMatchDT with nameAddStrSt1:" + nameAddStrSt1 + " " + e.getMessage());
                throw new DataProcessingException(e.getMessage(), e);
            }

        }

        // Create the name and address with nameTelePhone
        if (nameTelePhone != null) {
            EdxEntityMatchDT edxEntityMatchDT = new EdxEntityMatchDT();
            edxEntityMatchDT.setEntityUid(entityUid);
            edxEntityMatchDT.setTypeCd(NEDSSConstant.PRV);
            edxEntityMatchDT.setMatchString(nameTelePhone);
            edxEntityMatchDT.setMatchStringHashCode((long)(nameTelePhonehshCd));
            try {
                if (personVO.getRole() == null) {
                    edxPatientMatchRepositoryUtil.saveEdxEntityMatch(edxEntityMatchDT);
                }
            } catch (Exception e) {
                logger.error("Error in creating the EdxEntityMatchDT with nameTelePhone:" + nameTelePhone + " " + e.getMessage());
                throw new DataProcessingException(e.getMessage(), e);
            }
        }
        if (theEdxEntityMatchDT != null)
        {
            coll.add(theEdxEntityMatchDT);
        }
        if (coll != null) {
            Iterator<EdxEntityMatchDT> it = coll.iterator();
            while (it.hasNext()) {
                EdxEntityMatchDT edxEntityMatchDT = (EdxEntityMatchDT) it.next();
                edxEntityMatchDT.setEntityUid(entityUid);
                if (personVO.getRole() == null) {
                    edxPatientMatchRepositoryUtil.saveEdxEntityMatch(edxEntityMatchDT);
                }
            }
        }
        // returnung the entity Uid which is just created
        edxActivityDetailLogDT.setRecordId("" + entityUid);
        edxActivityDetailLogDT.setComment("" + DET_MSG_ENTITY_EXISTS_FAIL_NEW + edxActivityDetailLogDT.getRecordId());
        edxActivityDetailLogDT.setRecordType("" + MsgType.Provider);
        edxActivityDetailLogDT.setRecordName("PHCR_IMPORT");
        edxActivityDetailLogDT.setLogType("" + EdxRuleAlgorothmManagerDT.STATUS_VAL.Success);
        return edxActivityDetailLogDT;
    }

    
    @Transactional
    public EdxPatientMatchDT getMatchingPatient(PersonVO personVO) throws DataProcessingException {
        Long patientUid = personVO.getThePersonDT().getPersonUid();
        String cd = personVO.getThePersonDT().getCd();
        String patientRole = personVO.getRole();
        EdxPatientMatchDT edxPatientFoundDT = null;
        EdxPatientMatchDT edxPatientMatchFoundDT = null;
        PersonId patientPersonUid = new PersonId();
        boolean matchFound = false;
        boolean lrIDExists = true;

        boolean newPatientCreationApplied = false;

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
                        patientPersonUid = setAndCreateNewPerson(personVO);
                        personVO.getThePersonDT().setPersonParentUid(patientPersonUid.getPersonParentId());
                        personVO.getThePersonDT().setLocalId(patientPersonUid.getLocalId());
                        personVO.getThePersonDT().setPersonUid(patientPersonUid.getPersonId());
                        newPatientCreationApplied = true;
                    }
                } catch (Exception e) {
                    logger.error("Error in getting the entity Controller or Setting the Patient" + e.getMessage(), e);
                    throw new DataProcessingException("Error in getting the entity Controller or Setting the Patient" + e.getMessage(), e);
                }
                personVO.setPatientMatchedFound(false);
            }
            else {
                personVO.setPatientMatchedFound(true);
            }

            //NOTE: In this flow, if new patient, revision record is still get inserted
            //NOTE: if existing pateint, revision also insrted
            try {

                /**
                 * NOTE:
                 * Regarding New or Existing Patient
                 * This logic will do Patient Hash update and do Patient Revision update
                 * */

                /**
                 * 2.0 NOTE: if new patient flow, skip revision
                 * otherwise: go to update existing patient
                 * */


                if (!newPatientCreationApplied && personVO.getPatientMatchedFound()) {
                    personVO.getThePersonDT().setPersonParentUid(edxPatientMatchFoundDT.getPatientUid());
                    patientPersonUid = updateExistingPatient(personVO, NEDSSConstant.PAT_CR, personVO.getThePersonDT().getPersonParentUid());

                    personVO.getThePersonDT().setPersonParentUid(patientPersonUid.getPersonParentId());
                    personVO.getThePersonDT().setLocalId(patientPersonUid.getLocalId());
                    personVO.getThePersonDT().setPersonUid(patientPersonUid.getPersonId());
                }
                else if (newPatientCreationApplied) {
                    setPatientHashCd(personVO);
                }
            } catch (Exception e) {
                logger.error("Error in getting the entity Controller or Setting the Patient" + e.getMessage());
                throw new DataProcessingException("Error in getting the entity Controller or Setting the Patient" + e.getMessage(), e);
            }

        }
        return edxPatientMatchFoundDT;
    }

    public boolean getMultipleMatchFound() {
        return multipleMatchFound;
    }
    private PersonId setAndCreateNewPerson(PersonVO psn) throws DataProcessingException {
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
        person = patientRepositoryUtil.createPerson(personVO);
        personUID.setPersonId(person.getPersonUid());
        personUID.setPersonParentId(person.getPersonParentUid());
        personUID.setLocalId(person.getLocalId());

        logger.debug(" EntityControllerEJB.setPerson() Person Created");
        return personUID;

    }

    private PersonId updateExistingPatient(PersonVO personVO, String businessTriggerCd, Long personParentUid) throws DataProcessingException {
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
                        }
                        // NOTE: Person matching doesn't seem to hit this
                        else
                        {
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

    private List<String> getIdentifierForProvider(PersonVO personVO) throws DataProcessingException {
        String carrot = "^";
        List<String> identifierList = new ArrayList<String>();
        String identifier = null;
        Collection<EntityIdDT> newEntityIdDTColl = new ArrayList<>();
        try{
            if (personVO.getTheEntityIdDTCollection() != null
                    && personVO.getTheEntityIdDTCollection().size() > 0) {
                Collection<EntityIdDT> entityIdDTColl = personVO.getTheEntityIdDTCollection();
                Iterator<EntityIdDT> entityIdIterator = entityIdDTColl.iterator();
                while (entityIdIterator.hasNext()) {
                    EntityIdDT entityIdDT = (EntityIdDT) entityIdIterator.next();
                    if ((entityIdDT.getStatusCd().equalsIgnoreCase(NEDSSConstant.STATUS_ACTIVE))) {
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

//                                Coded coded = new Coded();
//                                coded.setCode(entityIdDT.getAssigningAuthorityCd());
//                                coded.setCodesetName(NEDSSConstant.EI_AUTH_PRV);
//                                coded.setCodesetTableName(DataTable.CODE_VALUE_GENERAL);
//                                NotificationSRTCodeLookupTranslationDAOImpl lookupDAO = new NotificationSRTCodeLookupTranslationDAOImpl();
//                                lookupDAO.retrieveSRTCodeInfo(coded);

                                Coded coded = new Coded();
                                coded.setCode(entityIdDT.getAssigningAuthorityCd());
                                coded.setCodesetName(NEDSSConstant.EI_AUTH);
                                coded.setCodesetTableName("Code_value_general");

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
                                String errorMessage = "The assigning authority "
                                        + entityIdDT.getAssigningAuthorityCd()
                                        + " does not exists in the system. ";
                                logger.debug(ex.getMessage() + errorMessage);
                            }
                        }
                        if (entityIdDT.getTypeCd()!=null && !entityIdDT.getTypeCd().equalsIgnoreCase("LR")) {
                            newEntityIdDTColl.add(entityIdDT);
                        }
                        if (identifier != null) {
                            identifierList.add(identifier);
                        }

                    }

                }

            }
            personVO.setTheEntityIdDTCollection(newEntityIdDTColl);

        }catch (Exception ex) {
            String errorMessage = "Exception while creating hashcode for Provider entity IDs . ";
            logger.debug(ex.getMessage() + errorMessage);
            throw new DataProcessingException(errorMessage, ex);
        }
        return identifierList;

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

        //TODO: Change this to Update Eixsting Patient
        patientRepositoryUtil.updateExistingPerson(personVO);

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

    private PersonId getPersonInternalV2(Long personUID) throws DataProcessingException {
        PersonId personId;
        try {
            Person person = null;
            if (personUID != null)
            {
                person = patientRepositoryUtil.findExistingPersonByUid(personUID);
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


    private List<String> nameAddressStreetOneNOK(PersonVO personVO) {
        String nameAddStr = null;
        String carrot = "^";
        List<String> nameAddressStreetOnelNOKist = new ArrayList();
        if (personVO.getTheEntityLocatorParticipationDTCollection() != null && personVO.getTheEntityLocatorParticipationDTCollection().size() > 0) {
            Iterator<EntityLocatorParticipationDT> addIter = personVO.getTheEntityLocatorParticipationDTCollection().iterator();

            while (addIter.hasNext()) {
                EntityLocatorParticipationDT entLocPartDT = (EntityLocatorParticipationDT) addIter.next();
                if (entLocPartDT.getClassCd() != null && entLocPartDT.getRecordStatusCd() !=null
                        && entLocPartDT.getRecordStatusCd().equalsIgnoreCase(NEDSSConstant.RECORD_STATUS_ACTIVE) && entLocPartDT.getClassCd().equals(
                        NEDSSConstant.POSTAL)) {
                    if (entLocPartDT.getCd() != null) {
                        PostalLocatorDT postLocDT = entLocPartDT.getThePostalLocatorDT();
                        if (postLocDT != null) {
                            if ((postLocDT.getStreetAddr1() != null && !postLocDT.getStreetAddr1().equals(""))
                                    && (postLocDT.getCityDescTxt() != null && !postLocDT.getCityDescTxt().equals(""))
                                    && (postLocDT.getStateCd() != null && !postLocDT.getStateCd().equals("")) && (postLocDT.getZipCd() != null
                                    && !postLocDT.getZipCd().equals(""))) {

                                nameAddStr = carrot + postLocDT.getStreetAddr1() + carrot + postLocDT.getCityDescTxt() + carrot
                                        + postLocDT.getStateCd() + carrot + postLocDT.getZipCd();
                            }
                        }
                    }
                }
            }
            if (nameAddStr != null)
            {
                nameAddStr = getNamesStr(personVO) + nameAddStr;
            }
            nameAddressStreetOnelNOKist.add(nameAddStr);

        }

        return nameAddressStreetOnelNOKist;
    }

    private List<String> telePhoneTxtNOK(PersonVO personVO) {
        String nameTeleStr = null;
        String carrot = "^";
        List<String> telePhoneTxtList = new ArrayList();
        if (personVO.getTheEntityLocatorParticipationDTCollection() != null && personVO.getTheEntityLocatorParticipationDTCollection().size() > 0) {
            Iterator<EntityLocatorParticipationDT> addIter = personVO.getTheEntityLocatorParticipationDTCollection().iterator();
            while (addIter.hasNext()) {
                EntityLocatorParticipationDT entLocPartDT = (EntityLocatorParticipationDT) addIter.next();
                if (entLocPartDT.getClassCd() != null && entLocPartDT.getClassCd().equals(NEDSSConstant.TELE)
                        && entLocPartDT.getRecordStatusCd()!=null && entLocPartDT.getRecordStatusCd().equalsIgnoreCase(NEDSSConstant.RECORD_STATUS_ACTIVE)) {
                    if (entLocPartDT.getCd() != null) {
                        TeleLocatorDT teleLocDT = entLocPartDT.getTheTeleLocatorDT();
                        if (teleLocDT != null && teleLocDT.getPhoneNbrTxt() != null && !teleLocDT.getPhoneNbrTxt().equals(""))
                        {
                            nameTeleStr = carrot + teleLocDT.getPhoneNbrTxt();
                        }

                    }
                    if (nameTeleStr != null) {

                        if (getNamesStr(personVO) != null) {
                            nameTeleStr = getNamesStr(personVO) + nameTeleStr;
                            telePhoneTxtList.add(nameTeleStr);
                        } else {
                            return null;
                        }
                    }
                }

            }
        }

        return telePhoneTxtList;
    }

    // Creating String for name + telephone for Providers
    private String telePhoneTxtProvider(PersonVO personVO) {
        String nameTeleStr = null;
        String carrot = "^";

        if (personVO.getTheEntityLocatorParticipationDTCollection() != null
                && personVO.getTheEntityLocatorParticipationDTCollection().size() > 0) {
            Iterator<EntityLocatorParticipationDT> addIter = personVO.getTheEntityLocatorParticipationDTCollection().iterator();
            while (addIter.hasNext()) {
                EntityLocatorParticipationDT entLocPartDT = (EntityLocatorParticipationDT) addIter.next();
                if (entLocPartDT.getClassCd() != null && entLocPartDT.getClassCd().equals(NEDSSConstant.TELE)) {
                    if (entLocPartDT.getCd() != null && entLocPartDT.getCd().equals(NEDSSConstant.PHONE)) {
                        TeleLocatorDT teleLocDT = entLocPartDT.getTheTeleLocatorDT();
                        if (teleLocDT != null && teleLocDT.getPhoneNbrTxt() != null && !teleLocDT.getPhoneNbrTxt().equals(""))
                            nameTeleStr = carrot + teleLocDT.getPhoneNbrTxt();

                    }
                }
            }
        }
        if (nameTeleStr != null)
        {
            nameTeleStr = getNameStringForProvider(personVO) + nameTeleStr;
        }
        return nameTeleStr;
    }

    // getting Last name,First name for the providers
    private String getNameStringForProvider(PersonVO personVO) {
        String nameStr = null;
        if (personVO.getThePersonNameDTCollection() != null && personVO.getThePersonNameDTCollection().size() > 0) {
            Collection<PersonNameDT> PersonNameDTColl = personVO.getThePersonNameDTCollection();
            Iterator<PersonNameDT> nameCollIter = PersonNameDTColl.iterator();
            while (nameCollIter.hasNext()) {
                PersonNameDT personNameDT = (PersonNameDT) nameCollIter.next();
                if (personNameDT.getNmUseCd() == null)
                {
                    String Message = "personNameDT.getNmUseCd() is null";
                    logger.debug(Message);
                }
                if (personNameDT.getNmUseCd() != null && personNameDT.getNmUseCd().equals(NEDSSConstant.LEGAL)) {
                    if (personNameDT.getLastNm() != null || personNameDT.getFirstNm() != null)
                        nameStr = personNameDT.getLastNm() + personNameDT.getFirstNm();
                }
            }
        }
        return nameStr;
    }

    // Creating string for name and address for providers
    private String nameAddressStreetOneProvider(PersonVO personVO) {
        String nameAddStr = null;
        String carrot = "^";
        if (personVO.getTheEntityLocatorParticipationDTCollection() != null && personVO.getTheEntityLocatorParticipationDTCollection().size() > 0) {
            Iterator<EntityLocatorParticipationDT> addIter = personVO.getTheEntityLocatorParticipationDTCollection().iterator();
            while (addIter.hasNext()) {
                EntityLocatorParticipationDT entLocPartDT = (EntityLocatorParticipationDT) addIter.next();
                if (entLocPartDT.getClassCd() != null && entLocPartDT.getClassCd().equals(NEDSSConstant.POSTAL)) {
                    if (entLocPartDT.getCd() != null
                            && entLocPartDT.getCd().equals(NEDSSConstant.OFFICE_CD)
                            && entLocPartDT.getUseCd() != null
                            && entLocPartDT.getUseCd().equals(NEDSSConstant.WORK_PLACE)) {
                        PostalLocatorDT postLocDT = entLocPartDT.getThePostalLocatorDT();
                        if (postLocDT != null) {
                            if ((postLocDT.getStreetAddr1() != null && !postLocDT.getStreetAddr1().equals(""))
                                    && (postLocDT.getCityDescTxt() != null && !postLocDT.getCityDescTxt().equals(""))
                                    && (postLocDT.getStateCd() != null && !postLocDT.getStateCd().equals(""))
                                    && (postLocDT.getZipCd() != null && !postLocDT.getZipCd().equals(""))) {
                                nameAddStr = carrot
                                        + postLocDT.getStreetAddr1() + carrot
                                        + postLocDT.getCityDescTxt() + carrot
                                        + postLocDT.getStateCd() + carrot
                                        + postLocDT.getZipCd();
                            }
                        }
                    }
                }
            }

        }
        if (nameAddStr != null)
            nameAddStr = getNameStringForProvider(personVO) + nameAddStr;
        return nameAddStr;
    }


    private Long processingProvider(PersonVO personVO, String businessObjLookupName, String businessTriggerCd) throws DataProcessingException {
        try {
            boolean callOrgHashCode= false;
            if(personVO.isItNew() && personVO.getThePersonDT().isItNew() && personVO.getThePersonDT().getElectronicInd().equalsIgnoreCase("Y")
                    && !personVO.getThePersonDT().isCaseInd()){
                callOrgHashCode= true;
                personVO.getThePersonDT().setEdxInd("Y");
            }
            long personUid= persistingProvider(personVO, "PROVIDER", businessTriggerCd );

            if(callOrgHashCode){
                try {
                    personVO.getThePersonDT().setPersonUid(personUid);
                    /**
                     * THIS CODE HAS THING TO DO WITH ORGANIZATION
                     * */
                    setProvidertoEntityMatch(personVO);
                } catch (Exception e) {
                    logger.error("EntityControllerEJB.setProvider method exception thrown for matching criteria:"+e);
                    throw new DataProcessingException("EntityControllerEJB.setProvider method exception thrown for matching criteria:"+e);
                }
            }
            return personUid;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage());
        }
    }

    private Long persistingProvider(PersonVO personVO, String businessObjLookupName, String businessTriggerCd) throws DataProcessingException  {
        Long personUID =  -1L;
        String localId = "";
        boolean isELRCase = false;
        try {
            localId = personVO.getThePersonDT().getLocalId();
            if (localId == null) {
                personVO.getThePersonDT().setEdxInd("Y");
                isELRCase = true;
            }

            /**
             * TODO: double check this
             * */
//            PrepareVOUtils prepVOUtils = new PrepareVOUtils();
//            RootDTInterface personDT = prepVOUtils.prepareVO(
//                    personVO.getThePersonDT(), businessObjLookupName,
//                    businessTriggerCd, "PERSON", "BASE", nbsSecurityObj);
//
//            if (personVO.getThePersonDT().isItNew()
//                    && !(businessObjLookupName
//                    .equalsIgnoreCase(NEDSSConstant.businessObjLookupNamePROVIDER)))
//                personDT.setLocalId(localId);
//
//            personVO.setThePersonDT((PersonDT) personDT);

            Collection<EntityLocatorParticipationDT> collParLocator = null;
            Collection<RoleDT> colRole = null;
            Collection<ParticipationDT> colPar = null;


            collParLocator = personVO.getTheEntityLocatorParticipationDTCollection();
            if (collParLocator != null) {
                entityHelper.iterateELPDTForEntityLocatorParticipation(collParLocator);
                personVO.setTheEntityLocatorParticipationDTCollection(collParLocator);
            }

            colRole = personVO.getTheRoleDTCollection();
            if (colRole != null) {
                entityHelper.iterateRDT(colRole);
                personVO.setTheRoleDTCollection(colRole);
            }
            colPar = personVO.getTheParticipationDTCollection();
            if (colPar != null) {
                entityHelper.iteratePDTForParticipation(colPar);
                personVO.setTheParticipationDTCollection(colPar);
            }

            patientRepositoryUtil.preparePersonNameBeforePersistence(personVO);

            if (personVO.isItNew()) {
                Person p = patientRepositoryUtil.createPerson(personVO);
                personUID = p.getPersonUid();
            }
            else {
                patientRepositoryUtil.updateExistingPerson(personVO);
                personUID = personVO.getThePersonDT().getPersonUid();

            }

            //NEW FLOW WONT HIT THIS
//            if(isELRCase){
//                try {
//                    personVO.getThePersonDT().setPersonUid(personUID);
//                    personVO.getThePersonDT().setPersonParentUid(personUID);
//                    setPatientHashCd(personVO);
//                } catch (Exception e) {
//                    logger.error("NEDSSAppException thrown while creating hashcode for the ELR patient."+e);
//                    throw new DataProcessingException(e.getMessage());
//                }
//            }

        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage());
        }
        return personUID;

    }

    private void setProvidertoEntityMatch(PersonVO personVO) throws Exception {

        Long entityUid = personVO.getThePersonDT().getPersonUid();
        String identifier = null;
        int identifierHshCd = 0;
        List identifierList = null;
        identifierList = getIdentifierForProvider(personVO);
        if (identifierList != null && !identifierList.isEmpty()) {
            for (int k = 0; k < identifierList.size(); k++) {
                identifier = (String) identifierList.get(k);
                if (identifier != null)
                {
                    identifier = identifier.toUpperCase();
                }
                identifierHshCd = identifier.hashCode();
                if (identifier != null) {
                    EdxEntityMatchDT edxEntityMatchDT = new EdxEntityMatchDT();
                    edxEntityMatchDT.setEntityUid(entityUid);
                    edxEntityMatchDT.setTypeCd(NEDSSConstant.PRV);
                    edxEntityMatchDT.setMatchString(identifier);
                    edxEntityMatchDT.setMatchStringHashCode((long)identifierHshCd);
                    try {
                        edxPatientMatchRepositoryUtil.saveEdxEntityMatch(edxEntityMatchDT);
                    } catch (Exception e) {
                        logger.error("Error in creating the EdxEntityMatchDT with identifier:" + identifier + " " + e.getMessage());
                        throw new DataProcessingException(e.getMessage(), e);
                    }
                }

            }

        }

        // Matching with name and address with street address1 alone
        String nameAddStrSt1 = null;
        int nameAddStrSt1hshCd = 0;
        nameAddStrSt1 = nameAddressStreetOneProvider(personVO);
        if (nameAddStrSt1 != null) {
            nameAddStrSt1 = nameAddStrSt1.toUpperCase();
            nameAddStrSt1hshCd = nameAddStrSt1.hashCode();
        }

        // Continue for name Telephone with no extension
        String nameTelePhone = null;
        int nameTelePhonehshCd = 0;
        nameTelePhone = telePhoneTxtProvider(personVO);
        if (nameTelePhone != null) {
            nameTelePhone = nameTelePhone.toUpperCase();
            nameTelePhonehshCd = nameTelePhone.hashCode();
        }

        EdxEntityMatchDT edxEntityMatchDT = null;
        // Create the name and address with no street 2(only street1)
        if (nameAddStrSt1 != null) {
            edxEntityMatchDT = new EdxEntityMatchDT();
            edxEntityMatchDT.setEntityUid(entityUid);
            edxEntityMatchDT.setTypeCd(NEDSSConstant.PRV);
            edxEntityMatchDT.setMatchString(nameAddStrSt1);
            edxEntityMatchDT.setMatchStringHashCode((long)nameAddStrSt1hshCd);
            try {
                edxPatientMatchRepositoryUtil.saveEdxEntityMatch(edxEntityMatchDT);
            } catch (Exception e) {
                logger.error("Error in creating the EdxEntityMatchDT with nameAddStrSt1:" + nameAddStrSt1 + " " + e.getMessage());
                throw new DataProcessingException(e.getMessage(), e);
            }

        }
        // Create the name and address with nameTelePhone
        if (nameTelePhone != null) {
            edxEntityMatchDT = new EdxEntityMatchDT();
            edxEntityMatchDT.setEntityUid(entityUid);
            edxEntityMatchDT.setTypeCd(NEDSSConstant.PRV);
            edxEntityMatchDT.setMatchString(nameTelePhone);
            edxEntityMatchDT.setMatchStringHashCode((long)nameTelePhonehshCd);
            try {
                edxPatientMatchRepositoryUtil.saveEdxEntityMatch(edxEntityMatchDT);
            } catch (Exception e) {
                logger.error("Error in creating the EdxEntityMatchDT with nameTelePhone:" + nameTelePhone + " " + e.getMessage());
                throw new DataProcessingException(e.getMessage(), e);
            }
        }
        if (edxEntityMatchDT != null) {
            patientRepositoryUtil.updateExistingPersonEdxIndByUid(edxEntityMatchDT.getEntityUid());
        }

    }



}
