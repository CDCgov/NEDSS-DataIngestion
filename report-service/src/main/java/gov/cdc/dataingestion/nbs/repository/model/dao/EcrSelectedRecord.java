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
    private EcrMsgContainerDto msgContainer;
    private List<EcrMsgPatientDto> msgPatients;
    private List<EcrSelectedCase> msgCases;
    private List<EcrMsgXmlAnswerDto> msgXmlAnswers;
    private List<EcrMsgProviderDto> msgProviders;
    private List<EcrMsgOrganizationDto> msgOrganizations;
    private List<EcrMsgPlaceDto> msgPlaces;
    private List<EcrSelectedInterview> msgInterviews;
    private List<EcrSelectedTreatment> msgTreatments;
}
