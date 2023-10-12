package gov.cdc.dataingestion.nbs.ecr.model.interview;

import gov.cdc.nedss.phdc.cda.POCDMT000040Encounter;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InterviewObs {

    public InterviewObs(int questionGroupSeqNbr,
                        int answerGroupSeqNbr,
                        String dataType,
                        int sequenceNbr) {
        // Map Inv OBS P1
        this.questionGroupSeqNbr = questionGroupSeqNbr;
        this.answerGroupSeqNbr = answerGroupSeqNbr;
        this.dataType = dataType;
        this.sequenceNbr = sequenceNbr;
    }

    public InterviewObs(String dataType,
                        int counter,
                        int sequenceNbr
                        ) {
        // Map Inv OBS P2
        this.dataType = dataType;
        this.counter = counter;
        this.sequenceNbr = sequenceNbr;
    }

    public InterviewObs(String questionSeq,
                        int counter) {
        // Map Inv OBS P3
        this.questionSeq = questionSeq;
        this.counter = counter;
    }


    int questionGroupSeqNbr;
    int answerGroupSeqNbr;
    String dataType;
    int sequenceNbr;
    int counter;
    POCDMT000040Encounter out;
    String questionSeq;

}
