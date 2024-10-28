package gov.cdc.dataingestion.nbs.repository.model.dao;

import gov.cdc.dataingestion.nbs.repository.model.dto.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
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
