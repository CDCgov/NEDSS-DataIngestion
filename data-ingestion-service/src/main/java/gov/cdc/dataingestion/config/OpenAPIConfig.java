package gov.cdc.dataingestion.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

import java.util.List;


@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("https://dataingestion.datateam-cdc-nbs.eqsandbox.com");
        devServer.setDescription("Server URL in Development environment");

        Server prodServer = new Server();
        prodServer.setUrl("https://dataingestion.datateam-cdc-nbs.eqsandbox.com");
        prodServer.setDescription("Server URL in Production environment");

        Contact contact = new Contact();
        contact.setEmail("dataingestionservice@cdc.com");
        contact.setName("Data Ingestion Service");
        contact.setUrl("https://localhost:8080");

        Info info = new Info()
                .title("Data Ingestion API")
                .version("1.0")
                .contact(contact)
                .description("This API exposes endpoints to manage Data Ingestion Service.");

        return new OpenAPI().info(info).servers(List.of(devServer, prodServer));
    }
}
