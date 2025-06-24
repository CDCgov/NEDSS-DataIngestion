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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonAddressMergeHandlerTest {

  @Mock
  private NamedParameterJdbcTemplate nbsTemplate;

  private PersonAddressMergeHandler handler;

  @BeforeEach
  void setUp() {
    handler = new PersonAddressMergeHandler(nbsTemplate);
  }

  @Test
  @SuppressWarnings("unchecked")
  void handleMerge_shouldPerformAllPersonAddressesRelatedDatabaseOperations() {
    String survivingId = "survivorId1";
    String matchId = "match123";
    PatientMergeRequest request = getPatientMergeRequest(survivingId);

    handler.handleMerge(matchId, request);

    verify(nbsTemplate).update(eq(PersonAddressMergeHandler.UPDATE_UN_SELECTED_ADDRESS_INACTIVE),
        (Map<String, Object>) argThat(params -> {
          Map<String, Object> paramMap = (Map<String, Object>) params;
          return survivingId.equals(paramMap.get("survivingId")) &&
              List.of("locator1", "locator2").equals(paramMap.get("selectedLocators"));
        }));

    verify(nbsTemplate).update(eq(PersonAddressMergeHandler.INSERT_NEW_LOCATORS),
        (Map<String, Object>) argThat(params -> {
          Map<String, Object> paramMap = (Map<String, Object>) params;
          return survivingId.equals(paramMap.get("survivingId")) &&
              List.of("locator1", "locator2").equals(paramMap.get("selectedLocators"));
        }));

  }

  private PatientMergeRequest getPatientMergeRequest(String survivingId) {
    List<PatientMergeRequest.AddressId> addressIds = Arrays.asList(
        new PatientMergeRequest.AddressId("locator1"),
        new PatientMergeRequest.AddressId("locator2")
    );
    return new PatientMergeRequest(survivingId, null,
        null, addressIds, null, null, null, null);
  }


}
