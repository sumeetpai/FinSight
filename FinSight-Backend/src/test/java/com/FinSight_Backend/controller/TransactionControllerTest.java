package com.FinSight_Backend.controller;

import com.FinSight_Backend.dto.TransactionDTO;
import com.FinSight_Backend.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testAddTransaction_Success() throws Exception {
        TransactionDTO dto = new TransactionDTO();
        dto.setStock_id(1);
        dto.setPortfolio_id(1);
        dto.setUser_id(1);
        dto.setType("BUY");

        Mockito.when(transactionService.addTransaction(Mockito.any()))
                .thenReturn(dto);

        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void testAddTransaction_BadRequest() throws Exception {
        TransactionDTO dto = new TransactionDTO();

        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetTransaction_NotFound() throws Exception {
        Mockito.when(transactionService.getTransaction(1))
                .thenReturn(null);

        mockMvc.perform(get("/api/v1/transactions/1"))
                .andExpect(status().isNotFound());
    }
}
