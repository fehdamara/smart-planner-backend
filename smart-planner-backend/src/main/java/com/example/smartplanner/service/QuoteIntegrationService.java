package com.example.smartplanner.service;

import com.example.smartplanner.dto.integration.QuotableQuoteDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class QuoteIntegrationService {

    private final RestClient quoteClient;
    private final ExternalCacheService cache;
    private final ObjectMapper om;

    public QuoteIntegrationService(RestClient quoteRestClient, ExternalCacheService cache, ObjectMapper om) {
        this.quoteClient = quoteRestClient;
        this.cache = cache;
        this.om = om;
    }

    public QuotableQuoteDto dailyQuote(Long userId) {
        String key = "quote:daily";
        String cached = cache.getCachedPayloadOrNull(userId, key);
        try {
            if (cached != null) {
                return om.readValue(cached, QuotableQuoteDto.class);
            }

            QuotableQuoteDto fresh = quoteClient.get()
                    .uri("/random")
                    .retrieve()
                    .body(QuotableQuoteDto.class);

            cache.savePayload(userId, key, om.writeValueAsString(fresh));
            return fresh;
        } catch (Exception e) {
            // fallback: if API fails and cache exists but parse failed, return a safe placeholder
            return new QuotableQuoteDto(null, "No quote available right now.", "system");
        }
    }
}
