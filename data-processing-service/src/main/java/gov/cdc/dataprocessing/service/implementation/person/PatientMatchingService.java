package gov.cdc.dataprocessing.service.implementation.person;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.matching.EdxPatientMatchDto;
import gov.cdc.dataprocessing.service.implementation.cache.CachingValueService;
import gov.cdc.dataprocessing.service.implementation.person.base.PatientMatchingBaseService;
import gov.cdc.dataprocessing.service.interfaces.person.IPatientMatchingService;
import gov.cdc.dataprocessing.service.model.person.PersonId;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import gov.cdc.dataprocessing.utilities.component.patient.EdxPatientMatchRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static gov.cdc.dataprocessing.constant.elr.EdxELRConstant.LOG_ERROR_ENTITY_PATIENT;
import static gov.cdc.dataprocessing.constant.elr.EdxELRConstant.LOG_ERROR_MATCHING_PATIENT;

@Service
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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201"})
public class PatientMatchingService extends PatientMatchingBaseService implements IPatientMatchingService {
    private static final Logger logger = LoggerFactory.getLogger(PatientMatchingService.class);
    private boolean multipleMatchFound = false;

    public PatientMatchingService(
            EdxPatientMatchRepositoryUtil edxPatientMatchRepositoryUtil,
            EntityHelper entityHelper,
            PatientRepositoryUtil patientRepositoryUtil,
            CachingValueService cachingValueService,
            PrepareAssocModelHelper prepareAssocModelHelper) {
        super(edxPatientMatchRepositoryUtil, entityHelper, patientRepositoryUtil, cachingValueService, prepareAssocModelHelper);
    }

