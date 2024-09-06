package gov.cdc.nbsDedup.nbs.odse.repos.matching;



import gov.cdc.nbsDedup.nbs.odse.model.matching.EdxEntityMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EdxEntityMatchRepository  extends JpaRepository<EdxEntityMatch, Long> {

}
