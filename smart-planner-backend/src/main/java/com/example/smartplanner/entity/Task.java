package com.example.smartplanner.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "tasks")
@DiscriminatorValue("TASK")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Task extends Item {

    private Instant dueDate;

    @Column(nullable = false)
    private Integer priority; // 1..5

    private Integer estimatedMinutes;

    private Instant completedAt;
}
