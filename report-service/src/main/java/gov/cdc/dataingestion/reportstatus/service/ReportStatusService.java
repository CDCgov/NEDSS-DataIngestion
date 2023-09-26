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
        logger.debug("NBS Interface id retrieved from the elr_record_status_id table is: {}", reportStatusIdData.get().getNbsInterfaceUid());

        Optional<NbsInterfaceModel> nbsInterfaceModel = nbsInterfaceRepository.findByNbsInterfaceUid(reportStatusIdData.get().getNbsInterfaceUid());
        logger.debug("NBS Interface table id for the requested report id is: {}", nbsInterfaceModel.get().getNbsInterfaceUid());

        return nbsInterfaceModel.get().getRecordStatusCd();
    }
}
