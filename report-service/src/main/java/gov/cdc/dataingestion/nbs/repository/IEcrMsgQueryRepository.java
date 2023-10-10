package gov.cdc.dataingestion.nbs.repository;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.repository.model.dto.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IEcrMsgQueryRepository {
     EcrMsgContainerDto FetchMsgContainerForApplicableEcr() throws EcrCdaXmlException;
     List<EcrMsgPatientDto> FetchMsgPatientForApplicableEcr(Integer containerId) throws EcrCdaXmlException;
     List<EcrMsgCaseDto> FetchMsgCaseForApplicableEcr(Integer containerId) throws EcrCdaXmlException;
     List<EcrMsgCaseParticipantDto> FetchMsgCaseParticipantForApplicableEcr(Integer containerId, String invLocalId) throws EcrCdaXmlException;
     List<EcrMsgCaseAnswerDto> FetchMsgCaseAnswerForApplicableEcr(Integer containerId, String invLocalId) throws EcrCdaXmlException;
     List<EcrMsgCaseAnswerRepeatDto> FetchMsgCaseAnswerRepeatForApplicableEcr(Integer containerId, String invLocalId) throws EcrCdaXmlException;
     List<EcrMsgXmlAnswerDto> FetchMsgXmlAnswerForApplicableEcr(Integer containerId, String invLocalId) throws EcrCdaXmlException;
     List<EcrMsgProviderDto> FetchMsgProviderForApplicableEcr(Integer containerId) throws EcrCdaXmlException;
     List<EcrMsgOrganizationDto> FetchMsgOrganizationForApplicableEcr(Integer containerId) throws EcrCdaXmlException;
     List<EcrMsgPlaceDto> FetchMsgPlaceForApplicableEcr(Integer containerId) throws EcrCdaXmlException;
     List<EcrMsgInterviewDto> FetchMsgInterviewForApplicableEcr(Integer containerId) throws EcrCdaXmlException;
     List<EcrMsgInterviewProviderDto> FetchMsgInterviewProviderForApplicableEcr(Integer containerId,  String ixsLocalId) throws EcrCdaXmlException;
     List<EcrMsgInterviewAnswerDto> FetchMsgInterviewAnswerForApplicableEcr(Integer containerId, String ixsLocalId) throws EcrCdaXmlException;
     List<EcrMsgInterviewAnswerRepeatDto> FetchMsgInterviewAnswerRepeatForApplicableEcr(Integer containerId, String ixsLocalId) throws EcrCdaXmlException;
     List<EcrMsgTreatmentDto> FetchMsgTreatmentForApplicableEcr(Integer containerId) throws EcrCdaXmlException;
     List<EcrMsgTreatmentProviderDto> FetchMsgTreatmentProviderForApplicableEcr(Integer containerId) throws EcrCdaXmlException;
     List<EcrMsgTreatmentOrganizationDto> FetchMsgTreatmentOrganizationForApplicableEcr(Integer containerId) throws EcrCdaXmlException;
     void UpdateMatchEcrRecordForProcessing(Integer containerUid) throws EcrCdaXmlException;
}
