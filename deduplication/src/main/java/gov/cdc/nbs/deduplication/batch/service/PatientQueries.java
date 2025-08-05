package gov.cdc.nbs.deduplication.batch.service;

class PatientQueries {

  private PatientQueries() {
  }

  static final String PERSONS_MERGE_DATA_BY_PERSON_IDS = """
      WITH id_settings(prefix, suffix,initial) as   (
        select
          generator.UID_prefix_cd     as prefix,
          generator.UID_suffix_CD     as suffix,
          cast(config.config_value as bigint)  as initial
        from Local_UID_generator generator WITH (NOLOCK), NBS_configuration config WITH (NOLOCK)
        where generator.class_name_cd = 'PERSON' AND generator.type_cd = 'LOCAL' AND config.config_key = 'SEED_VALUE'
      )
      SELECT
          cast(
              substring(
                  p.local_id,
                  len(id_settings.prefix) + 1,
                  len(p.local_id) - len(id_settings.prefix) - len(id_settings.suffix)
              ) as bigint
          ) - id_settings.initial                 as personId,
          p.person_parent_uid,
          p.add_time,
          p.as_of_date_admin AS comment_date,
          p.description AS admin_comments,
          nested.address,
          nested.phone,
          nested.name,
          nested.identifiers,
          nested.race,
          nested.ethnicity,
          nested.sexAndBirth,
          nested.mortality,
          nested.general,
          --INVESTIGATIONS
          (
              SELECT
                  inv.local_id AS id,
                  inv.activity_from_time AS startDate,
                  conditionCode.condition_short_nm AS 'condition'
              FROM
                  Participation part
              WITH
                  (NOLOCK)
                  JOIN Public_health_case inv
              WITH
                  (NOLOCK) ON inv.public_health_case_uid = part.act_uid
                  AND inv.record_status_cd != 'LOG_DEL'
                  AND inv.investigation_status_cd IN ('O', 'C')
                  LEFT JOIN nbs_srte..condition_code conditionCode
              WITH
                  (NOLOCK) ON conditionCode.condition_cd = inv.cd
              WHERE
                  part.subject_entity_uid IN (
                      SELECT
                          person_uid
                      FROM
                          person
                      WHERE
                          person_parent_uid = p.person_uid
                  )
                  AND part.type_cd = 'SubjOfPHC'
                  AND part.record_status_cd = 'ACTIVE'
                  AND part.subject_class_cd = 'PSN'
                  AND part.act_class_cd = 'CASE'
                  AND inv.program_jurisdiction_oid IN (:programJurisdictionOids)
                  ORDER BY 'condition' ASC, inv.activity_from_time DESC
              FOR JSON
                  PATH,
                  INCLUDE_NULL_VALUES
          ) AS investigations
      FROM
          id_settings, person p
      WITH
          (NOLOCK)
          OUTER APPLY (
              SELECT
                  *
              FROM
                  -- address
                  (
                      SELECT
                          (
                              SELECT
                                  elp.locator_uid AS 'id',
                                  elp.as_of_date AS 'asOf',
                                  addressTypeCode.code_short_desc_txt AS 'type',
                                  addressUseCode.code_short_desc_txt AS 'use',
                                  STRING_ESCAPE(pl.street_addr1, 'json') address,
                                  STRING_ESCAPE(pl.street_addr2, 'json') address2,
                                  city_desc_txt city,
                                  sc.code_desc_txt state,
                                  zip_cd zipcode,
                                  scc.code_desc_txt county,
                                  pl.census_tract AS censusTract,
                                  cc.code_short_desc_txt AS country,
                                  elp.locator_desc_txt AS comments
                              FROM
                                  Entity_locator_participation elp
                              WITH
                                  (NOLOCK)
                                  JOIN Postal_locator pl
                              WITH
                                  (NOLOCK) ON elp.locator_uid = pl.postal_locator_uid
                                  LEFT JOIN NBS_SRTE.dbo.code_value_general addressUseCode
                              WITH
                                  (NOLOCK) ON addressUseCode.code = use_cd
                                  AND addressUseCode.code_set_nm = 'EL_USE_PST_PAT'
                                  LEFT JOIN NBS_SRTE.dbo.code_value_general addressTypeCode
                              WITH
                                  (NOLOCK) ON addressTypeCode.code = cd
                                  AND addressTypeCode.code_set_nm = 'EL_TYPE_PST_PAT'
                                  LEFT JOIN NBS_SRTE.dbo.state_code sc
                              WITH
                                  (NOLOCK) ON sc.state_cd = pl.state_cd
                                  LEFT JOIN NBS_SRTE.dbo.state_county_code_value scc
                              WITH
                                  (NOLOCK) ON scc.code = pl.cnty_cd
                                  LEFT JOIN NBS_SRTE.dbo.country_code cc
                              WITH
                                  (NOLOCK) ON cc.code = pl.cntry_cd
                              WHERE
                                  elp.entity_uid = p.person_uid
                                  AND use_cd NOT IN ('BIR', 'DTH')
                                  AND elp.class_cd = 'PST'
                                  AND elp.record_status_cd = 'ACTIVE'
                              ORDER BY asOf desc
                              FOR JSON
                                  PATH,
                                  INCLUDE_NULL_VALUES
                          ) AS address
                  ) AS address,
                  -- identifiers
                  (
                      SELECT
                          (
                              SELECT
                                  eid.entity_uid AS personUid,
                                  eid.entity_id_seq AS 'sequence',
                                  eid.as_of_date AS asOf,
                                  identifierTypeCode.code_short_desc_txt AS 'type',
                                  eid.assigning_authority_desc_txt AS assigningAuthority,
                                  STRING_ESCAPE(REPLACE(REPLACE(eid.root_extension_txt, '-', ''), ' ', ''), 'json') AS 'value'
                              FROM
                                  Entity_id eid
                              WITH
                                  (NOLOCK)
                                  LEFT JOIN nbs_srte..code_value_general identifierTypeCode
                              WITH
                                  (NOLOCK) ON eid.type_cd = identifierTypeCode.code
                                  AND identifierTypeCode.code_set_nm = 'EI_TYPE_PAT'
                              WHERE
                                  eid.entity_uid = p.person_uid
                                  AND eid.record_status_cd = 'ACTIVE'
                              ORDER BY asOf desc
                              FOR JSON
                                  PATH,
                                  INCLUDE_NULL_VALUES
                          ) AS identifiers
                  ) AS identifiers,
                  -- person races
                  (
                      SELECT
                          (
                              SELECT
                                  person_uid AS personUid,
                                  pr.race_category_cd AS raceCode,
                                  as_of_date AS asOf,
                                  raceCode.code_short_desc_txt AS race,
                                  STRING_AGG(rc.code_short_desc_txt, ' | ') AS detailedRaces
                              FROM
                                  Person_race pr
                              WITH
                                  (NOLOCK)
                                  LEFT JOIN NBS_SRTE.dbo.race_code rc
                              WITH
                                  (NOLOCK) ON rc.code = race_cd
                                  AND rc.code_set_nm = 'P_RACE'
                                  JOIN NBS_SRTE.dbo.code_value_general raceCode
                              WITH
                                  (NOLOCK) ON raceCode.code = race_category_cd
                                  AND raceCode.code_set_nm = 'RACE_CALCULATED'
                              WHERE
                                  pr.person_uid = p.person_uid
                                  AND pr.record_status_cd = 'ACTIVE'
                              GROUP BY
                                  person_uid,
                                  race_category_cd,
                                  as_of_date,
                                  raceCode.code_short_desc_txt
                              ORDER BY asOf desc
                              FOR JSON
                                  PATH,
                                  INCLUDE_NULL_VALUES
                          ) AS race
                  ) AS race,
                  -- Ethnicity
                  (
                      SELECT
                          (
                              SELECT
                                  ep.as_of_date_ethnicity AS asOf,
                                  ethnicityCode.code_desc_txt AS ethnicity,
                                  reasonUnknownCode.code_short_desc_txt AS reasonUnknown,
                                  STRING_AGG(spanishOriginCode.code_desc_txt, ' | ') AS spanishOrigin
                              FROM
                                  person ep
                                  LEFT JOIN Person_ethnic_group eg ON ep.person_uid = eg.person_uid
                                  LEFT JOIN nbs_srte..code_value_general ethnicityCode
                              WITH
                                  (NOLOCK) ON ethnicityCode.code = ep.ethnic_group_ind
                                  AND ethnicityCode.code_set_nm = 'PHVS_ETHNICITYGROUP_CDC_UNK'
                                  LEFT JOIN nbs_srte..code_value_general spanishOriginCode
                              WITH
                                  (NOLOCK) ON spanishOriginCode.code = eg.ethnic_group_cd
                                  AND spanishOriginCode.code_set_nm = 'P_ETHN'
                                  LEFT JOIN nbs_srte..code_value_general reasonUnknownCode
                              WITH
                                  (NOLOCK) ON reasonUnknownCode.code = ep.ethnic_unk_reason_cd
                                  AND reasonUnknownCode.code_set_nm = 'P_ETHN_UNK_REASON'
                              WHERE
                                  ep.person_uid = p.person_uid
                                  AND (eg.record_status_cd IS NULL OR eg.record_status_cd = 'ACTIVE')
                              GROUP BY
                                  ep.person_uid,
                                  ep.as_of_date_ethnicity,
                                  ep.ethnic_unk_reason_cd,
                                  ethnicityCode.code_desc_txt,
                                  reasonUnknownCode.code_short_desc_txt
                              FOR JSON
                                  PATH,
                                  INCLUDE_NULL_VALUES,
                                  WITHOUT_ARRAY_WRAPPER
                          ) AS ethnicity
                  ) AS ethnicity,
                  -- person phone
                  (
                      SELECT
                          (
                              SELECT
                                  elp.locator_uid AS id,
                                  elp.as_of_date AS asOf,
                                  phoneTypeCode.code_short_desc_txt AS 'type',
                                  phoneUseCode.code_short_desc_txt AS 'use',
                                  tl.cntry_cd AS countryCode,
                                  REPLACE(REPLACE(tl.phone_nbr_txt, '-', ''), ' ', '') AS phoneNumber,
                                  tl.extension_txt AS extension,
                                  STRING_ESCAPE(tl.email_address, 'json') AS email,
                                  STRING_ESCAPE(tl.url_address, 'json') AS url,
                                  elp.locator_desc_txt comments
                              FROM
                                  Entity_locator_participation elp
                              WITH
                                  (NOLOCK)
                                  JOIN Tele_locator tl
                              WITH
                                  (NOLOCK) ON elp.locator_uid = tl.tele_locator_uid
                                  LEFT JOIN NBS_SRTE.dbo.code_value_general phoneUseCode
                              WITH
                                  (NOLOCK) ON phoneUseCode.code = elp.use_cd
                                  AND phoneUseCode.code_set_nm = 'EL_USE_TELE_PAT'
                                  LEFT JOIN NBS_SRTE.dbo.code_value_general phoneTypeCode
                              WITH
                                  (NOLOCK) ON phoneTypeCode.code = cd
                                  AND phoneTypeCode.code_set_nm = 'EL_TYPE_TELE_PAT'
                              WHERE
                                  elp.entity_uid = p.person_uid
                                  AND elp.class_cd = 'TELE'
                                  AND elp.record_status_cd = 'ACTIVE'
                                  AND (
                                      tl.phone_nbr_txt IS NOT NULL
                                      OR tl.email_address IS NOT NULL
                                      OR tl.url_address IS NOT NULL
                                  )
                              ORDER BY asOf desc
                              FOR JSON
                                  PATH,
                                  INCLUDE_NULL_VALUES
                          ) AS phone
                  ) AS phone,
                  -- person_names
                  (
                      SELECT
                          (
                              SELECT
                                  pn.person_uid AS personUid,
                                  pn.person_name_seq AS 'sequence',
                              pn.as_of_date AS asOf,
                              nameTypeCode.code_short_desc_txt AS 'type',
                              namePrefixCode.code_short_desc_txt AS prefix,
                              STRING_ESCAPE(pn.first_nm, 'json') AS 'first',
                              STRING_ESCAPE(pn.middle_nm, 'json') AS middle,
                              STRING_ESCAPE(pn.middle_nm2, 'json') AS secondMiddle,
                              STRING_ESCAPE(pn.last_nm, 'json') AS 'last',
                              STRING_ESCAPE(pn.last_nm2, 'json') AS secondLast,
                              nameSuffixCode.code_short_desc_txt AS suffix,
                              STRING_ESCAPE(pn.nm_degree, 'json') AS degree
                              FROM
                                  person_name pn
                              WITH
                                  (NOLOCK)
                                  LEFT JOIN nbs_srte..code_value_general nameTypeCode
                              WITH
                                  (NOLOCK) ON pn.nm_use_cd = nameTypeCode.code
                                  AND nameTypeCode.code_set_nm = 'P_NM_USE'
                                  LEFT JOIN nbs_srte..code_value_general namePrefixCode
                              WITH
                                  (NOLOCK) ON pn.nm_prefix = namePrefixCode.code
                                  AND namePrefixCode.code_set_nm = 'P_NM_PFX'
                                  LEFT JOIN nbs_srte..code_value_general nameSuffixCode
                              WITH
                                  (NOLOCK) ON pn.nm_suffix = nameSuffixCode.code
                                  AND nameSuffixCode.code_set_nm = 'P_NM_SFX'
                              WHERE
                                  pn.person_uid = p.person_uid
                                  AND pn.record_status_cd = 'ACTIVE'
                              ORDER BY asOf desc
                              FOR JSON
                                  PATH,
                                  INCLUDE_NULL_VALUES
                          ) AS name
                  ) AS name,
                  -- sex & birth
                  (
                      SELECT
                          (
                              SELECT
                                  psb.as_of_date_sex AS asOf,
                                  psb.birth_time AS dateOfBirth,
                                  currentSexCode.code_short_desc_txt AS currentSex,
                                  sexUnknownCode.code_short_desc_txt AS sexUnknown,
                                  transgenderCode.code_short_desc_txt AS transgender,
                                  psb.additional_gender_cd AS additionalGender,
                                  birthGenderCode.code_short_desc_txt AS birthGender,
                                  multipleBirthCode.code_short_desc_txt AS multipleBirth,
                                  psb.birth_order_nbr AS birthOrder,
                                  pl.city_desc_txt AS birthCity,
                                  sc.code_desc_txt AS birthState,
                                  scc.code_desc_txt AS birthCounty,
                                  cc.code_desc_txt AS birthCountry
                              FROM
                                  person psb
                              WITH
                                  (NOLOCK)
                                  LEFT JOIN Entity_locator_participation elp ON elp.entity_uid = psb.person_uid
                                  AND elp.use_cd = 'BIR'
                                  LEFT JOIN Postal_locator pl ON pl.postal_locator_uid = elp.locator_uid
                                  LEFT JOIN NBS_SRTE.dbo.code_value_general transgenderCode
                              WITH
                                  (NOLOCK) ON transgenderCode.code = psb.preferred_gender_cd
                                  AND transgenderCode.code_set_nm = 'NBS_STD_GENDER_PARPT'
                                  LEFT JOIN NBS_SRTE.dbo.code_value_general multipleBirthCode
                              WITH
                                  (NOLOCK) ON multipleBirthCode.code = psb.multiple_birth_ind
                                  AND multipleBirthCode.code_set_nm = 'YNU'
                                  LEFT JOIN NBS_SRTE.dbo.code_value_general currentSexCode
                              WITH
                                  (NOLOCK) ON currentSexCode.code = psb.curr_sex_cd
                                  AND currentSexCode.code_set_nm = 'SEX'
                                  LEFT JOIN NBS_SRTE.dbo.code_value_general birthGenderCode
                              WITH
                                  (NOLOCK) ON birthGenderCode.code = psb.birth_gender_cd
                                  AND birthGenderCode.code_set_nm = 'SEX'
                                  LEFT JOIN NBS_SRTE.dbo.code_value_general sexUnknownCode
                              WITH
                                  (NOLOCK) ON sexUnknownCode.code = psb.sex_unk_reason_cd
                                  AND sexUnknownCode.code_set_nm = 'SEX_UNK_REASON'
                                  LEFT JOIN NBS_SRTE.dbo.state_code sc ON sc.state_cd = pl.state_cd
                                  LEFT JOIN NBS_SRTE.dbo.state_county_code_value scc ON scc.code = pl.cnty_cd
                                  AND scc.parent_is_cd = pl.state_cd
                                  LEFT JOIN NBS_SRTE.dbo.country_code cc ON cc.code = pl.cntry_cd
                              WHERE
                                  psb.person_uid = p.person_uid
                                  AND psb.record_status_cd = 'ACTIVE'
                              FOR JSON
                                  PATH,
                                  INCLUDE_NULL_VALUES,
                                  WITHOUT_ARRAY_WRAPPER
                          ) AS sexAndBirth
                  ) AS sexAndBirth,
                  -- Mortality
                  (
                      SELECT
                          (
                              SELECT
                                  mp.as_of_date_morbidity AS asOf,
                                  deceasedCode.code_short_desc_txt AS deceased,
                                  mp.deceased_time AS dateOfDeath,
                                  STRING_ESCAPE(pl.city_desc_txt, 'json') AS deathCity,
                                  sc.code_desc_txt AS deathState,
                                  scc.code_desc_txt AS deathCounty,
                                  cc.code_short_desc_txt AS deathCountry
                              FROM
                                  person mp WITH (NOLOCK)
                                  LEFT JOIN Entity_locator_participation elp WITH (NOLOCK) ON elp.entity_uid = mp.person_uid
                                  AND elp.class_cd = 'PST'
                                  AND elp.status_cd = 'A'
                                  AND elp.use_cd = 'DTH'
                                  LEFT JOIN Postal_locator pl WITH (NOLOCK) ON elp.locator_uid = pl.postal_locator_uid
                                  LEFT JOIN NBS_SRTE.dbo.state_code sc WITH (NOLOCK) ON sc.state_cd = pl.state_cd
                                  LEFT JOIN NBS_SRTE.dbo.state_county_code_value scc WITH (NOLOCK) ON scc.code = pl.cnty_cd
                                  LEFT JOIN NBS_SRTE.dbo.country_code cc WITH (NOLOCK) ON cc.code = pl.cntry_cd
                                  LEFT JOIN NBS_SRTE.dbo.code_value_general deceasedCode WITH (NOLOCK) ON deceasedCode.code = mp.deceased_ind_cd
                                  AND deceasedCode.code_set_nm = 'YNU'
                              WHERE
                                  mp.person_uid = p.person_uid
                              FOR JSON
                                  PATH,
                                  INCLUDE_NULL_VALUES,
                                  WITHOUT_ARRAY_WRAPPER
                          ) AS mortality
                  ) AS mortality,
                  -- General Patient Info
                  (
                      SELECT
                          (
                              SELECT
                                  p.as_of_date_general AS asOf,
                                  maritalStatusCode.code_short_desc_txt AS maritalStatus,
                                  p.mothers_maiden_nm AS mothersMaidenName,
                                  p.adults_in_house_nbr AS numberOfAdultsInResidence,
                                  p.children_in_house_nbr AS numberOfChildrenInResidence,
                                  occupationCode.code_short_desc_txt AS primaryOccupation,
                                  educationLevelCode.code_short_desc_txt AS educationLevel,
                                  languageCode.code_short_desc_txt AS primaryLanguage,
                                  englishCode.code_short_desc_txt AS speaksEnglish,
                                  p.ehars_id AS stateHivCaseId
                              FROM
                                  person gp WITH (NOLOCK)
                                  LEFT JOIN NBS_SRTE.dbo.code_value_general maritalStatusCode WITH (NOLOCK) ON maritalStatusCode.code = p.marital_status_cd
                                  AND maritalStatusCode.code_set_nm = 'P_MARITAL'
                                  LEFT JOIN NBS_SRTE.dbo.NAICS_Industry_code occupationCode WITH (NOLOCK) ON occupationCode.code = p.occupation_cd
                                  LEFT JOIN NBS_SRTE.dbo.code_value_general educationLevelCode WITH (NOLOCK) ON educationLevelCode.code = p.education_level_cd
                                  AND educationLevelCode.code_set_nm = 'P_EDUC_LVL'
                                  LEFT JOIN NBS_SRTE.dbo.Language_code languageCode WITH (NOLOCK) ON languageCode.code = p.prim_lang_cd
                                  LEFT JOIN NBS_SRTE.dbo.code_value_general englishCode WITH (NOLOCK) ON englishCode.code = p.speaks_english_cd
                                  AND englishCode.code_set_nm = 'YNU'
                              WHERE
                                  gp.person_uid = p.person_uid
                              FOR JSON
                                  PATH,
                                  INCLUDE_NULL_VALUES,
                                  WITHOUT_ARRAY_WRAPPER
                          ) AS general
                  ) AS general
          ) AS nested
      WHERE
          p.person_uid IN (:ids)
          AND p.record_status_cd = 'ACTIVE'
          ORDER BY p.add_time asc;
        """;

