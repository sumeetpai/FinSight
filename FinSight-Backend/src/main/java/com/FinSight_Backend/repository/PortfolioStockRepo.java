package com.FinSight_Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.FinSight_Backend.model.PortfolioStock;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

public interface PortfolioStockRepo extends JpaRepository<PortfolioStock, Long> {

    @Query("SELECT ps FROM PortfolioStock ps WHERE ps.portfolio.portfolio_id = :portfolioId")
    List<PortfolioStock> findByPortfolioId(@Param("portfolioId") Integer portfolioId);

    @Query("SELECT ps FROM PortfolioStock ps WHERE ps.portfolio.portfolio_id = :portfolioId AND ps.stock.stock_id = :stockId")
    PortfolioStock findByPortfolioIdAndStockId(@Param("portfolioId") Integer portfolioId, @Param("stockId") Integer stockId);

    @Query("SELECT ps FROM PortfolioStock ps WHERE ps.stock.stock_id = :stockId")
    List<PortfolioStock> findByStockId(@Param("stockId") Integer stockId);

    @Modifying
    @Transactional
    @Query("DELETE FROM PortfolioStock ps WHERE ps.stock.stock_id = :stockId")
    void deleteByStockId(@Param("stockId") Integer stockId);
}
