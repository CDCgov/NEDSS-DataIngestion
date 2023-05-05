package gov.cdc.dataingestion.hl7.helper.model.hl7.group.order;

import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import static gov.cdc.dataingestion.hl7.helper.helper.modelListHelper.*;

@Getter
@Setter
public class ContactData {
    List<Ce> contactRole = new ArrayList<>();
    List<Xpn> contactName = new ArrayList<>();
    List<Xad> contactAddress = new ArrayList<>();
    Pl contactLocation = new Pl();
    List<Xtn> contactCommunicationInformation = new ArrayList<>();
    Ce preferredMethodOfContact = new Ce();
    List<Pln> contactIdentifiers = new ArrayList<>();

    public ContactData(ca.uhn.hl7v2.model.v251.segment.CTD ctd) {
        this.contactRole = GetCeList(ctd.getContactRole());
        this.contactName = GetXpnList(ctd.getContactName());
        this.contactAddress = GetXadList(ctd.getContactAddress());
        this.contactLocation = new Pl(ctd.getContactLocation());
        this.contactCommunicationInformation = GetXtnList(ctd.getContactCommunicationInformation());
        this.preferredMethodOfContact = new Ce(ctd.getPreferredMethodOfContact());
        this.contactIdentifiers = GetPlnList(ctd.getContactIdentifiers());
    }

    public ContactData() {

    }
}
