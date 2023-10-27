package gov.cdc.dataingestion.nbs.repository;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.repository.model.dto.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IEcrMsgQueryRepository {
     EcrMsgContainerDto fetchMsgContainerForApplicableEcr() throws EcrCdaXmlException;
     List<EcrMsgPatientDto> fetchMsgPatientForApplicableEcr(Integer containerId) throws EcrCdaXmlException;
     List<EcrMsgCaseDto> fetchMsgCaseForApplicableEcr(Integer containerId) throws EcrCdaXmlException;
     List<EcrMsgCaseParticipantDto> fetchMsgCaseParticipantForApplicableEcr(Integer containerId, String invLocalId) throws EcrCdaXmlException;
     List<EcrMsgCaseAnswerDto> fetchMsgCaseAnswerForApplicableEcr(Integer containerId, String invLocalId) throws EcrCdaXmlException;
     List<EcrMsgCaseAnswerDto> fetchMsgCaseAnswerRepeatForApplicableEcr(Integer containerId, String invLocalId) throws EcrCdaXmlException;
     List<EcrMsgXmlAnswerDto> fetchMsgXmlAnswerForApplicableEcr(Integer containerId, String invLocalId) throws EcrCdaXmlException;
     List<EcrMsgProviderDto> fetchMsgProviderForApplicableEcr(Integer containerId) throws EcrCdaXmlException;
     List<EcrMsgOrganizationDto> fetchMsgOrganizationForApplicableEcr(Integer containerId) throws EcrCdaXmlException;
     List<EcrMsgPlaceDto> fetchMsgPlaceForApplicableEcr(Integer containerId) throws EcrCdaXmlException;
     List<EcrMsgInterviewDto> fetchMsgInterviewForApplicableEcr(Integer containerId) throws EcrCdaXmlException;
     List<EcrMsgProviderDto> fetchMsgInterviewProviderForApplicableEcr(Integer containerId,  String ixsLocalId) throws EcrCdaXmlException;
     List<EcrMsgCaseAnswerDto> fetchMsgInterviewAnswerForApplicableEcr(Integer containerId, String ixsLocalId) throws EcrCdaXmlException;
     List<EcrMsgCaseAnswerDto> fetchMsgInterviewAnswerRepeatForApplicableEcr(Integer containerId, String ixsLocalId) throws EcrCdaXmlException;
     List<EcrMsgTreatmentDto> fetchMsgTreatmentForApplicableEcr(Integer containerId) throws EcrCdaXmlException;
     List<EcrMsgProviderDto> fetchMsgTreatmentProviderForApplicableEcr(Integer containerId) throws EcrCdaXmlException;
     List<EcrMsgOrganizationDto> fetchMsgTreatmentOrganizationForApplicableEcr(Integer containerId) throws EcrCdaXmlException;
     void updateMatchEcrRecordForProcessing(Integer containerUid) throws EcrCdaXmlException;
}
