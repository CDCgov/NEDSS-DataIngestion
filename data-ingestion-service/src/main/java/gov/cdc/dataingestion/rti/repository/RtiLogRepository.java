package gov.cdc.dataingestion.rti.repository;

import gov.cdc.dataingestion.odse.repository.model.EdxActivityLogModelView;
import gov.cdc.dataingestion.rti.repository.model.RtiLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RtiLogRepository extends JpaRepository<RtiLog, String> {
    @Query(value = """
            SELECT id
                  ,nbs_interface_id
                  ,rti_step
                  ,stack_trace
                  ,created_on
              FROM rti_log
              WHERE nbs_interface_id = :nbsInterfaceId
            """,
            nativeQuery = true)
    List<RtiLog> getRtiByNbsInterfaceId(@Param("nbsInterfaceId") String nbsInterfaceId);
}
