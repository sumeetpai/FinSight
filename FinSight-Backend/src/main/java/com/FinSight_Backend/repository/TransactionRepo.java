package com.FinSight_Backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.FinSight_Backend.model.Transaction;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface TransactionRepo extends JpaRepository<Transaction, Integer> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Transaction t WHERE t.portfolio_id = :portfolioId")
    void deleteByPortfolioId(@Param("portfolioId") Integer portfolioId);

    @Query("SELECT t FROM Transaction t WHERE t.portfolio_id = :portfolioId")
    List<Transaction> findByPortfolioId(@Param("portfolioId") Integer portfolioId);
}