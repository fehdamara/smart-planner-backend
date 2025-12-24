package com.example.smartplanner.service;

import com.example.smartplanner.dto.integration.OpenMeteoCurrentDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class WeatherIntegrationService {

    private final RestClient weatherClient;
    private final ExternalCacheService cache;
    private final ObjectMapper om;

    public WeatherIntegrationService(RestClient weatherRestClient, ExternalCacheService cache, ObjectMapper om) {
        this.weatherClient = weatherRestClient;
        this.cache = cache;
        this.om = om;
    }

    public OpenMeteoCurrentDto currentWeather(Long userId, double latitude, double longitude) {
        String key = "weather:current:" + latitude + ":" + longitude;
        String cached = cache.getCachedPayloadOrNull(userId, key);

        try {
            if (cached != null) {
                return om.readValue(cached, OpenMeteoCurrentDto.class);
            }

            OpenMeteoCurrentDto fresh = weatherClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/forecast")
                            .queryParam("latitude", latitude)
                            .queryParam("longitude", longitude)
                            .queryParam("current_weather", "true")
                            .build())
                    .retrieve()
                    .body(OpenMeteoCurrentDto.class);

            cache.savePayload(userId, key, om.writeValueAsString(fresh));
            return fresh;
        } catch (Exception e) {
            // fallback safe object
            return new OpenMeteoCurrentDto(null);
        }
    }
}
