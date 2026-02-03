package com.FinSight_Backend.service;

import com.FinSight_Backend.dto.PortfolioDTO;
import com.FinSight_Backend.model.Portfolio;
import com.FinSight_Backend.repository.PortfolioRepo;
import com.FinSight_Backend.repository.UserRepo;
import com.FinSight_Backend.repository.StocksRepo;
import com.FinSight_Backend.model.User;
import com.FinSight_Backend.model.Stocks;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {

    private PortfolioRepo portfolioRepo;
    private UserRepo userRepo;
    private StocksRepo stocksRepo;
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
        portfolioDTO.setUser_id(portfolio.getUser() != null ? portfolio.getUser().getUser_id() : null);
        portfolioDTO.setStock_ids(portfolio.getStocks() != null ?
                portfolio.getStocks().stream().map(Stocks::getStock_id).collect(Collectors.toList()) : null);
        return portfolioDTO;
    }

    private PortfolioDTO getPortfolioDTO(PortfolioDTO portfolioDTO, Portfolio portfolio) {
        portfolio.setShares(portfolioDTO.getShares());
        portfolio.setStock_id(portfolioDTO.getStock_id());
        portfolio.setCurrent_price(portfolioDTO.getCurrent_price());
        portfolio.setCost_basis(portfolioDTO.getCost_basis());
        portfolio.setYield(portfolioDTO.getYield());
        if (portfolioDTO.getUser_id() != null) {
            User u = userRepo.findById(portfolioDTO.getUser_id()).orElse(null);
            portfolio.setUser(u);
        } else {
            portfolio.setUser(null);
        }
        if (portfolioDTO.getStock_ids() != null) {
            List<Stocks> stocks = portfolioDTO.getStock_ids().stream()
                    .map(id -> stocksRepo.findById(id).orElse(null))
                    .filter(s -> s != null)
                    .collect(Collectors.toList());
            portfolio.setStocks(stocks);
        } else {
            portfolio.setStocks(null);
        }
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