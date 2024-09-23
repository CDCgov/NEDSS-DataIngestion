package gov.cdc.dataingestion.rawmessage.controller;

import gov.cdc.dataingestion.nbs.ecr.service.CdaMapper;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedCase;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedRecord;
import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgContainerDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgPatientDto;
import gov.cdc.dataingestion.nbs.services.EcrMsgQueryService;
import gov.cdc.dataingestion.nbs.services.NbsRepositoryServiceProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(EcrReportsController.class)
@ActiveProfiles("test")
class EcrReportsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EcrMsgQueryService ecrMsgQueryService;

    @MockBean
    private CdaMapper cdaMapper;

    @MockBean
    private NbsRepositoryServiceProvider nbsRepositoryServiceProvider;

    @Test
    void testSaveEcrMessagePHC236() throws Exception {
        String documentTypeCode = "PHC236";
        String payload = "dummyPayload";

        EcrSelectedRecord resultDto = new EcrSelectedRecord();
        EcrMsgContainerDto ecrMsgContainerDto = new EcrMsgContainerDto();
        ecrMsgContainerDto.setMsgContainerUid(1);
        ecrMsgContainerDto.setNbsInterfaceUid(1234);
        resultDto.setMsgContainer(ecrMsgContainerDto);
        resultDto.setMsgCases(Collections.singletonList(new EcrSelectedCase()));
        resultDto.setMsgPatients(Collections.singletonList(new EcrMsgPatientDto()));

        when(ecrMsgQueryService.getSelectedEcrRecord()).thenReturn(resultDto);
        when(cdaMapper.tranformSelectedEcrToCDAXml(resultDto)).thenReturn("transformedXml");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/ecrs/{document-type-code}", documentTypeCode)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(payload)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    @Test
    void testSaveEcrMessageNonPHC236() throws Exception {
        String documentTypeCode = "notPHC236";
        String payload = "dummyPayload";

        EcrSelectedRecord resultDto = new EcrSelectedRecord();
        EcrMsgContainerDto ecrMsgContainerDto = new EcrMsgContainerDto();
        ecrMsgContainerDto.setMsgContainerUid(1);
        ecrMsgContainerDto.setNbsInterfaceUid(1234);
        resultDto.setMsgContainer(ecrMsgContainerDto);
        resultDto.setMsgCases(Collections.singletonList(new EcrSelectedCase()));
        resultDto.setMsgPatients(Collections.singletonList(new EcrMsgPatientDto()));

        when(ecrMsgQueryService.getSelectedEcrRecord()).thenReturn(resultDto);
        when(cdaMapper.tranformSelectedEcrToCDAXml(resultDto)).thenReturn("transformedXml");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/ecrs/{document-type-code}", documentTypeCode)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(payload)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}