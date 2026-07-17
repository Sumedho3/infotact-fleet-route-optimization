package com.infotact.fleet.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPIBlueprintDefinition() {

        // 1. Define target infrastructure server environments
        Server localDevelopmentServer = new Server();
        localDevelopmentServer.setUrl("http://localhost:8081");
        localDevelopmentServer.setDescription(
                "Local Engineering Staging Environment (In-Memory H2 Grid)");

        // 2. Contact metadata
        Contact technicalSupportContact = new Contact();
        technicalSupportContact.setName(
                "Infotact Fleet Logistics Core Platform Team");
        technicalSupportContact.setEmail(
                "backend-engineers@infotact.com");

        // 3. API metadata
        Info apiMetadataRegistry = new Info()
                .title("Infotact Fleet Route Optimization System Engine API")
                .version("v1.0.0")
                .description(
                        "Automated backend microservice designed for processing high-throughput "
                                + "vehicle asset routing configurations, traveling salesperson coordinate "
                                + "sequencing matrices, and cascading real-time transactional dispatch states.")
                .contact(technicalSupportContact)
                .license(new License()
                        .name("Apache 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0"));

        // 4. OpenAPI bean
        return new OpenAPI()
                .info(apiMetadataRegistry)
                .servers(List.of(localDevelopmentServer));
    }
}