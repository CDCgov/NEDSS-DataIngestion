package gov.cdc.nbs.dibbs.nbs_deduplication.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonNameDto {
  private String firstNm;
  private String lastNm;
}
