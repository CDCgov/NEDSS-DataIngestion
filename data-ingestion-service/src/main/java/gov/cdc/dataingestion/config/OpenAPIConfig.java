package gov.cdc.dataingestion.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8081");
        devServer.setDescription("Server URL in Local environment");

        Server dts1Server = new Server();
        dts1Server.setUrl("https://dataingestion.dts1.nbspreview.com");
        dts1Server.setDescription("Server URL in DTS environment");

        Server int1Server = new Server();
        int1Server.setUrl("https://dataingestion.int1.nbspreview.com");
        int1Server.setDescription("Server URL in INT environment");

        Contact contact = new Contact();
        contact.setEmail("dataingestionservice@cdc.com");
        contact.setName("Data Ingestion Service");
        contact.setUrl("https://localhost:8080");

        Info info = new Info()
                .title("Data Ingestion API")
                .version("1.0")
                .contact(contact)
                .description("This API exposes endpoints to manage Data Ingestion Service.");

        Components components=new Components().
                    addSecuritySchemes("bearer-key",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).
                                scheme("bearer").bearerFormat("JWT").
                                description("JWT Token"));

        return new OpenAPI().info(info).servers(List.of(devServer, dts1Server,int1Server)).components(components);
    }
}
