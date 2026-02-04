package com.FinSight_Backend.repository;

import com.FinSight_Backend.model.Portfolio;
import com.FinSight_Backend.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
// Allow DataJpaTest to replace the DataSource with an embedded one for fast, isolated tests
@AutoConfigureTestDatabase(replace = Replace.ANY)
class PortfolioRepoTest {

    @Autowired
    private PortfolioRepo portfolioRepo;

    @Autowired
    private UserRepo userRepo; // persist users explicitly to avoid transient references

    @Test
    @Rollback
    void testSaveAndFindById() {
        // create and persist user
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("pass");
        User savedUser = userRepo.save(user);

        // create portfolio
        Portfolio p = new Portfolio();
        p.setName("Repo Test Portfolio");
        p.setTotal_value(1000L);
        p.setCost_basis(800L);
        p.setYield(200);
        p.setUser(savedUser);

        // save portfolio
        Portfolio saved = portfolioRepo.save(p);

        assertThat(saved.getPortfolio_id()).isNotNull();

        Portfolio found = portfolioRepo.findById(saved.getPortfolio_id()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Repo Test Portfolio");
    }

    @Test
    @Rollback
    void testFindByUserId() {
        // user A
        User userA = new User();
        userA.setUsername("userA");
        userA.setEmail("a@example.com");
        userA.setPassword("p");
        User persistedA = userRepo.save(userA);

        Portfolio pa1 = new Portfolio();
        pa1.setName("A1");
        pa1.setUser(persistedA);

        Portfolio pa2 = new Portfolio();
        pa2.setName("A2");
        pa2.setUser(persistedA);

        // user B
        User userB = new User();
        userB.setUsername("userB");
        userB.setEmail("b@example.com");
        userB.setPassword("p");
        User persistedB = userRepo.save(userB);

        Portfolio pb = new Portfolio();
        pb.setName("B1");
        pb.setUser(persistedB);

        // save all
        portfolioRepo.save(pa1);
        portfolioRepo.save(pa2);
        portfolioRepo.save(pb);

        List<Portfolio> all = portfolioRepo.findAll();
        assertThat(all).isNotEmpty();

        List<Portfolio> userAPortfolios = portfolioRepo.findByUserId(persistedA.getUser_id());
        assertThat(userAPortfolios).hasSizeGreaterThanOrEqualTo(2);
        assertThat(userAPortfolios).extracting("name").contains("A1", "A2");
    }

    @Test
    @Rollback
    void testDeletePortfolio() {
        User u = new User();
        u.setUsername("deluser");
        u.setEmail("del@example.com");
        u.setPassword("p");
        User persisted = userRepo.save(u);

        Portfolio p = new Portfolio();
        p.setName("ToDelete");
        p.setUser(persisted);

        Portfolio saved = portfolioRepo.save(p);
        Integer id = saved.getPortfolio_id();
        assertThat(portfolioRepo.findById(id)).isPresent();

        portfolioRepo.delete(saved);

        assertThat(portfolioRepo.findById(id)).isEmpty();
    }
}
