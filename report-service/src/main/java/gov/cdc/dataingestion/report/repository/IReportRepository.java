package gov.cdc.dataingestion.report.repository;

import gov.cdc.dataingestion.report.repository.model.Report;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

/**
 * Reports repository.
 */
@Component
public interface IReportRepository extends MongoRepository<Report, String> {
}
