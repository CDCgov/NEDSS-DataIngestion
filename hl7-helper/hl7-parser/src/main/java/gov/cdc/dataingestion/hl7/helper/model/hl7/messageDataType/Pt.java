package gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType;
import ca.uhn.hl7v2.model.v251.datatype.PT;
import lombok.Getter;

@Getter
public class Pt {
    String processingId;
    String processingMode;
    public Pt(PT pt) {
       this.processingId = pt.getProcessingID().getValue();
       this.processingMode = pt.getProcessingMode().getValue();
    }
}
