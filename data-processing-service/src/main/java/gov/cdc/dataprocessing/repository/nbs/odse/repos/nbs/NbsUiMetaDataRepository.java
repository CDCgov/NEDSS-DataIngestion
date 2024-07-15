package gov.cdc.dataprocessing.repository.nbs.odse.repos.nbs;

import gov.cdc.dataprocessing.constant.ComplexQueries;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsUiMetaData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface NbsUiMetaDataRepository extends JpaRepository<NbsUiMetaData, Long> {
    @Query(value = ComplexQueries.DMB_QUESTION_OID_METADATA_SQL, nativeQuery = true)
    Optional<List<NbsUiMetaData>> findDmbQuestionMetaData();


    @Query(value = ComplexQueries.PAM_QUESTION_OID_METADATA_SQL, nativeQuery = true)
    Optional<Collection<Object>> findPamQuestionMetaData();
}
