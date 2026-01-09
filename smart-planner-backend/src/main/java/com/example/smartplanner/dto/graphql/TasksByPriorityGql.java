package com.example.smartplanner.dto.graphql;

public record TasksByPriorityGql(Long projectId, Integer priority, long taskCount) {}
