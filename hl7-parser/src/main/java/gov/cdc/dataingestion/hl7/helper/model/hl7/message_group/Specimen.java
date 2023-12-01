package gov.cdc.dataingestion.hl7.helper.model.hl7.message_group;
import ca.uhn.hl7v2.HL7Exception;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.observation.ObservationResult;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class Specimen {
    gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.specimen.Specimen specimen = new gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.specimen.Specimen(); //NOSONAR
    List<ObservationResult> observationResult = new ArrayList<>();

    public Specimen() {

    }

    public Specimen(ca.uhn.hl7v2.model.v251.group.ORU_R01_SPECIMEN specimen) throws HL7Exception {
        this.specimen = new gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.specimen.Specimen(specimen.getSPM());
        this.observationResult = new ArrayList<>();
        for(var item : specimen.getOBXAll()) {
            this.observationResult.add(new ObservationResult(item));
        }
    }

    public Specimen(gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.specimen.Specimen specimen) {
        this.specimen = specimen;
    }
}
