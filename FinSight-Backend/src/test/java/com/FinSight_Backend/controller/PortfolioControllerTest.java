package com.FinSight_Backend.controller;

import com.FinSight_Backend.dto.*;
import com.FinSight_Backend.service.PortfolioService;
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

@WebMvcTest(PortfolioController.class)
class PortfolioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PortfolioService portfolioService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testAddPortfolio_Success() throws Exception {
        PortfolioDTO dto = new PortfolioDTO();
        dto.setPortfolio_id(1);

        Mockito.when(portfolioService.addPortfolio(Mockito.any()))
                .thenReturn(dto);

        mockMvc.perform(post("/api/v1/portfolio/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void testGetPortfolio_NotFound() throws Exception {
        Mockito.when(portfolioService.getPortfolio(1))
                .thenReturn(null);

        mockMvc.perform(get("/api/v1/portfolio/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeletePortfolio_Success() throws Exception {
        Mockito.when(portfolioService.deletePortfolio(1))
                .thenReturn("Deleted");

        mockMvc.perform(delete("/api/v1/portfolio/1"))
                .andExpect(status().isOk());
    }
}
