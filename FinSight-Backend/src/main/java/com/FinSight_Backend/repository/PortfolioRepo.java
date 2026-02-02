package com.FinSight_Backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.FinSight_Backend.model.Portfolio;
public interface PortfolioRepo extends JpaRepository<Portfolio, Integer> {
}