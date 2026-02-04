package com.FinSight_Backend.repository;

import com.FinSight_Backend.model.Transaction;
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
class TransactionRepoTest {

    @Autowired
    private TransactionRepo transactionRepo;

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
        t.setPrice(100L);

        Transaction saved = transactionRepo.save(t);
        assertThat(saved.getT_id()).isNotNull();

        Transaction found = transactionRepo.findById(saved.getT_id()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getType()).isEqualTo("ADD");
    }
}
