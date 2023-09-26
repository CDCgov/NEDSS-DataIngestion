package gov.cdc.dataingestion.nbs.repository;

import gov.cdc.dataingestion.nbs.repository.model.dto.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IEcrMsgQueryRepository {
     EcrMsgContainerDto FetchMsgContainerForApplicableEcr();
     List<EcrMsgPatientDto> FetchMsgPatientForApplicableEcr(Integer containerId);
     List<EcrMsgCaseDto> FetchMsgCaseForApplicableEcr(Integer containerId);
     List<EcrMsgCaseParticipantDto> FetchMsgCaseParticipantForApplicableEcr(Integer containerId, String invLocalId);
     List<EcrMsgCaseAnswerDto> FetchMsgCaseAnswerForApplicableEcr(Integer containerId, String invLocalId);
     List<EcrMsgCaseAnswerRepeatDto> FetchMsgCaseAnswerRepeatForApplicableEcr(Integer containerId, String invLocalId);
     List<EcrMsgXmlAnswerDto> FetchMsgXmlAnswerForApplicableEcr(Integer containerId, String invLocalId);
     List<EcrMsgProviderDto> FetchMsgProviderForApplicableEcr(Integer containerId);
     List<EcrMsgOrganizationDto> FetchMsgOrganizationForApplicableEcr(Integer containerId);
     List<EcrMsgPlaceDto> FetchMsgPlaceForApplicableEcr(Integer containerId);
     List<EcrMsgInterviewDto> FetchMsgInterviewForApplicableEcr(Integer containerId);
     List<EcrMsgInterviewProviderDto> FetchMsgInterviewProviderForApplicableEcr(Integer containerId,  String ixsLocalId);
     List<EcrMsgInterviewAnswerDto> FetchMsgInterviewAnswerForApplicableEcr(Integer containerId, String ixsLocalId);
     List<EcrMsgInterviewAnswerRepeatDto> FetchMsgInterviewAnswerRepeatForApplicableEcr(Integer containerId, String ixsLocalId);
     List<EcrMsgTreatmentDto> FetchMsgTreatmentForApplicableEcr(Integer containerId);
     List<EcrMsgTreatmentProviderDto> FetchMsgTreatmentProviderForApplicableEcr(Integer containerId);
     List<EcrMsgTreatmentOrganizationDto> FetchMsgTreatmentOrganizationForApplicableEcr(Integer containerId);
     void UpdateMatchEcrRecordForProcessing(Integer containerUid);
}
