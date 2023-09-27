package gov.cdc.dataingestion.nbs.services;

import gov.cdc.dataingestion.nbs.repository.IEcrMsgQueryRepository;
import gov.cdc.dataingestion.nbs.repository.implementation.JsonReaderTester;
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
    private IEcrMsgQueryRepository ecrMsgQueryRepository;

    @Autowired
    public EcrMsgQueryService(IEcrMsgQueryRepository ecrMsgQueryRepository) {
        this.ecrMsgQueryRepository = ecrMsgQueryRepository;
    }

    public EcrSelectedRecord getSelectedEcrFromJson() {
        var container = JsonReaderTester.loadContainer();
        var patient = JsonReaderTester.loadPatient();

        var msgCase = JsonReaderTester.loadCase();
        msgCase.initDataMap();
        var msgCasePar = JsonReaderTester.loadCasePar();
        var org = JsonReaderTester.loadOrg();
        org.initDataMap();
        var provider = JsonReaderTester.loadProvider();
        for( int i = 0; i < provider.size(); i++) {
            provider.get(i).initDataMap();
        }

        EcrSelectedRecord selectedRecord = new EcrSelectedRecord();
        selectedRecord.setMsgContainer(container);

        var paArr = new ArrayList<EcrMsgPatientDto>();
        paArr.add(patient);
        selectedRecord.setMsgPatients(paArr);

        EcrSelectedCase selectedCase = new EcrSelectedCase();
        selectedCase.setMsgCase(msgCase);
        selectedCase.setMsgCaseParticipants(msgCasePar);

        var caseSelectedArr = new ArrayList<EcrSelectedCase>();
        caseSelectedArr.add(selectedCase);
        selectedRecord.setMsgCases(caseSelectedArr);

        selectedRecord.setMsgXmlAnswers(new ArrayList<>());

        selectedRecord.setMsgProviders(provider);

        var orgArr = new ArrayList<EcrMsgOrganizationDto>();
        orgArr.add(org);
        selectedRecord.setMsgOrganizations(orgArr);



        return selectedRecord;
    }

    public EcrSelectedRecord GetSelectedEcrRecord() {
        EcrSelectedRecord selectedRecord = null;
        EcrMsgContainerDto msgContainer = this.ecrMsgQueryRepository.FetchMsgContainerForApplicableEcr();

        if (msgContainer != null && msgContainer.getMsgContainerUid() != null) {
            selectedRecord = new EcrSelectedRecord();
            this.ecrMsgQueryRepository.UpdateMatchEcrRecordForProcessing(msgContainer.getMsgContainerUid());
            List<EcrMsgPatientDto> msgPatients = this.ecrMsgQueryRepository.FetchMsgPatientForApplicableEcr(msgContainer.getMsgContainerUid());

            List<EcrSelectedCase> selectedMsgCases = new ArrayList<>();
            List<EcrMsgXmlAnswerDto> msgXmlAnswers = new ArrayList<>();
            List<EcrMsgCaseDto> msgCases = this.ecrMsgQueryRepository.FetchMsgCaseForApplicableEcr(msgContainer.getMsgContainerUid());
            for(var item : msgCases) {

                EcrSelectedCase selectedCase = new EcrSelectedCase();
                List<EcrMsgCaseParticipantDto> msgCaseParticipants = this.ecrMsgQueryRepository.FetchMsgCaseParticipantForApplicableEcr(
                        msgContainer.getMsgContainerUid(), item.getInvLocalId()
                );

                List<EcrMsgCaseAnswerDto> msgCaseAnswers = this.ecrMsgQueryRepository.FetchMsgCaseAnswerForApplicableEcr(
                        msgContainer.getMsgContainerUid(), item.getInvLocalId()
                );

                List<EcrMsgCaseAnswerRepeatDto> msgCaseAnswerRepeats = this.ecrMsgQueryRepository.FetchMsgCaseAnswerRepeatForApplicableEcr(
                        msgContainer.getMsgContainerUid(), item.getInvLocalId()
                );
                selectedCase.setMsgCase(item);
                selectedCase.setMsgCaseParticipants(msgCaseParticipants);
                selectedCase.setMsgCaseAnswers(msgCaseAnswers);
                selectedCase.setMsgCaseAnswerRepeats(msgCaseAnswerRepeats);
                selectedMsgCases.add(selectedCase);

                List<EcrMsgXmlAnswerDto> msgXmlAnswer = this.ecrMsgQueryRepository.FetchMsgXmlAnswerForApplicableEcr(msgContainer.getMsgContainerUid(), item.getInvLocalId());
                msgXmlAnswers.addAll(msgXmlAnswer);
            }

            List<EcrMsgProviderDto> msgProviders = this.ecrMsgQueryRepository.FetchMsgProviderForApplicableEcr(msgContainer.getMsgContainerUid());
            List<EcrMsgOrganizationDto> msgOrganizations = this.ecrMsgQueryRepository.FetchMsgOrganizationForApplicableEcr(msgContainer.getMsgContainerUid());
            List<EcrMsgPlaceDto> msgPlaces = this.ecrMsgQueryRepository.FetchMsgPlaceForApplicableEcr(msgContainer.getMsgContainerUid());

            List<EcrSelectedInterview> selectedInterviews = new ArrayList<>();
            List<EcrMsgInterviewDto> msgInterviews = this.ecrMsgQueryRepository.FetchMsgInterviewForApplicableEcr(msgContainer.getMsgContainerUid());
            for(var item : msgInterviews) {
                EcrSelectedInterview selectedInterview = new EcrSelectedInterview();

                List<EcrMsgInterviewProviderDto> msgInterviewProviders = this.ecrMsgQueryRepository.FetchMsgInterviewProviderForApplicableEcr(
                        msgContainer.getMsgContainerUid(), item.getIxsLocalId());

                List<EcrMsgInterviewAnswerDto> msgInterviewAnswers = this.ecrMsgQueryRepository.FetchMsgInterviewAnswerForApplicableEcr(
                        msgContainer.getMsgContainerUid(), item.getIxsLocalId());

                List<EcrMsgInterviewAnswerRepeatDto> msgInterviewAnswerRepeats = this.ecrMsgQueryRepository.FetchMsgInterviewAnswerRepeatForApplicableEcr(
                        msgContainer.getMsgContainerUid(), item.getIxsLocalId());

                selectedInterview.setMsgInterview(item);
                selectedInterview.setMsgInterviewProviders(msgInterviewProviders);
                selectedInterview.setMsgInterviewAnswers(msgInterviewAnswers);
                selectedInterview.setMsgInterviewAnswerRepeats(msgInterviewAnswerRepeats);
                selectedInterviews.add(selectedInterview);
            }

            List<EcrSelectedTreatment> selectedTreatments = new ArrayList<>();
            List<EcrMsgTreatmentDto> msgTreatments = this.ecrMsgQueryRepository.FetchMsgTreatmentForApplicableEcr(msgContainer.getMsgContainerUid());
            for(var item : msgTreatments) {
                EcrSelectedTreatment selectedTreatment = new EcrSelectedTreatment();

                List<EcrMsgTreatmentProviderDto> ecrMsgTreatmentProviders = this.ecrMsgQueryRepository.FetchMsgTreatmentProviderForApplicableEcr(
                        msgContainer.getMsgContainerUid());

                List<EcrMsgTreatmentOrganizationDto> ecrMsgTreatmentOrganizations = this.ecrMsgQueryRepository.FetchMsgTreatmentOrganizationForApplicableEcr(
                        msgContainer.getMsgContainerUid()
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
