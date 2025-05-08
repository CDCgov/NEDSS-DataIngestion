package gov.cdc.dataingestion.share.helper;

import com.google.gson.Gson;
import gov.cdc.dataingestion.hl7.helper.model.HL7ParsedMessage;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.CommonOrder;
import gov.cdc.dataingestion.hl7.helper.model.hl7.message_data_type.Eip;
import gov.cdc.dataingestion.hl7.helper.model.hl7.message_data_type.Prl;
import gov.cdc.dataingestion.hl7.helper.model.hl7.message_group.OrderObservation;
import gov.cdc.dataingestion.hl7.helper.model.hl7.message_group.PatientResult;
import gov.cdc.dataingestion.hl7.helper.model.hl7.message_type.OruR1;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ElrSplitter {
    public List<HL7ParsedMessage<OruR1>> splitElrByOBR(HL7ParsedMessage<OruR1> parsedMessageOrig) {
        List<HL7ParsedMessage<OruR1>> parsedMessageList=new ArrayList<>();

        OruR1 oruR1= parsedMessageOrig.getParsedMessage();
        List<PatientResult> patientResultList=oruR1.getPatientResult();
        //Take OBR list from the original ELR
        List<OrderObservation> obrList=patientResultList.get(0).getOrderObservation();

        if(obrList !=null && obrList.size()>1){
            log.debug("Multiple OBRs exist.");
            CommonOrder commonOrderFromFirstOBR=null;
            int i=0;
            for(OrderObservation orderObservation: obrList){
                i++;
                orderObservation.getObservationRequest().setSetIdObr("1");
                //ORC data is available only in the first OBR object and needs to be copied to the other OBRs.
                if(commonOrderFromFirstOBR==null && orderObservation.getCommonOrder().getOrderControl()!=null){
                    commonOrderFromFirstOBR= orderObservation.getCommonOrder();
                }
                if(orderObservation.getCommonOrder().getOrderControl()==null){
                    orderObservation.setCommonOrder(commonOrderFromFirstOBR);
                }
                //remove ParentResult,Parent from ObservationRequest
                orderObservation.getObservationRequest().setParent(new Eip());
                orderObservation.getObservationRequest().setParentResult(new Prl());

                Gson gson = new Gson();
                //copy and create new oruR1 obj from original message
                OruR1 oruR1Copy = gson.fromJson(gson.toJson(oruR1), OruR1.class);
                oruR1Copy.getPatientResult().get(0).setOrderObservation(List.of(orderObservation));
                //make MessageControlIDs unique for every message created when OBRs are split out.
                // ORUR01.MSH.MessageControlID
                String msgControlId=oruR1Copy.getMessageHeader().getMessageControlId();
                String newMsgControlId=msgControlId.substring(0,(msgControlId.length()/2)-3)+i+(i+1)+(i+2)+msgControlId.substring(msgControlId.length()/2,msgControlId.length());
                oruR1Copy.getMessageHeader().setMessageControlId(newMsgControlId);
                //create HL7ParsedMessage for xml creation
                HL7ParsedMessage<OruR1> parsedMessage = new HL7ParsedMessage<>();
                parsedMessage.setParsedMessage(oruR1Copy);
                parsedMessage.setType(parsedMessageOrig.getType());
                parsedMessage.setEventTrigger(parsedMessageOrig.getEventTrigger());
                parsedMessage.setOriginalVersion(parsedMessageOrig.getOriginalVersion());
                parsedMessageList.add(parsedMessage);
            }
        }else{
            log.debug("Single OBR exist.");
            parsedMessageList.add(parsedMessageOrig);
        }
        log.debug("phdc xml list: {}", parsedMessageList.size());
        System.out.println("Parsed messages size in ELRsplitter:"+parsedMessageList.size());
        return parsedMessageList;
    }
}
