package gov.cdc.nbs.deduplication.merge.id;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class LocalUidGenerator {
  private final JdbcClient client;

  public LocalUidGenerator(@Qualifier("nbsJdbcClient") JdbcClient client) {
    this.client = client;
  }

  static final String QUERY_BY_NBS_TYPE_CD = """
      SELECT
        seed_value_nbr as 'id',
        UID_prefix_cd as 'prefix',
        UID_suffix_CD as 'suffix'
      FROM
        Local_UID_generator
      WHERE
        type_cd = 'NBS';
        """;

  static final String INCREMENT_BY_NBS_TYPE_CD = """
      UPDATE Local_UID_generator
      SET
        seed_value_nbr = seed_value_nbr + 1
      WHERE
        type_cd = 'NBS';
      """;

  static final String QUERY_BY_ID = """
      SELECT
        seed_value_nbr as 'id',
        UID_prefix_cd as 'prefix',
        UID_suffix_CD as 'suffix'
      FROM
        Local_UID_generator
      WHERE
        class_name_cd = :classCd;
      """;

  static final String INCREMENT_BY_ID = """
      UPDATE Local_UID_generator
      SET
        seed_value_nbr = seed_value_nbr + 1
      WHERE
        class_name_cd = :classCd;
        """;

  @Transactional
  public GeneratedId getNextValidId(EntityType type) {
    if (type == null) {
      throw new IllegalArgumentException("EntityType must not be null");
    }

    if (EntityType.NBS.equals(type)) {
      // The 'NBS' type has a varying class_name_cd depending on the jurisdiction,
      // but it has a consistent and unique type_cd
      GeneratedId id = client.sql(QUERY_BY_NBS_TYPE_CD)
          .query(GeneratedId.class)
          .optional()
          .orElseThrow();

      client.sql(INCREMENT_BY_NBS_TYPE_CD).update();
      return id;
    } else {
      GeneratedId id = client.sql(QUERY_BY_ID)
          .param("classCd", type.toString())
          .query(GeneratedId.class)
          .optional()
          .orElseThrow();

      client.sql(INCREMENT_BY_NBS_TYPE_CD)
          .param("classCd", type.toString())
          .update();
      return id;
    }

  }

  /**
   * Matches the class_name_cd column of the Local_UID_generator table, other than
   * the NBS entry. Which references the
   * type_cd column as the class_name_cd for type NBS is dynamic based on the
   * jurisdiction
   */
  public enum EntityType {
    NBS,
    CLINICAL_DOCUMENT,
    COINFECTION_GROUP,
    CS_REPORT,
    CT_CONTACT,
    DEDUPLICATION_LOG,
    EPILINK,
    GEOCODING,
    GEOCODING_LOG,
    GROUP,
    INTERVENTION,
    INTERVIEW,
    MATERIAL,
    NBS_DOCUMENT,
    NBS_QUESTION_ID_LDF,
    NBS_QUESTION_LDF,
    NBS_UIMETEDATA_LDF,
    NND_METADATA,
    NON_LIVING_SUBJECT,
    NOTIFICATION,
    OBSERVATION,
    ORGANIZATION,
    PAGE,
    PATIENT_ENCOUNTER,
    PERSON,
    PERSON_GROUP,
    PLACE,
    PUBLIC_HEALTH_CASE,
    RDB_METADATA,
    REFERRAL,
    REPORT,
    REPORTDATASOURCE,
    REPORTDATASOURCECOLUMN,
    REPORTDISPLAYCOLUMN,
    REPORTFILTER,
    REPORTFILTERCODE,
    REPORTFILTERVALUE,
    SECURITY_LOG,
    TREATMENT,
    WORKUP
  }
}
