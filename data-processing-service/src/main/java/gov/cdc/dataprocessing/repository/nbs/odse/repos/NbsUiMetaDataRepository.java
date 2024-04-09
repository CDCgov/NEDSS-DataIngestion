package gov.cdc.dataprocessing.repository.nbs.odse.repos;

import gov.cdc.dataprocessing.constant.ComplexQueries;
import gov.cdc.dataprocessing.repository.nbs.odse.model.lookup.LookupQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Collection;
import java.util.Optional;

@Repository
public interface NbsUiMetaDataRepository  extends JpaRepository<Object, Long> {
    @Query(value = ComplexQueries.DMB_QUESTION_OID_METADATA_SQL, nativeQuery = true)
    Optional<Collection<Object>> findDmbQuestionMetaData();
    @Query(value = ComplexQueries.GENERIC_QUESTION_OID_METADATA_SQL, nativeQuery = true)
    Optional<Collection<Object>> findGenericQuestionMetaData();

    @Query(value = ComplexQueries.PAM_QUESTION_OID_METADATA_SQL, nativeQuery = true)
    Optional<Collection<Object>> findPamQuestionMetaData();
}
