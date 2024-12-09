package gov.cdc.srtedataservice.service.interfaces;

import gov.cdc.srtedataservice.repository.nbs.srte.model.ProgramAreaCode;

import java.util.List;

public interface IProgramAreaService {
    List<ProgramAreaCode> getAllProgramAreaCode();
}
