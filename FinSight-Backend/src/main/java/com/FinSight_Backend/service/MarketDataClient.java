package com.FinSight_Backend.service;

import com.FinSight_Backend.dto.MarketInfoDTO;
import com.FinSight_Backend.dto.MarketPriceDTO;
import com.FinSight_Backend.dto.MarketRiskDTO;
import com.FinSight_Backend.dto.MarketHistoryDTO;
import com.FinSight_Backend.dto.MarketCandleDTO;
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

    public MarketRiskDTO getRisk(String symbol) {
        try {
            Map resp = restTemplate.getForObject(baseUrl + "/stock/risk/" + symbol, Map.class);
            if (resp == null) return null;
            MarketRiskDTO dto = new MarketRiskDTO();
            dto.setSymbol(resp.get("symbol") != null ? resp.get("symbol").toString() : null);
            dto.setRisk_score(asInteger(resp.get("risk_score")));
            dto.setRisk_level(resp.get("risk_level") != null ? resp.get("risk_level").toString() : null);
            dto.setMeaning(resp.get("meaning") != null ? resp.get("meaning").toString() : null);
            return dto;
        } catch (Exception ex) {
            return null;
        }
    }

    public MarketHistoryDTO getHistory(String symbol, String period, String interval) {
        try {
            String url = baseUrl + "/stock/history/" + symbol + "?period=" + period + "&interval=" + interval;
            Map resp = restTemplate.getForObject(url, Map.class);
            if (resp == null) return null;
            MarketHistoryDTO dto = new MarketHistoryDTO();
            dto.setSymbol(resp.get("symbol") != null ? resp.get("symbol").toString() : null);
            Object candlesObj = resp.get("candles");
            if (candlesObj instanceof java.util.List) {
                java.util.List<MarketCandleDTO> candles = new java.util.ArrayList<>();
                for (Object item : (java.util.List) candlesObj) {
                    if (!(item instanceof Map)) continue;
                    Map m = (Map) item;
                    MarketCandleDTO c = new MarketCandleDTO();
                    c.setTime(asLong(m.get("time")));
                    c.setOpen(asDouble(m.get("open")));
                    c.setHigh(asDouble(m.get("high")));
                    c.setLow(asDouble(m.get("low")));
                    c.setClose(asDouble(m.get("close")));
                    c.setVolume(asLong(m.get("volume")));
                    candles.add(c);
                }
                dto.setCandles(candles);
            }
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

    private Integer asInteger(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return value != null ? Integer.parseInt(value.toString()) : null;
        } catch (Exception ex) {
            return null;
        }
    }
}
