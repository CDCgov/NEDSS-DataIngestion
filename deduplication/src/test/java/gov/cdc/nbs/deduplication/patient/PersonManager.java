package gov.cdc.nbs.deduplication.patient;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;

class PersonManager {

  private final JdbcClient client;

  public PersonManager(@Qualifier("nbsJdbcClient") final JdbcClient client) {
    this.client = client;
  }

  private static final String SELECT_NEXT_ID = """
      SELECT seed_value_nbr
      FROM local_UID_generator id
      WHERE id.class_name_cd = 'PERSON';
      """;

  private static final String INCREMENT_ID = """
      UPDATE local_UID_generator
        SET seed_value_nbr = seed_value_nbr + 1
      WHERE class_name_cd = 'PERSON';
      """;

  private static final String INSERT_ENTITY = """
      INSERT INTO Entity
        (entity_uid, class_cd)
      VALUES
        (:id, 'PSN');
      """;

  private static final String INSERT_PERSON = """
      INSERT INTO person
        (
          person_uid,
          person_parent_uid,
          local_id,
          cd,
          record_status_cd,
          record_status_time,
          status_cd,
          status_time,
          version_ctrl_nbr,
          electronic_ind,
          add_user_id,
          add_time,
          last_chg_user_id,
          last_chg_time
        )
      VALUES
      (
        :id, --person_uid
        :id, --person_parent_uid
        :localId, --local_id
        'PAT', --cd
        'ACTIVE', --record_status_cd
        GETDATE(), --record_status_time
        'A', --status_cd
        GETDATE(), --status_time
        1, --version_ctrl_nbr
        'N', --electronic_ind
        -1, --add_user_id
        GETDATE(), --add_time
        -1, --last_chg_user_id
        GETDATE() --last_chg_time
      )
      """;

  private static final String SET_INACTIVE = """
      UPDATE person
      SET
        record_status_cd = 'INACTIVE',
        record_status_time = GETDATE(),
        status_cd = 'I',
        status_time = GETDATE(),
        last_chg_user_id = -1,
        last_chg_time = GETDATE()
      WHERE
        person_uid = :id
      """;

  public Long create() {
    final Long patientId = getNextId();
    insertEntity(patientId);
    insertPatient(patientId);
    return patientId;
  }

  public void setInactive(long patientId) {
    client.sql(SET_INACTIVE)
        .param("id", patientId)
        .update();
  }

  private Long getNextId() {
    Long id = client.sql(SELECT_NEXT_ID).query(Long.class).single();
    client.sql(INCREMENT_ID).update();
    return id;
  }

  private void insertEntity(long id) {
    client.sql(INSERT_ENTITY)
        .param("id", id)
        .update();
  }

  private void insertPatient(long id) {
    client.sql(INSERT_PERSON)
        .param("id", id)
        .param("localId", "PSN" + id + "GA01")
        .update();
  }
}
