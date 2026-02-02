package com.FinSight_Backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.FinSight_Backend.model.Stocks;
public interface StocksRepo extends JpaRepository<Stocks, Integer> {
}