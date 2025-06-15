package gov.cdc.nbs.deduplication.merge.handler;

import gov.cdc.nbs.deduplication.merge.model.PatientMergeRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
@Order(4)
public class PersonAddressMergeHandler implements SectionMergeHandler {

  private final NamedParameterJdbcTemplate nbsTemplate;


  static final String UPDATE_UN_SELECTED_ADDRESS_INACTIVE = """
      UPDATE Entity_locator_participation
      SET record_status_cd = 'INACTIVE',
         last_chg_time = GETDATE()
      WHERE entity_uid = :survivingId
        AND locator_uid NOT IN (:selectedLocators)
        AND use_cd NOT IN ('BIR', 'DTH')
        AND class_cd = 'PST';
      """;

  static final String INSERT_NEW_LOCATORS = """
      INSERT INTO Entity_locator_participation (
          entity_uid,
          locator_uid,
          version_ctrl_nbr,
          add_reason_cd,
          add_time,
          add_user_id,
          cd,
          cd_desc_txt,
          class_cd,
          duration_amt,
          duration_unit_cd,
          from_time,
          last_chg_reason_cd,
          last_chg_time,
          last_chg_user_id,
          locator_desc_txt,
          record_status_cd,
          record_status_time,
          status_cd,
          status_time,
          to_time,
          use_cd,
          user_affiliation_txt,
          valid_time_txt,
          as_of_date
      )
      SELECT
          :survivingId,
          locator_uid,
          version_ctrl_nbr,
          add_reason_cd,
          add_time,
          add_user_id,
          cd,
          cd_desc_txt,
          class_cd,
          duration_amt,
          duration_unit_cd,
          from_time,
          last_chg_reason_cd,
          GETDATE(),
          last_chg_user_id,
          locator_desc_txt,
          record_status_cd,
          record_status_time,
          status_cd,
          status_time,
          to_time,
          use_cd,
          user_affiliation_txt,
          valid_time_txt,
          as_of_date
      FROM Entity_locator_participation
      WHERE locator_uid IN (:selectedLocators)
        AND entity_uid != :survivingId
        AND use_cd NOT IN ('BIR', 'DTH')
        AND class_cd = 'PST';
      """;


  public PersonAddressMergeHandler(@Qualifier("nbsNamedTemplate") NamedParameterJdbcTemplate nbsTemplate) {
    this.nbsTemplate = nbsTemplate;
  }

  @Override
  public void handleMerge(String matchId, PatientMergeRequest request) {
    mergePersonAddress(request);
  }

  private void mergePersonAddress(PatientMergeRequest request) {
    String survivingId = request.survivingRecord();
    List<String> selectedLocatorIds = request.addresses().stream()
        .map(PatientMergeRequest.AddressId::locatorId)
        .toList();

    if (!selectedLocatorIds.isEmpty()) {
      markUnselectedAddressInactive(survivingId, selectedLocatorIds);
      updateSelectedAddress(survivingId, selectedLocatorIds);
    }
  }

  private void markUnselectedAddressInactive(String survivingId, List<String> selectedLocators) {
    Map<String, Object> params = new HashMap<>();
    params.put("survivingId", survivingId);
    params.put("selectedLocators", selectedLocators);

    nbsTemplate.update(UPDATE_UN_SELECTED_ADDRESS_INACTIVE, params);
  }

  private void updateSelectedAddress(String survivingId, List<String> selectedLocators) {
    Map<String, Object> params = new HashMap<>();
    params.put("survivingId", survivingId);
    params.put("selectedLocators", selectedLocators);

    nbsTemplate.update(INSERT_NEW_LOCATORS, params);
  }


}
