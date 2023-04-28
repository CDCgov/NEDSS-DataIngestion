package gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType;
import ca.uhn.hl7v2.model.v251.datatype.VID;
import lombok.Getter;

@Getter
public class Vid {
    String versionId;
    Ce internationalizationCode;
    Ce internationalVersionId;

    public Vid(VID vid) {
        this.versionId = vid.getVersionID().getValue();
        this.internationalizationCode = new Ce(vid.getInternationalizationCode());
        this.internationalVersionId = new Ce(vid.getInternationalVersionID());
    }
}
