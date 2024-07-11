package gov.cdc.dataingestion.nbs.services;

import gov.cdc.dataingestion.exception.XmlConversionException;
import gov.cdc.dataingestion.hl7.helper.model.HL7ParsedMessage;
import gov.cdc.dataingestion.hl7.helper.model.hl7.message_type.OruR1;
import gov.cdc.dataingestion.nbs.repository.NbsInterfaceRepository;
import gov.cdc.dataingestion.nbs.repository.model.NbsInterfaceModel;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static gov.cdc.dataingestion.share.helper.TimeStampHelper.getCurrentTimeStamp;

@Service
@AllArgsConstructor
public class NbsRepositoryServiceProvider {
	private static Logger log = LoggerFactory.getLogger(NbsRepositoryServiceProvider.class);

	private static final String IMPEXP_CD = "I";
	private static final String STATUS_UNPROCESSED = "QUEUED";
	private static final String STATUS_UNPROCESSED_V2 = "RTI_QUEUED";

	private static final String SYSTEM_NAME_NBS = "NBS";
	private static final String DOCUMENT_TYPE_CODE = "11648804";

	private static final String ECR_DOC_TYPE = "PHC236";

    private NbsInterfaceRepository nbsInterfaceRepo;

	public void saveEcrCdaXmlMessage (String nbsInterfaceUid,
									  Integer dataMigrationStatus, String xmlMsg) {
		Optional<NbsInterfaceModel>  response = nbsInterfaceRepo.getNbsInterfaceByIdAndDocType(Integer.valueOf(nbsInterfaceUid), ECR_DOC_TYPE);
		var time = getCurrentTimeStamp();
		NbsInterfaceModel model = new NbsInterfaceModel();
		if (response.isPresent()) {
			model = response.get();
			model.setRecordStatusTime(time);
			model.setPayload(xmlMsg);
			model.setRecordStatusCd("QUEUED");
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
    
    public NbsInterfaceModel saveXmlMessage(String msgId, String xmlMsg, HL7ParsedMessage<OruR1> hl7ParsedMessage ,  boolean dataProcessingApplied) throws XmlConversionException {
		NbsInterfaceModel item = new NbsInterfaceModel();

		log.debug("{} : Xml being persisted to NBS Legacy database", msgId);

		item.setPayload(xmlMsg);
		item.setImpExpIndCd(IMPEXP_CD);
		if (dataProcessingApplied) {
			item.setRecordStatusCd(STATUS_UNPROCESSED_V2);
		} else {
			item.setRecordStatusCd(STATUS_UNPROCESSED);
		}

		var time = getCurrentTimeStamp();
		item.setRecordStatusTime(time);
		item.setAddTime(time);

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

	private NbsInterfaceModel savingNbsInterfaceModelHelper(OruR1 oru, NbsInterfaceModel nbsInterface) throws XmlConversionException {
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

		savingNbsInterfaceModelTimeStampHelper( specimenColDateStr,
				 nbsInterface);

		nbsInterface.setLabClia(labClia);
		nbsInterface.setFillerOrderNbr(filterOrderNumber);
		nbsInterface.setOrderTestCode(orderTestCode);
		return nbsInterface;
	}

	@SuppressWarnings({"java:S3776"})
	private void savingNbsInterfaceModelTimeStampHelper(String specimenColDateStr,
														NbsInterfaceModel nbsInterface) throws XmlConversionException {

		try {
			if (specimenColDateStr != null) {
				boolean noTimeStamp = false;
				String pattern = "yyyyMMddHHmm";
				//20240305155850.821+0217
				//20240305155850.8212+0217
				if (specimenColDateStr.contains(".") && (specimenColDateStr.contains("-") || specimenColDateStr.contains("+")) ) {
					int plusIndex=specimenColDateStr.indexOf("+");
					int minusIndex=specimenColDateStr.indexOf("-");

					if((plusIndex !=-1 && specimenColDateStr.substring(specimenColDateStr.indexOf(".")+1,plusIndex).trim().length()==3)
							|| (minusIndex!=-1 && specimenColDateStr.substring(specimenColDateStr.indexOf(".")+1,minusIndex).trim().length()==3)){
						pattern = "yyyyMMddHHmmss.SSSX";
					}else if((plusIndex !=-1 && specimenColDateStr.substring(specimenColDateStr.indexOf(".")+1,plusIndex).trim().length()==4)
							|| (minusIndex!=-1 && specimenColDateStr.substring(specimenColDateStr.indexOf(".")+1,minusIndex).trim().length()==4)){
						pattern = "yyyyMMddHHmmss.SSSSX";
					}
				} else if (specimenColDateStr.contains("-") || specimenColDateStr.contains("+") ) {
					pattern = "yyyyMMddHHmmssX";
				}else if (specimenColDateStr.length() == 8) {// date without time
					pattern = "yyyyMMdd";
					noTimeStamp = true;
				}else if (specimenColDateStr.length() == 10) {
					pattern = "yyyyMMddHH";
				}else if (specimenColDateStr.length() == 12) {
					pattern = "yyyyMMddHHmm";
				}else if (specimenColDateStr.length() == 14) {
					pattern = "yyyyMMddHHmmss";
				}
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
				LocalDateTime localDateTime;
				if (noTimeStamp) {
					LocalDate localDate = LocalDate.parse(specimenColDateStr, formatter);
					localDateTime = localDate.atStartOfDay();
				} else {
					localDateTime = LocalDateTime.parse(specimenColDateStr, formatter);
				}
				nbsInterface.setSpecimenCollDate(Timestamp.valueOf(localDateTime));
			} else {
				nbsInterface.setSpecimenCollDate(null);
			}
		} catch (Exception e) {
			throw new XmlConversionException(e.getMessage());
		}

	}
}