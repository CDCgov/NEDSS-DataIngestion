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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ElrSplitter {

    private final boolean COPY_SPM=false;

    private final IObxIdStdLookupRepository obxIdStdLookupRepository;

    public List<HL7ParsedMessage<OruR1>> splitElr(HL7ParsedMessage<OruR1> parsedMessageOrig) {
        List<HL7ParsedMessage<OruR1>> parsedMessageList;
        //identify obr or obx
        //obx
        //loop through all the OBRs and OBXx
        //check the code value in the look up table
        //if matches, create a new ELR with the OBR and OBX
        //If the OBX has Value Type of CWE and Name of Code System of SCT, change the to SNM
        String codeLookup = "82306-2,80364-3,6920-3,6919-5,6349-5,57288-3,57287-5,53926-2,53925-4,50411-8,50387-0,45001-5,43304-5,42931-6,38347-1,36902-5,35729-3,35713-7,34708-8,32774-2,21613-5,21190-4,11475-1,10352-3,11475-1,24111-7,32367-5,38347-1,43305-2,47387-6,5028-6,50388-8,53879-3,53927-0,57458-2,60255-7,60256-5,611-4,626-2,630-4,634-6,6462-6,6463-4,698-1,80366-8,aerBact ID,aerBact ID_R,GC Iso,Gonorrhea,Chlamydia,chlamiso,CGPCR,77577-5";
        List<String> codeList = Arrays.asList(codeLookup.split(","));
        System.out.println(codeList);
        System.out.println(codeList.size());
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
                System.out.println("obx code:" + code);
                //Optional<ObxIdStdLookup> stdLookup = getValueType(code);
                //System.out.println("stdLookup.isPresent()" + stdLookup.isPresent());
                if (codeList.contains(code)) {
                    System.out.println("code value found in the lookup:" + code);
                    obrsByObxSplit.add(obr);
                    isSplitByOBX = true;
                    break;
                }
            }
        }
        System.out.println("isSplitByOBX:" + isSplitByOBX);
        System.out.println("obrsToBeSplited size:" + obrsByObxSplit.size());
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
        //OruR1 oruR1Copy = gson.fromJson(gson.toJson(oruR1), OruR1.class);
        //Empty the OBRs in the OruR1 copy object
        //oruR1Copy.getPatientResult().get(0).getOrderObservation().clear();
        System.out.println("obr size in original ORU:" + oruR1.getPatientResult().getFirst().getOrderObservation().size());

        for (OrderObservation orderObservation : obrListToSplit) {
            orderObservation.getObservationRequest().setSetIdObr("1");
            //ORC data is available only in the first OBR object and needs to be copied to the other OBRs.
            orderObservation.setCommonOrder(orc);
            //remove ParentResult,Parent from ObservationRequest
            orderObservation.getObservationRequest().setParent(new Eip());
            orderObservation.getObservationRequest().setParentResult(new Prl());
            //copy OBX list
            List<Observation> obxList = orderObservation.getObservation();
            System.out.println("obxList before copy and clear:" + obxList.size());

            orderObservation.setObservation(new ArrayList<>());
            System.out.println("copied obxList after clear:" + obxList.size());
            System.out.println("obxList from orig obr after reset:" + orderObservation.getObservation().size());

            //create new ELR for each OBX. OBR to be duplicated.
            int i = 0;
            for (Observation obx : obxList) {
                i++;
                changeSctToSnmForCodingSystem(obx);
                System.out.println("from orig obx code:" + obx.getObservationResult().getObservationIdentifier().getIdentifier() + " obx id:" + obx.getObservationResult().getSetIdObx());
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
                System.out.println("Old Message control id:" + msgControlId);
                //create unique MessageControlId
                String newMsgControlId = getCustomMessageControlId(msgControlId, i);//StringUtils.left(msgControlId,8)+i+(i+1)+(i+2)+StringUtils.right(msgControlId,9);
                System.out.println("New Message control id:" + newMsgControlId);
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
        System.out.println("in OBX.. parsedMessageList size:" + parsedMessageList.size());
        return parsedMessageList;
    }

    private List<HL7ParsedMessage<OruR1>> splitElrByOBR(HL7ParsedMessage<OruR1> parsedMessageOrig) {
        List<HL7ParsedMessage<OruR1>> parsedMessageList = new ArrayList<>();
        Gson gson = new Gson();
        OruR1 oruR1 = parsedMessageOrig.getParsedMessage();
        List<PatientResult> patientResultList = oruR1.getPatientResult();
        //Take OBR list from the original ELR
        List<OrderObservation> obrList = patientResultList.getFirst().getOrderObservation();
        System.out.println("splitElrByOBR Obr list size:" + obrList.size());
        if (obrList != null && obrList.size() > 1) {
            log.debug("Multiple OBRs exist.");
            //ORC data is available only in the first OBR object and needs to be copied to the other OBRs.
            CommonOrder orc = oruR1.getPatientResult().getFirst().getOrderObservation().getFirst().getCommonOrder();
            System.out.println("SPM first:"+obrList.getFirst().getSpecimen()+" last:"+obrList.getLast().getSpecimen());
            int i = 0;
            for (OrderObservation orderObservation : obrList) {
                i++;
                orderObservation.getObservationRequest().setSetIdObr("1");
                //ORC data is available only in the first OBR object and needs to be copied to the other OBRs.
                System.out.println("ORC OrderControl:" + orderObservation.getCommonOrder().getOrderControl());
                if (orderObservation.getCommonOrder().getOrderControl() == null) {
                    orderObservation.setCommonOrder(orc);
                }
                //remove ParentResult,Parent from ObservationRequest
                orderObservation.getObservationRequest().setParent(new Eip());
                orderObservation.getObservationRequest().setParentResult(new Prl());
                //copy SPM segment data from the last OBR if SPM is not exist in the current OBR
                if(COPY_SPM){
                    System.out.println("obr "+i+" before update:"+orderObservation.getSpecimen().size());
                    if(orderObservation.getSpecimen()!=null && orderObservation.getSpecimen().isEmpty()){
                        orderObservation.setSpecimen(obrList.getLast().getSpecimen());
                        System.out.println("obr "+i+" after update:"+orderObservation.getSpecimen());
                    }
                }

                //copy and create new oruR1 obj from original message
                OruR1 oruR1Copy = gson.fromJson(gson.toJson(oruR1), OruR1.class);
                oruR1Copy.getPatientResult().get(0).setOrderObservation(List.of(orderObservation));
                //make MessageControlIDs unique for every message created when OBRs are split out.
                // ORUR01.MSH.MessageControlID
                String msgControlId = oruR1Copy.getMessageHeader().getMessageControlId();
                System.out.println("Old Message control id:" + msgControlId);
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
        log.debug("phdc xml list: {}", parsedMessageList.size());
        System.out.println("Parsed messages size in ELRsplitter:" + parsedMessageList.size());
        return parsedMessageList;
    }

    public Optional<ObxIdStdLookup> getValueType(String valueTypeId) {
        return obxIdStdLookupRepository.findByObxValueTypeId(valueTypeId);
    }

    private void changeSctToSnmForCodingSystem(Observation obx) {
        String valueType = obx.getObservationResult().getValueType();
        if (valueType != null && valueType.equals("CWE")) {
            obx.getObservationResult().getObservationValue().replaceAll(str -> str.replace("^SCT^", "^SNM^"));
        }
    }
    private static String getCustomMessageControlId(String messageControlId, int i) {
        System.out.println("getCustomMessageControlId i:"+i);
        return StringUtils.left(messageControlId, 8) + i + (i + 1) + (i + 2) + StringUtils.right(messageControlId, 9);
    }
}
