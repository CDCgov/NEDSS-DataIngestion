package gov.cdc.dataprocessing.repository.nbs.odse.repos;

import gov.cdc.dataprocessing.constant.ComplexQueries;
import gov.cdc.dataprocessing.model.dto.NbsQuestionMetadata;
import gov.cdc.dataprocessing.repository.nbs.odse.model.NbsUiMetaData;
import gov.cdc.dataprocessing.repository.nbs.odse.model.WAQuestion;
import gov.cdc.dataprocessing.repository.nbs.odse.model.lookup.LookupQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface NbsUiMetaDataRepository  extends JpaRepository<NbsUiMetaData, Long> {
    @Query(value = ComplexQueries.DMB_QUESTION_OID_METADATA_SQL, nativeQuery = true)
    Optional<List<NbsUiMetaData>> findDmbQuestionMetaData();


    @Query(value = ComplexQueries.PAM_QUESTION_OID_METADATA_SQL, nativeQuery = true)
    Optional<Collection<Object>> findPamQuestionMetaData();
}
