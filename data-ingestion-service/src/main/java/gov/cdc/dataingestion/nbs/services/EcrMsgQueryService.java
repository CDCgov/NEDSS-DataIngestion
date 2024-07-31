package gov.cdc.dataingestion.nbs.services;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.repository.IEcrMsgQueryRepository;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedCase;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedInterview;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedRecord;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedTreatment;
import gov.cdc.dataingestion.nbs.repository.model.dto.*;
import gov.cdc.dataingestion.nbs.services.interfaces.IEcrMsgQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EcrMsgQueryService implements IEcrMsgQueryService {
    private final IEcrMsgQueryRepository ecrMsgQueryRepository;

    @Autowired
    public EcrMsgQueryService(IEcrMsgQueryRepository ecrMsgQueryRepository) {
        this.ecrMsgQueryRepository = ecrMsgQueryRepository;
    }

    public EcrSelectedRecord getSelectedEcrRecord() throws EcrCdaXmlException {
        EcrSelectedRecord selectedRecord = null;
        EcrMsgContainerDto msgContainer = this.ecrMsgQueryRepository.fetchMsgContainerForApplicableEcr();

        if (msgContainer != null && msgContainer.getMsgContainerUid() != null) {
            selectedRecord = new EcrSelectedRecord();

            // Commented out for testing right now and will need to be uncommented during the unit tests stage
            // this.ecrMsgQueryRepository.updateMatchEcrRecordForProcessing(msgContainer.getMsgContainerUid()); //NOSONAR

            List<EcrMsgPatientDto> msgPatients = this.ecrMsgQueryRepository.fetchMsgPatientForApplicableEcr(msgContainer.getMsgContainerUid());

            List<EcrSelectedCase> selectedMsgCases = new ArrayList<>();
            List<EcrMsgXmlAnswerDto> msgXmlAnswers = new ArrayList<>();
            List<EcrMsgCaseDto> msgCases = this.ecrMsgQueryRepository.fetchMsgCaseForApplicableEcr(msgContainer.getMsgContainerUid());
            for(var item : msgCases) {

                EcrSelectedCase selectedCase = new EcrSelectedCase();
                List<EcrMsgCaseParticipantDto> msgCaseParticipants = this.ecrMsgQueryRepository.fetchMsgCaseParticipantForApplicableEcr(
                        msgContainer.getMsgContainerUid(), item.getInvLocalId()
                );

                List<EcrMsgCaseAnswerDto> msgCaseAnswers = this.ecrMsgQueryRepository.fetchMsgCaseAnswerForApplicableEcr(
                        msgContainer.getMsgContainerUid(), item.getInvLocalId()
                );

                List<EcrMsgCaseAnswerDto> msgCaseAnswerRepeats = this.ecrMsgQueryRepository.fetchMsgCaseAnswerRepeatForApplicableEcr(
                        msgContainer.getMsgContainerUid(), item.getInvLocalId()
                );
                selectedCase.setMsgCase(item);
                selectedCase.setMsgCaseParticipants(msgCaseParticipants);
                selectedCase.setMsgCaseAnswers(msgCaseAnswers);
                selectedCase.setMsgCaseAnswerRepeats(msgCaseAnswerRepeats);
                selectedMsgCases.add(selectedCase);

                List<EcrMsgXmlAnswerDto> msgXmlAnswer = this.ecrMsgQueryRepository.fetchMsgXmlAnswerForApplicableEcr(msgContainer.getMsgContainerUid(), item.getInvLocalId());
                msgXmlAnswers.addAll(msgXmlAnswer);
            }

            List<EcrMsgProviderDto> msgProviders = this.ecrMsgQueryRepository.fetchMsgProviderForApplicableEcr(msgContainer.getMsgContainerUid());
            List<EcrMsgOrganizationDto> msgOrganizations = this.ecrMsgQueryRepository.fetchMsgOrganizationForApplicableEcr(msgContainer.getMsgContainerUid());
            List<EcrMsgPlaceDto> msgPlaces = this.ecrMsgQueryRepository.fetchMsgPlaceForApplicableEcr(msgContainer.getMsgContainerUid());

            List<EcrSelectedInterview> selectedInterviews = new ArrayList<>();
            List<EcrMsgInterviewDto> msgInterviews = this.ecrMsgQueryRepository.fetchMsgInterviewForApplicableEcr(msgContainer.getMsgContainerUid());
            for(var item : msgInterviews) {
                EcrSelectedInterview selectedInterview = new EcrSelectedInterview();

                List<EcrMsgProviderDto> msgInterviewProviders = this.ecrMsgQueryRepository.fetchMsgInterviewProviderForApplicableEcr(
                        msgContainer.getMsgContainerUid(), item.getIxsLocalId());

                List<EcrMsgCaseAnswerDto> msgInterviewAnswers = this.ecrMsgQueryRepository.fetchMsgInterviewAnswerForApplicableEcr(
                        msgContainer.getMsgContainerUid(), item.getIxsLocalId());

                List<EcrMsgCaseAnswerDto> msgInterviewAnswerRepeats = this.ecrMsgQueryRepository.fetchMsgInterviewAnswerRepeatForApplicableEcr(
                        msgContainer.getMsgContainerUid(), item.getIxsLocalId());

                selectedInterview.setMsgInterview(item);
                selectedInterview.setMsgInterviewProviders(msgInterviewProviders);
                selectedInterview.setMsgInterviewAnswers(msgInterviewAnswers);
                selectedInterview.setMsgInterviewAnswerRepeats(msgInterviewAnswerRepeats);
                selectedInterviews.add(selectedInterview);
            }

            List<EcrSelectedTreatment> selectedTreatments = new ArrayList<>();
            List<EcrMsgTreatmentDto> msgTreatments = this.ecrMsgQueryRepository.fetchMsgTreatmentForApplicableEcr(msgContainer.getMsgContainerUid());
            for(int i = 0; i <= msgTreatments.size() - 1; i++) {

                var item = msgTreatments.get(i);
                EcrSelectedTreatment selectedTreatment = new EcrSelectedTreatment();

                String trtLocalId = item.getTrtLocalId();

                List<EcrMsgProviderDto> ecrMsgTreatmentProviders = this.ecrMsgQueryRepository.fetchMsgTreatmentProviderForApplicableEcr(
                        msgContainer.getMsgContainerUid(), trtLocalId);

                List<EcrMsgOrganizationDto> ecrMsgTreatmentOrganizations = this.ecrMsgQueryRepository.fetchMsgTreatmentOrganizationForApplicableEcr(
                        msgContainer.getMsgContainerUid(), trtLocalId
                );

                selectedTreatment.setMsgTreatment(item);
                selectedTreatment.setMsgTreatmentProviders(ecrMsgTreatmentProviders);
                selectedTreatment.setMsgTreatmentOrganizations(ecrMsgTreatmentOrganizations);
                selectedTreatments.add(selectedTreatment);
            }

            selectedRecord.setMsgContainer(msgContainer);
            selectedRecord.setMsgPatients(msgPatients);
            selectedRecord.setMsgCases(selectedMsgCases);
            selectedRecord.setMsgXmlAnswers(msgXmlAnswers);
            selectedRecord.setMsgProviders(msgProviders);
            selectedRecord.setMsgOrganizations(msgOrganizations);
            selectedRecord.setMsgPlaces(msgPlaces);
            selectedRecord.setMsgInterviews(selectedInterviews);
            selectedRecord.setMsgTreatments(selectedTreatments);
        }
        return selectedRecord;
    }
}
