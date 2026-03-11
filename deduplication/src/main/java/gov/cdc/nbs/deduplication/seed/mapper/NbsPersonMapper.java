package gov.cdc.nbs.deduplication.seed.mapper;

import gov.cdc.nbs.deduplication.seed.model.NbsPerson;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class NbsPersonMapper implements RowMapper<NbsPerson> {

  @Override
  @Nullable public NbsPerson mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
    return new NbsPerson(rs.getString("person_uid"), rs.getString("person_parent_uid"));
  }
}
