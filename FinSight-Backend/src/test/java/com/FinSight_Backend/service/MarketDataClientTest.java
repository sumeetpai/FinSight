package com.FinSight_Backend.service;

import com.FinSight_Backend.dto.MarketHistoryDTO;
import com.FinSight_Backend.dto.MarketPriceDTO;
import com.FinSight_Backend.dto.MarketRiskDTO;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MarketDataClientTest {

    @Test
    void getPrice_mapsResponse() {
        MarketDataClient client = new MarketDataClient("http://localhost:8000");
        RestTemplate rt = Mockito.mock(RestTemplate.class);
        ReflectionTestUtils.setField(client, "restTemplate", rt);

        Mockito.when(rt.getForObject(Mockito.contains("/stock/price/AAPL"), Mockito.eq(Map.class)))
                .thenReturn(Map.of("symbol", "AAPL", "price", 123.45, "currency", "USD", "timestamp", 1700));

        MarketPriceDTO dto = client.getPrice("AAPL");
        assertNotNull(dto);
        assertEquals("AAPL", dto.getSymbol());
        assertEquals(123.45, dto.getPrice());
    }

    @Test
    void getRisk_mapsResponse() {
        MarketDataClient client = new MarketDataClient("http://localhost:8000");
        RestTemplate rt = Mockito.mock(RestTemplate.class);
        ReflectionTestUtils.setField(client, "restTemplate", rt);

        Mockito.when(rt.getForObject(Mockito.contains("/stock/risk/TSLA"), Mockito.eq(Map.class)))
                .thenReturn(Map.of("symbol", "TSLA", "risk_score", 70, "risk_level", "High", "meaning", "Volatile"));

        MarketRiskDTO dto = client.getRisk("TSLA");
        assertNotNull(dto);
        assertEquals(70, dto.getRisk_score());
        assertEquals("High", dto.getRisk_level());
    }

    @Test
    void getHistory_mapsResponse() {
        MarketDataClient client = new MarketDataClient("http://localhost:8000");
        RestTemplate rt = Mockito.mock(RestTemplate.class);
        ReflectionTestUtils.setField(client, "restTemplate", rt);

        Mockito.when(rt.getForObject(Mockito.contains("/stock/history/MSFT"), Mockito.eq(Map.class)))
                .thenReturn(Map.of(
                        "symbol", "MSFT",
                        "candles", List.of(
                                Map.of("time", 1, "open", 1.0, "high", 2.0, "low", 0.5, "close", 1.5, "volume", 10)
                        )
                ));

        MarketHistoryDTO dto = client.getHistory("MSFT", "1y", "1d");
        assertNotNull(dto);
        assertEquals("MSFT", dto.getSymbol());
        assertEquals(1, dto.getCandles().size());
        assertEquals(1.5, dto.getCandles().get(0).getClose());
    }
}
