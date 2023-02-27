package com.cdceq.nbsadapter.services;

import	com.cdceq.nbsadapter.persistance.model.EntityNbsInterface;
import 	com.cdceq.nbsadapter.persistance.NbsInterfaceRepository;

import 	org.springframework.beans.factory.annotation.Autowired;
import 	org.springframework.stereotype.Service;

import  org.slf4j.Logger;
import  org.slf4j.LoggerFactory;

import	lombok.NoArgsConstructor;

import	java.util.GregorianCalendar;
import	java.sql.Timestamp;

@Service
@NoArgsConstructor
public class ElrDataServiceProvider {
	private static Logger LOG = LoggerFactory.getLogger(ElrDataServiceProvider.class);
	
	private static String IMPEXP_CD = "l";
	private static String STATUS_UNPROCESSED = "QUEUED";
	private static String SYSTEM_NAME_NBS = "NBS";
	private static String DOCUMENT_TYPE_CODE = "11648804";
	private static String FILLER_ORDER_NBR = "HL7EntityIdentifier";
	private static String LAB_CLIA = "HL7UniversalID";
	private static String ORDER_TEST_CODE = "HL7AlternateIdentifier";
    @Autowired
    private NbsInterfaceRepository nbsInterfaceRepo;
    
    public boolean saveXmlMessage(String msgXml) {
    	EntityNbsInterface msg = new EntityNbsInterface();

/*
imp_exp_ind_cd = 'l'
record_status_cd = 'QUEUED'
record_status_time = timestamp
add_time = timestamp
system_nm = 'NBS'
doc_type_cd = '11648804',
// data from xml
filler_order_nbr = HL7EntityIdentifier
lab_clia = HL7UniversalID
order_test_code = HL7AlternateIdentifier
*/
    	
    	msg.setPayload(msgXml);
    	msg.setImpExpIndCd(IMPEXP_CD);
    	msg.setRecordStatusCd(STATUS_UNPROCESSED);
    	
    	GregorianCalendar currentTimestamp = new GregorianCalendar();
    	Timestamp recordTimestamp = new Timestamp(currentTimestamp.getTimeInMillis());

    	msg.setRecordStatusTime(recordTimestamp);
    	msg.setAddTime(recordTimestamp);
    	
    	msg.setSystemNm(SYSTEM_NAME_NBS);
    	msg.setDocTypeCd(DOCUMENT_TYPE_CODE);
    	msg.setOriginalPayload(null);
    	msg.setOriginalDocTypeCd(null);
    	msg.setFillerOrderNbr(FILLER_ORDER_NBR);
    	msg.setLabClia(LAB_CLIA);
    	msg.setSpecimenCollDate(null);
    	msg.setOrderTestCode(ORDER_TEST_CODE);
    	msg.setObservationUid(null);
    	
    	nbsInterfaceRepo.save(msg);

    	return true;
    }
    
    public void findAll() {
        Iterable<EntityNbsInterface> itemsIterator = nbsInterfaceRepo.findAll();
        
        for(EntityNbsInterface item : itemsIterator) {
        	LOG.info(item.toString());
        }
    }
}