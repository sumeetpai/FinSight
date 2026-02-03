package com.FinSight_Backend.service;

import com.FinSight_Backend.dto.PortfolioDTO;
import com.FinSight_Backend.model.Portfolio;
import com.FinSight_Backend.repository.PortfolioRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {

    private PortfolioRepo portfolioRepo;
    @Override
    public PortfolioDTO addPortfolio(PortfolioDTO portfolioDTO) {
        Portfolio portfolio = new Portfolio();
        return getPortfolioDTO(portfolioDTO, portfolio);
    }

    private PortfolioDTO getPortfolioDTO(Portfolio portfolio) {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.setPortfolio_id(portfolio.getPortfolio_id());
        portfolioDTO.setShares(portfolio.getShares());
        portfolioDTO.setStock_id(portfolio.getStock_id());
        portfolioDTO.setCurrent_price(portfolio.getCurrent_price());
        portfolioDTO.setCost_basis(portfolio.getCost_basis());
        portfolioDTO.setYield(portfolio.getYield());
        portfolioDTO.setUser(portfolio.getUser());
        portfolioDTO.setStocks(portfolio.getStocks());
        return portfolioDTO;
    }

    private PortfolioDTO getPortfolioDTO(PortfolioDTO portfolioDTO, Portfolio portfolio) {
        portfolio.setShares(portfolioDTO.getShares());
        portfolio.setStock_id(portfolioDTO.getStock_id());
        portfolio.setCurrent_price(portfolioDTO.getCurrent_price());
        portfolio.setCost_basis(portfolioDTO.getCost_basis());
        portfolio.setYield(portfolioDTO.getYield());
        portfolio.setUser(portfolioDTO.getUser());
        portfolio.setStocks(portfolioDTO.getStocks());
        Portfolio savedPortfolio = portfolioRepo.save(portfolio);
        return getPortfolioDTO(savedPortfolio);
    }

    @Override
    public PortfolioDTO getPortfolio(Integer portfolio_id) {
        Portfolio portfolio = portfolioRepo.findById(portfolio_id).isPresent() ? portfolioRepo.findById(portfolio_id).get() : null;
        assert portfolio != null;
        return getPortfolioDTO(portfolio);
    }

    @Override
    public PortfolioDTO updatePortfolio(Integer portfolio_id, PortfolioDTO portfolioDTO) {
        Portfolio portfolio = portfolioRepo.findById(portfolio_id).isPresent() ? portfolioRepo.findById(portfolio_id).get() : null;
        assert portfolio != null;
        return getPortfolioDTO(portfolioDTO, portfolio);
    }

    @Override
    public String deletePortfolio(Integer portfolio_id) {
        Portfolio portfolio = portfolioRepo.findById(portfolio_id).isPresent() ? portfolioRepo.findById(portfolio_id).get() : null;
        assert portfolio != null;
        portfolioRepo.delete(portfolio);
        return "Portfolio deleted";
    }
}