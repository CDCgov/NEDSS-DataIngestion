package gov.cdc.dataingestion.hl7.helper.model.hl7.message_segment;
import ca.uhn.hl7v2.model.v251.segment.SFT;
import gov.cdc.dataingestion.hl7.helper.model.hl7.message_data_type.Ts;
import gov.cdc.dataingestion.hl7.helper.model.hl7.message_data_type.Xon;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SoftwareSegment {
    Xon softwareVendorOrganization = new Xon();
    String softwareCertifiedVersionOrReleaseNumber;
    String softwareProductName;
    String softwareBinaryId;
    String softwareProductInformation;
    Ts softwareInstallDate = new Ts();

    public SoftwareSegment(SFT sft) {
        this.softwareVendorOrganization = new Xon(sft.getSoftwareVendorOrganization());
        this.softwareCertifiedVersionOrReleaseNumber = sft.getSoftwareCertifiedVersionOrReleaseNumber().getValue();
        this.softwareProductName = sft.getSoftwareProductName().getValue();
        this.softwareBinaryId = sft.getSoftwareBinaryID().getValue();
        this.softwareProductInformation = sft.getSoftwareProductInformation().getValue();
        this.softwareInstallDate = new Ts(sft.getSoftwareInstallDate());
    }

    public SoftwareSegment() {
    }
}
