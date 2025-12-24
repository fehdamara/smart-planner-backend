package com.example.smartplanner.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "external_cache",
        indexes = @Index(name = "idx_cache_user_key", columnList = "user_id, cacheKey")
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ExternalCache {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 200)
    private String cacheKey;

    @Column(nullable = false, length = 12000)
    private String payloadJson;

    @Column(nullable = false)
    private Instant fetchedAt;

    @PrePersist
    void prePersist() {
        if (fetchedAt == null) fetchedAt = Instant.now();
    }
}
