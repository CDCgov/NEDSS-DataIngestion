package gov.cdc.dataprocessing.utilities;

import gov.cdc.dataprocessing.model.classic_model.dt.EdxLabInformationDT;
import gov.cdc.dataprocessing.model.classic_model.vo.LabResultProxyVO;
import gov.cdc.dataprocessing.model.phdc.HL7MSHType;

public class LabResultHandler {
    public LabResultProxyVO getLabResultMessage(HL7MSHType hl7MSHType, EdxLabInformationDT edxLabInformationDT) {
        LabResultProxyVO labResultProxy  = new LabResultProxyVO();
        return labResultProxy;
    }
}
