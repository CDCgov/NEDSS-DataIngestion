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
      INSERT INTO match_candidates (nbs_id, possible_match_nbs_id)
      VALUES (:nbsId, :possibleMatchNbsId)
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
}
