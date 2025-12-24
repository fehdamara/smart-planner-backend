package com.example.smartplanner.service;

import com.example.smartplanner.dto.report.TopTagReportRow;
import com.example.smartplanner.dto.report.TasksByPriorityRow;
import com.example.smartplanner.repository.ItemRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {

    private final ItemRepository items;

    public ReportService(ItemRepository items) {
        this.items = items;
    }

    public List<TopTagReportRow> topTags(Long projectId, int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), 50);
        return items.topTags(projectId, PageRequest.of(0, safeLimit));
    }

    public List<TasksByPriorityRow> tasksByPriority(Long projectId) {
        return items.tasksByPriority(projectId);
    }
}
