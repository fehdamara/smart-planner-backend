package com.example.smartplanner.repository;

import com.example.smartplanner.entity.ExternalCache;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExternalCacheRepository extends JpaRepository<ExternalCache, Long> {
    Optional<ExternalCache> findTopByUserIdAndCacheKeyOrderByFetchedAtDesc(Long userId, String cacheKey);
}
