package gov.cdc.dataprocessing.model.dto.phc;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.phc.ConfirmationMethod;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 1118 - Private constructor complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118"})
public class ConfirmationMethodDto extends BaseContainer
{
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
