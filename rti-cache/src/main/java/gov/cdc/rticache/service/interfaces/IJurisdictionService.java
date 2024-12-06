package gov.cdc.rticache.service.interfaces;

import gov.cdc.rticache.repository.nbs.srte.model.JurisdictionCode;

import java.util.List;

public interface IJurisdictionService {
    List<JurisdictionCode> getJurisdictionCode();
}
