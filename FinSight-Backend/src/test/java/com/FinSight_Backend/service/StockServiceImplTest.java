package com.FinSight_Backend.service;

import com.FinSight_Backend.dto.MarketPriceDTO;
import com.FinSight_Backend.dto.StocksDTO;
import com.FinSight_Backend.model.Stocks;
import com.FinSight_Backend.repository.PortfolioRepo;
import com.FinSight_Backend.repository.PortfolioStockRepo;
import com.FinSight_Backend.repository.StocksRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StockServiceImplTest {

    @Mock
    private StocksRepo stocksRepo;

    @Mock
    private PortfolioRepo portfolioRepo;

    @Mock
    private PortfolioStockRepo portfolioStockRepo;

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
    }
}
