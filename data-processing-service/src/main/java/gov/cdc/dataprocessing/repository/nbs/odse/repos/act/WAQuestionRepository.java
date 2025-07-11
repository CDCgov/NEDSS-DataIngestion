package gov.cdc.dataprocessing.repository.nbs.odse.repos.act;

import gov.cdc.dataprocessing.constant.ComplexQueries;
import gov.cdc.dataprocessing.repository.nbs.odse.model.question.WAQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository


public interface WAQuestionRepository extends JpaRepository<WAQuestion, Long> {
    @Query(value = ComplexQueries.GENERIC_QUESTION_OID_METADATA_SQL, nativeQuery = true)
    Optional<List<WAQuestion>> findGenericQuestionMetaData();
}
