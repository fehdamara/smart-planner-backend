package com.example.smartplanner.controller;

import com.example.smartplanner.dto.report.TopTagReportRow;
import com.example.smartplanner.dto.report.TasksByPriorityRow;
import com.example.smartplanner.service.ReportService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manager/reports")
public class ReportController {

    private final ReportService reports;

    public ReportController(ReportService reports) {
        this.reports = reports;
    }

    // GET /manager/reports/top-tags?projectId=1&limit=10
    @GetMapping("/top-tags")
    public List<TopTagReportRow> topTags(
            @RequestParam(required = false) Long projectId,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return reports.topTags(projectId, limit);
    }

    // GET /manager/reports/tasks-by-priority?projectId=1
    @GetMapping("/tasks-by-priority")
    public List<TasksByPriorityRow> tasksByPriority(
            @RequestParam(required = false) Long projectId
    ) {
        return reports.tasksByPriority(projectId);
    }
}