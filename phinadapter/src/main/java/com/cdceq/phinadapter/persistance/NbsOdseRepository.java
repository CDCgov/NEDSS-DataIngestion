package com.cdceq.phinadapter.persistance;

import  com.cdceq.phinadapter.persistance.model.EntityNbsOdseELRWorkerQueue;

import  org.springframework.data.repository.CrudRepository;

public interface NbsOdseRepository extends CrudRepository<EntityNbsOdseELRWorkerQueue, Integer> {
    EntityNbsOdseELRWorkerQueue findByRecordId(int recordId);
}