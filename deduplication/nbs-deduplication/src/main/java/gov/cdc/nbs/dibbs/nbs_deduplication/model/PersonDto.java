package gov.cdc.nbs.dibbs.nbs_deduplication.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonDto {
  private Long personUid;
  @JsonFormat(pattern = "yyyy-MM-dd")
  private Timestamp birthTime;

}
