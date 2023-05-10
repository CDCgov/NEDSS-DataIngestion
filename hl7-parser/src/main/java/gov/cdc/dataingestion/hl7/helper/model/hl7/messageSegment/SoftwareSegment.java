package gov.cdc.dataingestion.hl7.helper.model.hl7.messageSegment;
import ca.uhn.hl7v2.model.v251.segment.SFT;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Ts;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Xon;
import lombok.Getter;

@Getter
public class SoftwareSegment {
    Xon softwareVendorOrganization;
    String softwareCertifiedVersionOrReleaseNumber;
    String softwareProductName;
    String softwareBinaryId;
    String softwareProductInformation;
    Ts softwareInstallDate;

    public SoftwareSegment(SFT sft) {
        this.softwareVendorOrganization = new Xon(sft.getSoftwareVendorOrganization());
        this.softwareCertifiedVersionOrReleaseNumber = sft.getSoftwareCertifiedVersionOrReleaseNumber().getValue();
        this.softwareProductName = sft.getSoftwareProductName().getValue();
        this.softwareBinaryId = sft.getSoftwareBinaryID().getValue();
        this.softwareProductInformation = sft.getSoftwareProductInformation().getValue();
        this.softwareInstallDate = new Ts(sft.getSoftwareInstallDate());
    }
}
