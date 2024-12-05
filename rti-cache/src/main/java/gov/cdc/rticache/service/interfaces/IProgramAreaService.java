package gov.cdc.rticache.service.interfaces;

import gov.cdc.rticache.repository.nbs.srte.model.ProgramAreaCode;

import java.util.List;

public interface IProgramAreaService {
    List<ProgramAreaCode> getAllProgramAreaCode();
}
