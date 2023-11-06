package gov.cdc.dataingestion.ecr.service;

import gov.cdc.dataingestion.deadletter.service.ElrDeadLetterService;
import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.repository.IEcrMsgQueryRepository;
import gov.cdc.dataingestion.nbs.repository.model.dto.*;
import gov.cdc.dataingestion.nbs.services.EcrMsgQueryService;
import gov.cdc.dataingestion.nbs.services.interfaces.IEcrMsgQueryService;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import static org.mockito.Mockito.when;


class EcrMsgQueryServiceTest {
    @Mock
    private IEcrMsgQueryRepository ecrMsgQueryRepository;
    @InjectMocks
    private EcrMsgQueryService target;

    @BeforeEach
    public void setUpEach() {
        MockitoAnnotations.openMocks(this);
        target = new EcrMsgQueryService(ecrMsgQueryRepository);
    }

    @Test
    void getSelectedEcrRecord_Test() throws EcrCdaXmlException {
        EcrMsgContainerDto container = new EcrMsgContainerDto();
        container.setMsgContainerUid(1);
        container.setInvLocalId("1");
        container.setNbsInterfaceUid(1);
        container.setReceivingSystem("test");
        container.setOngoingCase("test");
        container.setVersionCtrNbr(1);
        container.setDataMigrationStatus(1);
        when(ecrMsgQueryRepository.fetchMsgContainerForApplicableEcr()).thenReturn(container);

        EcrMsgPatientDto patient = new EcrMsgPatientDto();
        List<EcrMsgPatientDto> patientList = new ArrayList<>();
        patientList.add(patient);
        when(ecrMsgQueryRepository.fetchMsgPatientForApplicableEcr(container.getMsgContainerUid())).thenReturn(patientList);

        EcrMsgCaseDto cases = new EcrMsgCaseDto();
        cases.setInvLocalId("test");
        List<EcrMsgCaseDto> caseList = new ArrayList<>();
        caseList.add(cases);
        when(ecrMsgQueryRepository.fetchMsgCaseForApplicableEcr(container.getMsgContainerUid())).thenReturn(caseList);

        EcrMsgCaseParticipantDto casePar = new EcrMsgCaseParticipantDto();
        List<EcrMsgCaseParticipantDto> caseParList = new ArrayList<>();
        caseParList.add(casePar);
        when(ecrMsgQueryRepository.fetchMsgCaseParticipantForApplicableEcr(container.getMsgContainerUid(),
                cases.getInvLocalId())).thenReturn(caseParList);

        EcrMsgCaseAnswerDto caseAns = new EcrMsgCaseAnswerDto();
        List<EcrMsgCaseAnswerDto> caseAnsList = new ArrayList<>();
        caseAnsList.add(caseAns);
        when(ecrMsgQueryRepository.fetchMsgCaseAnswerForApplicableEcr(container.getMsgContainerUid(),
                cases.getInvLocalId())).thenReturn(caseAnsList);

        EcrMsgCaseAnswerDto caseAnsRe = new EcrMsgCaseAnswerDto();
        List<EcrMsgCaseAnswerDto> caseAnsReList = new ArrayList<>();
        caseAnsReList.add(caseAnsRe);
        when(ecrMsgQueryRepository.fetchMsgCaseAnswerRepeatForApplicableEcr(container.getMsgContainerUid(),
                cases.getInvLocalId())).thenReturn(caseAnsReList);

        EcrMsgXmlAnswerDto xmlAns = new EcrMsgXmlAnswerDto();
        List<EcrMsgXmlAnswerDto> xmlAnsList = new ArrayList<>();
        xmlAnsList.add(xmlAns);
        when(ecrMsgQueryRepository.fetchMsgXmlAnswerForApplicableEcr(container.getMsgContainerUid(),
                cases.getInvLocalId())).thenReturn(xmlAnsList);

        EcrMsgProviderDto provider = new EcrMsgProviderDto();
        List<EcrMsgProviderDto> providerList = new ArrayList<>();
        providerList.add(provider);
        when(ecrMsgQueryRepository.fetchMsgProviderForApplicableEcr(container.getMsgContainerUid()))
                .thenReturn(providerList);

        EcrMsgOrganizationDto org = new EcrMsgOrganizationDto();
        List<EcrMsgOrganizationDto> orgList = new ArrayList<>();
        orgList.add(org);
        when(ecrMsgQueryRepository.fetchMsgOrganizationForApplicableEcr(container.getMsgContainerUid()))
                .thenReturn(orgList);

        EcrMsgPlaceDto plc = new EcrMsgPlaceDto();
        List<EcrMsgPlaceDto> plcList = new ArrayList<>();
        plcList.add(plc);
        when(ecrMsgQueryRepository.fetchMsgPlaceForApplicableEcr(container.getMsgContainerUid()))
                .thenReturn(plcList);

        EcrMsgInterviewDto inx = new EcrMsgInterviewDto();
        inx.setIxsLocalId("test");
        List<EcrMsgInterviewDto> inxList = new ArrayList<>();
        inxList.add(inx);
        when(ecrMsgQueryRepository.fetchMsgInterviewForApplicableEcr(container.getMsgContainerUid()))
                .thenReturn(inxList);

        EcrMsgProviderDto providerInx = new EcrMsgProviderDto();
        List<EcrMsgProviderDto> providerInxList = new ArrayList<>();
        providerInxList.add(providerInx);
        when(ecrMsgQueryRepository.fetchMsgInterviewProviderForApplicableEcr(container.getMsgContainerUid(),
                inx.getIxsLocalId())).thenReturn(providerInxList);

        EcrMsgCaseAnswerDto caseAnsInx = new EcrMsgCaseAnswerDto();
        List<EcrMsgCaseAnswerDto> caseAnsInxList = new ArrayList<>();
        caseAnsInxList.add(caseAnsInx);
        when(ecrMsgQueryRepository.fetchMsgInterviewAnswerForApplicableEcr(container.getMsgContainerUid(),
                inx.getIxsLocalId())).thenReturn(caseAnsInxList);

        EcrMsgCaseAnswerDto caseAnsReInx = new EcrMsgCaseAnswerDto();
        List<EcrMsgCaseAnswerDto> caseAnsReInxList = new ArrayList<>();
        caseAnsReInxList.add(caseAnsReInx);
        when(ecrMsgQueryRepository.fetchMsgInterviewAnswerRepeatForApplicableEcr(container.getMsgContainerUid(),
                inx.getIxsLocalId())).thenReturn(caseAnsReInxList);

        EcrMsgTreatmentDto treat = new EcrMsgTreatmentDto();
        List<EcrMsgTreatmentDto> treatList = new ArrayList<>();
        treatList.add(treat);
        when(ecrMsgQueryRepository.fetchMsgTreatmentForApplicableEcr(container.getMsgContainerUid()))
                .thenReturn(treatList);

        EcrMsgProviderDto providerTreat = new EcrMsgProviderDto();
        List<EcrMsgProviderDto> providerTreatList = new ArrayList<>();
        providerTreatList.add(providerTreat);
        when(ecrMsgQueryRepository.fetchMsgTreatmentProviderForApplicableEcr(container.getMsgContainerUid()))
                .thenReturn(providerTreatList);

        EcrMsgOrganizationDto orgTreat = new EcrMsgOrganizationDto();
        List<EcrMsgOrganizationDto> orgTreatList = new ArrayList<>();
        orgTreatList.add(orgTreat);
        when(ecrMsgQueryRepository.fetchMsgTreatmentOrganizationForApplicableEcr(container.getMsgContainerUid()))
                .thenReturn(orgTreatList);

        var result = target.getSelectedEcrRecord();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getMsgPatients().size());
    }

}
