package com.FinSight_Backend.service;

import com.FinSight_Backend.dto.PortfolioDTO;
import com.FinSight_Backend.dto.StockEntryDTO;
import com.FinSight_Backend.model.Portfolio;
import com.FinSight_Backend.model.PortfolioStock;
import com.FinSight_Backend.model.Stocks;
import com.FinSight_Backend.model.Transaction;
import com.FinSight_Backend.model.User;
import com.FinSight_Backend.repository.PortfolioRepo;
import com.FinSight_Backend.repository.PortfolioStockRepo;
import com.FinSight_Backend.repository.StocksRepo;
import com.FinSight_Backend.repository.TransactionRepo;
import com.FinSight_Backend.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
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
        portfolioDTO.setTotal_value(10000L);
        portfolioDTO.setCost_basis(9000L);
        portfolioDTO.setYield(1000);
        portfolioDTO.setUser_id(1);
        portfolioDTO.setStock_entries(null);

        portfolio = new Portfolio();
        portfolio.setPortfolio_id(1);
        portfolio.setName("My Portfolio");
        portfolio.setTotal_value(10000L);
        portfolio.setCost_basis(9000L);
        portfolio.setYield(1000);
        portfolio.setUser(user);
        portfolio.setPortfolioStocks(new ArrayList<>());

        stocks = new Stocks();
        stocks.setStock_id(1);
        stocks.setName("Apple Inc.");
        stocks.setStock_sym("AAPL");
        stocks.setCurrent_price(150);
    }

    @Test
    void testAddPortfolio_Success() {
        // Arrange
        when(userRepo.existsById(1)).thenReturn(true);
        when(portfolioRepo.save(any(Portfolio.class))).thenReturn(portfolio);

        // Act
        PortfolioDTO result = portfolioService.addPortfolio(portfolioDTO);

        // Assert
        assertNotNull(result);
        assertEquals("My Portfolio", result.getName());
        assertEquals(10000L, result.getTotal_value());
        verify(userRepo, times(1)).existsById(1);
        verify(portfolioRepo, times(1)).save(any(Portfolio.class));
    }

    @Test
    void testAddPortfolio_UserNotFound() {
        // Arrange
        portfolioDTO.setUser_id(999);
        when(userRepo.existsById(999)).thenReturn(false);

        // Act
        PortfolioDTO result = portfolioService.addPortfolio(portfolioDTO);

        // Assert
        assertNull(result);
        verify(userRepo, times(1)).existsById(999);
        verify(portfolioRepo, never()).save(any(Portfolio.class));
    }

    @Test
    void testAddPortfolio_NoUserIdProvided() {
        // Arrange
        portfolioDTO.setUser_id(null);

        // Act
        PortfolioDTO result = portfolioService.addPortfolio(portfolioDTO);

        // Assert
        assertNull(result);
        verify(userRepo, never()).existsById(anyInt());
        verify(portfolioRepo, never()).save(any(Portfolio.class));
    }

    @Test
    void testAddPortfolio_WithStockEntries() {
        // Arrange
        List<StockEntryDTO> stockEntries = new ArrayList<>();
        StockEntryDTO entry1 = new StockEntryDTO(1, 10);
        stockEntries.add(entry1);
        portfolioDTO.setStock_entries(stockEntries);

        PortfolioStock ps = new PortfolioStock();
        ps.setPortfolio(portfolio);
        ps.setStock(stocks);
        ps.setQuantity(10);

        portfolio.setPortfolioStocks(new ArrayList<>());
        portfolio.getPortfolioStocks().add(ps);

        when(userRepo.existsById(1)).thenReturn(true);
        when(stocksRepo.findById(1)).thenReturn(Optional.of(stocks));
        when(portfolioRepo.save(any(Portfolio.class))).thenReturn(portfolio);

        // Act
        PortfolioDTO result = portfolioService.addPortfolio(portfolioDTO);

        // Assert
        assertNotNull(result);
        assertEquals("My Portfolio", result.getName());
        verify(userRepo, times(1)).existsById(1);
        verify(stocksRepo, times(1)).findById(1);
        verify(portfolioRepo, times(1)).save(any(Portfolio.class));
    }

    @Test
    void testGetPortfolio_Success() {
        // Arrange
        when(portfolioRepo.findById(1)).thenReturn(Optional.of(portfolio));

        // Act
        PortfolioDTO result = portfolioService.getPortfolio(1);

        // Assert
        assertNotNull(result);
        assertEquals("My Portfolio", result.getName());
        assertEquals(1, result.getPortfolio_id());
        verify(portfolioRepo, times(2)).findById(1);
    }

    @Test
    void testUpdatePortfolio_Success() {
        // Arrange
        PortfolioDTO updateDTO = new PortfolioDTO();
        updateDTO.setName("Updated Portfolio");
        updateDTO.setTotal_value(15000L);
        updateDTO.setCost_basis(12000L);
        updateDTO.setYield(3000);
        updateDTO.setUser_id(1);

        Portfolio updatedPortfolio = new Portfolio();
        updatedPortfolio.setPortfolio_id(1);
        updatedPortfolio.setName("Updated Portfolio");
        updatedPortfolio.setTotal_value(15000L);
        updatedPortfolio.setCost_basis(12000L);
        updatedPortfolio.setYield(3000);
        updatedPortfolio.setUser(user);

        when(portfolioRepo.findById(1)).thenReturn(Optional.of(portfolio));
        when(userRepo.existsById(1)).thenReturn(true);
        when(portfolioRepo.save(any(Portfolio.class))).thenReturn(updatedPortfolio);

        // Act
        PortfolioDTO result = portfolioService.updatePortfolio(1, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Portfolio", result.getName());
        assertEquals(15000L, result.getTotal_value());
        verify(portfolioRepo, times(2)).findById(1);
        verify(portfolioRepo, times(1)).save(any(Portfolio.class));
    }

    @Test
    void testUpdatePortfolio_NotFound() {
        // Arrange
        when(portfolioRepo.findById(999)).thenReturn(Optional.empty());

        // Act
        PortfolioDTO result = portfolioService.updatePortfolio(999, portfolioDTO);

        // Assert
        assertNull(result);
        verify(portfolioRepo, times(1)).findById(999);
        verify(portfolioRepo, never()).save(any(Portfolio.class));
    }

    @Test
    void testUpdatePortfolio_InvalidUser() {
        // Arrange
        PortfolioDTO updateDTO = new PortfolioDTO();
        updateDTO.setUser_id(999);
        updateDTO.setName("Updated Portfolio");

        when(portfolioRepo.findById(1)).thenReturn(Optional.of(portfolio));
        when(userRepo.existsById(999)).thenReturn(false);

        // Act
        PortfolioDTO result = portfolioService.updatePortfolio(1, updateDTO);

        // Assert
        assertNull(result);
        verify(portfolioRepo, times(2)).findById(1);
        verify(userRepo, times(1)).existsById(999);
    }

    @Test
    void testDeletePortfolio_Success() {
        // Arrange
        when(portfolioRepo.findById(1)).thenReturn(Optional.of(portfolio));
        doNothing().when(portfolioRepo).delete(any(Portfolio.class));

        // Act
        String result = portfolioService.deletePortfolio(1);

        // Assert
        assertNotNull(result);
        assertEquals("Portfolio deleted", result);
        verify(portfolioRepo, times(2)).findById(1);
        verify(portfolioRepo, times(1)).delete(portfolio);
    }

    @Test
    void testAddStockToPortfolio_Success() {
        // Arrange
        when(portfolioRepo.findById(1)).thenReturn(Optional.of(portfolio));
        when(stocksRepo.findById(1)).thenReturn(Optional.of(stocks));
        when(portfolioStockRepo.findByPortfolioIdAndStockId(1, 1)).thenReturn(null);
        when(portfolioStockRepo.findByPortfolioId(1)).thenReturn(new ArrayList<>());
        when(portfolioRepo.save(any(Portfolio.class))).thenReturn(portfolio);
        when(transactionRepo.save(any(Transaction.class))).thenReturn(new Transaction());

        // Act
        PortfolioDTO result = portfolioService.addStockToPortfolio(1, 1, 1, 10);

        // Assert
        assertNotNull(result);
        assertEquals("My Portfolio", result.getName());
        verify(portfolioRepo, times(1)).findById(1);
        verify(stocksRepo, times(1)).findById(1);
        verify(portfolioStockRepo, times(1)).save(any(PortfolioStock.class));
        verify(transactionRepo, times(1)).save(any(Transaction.class));
    }

    @Test
    void testAddStockToPortfolio_PortfolioNotFound() {
        // Arrange
        when(portfolioRepo.findById(999)).thenReturn(Optional.empty());

        // Act
        PortfolioDTO result = portfolioService.addStockToPortfolio(999, 1, 1, 10);

        // Assert
        assertNull(result);
        verify(portfolioRepo, times(1)).findById(999);
        verify(stocksRepo, never()).findById(anyInt());
    }

    @Test
    void testAddStockToPortfolio_StockNotFound() {
        // Arrange
        when(portfolioRepo.findById(1)).thenReturn(Optional.of(portfolio));
        when(stocksRepo.findById(999)).thenReturn(Optional.empty());

        // Act
        PortfolioDTO result = portfolioService.addStockToPortfolio(1, 999, 1, 10);

        // Assert
        assertNull(result);
        verify(portfolioRepo, times(1)).findById(1);
        verify(stocksRepo, times(1)).findById(999);
    }

    @Test
    void testAddStockToPortfolio_ExistingStock() {
        // Arrange
        PortfolioStock existingPS = new PortfolioStock();
        existingPS.setPortfolio(portfolio);
        existingPS.setStock(stocks);
        existingPS.setQuantity(5);

        when(portfolioRepo.findById(1)).thenReturn(Optional.of(portfolio));
        when(stocksRepo.findById(1)).thenReturn(Optional.of(stocks));
        when(portfolioStockRepo.findByPortfolioIdAndStockId(1, 1)).thenReturn(existingPS);
        when(portfolioStockRepo.findByPortfolioId(1)).thenReturn(new ArrayList<>());
        when(portfolioRepo.save(any(Portfolio.class))).thenReturn(portfolio);
        when(transactionRepo.save(any(Transaction.class))).thenReturn(new Transaction());

        // Act
        PortfolioDTO result = portfolioService.addStockToPortfolio(1, 1, 1, 10);

        // Assert
        assertNotNull(result);
        verify(portfolioStockRepo, times(1)).save(any(PortfolioStock.class));
        verify(transactionRepo, times(1)).save(any(Transaction.class));
    }

    @Test
    void testRemoveStockFromPortfolio_Success() {
        // Arrange
        PortfolioStock ps = new PortfolioStock();
        ps.setPortfolio(portfolio);
        ps.setStock(stocks);
        ps.setQuantity(10);

        when(portfolioRepo.findById(1)).thenReturn(Optional.of(portfolio));
        when(portfolioStockRepo.findByPortfolioIdAndStockId(1, 1)).thenReturn(ps);
        when(portfolioStockRepo.findByPortfolioId(1)).thenReturn(new ArrayList<>());
        when(portfolioRepo.save(any(Portfolio.class))).thenReturn(portfolio);
        when(transactionRepo.save(any(Transaction.class))).thenReturn(new Transaction());

        // Act
        PortfolioDTO result = portfolioService.removeStockFromPortfolio(1, 1, 1, 5);

        // Assert
        assertNotNull(result);
        assertEquals("My Portfolio", result.getName());
        verify(portfolioRepo, times(1)).findById(1);
        verify(portfolioStockRepo, times(1)).findByPortfolioIdAndStockId(1, 1);
        verify(portfolioStockRepo, times(1)).save(any(PortfolioStock.class));
        verify(transactionRepo, times(1)).save(any(Transaction.class));
    }

    @Test
    void testRemoveStockFromPortfolio_DeletesEntryWhenQtyReachesZero() {
        // Arrange
        PortfolioStock ps = new PortfolioStock();
        ps.setPortfolio(portfolio);
        ps.setStock(stocks);
        ps.setQuantity(5);

        when(portfolioRepo.findById(1)).thenReturn(Optional.of(portfolio));
        when(portfolioStockRepo.findByPortfolioIdAndStockId(1, 1)).thenReturn(ps);
        when(portfolioStockRepo.findByPortfolioId(1)).thenReturn(new ArrayList<>());
        when(portfolioRepo.save(any(Portfolio.class))).thenReturn(portfolio);
        when(transactionRepo.save(any(Transaction.class))).thenReturn(new Transaction());

        // Act
        PortfolioDTO result = portfolioService.removeStockFromPortfolio(1, 1, 1, 5);

        // Assert
        assertNotNull(result);
        verify(portfolioStockRepo, times(1)).delete(any(PortfolioStock.class));
        verify(transactionRepo, times(1)).save(any(Transaction.class));
    }

    @Test
    void testRemoveStockFromPortfolio_PortfolioNotFound() {
        // Arrange
        when(portfolioRepo.findById(999)).thenReturn(Optional.empty());

        // Act
        PortfolioDTO result = portfolioService.removeStockFromPortfolio(999, 1, 1, 5);

        // Assert
        assertNull(result);
        verify(portfolioRepo, times(1)).findById(999);
    }

    @Test
    void testRemoveStockFromPortfolio_StockNotInPortfolio() {
        // Arrange
        when(portfolioRepo.findById(1)).thenReturn(Optional.of(portfolio));
        when(portfolioStockRepo.findByPortfolioIdAndStockId(1, 999)).thenReturn(null);

        // Act
        PortfolioDTO result = portfolioService.removeStockFromPortfolio(1, 999, 1, 5);

        // Assert
        assertNull(result);
        verify(portfolioRepo, times(1)).findById(1);
        verify(portfolioStockRepo, times(1)).findByPortfolioIdAndStockId(1, 999);
    }

    @Test
    void testAddStockToPortfolio_DefaultQtyOne() {
        // Arrange
        when(portfolioRepo.findById(1)).thenReturn(Optional.of(portfolio));
        when(stocksRepo.findById(1)).thenReturn(Optional.of(stocks));
        when(portfolioStockRepo.findByPortfolioIdAndStockId(1, 1)).thenReturn(null);
        when(portfolioStockRepo.findByPortfolioId(1)).thenReturn(new ArrayList<>());
        when(portfolioRepo.save(any(Portfolio.class))).thenReturn(portfolio);
        when(transactionRepo.save(any(Transaction.class))).thenReturn(new Transaction());

        // Act
        PortfolioDTO result = portfolioService.addStockToPortfolio(1, 1, 1, null);

        // Assert
        assertNotNull(result);
        verify(portfolioStockRepo, times(1)).save(any(PortfolioStock.class));
    }
}
