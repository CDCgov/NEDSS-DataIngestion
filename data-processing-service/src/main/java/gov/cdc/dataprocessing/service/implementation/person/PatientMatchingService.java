package gov.cdc.dataprocessing.service.implementation.person;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.matching.EdxPatientMatchDto;
import gov.cdc.dataprocessing.service.implementation.cache.CachingValueService;
import gov.cdc.dataprocessing.service.implementation.person.base.PatientMatchingBaseService;
import gov.cdc.dataprocessing.service.implementation.person.matching.DeduplicationService;
import gov.cdc.dataprocessing.service.implementation.person.matching.MatchResponse;
import gov.cdc.dataprocessing.service.implementation.person.matching.MatchResponse.MatchType;
import gov.cdc.dataprocessing.service.implementation.person.matching.PersonMatchRequest;
import gov.cdc.dataprocessing.service.implementation.person.matching.RelateRequest;
import gov.cdc.dataprocessing.service.interfaces.person.IPatientMatchingService;
import gov.cdc.dataprocessing.service.model.person.PersonId;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import gov.cdc.dataprocessing.utilities.component.patient.EdxPatientMatchRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
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

  private final boolean modernizedMatchingEnabled;
  private final DeduplicationService deduplicationService;

  public PatientMatchingService(
      EdxPatientMatchRepositoryUtil edxPatientMatchRepositoryUtil,
      EntityHelper entityHelper,
      PatientRepositoryUtil patientRepositoryUtil,
      CachingValueService cachingValueService,
      PrepareAssocModelHelper prepareAssocModelHelper,
      @Value("${features.modernizedMatching.enabled:false}") boolean modernizedMatchingEnabled,
      ObjectProvider<DeduplicationService> deduplicationService) {
    super(edxPatientMatchRepositoryUtil,
        entityHelper,
        patientRepositoryUtil,
        cachingValueService,
        prepareAssocModelHelper);
    this.modernizedMatchingEnabled = modernizedMatchingEnabled;
    this.deduplicationService = deduplicationService.getIfAvailable();
  }

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
    MatchResponse response = deduplicationService.match(request);

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
    deduplicationService.relate(relateRequest);

    return new EdxPatientMatchDto();
  }

  private EdxPatientMatchDto doNbsClassicMatching(PersonContainer personContainer) throws DataProcessingException {
    // Try to match using localId match string
    EdxPatientMatchDto edxPatientMatchDto = tryMatchByLocalId(personContainer);

    // Try to match using identifiers
    if (edxPatientMatchDto != null) {
      edxPatientMatchDto = tryMatchByIdentifier(personContainer);
    }

    // Try to match using last name, first name, date of birth and current sex
    if (edxPatientMatchDto != null) {
      edxPatientMatchDto = tryMatchByDemographics(personContainer);
    }

    if (edxPatientMatchDto != null) {
      // Creates revision associated with matched patient
      handleCreatePerson(personContainer, true, edxPatientMatchDto.getPatientUid());
    } else {
      // Creates a new master patient record as well as a revision for the ELR
      edxPatientMatchDto = new EdxPatientMatchDto();
      handleCreatePerson(personContainer, false, edxPatientMatchDto.getPatientUid());
    }

    return edxPatientMatchDto;
  }

  /**
   * Calls the stored procedure with localId match string. Returns
   * EdxPatientMatchDto if a match was found. Returns null otherwise
   * 
   * @param personContainer
   * @return {@link EdxPatientMatchDto} if match, otherwise
   *         null
   * @throws DataProcessingException
   */
  EdxPatientMatchDto tryMatchByLocalId(PersonContainer personContainer) throws DataProcessingException {
    String cd = personContainer.getThePersonDto().getCd();
    String localId;
    localId = getLocalId(personContainer);
    if (localId != null) {
      localId = localId.toUpperCase();
    }

    EdxPatientMatchDto edxPatientMatchDto = getEdxPatientMatchRepositoryUtil()
        .getEdxPatientMatchOnMatchString(cd, localId);
    if (edxPatientMatchDto != null
        && !edxPatientMatchDto.isMultipleMatch()
        && edxPatientMatchDto.getPatientUid() != null
        && edxPatientMatchDto.getPatientUid() > 0) {
      return edxPatientMatchDto;
    } else {
      return null;
    }

  }

  /**
   * Calls the stored procedure with identifier match string. Returns
   * EdxPatientMatchDto if a match was found. Returns null otherwise
   * 
   * @param personContainer
   * @return {@link EdxPatientMatchDto} if match, otherwise
   *         null
   * @throws DataProcessingException
   */
  EdxPatientMatchDto tryMatchByIdentifier(PersonContainer personContainer) throws DataProcessingException {
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
    return null;
  }

  /**
   * Calls the stored procedure with demographic match string. Returns
   * EdxPatientMatchDto if a match was found. Returns null otherwise
   * 
   * @param personContainer
   * @return {@link EdxPatientMatchDto} if match, otherwise
   *         null
   * @throws DataProcessingException
   */
  EdxPatientMatchDto tryMatchByDemographics(PersonContainer personContainer) throws DataProcessingException {
    String namesdobcursexStr = getLNmFnmDobCurSexStr(personContainer);
    String cd = personContainer.getThePersonDto().getCd();
    if (namesdobcursexStr == null) {
      return null;
    }

    EdxPatientMatchDto edxPatientMatchDto = getEdxPatientMatchRepositoryUtil()
        .getEdxPatientMatchOnMatchString(cd, namesdobcursexStr.toUpperCase());

    if (edxPatientMatchDto != null
        && !edxPatientMatchDto.isMultipleMatch()
        && edxPatientMatchDto.getPatientUid() != null
        && edxPatientMatchDto.getPatientUid() > 0) {
      return edxPatientMatchDto;
    } else {
      return null;
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

    boolean newPersonCreated = false;
    // No match was found. create a new person
    if (!matchFound) {
      filterLREntityId(personContainer);
      // NOTE: If personDto.cd is 'PAT' then create a new person entry
      if (personContainer.getThePersonDto().getCd().equals(NEDSSConstant.PAT)) { // Patient
        patientPersonUid = setAndCreateNewPerson(personContainer);
        newPersonCreated = true;
        personContainer.getThePersonDto().setPersonParentUid(patientPersonUid.getPersonParentId());
        personContainer.getThePersonDto().setLocalId(patientPersonUid.getLocalId());
        personContainer.getThePersonDto().setPersonUid(patientPersonUid.getPersonId());
      }
    }

    // newPersonCreated flag prevent revision log for do another unnecessary MPR update
    personContainer.setNewPersonCreated(newPersonCreated);
    createPatientRevision(personContainer);
    personContainer.setNewPersonCreated(false);
  }

  // It appears a revision is always created during ingestion, even if a match was
  // not found and a new MPR was created
  private void createPatientRevision(PersonContainer personContainer) throws DataProcessingException {
    Long patientUid = setPatientRevision(personContainer, NEDSSConstant.PAT_CR, NEDSSConstant.PAT);
    personContainer.getThePersonDto().setPersonUid(patientUid);
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

  public Long updateExistingPerson(PersonContainer personContainer, String businessTriggerCd)
      throws DataProcessingException {
    return updateExistingPerson(personContainer, businessTriggerCd,
        personContainer.getThePersonDto().getPersonParentUid()).getPersonId();
  }

}
