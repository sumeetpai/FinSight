package com.FinSight_Backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.FinSight_Backend.model.Transaction;
public interface TransactionRepo extends JpaRepository<Transaction, Integer> {
}