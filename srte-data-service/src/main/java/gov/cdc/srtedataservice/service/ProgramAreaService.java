package gov.cdc.srtedataservice.service;

import gov.cdc.srtedataservice.repository.nbs.srte.model.ProgramAreaCode;
import gov.cdc.srtedataservice.repository.nbs.srte.repository.ProgramAreaCodeRepository;
import gov.cdc.srtedataservice.service.interfaces.IProgramAreaService;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProgramAreaService implements IProgramAreaService {
  private final ProgramAreaCodeRepository programAreaCodeRepository;

  public ProgramAreaService(ProgramAreaCodeRepository programAreaCodeRepository) {
    this.programAreaCodeRepository = programAreaCodeRepository;
  }

  public List<ProgramAreaCode> getAllProgramAreaCode() {
    var progCodeRes = programAreaCodeRepository.findAll();
    if (!progCodeRes.isEmpty()) {
      return progCodeRes;
    } else {
      return new ArrayList<>();
    }
  }
}
