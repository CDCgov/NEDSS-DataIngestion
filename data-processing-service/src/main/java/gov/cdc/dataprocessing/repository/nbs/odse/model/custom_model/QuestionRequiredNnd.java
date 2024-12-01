package gov.cdc.dataprocessing.repository.nbs.odse.model.custom_model;

import lombok.Data;

@Data
public class QuestionRequiredNnd {
    private Long nbsQuestionUid;
    private String questionIdentifier;
    private String questionLabel;
    private String dataLocation;
}
