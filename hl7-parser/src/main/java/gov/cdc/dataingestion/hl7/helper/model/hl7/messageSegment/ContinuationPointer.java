package gov.cdc.dataingestion.hl7.helper.model.hl7.messageSegment;
import ca.uhn.hl7v2.model.v251.segment.DSC;
import lombok.Getter;
import lombok.Setter;
import static gov.cdc.dataingestion.hl7.helper.helper.modelListHelper.*;

import java.util.List;

@Getter
@Setter
public class ContinuationPointer {
    String continuationPointer;
    String continuationStyle;

    public ContinuationPointer(DSC dsc) {
        this.continuationPointer = dsc.getContinuationPointer().getValue();
        this.continuationStyle = dsc.getContinuationStyle().getValue();
    }

    public ContinuationPointer() {

    }
}
