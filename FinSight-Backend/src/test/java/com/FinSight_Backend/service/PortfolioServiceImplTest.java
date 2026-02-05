package com.FinSight_Backend.service;

import com.FinSight_Backend.dto.MarketPriceDTO;
import com.FinSight_Backend.dto.PortfolioDTO;
import com.FinSight_Backend.model.Portfolio;
import com.FinSight_Backend.model.PortfolioStock;
import com.FinSight_Backend.model.Stocks;
import com.FinSight_Backend.repository.PortfolioRepo;
import com.FinSight_Backend.repository.PortfolioStockRepo;
import com.FinSight_Backend.repository.StocksRepo;
import com.FinSight_Backend.repository.TransactionRepo;
import com.FinSight_Backend.repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

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
    @Mock
    private MarketDataClient marketDataClient;
    @Mock
    private EntityManager em;

    @InjectMocks
    private PortfolioServiceImpl portfolioService;

    @Test
    void addPortfolio_returnsNullWhenUserMissing() {
        PortfolioDTO dto = new PortfolioDTO();
        dto.setUser_id(1);
        Mockito.when(userRepo.existsById(1)).thenReturn(false);
        assertNull(portfolioService.addPortfolio(dto));
    }

    @Test
    void addStockToPortfolio_updatesCostBasis() {
        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolio_id(1);
        portfolio.setCost_basis(100.0);

        Stocks stock = new Stocks();
        stock.setStock_id(2);
        stock.setCurrent_price(10.0);

        Mockito.when(portfolioRepo.findById(1)).thenReturn(Optional.of(portfolio));
        Mockito.when(stocksRepo.findById(2)).thenReturn(Optional.of(stock));
        Mockito.when(portfolioStockRepo.findByPortfolioIdAndStockId(1, 2)).thenReturn(null);
        Mockito.when(portfolioRepo.save(Mockito.any())).thenAnswer(i -> i.getArgument(0));

        PortfolioDTO out = portfolioService.addStockToPortfolio(1, 2, 3, 2);
        assertNotNull(out);

        ArgumentCaptor<Portfolio> captor = ArgumentCaptor.forClass(Portfolio.class);
        Mockito.verify(portfolioRepo, Mockito.atLeastOnce()).save(captor.capture());
        Portfolio saved = captor.getValue();
        assertEquals(120.0, saved.getCost_basis());
    }

    @Test
    void removeStockFromPortfolio_usesLivePriceForTransaction() {
        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolio_id(1);

        Stocks stock = new Stocks();
        stock.setStock_id(2);
        stock.setStock_sym("AAPL");
        stock.setCurrent_price(10.0);

        PortfolioStock ps = new PortfolioStock();
        ps.setStock(stock);
        ps.setPortfolio(portfolio);
        ps.setQuantity(5);

        Mockito.when(portfolioRepo.findById(1)).thenReturn(Optional.of(portfolio));
        Mockito.when(portfolioStockRepo.findByPortfolioIdAndStockId(1, 2)).thenReturn(ps);
        Mockito.when(portfolioStockRepo.findByPortfolioId(1)).thenReturn(List.of(ps));
        Mockito.when(portfolioRepo.save(Mockito.any())).thenAnswer(i -> i.getArgument(0));

        MarketPriceDTO live = new MarketPriceDTO();
        live.setPrice(55.0);
        Mockito.when(marketDataClient.getPrice("AAPL")).thenReturn(live);

        portfolioService.removeStockFromPortfolio(1, 2, 3, 1);

        ArgumentCaptor<com.FinSight_Backend.model.Transaction> txCaptor =
                ArgumentCaptor.forClass(com.FinSight_Backend.model.Transaction.class);
        Mockito.verify(transactionRepo).save(txCaptor.capture());
        assertEquals(55.0, txCaptor.getValue().getPrice());
    }

    @Test
    void updatePortfolio_returnsNullWhenNotFound() {
        Mockito.when(portfolioRepo.findById(1)).thenReturn(Optional.empty());
        assertNull(portfolioService.updatePortfolio(1, new PortfolioDTO()));
    }

    @Test
    void setPortfolioActiveStatus_updatesFlag() {
        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolio_id(1);
        Mockito.when(portfolioRepo.findById(1)).thenReturn(Optional.of(portfolio));
        Mockito.when(portfolioRepo.save(Mockito.any())).thenAnswer(i -> i.getArgument(0));

        PortfolioDTO out = portfolioService.setPortfolioActiveStatus(1, true);
        assertNotNull(out);
        assertEquals(Boolean.TRUE, out.getActive());
    }
}
