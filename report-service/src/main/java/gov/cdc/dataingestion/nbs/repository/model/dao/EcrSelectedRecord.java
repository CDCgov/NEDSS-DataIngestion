package gov.cdc.dataingestion.nbs.repository.model.dao;

import gov.cdc.dataingestion.nbs.repository.model.dto.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class EcrSelectedRecord {
    private EcrMsgContainerDto MsgContainer;
    private List<EcrMsgPatientDto> MsgPatients;
    private List<EcrSelectedCase> MsgCases;
    private List<EcrMsgXmlAnswerDto> MsgXmlAnswers;
    private List<EcrMsgProviderDto> MsgProviders;
    private List<EcrMsgOrganizationDto> MsgOrganizations;
    private List<EcrMsgPlaceDto> MsgPlaces;
    private List<EcrSelectedInterview> MsgInterviews;
    private List<EcrSelectedTreatment> MsgTreatments;
}
