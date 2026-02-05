package com.FinSight_Backend.controller;

import com.FinSight_Backend.dto.MarketHistoryDTO;
import com.FinSight_Backend.dto.MarketPriceDTO;
import com.FinSight_Backend.dto.MarketRiskDTO;
import com.FinSight_Backend.service.MarketDataClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MarketDataController.class)
class MarketDataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MarketDataClient marketDataClient;

    @Test
    void getPrice_returnsOk() throws Exception {
        MarketPriceDTO dto = new MarketPriceDTO();
        dto.setSymbol("AAPL");
        dto.setPrice(190.0);
        Mockito.when(marketDataClient.getPrice("AAPL")).thenReturn(dto);

        mockMvc.perform(get("/api/v1/market/price/AAPL").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getRisk_returnsOk() throws Exception {
        MarketRiskDTO dto = new MarketRiskDTO();
        dto.setSymbol("AAPL");
        dto.setRisk_score(42);
        dto.setRisk_level("Moderate");
        Mockito.when(marketDataClient.getRisk("AAPL")).thenReturn(dto);

        mockMvc.perform(get("/api/v1/market/risk/AAPL").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getHistory_returnsOk() throws Exception {
        MarketHistoryDTO dto = new MarketHistoryDTO();
        dto.setSymbol("AAPL");
        dto.setCandles(Collections.emptyList());
        Mockito.when(marketDataClient.getHistory("AAPL", "1y", "1d")).thenReturn(dto);

        mockMvc.perform(get("/api/v1/market/history/AAPL?period=1y&interval=1d").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getPrice_badRequest() throws Exception {
        mockMvc.perform(get("/api/v1/market/price/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
