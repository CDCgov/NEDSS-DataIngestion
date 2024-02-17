package gov.cdc.dataprocessing.repository.nbs.odse;

import gov.cdc.dataprocessing.repository.nbs.odse.model.LocalUidGenerator;
import gov.cdc.dataprocessing.repository.nbs.odse.model.PersonEthnicGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocalUidGeneratorRepository extends JpaRepository<LocalUidGenerator, String> {

}
