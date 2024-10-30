package gov.cdc.dataprocessing.repository.nbs.odse.repos.edx;

import gov.cdc.dataprocessing.repository.nbs.odse.model.edx.EdxDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;


@Repository
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139"})
public interface EdxDocumentRepository extends JpaRepository<EdxDocument, Long> {
    /*
    * String SELECT_EDX_DOCUMENT_COLLECTION = "SELECT EDX_Document_uid \"eDXDocumentUid\", act_uid \"actUid\",
    * add_time \"addTime\" FROM  EDX_Document WITH (NOLOCK) WHERE act_uid = ? order by add_time desc"
     * */
    @Query("SELECT data FROM EdxDocument data WHERE data.actUid = :uid")
    Optional<Collection<EdxDocument>> selectEdxDocumentCollectionByActUid(@Param("uid") Long uid);

}
