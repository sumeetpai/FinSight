package com.FinSight_Backend.repository;

import com.FinSight_Backend.model.Stocks;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.ANY)
class StockRepoTest {

    @Autowired
    private StocksRepo stocksRepo;

    @Test
    @Rollback
    void testSaveAndFind() {
        Stocks s = new Stocks();
        s.setStock_sym("AAA");
        s.setName("AAA Inc");
        s.setCurrent_price(50.00);
        Stocks saved = stocksRepo.save(s);

        assertThat(saved.getStock_id()).isNotNull();

        Stocks found = stocksRepo.findById(saved.getStock_id()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getStock_sym()).isEqualTo("AAA");
    }
}
