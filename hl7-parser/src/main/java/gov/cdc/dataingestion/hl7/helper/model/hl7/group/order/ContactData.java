package gov.cdc.dataingestion.hl7.helper.model.hl7.group.order;

import gov.cdc.dataingestion.hl7.helper.model.hl7.message_data_type.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static gov.cdc.dataingestion.hl7.helper.helper.ModelListHelper.*;

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
        this.contactRole = getCeList(ctd.getContactRole());
        this.contactName = getXpnList(ctd.getContactName());
        this.contactAddress = getXadList(ctd.getContactAddress());
        this.contactLocation = new Pl(ctd.getContactLocation());
        this.contactCommunicationInformation = getXtnList(ctd.getContactCommunicationInformation());
        this.preferredMethodOfContact = new Ce(ctd.getPreferredMethodOfContact());
        this.contactIdentifiers = getPlnList(ctd.getContactIdentifiers());
    }

    public ContactData() {

    }
}
