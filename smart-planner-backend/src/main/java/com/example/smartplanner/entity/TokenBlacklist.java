package com.example.smartplanner.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "token_blacklist",
        indexes = {
                @Index(name = "idx_blacklist_jti", columnList = "tokenJti", unique = true),
                @Index(name = "idx_blacklist_expires", columnList = "expiresAt")
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TokenBlacklist {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String tokenJti;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false, updatable = false)
    private Instant blacklistedAt;

    @PrePersist
    void prePersist() {
        if (blacklistedAt == null) blacklistedAt = Instant.now();
    }
}
