package gov.cdc.dataprocessing.repository.nbs.odse.repos.edx;

import gov.cdc.dataprocessing.repository.nbs.odse.model.edx.EdxDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;


@Repository
public interface EdxDocumentRepository extends JpaRepository<EdxDocument, Long> {
    /*
     * String SELECT_EDX_DOCUMENT_COLLECTION = "SELECT EDX_Document_uid \"eDXDocumentUid\", act_uid \"actUid\",
     * add_time \"addTime\" FROM  EDX_Document WITH (NOLOCK) WHERE act_uid = ? order by add_time desc"
     * */
    @Query("SELECT data FROM EdxDocument data WHERE data.actUid = :uid")
    Optional<Collection<EdxDocument>> selectEdxDocumentCollectionByActUid(@Param("uid") Long uid);

}
