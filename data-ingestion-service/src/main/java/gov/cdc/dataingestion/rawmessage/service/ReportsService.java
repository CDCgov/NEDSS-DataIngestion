package gov.cdc.dataingestion.rawmessage.service;

import gov.cdc.dataingestion.nbs.repository.NbsInterfaceRepository;
import gov.cdc.dataingestion.nbs.repository.model.NbsInterfaceModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportsService {
    private final NbsInterfaceRepository nbsInterfaceRepo;

    public List<NbsInterfaceModel> getAllSubmissions() {
        return nbsInterfaceRepo.findAll();
    }
}
