package gov.cdc.nbs.deduplication.constants;

public class QueryConstants {

  private QueryConstants() {
  }

  public static final String NBS_MPI_QUERY = """
      INSERT INTO nbs_mpi_mapping
        (person_uid, person_parent_uid, mpi_patient, mpi_person, status,person_add_time)
      VALUES
        (:person_uid, :person_parent_uid, :mpi_patient, :mpi_person, :status ,:person_add_time);
      """;

  public static final String MPI_PERSON_ID_QUERY = """
      SELECT mpi_person
      FROM nbs_mpi_mapping
      WHERE person_uid = :personId
      """;

  public static final String MPI_PATIENT_ID_QUERY = """
      SELECT mpi_patient
      FROM nbs_mpi_mapping
      WHERE person_uid =  :personId
      """;

  public static final String PERSON_RECORDS_BY_PARENT_IDS = """
      SELECT
          p.person_uid external_id,
          p.person_parent_uid,
          cast(p.birth_time as Date) birth_date,
          p.curr_sex_cd sex,
          nested.address,
          nested.phone,
          nested.name,
          nested.identifiers,
          nested.race
      FROM
          person p WITH (NOLOCK)
          OUTER apply (
              SELECT
                  *
              FROM
                  -- address
                  (
                      SELECT
                          (
                              SELECT
                                  STRING_ESCAPE(pl.street_addr1, 'json') street,
                                  STRING_ESCAPE(pl.street_addr2, 'json') street2,
                                  city_desc_txt city,
                                  sc.code_desc_txt state,
                                  zip_cd zip,
                                  scc.code_desc_txt county
                              FROM
                                  Entity_locator_participation elp WITH (NOLOCK)
                                  JOIN Postal_locator pl WITH (NOLOCK) ON elp.locator_uid = pl.postal_locator_uid
                                  LEFT JOIN NBS_SRTE.dbo.state_code sc ON sc.state_cd = pl.state_cd
                                  LEFT JOIN NBS_SRTE.dbo.state_county_code_value scc ON scc.code = pl.cnty_cd
                              WHERE
                                  elp.entity_uid = p.person_uid
                                  AND elp.class_cd = 'PST'
                                  AND elp.status_cd = 'A'
                                  AND pl.street_addr1 IS NOT NULL FOR json path
                          ) AS address
                  ) AS address,
                  -- identifiers
                  (
                      SELECT
                          (
                              SELECT
                                eid.type_cd type,
                                  STRING_ESCAPE(REPLACE(REPLACE(eid.root_extension_txt,'-',''),' ',''), 'json') value,
                                  eid.assigning_authority_cd authority
                              FROM
                                  entity_id eid WITH (NOLOCK)
                              WHERE
                                  eid.entity_uid = p.person_uid FOR json path) as identifiers) as identifiers,
                  -- person races
                  (
                      SELECT
                          (
                              SELECT TOP 1
                                  pr.race_category_cd value
                              FROM
                                  Person_race pr WITH (NOLOCK)
                              WHERE
                                  person_uid = p.person_uid
                          ) AS race
                  ) AS race,
                  -- person phone
                  (
                      SELECT
                          (
                              SELECT
                                  REPLACE(REPLACE(tl.phone_nbr_txt,'-',''),' ','') value
                              FROM
                                  Entity_locator_participation elp WITH (NOLOCK)
                                  JOIN Tele_locator tl WITH (NOLOCK) ON elp.locator_uid = tl.tele_locator_uid
                              WHERE
                                  elp.entity_uid = p.person_uid
                                  AND elp.class_cd = 'TELE'
                                  AND elp.status_cd = 'A'
                                  AND tl.phone_nbr_txt IS NOT NULL FOR json path
                          ) AS phone
                  ) AS phone,
                  -- person_names
                  (
                      SELECT
                          (
                              SELECT
                                  STRING_ESCAPE(REPLACE(pn.last_nm,'-',' '), 'json') lastNm,
                                  STRING_ESCAPE(pn.middle_nm, 'json') middleNm,
                                  STRING_ESCAPE(pn.first_nm, 'json') firstNm,
                                  pn.nm_suffix nmSuffix
                              FROM
                                  person_name pn WITH (NOLOCK)
                              WHERE
                                  person_uid = p.person_uid FOR json path
                          ) AS name
                  ) AS name
              ) AS nested
      WHERE
          p.person_parent_uid IN (:ids)
          AND p.record_status_cd = 'ACTIVE';
      """;

  public static final String INSERT_PERSON_MERGE_RECORD = """
      INSERT INTO PERSON_MERGE (
          SURVIVING_PERSON_UID,
          superced_person_uid,
          surviving_parent_uid,
          superceded_parent_uid,
          MERGE_TIME,
          superceded_version_ctrl_nbr,
          surviving_version_ctrl_nbr,
          RECORD_STATUS_CD,
          record_status_time,
          merge_user_id
      ) VALUES (
          :survivorPersonId,
          :supersededPersonId,
          (SELECT person_parent_uid FROM person WHERE person_uid = :survivorPersonId),
          (SELECT person_parent_uid FROM person WHERE person_uid = :supersededPersonId),
          :mergeTime,
          (SELECT MAX(version_ctrl_nbr) FROM person_hist WHERE person_uid = :supersededPersonId),
          (SELECT MAX(version_ctrl_nbr) FROM person_hist WHERE person_uid = :survivorPersonId),
          'PAT_MERGE',
          :mergeTime,
          :mergeUserId
      )
      """;

  public static final String CHILD_IDS_BY_PARENT_PERSON_IDS = """
      SELECT person_uid
      FROM person
      WHERE person_parent_uid IN (:parentPersonIds)
      AND person_uid != person_parent_uid
      """;

  public static final String MPI_PATIENT_EXISTS_CHECK = """
      SELECT CASE WHEN EXISTS (
            SELECT 1
            FROM nbs_mpi_mapping
            WHERE person_uid = :personId
        ) THEN 1 ELSE 0 END
      """;

  public static final String LINK_SUPERSEDED_CHILD_IDS_TO_SURVIVOR = """
      UPDATE person
      SET person_parent_uid = :survivorId
      WHERE person_uid IN (:supersededChildIds)
      """;

  public static final String MARK_SUPERSEDED_RECORDS = """
      UPDATE person
      SET record_status_cd = 'SUPERCEDED'
      WHERE person_uid IN (:supersededPersonIds)
      """;

  public static final String UPDATE_LAST_CHANGE_TIME_FOR_PATIENTS = """
      UPDATE person
      SET last_chg_time = :lastChgTime
      WHERE person_uid IN (:involvedPatients)
      """;


  public static final String INCREMENT_PERSON_VERSION_NUMBER = """
      UPDATE person
      SET version_ctrl_nbr = version_ctrl_nbr + 1
      WHERE person_uid IN (:involvedPatients);
      """;

  public static final String UPDATE_PERSON_ADMIN_COMMENT_FROM_SOURCE = """
      UPDATE person
      SET
          description = (SELECT description FROM person WHERE person_uid =:adminSourcePersonUid),
          as_of_date_admin = (SELECT as_of_date_admin FROM person WHERE person_uid =:adminSourcePersonUid)
      WHERE
          person_uid = :survivorId;
      """;

}
