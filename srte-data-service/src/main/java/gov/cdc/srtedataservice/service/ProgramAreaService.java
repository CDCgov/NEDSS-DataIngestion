package gov.cdc.srtedataservice.service;

import gov.cdc.srtedataservice.repository.nbs.srte.model.ProgramAreaCode;
import gov.cdc.srtedataservice.repository.nbs.srte.repository.ProgramAreaCodeRepository;
import gov.cdc.srtedataservice.service.interfaces.IProgramAreaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 1192 - duplicate literal
 135 - for loop
 117 - naming
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192", "java:S135", "java:S117"})
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