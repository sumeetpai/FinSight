package com.FinSight_Backend.repository;

import com.FinSight_Backend.model.Portfolio;
import com.FinSight_Backend.model.PortfolioStock;
import com.FinSight_Backend.model.Stocks;
import com.FinSight_Backend.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.ANY)
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
        s.setCurrent_price(100);
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
    }
}
