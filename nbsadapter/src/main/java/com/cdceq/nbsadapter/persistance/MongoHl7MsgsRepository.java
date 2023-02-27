package com.cdceq.nbsadapter.persistance;

import  org.springframework.data.mongodb.repository.MongoRepository;
import  com.cdceq.nbsadapter.persistance.model.EntityHl7Message;

public interface MongoHl7MsgsRepository extends MongoRepository<EntityHl7Message, String> {
    // 
}
