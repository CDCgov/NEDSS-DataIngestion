package gov.cdc.nbs.deduplication.seed.step;

import javax.sql.DataSource;

import gov.cdc.nbs.deduplication.seed.mapper.NbsPersonMapper;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import gov.cdc.nbs.deduplication.seed.model.NbsPerson;

@Component
public class PersonReader extends JdbcPagingItemReader<NbsPerson> {

  private static final String SELECT = """
           SELECT
            p.person_uid ,
            p.person_parent_uid,
            cast(p.birth_time as Date) birth_date,
            p.curr_sex_cd sex,
            p.additional_gender_cd gender,
            nested.address,
            nested.phone,
            nested.name,
            nested.drivers_license,
            nested.ssn,
            nested.race
        """;

  private static final String FROM = """
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
               --ssn
               (
                   SELECT
                    (
                     SELECT
                         TOP 1 eid.root_extension_txt
                     FROM
                         entity_id eid WITH (NOLOCK)
                     WHERE
                         eid.entity_uid = p.person_uid
                     AND eid.type_cd = 'SS') as ssn) as ssn,
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
               ) AS name,

                -- Drivers license
               (
                 SELECT
                   (
                     SELECT
                       ei.assigning_authority_cd authority,
                       STRING_ESCAPE(REPLACE(REPLACE(ei.root_extension_txt,'-',''),' ',''), 'json') value
                     FROM
                       entity_id ei WITH (NOLOCK)
                     WHERE
                       ei.entity_uid = p.person_uid
                       AND ei.type_cd = 'DL' FOR json path
                   ) AS drivers_license
               ) AS drivers_license
           ) AS nested
    """;


  private final NbsPersonMapper mapper = new NbsPersonMapper();

  public PersonReader(
      @Qualifier("nbs") DataSource dataSource) throws Exception {

    SqlPagingQueryProviderFactoryBean provider = new SqlPagingQueryProviderFactoryBean();
    provider.setDataSource(dataSource);
    provider.setSelectClause(SELECT);
    provider.setFromClause(FROM);
    provider.setWhereClause("WHERE p.record_status_cd = 'ACTIVE' AND p.cd = 'PAT'");
    provider.setSortKey("person_uid");
    this.setName("nbsPersonReader");
    this.setDataSource(dataSource);
    PagingQueryProvider queryProvider = provider.getObject();
    if (queryProvider != null) {
      this.setQueryProvider(queryProvider);
    }
    this.setRowMapper(mapper);
    this.setPageSize(100);
  }

}
