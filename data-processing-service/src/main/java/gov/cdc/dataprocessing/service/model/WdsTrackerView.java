package gov.cdc.dataprocessing.service.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WdsTrackerView {
    private List<WdsReport> wdsReport;
}
