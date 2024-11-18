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
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static gov.cdc.dataingestion.share.helper.TimeStampHelper.getCurrentTimeStamp;

@Service
@AllArgsConstructor
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
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
		Optional<NbsInterfaceModel> response = nbsInterfaceRepo.getNbsInterfaceByIdAndDocType(Integer.valueOf(nbsInterfaceUid), ECR_DOC_TYPE);
		var time = getCurrentTimeStamp();
		NbsInterfaceModel model = new NbsInterfaceModel();
		if (response.isPresent()) {
			model = response.get();
			model.setRecordStatusCd(STATUS_UNPROCESSED);
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

	public NbsInterfaceModel saveElrXmlMessage(String messageId, String xmlMsg, boolean dataProcessingApplied) throws XmlConversionException {

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
		item = savingElrXmlNbsInterfaceModelHelper(xmlMsg, item);
		
		item.setObservationUid(null);

		NbsInterfaceModel nbsInterfaceModel = nbsInterfaceRepo.save(item);
		log.debug("Persisted the following Elr xml to NBS_interface table: {}", xmlMsg);

		return nbsInterfaceModel;
	}

	public NbsInterfaceModel savingElrXmlNbsInterfaceModelHelper(String xmlMsg, NbsInterfaceModel item) throws XmlConversionException {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new InputSource(new StringReader(xmlMsg)));

			// OWASP recommended XXE prevention measures
			dbFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			dbFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
			dbFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

			// Additional OWASP recommendations for secure XML processing
			dbFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			dbFactory.setXIncludeAware(false);
			dbFactory.setExpandEntityReferences(false);


			String labClia = getNodeValue(doc, "/Container/HL7LabReport/HL7MSH/SendingFacility/HL7UniversalID");

			String fillerOrderNumber = getNodeValue(doc, "/Container/HL7LabReport/HL7PATIENT_RESULT/ORDER_OBSERVATION/ObservationRequest/FillerOrderNumber/HL7EntityIdentifier");

			String orderTestCode = getNodeValue(doc, "/Container/HL7LabReport/HL7PATIENT_RESULT/ORDER_OBSERVATION/ObservationRequest/UniversalServiceIdentifier/HL7Identifier");
			if (orderTestCode == null) {
				orderTestCode = getNodeValue(doc, "/Container/HL7LabReport/HL7PATIENT_RESULT/ORDER_OBSERVATION/ObservationRequest/UniversalServiceIdentifier/HL7AlternateIdentifier");
			}

			String specimenColDateStr = getSpecimenCollectionDateStr(doc);
			savingNbsInterfaceModelTimeStampHelper(specimenColDateStr, item);

			item.setLabClia(labClia);
			item.setFillerOrderNbr(fillerOrderNumber);
			item.setOrderTestCode(orderTestCode);
		} catch (Exception e) {
			throw new XmlConversionException(e.getMessage());
		}
		return item;
	}

	public String getSpecimenCollectionDateStr(Document doc) {
		String year = getNodeValue(doc, "/Container/HL7LabReport/HL7PATIENT_RESULT/ORDER_OBSERVATION/PatientResultOrderSPMObservation/SPECIMEN/SPECIMEN/SpecimenCollectionDateTime/HL7RangeStartDateTime/year");
		String month = getPaddedNodeValue(doc, "/Container/HL7LabReport/HL7PATIENT_RESULT/ORDER_OBSERVATION/PatientResultOrderSPMObservation/SPECIMEN/SPECIMEN/SpecimenCollectionDateTime/HL7RangeStartDateTime/month");
		String day = getPaddedNodeValue(doc, "/Container/HL7LabReport/HL7PATIENT_RESULT/ORDER_OBSERVATION/PatientResultOrderSPMObservation/SPECIMEN/SPECIMEN/SpecimenCollectionDateTime/HL7RangeStartDateTime/day");
		String hours = getPaddedNodeValue(doc, "/Container/HL7LabReport/HL7PATIENT_RESULT/ORDER_OBSERVATION/PatientResultOrderSPMObservation/SPECIMEN/SPECIMEN/SpecimenCollectionDateTime/HL7RangeStartDateTime/hours");
		String minutes = getPaddedNodeValue(doc, "/Container/HL7LabReport/HL7PATIENT_RESULT/ORDER_OBSERVATION/PatientResultOrderSPMObservation/SPECIMEN/SPECIMEN/SpecimenCollectionDateTime/HL7RangeStartDateTime/minutes");

		if (year == null || month == null || day == null || hours == null || minutes == null) {
			return null;
		}
		return year + month + day + hours + minutes;
	}

	public String getPaddedNodeValue(Document doc, String xpathExpr) {
		String value = getNodeValue(doc, xpathExpr);
		if (value != null && !value.isEmpty()) {
			return value.length() == 1 ? "0" + value : value;
		}
		return null;
	}

	public String getNodeValue(Document doc, String path) {
		Node node = getNode(doc, path);
		return (node != null) ? node.getTextContent() : null;
	}

	private Node getNode(Document doc, String xpathExpression) {
		try {
			XPath xPath = XPathFactory.newInstance().newXPath();
			return (Node) xPath.evaluate(xpathExpression, doc, XPathConstants.NODE);
		} catch (Exception e) {
			return null;
		}
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

		return nbsInterfaceRepo.save(ecrModel);
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

		return nbsInterfaceRepo.save(ecrModelWithRR);
	}
}