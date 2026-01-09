package com.example.smartplanner.controller.graphql;

import com.example.smartplanner.dto.graphql.*;
import com.example.smartplanner.security.UserPrincipal;
import com.example.smartplanner.service.DashboardGraphqlService;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class DashboardGraphqlController {

    private final DashboardGraphqlService service;

    public DashboardGraphqlController(DashboardGraphqlService service) {
        this.service = service;
    }

    @QueryMapping
    public DashboardGql dashboard(
            @AuthenticationPrincipal UserPrincipal principal,
            @Argument Long projectId,
            @Argument Integer topTagsLimit,
            @Argument Double latitude,
            @Argument Double longitude
    ) {
        int limit = (topTagsLimit == null) ? 10 : topTagsLimit;
        return service.dashboard(principal.getId(), projectId, limit, latitude, longitude);
    }

    @QueryMapping
    public List<TopTagGql> topTags(
            @Argument Long projectId,
            @Argument Integer limit
    ) {
        int safeLimit = (limit == null) ? 10 : limit;
        return service.topTags(projectId, safeLimit);
    }

    @QueryMapping
    public List<TasksByPriorityGql> tasksByPriority(@Argument Long projectId) {
        return service.tasksByPriority(projectId);
    }

    @QueryMapping
    public QuoteGql dailyQuote(@AuthenticationPrincipal UserPrincipal principal) {
        return service.dailyQuote(principal.getId());
    }

    @QueryMapping
    public WeatherGql currentWeather(
            @AuthenticationPrincipal UserPrincipal principal,
            @Argument Double latitude,
            @Argument Double longitude
    ) {
        return service.currentWeather(principal.getId(), latitude, longitude);
    }
}
