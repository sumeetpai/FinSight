package com.FinSight_Backend.service;

import com.FinSight_Backend.dto.StocksDTO;
import com.FinSight_Backend.model.Portfolio;
import com.FinSight_Backend.model.PortfolioStock;
import com.FinSight_Backend.model.Stocks;
import com.FinSight_Backend.repository.PortfolioRepo;
import com.FinSight_Backend.repository.PortfolioStockRepo;
import com.FinSight_Backend.repository.StocksRepo;
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
class StockServiceImplTest {

    @Mock
    private StocksRepo stocksRepo;

    @Mock
    private PortfolioRepo portfolioRepo;

    @Mock
    private PortfolioStockRepo portfolioStockRepo;

    @InjectMocks
    private StockServiceImpl stockService;

    private StocksDTO stocksDTO;
    private Stocks stocks;

    @BeforeEach
    void setUp() {
        stocksDTO = new StocksDTO();
        stocksDTO.setName("Apple Inc.");
        stocksDTO.setStock_sym("AAPL");
        stocksDTO.setDay_before_price(150);
        stocksDTO.setMarket_cap(2500000000000L);
        stocksDTO.setCurrent_price(155);
        stocksDTO.setPortfolio_ids(null);

        stocks = new Stocks();
        stocks.setStock_id(1);
        stocks.setName("Apple Inc.");
        stocks.setStock_sym("AAPL");
        stocks.setDay_before_price(150);
        stocks.setMarket_cap(2500000000000L);
        stocks.setCurrent_price(155);
    }

