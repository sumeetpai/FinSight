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

    @Modifying
    @Transactional
    @Query("DELETE FROM PortfolioStock ps WHERE ps.portfolio.portfolio_id = :portfolioId")
    void deleteByPortfolioId(@Param("portfolioId") Integer portfolioId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM portfolio_stocks WHERE portfolio_portfolio_id = :portfolioId", nativeQuery = true)
    void deleteLegacyByPortfolioId(@Param("portfolioId") Integer portfolioId);

    // Combined native delete: handles either column name that might exist in legacy dumps
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM portfolio_stocks WHERE portfolio_portfolio_id = :portfolioId OR portfolio_id = :portfolioId", nativeQuery = true)
    void deleteLegacyByPortfolioIdCombined(@Param("portfolioId") Integer portfolioId);

    // native delete for singular entity table naming variations
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM portfolio_stock WHERE portfolio_id = :portfolioId OR portfolio_portfolio_id = :portfolioId", nativeQuery = true)
    void deleteNativePortfolioStockByPortfolioIdCombined(@Param("portfolioId") Integer portfolioId);
}
