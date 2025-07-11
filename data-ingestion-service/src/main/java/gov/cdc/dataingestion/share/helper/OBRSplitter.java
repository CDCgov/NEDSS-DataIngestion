package gov.cdc.dataingestion.share.helper;

import com.google.gson.Gson;
import gov.cdc.dataingestion.hl7.helper.model.HL7ParsedMessage;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.CommonOrder;
import gov.cdc.dataingestion.hl7.helper.model.hl7.message_data_type.Eip;
import gov.cdc.dataingestion.hl7.helper.model.hl7.message_data_type.Prl;
import gov.cdc.dataingestion.hl7.helper.model.hl7.message_group.Observation;
import gov.cdc.dataingestion.hl7.helper.model.hl7.message_group.OrderObservation;
import gov.cdc.dataingestion.hl7.helper.model.hl7.message_group.PatientResult;
import gov.cdc.dataingestion.hl7.helper.model.hl7.message_group.Specimen;
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
public class OBRSplitter {

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
                log.info("obx-3 code:" + code + " in Lookup isPresent" + stdLookup.isPresent());
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
        log.info("After the OBR split..ELRs size:" + parsedMessageList.size());
        return parsedMessageList;
    }

    private List<HL7ParsedMessage<OruR1>> splitElrByOBX(HL7ParsedMessage<OruR1> parsedMessageOrig, List<OrderObservation> obrListToSplit) {
        List<HL7ParsedMessage<OruR1>> parsedMessageList = new ArrayList<>();
        OruR1 oruR1 = parsedMessageOrig.getParsedMessage();
        //Get ORC content
        //ORC data is available only in the first OBR object and needs to be copied to the other OBRs.
        CommonOrder orc = oruR1.getPatientResult().getFirst().getOrderObservation().getFirst().getCommonOrder();
        Gson gson = new Gson();

        log.info("OBR size in original Message:" + oruR1.getPatientResult().getFirst().getOrderObservation().size());

        for (OrderObservation orderObservation:obrListToSplit) {
            orderObservation.setCommonOrder(orc);
            //remove ParentResult,Parent from ObservationRequest
            orderObservation.getObservationRequest().setParent(new Eip());
            orderObservation.getObservationRequest().setParentResult(new Prl());
            //copy OBX list
            List<Observation> obxList = orderObservation.getObservation();
            //Empty OBX list from OBR
            orderObservation.setObservation(new ArrayList<>());
            //create new ELR for each OBX. Duplicate the OBR.

            for (int j=0;j<obxList.size();j++) {
                Observation obx =obxList.get(j);
                changeSctToSnmForCodingSystem(obx);
                obx.getObservationResult().setSetIdObx("1");

                //Clone the OBR object
                OrderObservation obrCopy = gson.fromJson(gson.toJson(orderObservation), OrderObservation.class);
                obrCopy.getObservationRequest().setSetIdObr("1");

                // To make split OBRs unique to defeat snapshot processing, make addtional changes to the specimen collection time, //NOSONAR
                // First the outgoing minutes to incoming seconds field //NOSONAR
                //out.Messages[i].ORUR01.PATIENT_RESULT[0].ORDER_OBSERVATION[0].OBR.ObservationDateTime.Time.Minutes = in.PATIENT_RESULT[0].ORDER_OBSERVATION[0].OBR.ObservationDateTime.Time.Seconds ;//NOSONAR
                //out.Messages[i].ORUR01.PATIENT_RESULT[0].ORDER_OBSERVATION[0].SPECIMEN[0].SPM.SpecimenCollectionDateTime.RangeStartDateTime.Time.Minutes = out.Messages[i].ORUR01.PATIENT_RESULT[0].ORDER_OBSERVATION[0].SPECIMEN[0].SPM.SpecimenCollectionDateTime.RangeStartDateTime.Time.Seconds;//NOSONAR
                String obrDateTime= obrCopy.getObservationRequest().getObservationDateTime().getTime();
                String newObrDateTime=replaceMinutesWithSeconds(obrDateTime);
                obrCopy.getObservationRequest().getObservationDateTime().setTime(newObrDateTime);
                List<Specimen> spmList= obrCopy.getSpecimen();
                for(Specimen specimen : spmList) {
                    String spmCollTime= specimen.getSpecimen().getSpecimenCollectionDateTime().getRangeStartDateTime().getTime();
                    String newCollTime=replaceMinutesWithSeconds(spmCollTime);
                    newCollTime=replaceSecondsWithIndex(newCollTime,j+1);
                    specimen.getSpecimen().getSpecimenCollectionDateTime().getRangeStartDateTime().setTime(newCollTime);
                }
                //Add obx in the OBR
                obrCopy.setObservation(List.of(obx));

                //copy and create new oruR1 obj from original message
                OruR1 oruR1Copy = gson.fromJson(gson.toJson(oruR1), OruR1.class);
                //Empty the existing OBRs
                oruR1Copy.getPatientResult().getFirst().getOrderObservation().clear();
                //Add OBR
                oruR1Copy.getPatientResult().getFirst().setOrderObservation(List.of(obrCopy));
                //make MessageControlIDs unique for every message created when OBRs are split out.
                // ORUR01.MSH.MessageControlID
                String msgControlId = oruR1Copy.getMessageHeader().getMessageControlId();
                //create unique MessageControlId
                String newMsgControlId = getCustomMessageControlId(msgControlId, j+1);
                oruR1Copy.getMessageHeader().setMessageControlId(newMsgControlId);
                oruR1Copy.getMessageHeader().getSendingFacility().getUniversalId();
                //Update MSH.SendingFacility.UniversalID
                //out.Messages[i].ORUR01.MSH.SendingFacility.UniversalID = (out.Messages[i].ORUR01.MSH.SendingFacility.UniversalID + (i * .01) );//NOSONAR
                String newUniversalID=oruR1Copy.getMessageHeader().getSendingFacility().getUniversalId()+((j+1) * .01);
                oruR1Copy.getMessageHeader().getSendingFacility().setUniversalId(newUniversalID);

                //create HL7ParsedMessage for xml creation
                HL7ParsedMessage<OruR1> parsedMessage = new HL7ParsedMessage<>();
                parsedMessage.setParsedMessage(oruR1Copy);
                parsedMessage.setType(parsedMessageOrig.getType());
                parsedMessage.setEventTrigger(parsedMessageOrig.getEventTrigger());
                parsedMessage.setOriginalVersion(parsedMessageOrig.getOriginalVersion());
                parsedMessageList.add(parsedMessage);
            }
        }
        log.info("splitElrByOBX - parsedMessageList size:" + parsedMessageList.size());
        return parsedMessageList;
    }

    @SuppressWarnings({"java:S3776","java:S117","java:S6541"})
    private List<HL7ParsedMessage<OruR1>> splitElrByOBR(HL7ParsedMessage<OruR1> parsedMessageOrig) {
        List<HL7ParsedMessage<OruR1>> parsedMessageList = new ArrayList<>();
        Gson gson = new Gson();
        OruR1 oruR1 = parsedMessageOrig.getParsedMessage();
        List<PatientResult> patientResultList = oruR1.getPatientResult();
        //Take OBR list from the original ELR
        List<OrderObservation> obrList = patientResultList.getFirst().getOrderObservation();
        if (obrList != null && obrList.size() > 1) {
            log.debug("Before OBR split..splitElrByOBR Incoming Obr list size:" + obrList.size());
            //Get Specimen from an obr
            List<Specimen> spmObj=new ArrayList<>();
            for (int i = obrList.size() - 1; i >= 0; i--) {
                spmObj= obrList.get(i).getSpecimen();
                if(spmObj!=null && !spmObj.isEmpty()) {
                    break;
                }
            }
            //Fix obr ids to be in sequence like 1,2,3 and set specimen value before splitting
            for (int i = 0; i < obrList.size(); i++) {
                OrderObservation obr = obrList.get(i);
                obr.getObservationRequest().setSetIdObr(""+(i+1));
                if(obr.getSpecimen() == null || obr.getSpecimen().isEmpty()){
                    obr.setSpecimen(spmObj);
                }
            }
            //ORC data is available only in the first OBR object and needs to be copied to the other OBRs.
            CommonOrder orc = oruR1.getPatientResult().getFirst().getOrderObservation().getFirst().getCommonOrder();
            //Check if a OBR is parent
            //parent order number
            //filler order number
            List<ArrayList<OrderObservation>> splitOBRs = new ArrayList<>();
            List<String> obrIds = new ArrayList<>();

            for (int i = 0; i < obrList.size(); i++) {
                OrderObservation obr = obrList.get(i);

                String obr_2_1_placerOrderId = obr.getObservationRequest().getPlacerOrderNumber().getEntityIdentifier();
                String obr_3_1_fillerOrderId = obr.getObservationRequest().getFillerOrderNumber().getEntityIdentifier();
                String obrId = obr.getObservationRequest().getSetIdObr();
                log.info("Parent SetIdObr:" + obrId + " placerOrderId:" + obr_2_1_placerOrderId + " fillerOrderId:" + obr_3_1_fillerOrderId);

                ArrayList<OrderObservation> tempObr = new ArrayList<>();
                if (!obrIds.contains(obrId)) {
                    OrderObservation obrCopy = gson.fromJson(gson.toJson(obr), OrderObservation.class);
                    tempObr.add(obrCopy);
                    obrIds.add(obrId);

                    List<Observation> obxList = obr.getObservation();
                    for (Observation obx : obxList) {
                        log.info("Parent obx setId:" + obx.getObservationResult().getSetIdObx());
                        //OBX-3 for Parent
                        String obx_3_1_id = obx.getObservationResult().getObservationIdentifier().getIdentifier();
                        String obx_3_2_text = obx.getObservationResult().getObservationIdentifier().getText();
                        String obx_3_3_nameOfCodeSys = obx.getObservationResult().getObservationIdentifier().getNameOfCodingSystem();
                        log.info("Parent OBX-3 - obx_3_1_id:" + obx_3_1_id + " obx_3_2_text:" + obx_3_2_text + " obx_3_3_nameOfCodeSys:" + obx_3_3_nameOfCodeSys);
                        //OBX-4 for Parent
                        String obx_4_subId = obx.getObservationResult().getObservationSubId();
                        log.info("Parent OBX-4 sub-id obx_4_subId"+obx_4_subId);
                        //loop the next OBRs for the child
                        for (int j = i + 1; j < obrList.size(); j++) {
                            OrderObservation nextObr = obrList.get(j);
                            String nextObrId = nextObr.getObservationRequest().getSetIdObr();
                            if (!obrIds.contains(nextObrId)) {
                                //OBR-29 Parent link from the child obr
                                String obr_29_1_1_placerOrderNumId = nextObr.getObservationRequest().getParent().getPlacerAssignedIdentifier().getEntityIdentifier();
                                String obr_29_2_1_fillerOrderNumId = nextObr.getObservationRequest().getParent().getFillerAssignedIdentifier().getEntityIdentifier();
                                log.info("Child:OBR-29 obr_29_2_1_fillerOrderNumId:" + obr_29_2_1_fillerOrderNumId+" obr_29_1_1_placerOrderNumId:"+obr_29_1_1_placerOrderNumId+" parent:obr_2_1_placerOrderId"+obr_2_1_placerOrderId+" obr_3_1_fillerOrderId:"+obr_3_1_fillerOrderId);
                                //OBR-26 Parent Result link from the child obr
                                String obr_26_1_1_parentRsltId = nextObr.getObservationRequest().getParentResult().getParentObservationIdentifier().getIdentifier();
                                String obr_26_1_2_parentRsltTxt = nextObr.getObservationRequest().getParentResult().getParentObservationIdentifier().getText();
                                String obr_26_1_3_parentRsltCodeSys = nextObr.getObservationRequest().getParentResult().getParentObservationIdentifier().getNameOfCodingSystem();
                                log.info("Child obr_26_1_1_parentRsltId:" + obr_26_1_1_parentRsltId + " obr_26_1_2_parentRsltTxt:" + obr_26_1_2_parentRsltTxt + " obr_26_1_3_parentRsltCodeSys:" + obr_26_1_3_parentRsltCodeSys);

                                String obr_26_2_parentRsltSubId = nextObr.getObservationRequest().getParentResult().getParentObservationSubIdentifier();
                                log.info("Child:obr_26_2_parentRsltSubId:" + obr_26_2_parentRsltSubId);

                                //Child OBR-29.2.1(filler order number) = Parent OBR-3.1(filler order number);//NOSONAR
                                // Child OBR-29.1.1(placer order number) = Parent OBR-2.1(Placer order number);//NOSONAR
                                //the filler order may be blank but then it is blank in both OBR.3.1 and OBR.29.2.1//NOSONAR
                                //Child OBR-26.1 = OBX-3 from the parent OBX;//NOSONAR
                                //Child OBR-26.2 = OBX-4 from parent OBX;//NOSONAR
                                if (StringUtils.equals(obr_29_1_1_placerOrderNumId, obr_2_1_placerOrderId) && StringUtils.equals(obr_29_2_1_fillerOrderNumId, obr_3_1_fillerOrderId)
                                        && StringUtils.equals(obr_26_1_1_parentRsltId, obx_3_1_id) && StringUtils.equals(obr_26_1_2_parentRsltTxt, obx_3_2_text) && StringUtils.equals(obr_26_1_3_parentRsltCodeSys, obx_3_3_nameOfCodeSys)
                                        && StringUtils.equals(obr_26_2_parentRsltSubId, obx_4_subId)) {
                                    log.info("Matching parent-child found. parent obr:"+obrId+ "child Obr:" + nextObrId);
                                    OrderObservation nextObrCopy = gson.fromJson(gson.toJson(nextObr), OrderObservation.class);
                                    tempObr.add(nextObrCopy);
                                    obrIds.add(nextObrId);
                                }
                            }
                        }
                    }
                    splitOBRs.add(tempObr);
                }
            }
            log.info("Number of OBR groups after applying parent child:" + splitOBRs.size());

            for (int i = 0; i < splitOBRs.size(); i++) {
                ArrayList<OrderObservation> obrs = splitOBRs.get(i);
                int obrCount = obrs.size();
                //Add ORC
                if (obrs.getFirst().getCommonOrder().getOrderControl() == null) {
                    obrs.getFirst().setCommonOrder(orc);
                }
                if(obrCount==1){
                    //There is no valid parent child. So remove ParentResult,Parent if any from the OBR.
                    obrs.getFirst().getObservationRequest().setParent(new Eip());
                    obrs.getFirst().getObservationRequest().setParentResult(new Prl());
                }
                for (int j = 0; j < obrCount; j++) {
                    OrderObservation orderObservation = obrs.get(j);
                    orderObservation.getObservationRequest().setSetIdObr(""+(j+1));
                }
                //Create a New ELR per OBR or set of OBRs.
                //copy and create new oruR1 obj from original message
                OruR1 oruR1Copy = gson.fromJson(gson.toJson(oruR1), OruR1.class);
                oruR1Copy.getPatientResult().get(0).setOrderObservation(obrs);
                //make MessageControlIDs unique for every message created when OBRs are split out.
                //ORUR01.MSH.MessageControlID
                String msgControlId = oruR1Copy.getMessageHeader().getMessageControlId();
                String newMsgControlId = getCustomMessageControlId(msgControlId, i+1);
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
            log.info("Single OBR. No split.");
            parsedMessageList.add(parsedMessageOrig);
        }
        log.info("Parsed ELR message sizes in OBRSplitter: {}", parsedMessageList.size());
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
    private static String replaceSecondsWithIndex(String specimenTime,int indexVal) {
        String newSeconds = String.format("%02d", indexVal);
        String updatedtTime=specimenTime;
        if(!StringUtils.isBlank(specimenTime) && specimenTime.length()>=12) {
            String uptoMinutes= StringUtils.left(specimenTime, 12);
            String afterSeconds="";
            if(specimenTime.length()>14){
                afterSeconds= specimenTime.substring(14);
            }
            updatedtTime=uptoMinutes+newSeconds+afterSeconds;
        }
        return updatedtTime;
    }
    static String replaceMinutesWithSeconds(String specimenTime) {
        String updatedTime=specimenTime;
        if(!StringUtils.isBlank(specimenTime) && specimenTime.length()>=14) {
            String seconds=specimenTime.substring(12,14);
            String uptoHours= StringUtils.left(specimenTime, 10);
            String fromSeconds=specimenTime.substring(12);
            updatedTime=uptoHours+seconds+fromSeconds;
        }
        return updatedTime;
    }
}