package com.FinSight_Backend.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@Component
public class FinnhubClient {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String baseUrl;
    private final boolean enabled;

    public FinnhubClient(RestTemplate restTemplate,
                         @Value("${finnhub.api.key:}") String apiKey,
                         @Value("${finnhub.base-url:https://finnhub.io/api/v1/quote}") String baseUrl,
                         @Value("${finnhub.enabled:true}") boolean enabled) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Optional<BigDecimal> fetchLatestPrice(String symbol) {
        if (!enabled || apiKey == null || apiKey.isEmpty()) return Optional.empty();
        try {
            String url = baseUrl + "?symbol=" + symbol + "&token=" + apiKey;
            Map response = restTemplate.getForObject(url, Map.class);
            if (response == null) return Optional.empty();
            Object current = response.get("c"); // 'c' is current price in Finnhub quote response
            if (current == null) return Optional.empty();
            try {
                BigDecimal price = new BigDecimal(current.toString());
                return Optional.of(price);
            } catch (NumberFormatException nfe) {
                return Optional.empty();
            }
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
