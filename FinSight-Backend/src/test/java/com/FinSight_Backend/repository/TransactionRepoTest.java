package com.FinSight_Backend.repository;

import com.FinSight_Backend.model.Transaction;
<<<<<<< HEAD
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
=======
import com.FinSight_Backend.model.User;
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
>>>>>>> 323f0136fc4e141b74bb75a0c52ada2cc2f11153
class TransactionRepoTest {

    @Autowired
    private TransactionRepo transactionRepo;

<<<<<<< HEAD
    @Test
    void findByPortfolioId_returnsRows() {
        Transaction t = new Transaction();
        t.setPortfolio_id(1);
        t.setStock_id(2);
        t.setUser_id(3);
        t.setType("ADD");
        t.setQty(1);
        transactionRepo.save(t);

        List<Transaction> found = transactionRepo.findByPortfolioId(1);
        assertEquals(1, found.size());
    }

    @Test
    void deleteByPortfolioId_deletesRows() {
        Transaction t = new Transaction();
        t.setPortfolio_id(2);
        t.setStock_id(2);
        t.setUser_id(3);
        t.setType("ADD");
        t.setQty(1);
        transactionRepo.save(t);

        transactionRepo.deleteByPortfolioId(2);
        List<Transaction> found = transactionRepo.findByPortfolioId(2);
        assertEquals(0, found.size());
=======
    @Autowired
    private UserRepo userRepo;

    @Test
    @Rollback
    void testSaveAndFind() {
        User u = new User();
        u.setUsername("tuser");
        u.setEmail("t@example.com");
        u.setPassword("p");
        User persisted = userRepo.save(u);

        Transaction t = new Transaction();
        t.setUser_id(persisted.getUser_id());
        t.setStock_id(1);
        t.setPortfolio_id(1);
        t.setType("ADD");
        t.setQty(3);
        t.setPrice(100.0);

        Transaction saved = transactionRepo.save(t);
        assertThat(saved.getT_id()).isNotNull();

        Transaction found = transactionRepo.findById(saved.getT_id()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getType()).isEqualTo("ADD");
>>>>>>> 323f0136fc4e141b74bb75a0c52ada2cc2f11153
    }
}
