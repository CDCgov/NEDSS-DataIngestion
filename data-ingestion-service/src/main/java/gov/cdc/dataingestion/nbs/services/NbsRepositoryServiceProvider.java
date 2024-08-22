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
	private static final String ECR_STATUS = "ORIG_QUEUED";

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

	public NbsInterfaceModel saveElrXmlMessage(String messageId, String xmlMsg, boolean dataProcessingApplied) {

		log.debug("Processing Elr xml: \n {} \n with an uid: {}", xmlMsg, messageId);
		NbsInterfaceModel item = new NbsInterfaceModel();

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
		item.setLabClia(null);
		item.setFillerOrderNbr(null);
		item.setOrderTestCode(null);
		item.setObservationUid(null);

		NbsInterfaceModel nbsInterfaceModel = nbsInterfaceRepo.save(item);
		log.debug("Persisted the following Elr xml to NBS_interface table: {}", xmlMsg);

		return nbsInterfaceModel;
	}

	private NbsInterfaceModel savingNbsInterfaceModelHelper(OruR1 oru, NbsInterfaceModel nbsInterface) throws XmlConversionException {
		return null;
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

	public NbsInterfaceModel saveIncomingEcrMessageWithoutRR(String payload, String systemNm, String origDocTypeEicr) {
		log.debug("Processing ecr message: \n {}", payload);
		NbsInterfaceModel ecrModel = new NbsInterfaceModel();

		ecrModel.setImpExpIndCd(IMPEXP_CD);
		ecrModel.setRecordStatusCd(ECR_STATUS);

		var time = getCurrentTimeStamp();
		ecrModel.setRecordStatusTime(time);
		ecrModel.setAddTime(time);

		ecrModel.setSystemNm(systemNm);
		ecrModel.setDocTypeCd(ECR_DOC_TYPE);
		ecrModel.setOriginalPayload(payload);
		ecrModel.setOriginalDocTypeCd(origDocTypeEicr);
		ecrModel.setSpecimenCollDate(null);
		ecrModel.setLabClia(null);
		ecrModel.setFillerOrderNbr(null);
		ecrModel.setOrderTestCode(null);
		ecrModel.setObservationUid(null);
		ecrModel.setOriginalPayloadRR(null);
		ecrModel.setOriginalDocTypeCdRR(null);

		NbsInterfaceModel nbsInterfaceModel = nbsInterfaceRepo.save(ecrModel);
		return nbsInterfaceModel;
	}

	public NbsInterfaceModel saveIncomingEcrMessageWithRR(String payload, String systemNm, String origDocTypeEicr, String incomingRR, String origDocTypeRR) {
		log.debug("Processing ecr message: \n {} \n and RR message: \n {}", payload, incomingRR);
		NbsInterfaceModel ecrModelWithRR = new NbsInterfaceModel();

		ecrModelWithRR.setImpExpIndCd(IMPEXP_CD);
		ecrModelWithRR.setRecordStatusCd(ECR_STATUS);

		var time = getCurrentTimeStamp();
		ecrModelWithRR.setRecordStatusTime(time);
		ecrModelWithRR.setAddTime(time);

		ecrModelWithRR.setSystemNm(systemNm);
		ecrModelWithRR.setDocTypeCd(ECR_DOC_TYPE);
		ecrModelWithRR.setOriginalPayload(payload);
		ecrModelWithRR.setOriginalDocTypeCd(origDocTypeEicr);
		ecrModelWithRR.setSpecimenCollDate(null);
		ecrModelWithRR.setLabClia(null);
		ecrModelWithRR.setFillerOrderNbr(null);
		ecrModelWithRR.setOrderTestCode(null);
		ecrModelWithRR.setObservationUid(null);
		ecrModelWithRR.setOriginalPayloadRR(incomingRR);
		ecrModelWithRR.setOriginalDocTypeCdRR(origDocTypeRR);

		NbsInterfaceModel nbsInterfaceModel = nbsInterfaceRepo.save(ecrModelWithRR);
		return nbsInterfaceModel;
	}
}