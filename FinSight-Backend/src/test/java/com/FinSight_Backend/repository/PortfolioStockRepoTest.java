package com.FinSight_Backend.repository;

import com.FinSight_Backend.model.Portfolio;
import com.FinSight_Backend.model.PortfolioStock;
import com.FinSight_Backend.model.Stocks;
import com.FinSight_Backend.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
<<<<<<< HEAD
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
=======
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
>>>>>>> 323f0136fc4e141b74bb75a0c52ada2cc2f11153
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

<<<<<<< HEAD
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
@ActiveProfiles("test")
=======
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.ANY)
>>>>>>> 323f0136fc4e141b74bb75a0c52ada2cc2f11153
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
<<<<<<< HEAD
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
=======
    @Rollback
    void testFindByPortfolioAndStockAndDelete() {
        User u = new User();
        u.setUsername("psuser");
        u.setEmail("ps@example.com");
        u.setPassword("p");
        User savedUser = userRepo.save(u);

        Portfolio p = new Portfolio();
        p.setName("PS Portfolio");
        p.setUser(savedUser);
        Portfolio savedPortfolio = portfolioRepo.save(p);

        Stocks s = new Stocks();
        s.setStock_sym("TST");
        s.setName("Test Stock");
        s.setCurrent_price(100.0);
        Stocks savedStock = stocksRepo.save(s);

        PortfolioStock ps = new PortfolioStock();
        ps.setPortfolio(savedPortfolio);
        ps.setStock(savedStock);
        ps.setQuantity(7);
        PortfolioStock savedPS = portfolioStockRepo.save(ps);

        List<PortfolioStock> byPortfolio = portfolioStockRepo.findByPortfolioId(savedPortfolio.getPortfolio_id());
        assertThat(byPortfolio).isNotEmpty();
        assertThat(byPortfolio.get(0).getQuantity()).isEqualTo(7);

        PortfolioStock found = portfolioStockRepo.findByPortfolioIdAndStockId(savedPortfolio.getPortfolio_id(), savedStock.getStock_id());
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(savedPS.getId());

        List<PortfolioStock> byStock = portfolioStockRepo.findByStockId(savedStock.getStock_id());
        assertThat(byStock).isNotEmpty();

        // delete by stock id
        portfolioStockRepo.deleteByStockId(savedStock.getStock_id());
        List<PortfolioStock> afterDelete = portfolioStockRepo.findByStockId(savedStock.getStock_id());
        assertThat(afterDelete).isEmpty();
>>>>>>> 323f0136fc4e141b74bb75a0c52ada2cc2f11153
    }
}
