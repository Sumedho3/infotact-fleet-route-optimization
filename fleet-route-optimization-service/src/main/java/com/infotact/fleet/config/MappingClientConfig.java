package com.infotact.fleet.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration // 🎯 THE INFRASTRUCTURE HOOK: Registers structural beans into the Spring application context
public class MappingClientConfig {

    @Value("${app.external.map-service.base-url}")
    private String baseUrl;

    @Value("${app.external.map-service.api-key}")
    private String apiKey;
    
    
    /**
     * 🎯 THE FALLBACK FACTORY HOOK:
     * Explicitly provides a WebClient.Builder bean if the Spring Boot 
     * auto-configuration wrapper is bypassed in hybrid environments.
     */
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
    

    /**
     * Constructs a centralized, thread-safe, and non-blocking WebClient instance.
     * This client pre-configures base connection paths and authorization headers 
     * so outbound service instances can consume external APIs easily.
     */
    @Bean
    public WebClient routingWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                // Assuming bearer tokens or query authorization parameters for enterprise setup;
                // Pre-attaching custom API key header as an architectural placeholder
                .defaultHeader("X-API-KEY", apiKey)
                .build();
    }
}