package gov.cdc.dataingestion.nbs.services;

import gov.cdc.dataingestion.hl7.helper.model.HL7ParsedMessage;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageType.OruR1;
import gov.cdc.dataingestion.nbs.repository.NbsInterfaceRepository;
import gov.cdc.dataingestion.nbs.repository.model.NbsInterfaceModel;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Optional;
import java.util.TimeZone;

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

	private static final String ECR_DOC_TYPE = "PHC236";

    @Autowired
    private NbsInterfaceRepository nbsInterfaceRepo;

	public void saveEcrCdaXmlMessage (String nbsInterfaceUid,
									  Integer dataMigrationStatus, String xmlMsg) {
		Optional<NbsInterfaceModel>  response = nbsInterfaceRepo.getNbsInterfaceByIdAndDocType(Integer.valueOf(nbsInterfaceUid), ECR_DOC_TYPE);
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
				model.setDocTypeCd(ECR_DOC_TYPE);
				nbsInterfaceRepo.save(model);
			}
			else if (dataMigrationStatus == -2) {
				model.setPayload(xmlMsg);
				model.setImpExpIndCd("I");
				model.setRecordStatusCd(STATUS_UNPROCESSED);
				model.setRecordStatusTime(time);
				model.setAddTime(time);
				model.setSystemNm(SYSTEM_NAME_NBS);
				model.setDocTypeCd(ECR_DOC_TYPE);
				nbsInterfaceRepo.save(model);
			}
		}
	}
    
    public NbsInterfaceModel saveXmlMessage(String msgId, String xmlMsg, HL7ParsedMessage<OruR1> hl7ParsedMessage) {
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


		item.setSpecimenCollDate(null);
		OruR1 oru = hl7ParsedMessage.getParsedMessage();
		if (oru != null) {
			item = savingNbsInterfaceModelHelper(oru, item);
		}
		else {
			item.setLabClia(null);
			item.setFillerOrderNbr(null);
			item.setOrderTestCode(null);
		}
		item.setObservationUid(null);

		NbsInterfaceModel nbsInterfaceModel = nbsInterfaceRepo.save(item);
		log.debug("{} : Persisted xml to nbs database", msgId);

    	return nbsInterfaceModel;
    }

	private NbsInterfaceModel savingNbsInterfaceModelHelper(OruR1 oru, NbsInterfaceModel nbsInterface) {
		String labClia = (oru.getMessageHeader() != null && oru.getMessageHeader().getSendingFacility() != null)
				? oru.getMessageHeader().getSendingFacility().getUniversalId() : null;

		String filterOrderNumber = (oru.getPatientResult() != null && !oru.getPatientResult().isEmpty()
				&& oru.getPatientResult().get(0).getOrderObservation() != null
				&& !oru.getPatientResult().get(0).getOrderObservation().isEmpty()
				&& oru.getPatientResult().get(0).getOrderObservation().get(0).getObservationRequest() != null
				&& oru.getPatientResult().get(0).getOrderObservation().get(0).getObservationRequest().getFillerOrderNumber() != null)
				? oru.getPatientResult().get(0).getOrderObservation().get(0).getObservationRequest().getFillerOrderNumber().getEntityIdentifier() : null;


		var orderTestCodeObj = (oru.getPatientResult() != null && !oru.getPatientResult().isEmpty()
				&& oru.getPatientResult().get(0).getOrderObservation() != null
				&& !oru.getPatientResult().get(0).getOrderObservation().isEmpty()
				&& oru.getPatientResult().get(0).getOrderObservation().get(0).getObservationRequest() != null)
				? oru.getPatientResult().get(0).getOrderObservation().get(0).getObservationRequest() : null;

		String orderTestCode = null;
		if (orderTestCodeObj != null && orderTestCodeObj.getUniversalServiceIdentifier() != null ) {
			if (orderTestCodeObj.getUniversalServiceIdentifier().getIdentifier() != null) {
				orderTestCode = orderTestCodeObj.getUniversalServiceIdentifier().getIdentifier();
			}
			else if (orderTestCodeObj.getUniversalServiceIdentifier().getAlternateIdentifier() != null){
				orderTestCode = orderTestCodeObj.getUniversalServiceIdentifier().getAlternateIdentifier();
			}
		}

			String specimenColDateStr = (oru.getPatientResult() != null && !oru.getPatientResult().isEmpty()
				&& oru.getPatientResult().get(0).getOrderObservation() != null
				&& !oru.getPatientResult().get(0).getOrderObservation().isEmpty()
				&& oru.getPatientResult().get(0).getOrderObservation().get(0).getSpecimen() != null
				&& !oru.getPatientResult().get(0).getOrderObservation().get(0).getSpecimen().isEmpty()
				&& oru.getPatientResult().get(0).getOrderObservation().get(0).getSpecimen().get(0).getSpecimen().getSpecimenCollectionDateTime() != null
				&& oru.getPatientResult().get(0).getOrderObservation().get(0).getSpecimen().get(0).getSpecimen().getSpecimenCollectionDateTime().getRangeStartDateTime() != null)
				? oru.getPatientResult().get(0).getOrderObservation().get(0).getSpecimen().get(0).getSpecimen().getSpecimenCollectionDateTime().getRangeStartDateTime().getTime() : null;

		if (specimenColDateStr != null) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
			LocalDateTime localDateTime = LocalDateTime.parse(specimenColDateStr, formatter);
			nbsInterface.setSpecimenCollDate(Timestamp.valueOf(localDateTime));
		} else {
			nbsInterface.setSpecimenCollDate(null);
		}

		nbsInterface.setLabClia(labClia);
		nbsInterface.setFillerOrderNbr(filterOrderNumber);
		nbsInterface.setOrderTestCode(orderTestCode);
		return nbsInterface;
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