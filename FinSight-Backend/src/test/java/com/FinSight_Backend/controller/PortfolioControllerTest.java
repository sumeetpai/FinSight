package com.FinSight_Backend.controller;

<<<<<<< HEAD
import com.FinSight_Backend.dto.AddStockRequestDTO;
import com.FinSight_Backend.dto.PortfolioDTO;
import com.FinSight_Backend.dto.PortfolioStatusDTO;
import com.FinSight_Backend.dto.RemoveStockRequestDTO;
import com.FinSight_Backend.service.PortfolioService;
import com.fasterxml.jackson.databind.ObjectMapper;
=======
import com.FinSight_Backend.dto.*;
import com.FinSight_Backend.service.PortfolioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
>>>>>>> 323f0136fc4e141b74bb75a0c52ada2cc2f11153
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

<<<<<<< HEAD
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
=======
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
>>>>>>> 323f0136fc4e141b74bb75a0c52ada2cc2f11153

@WebMvcTest(PortfolioController.class)
class PortfolioControllerTest {

    @Autowired
    private MockMvc mockMvc;

<<<<<<< HEAD
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
=======
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
>>>>>>> 323f0136fc4e141b74bb75a0c52ada2cc2f11153
                .andExpect(status().isBadRequest());
    }

    @Test
<<<<<<< HEAD
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
=======
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
>>>>>>> 323f0136fc4e141b74bb75a0c52ada2cc2f11153
                .andExpect(status().isBadRequest());
    }

    @Test
<<<<<<< HEAD
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
=======
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
>>>>>>> 323f0136fc4e141b74bb75a0c52ada2cc2f11153
}
