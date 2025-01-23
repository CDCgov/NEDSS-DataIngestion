package gov.cdc.nbs.deduplication.seed.step;

import javax.sql.DataSource;

import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import gov.cdc.nbs.deduplication.seed.mapper.NbsPersonMapper;
import gov.cdc.nbs.deduplication.seed.model.NbsPerson;

@Component
public class PersonReader extends JdbcPagingItemReader<NbsPerson> {

  private final NbsPersonMapper mapper = new NbsPersonMapper();

  public PersonReader(
          @Qualifier("nbs") DataSource dataSource,
          @Qualifier("deduplication") DataSource deduplicationDataSource
  ) throws Exception {

    // Fetch the last high-water mark from deduplication DB
    Long lastProcessedId = getLastProcessedId(deduplicationDataSource);

    SqlPagingQueryProviderFactoryBean provider = new SqlPagingQueryProviderFactoryBean();
    provider.setDataSource(dataSource);
    provider.setSelectClause("SELECT person_uid, person_parent_uid");
    provider.setFromClause("FROM person");
    provider.setWhereClause("WHERE person_uid = person_parent_uid AND record_status_cd = 'ACTIVE' AND cd = 'PAT'");
    provider.setSortKey("person_uid");

    this.setName("nbsPersonReader");
    this.setDataSource(dataSource);
    PagingQueryProvider queryProvider = provider.getObject();
    if (queryProvider != null) {
      this.setQueryProvider(queryProvider);
    }
    this.setRowMapper(mapper);
    this.setPageSize(10000);
  }

  private Long getLastProcessedId(DataSource deduplicationDataSource) {
    // Query to fetch the last high-water mark
    String sql = "SELECT COALESCE(MAX(last_processed_id), 0) FROM deduplication_watermark";
    try (var connection = deduplicationDataSource.getConnection();
         var statement = connection.createStatement();
         var resultSet = statement.executeQuery(sql)) {
      if (resultSet.next()) {
        return resultSet.getLong(1);
      }
    } catch (Exception ex) {
      throw new RuntimeException("Failed to fetch the last processed ID from deduplication_watermark", ex);
    }
    return 0L; // Default to 0 if no record exists
  }

}
