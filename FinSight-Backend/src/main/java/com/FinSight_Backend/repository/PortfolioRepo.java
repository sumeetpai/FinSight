package com.FinSight_Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.FinSight_Backend.model.Portfolio;
import java.util.List;

public interface PortfolioRepo extends JpaRepository<Portfolio, Integer> {

    @Query("SELECT p FROM Portfolio p WHERE p.user.user_id = :userId")
    List<Portfolio> findByUserId(@Param("userId") Integer userId);
}