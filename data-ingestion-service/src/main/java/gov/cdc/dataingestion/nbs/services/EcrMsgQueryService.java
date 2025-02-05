package gov.cdc.dataingestion.nbs.services;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.ecr.resolver.EcrMsgContainerResolver;
import gov.cdc.dataingestion.nbs.repository.IEcrMsgQueryRepository;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedCase;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedInterview;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedRecord;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedTreatment;
import gov.cdc.dataingestion.nbs.repository.model.dto.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EcrMsgQueryService {
    private final IEcrMsgQueryRepository ecrMsgQueryRepository;
    private final EcrMsgContainerResolver ecrMsgContainerResolver;
    private final Integer batchSize;

    public EcrMsgQueryService(
            final IEcrMsgQueryRepository ecrMsgQueryRepository,
            final EcrMsgContainerResolver ecrMsgContainerResolver,
            @Value("${ecr.processing.batchsize:100}") final Integer batchSize) {
        this.ecrMsgQueryRepository = ecrMsgQueryRepository;
        this.ecrMsgContainerResolver = ecrMsgContainerResolver;
        this.batchSize = batchSize;
    }

    public List<EcrSelectedRecord> getSelectedEcrRecord() throws EcrCdaXmlException {
        List<EcrSelectedRecord> selectedRecords = new ArrayList<>();
        List<EcrMsgContainerDto> msgContainers = this.ecrMsgContainerResolver.resolve(batchSize)
                .stream()
                .filter(c -> c != null && c.msgContainerUid() != null)
                .toList();

        for (EcrMsgContainerDto msgContainer : msgContainers) {
            EcrSelectedRecord selectedRecord = new EcrSelectedRecord();
            selectedRecord.setMsgContainer(msgContainer);
            final Integer containerUid = msgContainer.msgContainerUid();

            // Sets the data_migration_status = 2
            this.ecrMsgQueryRepository.updateMatchEcrRecordForProcessing(containerUid);

            selectedRecord.setMsgPatients(ecrMsgQueryRepository.fetchMsgPatientForApplicableEcr(containerUid));
            setMsgCaseAndAnswers(selectedRecord, containerUid);
            selectedRecord.setMsgProviders(ecrMsgQueryRepository.fetchMsgProviderForApplicableEcr(containerUid));
            selectedRecord.setMsgOrganizations(
                    ecrMsgQueryRepository.fetchMsgOrganizationForApplicableEcr(containerUid));
            selectedRecord.setMsgPlaces(ecrMsgQueryRepository.fetchMsgPlaceForApplicableEcr(containerUid));
            setMsgInterviews(selectedRecord, containerUid);
            setTreatments(selectedRecord, containerUid);

            selectedRecords.add(selectedRecord);
        }

        return selectedRecords;
    }

    private final void setMsgCaseAndAnswers(
            EcrSelectedRecord selectedRecord,
            final Integer containerUid) throws EcrCdaXmlException {
        List<EcrSelectedCase> selectedMsgCases = new ArrayList<>();
        List<EcrMsgXmlAnswerDto> msgXmlAnswers = new ArrayList<>();
        List<EcrMsgCaseDto> msgCases = ecrMsgQueryRepository.fetchMsgCaseForApplicableEcr(containerUid);

        for (EcrMsgCaseDto item : msgCases) {
            EcrSelectedCase selectedCase = new EcrSelectedCase();
            List<EcrMsgCaseParticipantDto> msgCaseParticipants = ecrMsgQueryRepository
                    .fetchMsgCaseParticipantForApplicableEcr(
                            containerUid,
                            item.getInvLocalId());

            List<EcrMsgCaseAnswerDto> msgCaseAnswers = ecrMsgQueryRepository
                    .fetchMsgCaseAnswerForApplicableEcr(
                            containerUid,
                            item.getInvLocalId());

            List<EcrMsgCaseAnswerDto> msgCaseAnswerRepeats = ecrMsgQueryRepository
                    .fetchMsgCaseAnswerRepeatForApplicableEcr(
                            containerUid,
                            item.getInvLocalId());

            selectedCase.setMsgCase(item);
            selectedCase.setMsgCaseParticipants(msgCaseParticipants);
            selectedCase.setMsgCaseAnswers(msgCaseAnswers);
            selectedCase.setMsgCaseAnswerRepeats(msgCaseAnswerRepeats);

            selectedMsgCases.add(selectedCase);

            List<EcrMsgXmlAnswerDto> msgXmlAnswer = ecrMsgQueryRepository.fetchMsgXmlAnswerForApplicableEcr(
                    containerUid,
                    item.getInvLocalId());
            msgXmlAnswers.addAll(msgXmlAnswer);
        }

        selectedRecord.setMsgCases(selectedMsgCases);
        selectedRecord.setMsgXmlAnswers(msgXmlAnswers);
    }

    private final void setMsgInterviews(
            EcrSelectedRecord selectedRecord,
            final Integer containerUid) throws EcrCdaXmlException {
        List<EcrSelectedInterview> selectedInterviews = new ArrayList<>();
        List<EcrMsgInterviewDto> msgInterviews = this.ecrMsgQueryRepository
                .fetchMsgInterviewForApplicableEcr(containerUid);
        for (var item : msgInterviews) {
            EcrSelectedInterview selectedInterview = new EcrSelectedInterview();

            List<EcrMsgProviderDto> msgInterviewProviders = this.ecrMsgQueryRepository
                    .fetchMsgInterviewProviderForApplicableEcr(
                            containerUid, item.getIxsLocalId());

            List<EcrMsgCaseAnswerDto> msgInterviewAnswers = this.ecrMsgQueryRepository
                    .fetchMsgInterviewAnswerForApplicableEcr(
                            containerUid, item.getIxsLocalId());

            List<EcrMsgCaseAnswerDto> msgInterviewAnswerRepeats = this.ecrMsgQueryRepository
                    .fetchMsgInterviewAnswerRepeatForApplicableEcr(
                            containerUid, item.getIxsLocalId());

            selectedInterview.setMsgInterview(item);
            selectedInterview.setMsgInterviewProviders(msgInterviewProviders);
            selectedInterview.setMsgInterviewAnswers(msgInterviewAnswers);
            selectedInterview.setMsgInterviewAnswerRepeats(msgInterviewAnswerRepeats);
            selectedInterviews.add(selectedInterview);
        }

        selectedRecord.setMsgInterviews(selectedInterviews);
    }

    private final void setTreatments(
            EcrSelectedRecord selectedRecord,
            final Integer containerUid) throws EcrCdaXmlException {
        List<EcrSelectedTreatment> selectedTreatments = new ArrayList<>();
        List<EcrMsgTreatmentDto> msgTreatments = this.ecrMsgQueryRepository
                .fetchMsgTreatmentForApplicableEcr(containerUid);
        for (EcrMsgTreatmentDto treatment : msgTreatments) {
            EcrSelectedTreatment selectedTreatment = new EcrSelectedTreatment();
            selectedTreatment.setMsgTreatment(treatment);

            selectedTreatment.setMsgTreatmentProviders(ecrMsgQueryRepository
                    .fetchMsgTreatmentProviderForApplicableEcr(
                            containerUid, treatment.getTrtLocalId()));

            selectedTreatment.setMsgTreatmentOrganizations(ecrMsgQueryRepository
                    .fetchMsgTreatmentOrganizationForApplicableEcr(
                            containerUid, treatment.getTrtLocalId()));

            selectedTreatments.add(selectedTreatment);
        }

        selectedRecord.setMsgTreatments(selectedTreatments);
    }
}
