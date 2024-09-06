package gov.cdc.nbsDedup.service.interfaces.jurisdiction;


import gov.cdc.nbsDedup.nbs.srte.model.JurisdictionCode;
import java.util.List;

public interface IJurisdictionService {

    List<JurisdictionCode> getJurisdictionCode();
}