  static final String PERSON_RECORD_BY_PARENT_ID = """
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

  static final String PERSON_RECORD_BY_PERSON_ID = """
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

  static final String PERSON_RECORDS_BY_PERSON_IDS = """
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

  static final String FETCH_PATIENT_ADD_TIME_QUERY = """
      SELECT
          p.person_uid,
          p.add_time
      FROM
          person p
      WHERE
          p.person_uid IN (:ids)
      """;

  static final String FIND_NBS_ADD_TIME_AND_NAME_QUERY = """
      WITH
        id_settings (prefix, suffix, initial) AS (
          SELECT
            generator.UID_prefix_cd AS prefix,
            generator.UID_suffix_CD AS suffix,
            cast(config.config_value AS bigint) AS initial
          FROM
            Local_UID_generator generator
          WITH
            (NOLOCK),
            NBS_configuration config
          WITH
            (NOLOCK)
          WHERE
            generator.class_name_cd = 'PERSON'
            AND generator.type_cd = 'LOCAL'
            AND config.config_key = 'SEED_VALUE'
        )
      SELECT TOP 1
        CAST(SUBSTRING(p.local_id, LEN(id_settings.prefix) + 1, LEN(p.local_id) - LEN(id_settings.prefix) - LEN(id_settings.suffix)) AS bigint) - id_settings.initial AS personLocalId,
        CONCAT(COALESCE(pn.last_nm, '--'), ', ', COALESCE(pn.first_nm, '--')) AS name,
        p.add_time
      FROM
        id_settings, person p
        LEFT JOIN person_name pn ON pn.person_uid = p.person_uid
      WHERE
        p.person_uid = :id
      ORDER BY
        CASE
          WHEN pn.nm_use_cd = 'L' THEN 1
          ELSE 2
        END,
        pn.as_of_date DESC
      """;
}
