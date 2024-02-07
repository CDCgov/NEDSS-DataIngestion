package gov.cdc.dataprocessing.repository.nbs.msgoute;

import gov.cdc.dataprocessing.repository.nbs.msgoute.model.NbsInterfaceModel;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public interface StoredProcRepository  extends CrudRepository<NbsInterfaceModel, Integer> {
    @Procedure(name = "UpdateSpecimenCollDate_SP")
    void updateSpecimenCollDateSP(@Param("NBSInterfaceUid") Long nbsInterfaceUid, @Param("specimentCollectionDate") Timestamp specimentCollectionDate);
}
