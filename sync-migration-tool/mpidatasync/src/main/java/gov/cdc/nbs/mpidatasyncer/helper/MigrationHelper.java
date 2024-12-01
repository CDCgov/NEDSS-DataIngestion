package gov.cdc.nbs.mpidatasyncer.helper;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class MigrationHelper {
  private Long maxPersonUid;
}
