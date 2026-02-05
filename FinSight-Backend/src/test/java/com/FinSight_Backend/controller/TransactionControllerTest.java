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
<<<<<<< HEAD
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
=======
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
>>>>>>> 323f0136fc4e141b74bb75a0c52ada2cc2f11153

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

<<<<<<< HEAD
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;

    @Test
    void addTransaction_badRequest() throws Exception {
        TransactionDTO dto = new TransactionDTO();
        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addTransaction_created() throws Exception {
        TransactionDTO dto = new TransactionDTO();
        dto.setStock_id(1);
        dto.setPortfolio_id(2);
        dto.setUser_id(3);
        dto.setType("ADD");
        Mockito.when(transactionService.addTransaction(Mockito.any())).thenReturn(dto);

        mockMvc.perform(post("/api/v1/transactions")
=======
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

        mockMvc.perform(post("/api/v1/transaction/")
>>>>>>> 323f0136fc4e141b74bb75a0c52ada2cc2f11153
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
<<<<<<< HEAD
    void getTransaction_notFound() throws Exception {
        Mockito.when(transactionService.getTransaction(9)).thenReturn(null);
        mockMvc.perform(get("/api/v1/transactions/9"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTransaction_notFound() throws Exception {
        TransactionDTO dto = new TransactionDTO();
        Mockito.when(transactionService.updateTransaction(Mockito.eq(1), Mockito.any())).thenReturn(null);

        mockMvc.perform(put("/api/v1/transactions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTransaction_notFound() throws Exception {
        Mockito.when(transactionService.deleteTransaction(1)).thenReturn(null);
        mockMvc.perform(delete("/api/v1/transactions/1"))
=======
    void testAddTransaction_BadRequest() throws Exception {
        TransactionDTO dto = new TransactionDTO();

        mockMvc.perform(post("/api/v1/transaction/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetTransaction_NotFound() throws Exception {
        Mockito.when(transactionService.getTransaction(1))
                .thenReturn(null);

        mockMvc.perform(get("/api/v1/transaction/1"))
>>>>>>> 323f0136fc4e141b74bb75a0c52ada2cc2f11153
                .andExpect(status().isNotFound());
    }
}
