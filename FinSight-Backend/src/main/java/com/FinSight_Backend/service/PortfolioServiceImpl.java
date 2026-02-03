package com.FinSight_Backend.service;

import com.FinSight_Backend.dto.PortfolioDTO;
import com.FinSight_Backend.model.Portfolio;
import com.FinSight_Backend.repository.PortfolioRepo;
import com.FinSight_Backend.repository.UserRepo;
import com.FinSight_Backend.repository.StocksRepo;
import com.FinSight_Backend.repository.TransactionRepo;
import com.FinSight_Backend.model.User;
import com.FinSight_Backend.model.Stocks;
import com.FinSight_Backend.model.Transaction;
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
    private TransactionRepo transactionRepo;
    @Override
    public PortfolioDTO addPortfolio(PortfolioDTO portfolioDTO) {
        // validate owning user exists
        if (portfolioDTO.getUser_id() == null) {
            return null; // controller will translate to BAD_REQUEST
        }
        if (!userRepo.existsById(portfolioDTO.getUser_id())) {
            return null; // controller will translate to NOT_FOUND
        }
        Portfolio portfolio = new Portfolio();
        return getPortfolioDTO(portfolioDTO, portfolio);
    }

    private PortfolioDTO getPortfolioDTO(Portfolio portfolio) {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.setPortfolio_id(portfolio.getPortfolio_id());
        portfolioDTO.setName(portfolio.getName());
        portfolioDTO.setTotal_value(portfolio.getTotal_value());
        portfolioDTO.setCost_basis(portfolio.getCost_basis());
        portfolioDTO.setYield(portfolio.getYield());
        portfolioDTO.setUser_id(portfolio.getUser() != null ? portfolio.getUser().getUser_id() : null);
        portfolioDTO.setStock_ids(portfolio.getStocks() != null ?
                portfolio.getStocks().stream().map(Stocks::getStock_id).collect(Collectors.toList()) : null);
        return portfolioDTO;
    }

    private PortfolioDTO getPortfolioDTO(PortfolioDTO portfolioDTO, Portfolio portfolio) {
        portfolio.setName(portfolioDTO.getName());
        portfolio.setTotal_value(portfolioDTO.getTotal_value());
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
            // keep both sides in sync
            for (Stocks s : stocks) {
                if (s.getPortfolio() == null) {
                    s.setPortfolio(new java.util.ArrayList<>());
                }
                if (!s.getPortfolio().contains(portfolio)) {
                    s.getPortfolio().add(portfolio);
                }
            }
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
        if (portfolio == null) {
            return null;
        }
        // if user_id is provided, ensure the user exists
        if (portfolioDTO.getUser_id() != null && !userRepo.existsById(portfolioDTO.getUser_id())) {
            return null;
        }
        return getPortfolioDTO(portfolioDTO, portfolio);
    }

    @Override
    public String deletePortfolio(Integer portfolio_id) {
        Portfolio portfolio = portfolioRepo.findById(portfolio_id).isPresent() ? portfolioRepo.findById(portfolio_id).get() : null;
        assert portfolio != null;
        portfolioRepo.delete(portfolio);
        return "Portfolio deleted";
    }

    @Override
    public PortfolioDTO addStockToPortfolio(Integer portfolio_id, Integer stock_id, Integer user_id) {
        Portfolio portfolio = portfolioRepo.findById(portfolio_id).orElse(null);
        if (portfolio == null) return null;
        // validate stock exists
        Stocks stock = stocksRepo.findById(stock_id).orElse(null);
        if (stock == null) return null;
        // add if not present
        if (portfolio.getStocks() == null) {
            portfolio.setStocks(new java.util.ArrayList<>());
        }
        boolean added = false;
        if (!portfolio.getStocks().contains(stock)) {
            portfolio.getStocks().add(stock);
            // keep inverse side
            if (stock.getPortfolio() == null) stock.setPortfolio(new java.util.ArrayList<>());
            if (!stock.getPortfolio().contains(portfolio)) stock.getPortfolio().add(portfolio);
            added = true;
        }
        // update total_value as sum of stock.current_price
        long total = portfolio.getStocks().stream().mapToLong(s -> s.getCurrent_price() != null ? s.getCurrent_price() : 0).sum();
        portfolio.setTotal_value(total);
        Portfolio saved = portfolioRepo.save(portfolio);
        // record transaction if added
        if (added) {
            Transaction tx = new Transaction();
            tx.setStock_id(stock_id);
            tx.setPortfolio_id(saved.getPortfolio_id());
            tx.setUser_id(user_id);
            tx.setType("ADD");
            tx.setQty(1);
            tx.setPrice(stock.getCurrent_price() != null ? stock.getCurrent_price().longValue() : 0L);
            tx.setTimestamp_t(new java.sql.Timestamp(System.currentTimeMillis()));
            transactionRepo.save(tx);
        }
        return getPortfolioDTO(saved);
    }

    @Override
    public PortfolioDTO removeStockFromPortfolio(Integer portfolio_id, Integer stock_id, Integer user_id) {
        Portfolio portfolio = portfolioRepo.findById(portfolio_id).orElse(null);
        if (portfolio == null) return null;
        Stocks stock = stocksRepo.findById(stock_id).orElse(null);
        if (stock == null) return null;
        boolean removed = false;
        if (portfolio.getStocks() != null && portfolio.getStocks().contains(stock)) {
            portfolio.getStocks().remove(stock);
            removed = true;
            // remove inverse side
            if (stock.getPortfolio() != null) stock.getPortfolio().remove(portfolio);
        }
        long total = portfolio.getStocks() != null ? portfolio.getStocks().stream().mapToLong(s -> s.getCurrent_price() != null ? s.getCurrent_price() : 0).sum() : 0L;
        portfolio.setTotal_value(total);
        Portfolio saved = portfolioRepo.save(portfolio);
        if (removed) {
            Transaction tx = new Transaction();
            tx.setStock_id(stock_id);
            tx.setPortfolio_id(saved.getPortfolio_id());
            tx.setUser_id(user_id);
            tx.setType("REMOVE");
            tx.setQty(1);
            tx.setPrice(stock.getCurrent_price() != null ? stock.getCurrent_price().longValue() : 0L);
            tx.setTimestamp_t(new java.sql.Timestamp(System.currentTimeMillis()));
            transactionRepo.save(tx);
        }
        return getPortfolioDTO(saved);
    }
}