package gov.cdc.dataingestion.deadletter.repository;

import gov.cdc.dataingestion.deadletter.repository.model.ElrDeadLetterModel;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IElrDeadLetterRepository extends JpaRepository<ElrDeadLetterModel, String> {
    Optional<List<ElrDeadLetterModel>> findAllDltRecordByDltStatus(String dltStatus, Sort sort);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO elr_dlt (error_message_id, error_message_source, error_stack_trace, error_stack_trace_short, dlt_status, dlt_occurrence, message, created_by, updated_by) VALUES(:id, :topicName, :errorStatus, :errorStatus , 'KAFKA_ERROR_' + :type, :dltOccurrence, :payload, 'elr_raw_service', 'elr_raw_service')", nativeQuery = true)
    void addErrorStatusForRawId(String id, String topicName, String type, String payload, String errorStatus, int dltOccurrence);

    @Modifying
    @Transactional
    @Query(value = "UPDATE elr_dlt SET dlt_occurrence = :dltOccurrence, dlt_status = :errorStatus WHERE error_message_id =:id", nativeQuery = true)
    void updateDltOccurrenceForRawId(String id, int dltOccurrence, String errorStatus);

    @Modifying
    @Transactional
    @Query(value = "UPDATE elr_dlt SET dlt_status = :errorStatus WHERE error_message_id = :id", nativeQuery = true)
    void updateErrorStatusForRawId(String id, String errorStatus);

    @Query(value = "SELECT * FROM elr_dlt WHERE dlt_status LIKE '%KAFKA%' AND dlt_occurrence <= 2", nativeQuery = true)
    List<ElrDeadLetterModel> getAllErrorDltRecordForKafkaError();

    @Query(value = """
            select * from NBS_DataIngest.dbo.elr_dlt ed
             WHERE created_on BETWEEN :startDate AND :endDate order by created_on desc;
            """,
            nativeQuery = true)
    Optional<List<ElrDeadLetterModel>> findAllDltRecordsByDate(@Param("startDate") String startDate, @Param("endDate") String endDate);

}