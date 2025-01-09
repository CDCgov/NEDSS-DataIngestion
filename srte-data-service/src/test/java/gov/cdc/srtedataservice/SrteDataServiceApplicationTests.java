package gov.cdc.srtedataservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import gov.cdc.srtedataservice.containers.UseNbsDatabaseContainer;
import gov.cdc.srtedataservice.controller.Controller;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@UseNbsDatabaseContainer
class SrteDataServiceApplicationTests {

  @Autowired
  private Controller controller;

  @Test
  void contextLoads() {
    assertThat(controller).isNotNull();
  }

}
