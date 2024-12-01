package gov.cdc.dataprocessing.model.container.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ProviderDataForPrintContainer implements Serializable {

    private static final long serialVersionUID = -6215612766789766219L;
    private String providerStreetAddress1= "";
    private String providerCity;
    private String providerState;
    private String providerZip;
    private String providerPhone= "";
    private String providerPhoneExtension;
    private String facilityName= "";
    private String facilityCity;
    private String facilityState;
    private String facilityAddress1= "";
    private String facilityAddress2= "";
    private String facility= "";
    private String facilityZip= "";
    private String facilityPhoneExtension;
    private String facilityPhone= "";
}
