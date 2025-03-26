package gov.cdc.nbs.deduplication.constants;

public class QueryConstants {

  private QueryConstants() {
  }

  public static final String UPDATE_PROCESSED_PERSONS = """
      UPDATE nbs_mpi_mapping
      SET status = 'P'
      WHERE person_uid IN (:personIds)
      """;

  public static final String UPDATE_PROCESSED_PERSON = """
      UPDATE nbs_mpi_mapping
      SET status = 'P'
      WHERE person_uid = :personId
      """;

  public static final String MATCH_CANDIDATES_QUERY = """
      INSERT INTO match_candidates (person_uid,mpi_person_id,date_identified)
      VALUES (:personUid, :mpiPersonId,:identifiedDate)
      """;

  public static final String NBS_MPI_QUERY = """ 
      INSERT INTO nbs_mpi_mapping
        (person_uid, person_parent_uid, mpi_patient, mpi_person, status)
      VALUES
        (:person_uid, :person_parent_uid, :mpi_patient, :mpi_person, :status);
      """;


  public static final String MPI_PERSON_ID_QUERY = """
      SELECT mpi_person
      FROM nbs_mpi_mapping
      WHERE person_uid =  :personId
      """;

  public static final String MPI_PATIENT_ID_QUERY = """
      SELECT mpi_patient
      FROM nbs_mpi_mapping
      WHERE person_uid =  :personId
      """;

  public static final String FETCH_PERSON_UID_BY_POSTAL_LOCATOR = """
      SELECT elp.entity_uid AS person_uid
      FROM Entity_locator_participation elp
      JOIN Postal_locator pl ON elp.locator_uid = pl.postal_locator_uid
      WHERE pl.street_addr1 IS NOT NULL
      AND elp.class_cd = 'PST'
      AND elp.status_cd = 'A'
      AND pl.postal_locator_uid = :id
      """;

  public static final String FETCH_PERSON_UID_BY_TELE_LOCATOR = """
      SELECT elp.entity_uid AS person_uid
      FROM Entity_locator_participation elp
      JOIN Tele_locator tl ON elp.locator_uid = tl.tele_locator_uid
      WHERE tl.phone_nbr_txt IS NOT NULL
      AND elp.class_cd = 'TELE'
      AND elp.status_cd = 'A'
      AND tl.tele_locator_uid = :id
      """;


  public static final String PERSON_RECORD_BY_PARENT_ID = """
      SELECT
          p.person_uid external_id,
          p.person_parent_uid,
          cast(p.birth_time as Date) birth_date,
          p.curr_sex_cd sex,
          nested.address,
          nested.phone,
          nested.name,
          nested.identifiers,
          nested.race,
          p.last_chg_time -- Add last_chg_time to the query
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
                                  eid.entity_uid = p.person_uid FOR json path
                          ) AS identifiers
                  ) AS identifiers,
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
          p.person_parent_uid = :personParentUid
          AND p.record_status_cd = 'ACTIVE'
      ORDER BY p.last_chg_time DESC
      """;



  public static final String PERSON_RECORD_BY_PERSON_ID = """
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
          p.person_uid = :personUid
          AND p.record_status_cd = 'ACTIVE';
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



  public static final String PERSON_RECORDS_BY_PERSON_IDS = """
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
                                  person_uid = p.person_uid
                              ORDER BY
                                  pn.status_time DESC FOR json path
                          ) AS name
                  ) AS name
              ) AS nested
      WHERE
          p.person_uid IN (:ids)
          AND p.record_status_cd = 'ACTIVE'
      ORDER BY
          p.add_time ASC;
      """;


  public static final String POSSIBLE_MATCH_GROUP = """
      SELECT
          person_uid,
          STRING_AGG(CAST(mpi_person_id AS NVARCHAR(MAX)), ', ') AS mpi_person_ids,
          date_identified
      FROM
          match_candidates
      WHERE is_merge  is NULL
      GROUP BY
          person_uid,
          date_identified
      ORDER BY person_uid
          OFFSET :offset ROWS
          FETCH NEXT :limit ROWS ONLY;
      """;

  public static final String PERSON_UIDS_BY_MPI_PATIENT_IDS = """
      SELECT person_uid
      FROM nbs_mpi_mapping
      WHERE mpi_person IN (:mpiPersonIds)
      AND person_uid=person_parent_uid
      """;

  public static final String UPDATE_MERGE_STATUS_FOR_GROUP = """
      UPDATE match_candidates
      SET is_merge = :isMerge
      WHERE person_uid = :personUid
      """;

  public static final String FIND_POSSIBLE_MATCH = """
       SELECT COUNT(*)
       FROM match_candidates
       WHERE person_uid = :personUid
         AND mpi_person_id = :mpiPersonId
      """;

  public static final String PATIENT_IDS_BY_PERSON_UIDS = """
      SELECT mpi_person
      FROM nbs_mpi_mapping
      WHERE person_uid IN (:personIds)
      AND person_uid=person_parent_uid
      """;

  public static final String PERSON_UID_BY_MPI_PATIENT_ID = """
      SELECT person_uid
      FROM nbs_mpi_mapping
      WHERE mpi_person = :mpiId
      AND person_uid=person_parent_uid
      """;

  public static final String UPDATE_MERGE_STATUS_FOR_PATIENTS = """
      UPDATE match_candidates
      SET is_merge = 1
      WHERE person_uid = :personId
      AND mpi_person_id IN (:mpiIds)
      """;

  public static final String UPDATE_MERGE_STATUS_FOR_NON_PATIENTS = """
      UPDATE match_candidates
      SET is_merge = 0
      WHERE person_uid IN (:personIds)
      OR (mpi_person_id IN (:mpiIds) AND person_uid != :personId)
      """;

  public static final String UPDATE_SINGLE_RECORD = """
      WITH SingleUnmarkedRecord AS (
          SELECT person_uid
          FROM match_candidates
          WHERE person_uid = :personUid
            AND is_merge IS NULL
      )
      UPDATE match_candidates
      SET is_merge = 0
      WHERE (SELECT COUNT(*) FROM SingleUnmarkedRecord) = 1
      AND person_uid = :personUid
      AND is_merge IS NULL
      """;



  public static final String MARK_SUPERSEDED_RECORDS = """
      UPDATE person
      SET record_status_cd = 'SUPERCEDED',
          person_parent_uid = :personId
      WHERE person_uid IN (:supersededPersonIds)
      """;


  public static final String CREATE_MERGE_METADATA = """
       INSERT INTO PERSON_MERGE (
          SURVIVING_PERSON_UID,
          superced_person_uid,
          surviving_parent_uid,
          superceded_parent_uid,
          MERGE_TIME,
          superceded_version_ctrl_nbr,
          RECORD_STATUS_CD
      ) VALUES (
          :survivorPersonId,
          :supersededPersonId,
          :survivorPersonId,
          :supersededPersonId,
          :mergeTime,
          1,
          'PAT_MERGE'
      )
      """;


}
