package gov.cdc.dataingestion.hl7.helper.model.hl7.group.order;

import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import static gov.cdc.dataingestion.hl7.helper.helper.modelListHelper.*;

@Getter
@Setter
public class ContactData {
    List<Ce> contactRole;
    List<Xpn> contactName;
    List<Xad> contactAddress;
    Pl contactLocation;
    List<Xtn> contactCommunicationInformation;
    Ce preferredMethodOfContact;
    List<Pln> contactIdentifiers;

    public ContactData(ca.uhn.hl7v2.model.v251.segment.CTD ctd) {
        this.contactRole = GetCeList(ctd.getContactRole());
        this.contactName = GetXpnList(ctd.getContactName());
        this.contactAddress = GetXadList(ctd.getContactAddress());
        this.contactLocation = new Pl(ctd.getContactLocation());
        this.contactCommunicationInformation = GetXtnList(ctd.getContactCommunicationInformation());
        this.preferredMethodOfContact = new Ce(ctd.getPreferredMethodOfContact());
        this.contactIdentifiers = GetPlnList(ctd.getContactIdentifiers());
    }
}
