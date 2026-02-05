package com.FinSight_Backend.repository;

import com.FinSight_Backend.model.Transaction;
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
class TransactionRepoTest {

    @Autowired
    private TransactionRepo transactionRepo;

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
    }
}
