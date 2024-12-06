package gov.cdc.dataingestion.reportstatus.model;

import gov.cdc.dataingestion.odse.repository.model.EdxActivityDetailLog;
import gov.cdc.dataingestion.odse.repository.model.EdxActivityLog;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EdxLogStatus {
    private EdxActivityLog edxActivityLog;
    private List<EdxActivityDetailLog> edxActivityDetailLogList = new ArrayList<>();
}
