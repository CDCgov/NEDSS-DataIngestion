package gov.cdc.nbsDedup.service.interfaces.jurisdiction;

import gov.cdc.nbsDedup.nbs.srte.model.ProgramAreaCode;
import java.util.List;

public interface IProgramAreaService {
    List<ProgramAreaCode> getAllProgramAreaCode();

}
