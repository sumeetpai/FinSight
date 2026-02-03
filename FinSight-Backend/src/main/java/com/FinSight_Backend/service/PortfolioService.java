package com.FinSight_Backend.service;

import com.FinSight_Backend.dto.PortfolioDTO;

public interface PortfolioService {
    PortfolioDTO addPortfolio(PortfolioDTO portfolioDTO);
    PortfolioDTO getPortfolio(Integer portfolio_id);
    PortfolioDTO updatePortfolio(Integer portfolio_id, PortfolioDTO portfolioDTO);
    String deletePortfolio(Integer portfolio_id);
    PortfolioDTO addStockToPortfolio(Integer portfolio_id, Integer stock_id, Integer user_id, Integer qty);
    PortfolioDTO removeStockFromPortfolio(Integer portfolio_id, Integer stock_id, Integer user_id, Integer qty);
}