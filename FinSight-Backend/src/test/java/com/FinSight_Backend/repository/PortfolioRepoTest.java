package com.FinSight_Backend.repository;

import com.FinSight_Backend.model.Portfolio;
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
class PortfolioRepoTest {

    @Autowired
    private PortfolioRepo portfolioRepo;

    @Autowired
    private UserRepo userRepo;

    @Test
    void findByUserId_returnsPortfolios() {
        User user = new User();
        user.setUsername("u");
        user = userRepo.save(user);

        Portfolio p = new Portfolio();
        p.setName("P1");
        p.setUser(user);
        portfolioRepo.save(p);

        List<Portfolio> found = portfolioRepo.findByUserId(user.getUser_id());
        assertEquals(1, found.size());
    }
}
