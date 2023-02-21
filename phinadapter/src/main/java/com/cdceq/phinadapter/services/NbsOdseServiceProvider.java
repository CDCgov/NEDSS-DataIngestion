package com.cdceq.phinadapter.services;

import	com.cdceq.phinadapter.services.model.ElrWorkerThreadUpdateRequestHolder;
import	com.cdceq.phinadapter.persistance.model.EntityNbsOdseELRWorkerQueue;
import 	com.cdceq.phinadapter.persistance.NbsOdseRepository;

import com.vault.utils.SecretsReplyHolder;
import 	org.springframework.beans.factory.annotation.Autowired;
import 	org.springframework.stereotype.Service;

import  org.slf4j.Logger;
import  org.slf4j.LoggerFactory;

import	lombok.NoArgsConstructor;

import  com.google.gson.Gson;

@Service
@NoArgsConstructor
public class NbsOdseServiceProvider {
	private static Logger logger = LoggerFactory.getLogger(NbsOdseServiceProvider.class);
	private static Gson gson = new Gson();

    @Autowired
    private NbsOdseRepository nbsOdseRepo;
    
    public int processMessage(String payload, StringBuffer sb) {
		int recordId = -1;
		ElrWorkerThreadUpdateRequestHolder request = null;


		try {
			request = gson.fromJson(payload, ElrWorkerThreadUpdateRequestHolder.class);
		}
		catch(Exception e) {
			sb.append("Failed to parse payload, please check body");
			return recordId;
		}

		String strRecordId = request.recordId;
		logger.info("strRecordId = {}", strRecordId);

		if((null != strRecordId) && (strRecordId.length() > 0)) {
			recordId = Integer.parseInt(strRecordId);
		}

		logger.info("recordId = {}", recordId);

		if(recordId > 0) {
			EntityNbsOdseELRWorkerQueue item = nbsOdseRepo.findByRecordId(recordId);
			item.setProcessingStatus("queued");
			nbsOdseRepo.save(item);
		}

    	return recordId;
    }
}