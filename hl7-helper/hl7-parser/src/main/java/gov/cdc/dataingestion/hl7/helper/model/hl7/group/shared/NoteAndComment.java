package gov.cdc.dataingestion.hl7.helper.model.hl7.group.shared;

import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Ce;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import static gov.cdc.dataingestion.hl7.helper.helper.modelListHelper.*;
@Getter
@Setter
public class NoteAndComment {
    String setIdNte;
    String sourceOfComment;
    List<String> comment;
    Ce commentType;

    public NoteAndComment(ca.uhn.hl7v2.model.v251.segment.NTE nte) {
        this.setIdNte = nte.getSetIDNTE().getValue();
        this.sourceOfComment = nte.getSourceOfComment().getValue();
        this.comment = GetFtStringList(nte.getComment());
        this.commentType = new Ce(nte.getCommentType());
    }
}
