package gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType;

import ca.uhn.hl7v2.model.v251.datatype.RI;
import ca.uhn.hl7v2.model.v251.datatype.RPT;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Rpt {
    Cwe repeatPatternCode;
    String calendarAlignment;
    String phaseRangeBeginValue;
    String phaseRangeEndValue;
    String periodQuantity;
    String periodUnits;
    String institutionSpecifiedTime;
    String event;
    String eventOffsetQuantity;
    String eventOffsetUnits;
    String generalTimingSpecification;
    public Rpt(RPT rpt) {
        this.repeatPatternCode = new Cwe(rpt.getRepeatPatternCode());
        this.calendarAlignment = rpt.getCalendarAlignment().getValue();
        this.phaseRangeBeginValue = rpt.getPhaseRangeBeginValue().getValue();
        this.phaseRangeEndValue = rpt.getPhaseRangeEndValue().getValue();
        this.periodQuantity = rpt.getPeriodQuantity().getValue();
        this.periodUnits = rpt.getPeriodUnits().getValue();
        this.institutionSpecifiedTime= rpt.getInstitutionSpecifiedTime().getValue();
        this.event = rpt.getEvent().getValue();
        this.eventOffsetQuantity = rpt.getEventOffsetQuantity().getValue();
        this.eventOffsetUnits = rpt.getEventOffsetUnits().getValue();
        this.generalTimingSpecification = rpt.getGeneralTimingSpecification().getValue();
    }

    public Rpt() {

    }
}
