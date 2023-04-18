package gov.cdc.dataingestion.it;

import gov.cdc.dataingestion.rawmessage.dto.RawERLDto;
import gov.cdc.dataingestion.rawmessage.service.RawELRService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Collections;


@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = RawMessageServiceIT.DataSourceInitializer.class)
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
public class RawMessageServiceIT {

    @Autowired
    private RawELRService rawELRService;

    private static final DockerImageName taggedImageName = DockerImageName.parse("mcr.microsoft.com/azure-sql-edge")
            .withTag("latest")
            .asCompatibleSubstituteFor("mcr.microsoft.com/mssql/server");
    @Container
    private static final MSSQLServerContainer database = new MSSQLServerContainer<>(taggedImageName)
            .withTmpFs(Collections.singletonMap("/testtmpfs", "rw"))
            .withReuse(true)
            .acceptLicense();

    public static class DataSourceInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                    applicationContext,
                    "spring.test.database.replace=none",
                    "spring.datasource.url=" + database.getJdbcUrl(),
                    "spring.datasource.username=" + database.getUsername(),
                    "spring.datasource.password=" + database.getPassword(),
                    "spring.datasource.nbs.url=" + database.getJdbcUrl(),
                    "spring.datasource.nbs.username=" + database.getUsername(),
                    "spring.datasource.nbs.password=" + database.getPassword()
            );
        }
    }


    @Test
    public void saveRawMessage(){

        RawERLDto entity = new RawERLDto();

        entity.setId("Test123");
        entity.setPayload("Content");

        String newEntityId = rawELRService.submission(entity);
        RawERLDto entityRetrieved = rawELRService.getById(newEntityId);
        Assertions.assertThat(newEntityId).isEqualTo(entityRetrieved.getId());
    }


}
