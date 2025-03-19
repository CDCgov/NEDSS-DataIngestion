package gov.cdc.dataingestion.deadletter.repository;

import gov.cdc.dataingestion.deadletter.repository.model.ElrDeadLetterModel;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IElrDeadLetterRepository extends JpaRepository<ElrDeadLetterModel, String> {
    Optional<List<ElrDeadLetterModel>> findAllDltRecordByDltStatus (String dltStatus, Sort sort);
    @Query(value = """
            select * from NBS_DataIngest.dbo.elr_dlt ed
             WHERE created_on BETWEEN :startDate AND :endDate order by created_on desc;
            """,
            nativeQuery = true)
    Optional<List<ElrDeadLetterModel>> findAllDltRecordsByDate (@Param("startDate") String startDate,@Param("endDate") String endDate);

}