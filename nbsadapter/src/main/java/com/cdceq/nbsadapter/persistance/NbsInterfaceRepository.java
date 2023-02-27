package com.cdceq.nbsadapter.persistance;

import  com.cdceq.nbsadapter.persistance.model.EntityNbsInterface;

import  org.springframework.data.repository.CrudRepository;
import 	org.springframework.data.jpa.repository.Query;

import	java.math.BigInteger;

public interface NbsInterfaceRepository extends CrudRepository<EntityNbsInterface, Integer> {
	@Query(value = "select max(nbs_interface_uid) from NBS_interface", nativeQuery = true)
	public BigInteger getMaxNbsInterfaceUid();
}