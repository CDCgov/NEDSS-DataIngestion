package gov.cdc.nbs.deduplication.merge.handler;

import gov.cdc.nbs.deduplication.merge.model.PatientMergeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PersonPhoneEmailMergeHandlerTest {

  @Mock
  private NamedParameterJdbcTemplate nbsTemplate;

  private PersonPhoneEmailMergeHandler handler;

  @BeforeEach
  void setUp() {
    handler = new PersonPhoneEmailMergeHandler(nbsTemplate);
  }

  @Test
  @SuppressWarnings("unchecked")
  void handleMerge_shouldPerformAllPersonPhoneEmailRelatedDatabaseOperations() {
    String survivingId = "survivorId1";
    String matchId = "match123";
    PatientMergeRequest request = getPatientMergeRequest(survivingId);

    handler.handleMerge(matchId, request);

    verify(nbsTemplate).update(eq(PersonPhoneEmailMergeHandler.UPDATE_UN_SELECTED_PHONE_EMAIL_INACTIVE),
        (Map<String, Object>) argThat(params -> {
          Map<String, Object> paramMap = (Map<String, Object>) params;
          return survivingId.equals(paramMap.get("survivingId")) &&
              List.of("locator1", "locator2").equals(paramMap.get("selectedLocators"));
        }));

    verify(nbsTemplate).update(eq(PersonPhoneEmailMergeHandler.INSERT_NEW_PHONE_EMAIL_LOCATORS),
        (Map<String, Object>) argThat(params -> {
          Map<String, Object> paramMap = (Map<String, Object>) params;
          return survivingId.equals(paramMap.get("survivingId")) &&
              List.of("locator1", "locator2").equals(paramMap.get("selectedLocators"));
        }));

    verify(nbsTemplate).update(eq(PersonPhoneEmailMergeHandler.UPDATE_PHONE_EMAIL_LOCATORS_HIST_TO_SURVIVING),
        (Map<String, Object>) argThat(params -> {
          Map<String, Object> paramMap = (Map<String, Object>) params;
          return survivingId.equals(paramMap.get("survivingId")) &&
              List.of("locator1", "locator2").equals(paramMap.get("selectedLocators"));
        }));

    verify(nbsTemplate).update(eq(PersonPhoneEmailMergeHandler.DELETE_OLD_PHONE_EMAIL_LOCATORS),
        (Map<String, Object>) argThat(params -> {
          Map<String, Object> paramMap = (Map<String, Object>) params;
          return survivingId.equals(paramMap.get("survivingId")) &&
              List.of("locator1", "locator2").equals(paramMap.get("selectedLocators"));
        }));

  }

  private PatientMergeRequest getPatientMergeRequest(String survivingId) {
    List<PatientMergeRequest.PhoneEmailId> PhoneEmailIds = Arrays.asList(
        new PatientMergeRequest.PhoneEmailId("locator1"),
        new PatientMergeRequest.PhoneEmailId("locator2")
    );
    return new PatientMergeRequest(survivingId, null,
        null, null, PhoneEmailIds, null, null);
  }


}
