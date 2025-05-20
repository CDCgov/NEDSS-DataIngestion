package gov.cdc.dataingestion.share.helper;

import com.google.gson.Gson;
import gov.cdc.dataingestion.hl7.helper.model.HL7ParsedMessage;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.CommonOrder;
import gov.cdc.dataingestion.hl7.helper.model.hl7.message_data_type.Eip;
import gov.cdc.dataingestion.hl7.helper.model.hl7.message_data_type.Prl;
import gov.cdc.dataingestion.hl7.helper.model.hl7.message_group.Observation;
import gov.cdc.dataingestion.hl7.helper.model.hl7.message_group.OrderObservation;
import gov.cdc.dataingestion.hl7.helper.model.hl7.message_group.PatientResult;
import gov.cdc.dataingestion.hl7.helper.model.hl7.message_type.OruR1;
import gov.cdc.dataingestion.share.repository.IObxIdStdLookupRepository;
import gov.cdc.dataingestion.share.repository.model.ObxIdStdLookup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ElrSplitter {

    private static final boolean COPY_SPM = false;

    private final IObxIdStdLookupRepository obxIdStdLookupRepository;

    public List<HL7ParsedMessage<OruR1>> splitElr(HL7ParsedMessage<OruR1> parsedMessageOrig) {
        List<HL7ParsedMessage<OruR1>> parsedMessageList;
        //identify obr or obx
        //obx
        //loop through all the OBRs and OBXx
        //check the code value in the look up table
        //if matches, create a new ELR with the OBR and OBX
        //If the OBX has Value Type of CWE and Name of Code System of SCT, change the to SNM
        boolean isSplitByOBX = false;
        List<OrderObservation> obrsByObxSplit = new ArrayList<>();
        OruR1 oruR1 = parsedMessageOrig.getParsedMessage();
        List<PatientResult> patientResultList = oruR1.getPatientResult();
        //Take OBR list from the original ELR
        List<OrderObservation> obrList = patientResultList.get(0).getOrderObservation();
        for (OrderObservation obr : obrList) {//OBR
            List<Observation> obxList = obr.getObservation();
            for (Observation obx : obxList) {
                String code = obx.getObservationResult().getObservationIdentifier().getIdentifier();
                Optional<ObxIdStdLookup> stdLookup = getValueType(code);
                log.debug("obx-3 code:" + code + " stdLookup.isPresent()" + stdLookup.isPresent());
                if (stdLookup.isPresent()) {
                    obrsByObxSplit.add(obr);
                    isSplitByOBX = true;
                    break;
                }
            }
        }
        log.debug("isSplitByOBX:" + isSplitByOBX + "obrsByObxSplit:" + obrsByObxSplit.size());
        //OBX split
        if (!obrsByObxSplit.isEmpty()) {
            parsedMessageList = splitElrByOBX(parsedMessageOrig, obrsByObxSplit);
        } else {
            parsedMessageList = splitElrByOBR(parsedMessageOrig);
        }
        return parsedMessageList;
    }

    private List<HL7ParsedMessage<OruR1>> splitElrByOBX(HL7ParsedMessage<OruR1> parsedMessageOrig, List<OrderObservation> obrListToSplit) {
        List<HL7ParsedMessage<OruR1>> parsedMessageList = new ArrayList<>();
        OruR1 oruR1 = parsedMessageOrig.getParsedMessage();
        //Get ORC content
        CommonOrder orc = oruR1.getPatientResult().getFirst().getOrderObservation().getFirst().getCommonOrder();
        Gson gson = new Gson();

        log.debug("OBR size in original Message:" + oruR1.getPatientResult().getFirst().getOrderObservation().size());

        for (OrderObservation orderObservation : obrListToSplit) {
            orderObservation.getObservationRequest().setSetIdObr("1");
            //ORC data is available only in the first OBR object and needs to be copied to the other OBRs.
            orderObservation.setCommonOrder(orc);
            //remove ParentResult,Parent from ObservationRequest
            orderObservation.getObservationRequest().setParent(new Eip());
            orderObservation.getObservationRequest().setParentResult(new Prl());
            //copy OBX list
            List<Observation> obxList = orderObservation.getObservation();
            //Empty OBX list from OBR
            orderObservation.setObservation(new ArrayList<>());
            //create new ELR for each OBX. OBR to be duplicated.
            int i = 0;
            for (Observation obx : obxList) {
                i++;
                changeSctToSnmForCodingSystem(obx);
                log.debug("from orig obx code:" + obx.getObservationResult().getObservationIdentifier().getIdentifier() + " obx id:" + obx.getObservationResult().getSetIdObx());
                obx.getObservationResult().setSetIdObx("1");
                //Create new OBR object
                OrderObservation obrCopy = gson.fromJson(gson.toJson(orderObservation), OrderObservation.class);
                //Add obx in the OBR
                obrCopy.setObservation(List.of(obx));

                //copy and create new oruR1 obj from original message
                OruR1 oruR1Copy = gson.fromJson(gson.toJson(oruR1), OruR1.class);
                //Empty the existing OBRs
                oruR1Copy.getPatientResult().getFirst().getOrderObservation().clear();
                //set OBR
                oruR1Copy.getPatientResult().getFirst().setOrderObservation(List.of(obrCopy));
                //make MessageControlIDs unique for every message created when OBRs are split out.
                // ORUR01.MSH.MessageControlID
                String msgControlId = oruR1Copy.getMessageHeader().getMessageControlId();
                //create unique MessageControlId
                String newMsgControlId = getCustomMessageControlId(msgControlId, i);
                oruR1Copy.getMessageHeader().setMessageControlId(newMsgControlId);
                //out.Messages[i].ORUR01.MSH.SendingFacility.UniversalID = (out.Messages[i].ORUR01.MSH.SendingFacility.UniversalID + (i * .01) );//TODO

                //TODO
                // To make split OBRs unique to defeat snapshot processing, make addtional changes to the specimen collection time, 03-07-13, L.Takashima.
                // First the outgoing minutes to incoming seconds field
                //out.Messages[i].ORUR01.PATIENT_RESULT[0].ORDER_OBSERVATION[0].OBR.ObservationDateTime.Time.Minutes = in.PATIENT_RESULT[0].ORDER_OBSERVATION[0].OBR.ObservationDateTime.Time.Seconds ;
                //out.Messages[i].ORUR01.PATIENT_RESULT[0].ORDER_OBSERVATION[0].SPECIMEN[0].SPM.SpecimenCollectionDateTime.RangeStartDateTime.Time.Minutes = out.Messages[i].ORUR01.PATIENT_RESULT[0].ORDER_OBSERVATION[0].SPECIMEN[0].SPM.SpecimenCollectionDateTime.RangeStartDateTime.Time.Seconds ;

                //create HL7ParsedMessage for xml creation
                HL7ParsedMessage<OruR1> parsedMessage = new HL7ParsedMessage<>();
                parsedMessage.setParsedMessage(oruR1Copy);
                parsedMessage.setType(parsedMessageOrig.getType());
                parsedMessage.setEventTrigger(parsedMessageOrig.getEventTrigger());
                parsedMessage.setOriginalVersion(parsedMessageOrig.getOriginalVersion());
                parsedMessageList.add(parsedMessage);
            }
        }
        log.debug("splitElrByOBX - parsedMessageList size:" + parsedMessageList.size());
        return parsedMessageList;
    }

    @SuppressWarnings("java:S3776")
    private List<HL7ParsedMessage<OruR1>> splitElrByOBR(HL7ParsedMessage<OruR1> parsedMessageOrig) {
        List<HL7ParsedMessage<OruR1>> parsedMessageList = new ArrayList<>();
        Gson gson = new Gson();
        OruR1 oruR1 = parsedMessageOrig.getParsedMessage();
        List<PatientResult> patientResultList = oruR1.getPatientResult();
        //Take OBR list from the original ELR
        List<OrderObservation> obrList = patientResultList.getFirst().getOrderObservation();
        if (obrList != null && obrList.size() > 1) {
            log.debug("splitElrByOBR Obr list size:" + obrList.size());
            //ORC data is available only in the first OBR object and needs to be copied to the other OBRs.
            CommonOrder orc = oruR1.getPatientResult().getFirst().getOrderObservation().getFirst().getCommonOrder();
            int i = 0;
            for (OrderObservation orderObservation : obrList) {
                i++;
                orderObservation.getObservationRequest().setSetIdObr("1");
                //ORC data is available only in the first OBR object and needs to be copied to the other OBRs.
                if (orderObservation.getCommonOrder().getOrderControl() == null) {
                    orderObservation.setCommonOrder(orc);
                }
                //remove ParentResult,Parent from ObservationRequest
                orderObservation.getObservationRequest().setParent(new Eip());
                orderObservation.getObservationRequest().setParentResult(new Prl());
                //copy SPM segment data from the last OBR if SPM is not exist in the current OBR
                //Some STLT needs Specimen data in all OBRs

                if (COPY_SPM && (orderObservation.getSpecimen() != null && orderObservation.getSpecimen().isEmpty())) {
                    orderObservation.setSpecimen(obrList.getLast().getSpecimen());
                }

                //copy and create new oruR1 obj from original message
                OruR1 oruR1Copy = gson.fromJson(gson.toJson(oruR1), OruR1.class);
                oruR1Copy.getPatientResult().get(0).setOrderObservation(List.of(orderObservation));
                //make MessageControlIDs unique for every message created when OBRs are split out.
                // ORUR01.MSH.MessageControlID
                String msgControlId = oruR1Copy.getMessageHeader().getMessageControlId();
                String newMsgControlId = getCustomMessageControlId(msgControlId, i);
                oruR1Copy.getMessageHeader().setMessageControlId(newMsgControlId);

                //create HL7ParsedMessage for xml creation
                HL7ParsedMessage<OruR1> parsedMessage = new HL7ParsedMessage<>();
                parsedMessage.setParsedMessage(oruR1Copy);
                parsedMessage.setType(parsedMessageOrig.getType());
                parsedMessage.setEventTrigger(parsedMessageOrig.getEventTrigger());
                parsedMessage.setOriginalVersion(parsedMessageOrig.getOriginalVersion());
                parsedMessageList.add(parsedMessage);
            }
        } else {
            log.debug("Single OBR exist.");
            parsedMessageList.add(parsedMessageOrig);
        }
        log.debug("Parsed messages size in ELRsplitter: {}", parsedMessageList.size());
        return parsedMessageList;
    }

    private Optional<ObxIdStdLookup> getValueType(String valueTypeId) {
        return obxIdStdLookupRepository.findByObxValueTypeId(valueTypeId);
    }

    private void changeSctToSnmForCodingSystem(Observation obx) {
        String valueType = obx.getObservationResult().getValueType();
        if (valueType != null && valueType.equals("CWE")) {
            obx.getObservationResult().getObservationValue().replaceAll(str -> str.replace("^SCT^", "^SNM^"));
        }
    }

    private static String getCustomMessageControlId(String messageControlId, int i) {
        return StringUtils.left(messageControlId, 8) + i + (i + 1) + (i + 2) + StringUtils.right(messageControlId, 9);
    }
}
