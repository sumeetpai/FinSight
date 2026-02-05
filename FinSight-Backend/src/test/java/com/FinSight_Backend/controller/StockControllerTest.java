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
<<<<<<< HEAD
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
=======
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
>>>>>>> 323f0136fc4e141b74bb75a0c52ada2cc2f11153

@WebMvcTest(StockController.class)
class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

<<<<<<< HEAD
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StockService stockService;

    @Test
    void addStocks_returnsOkWhenExists() throws Exception {
        StocksDTO dto = new StocksDTO();
        dto.setStock_sym("AAPL");
        Mockito.when(stockService.getStocksBySymbol("AAPL")).thenReturn(Optional.of(dto));

        mockMvc.perform(post("/api/v1/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void addStocks_returnsCreatedWhenNew() throws Exception {
        StocksDTO dto = new StocksDTO();
        dto.setStock_sym("MSFT");
        Mockito.when(stockService.getStocksBySymbol("MSFT")).thenReturn(Optional.empty());
        Mockito.when(stockService.addStocks(Mockito.any())).thenReturn(dto);
=======
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
>>>>>>> 323f0136fc4e141b74bb75a0c52ada2cc2f11153

        mockMvc.perform(post("/api/v1/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
<<<<<<< HEAD
    void getStocks_returnsOk() throws Exception {
        Mockito.when(stockService.getStocks(1)).thenReturn(new StocksDTO());
        mockMvc.perform(get("/api/v1/stocks/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getBySymbol_notFound() throws Exception {
        Mockito.when(stockService.getStocksBySymbol("NONE")).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/v1/stocks/symbol/NONE"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBySymbol_badRequest() throws Exception {
        mockMvc.perform(get("/api/v1/stocks/symbol/"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateStocks_returnsOk() throws Exception {
        Mockito.when(stockService.updateStocks(Mockito.eq(1), Mockito.any())).thenReturn(new StocksDTO());
        StocksDTO dto = new StocksDTO();
        dto.setStock_sym("AAPL");

        mockMvc.perform(put("/api/v1/stocks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteStocks_returnsOk() throws Exception {
        Mockito.when(stockService.deleteStocks(1)).thenReturn("ok");
        mockMvc.perform(delete("/api/v1/stocks/1"))
                .andExpect(status().isOk());
    }
=======
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
>>>>>>> 323f0136fc4e141b74bb75a0c52ada2cc2f11153
}
