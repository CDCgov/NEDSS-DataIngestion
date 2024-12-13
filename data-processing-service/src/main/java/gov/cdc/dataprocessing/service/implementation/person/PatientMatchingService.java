package gov.cdc.dataprocessing.service.implementation.person;

import static gov.cdc.dataprocessing.constant.elr.EdxELRConstant.LOG_ERROR_ENTITY_PATIENT;
import static gov.cdc.dataprocessing.constant.elr.EdxELRConstant.LOG_ERROR_MATCHING_PATIENT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.matching.EdxPatientMatchDto;
import gov.cdc.dataprocessing.service.implementation.cache.CachingValueService;
import gov.cdc.dataprocessing.service.implementation.person.base.PatientMatchingBaseService;
import gov.cdc.dataprocessing.service.implementation.person.matching.MatchResponse;
import gov.cdc.dataprocessing.service.implementation.person.matching.PersonMatchRequest;
import gov.cdc.dataprocessing.service.implementation.person.matching.RelateRequest;
import gov.cdc.dataprocessing.service.implementation.person.matching.MatchResponse.MatchType;
import gov.cdc.dataprocessing.service.interfaces.person.IPatientMatchingService;
import gov.cdc.dataprocessing.service.model.person.PersonId;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import gov.cdc.dataprocessing.utilities.component.patient.EdxPatientMatchRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;

@Service
public class PatientMatchingService extends PatientMatchingBaseService implements IPatientMatchingService {
  private static final Logger logger = LoggerFactory.getLogger(PatientMatchingService.class);

  @Value("${features.modernizedMatching.enabled:false}")
  private boolean modernizedMatchingEnabled;

  @Value("${features.modernizedMatching.url}")
  private String modernizedMatchingUrl;

  private final RestClient restClient;

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
    this.restClient = RestClient.create();

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

    if (modernizedMatchingEnabled) {
      return doModernizedMatching(personContainer);
    } else {
      return doNbsClassicMatching(personContainer);
    }
  }

  // Sends a request to the NBS deduplication service and acts upon the response.
  // After patient creation, sends another request to the NBS deduplication
  // service to associate the newly created record with the entry the Record
  // Linkage service created in the MPI
  private EdxPatientMatchDto doModernizedMatching(PersonContainer personContainer) throws DataProcessingException {
    PersonMatchRequest request = new PersonMatchRequest(personContainer);
    MatchResponse response = restClient.post()
        .uri(modernizedMatchingUrl + "/match")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(request)
        .retrieve()
        .body(MatchResponse.class);

    // Create new entry in database based on match result
    if (response == null) {
      throw new DataProcessingException("Null response returned from deduplication service");
    }
    handleCreatePerson(personContainer, MatchType.EXACT.equals(response.matchType()), response.match());

    // Tell deduplication service to link newly created patient to MPI patient
    RelateRequest relateRequest = new RelateRequest(
        personContainer.getThePersonDto().getPersonUid(),
        personContainer.getThePersonDto().getPersonParentUid(),
        response.matchType(),
        response.linkResponse());
    restClient.post()
        .uri(modernizedMatchingUrl + "/relate")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(relateRequest)
        .retrieve()
        .body(Void.class);

    return new EdxPatientMatchDto();
  }

  private EdxPatientMatchDto doNbsClassicMatching(PersonContainer personContainer) throws DataProcessingException {
    boolean matchFound = false;
    // Try to match using match string
    EdxPatientMatchDto edxPatientMatchDto = tryMatchByMatchString(personContainer);
    if (edxPatientMatchDto != null
        && !edxPatientMatchDto.isMultipleMatch()
        && edxPatientMatchDto.getPatientUid() != null) {
      matchFound = true;
    }

    // Try to match using identifiers
    if (!matchFound) {
      edxPatientMatchDto = tryMatchByIdentifier(personContainer);
      if (edxPatientMatchDto != null
          && !edxPatientMatchDto.isMultipleMatch()
          && edxPatientMatchDto.getPatientUid() != null
          && edxPatientMatchDto.getPatientUid() > 0) {
        matchFound = true;
      }
    }

    // Try to match using last name, first name, date of birth and current sex
    if (!matchFound) {
      edxPatientMatchDto = tryMatchByDemographics(personContainer);
      if (edxPatientMatchDto != null
          && !edxPatientMatchDto.isMultipleMatch()
          && edxPatientMatchDto.getPatientUid() != null
          && edxPatientMatchDto.getPatientUid() > 0) {
        matchFound = true;
      }
    }

    // Create either a new Person or a Revision
    if (edxPatientMatchDto == null) {
      throw new DataProcessingException(LOG_ERROR_MATCHING_PATIENT);
    }
    handleCreatePerson(personContainer, matchFound, edxPatientMatchDto.getPatientUid());

    return edxPatientMatchDto;
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

  private void handleCreatePerson(
      PersonContainer personContainer,
      boolean matchFound,
      Long matchUid)
      throws DataProcessingException {
    PersonId patientPersonUid = null;
    // Default personParentUid to matchUid (possibly null).
    // Will be overwritten if no match was found
    personContainer.getThePersonDto().setPersonParentUid(matchUid);
    personContainer.setPatientMatchedFound(matchFound);

    // No match was found. create a new person
    if (!matchFound) {
      filterLREntityId(personContainer);
      try {
        // NOTE: If personDto.cd is 'PAT' then create a new person entry
        if (personContainer.getThePersonDto().getCd().equals(NEDSSConstant.PAT)) { // Patient
          patientPersonUid = setAndCreateNewPerson(personContainer);
          personContainer.getThePersonDto().setPersonParentUid(patientPersonUid.getPersonParentId());
          personContainer.getThePersonDto().setLocalId(patientPersonUid.getLocalId());
          personContainer.getThePersonDto().setPersonUid(patientPersonUid.getPersonId());
        }
      } catch (Exception e) {
        throw new DataProcessingException(LOG_ERROR_ENTITY_PATIENT + e.getMessage(), e);
      }
    }

    createPatientRevision(personContainer);
  }

  // It appears a revision is always created during ingestion, even if a match was
  // not found and a new MPR was created
  private void createPatientRevision(PersonContainer personContainer) throws DataProcessingException {
    try {
      Long patientUid = setPatientRevision(personContainer, NEDSSConstant.PAT_CR, NEDSSConstant.PAT);
      personContainer.getThePersonDto().setPersonUid(patientUid);
    } catch (Exception e) {
      throw new DataProcessingException(LOG_ERROR_ENTITY_PATIENT + e.getMessage(), e);
    }
  }

  // Filter out LR EntityId
  private void filterLREntityId(PersonContainer personContainer) {
    if (personContainer.getTheEntityIdDtoCollection() != null) {
      Collection<EntityIdDto> newEntityIdDtoColl = new ArrayList<>();
      for (EntityIdDto entityIdDto : personContainer.getTheEntityIdDtoCollection()) {
        if (entityIdDto.getTypeCd() != null && !entityIdDto.getTypeCd().equalsIgnoreCase("LR")) {
          newEntityIdDtoColl.add(entityIdDto);
        }
      }
      personContainer.setTheEntityIdDtoCollection(newEntityIdDtoColl);
    }
  }

  @Transactional
  public Long updateExistingPerson(PersonContainer personContainer, String businessTriggerCd)
      throws DataProcessingException {
    return updateExistingPerson(personContainer, businessTriggerCd,
        personContainer.getThePersonDto().getPersonParentUid()).getPersonId();
  }

}
