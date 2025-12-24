package com.example.smartplanner.dto.integration;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpenMeteoCurrentDto(
        @JsonProperty("current_weather") CurrentWeather currentWeather
) {
    public record CurrentWeather(
            double temperature,
            double windspeed,
            @JsonProperty("weathercode") int weatherCode
    ) {}
}
