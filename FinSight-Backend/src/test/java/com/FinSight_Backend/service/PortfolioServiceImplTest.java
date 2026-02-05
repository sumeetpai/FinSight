package com.FinSight_Backend.service;

<<<<<<< HEAD
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

=======
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
>>>>>>> 323f0136fc4e141b74bb75a0c52ada2cc2f11153
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
<<<<<<< HEAD
=======
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
>>>>>>> 323f0136fc4e141b74bb75a0c52ada2cc2f11153

@ExtendWith(MockitoExtension.class)
class PortfolioServiceImplTest {

    @Mock
    private PortfolioRepo portfolioRepo;
<<<<<<< HEAD
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
=======

    @Mock
    private UserRepo userRepo;

    @Mock
    private StocksRepo stocksRepo;

    @Mock
    private TransactionRepo transactionRepo;

    @Mock
    private PortfolioStockRepo portfolioStockRepo;
>>>>>>> 323f0136fc4e141b74bb75a0c52ada2cc2f11153

    @InjectMocks
    private PortfolioServiceImpl portfolioService;

<<<<<<< HEAD
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
=======
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
>>>>>>> 323f0136fc4e141b74bb75a0c52ada2cc2f11153
    }
}