    @SuppressWarnings({"java:S6541", "java:S3776"})
    @Transactional
    public EdxPatientMatchDto getMatchingPatient(PersonContainer personContainer) throws DataProcessingException {
        Long patientUid = personContainer.getThePersonDto().getPersonUid();
        String cd = personContainer.getThePersonDto().getCd();
        String patientRole = personContainer.getRole();
        EdxPatientMatchDto edxPatientFoundDT;
        EdxPatientMatchDto edxPatientMatchFoundDT = null;
        PersonId patientPersonUid = null;
        boolean matchFound = false;

        boolean newPatientCreationApplied = false;

        if (patientRole == null || patientRole.isEmpty() || patientRole.equalsIgnoreCase(EdxELRConstant.ELR_PATIENT_ROLE_CD)) {
            EdxPatientMatchDto localIdHashCode;
            String localId;
            int localIdhshCd = 0;
            localId = getLocalId(personContainer);
            if (localId != null) {
                localId = localId.toUpperCase();
                localIdhshCd = localId.hashCode();
            }
            //NOTE: Matching Start here
            try {
                // Try to get the matching with the match string
                //	(was hash code but hash code had dups on rare occasions)
                edxPatientMatchFoundDT = getEdxPatientMatchRepositoryUtil().getEdxPatientMatchOnMatchString(cd, localId);
                if (edxPatientMatchFoundDT != null && edxPatientMatchFoundDT.isMultipleMatch()){
                    multipleMatchFound = true;
                    matchFound = false;
                }
                else if (edxPatientMatchFoundDT != null && edxPatientMatchFoundDT.getPatientUid() != null) {
                    matchFound = true;
                }
            } catch (Exception ex) {
                logger.error(LOG_ERROR_MATCHING_PATIENT);
                throw new DataProcessingException(LOG_ERROR_MATCHING_PATIENT + ex.getMessage(), ex);
            }

            if (localId != null) {
                localIdHashCode = new EdxPatientMatchDto();
                localIdHashCode.setTypeCd(NEDSSConstant.PAT);
                localIdHashCode.setMatchString(localId);
                localIdHashCode.setMatchStringHashCode((long) localIdhshCd);
            }

            // NOTE: Matching by Identifier
            if (!matchFound) {
                String IdentifierStr;
                int identifierStrhshCd = 0;

                List<String> identifierStrList = getIdentifier(personContainer);
                if (identifierStrList != null && !identifierStrList.isEmpty()) {
                    for (String s : identifierStrList) {
                        IdentifierStr = s;
                        if (IdentifierStr != null) {
                            IdentifierStr = IdentifierStr.toUpperCase();
                            identifierStrhshCd = IdentifierStr.hashCode();
                        }

                        if (IdentifierStr != null) {
                            edxPatientFoundDT = new EdxPatientMatchDto();
                            edxPatientFoundDT.setTypeCd(NEDSSConstant.PAT);
                            edxPatientFoundDT.setMatchString(IdentifierStr);
                            edxPatientFoundDT.setMatchStringHashCode((long) identifierStrhshCd);
                            // Try to get the matching with the hash code
                            edxPatientMatchFoundDT = getEdxPatientMatchRepositoryUtil().getEdxPatientMatchOnMatchString(cd, IdentifierStr);

                            if (edxPatientMatchFoundDT.isMultipleMatch()) {
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
                String namesdobcursexStr;
                int namesdobcursexStrhshCd;
                namesdobcursexStr = getLNmFnmDobCurSexStr(personContainer);
                if (namesdobcursexStr != null) {
                    namesdobcursexStr = namesdobcursexStr.toUpperCase();
                    namesdobcursexStrhshCd = namesdobcursexStr.hashCode();
                    try {
                        if (namesdobcursexStr != null) {
                            edxPatientFoundDT = new EdxPatientMatchDto();
                            edxPatientFoundDT.setPatientUid(patientUid);
                            edxPatientFoundDT.setTypeCd(NEDSSConstant.PAT);
                            edxPatientFoundDT.setMatchString(namesdobcursexStr);
                            edxPatientFoundDT.setMatchStringHashCode((long) namesdobcursexStrhshCd);
                        }
                        edxPatientMatchFoundDT = getEdxPatientMatchRepositoryUtil().getEdxPatientMatchOnMatchString(cd, namesdobcursexStr);
                        if (edxPatientMatchFoundDT.isMultipleMatch()){
                            multipleMatchFound = true;
                            matchFound = false;
                        } else if (edxPatientMatchFoundDT.getPatientUid() == null || (edxPatientMatchFoundDT.getPatientUid() != null && edxPatientMatchFoundDT.getPatientUid() <= 0)) {
                            matchFound = false;
                        } else {
                            matchFound = true;
                        }
                    } catch (Exception ex) {
                        logger.error(LOG_ERROR_MATCHING_PATIENT);
                        throw new DataProcessingException(LOG_ERROR_MATCHING_PATIENT + ex.getMessage(), ex);
                    }
                }
            }

            // NOTE: Decision, Match Not Found, Start Person Creation
            if (!matchFound) {
                if (personContainer.getTheEntityIdDtoCollection() != null) {
                    //SORTING out existing EntityId
                    Collection<EntityIdDto> newEntityIdDtoColl = new ArrayList<>();
                    for (EntityIdDto entityIdDto : personContainer.getTheEntityIdDtoCollection()) {
                        if (entityIdDto.getTypeCd() != null && !entityIdDto.getTypeCd().equalsIgnoreCase("LR")) {
                            newEntityIdDtoColl.add(entityIdDto);
                        }
                    }
                    personContainer.setTheEntityIdDtoCollection(newEntityIdDtoColl);
                }
                try {
                    // NOTE: IF new patient then create
                    // IF existing patient, then query find it, then Get Parent Patient ID
                    if (personContainer.getThePersonDto().getCd().equals(NEDSSConstant.PAT)) { // Patient
                        patientPersonUid = setAndCreateNewPerson(personContainer);
                        personContainer.getThePersonDto().setPersonParentUid(patientPersonUid.getPersonParentId());
                        personContainer.getThePersonDto().setLocalId(patientPersonUid.getLocalId());
                        personContainer.getThePersonDto().setPersonUid(patientPersonUid.getPersonId());
                        newPatientCreationApplied = true;
                    }
                } catch (Exception e) {
                    logger.error("{} {}", LOG_ERROR_ENTITY_PATIENT, e.getMessage());
                    throw new DataProcessingException(LOG_ERROR_ENTITY_PATIENT + e.getMessage(), e);
                }
                personContainer.setPatientMatchedFound(false);
            }
            else {
                personContainer.setPatientMatchedFound(true);
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

                //REVISION
                if (!newPatientCreationApplied) {
                    personContainer.getThePersonDto().setPersonParentUid(edxPatientMatchFoundDT.getPatientUid());
                } else {
                    personContainer.getThePersonDto().setPersonParentUid(patientPersonUid.getPersonParentId());
                }

                // SetPatientRevision

                patientUid = setPatientRevision(personContainer, NEDSSConstant.PAT_CR, NEDSSConstant.PAT);
                personContainer.getThePersonDto().setPersonUid(patientUid);

                //END REVISION

            } catch (Exception e) {
                logger.error("{}: {}", LOG_ERROR_ENTITY_PATIENT, e.getMessage());
                throw new DataProcessingException(LOG_ERROR_ENTITY_PATIENT + e.getMessage(), e);
            }

        }
        return edxPatientMatchFoundDT;
    }

    public boolean getMultipleMatchFound() {
        return multipleMatchFound;
    }

    @Transactional
    public Long updateExistingPerson(PersonContainer personContainer, String businessTriggerCd) throws DataProcessingException {
        return updateExistingPerson(personContainer,businessTriggerCd, personContainer.getThePersonDto().getPersonParentUid()).getPersonId();
    }







}
