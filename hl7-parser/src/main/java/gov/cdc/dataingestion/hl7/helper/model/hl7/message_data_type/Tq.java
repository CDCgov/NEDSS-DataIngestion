package gov.cdc.dataingestion.hl7.helper.model.hl7.message_data_type;

import ca.uhn.hl7v2.model.v251.datatype.TQ;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tq {
    Cq quantity = new Cq();
    Ri interval = new Ri();
    String duration;
    Ts startDateTime = new Ts();
    Ts endDateTime = new Ts();
    String priority;
    String condition;
    String text;
    String conjunction;
    Osd orderSequencing = new Osd();
    Ce occurrenceDuration = new Ce();
    String totalOccurrences;
    public Tq(TQ tq) {
        this.quantity = new Cq(tq.getQuantity());
        this.interval = new Ri(tq.getInterval());
        this.duration = tq.getDuration().getValue();
        this.startDateTime = new Ts(tq.getStartDateTime());
        this.endDateTime = new Ts(tq.getEndDateTime());
        this.priority = tq.getPriority().getValue();
        this.condition = tq.getCondition().getValue();
        this.text = tq.getText().getValue();
        this.conjunction = tq.getConjunction().getValue();
        this.orderSequencing = new Osd(tq.getOrderSequencing());
        this.occurrenceDuration = new Ce(tq.getOccurrenceDuration());
        this.totalOccurrences = tq.getTotalOccurrences().getValue();
    }

    public Tq() {

    }
}
