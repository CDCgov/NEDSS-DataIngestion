package gov.cdc.dataprocessing.model.dto;

import gov.cdc.dataprocessing.model.container.BaseContainer;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
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

    private boolean itDirty = false;

    private boolean itNew = true;

    private boolean itDelete = false;

}
