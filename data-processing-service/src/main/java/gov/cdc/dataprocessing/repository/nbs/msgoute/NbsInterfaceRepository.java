package gov.cdc.dataprocessing.repository.nbs.msgoute;

import gov.cdc.dataprocessing.repository.nbs.msgoute.model.NbsInterfaceModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NbsInterfaceRepository extends JpaRepository<NbsInterfaceModel, Integer> {
    Optional<NbsInterfaceModel> findByNbsInterfaceUid(Integer id);
}
