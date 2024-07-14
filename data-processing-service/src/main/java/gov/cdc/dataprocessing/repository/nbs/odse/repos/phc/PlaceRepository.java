package gov.cdc.dataprocessing.repository.nbs.odse.repos.phc;

import gov.cdc.dataprocessing.repository.nbs.odse.model.phc.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
}
