package gov.cdc.dataprocessing.repository.nbs.odse.repos.nbs;

import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsCaseAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository


public interface NbsCaseAnswerRepository  extends JpaRepository<NbsCaseAnswer, Long> {

    @Query("SELECT data FROM NbsCaseAnswer data WHERE data.actUid = :uid")
    Optional<Collection<NbsCaseAnswer>> getNbsCaseAnswerByActUid(@Param("uid") Long uid);
}
