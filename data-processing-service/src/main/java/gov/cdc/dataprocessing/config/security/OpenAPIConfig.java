package gov.cdc.dataprocessing.config.security;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.util.List;

@Configuration
public class OpenAPIConfig {
    @Value("${dp-server.host}")
    private String serverhost;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Bean
    public OpenAPI myOpenAPI() throws Exception{
        String serverUrl = "";
        String scheme="";
        if(serverhost!=null && !serverhost.contains("localhost")){
            scheme="https";
        }else{
            scheme="http";
        }
        URI uriBuilder = new URIBuilder()
                .setScheme(scheme)
                .setHost(serverhost)
                .setPath(contextPath)
                .build();
        serverUrl=uriBuilder.toString();

        Server server = new Server();
        server.setUrl(serverUrl);
        server.setDescription("Server URL");

        Contact contact = new Contact();
        contact.setEmail("rtiservice@cdc.com");
        contact.setName("Real Time Ingestion");

        Info info = new Info()
                .title("Real Time Ingestion API")
                .version("1.0")
                .contact(contact)
                .description("This API exposes endpoints to manage Real Time Ingestion Services.");

        Components components=new Components().
                addSecuritySchemes("bearer-key",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).
                                scheme("bearer").bearerFormat("JWT").
                                description("JWT Token"));

        return new OpenAPI().info(info).servers(List.of(server)).components(components);
    }
}