package gov.cdc.dataingestion.reportstatus.service;

import gov.cdc.dataingestion.nbs.repository.NbsInterfaceRepository;
import gov.cdc.dataingestion.nbs.repository.model.NbsInterfaceModel;
import gov.cdc.dataingestion.reportstatus.model.ReportStatusIdData;
import gov.cdc.dataingestion.reportstatus.repository.IReportStatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReportStatusService {
    private static Logger logger = LoggerFactory.getLogger(ReportStatusService.class);
    private final IReportStatusRepository iReportStatusRepository;
    private final NbsInterfaceRepository nbsInterfaceRepository;

    public ReportStatusService(IReportStatusRepository iReportStatusRepository, NbsInterfaceRepository nbsInterfaceRepository) {
        this.iReportStatusRepository = iReportStatusRepository;
        this.nbsInterfaceRepository = nbsInterfaceRepository;
    }

    public String getStatusForReport(String id) {
        Optional<ReportStatusIdData > reportStatusIdData = iReportStatusRepository.findByRawMessageId(id);
        if(reportStatusIdData.isEmpty()) {
            return "Provided UUID is not present in the database.";
        }

        Optional<NbsInterfaceModel> nbsInterfaceModel = nbsInterfaceRepository.findByNbsInterfaceUid(reportStatusIdData.get().getNbsInterfaceUid());
        if(nbsInterfaceModel.isEmpty()) {
            return "Couldn't find status for the requested ID.";
        }
        return nbsInterfaceModel.get().getRecordStatusCd();
    }
}
