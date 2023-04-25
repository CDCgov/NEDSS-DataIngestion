package gov.cdc.dataingestion.hl7.helper.model.hl7.messageGroup;
import ca.uhn.hl7v2.HL7Exception;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.observation.ObservationResult;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.shared.NoteAndComment;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class Observation {
    ObservationResult observationResult;
    List<NoteAndComment> noteAndComments;

    public Observation(ca.uhn.hl7v2.model.v251.group.ORU_R01_OBSERVATION observation) throws HL7Exception {
        this.observationResult = new ObservationResult(observation.getOBX());
        this.noteAndComments = new ArrayList<>();
        for(var item : observation.getNTEAll()) {
            this.noteAndComments.add(new NoteAndComment(item));
        }
    }
}
