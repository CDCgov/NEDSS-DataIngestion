package gov.cdc.srtedataservice.service;

import gov.cdc.srtedataservice.repository.nbs.srte.model.JurisdictionCode;
import gov.cdc.srtedataservice.repository.nbs.srte.repository.JurisdictionCodeRepository;
import gov.cdc.srtedataservice.service.interfaces.IJurisdictionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JurisdictionService implements IJurisdictionService {
    private final JurisdictionCodeRepository jurisdictionCodeRepository;

    public JurisdictionService(JurisdictionCodeRepository jurisdictionCodeRepository) {
        this.jurisdictionCodeRepository = jurisdictionCodeRepository;
    }

    public List<JurisdictionCode> getJurisdictionCode() {
        var jusCode = jurisdictionCodeRepository.findAll();
        if (!jusCode.isEmpty()) {
            return jusCode;
        }
        else
        {
            return new ArrayList<>();
        }
    }
}
