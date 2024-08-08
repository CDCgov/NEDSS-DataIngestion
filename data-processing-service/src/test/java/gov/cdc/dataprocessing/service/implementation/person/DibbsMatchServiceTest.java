package gov.cdc.dataprocessing.service.implementation.person;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.io.IOException;
import java.net.http.HttpResponse;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;


@ExtendWith(MockitoExtension.class)
 class DibbsMatchServiceTest {

  private final String converterApiUrl = "http://example.com/";

  @InjectMocks
  private DibbsMatchService dibbsMatchService;

  @BeforeEach
  public void setUp() {
    ReflectionTestUtils.setField(dibbsMatchService, "converterApiUrl", converterApiUrl);
  }

  @Test
   void testCallDIBBSConverterApiSuccess() throws Exception {
    PersonContainer personContainer = new PersonContainer();
    DibbsMatchService spyService = spy(dibbsMatchService);
    doReturn(true).when(spyService).handleResponse(any(HttpResponse.class));
    boolean result = spyService.callDIBBSConverterApi(personContainer);
    assertTrue(result);
    verify(spyService).handleResponse(any(HttpResponse.class));
  }

  @Test
   void testCallDIBBSConverterApiNoMatch() throws Exception {
    PersonContainer personContainer = new PersonContainer();
    DibbsMatchService spyService = spy(dibbsMatchService);
    doReturn(false).when(spyService).handleResponse(any(HttpResponse.class));
    boolean result = spyService.callDIBBSConverterApi(personContainer);
    assertFalse(result);
    verify(spyService).handleResponse(any(HttpResponse.class));
  }

  @Test
   void testCallDIBBSConverterApiError() throws Exception {
    PersonContainer personContainer = new PersonContainer();
    DibbsMatchService spyService = spy(dibbsMatchService);
    doThrow(new IOException("API Error")).when(spyService).handleResponse(any(HttpResponse.class));
    assertThrows(IOException.class, () -> {
      spyService.callDIBBSConverterApi(personContainer);
    });
    verify(spyService).handleResponse(any(HttpResponse.class));
  }
}
