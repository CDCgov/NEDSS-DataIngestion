package gov.cdc.dataprocessing.model.dto.phc;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.phc.ConfirmationMethod;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@SuppressWarnings("all")
public class ConfirmationMethodDto extends BaseContainer {
    private static final long serialVersionUID = 1L;

    private Long publicHealthCaseUid;

    private String confirmationMethodCd;

    private String confirmationMethodDescTxt;

    private Timestamp confirmationMethodTime;

    private String progAreaCd = null;

    private String jurisdictionCd = null;

    private Long programJurisdictionOid = null;

    private String sharedInd = null;

    public ConfirmationMethodDto() {
        itDirty = false;
        itNew = true;
        itDelete = false;
    }

    public ConfirmationMethodDto(ConfirmationMethod confirmationMethod) {
        itDirty = false;
        itNew = true;
        itDelete = false;

        publicHealthCaseUid = confirmationMethod.getPublicHealthCaseUid();
        confirmationMethodCd = confirmationMethod.getConfirmationMethodCd();
        confirmationMethodDescTxt = confirmationMethod.getConfirmationMethodDescTxt();
        confirmationMethodTime = confirmationMethod.getConfirmationMethodTime();
    }

}
