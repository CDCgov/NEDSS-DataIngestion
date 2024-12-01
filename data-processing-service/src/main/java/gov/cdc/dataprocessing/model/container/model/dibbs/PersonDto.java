package gov.cdc.dataprocessing.model.container.model.dibbs;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;


@Data
public class PersonDto {
  @JsonFormat(pattern = "yyyy-MM-dd")
  private Timestamp birthTime;

  private Long personUid;
}
