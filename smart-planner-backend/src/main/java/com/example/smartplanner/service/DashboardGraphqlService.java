package com.example.smartplanner.service;

import com.example.smartplanner.dto.graphql.*;
import com.example.smartplanner.dto.integration.OpenMeteoCurrentDto;
import com.example.smartplanner.dto.integration.QuotableQuoteDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardGraphqlService {

    private final ReportService reportService;
    private final QuoteIntegrationService quotes;
    private final WeatherIntegrationService weather;

    public DashboardGraphqlService(ReportService reportService,
                                   QuoteIntegrationService quotes,
                                   WeatherIntegrationService weather) {
        this.reportService = reportService;
        this.quotes = quotes;
        this.weather = weather;
    }

    public List<TopTagGql> topTags(Long projectId, int limit) {
        return reportService.topTags(projectId, limit).stream()
                .map(r -> new TopTagGql(r.tagName(), r.usageCount()))
                .toList();
    }

    public List<TasksByPriorityGql> tasksByPriority(Long projectId) {
        return reportService.tasksByPriority(projectId).stream()
                .map(r -> new TasksByPriorityGql(r.projectId(), r.priority(), r.taskCount()))
                .toList();
    }

    public QuoteGql dailyQuote(Long userId) {
        QuotableQuoteDto q = quotes.dailyQuote(userId);
        return new QuoteGql(q.content(), q.author());
    }

    public WeatherGql currentWeather(Long userId, Double latitude, Double longitude) {
        if (latitude == null || longitude == null) return null;

        OpenMeteoCurrentDto dto = weather.currentWeather(userId, latitude, longitude);
        if (dto == null || dto.currentWeather() == null) return new WeatherGql(null, null, null);

        var cw = dto.currentWeather();
        return new WeatherGql(cw.temperature(), cw.windspeed(), cw.weatherCode());
    }

    public DashboardGql dashboard(Long userId, Long projectId, int topTagsLimit, Double latitude, Double longitude) {
        List<TopTagGql> tags = topTags(projectId, topTagsLimit);
        List<TasksByPriorityGql> byPriority = tasksByPriority(projectId);
        QuoteGql quote = dailyQuote(userId);
        WeatherGql w = currentWeather(userId, latitude, longitude);
        return new DashboardGql(tags, byPriority, quote, w);
    }
}
