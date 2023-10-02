package gov.cdc.dataingestion.nbs.repository;

import  gov.cdc.dataingestion.nbs.repository.model.NbsInterfaceModel;

import	org.springframework.stereotype.Repository;
import  org.springframework.data.jpa.repository.JpaRepository;
import 	org.springframework.data.jpa.repository.Query;

import	java.math.BigInteger;
import java.util.Optional;

@Repository
public interface NbsInterfaceRepository extends JpaRepository<NbsInterfaceModel, Integer> {
	@Query(value = "select max(nbs_interface_uid) from NBS_interface", nativeQuery = true)
    BigInteger getMaxNbsInterfaceUid();

    Optional<NbsInterfaceModel> findByNbsInterfaceUid(Integer id);
}
