package gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient;
import ca.uhn.hl7v2.model.v251.datatype.CX;
import ca.uhn.hl7v2.model.v251.datatype.XPN;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Cx;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Xpn;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PatientIdentification {
    // Set ID - PID
    String setPid;
    Cx patientId;
    List<Cx> patientIdentifierList;
    List<Cx> alternativePatientId;
    List<Xpn> patientName;
    List<Xpn> motherMaidenName;
    public PatientIdentification(ca.uhn.hl7v2.model.v251.segment.PID patientIdentification) {
        this.setPid = patientIdentification.getSetIDPID().getValue();
        this.patientId = new Cx(patientIdentification.getPatientID());
        this.patientIdentifierList = GetCxList(patientIdentification.getPatientIdentifierList());
        this.alternativePatientId = GetCxList(patientIdentification.getAlternatePatientIDPID());
        this.patientName = GetXpnList(patientIdentification.getPatientName());

    }

    private ArrayList<Xpn> GetXpnList(XPN[] xpns) {
        var lst = new ArrayList<Xpn>();
        for(var data: xpns) {
            Xpn item = new Xpn(data);
            lst.add(item);
        }
        return lst;
    }

    private ArrayList<Cx> GetCxList(CX[] cxs) {
        var lst = new ArrayList<Cx>();
        for(var data: cxs) {
            Cx item = new Cx(data);
            lst.add(item);
        }
        return lst;
    }


}
