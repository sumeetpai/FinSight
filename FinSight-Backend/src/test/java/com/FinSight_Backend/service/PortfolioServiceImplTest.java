package com.FinSight_Backend.service;

import com.FinSight_Backend.dto.PortfolioDTO;
import com.FinSight_Backend.dto.StockEntryDTO;
import com.FinSight_Backend.model.*;
import com.FinSight_Backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceImplTest {

    @Mock
    private PortfolioRepo portfolioRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private StocksRepo stocksRepo;

    @Mock
    private TransactionRepo transactionRepo;

    @Mock
    private PortfolioStockRepo portfolioStockRepo;

    @InjectMocks
    private PortfolioServiceImpl portfolioService;

    private PortfolioDTO portfolioDTO;
    private Portfolio portfolio;
    private User user;
    private Stocks stocks;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUser_id(1);
        user.setUsername("testuser");

        portfolioDTO = new PortfolioDTO();
        portfolioDTO.setName("My Portfolio");
        portfolioDTO.setTotal_value(10000.0);
        portfolioDTO.setCost_basis(9000.0);
        portfolioDTO.setYield(1000.00);
        portfolioDTO.setUser_id(1);
        portfolioDTO.setStock_entries(null);

        portfolio = new Portfolio();
        portfolio.setPortfolio_id(1);
        portfolio.setName("My Portfolio");
        portfolio.setTotal_value(10000.0);
        portfolio.setCost_basis(9000.0);
        portfolio.setYield(1000.00);
        portfolio.setUser(user);
        portfolio.setPortfolioStocks(new ArrayList<>());

        stocks = new Stocks();
        stocks.setStock_id(1);
        stocks.setName("Apple Inc.");
        stocks.setStock_sym("AAPL");
        stocks.setCurrent_price(150.00);
    }

    // ===============================
    // ADD PORTFOLIO
    // ===============================

    @Test
    void testAddPortfolio_Success() {
        when(userRepo.existsById(1)).thenReturn(true);
        when(portfolioRepo.save(any(Portfolio.class))).thenReturn(portfolio);

        PortfolioDTO result = portfolioService.addPortfolio(portfolioDTO);

        assertNotNull(result);
        assertEquals("My Portfolio", result.getName());
        verify(userRepo).existsById(1);
        verify(portfolioRepo).save(any(Portfolio.class));
    }

    @Test
    void testAddPortfolio_UserNotFound() {
        portfolioDTO.setUser_id(999);
        when(userRepo.existsById(999)).thenReturn(false);

        PortfolioDTO result = portfolioService.addPortfolio(portfolioDTO);

        assertNull(result);
        verify(portfolioRepo, never()).save(any());
    }

    @Test
    void testAddPortfolio_NoUserIdProvided() {
        portfolioDTO.setUser_id(null);

        PortfolioDTO result = portfolioService.addPortfolio(portfolioDTO);

        assertNull(result);
        verify(portfolioRepo, never()).save(any());
    }

    // ===============================
    // ADD STOCK TO PORTFOLIO
    // ===============================

    @Test
    void testAddStockToPortfolio_Success() {

        when(portfolioRepo.findById(1)).thenReturn(Optional.of(portfolio));
        when(stocksRepo.findById(1)).thenReturn(Optional.of(stocks));
        when(portfolioStockRepo.findByPortfolioIdAndStockId(1, 1)).thenReturn(null);
        when(portfolioRepo.save(any())).thenReturn(portfolio);
        when(transactionRepo.save(any())).thenReturn(new Transaction());

        PortfolioDTO result = portfolioService.addStockToPortfolio(1, 1, 1, 10);

        assertNotNull(result);

        verify(portfolioRepo).findById(1);
        verify(stocksRepo).findById(1);
        verify(portfolioStockRepo).save(any(PortfolioStock.class));
        verify(transactionRepo).save(any(Transaction.class));
    }

    @Test
    void testAddStockToPortfolio_ExistingStock() {

        PortfolioStock existingPS = new PortfolioStock();
        existingPS.setPortfolio(portfolio);
        existingPS.setStock(stocks);
        existingPS.setQuantity(5);

        when(portfolioRepo.findById(1)).thenReturn(Optional.of(portfolio));
        when(stocksRepo.findById(1)).thenReturn(Optional.of(stocks));
        when(portfolioStockRepo.findByPortfolioIdAndStockId(1, 1)).thenReturn(existingPS);
        when(portfolioRepo.save(any())).thenReturn(portfolio);
        when(transactionRepo.save(any())).thenReturn(new Transaction());

        PortfolioDTO result = portfolioService.addStockToPortfolio(1, 1, 1, 10);

        assertNotNull(result);

        verify(portfolioStockRepo).save(any(PortfolioStock.class));
        verify(transactionRepo).save(any(Transaction.class));
    }

    @Test
    void testAddStockToPortfolio_DefaultQtyOne() {

        when(portfolioRepo.findById(1)).thenReturn(Optional.of(portfolio));
        when(stocksRepo.findById(1)).thenReturn(Optional.of(stocks));
        when(portfolioStockRepo.findByPortfolioIdAndStockId(1, 1)).thenReturn(null);
        when(portfolioRepo.save(any())).thenReturn(portfolio);
        when(transactionRepo.save(any())).thenReturn(new Transaction());

        PortfolioDTO result = portfolioService.addStockToPortfolio(1, 1, 1, null);

        assertNotNull(result);

        verify(portfolioStockRepo).save(any(PortfolioStock.class));
    }
}
