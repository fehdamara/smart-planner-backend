package com.example.smartplanner.dto.graphql;

import java.util.List;

public record DashboardGql(
        List<TopTagGql> topTags,
        List<TasksByPriorityGql> tasksByPriority,
        QuoteGql quote,
        WeatherGql weather
) {}
