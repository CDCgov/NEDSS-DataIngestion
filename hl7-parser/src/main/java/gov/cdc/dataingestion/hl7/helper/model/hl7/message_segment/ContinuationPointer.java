package gov.cdc.dataingestion.hl7.helper.model.hl7.message_segment;
import ca.uhn.hl7v2.model.v251.segment.DSC;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContinuationPointer {
    String continuationPointer; //NOSONAR
    String continuationStyle;

    public ContinuationPointer(DSC dsc) {
        this.continuationPointer = dsc.getContinuationPointer().getValue();
        this.continuationStyle = dsc.getContinuationStyle().getValue();
    }

    public ContinuationPointer() {

    }
}
