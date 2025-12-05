package com.pm.authservice.config;
import org.springframework.web.reactive.function.client.WebClient;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebClientConfig {
    @Bean("patientClient")
    public WebClient patientWebClient() {
        return WebClient.builder()
                .baseUrl("http://patient-service:4000")
                .build();
    }
}
