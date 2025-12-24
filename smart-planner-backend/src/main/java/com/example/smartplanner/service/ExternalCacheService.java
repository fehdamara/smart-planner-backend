package com.example.smartplanner.service;

import com.example.smartplanner.entity.ExternalCache;
import com.example.smartplanner.entity.User;
import com.example.smartplanner.repository.ExternalCacheRepository;
import com.example.smartplanner.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class ExternalCacheService {

    private final ExternalCacheRepository cacheRepo;
    private final UserRepository users;
    private final long cacheMinutes;

    public ExternalCacheService(ExternalCacheRepository cacheRepo,
                                UserRepository users,
                                @Value("${app.integrations.cacheMinutes:30}") long cacheMinutes) {
        this.cacheRepo = cacheRepo;
        this.users = users;
        this.cacheMinutes = cacheMinutes;
    }

    public String getCachedPayloadOrNull(Long userId, String cacheKey) {
        return cacheRepo.findTopByUserIdAndCacheKeyOrderByFetchedAtDesc(userId, cacheKey)
                .filter(row -> row.getFetchedAt().isAfter(Instant.now().minus(cacheMinutes, ChronoUnit.MINUTES)))
                .map(ExternalCache::getPayloadJson)
                .orElse(null);
    }

    public void savePayload(Long userId, String cacheKey, String payloadJson) {
        User u = users.findById(userId).orElseThrow();
        cacheRepo.save(ExternalCache.builder()
                .user(u)
                .cacheKey(cacheKey)
                .payloadJson(payloadJson)
                .fetchedAt(Instant.now())
                .build());
    }
}
