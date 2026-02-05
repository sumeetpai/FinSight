package com.FinSight_Backend.controller;

import com.FinSight_Backend.dto.StocksDTO;
import com.FinSight_Backend.service.StockService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StockController.class)
class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StockService stockService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testAddStock_Success() throws Exception {

        StocksDTO dto = new StocksDTO();
        dto.setName("Apple");
        dto.setStock_sym("AAPL");   // ✅ REQUIRED

        // First call checks if symbol exists → return empty
        Mockito.when(stockService.getStocksBySymbol("AAPL"))
                .thenReturn(Optional.empty());

        // Then service creates stock
        Mockito.when(stockService.addStocks(Mockito.any()))
                .thenReturn(dto);

        mockMvc.perform(post("/api/v1/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void testAddStock_BadRequest() throws Exception {

        StocksDTO dto = new StocksDTO(); // no stock_sym → should fail

        mockMvc.perform(post("/api/v1/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetStock() throws Exception {

        StocksDTO dto = new StocksDTO();
        dto.setStock_id(1);

        Mockito.when(stockService.getStocks(1))
                .thenReturn(dto);

        mockMvc.perform(get("/api/v1/stocks/1"))
                .andExpect(status().isOk());
    }
}
