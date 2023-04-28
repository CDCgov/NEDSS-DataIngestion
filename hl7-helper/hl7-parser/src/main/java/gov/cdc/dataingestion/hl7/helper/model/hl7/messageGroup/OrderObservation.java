package gov.cdc.dataingestion.hl7.helper.model.hl7.messageGroup;

import ca.uhn.hl7v2.HL7Exception;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.*;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.shared.NoteAndComment;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class OrderObservation {
    CommonOrder commonOrder;
    ObservationRequest observationRequest;
    List<NoteAndComment> noteAndComment;
    ContactData contactData;
    List<FinancialTransaction> financialTransaction;
    List<ClinicalTrialIdentification> clinicalTrialIdentification;

    // Nested Group
    List<TimingQty> timingQty;
    List<Observation> observation;
    List<Specimen> specimen;
    public OrderObservation(ca.uhn.hl7v2.model.v251.group.ORU_R01_ORDER_OBSERVATION oruR01OrderObservation) throws HL7Exception {
        this.commonOrder = new CommonOrder(oruR01OrderObservation.getORC());
        this.observationRequest = new ObservationRequest(oruR01OrderObservation.getOBR());

        noteAndComment = new ArrayList<>();
        for(var item: oruR01OrderObservation.getNTEAll()) {
            this.noteAndComment.add(new NoteAndComment(item));
        }

        this.contactData = new ContactData(oruR01OrderObservation.getCTD());

        financialTransaction = new ArrayList<>();
        for(var item: oruR01OrderObservation.getFT1All()) {
            this.financialTransaction.add(new FinancialTransaction(item));
        }

        clinicalTrialIdentification = new ArrayList<>();
        for(var item: oruR01OrderObservation.getCTIAll()) {
            this.clinicalTrialIdentification.add(new ClinicalTrialIdentification(item));
        }

        this.timingQty = new ArrayList<>();
        for(var item : oruR01OrderObservation.getTIMING_QTYAll()) {
            this.timingQty.add(new TimingQty(item));
        }

        this.observation = new ArrayList<>();
        for(var item: oruR01OrderObservation.getOBSERVATIONAll()) {
            this.observation.add(new Observation(item));
        }

        this.specimen = new ArrayList<>();
        for(var item: oruR01OrderObservation.getSPECIMENAll()) {
            this.specimen.add(new Specimen(item));
        }
    }
}
