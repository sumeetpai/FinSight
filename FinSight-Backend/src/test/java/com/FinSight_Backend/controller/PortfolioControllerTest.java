package com.FinSight_Backend.controller;

import com.FinSight_Backend.dto.*;
import com.FinSight_Backend.service.PortfolioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

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

    private PortfolioDTO portfolioDTO;
    private AddStockRequestDTO addStockRequestDTO;
    private RemoveStockRequestDTO removeStockRequestDTO;

    @BeforeEach
    void setUp() {
        // Setup PortfolioDTO
        portfolioDTO = new PortfolioDTO();
        portfolioDTO.setPortfolio_id(1);
        portfolioDTO.setName("My Portfolio");
        portfolioDTO.setUser_id(1);
        portfolioDTO.setTotal_value(10000.0);
        portfolioDTO.setCost_basis(8000.0);
        portfolioDTO.setYield(2000.0);

        // Setup AddStockRequestDTO
        addStockRequestDTO = new AddStockRequestDTO();
        addStockRequestDTO.setStock_id(1);
        addStockRequestDTO.setUser_id(1);
        addStockRequestDTO.setQty(10);

        // Setup RemoveStockRequestDTO
        removeStockRequestDTO = new RemoveStockRequestDTO();
        removeStockRequestDTO.setUser_id(1);
        removeStockRequestDTO.setQty(5);
    }

    // ========== ADD PORTFOLIO TESTS ==========
    @Test
    void testAddPortfolio_Success() throws Exception {
        Mockito.when(portfolioService.addPortfolio(Mockito.any(PortfolioDTO.class)))
                .thenReturn(portfolioDTO);

        mockMvc.perform(post("/api/v1/portfolio/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(portfolioDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.portfolio_id").value(1))
                .andExpect(jsonPath("$.name").value("My Portfolio"));

        Mockito.verify(portfolioService, Mockito.times(1))
                .addPortfolio(Mockito.any(PortfolioDTO.class));
    }

    @Test
    void testAddPortfolio_BadRequest_NullDTO() throws Exception {
        mockMvc.perform(post("/api/v1/portfolio/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddPortfolio_NotFound_UserDoesNotExist() throws Exception {
        Mockito.when(portfolioService.addPortfolio(Mockito.any(PortfolioDTO.class)))
                .thenReturn(null);

        mockMvc.perform(post("/api/v1/portfolio/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(portfolioDTO)))
                .andExpect(status().isNotFound());
    }

    // ========== GET PORTFOLIO TESTS ==========
    @Test
    void testGetPortfolio_Success() throws Exception {
        Mockito.when(portfolioService.getPortfolio(1))
                .thenReturn(portfolioDTO);

        mockMvc.perform(get("/api/v1/portfolio/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.portfolio_id").value(1))
                .andExpect(jsonPath("$.name").value("My Portfolio"));

        Mockito.verify(portfolioService, Mockito.times(1))
                .getPortfolio(1);
    }

    @Test
    void testGetPortfolio_NotFound() throws Exception {
        Mockito.when(portfolioService.getPortfolio(999))
                .thenReturn(null);

        mockMvc.perform(get("/api/v1/portfolio/999"))
                .andExpect(status().isNotFound());
    }

    // ========== UPDATE PORTFOLIO TESTS ==========
    @Test
    void testUpdatePortfolio_Success() throws Exception {
        PortfolioDTO updatedDTO = new PortfolioDTO();
        updatedDTO.setPortfolio_id(1);
        updatedDTO.setName("Updated Portfolio");
        updatedDTO.setUser_id(1);
        updatedDTO.setTotal_value(15000.0);

        Mockito.when(portfolioService.updatePortfolio(Mockito.eq(1), Mockito.any(PortfolioDTO.class)))
                .thenReturn(updatedDTO);

        mockMvc.perform(put("/api/v1/portfolio/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Portfolio"))
                .andExpect(jsonPath("$.total_value").value(15000));

        Mockito.verify(portfolioService, Mockito.times(1))
                .updatePortfolio(Mockito.eq(1), Mockito.any(PortfolioDTO.class));
    }

    @Test
    void testUpdatePortfolio_NotFound() throws Exception {
        Mockito.when(portfolioService.updatePortfolio(Mockito.eq(999), Mockito.any(PortfolioDTO.class)))
                .thenReturn(null);

        mockMvc.perform(put("/api/v1/portfolio/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(portfolioDTO)))
                .andExpect(status().isNotFound());
    }

    // ========== DELETE PORTFOLIO TESTS ==========
    @Test
    void testDeletePortfolio_Success() throws Exception {
        Mockito.when(portfolioService.deletePortfolio(1))
                .thenReturn("Portfolio deleted");

        mockMvc.perform(delete("/api/v1/portfolio/1"))
                .andExpect(status().isOk());

        Mockito.verify(portfolioService, Mockito.times(1))
                .deletePortfolio(1);
    }

    @Test
    void testDeletePortfolio_NotFound() throws Exception {
        Mockito.when(portfolioService.deletePortfolio(999))
                .thenReturn(null);

        mockMvc.perform(delete("/api/v1/portfolio/999"))
                .andExpect(status().isNotFound());
    }

    // ========== GET ALL PORTFOLIOS TESTS ==========
    @Test
    void testGetAllPortfolios_Success() throws Exception {
        List<PortfolioDTO> portfolios = new ArrayList<>();
        portfolios.add(portfolioDTO);

        PortfolioDTO portfolio2 = new PortfolioDTO();
        portfolio2.setPortfolio_id(2);
        portfolio2.setName("Portfolio 2");
        portfolios.add(portfolio2);

        Mockito.when(portfolioService.getAllPortfolios())
                .thenReturn(portfolios);

        mockMvc.perform(get("/api/v1/portfolio/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].portfolio_id").value(1))
                .andExpect(jsonPath("$[1].portfolio_id").value(2))
                .andExpect(jsonPath("$.length()").value(2));

        Mockito.verify(portfolioService, Mockito.times(1))
                .getAllPortfolios();
    }

    @Test
    void testGetAllPortfolios_Empty() throws Exception {
        Mockito.when(portfolioService.getAllPortfolios())
                .thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/v1/portfolio/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ========== GET PORTFOLIOS BY USER TESTS ==========
    @Test
    void testGetPortfoliosByUser_Success() throws Exception {
        List<PortfolioDTO> userPortfolios = new ArrayList<>();
        userPortfolios.add(portfolioDTO);

        Mockito.when(portfolioService.getPortfoliosByUser(1))
                .thenReturn(userPortfolios);

        mockMvc.perform(get("/api/v1/portfolio/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].portfolio_id").value(1))
                .andExpect(jsonPath("$[0].user_id").value(1));

        Mockito.verify(portfolioService, Mockito.times(1))
                .getPortfoliosByUser(1);
    }

    @Test
    void testGetPortfoliosByUser_NotFound() throws Exception {
        Mockito.when(portfolioService.getPortfoliosByUser(999))
                .thenReturn(null);

        mockMvc.perform(get("/api/v1/portfolio/user/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetPortfoliosByUser_Empty() throws Exception {
        Mockito.when(portfolioService.getPortfoliosByUser(1))
                .thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/v1/portfolio/user/1"))
                .andExpect(status().isNotFound());
    }

    // ========== ADD STOCK TO PORTFOLIO TESTS ==========
    @Test
    void testAddStockToPortfolio_Success() throws Exception {
        PortfolioDTO updatedPortfolio = new PortfolioDTO();
        updatedPortfolio.setPortfolio_id(1);
        updatedPortfolio.setName("My Portfolio");

        Mockito.when(portfolioService.addStockToPortfolio(Mockito.eq(1), Mockito.eq(1), Mockito.eq(1), Mockito.eq(10)))
                .thenReturn(updatedPortfolio);

        mockMvc.perform(post("/api/v1/portfolio/1/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addStockRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.portfolio_id").value(1));

        Mockito.verify(portfolioService, Mockito.times(1))
                .addStockToPortfolio(Mockito.eq(1), Mockito.eq(1), Mockito.eq(1), Mockito.eq(10));
    }

    @Test
    void testAddStockToPortfolio_BadRequest_MissingStockId() throws Exception {
        AddStockRequestDTO invalidRequest = new AddStockRequestDTO();
        invalidRequest.setUser_id(1);
        invalidRequest.setQty(10);

        mockMvc.perform(post("/api/v1/portfolio/1/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddStockToPortfolio_BadRequest_MissingUserId() throws Exception {
        AddStockRequestDTO invalidRequest = new AddStockRequestDTO();
        invalidRequest.setStock_id(1);
        invalidRequest.setQty(10);

        mockMvc.perform(post("/api/v1/portfolio/1/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddStockToPortfolio_NotFound() throws Exception {
        Mockito.when(portfolioService.addStockToPortfolio(Mockito.eq(999), Mockito.eq(1), Mockito.eq(1), Mockito.eq(10)))
                .thenReturn(null);

        mockMvc.perform(post("/api/v1/portfolio/999/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addStockRequestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAddStockToPortfolio_DefaultQty() throws Exception {
        AddStockRequestDTO requestWithoutQty = new AddStockRequestDTO();
        requestWithoutQty.setStock_id(1);
        requestWithoutQty.setUser_id(1);

        PortfolioDTO updatedPortfolio = new PortfolioDTO();
        updatedPortfolio.setPortfolio_id(1);

        Mockito.when(portfolioService.addStockToPortfolio(Mockito.eq(1), Mockito.eq(1), Mockito.eq(1), Mockito.eq(1)))
                .thenReturn(updatedPortfolio);

        mockMvc.perform(post("/api/v1/portfolio/1/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestWithoutQty)))
                .andExpect(status().isOk());
    }

    // ========== REMOVE STOCK FROM PORTFOLIO TESTS ==========
    @Test
    void testRemoveStockFromPortfolio_Success() throws Exception {
        PortfolioDTO updatedPortfolio = new PortfolioDTO();
        updatedPortfolio.setPortfolio_id(1);
        updatedPortfolio.setName("My Portfolio");

        Mockito.when(portfolioService.removeStockFromPortfolio(Mockito.eq(1), Mockito.eq(1), Mockito.eq(1), Mockito.eq(5)))
                .thenReturn(updatedPortfolio);

        mockMvc.perform(delete("/api/v1/portfolio/1/stocks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(removeStockRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.portfolio_id").value(1));

        Mockito.verify(portfolioService, Mockito.times(1))
                .removeStockFromPortfolio(Mockito.eq(1), Mockito.eq(1), Mockito.eq(1), Mockito.eq(5));
    }

    @Test
    void testRemoveStockFromPortfolio_BadRequest_MissingUserId() throws Exception {
        RemoveStockRequestDTO invalidRequest = new RemoveStockRequestDTO();
        invalidRequest.setQty(5);

        mockMvc.perform(delete("/api/v1/portfolio/1/stocks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRemoveStockFromPortfolio_NotFound() throws Exception {
        Mockito.when(portfolioService.removeStockFromPortfolio(Mockito.eq(999), Mockito.eq(1), Mockito.eq(1), Mockito.eq(5)))
                .thenReturn(null);

        mockMvc.perform(delete("/api/v1/portfolio/999/stocks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(removeStockRequestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRemoveStockFromPortfolio_DefaultQty() throws Exception {
        RemoveStockRequestDTO requestWithoutQty = new RemoveStockRequestDTO();
        requestWithoutQty.setUser_id(1);

        PortfolioDTO updatedPortfolio = new PortfolioDTO();
        updatedPortfolio.setPortfolio_id(1);

        Mockito.when(portfolioService.removeStockFromPortfolio(Mockito.eq(1), Mockito.eq(1), Mockito.eq(1), Mockito.eq(1)))
                .thenReturn(updatedPortfolio);

        mockMvc.perform(delete("/api/v1/portfolio/1/stocks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestWithoutQty)))
                .andExpect(status().isOk());
    }
}
