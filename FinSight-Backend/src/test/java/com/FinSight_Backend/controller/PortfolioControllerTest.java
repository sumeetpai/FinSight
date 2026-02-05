package com.FinSight_Backend.controller;

import com.FinSight_Backend.dto.AddStockRequestDTO;
import com.FinSight_Backend.dto.PortfolioDTO;
import com.FinSight_Backend.dto.PortfolioStatusDTO;
import com.FinSight_Backend.dto.RemoveStockRequestDTO;
import com.FinSight_Backend.service.PortfolioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PortfolioController.class)
class PortfolioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PortfolioService portfolioService;

    @Test
    void addPortfolio_returnsCreated() throws Exception {
        PortfolioDTO dto = new PortfolioDTO();
        dto.setUser_id(1);
        dto.setName("Test");
        Mockito.when(portfolioService.addPortfolio(Mockito.any())).thenReturn(dto);

        mockMvc.perform(post("/api/v1/portfolio/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void addPortfolio_badRequestWhenNullBody() throws Exception {
        mockMvc.perform(post("/api/v1/portfolio/"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addPortfolio_notFoundWhenServiceReturnsNull() throws Exception {
        PortfolioDTO dto = new PortfolioDTO();
        dto.setUser_id(99);
        Mockito.when(portfolioService.addPortfolio(Mockito.any())).thenReturn(null);

        mockMvc.perform(post("/api/v1/portfolio/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPortfolio_notFound() throws Exception {
        Mockito.when(portfolioService.getPortfolio(99)).thenReturn(null);
        mockMvc.perform(get("/api/v1/portfolio/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updatePortfolio_returnsOk() throws Exception {
        PortfolioDTO dto = new PortfolioDTO();
        dto.setName("Updated");
        Mockito.when(portfolioService.updatePortfolio(Mockito.eq(1), Mockito.any())).thenReturn(dto);

        mockMvc.perform(put("/api/v1/portfolio/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void updatePortfolio_notFound() throws Exception {
        PortfolioDTO dto = new PortfolioDTO();
        Mockito.when(portfolioService.updatePortfolio(Mockito.eq(1), Mockito.any())).thenReturn(null);

        mockMvc.perform(put("/api/v1/portfolio/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllPortfolios_returnsOk() throws Exception {
        Mockito.when(portfolioService.getAllPortfolios()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/v1/portfolio/"))
                .andExpect(status().isOk());
    }

    @Test
    void setPortfolioStatus_returnsOk() throws Exception {
        PortfolioStatusDTO status = new PortfolioStatusDTO();
        status.setActive(true);
        Mockito.when(portfolioService.setPortfolioActiveStatus(Mockito.eq(1), Mockito.eq(true))).thenReturn(new PortfolioDTO());

        mockMvc.perform(put("/api/v1/portfolio/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(status)))
                .andExpect(status().isOk());
    }

    @Test
    void setPortfolioStatus_badRequest() throws Exception {
        mockMvc.perform(put("/api/v1/portfolio/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addStockToPortfolio_returnsOk() throws Exception {
        AddStockRequestDTO req = new AddStockRequestDTO();
        req.setStock_id(1);
        req.setUser_id(1);
        req.setQty(2);
        Mockito.when(portfolioService.addStockToPortfolio(Mockito.eq(1), Mockito.eq(1), Mockito.eq(1), Mockito.eq(2)))
                .thenReturn(new PortfolioDTO());

        mockMvc.perform(post("/api/v1/portfolio/1/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void addStockToPortfolio_badRequest() throws Exception {
        mockMvc.perform(post("/api/v1/portfolio/1/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void removeStockFromPortfolio_returnsOk() throws Exception {
        RemoveStockRequestDTO req = new RemoveStockRequestDTO();
        req.setUser_id(1);
        req.setQty(1);
        Mockito.when(portfolioService.removeStockFromPortfolio(Mockito.eq(1), Mockito.eq(2), Mockito.eq(1), Mockito.eq(1)))
                .thenReturn(new PortfolioDTO());

        mockMvc.perform(delete("/api/v1/portfolio/1/stocks/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void removeStockFromPortfolio_badRequest() throws Exception {
        mockMvc.perform(delete("/api/v1/portfolio/1/stocks/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
