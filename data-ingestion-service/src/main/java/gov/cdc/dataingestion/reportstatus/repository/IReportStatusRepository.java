package gov.cdc.dataingestion.reportstatus.repository;

import gov.cdc.dataingestion.reportstatus.model.ReportStatusIdData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IReportStatusRepository extends JpaRepository<ReportStatusIdData, String> {
    List<ReportStatusIdData> findByRawMessageId(String id);
}
