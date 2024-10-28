package gov.cdc.dataingestion.nbs.ecr.model.interview;

import gov.cdc.nedss.phdc.cda.POCDMT000040Encounter;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126"})
public class InterviewMultiObs {
    public InterviewMultiObs() {
        // default
    }

    public InterviewMultiObs(String dataType,
                             int sectionCounter,
                             int componentCounter,
                             int seqNbr,
                             String questionIdentifier) {
        // Multi Select Obs P2
        this.dataType = dataType;
        this.sectionCounter = sectionCounter;
        this.componentCounter = componentCounter;
        this.seqNbr = seqNbr;
        this.questionIdentifier = questionIdentifier;
    }

    public InterviewMultiObs(String questionIdentifier,
                            String questionId,
                             int sectionCounter,
                             int componentCounter) {
        // Multi Select Obs P3
        this.questionIdentifier = questionIdentifier;
        this.questionId = questionId;
        this.sectionCounter = sectionCounter;
        this.componentCounter = componentCounter;
    }
    POCDMT000040Encounter out;
    int answerGroupSeqNbr;
    int answerGroupCounter;
    int questionGroupSeqNbr;
    int questionGroupCounter;
    int sectionCounter;
    int componentCounter;
    String dataType;
    int seqNbr;
    String questionIdentifier;
    String questionId;
}
