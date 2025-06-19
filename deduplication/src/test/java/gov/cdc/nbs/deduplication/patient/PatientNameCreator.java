package gov.cdc.nbs.deduplication.patient;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;

import gov.cdc.nbs.deduplication.patient.PatientName.CodedValue;

class PatientNameCreator {
  private final JdbcClient client;

  public PatientNameCreator(@Qualifier("nbsJdbcClient") final JdbcClient client) {
    this.client = client;
  }

  private static final String INSERT_NAME = """
      INSERT INTO person_name
      (
        person_uid,
        person_name_seq,
        add_reason_cd,
        add_time,
        add_user_id,
        as_of_date,
        nm_use_cd,
        nm_prefix,
        first_nm,
        middle_nm,
        last_nm,
        nm_suffix,
        nm_degree,
        record_status_cd,
        record_status_time,
        status_cd,
        status_time,
        last_chg_user_id,
        last_chg_time
      )
      VALUES
      (
        :id, -- person_uid
        (SELECT
          COALESCE(MAX(person_name_seq) + 1, 1)
        FROM
          person_name
        WHERE
          person_uid = :id), -- person_name_seq
        'Add', -- add_reason_cd
        GETDATE(), -- add_time
        -1, -- add_user_id
        :asOf, -- as_of_date
        :type, -- nm_use_cd
        :prefix, -- nm_prefix
        :first, -- first_nm
        :middle, -- middle_nm
        :last, -- last_nm
        :suffix, -- nm_suffix
        :degree, -- nm_degree,
        'ACTIVE', -- record_status_cd
        GETDATE(), -- record_status_time
        'A', -- status_cd
        GETDATE(), -- status_time
        -1, -- last_chg_user_id
        GETDATE() -- last_chg_time
      );
      """;

  public void create(long patientId, PatientName name) {
    client.sql(INSERT_NAME)
        .param("id", patientId)
        .param("asOf", name.asof())
        .param("type", getCode(name.type()))
        .param("prefix", getCode(name.prefix()))
        .param("first", name.first())
        .param("middle", name.middle())
        .param("last", name.last())
        .param("suffix", getCode(name.suffix()))
        .param("degree", getCode(name.degree()))
        .update();
  }

  private String getCode(CodedValue codedValue) {
    return codedValue != null ? codedValue.code() : "";
  }
}
