package gov.cdc.dataprocessing.model.container.model.dibbs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DibbsPersonNameDto {
  private String firstNm;
  private String lastNm;
}
