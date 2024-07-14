package gov.cdc.dataprocessing.service.model.wds;


import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuppressWarnings("all")
public class WdsReport {
    private WdsValueCodedReport wdsValueCodedReport;
    private List<WdsValueTextReport> wdsValueTextReportList = new ArrayList<>();
    private List<WdsValueNumericReport> wdsValueNumericReportList = new ArrayList<>();

    private String Action;
    private String message;
    private boolean algorithmMatched;
}
