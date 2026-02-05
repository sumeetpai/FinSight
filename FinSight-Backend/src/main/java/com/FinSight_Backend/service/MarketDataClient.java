package com.FinSight_Backend.service;

import com.FinSight_Backend.dto.MarketInfoDTO;
import com.FinSight_Backend.dto.MarketPriceDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class MarketDataClient {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl;

    public MarketDataClient(@Value("${marketdata.base-url:http://localhost:8000}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public MarketPriceDTO getPrice(String symbol) {
        try {
            Map resp = restTemplate.getForObject(baseUrl + "/stock/price/" + symbol, Map.class);
            if (resp == null) return null;
            MarketPriceDTO dto = new MarketPriceDTO();
            dto.setSymbol(resp.get("symbol") != null ? resp.get("symbol").toString() : null);
            dto.setPrice(asDouble(resp.get("price")));
            dto.setCurrency(resp.get("currency") != null ? resp.get("currency").toString() : null);
            dto.setTimestamp(asLong(resp.get("timestamp")));
            return dto;
        } catch (Exception ex) {
            return null;
        }
    }

    public MarketInfoDTO getInfo(String symbol) {
        try {
            Map resp = restTemplate.getForObject(baseUrl + "/stock/info/" + symbol, Map.class);
            if (resp == null) return null;
            MarketInfoDTO dto = new MarketInfoDTO();
            dto.setSymbol(resp.get("symbol") != null ? resp.get("symbol").toString() : null);
            dto.setName(resp.get("name") != null ? resp.get("name").toString() : null);
            dto.setSector(resp.get("sector") != null ? resp.get("sector").toString() : null);
            dto.setIndustry(resp.get("industry") != null ? resp.get("industry").toString() : null);
            return dto;
        } catch (Exception ex) {
            return null;
        }
    }

    private Double asDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return value != null ? Double.parseDouble(value.toString()) : null;
        } catch (Exception ex) {
            return null;
        }
    }

    private Long asLong(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return value != null ? Long.parseLong(value.toString()) : null;
        } catch (Exception ex) {
            return null;
        }
    }
}
