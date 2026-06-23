package gov.cdc.dataprocessing.repository.nbs.msgoute.repos;

import gov.cdc.dataprocessing.repository.nbs.msgoute.model.NbsInterfaceModel;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NbsInterfaceRepository extends JpaRepository<NbsInterfaceModel, Integer> {
  Optional<NbsInterfaceModel> findByNbsInterfaceUid(Integer id);
}
