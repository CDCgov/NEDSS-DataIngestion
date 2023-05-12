package gov.cdc.dataingestion.nbs.services;

import	gov.cdc.dataingestion.nbs.repository.model.NbsInterfaceModel;
import 	gov.cdc.dataingestion.nbs.repository.NbsInterfaceRepository;

import 	org.springframework.beans.factory.annotation.Autowired;
import 	org.springframework.stereotype.Service;

import  org.slf4j.Logger;
import  org.slf4j.LoggerFactory;

import	lombok.NoArgsConstructor;

import	java.util.GregorianCalendar;
import	java.sql.Timestamp;

@Service
@NoArgsConstructor
public class NbsRepositoryServiceProvider {
	private static final Logger log = LoggerFactory.getLogger(NbsRepositoryServiceProvider.class);
	
	private static final String IMPEXP_CD = "l";
	private static final String STATUS_UNPROCESSED = "QUEUED";
	private static final String SYSTEM_NAME_NBS = "NBS";
	private static final String DOCUMENT_TYPE_CODE = "11648804";
	private static final String FILLER_ORDER_NBR = "HL7EntityIdentifier";
	private static final String LAB_CLIA = "HL7UniversalID";
	private static final String ORDER_TEST_CODE = "HL7AlternateIdentifier";
    @Autowired
    private NbsInterfaceRepository nbsInterfaceRepo;
    
    public boolean saveXmlMessage(String msgXml) {
		NbsInterfaceModel item = new NbsInterfaceModel();

		item.setPayload(msgXml);
		item.setImpExpIndCd(IMPEXP_CD);
		item.setRecordStatusCd(STATUS_UNPROCESSED);
    	
    	GregorianCalendar currentTimestamp = new GregorianCalendar();
    	Timestamp recordTimestamp = new Timestamp(currentTimestamp.getTimeInMillis());

		item.setRecordStatusTime(recordTimestamp);
		item.setAddTime(recordTimestamp);

		item.setSystemNm(SYSTEM_NAME_NBS);
		item.setDocTypeCd(DOCUMENT_TYPE_CODE);
		item.setOriginalPayload(null);
		item.setOriginalDocTypeCd(null);
		item.setFillerOrderNbr(FILLER_ORDER_NBR);
		item.setLabClia(LAB_CLIA);
		item.setSpecimenCollDate(null);
		item.setOrderTestCode(ORDER_TEST_CODE);
		item.setObservationUid(null);
    	
    	nbsInterfaceRepo.save(item);

		log.info("Persisted xml to nbs database");

    	return true;
    }
}