    @Test
    void testAddStocks_Success() {
        // Arrange
        when(stocksRepo.save(any(Stocks.class))).thenReturn(stocks);
        when(portfolioStockRepo.findByStockId(1)).thenReturn(new ArrayList<>());

        // Act
        StocksDTO result = stockService.addStocks(stocksDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Apple Inc.", result.getName());
        assertEquals("AAPL", result.getStock_sym());
        assertEquals(155, result.getCurrent_price());
        verify(stocksRepo, times(1)).save(any(Stocks.class));
    }

    @Test
    void testAddStocks_WithPortfolios() {
        // Arrange
        List<Integer> portfolioIds = new ArrayList<>();
        portfolioIds.add(1);
        portfolioIds.add(2);
        stocksDTO.setPortfolio_ids(portfolioIds);

        Portfolio portfolio1 = new Portfolio();
        portfolio1.setPortfolio_id(1);

        Portfolio portfolio2 = new Portfolio();
        portfolio2.setPortfolio_id(2);

        when(portfolioRepo.findById(1)).thenReturn(Optional.of(portfolio1));
        when(portfolioRepo.findById(2)).thenReturn(Optional.of(portfolio2));
        when(stocksRepo.save(any(Stocks.class))).thenReturn(stocks);
        when(portfolioStockRepo.findByStockId(1)).thenReturn(new ArrayList<>());

        // Act
        StocksDTO result = stockService.addStocks(stocksDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Apple Inc.", result.getName());
        verify(portfolioRepo, times(2)).findById(anyInt());
        verify(stocksRepo, times(1)).save(any(Stocks.class));
    }

    @Test
    void testGetStocks_Success() {
        // Arrange
        when(stocksRepo.findById(1)).thenReturn(Optional.of(stocks));
        when(portfolioStockRepo.findByStockId(1)).thenReturn(new ArrayList<>());

        // Act
        StocksDTO result = stockService.getStocks(1);

        // Assert
        assertNotNull(result);
        assertEquals("Apple Inc.", result.getName());
        assertEquals("AAPL", result.getStock_sym());
        verify(stocksRepo, times(2)).findById(1);
    }

    @Test
    void testGetStocks_WithPortfolios() {
        // Arrange
        Portfolio portfolio1 = new Portfolio();
        portfolio1.setPortfolio_id(1);

        PortfolioStock ps1 = new PortfolioStock();
        ps1.setPortfolio(portfolio1);
        ps1.setStock(stocks);

        List<PortfolioStock> portfolioStocks = new ArrayList<>();
        portfolioStocks.add(ps1);

        when(stocksRepo.findById(1)).thenReturn(Optional.of(stocks));
        when(portfolioStockRepo.findByStockId(1)).thenReturn(portfolioStocks);

        // Act
        StocksDTO result = stockService.getStocks(1);

        // Assert
        assertNotNull(result);
        assertEquals("Apple Inc.", result.getName());
        assertNotNull(result.getPortfolio_ids());
        assertEquals(1, result.getPortfolio_ids().size());
        assertEquals(1, result.getPortfolio_ids().get(0));
        verify(stocksRepo, times(2)).findById(1);
    }

    @Test
    void testUpdateStocks_Success() {
        // Arrange
        StocksDTO updateDTO = new StocksDTO();
        updateDTO.setName("Apple Inc. Updated");
        updateDTO.setStock_sym("AAPL");
        updateDTO.setDay_before_price(155);
        updateDTO.setMarket_cap(2600000000000L);
        updateDTO.setCurrent_price(160);

        Stocks updatedStocks = new Stocks();
        updatedStocks.setStock_id(1);
        updatedStocks.setName("Apple Inc. Updated");
        updatedStocks.setStock_sym("AAPL");
        updatedStocks.setDay_before_price(155);
        updatedStocks.setMarket_cap(2600000000000L);
        updatedStocks.setCurrent_price(160);

        when(stocksRepo.findById(1)).thenReturn(Optional.of(stocks));
        when(stocksRepo.save(any(Stocks.class))).thenReturn(updatedStocks);
        when(portfolioStockRepo.findByStockId(1)).thenReturn(new ArrayList<>());

        // Act
        StocksDTO result = stockService.updateStocks(1, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Apple Inc. Updated", result.getName());
        assertEquals(160, result.getCurrent_price());
        verify(stocksRepo, times(2)).findById(1);
        verify(stocksRepo, times(1)).save(any(Stocks.class));
    }

    @Test
    void testDeleteStocks_Success() {
        // Arrange
        when(stocksRepo.findById(1)).thenReturn(Optional.of(stocks));
        when(portfolioStockRepo.findByStockId(1)).thenReturn(new ArrayList<>());
        doNothing().when(stocksRepo).delete(any(Stocks.class));

        // Act
        String result = stockService.deleteStocks(1);

        // Assert
        assertNotNull(result);
        assertTrue(result.length() > 0);
        verify(stocksRepo, times(2)).findById(1);
        verify(stocksRepo, times(1)).delete(stocks);
    }

    @Test
    void testUpdateStocks_WithPortfolios() {
        // Arrange
        List<Integer> portfolioIds = new ArrayList<>();
        portfolioIds.add(1);
        portfolioIds.add(2);

        StocksDTO updateDTO = new StocksDTO();
        updateDTO.setName("Apple Inc.");
        updateDTO.setStock_sym("AAPL");
        updateDTO.setDay_before_price(150);
        updateDTO.setMarket_cap(2500000000000L);
        updateDTO.setCurrent_price(155);
        updateDTO.setPortfolio_ids(portfolioIds);

        Portfolio portfolio1 = new Portfolio();
        portfolio1.setPortfolio_id(1);

        Portfolio portfolio2 = new Portfolio();
        portfolio2.setPortfolio_id(2);

        Stocks updatedStocks = new Stocks();
        updatedStocks.setStock_id(1);
        updatedStocks.setName("Apple Inc.");
        updatedStocks.setStock_sym("AAPL");

        when(stocksRepo.findById(1)).thenReturn(Optional.of(stocks));
        when(portfolioRepo.findById(1)).thenReturn(Optional.of(portfolio1));
        when(portfolioRepo.findById(2)).thenReturn(Optional.of(portfolio2));
        when(stocksRepo.save(any(Stocks.class))).thenReturn(updatedStocks);
        when(portfolioStockRepo.findByStockId(1)).thenReturn(new ArrayList<>());

        // Act
        StocksDTO result = stockService.updateStocks(1, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Apple Inc.", result.getName());
        verify(stocksRepo, times(2)).findById(1);
        verify(portfolioRepo, times(2)).findById(anyInt());
        verify(stocksRepo, times(1)).save(any(Stocks.class));
    }

    @Test
    void testAddStocks_WithInvalidPortfolio() {
        // Arrange
        List<Integer> portfolioIds = new ArrayList<>();
        portfolioIds.add(1);
        portfolioIds.add(999);
        stocksDTO.setPortfolio_ids(portfolioIds);

        Portfolio portfolio1 = new Portfolio();
        portfolio1.setPortfolio_id(1);

        when(portfolioRepo.findById(1)).thenReturn(Optional.of(portfolio1));
        when(portfolioRepo.findById(999)).thenReturn(Optional.empty());
        when(stocksRepo.save(any(Stocks.class))).thenReturn(stocks);
        when(portfolioStockRepo.findByStockId(1)).thenReturn(new ArrayList<>());

        // Act
        StocksDTO result = stockService.addStocks(stocksDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Apple Inc.", result.getName());
        verify(stocksRepo, times(1)).save(any(Stocks.class));
    }
}
