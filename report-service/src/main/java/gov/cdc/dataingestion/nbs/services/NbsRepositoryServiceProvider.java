package gov.cdc.dataingestion.nbs.services;

import	gov.cdc.dataingestion.nbs.repository.model.NbsInterfaceModel;
import 	gov.cdc.dataingestion.nbs.repository.NbsInterfaceRepository;

import gov.cdc.dataingestion.validation.repository.model.ValidatedELRModel;
import lombok.AllArgsConstructor;
import 	org.springframework.beans.factory.annotation.Autowired;
import 	org.springframework.stereotype.Service;

import  org.slf4j.Logger;
import  org.slf4j.LoggerFactory;

import	lombok.NoArgsConstructor;

import java.time.Instant;
import  java.time.ZonedDateTime;
import  java.time.ZoneOffset;
import	java.util.Calendar;
import java.util.Optional;
import	java.util.TimeZone;
import	java.sql.Timestamp;

@Service
@AllArgsConstructor
public class NbsRepositoryServiceProvider {
	private static Logger log = LoggerFactory.getLogger(NbsRepositoryServiceProvider.class);

	private static String IMPEXP_CD = "I";
	private static String STATUS_UNPROCESSED = "QUEUED";
	private static String SYSTEM_NAME_NBS = "NBS";
	private static String DOCUMENT_TYPE_CODE = "11648804";
	private static String FILLER_ORDER_NBR = "HL7EntityIdentifier";
	private static String LAB_CLIA = "HL7UniversalID";
	private static String ORDER_TEST_CODE = "HL7AlternateIdentifier";

    @Autowired
    private NbsInterfaceRepository nbsInterfaceRepo;

	public void saveEcrCdaXmlMessage (String nbsInterfaceUid,
									  Integer dataMigrationStatus, String xmlMsg) {
		Optional<NbsInterfaceModel>  response = nbsInterfaceRepo.getNbsInterfaceByIdAndDocType(Integer.valueOf(nbsInterfaceUid), "PHC236");
		var time = Timestamp.from(Instant.now());
		NbsInterfaceModel model = new NbsInterfaceModel();
		if (response.isPresent()) {
			model = response.get();
			model.setRecordStatusTime(time);
			model.setPayload(xmlMsg);
			nbsInterfaceRepo.save(model);
		} else {
			if (dataMigrationStatus == -1) {
				model.setPayload(xmlMsg);
				model.setImpExpIndCd("E");
				model.setRecordStatusCd(STATUS_UNPROCESSED);
				model.setRecordStatusTime(time);
				model.setAddTime(time);
				model.setSystemNm(SYSTEM_NAME_NBS);
				model.setDocTypeCd("PHC236");
				nbsInterfaceRepo.save(model);
			}
			else if (dataMigrationStatus == -2) {
				model.setPayload(xmlMsg);
				model.setImpExpIndCd("I");
				model.setRecordStatusCd(STATUS_UNPROCESSED);
				model.setRecordStatusTime(time);
				model.setAddTime(time);
				model.setSystemNm(SYSTEM_NAME_NBS);
				model.setDocTypeCd("PHC236");
				nbsInterfaceRepo.save(model);
			}
		}
	}
    
    public boolean saveXmlMessage(String msgId, String xmlMsg) {
		NbsInterfaceModel item = new NbsInterfaceModel();

		log.debug("{} : Xml being persisted to NBS Legacy database", msgId);

		item.setPayload(xmlMsg);
		item.setImpExpIndCd(IMPEXP_CD);
		item.setRecordStatusCd(STATUS_UNPROCESSED);

		Timestamp recordTimestamp = new Timestamp(getGmtTimestamp());

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
		log.debug("{} : Persisted xml to nbs database", msgId);

    	return true;
    }

	private long getGmtTimestamp() {
		ZonedDateTime currentDate = ZonedDateTime.now( ZoneOffset.UTC );
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.set(Calendar.HOUR, currentDate.getHour());
		cal.set(Calendar.MINUTE, currentDate.getMinute());
		cal.set(Calendar.SECOND, currentDate.getSecond());
		return cal.getTimeInMillis();
	}
}