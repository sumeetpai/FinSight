package com.FinSight_Backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.FinSight_Backend.model.Stocks;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface StocksRepo extends JpaRepository<Stocks, Integer> {
    @Query("SELECT s FROM Stocks s WHERE LOWER(s.stock_sym) = LOWER(:symbol)")
    Optional<Stocks> findBySymbolIgnoreCase(@Param("symbol") String symbol);
}