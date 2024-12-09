package gov.cdc.srtedataservice.service.interfaces;

import gov.cdc.srtedataservice.repository.nbs.srte.model.JurisdictionCode;

import java.util.List;

public interface IJurisdictionService {
    List<JurisdictionCode> getJurisdictionCode();
}
