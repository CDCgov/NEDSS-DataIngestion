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
public class PatientMatchingService extends PatientMatchingBaseService implements IPatientMatchingService {
    private static final Logger logger = LoggerFactory.getLogger(PatientMatchingService.class);

    public PatientMatchingService(
            EdxPatientMatchRepositoryUtil edxPatientMatchRepositoryUtil,
            EntityHelper entityHelper,
            PatientRepositoryUtil patientRepositoryUtil,
            CachingValueService cachingValueService,
            PrepareAssocModelHelper prepareAssocModelHelper) {
        super(edxPatientMatchRepositoryUtil,
                entityHelper,
                patientRepositoryUtil,
                cachingValueService,
                prepareAssocModelHelper);
    }

    @Transactional
    public EdxPatientMatchDto getMatchingPatient(PersonContainer personContainer) throws DataProcessingException {
        String patientRole = personContainer.getRole();

        // If patientRole is set and not equal to 'PAT', do not perform matching
        if (patientRole != null
                && !patientRole.isEmpty()
                && !patientRole.equalsIgnoreCase(EdxELRConstant.ELR_PATIENT_ROLE_CD)) {
            return null;
        }

        // Try to match using match string
        EdxPatientMatchDto edxPatientMatchDto = tryMatchByMatchString(personContainer);
        if (edxPatientMatchDto != null
                && !edxPatientMatchDto.isMultipleMatch()
                && edxPatientMatchDto.getPatientUid() != null) {
            personContainer.setPatientMatchedFound(true);
            return edxPatientMatchDto;
        }

        // Try to match using identifiers
        edxPatientMatchDto = tryMatchByIdentifier(personContainer);
        if (edxPatientMatchDto != null
                && !edxPatientMatchDto.isMultipleMatch()
                && edxPatientMatchDto.getPatientUid() != null
                && edxPatientMatchDto.getPatientUid() > 0) {
            personContainer.setPatientMatchedFound(true);

            return edxPatientMatchDto;
        }

        // Try to match using last name, first name, date of birth and current sex
        edxPatientMatchDto = tryMatchByDemographics(personContainer);
        if (edxPatientMatchDto != null
                && !edxPatientMatchDto.isMultipleMatch()
                && edxPatientMatchDto.getPatientUid() != null
                && edxPatientMatchDto.getPatientUid() > 0) {
            personContainer.setPatientMatchedFound(true);
            return edxPatientMatchDto;
        }

        // Still no match found. Create new person
        handleCreatePerson(personContainer);

        return new EdxPatientMatchDto();
    }

    private EdxPatientMatchDto tryMatchByMatchString(PersonContainer personContainer) throws DataProcessingException {
        String cd = personContainer.getThePersonDto().getCd();
        String localId;
        localId = getLocalId(personContainer);
        if (localId != null) {
            localId = localId.toUpperCase();
        }

        try {
            return getEdxPatientMatchRepositoryUtil().getEdxPatientMatchOnMatchString(cd, localId);
        } catch (Exception ex) {
            logger.error(LOG_ERROR_MATCHING_PATIENT);
            throw new DataProcessingException(LOG_ERROR_MATCHING_PATIENT + ex.getMessage(), ex);
        }

    }

    private EdxPatientMatchDto tryMatchByIdentifier(PersonContainer personContainer) throws DataProcessingException {
        String cd = personContainer.getThePersonDto().getCd();
        List<String> identifierStrList = getIdentifier(personContainer);
        EdxPatientMatchDto edxPatientMatchDto = null;

        if (identifierStrList != null && !identifierStrList.isEmpty()) {
            for (String identifierStr : identifierStrList) {

                if (identifierStr != null) {
                    edxPatientMatchDto = getEdxPatientMatchRepositoryUtil()
                            .getEdxPatientMatchOnMatchString(cd, identifierStr.toUpperCase());

                    if (edxPatientMatchDto != null
                            && !edxPatientMatchDto.isMultipleMatch()
                            && edxPatientMatchDto.getPatientUid() != null
                            && edxPatientMatchDto.getPatientUid() > 0) {
                        return edxPatientMatchDto;
                    }
                }
            }
        }
        return edxPatientMatchDto;
    }

    private EdxPatientMatchDto tryMatchByDemographics(PersonContainer personContainer) throws DataProcessingException {
        String namesdobcursexStr = getLNmFnmDobCurSexStr(personContainer);
        String cd = personContainer.getThePersonDto().getCd();
        if (namesdobcursexStr == null) {
            return new EdxPatientMatchDto();
        }

        try {
            return getEdxPatientMatchRepositoryUtil()
                    .getEdxPatientMatchOnMatchString(cd, namesdobcursexStr.toUpperCase());

        } catch (Exception ex) {
            logger.error(LOG_ERROR_MATCHING_PATIENT);
            throw new DataProcessingException(LOG_ERROR_MATCHING_PATIENT + ex.getMessage(), ex);
        }

    }

    private void handleCreatePerson(PersonContainer personContainer) throws DataProcessingException {
        boolean newPatientCreationApplied = false;
        PersonId patientPersonUid = null;
        // NOTE: Decision, Match Not Found, Start Person Creation
        if (personContainer.getTheEntityIdDtoCollection() != null) {
            // SORTING out existing EntityId
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
                personContainer.getThePersonDto().setPersonParentUid(patientPersonUid.getPersonParentId());
            }
        } catch (Exception e) {
            throw new DataProcessingException(LOG_ERROR_ENTITY_PATIENT + e.getMessage(), e);
        }

        personContainer.setPatientMatchedFound(false);

        // NOTE: In this flow, if new patient, revision record is still get inserted
        // NOTE: if existing pateint, revision also insrted
        try {

            /**
             * NOTE:
             * Regarding New or Existing Patient
             * This logic will do Patient Hash update and do Patient Revision update
             */

            /**
             * 2.0 NOTE: if new patient flow, skip revision
             * otherwise: go to update existing patient
             */

            // REVISION
            if (!newPatientCreationApplied) {
                personContainer.getThePersonDto().setPersonParentUid(null);
            }

            // SetPatientRevision
            Long patientUid = setPatientRevision(personContainer, NEDSSConstant.PAT_CR, NEDSSConstant.PAT);
            personContainer.getThePersonDto().setPersonUid(patientUid);

        } catch (Exception e) {
            throw new DataProcessingException(LOG_ERROR_ENTITY_PATIENT + e.getMessage(), e);
        }
    }

    @Transactional
    public Long updateExistingPerson(PersonContainer personContainer, String businessTriggerCd)
            throws DataProcessingException {
        return updateExistingPerson(personContainer, businessTriggerCd,
                personContainer.getThePersonDto().getPersonParentUid()).getPersonId();
    }

}
