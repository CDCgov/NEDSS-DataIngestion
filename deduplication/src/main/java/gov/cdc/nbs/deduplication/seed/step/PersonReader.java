package gov.cdc.nbs.deduplication.seed.step;

import javax.sql.DataSource;
import java.sql.ResultSet;

import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import gov.cdc.nbs.deduplication.seed.mapper.NbsPersonMapper;
import gov.cdc.nbs.deduplication.seed.model.NbsPerson;

@Component
public class PersonReader extends JdbcCursorItemReader<NbsPerson> {

  private final NbsPersonMapper mapper = new NbsPersonMapper();

  public PersonReader(@Qualifier("nbs") DataSource dataSource) throws Exception {

    this.setDataSource(dataSource);
    this.setSql("SELECT person_uid, person_parent_uid, address, name, phone, drivers_license, race FROM person WHERE person_uid = person_parent_uid");

    this.setRowMapper((ResultSet rs, int rowNum) -> {
      Long personUid = rs.getLong("person_uid");
      Long personParentUid = rs.getLong("person_parent_uid");

      return mapper.mapRow(rs, rowNum);
    });

    this.setFetchSize(1000);
  }
}
