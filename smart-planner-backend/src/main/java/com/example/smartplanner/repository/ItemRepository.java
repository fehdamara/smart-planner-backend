package com.example.smartplanner.repository;

import com.example.smartplanner.dto.report.TopTagReportRow;
import com.example.smartplanner.dto.report.TasksByPriorityRow;
import com.example.smartplanner.entity.Item;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    // REPORT 1: Top tags
    @Query("""
    SELECT new com.example.smartplanner.dto.report.TopTagReportRow(t.name, COUNT(i))
    FROM Item i
    JOIN i.tags t
    WHERE (:projectId IS NULL OR i.project.id = :projectId)
    GROUP BY t.name
    ORDER BY COUNT(i) DESC
  """)
    List<TopTagReportRow> topTags(@Param("projectId") Long projectId, Pageable pageable);

    // REPORT 2: Tasks by priority
    @Query("""
    SELECT new com.example.smartplanner.dto.report.TasksByPriorityRow(t.project.id, t.priority, COUNT(t))
    FROM Task t
    WHERE (:projectId IS NULL OR t.project.id = :projectId)
    GROUP BY t.project.id, t.priority
    ORDER BY t.project.id ASC, t.priority ASC
  """)
    List<TasksByPriorityRow> tasksByPriority(@Param("projectId") Long projectId);
}
