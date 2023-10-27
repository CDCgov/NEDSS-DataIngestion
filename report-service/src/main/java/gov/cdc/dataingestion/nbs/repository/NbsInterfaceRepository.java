package gov.cdc.dataingestion.nbs.repository;

import  gov.cdc.dataingestion.nbs.repository.model.NbsInterfaceModel;

import org.springframework.data.repository.query.Param;
import	org.springframework.stereotype.Repository;
import  org.springframework.data.jpa.repository.JpaRepository;
import 	org.springframework.data.jpa.repository.Query;

import	java.math.BigInteger;
import java.util.Optional;

@Repository
public interface NbsInterfaceRepository extends JpaRepository<NbsInterfaceModel, Integer> {
    @Query(value = "SELECT * FROM NBS_interface WHERE nbs_interface_uid = :nbsInterfaceUid AND doc_type_cd = :docTypeCd", nativeQuery = true)
    Optional<NbsInterfaceModel> getNbsInterfaceByIdAndDocType(@Param("nbsInterfaceUid") Integer id, @Param("docTypeCd") String docType);
    Optional<NbsInterfaceModel> findByNbsInterfaceUid(Integer id);
}
