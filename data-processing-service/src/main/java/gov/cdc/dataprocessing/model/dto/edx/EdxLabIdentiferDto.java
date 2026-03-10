package gov.cdc.dataprocessing.model.dto.edx;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EdxLabIdentiferDto {
  private static final long serialVersionUID = 1L;
  private String identifer;
  private String subMapID;
  private Long observationUid;
  private List<String> observationValues;
}
