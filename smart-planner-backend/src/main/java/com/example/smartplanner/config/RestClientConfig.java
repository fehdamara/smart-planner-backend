package com.example.smartplanner.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    RestClient weatherRestClient(@Value("${integrations.weather.baseUrl}") String baseUrl) {
        return RestClient.builder().baseUrl(baseUrl).build();
    }

    @Bean
    RestClient quoteRestClient(@Value("${integrations.quote.baseUrl}") String baseUrl) {
        return RestClient.builder().baseUrl(baseUrl).build();
    }
}
