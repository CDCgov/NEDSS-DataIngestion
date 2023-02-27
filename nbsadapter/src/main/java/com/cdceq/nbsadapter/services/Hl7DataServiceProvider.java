package com.cdceq.nbsadapter.services;

import	com.cdceq.nbsadapter.persistance.model.EntityHl7Message;
import 	com.cdceq.nbsadapter.persistance.MongoHl7MsgsRepository;

import 	org.springframework.beans.factory.annotation.Autowired;
import 	org.springframework.stereotype.Service;

import  org.slf4j.Logger;
import  org.slf4j.LoggerFactory;

import	lombok.NoArgsConstructor;

import  java.text.SimpleDateFormat;
import  java.util.Date;
import  java.util.UUID;

@Service
@NoArgsConstructor
public class Hl7DataServiceProvider {
	private static Logger LOG = LoggerFactory.getLogger(Hl7DataServiceProvider.class);
    private static String pattern = "MM-dd-yyyy HH:mm:ss.SSS z";
    private static SimpleDateFormat sdf = new SimpleDateFormat(pattern);

    @Autowired
    private MongoHl7MsgsRepository hl7MsgsRepository;
    
    public boolean saveHl7Message(String source, String msgHl7) {
        EntityHl7Message hl7Msg = new EntityHl7Message();

        hl7Msg.setId(UUID.randomUUID().toString());
        hl7Msg.setTimestamp(sdf.format(new Date()));
        hl7Msg.setSource(source);
        hl7Msg.setData(msgHl7);

        hl7MsgsRepository.save(hl7Msg);

    	return true;
    }
}