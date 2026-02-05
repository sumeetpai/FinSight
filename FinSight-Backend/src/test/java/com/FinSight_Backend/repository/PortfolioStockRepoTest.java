package com.FinSight_Backend.repository;

import com.FinSight_Backend.model.Portfolio;
import com.FinSight_Backend.model.PortfolioStock;
import com.FinSight_Backend.model.Stocks;
import com.FinSight_Backend.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
@ActiveProfiles("test")
class PortfolioStockRepoTest {

    @Autowired
    private PortfolioStockRepo portfolioStockRepo;

    @Autowired
    private PortfolioRepo portfolioRepo;

    @Autowired
    private StocksRepo stocksRepo;

    @Autowired
    private UserRepo userRepo;

    @Test
    void findByPortfolioIdAndStockId_returnsEntry() {
        User user = new User();
        user.setUsername("u");
        user = userRepo.save(user);

        Portfolio portfolio = new Portfolio();
        portfolio.setName("P");
        portfolio.setUser(user);
        portfolio = portfolioRepo.save(portfolio);

        Stocks stock = new Stocks();
        stock.setStock_sym("AAPL");
        stock = stocksRepo.save(stock);

        PortfolioStock ps = new PortfolioStock();
        ps.setPortfolio(portfolio);
        ps.setStock(stock);
        ps.setQuantity(5);
        portfolioStockRepo.save(ps);

        PortfolioStock found = portfolioStockRepo.findByPortfolioIdAndStockId(portfolio.getPortfolio_id(), stock.getStock_id());
        assertNotNull(found);
    }

    @Test
    void findByPortfolioId_returnsList() {
        User user = new User();
        user.setUsername("u2");
        user = userRepo.save(user);

        Portfolio portfolio = new Portfolio();
        portfolio.setName("P2");
        portfolio.setUser(user);
        portfolio = portfolioRepo.save(portfolio);

        Stocks stock = new Stocks();
        stock.setStock_sym("MSFT");
        stock = stocksRepo.save(stock);

        PortfolioStock ps = new PortfolioStock();
        ps.setPortfolio(portfolio);
        ps.setStock(stock);
        ps.setQuantity(2);
        portfolioStockRepo.save(ps);

        List<PortfolioStock> found = portfolioStockRepo.findByPortfolioId(portfolio.getPortfolio_id());
        assertEquals(1, found.size());
    }

    @Test
    void findByStockId_returnsList() {
        User user = new User();
        user.setUsername("u3");
        user = userRepo.save(user);

        Portfolio portfolio = new Portfolio();
        portfolio.setName("P3");
        portfolio.setUser(user);
        portfolio = portfolioRepo.save(portfolio);

        Stocks stock = new Stocks();
        stock.setStock_sym("TSLA");
        stock = stocksRepo.save(stock);

        PortfolioStock ps = new PortfolioStock();
        ps.setPortfolio(portfolio);
        ps.setStock(stock);
        ps.setQuantity(1);
        portfolioStockRepo.save(ps);

        List<PortfolioStock> found = portfolioStockRepo.findByStockId(stock.getStock_id());
        assertEquals(1, found.size());
    }
}
