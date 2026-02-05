package com.FinSight_Backend.repository;

import com.FinSight_Backend.model.Stocks;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
@ActiveProfiles("test")
class StocksRepoTest {

    @Autowired
    private StocksRepo stocksRepo;

    @Test
    void findBySymbolIgnoreCase_finds() {
        Stocks s = new Stocks();
        s.setStock_sym("aapl");
        s.setName("Apple");
        stocksRepo.save(s);

        assertTrue(stocksRepo.findBySymbolIgnoreCase("AAPL").isPresent());
    }

    @Test
    void findBySymbolIgnoreCase_returnsEmpty() {
        assertTrue(stocksRepo.findBySymbolIgnoreCase("NONE").isEmpty());
    }
}
