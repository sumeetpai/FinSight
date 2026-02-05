package com.FinSight_Backend.service;

<<<<<<< HEAD
import com.FinSight_Backend.dto.MarketPriceDTO;
import com.FinSight_Backend.dto.StocksDTO;
=======
import com.FinSight_Backend.dto.StocksDTO;
import com.FinSight_Backend.model.Portfolio;
import com.FinSight_Backend.model.PortfolioStock;
>>>>>>> 323f0136fc4e141b74bb75a0c52ada2cc2f11153
import com.FinSight_Backend.model.Stocks;
import com.FinSight_Backend.repository.PortfolioRepo;
import com.FinSight_Backend.repository.PortfolioStockRepo;
import com.FinSight_Backend.repository.StocksRepo;
<<<<<<< HEAD
=======
import org.junit.jupiter.api.BeforeEach;
>>>>>>> 323f0136fc4e141b74bb75a0c52ada2cc2f11153
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
<<<<<<< HEAD
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
=======
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
>>>>>>> 323f0136fc4e141b74bb75a0c52ada2cc2f11153

@ExtendWith(MockitoExtension.class)
class StockServiceImplTest {

    @Mock
    private StocksRepo stocksRepo;

    @Mock
    private PortfolioRepo portfolioRepo;

    @Mock
    private PortfolioStockRepo portfolioStockRepo;

<<<<<<< HEAD
    @Mock
    private MarketDataClient marketDataClient;

    @InjectMocks
    private StockServiceImpl stockService;

    @Test
    void getStocks_appliesLivePrice() {
        Stocks s = new Stocks();
        s.setStock_id(1);
        s.setStock_sym("AAPL");
        s.setCurrent_price(100.0);
        Mockito.when(stocksRepo.findById(1)).thenReturn(Optional.of(s));

        MarketPriceDTO live = new MarketPriceDTO();
        live.setPrice(123.45);
        Mockito.when(marketDataClient.getPrice("AAPL")).thenReturn(live);

        StocksDTO dto = stockService.getStocks(1);
        assertNotNull(dto);
        assertEquals(123.45, dto.getCurrent_price());
    }

    @Test
    void getStocksBySymbol_returnsEmptyWhenNotFound() {
        Mockito.when(stocksRepo.findBySymbolIgnoreCase("NONE")).thenReturn(Optional.empty());
        assertTrue(stockService.getStocksBySymbol("NONE").isEmpty());
    }

    @Test
    void getStocksBySymbol_appliesLivePrice() {
        Stocks s = new Stocks();
        s.setStock_sym("MSFT");
        s.setCurrent_price(50.0);
        Mockito.when(stocksRepo.findBySymbolIgnoreCase("MSFT")).thenReturn(Optional.of(s));

        MarketPriceDTO live = new MarketPriceDTO();
        live.setPrice(77.0);
        Mockito.when(marketDataClient.getPrice("MSFT")).thenReturn(live);

        Optional<StocksDTO> out = stockService.getStocksBySymbol("MSFT");
        assertTrue(out.isPresent());
        assertEquals(77.0, out.get().getCurrent_price());
    }

    @Test
    void updateStocks_persistsFields() {
        Stocks existing = new Stocks();
        existing.setStock_id(1);
        Mockito.when(stocksRepo.findById(1)).thenReturn(Optional.of(existing));
        Mockito.when(stocksRepo.save(Mockito.any())).thenAnswer(i -> i.getArgument(0));

        StocksDTO dto = new StocksDTO();
        dto.setStock_sym("AAPL");
        dto.setName("Apple");
        dto.setCurrent_price(123.0);

        StocksDTO out = stockService.updateStocks(1, dto);
        assertNotNull(out);
        assertEquals("AAPL", out.getStock_sym());
    }

    @Test
    void deleteStocks_returnsMessage() {
        Stocks s = new Stocks();
        s.setStock_id(1);
        Mockito.when(stocksRepo.findById(1)).thenReturn(Optional.of(s));
        String msg = stockService.deleteStocks(1);
        assertNotNull(msg);
=======
    @InjectMocks
    private StockServiceImpl stockService;

    private StocksDTO stocksDTO;
    private Stocks stocks;

    @BeforeEach
    void setUp() {
        stocksDTO = new StocksDTO();
        stocksDTO.setName("Apple Inc.");
        stocksDTO.setStock_sym("AAPL");
        stocksDTO.setDay_before_price(150.0);
        stocksDTO.setMarket_cap(2500000000000.0);
        stocksDTO.setCurrent_price(155.0);
        stocksDTO.setPortfolio_ids(null);

        stocks = new Stocks();
        stocks.setStock_id(1);
        stocks.setName("Apple Inc.");
        stocks.setStock_sym("AAPL");
        stocks.setDay_before_price(150.0);
        stocks.setMarket_cap(2500000000000.0);
        stocks.setCurrent_price(155.0);
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
        updateDTO.setDay_before_price(155.0);
        updateDTO.setMarket_cap(2600000000000.0);
        updateDTO.setCurrent_price(160.00);

        Stocks updatedStocks = new Stocks();
        updatedStocks.setStock_id(1);
        updatedStocks.setName("Apple Inc. Updated");
        updatedStocks.setStock_sym("AAPL");
        updatedStocks.setDay_before_price(155.00);
        updatedStocks.setMarket_cap(2600000000000.00);
        updatedStocks.setCurrent_price(160.00);

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
        updateDTO.setDay_before_price(150.00);
        updateDTO.setMarket_cap(2500000000000.0);
        updateDTO.setCurrent_price(155.00);
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
>>>>>>> 323f0136fc4e141b74bb75a0c52ada2cc2f11153
    }
}
