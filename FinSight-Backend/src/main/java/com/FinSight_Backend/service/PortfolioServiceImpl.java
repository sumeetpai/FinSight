package com.FinSight_Backend.service;

import com.FinSight_Backend.dto.PortfolioDTO;
import com.FinSight_Backend.model.Portfolio;
import com.FinSight_Backend.repository.PortfolioRepo;
import com.FinSight_Backend.repository.UserRepo;
import com.FinSight_Backend.repository.StocksRepo;
import com.FinSight_Backend.repository.TransactionRepo;
import com.FinSight_Backend.repository.PortfolioStockRepo;
import com.FinSight_Backend.model.User;
import com.FinSight_Backend.model.Stocks;
import com.FinSight_Backend.model.Transaction;
import com.FinSight_Backend.model.PortfolioStock;
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
    private PortfolioStockRepo portfolioStockRepo;
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
        portfolioDTO.setDescription(portfolio.getDescription());
        portfolioDTO.setTotal_value(portfolio.getTotal_value());
        portfolioDTO.setCost_basis(portfolio.getCost_basis());
        portfolioDTO.setYield(portfolio.getYield());
        portfolioDTO.setUser_id(portfolio.getUser() != null ? portfolio.getUser().getUser_id() : null);
        portfolioDTO.setStock_entries(portfolio.getPortfolioStocks() != null ?
                portfolio.getPortfolioStocks().stream()
                        .map(ps -> new com.FinSight_Backend.dto.StockEntryDTO(ps.getStock().getStock_id(), ps.getQuantity()))
                        .collect(Collectors.toList()) : null);
        return portfolioDTO;
    }

    private PortfolioDTO getPortfolioDTO(PortfolioDTO portfolioDTO, Portfolio portfolio) {
        portfolio.setName(portfolioDTO.getName());
        portfolio.setDescription(portfolioDTO.getDescription());
        portfolio.setTotal_value(portfolioDTO.getTotal_value());
        portfolio.setCost_basis(portfolioDTO.getCost_basis());
        portfolio.setYield(portfolioDTO.getYield());
        if (portfolioDTO.getUser_id() != null) {
            User u = userRepo.findById(portfolioDTO.getUser_id()).orElse(null);
            portfolio.setUser(u);
        } else {
            portfolio.setUser(null);
        }
        if (portfolioDTO.getStock_entries() != null) {
            // Merge incoming entries into existing PortfolioStock collection to avoid replacing the managed collection
            List<com.FinSight_Backend.dto.StockEntryDTO> incoming = portfolioDTO.getStock_entries();
            // build a map of incoming entries (stockId -> qty)
            java.util.Map<Integer, Integer> incomingMap = incoming.stream()
                    .filter(e -> e.getStock_id() != null)
                    .collect(Collectors.toMap(e -> e.getStock_id(), e -> e.getQuantity() != null ? e.getQuantity() : 1, (a, b) -> b));

            // ensure portfolio has an initialized collection
            if (portfolio.getPortfolioStocks() == null) {
                portfolio.setPortfolioStocks(new java.util.ArrayList<>());
            }

            // update existing entries or add new ones
            for (java.util.Map.Entry<Integer, Integer> e : incomingMap.entrySet()) {
                Integer stockId = e.getKey();
                Integer qty = e.getValue();
                Stocks s = stocksRepo.findById(stockId).orElse(null);
                if (s == null) continue; // skip non-existing stocks

                PortfolioStock existingPs = portfolio.getPortfolioStocks().stream()
                        .filter(ps -> ps.getStock() != null && ps.getStock().getStock_id().equals(stockId))
                        .findFirst().orElse(null);

                if (existingPs != null) {
                    existingPs.setQuantity(qty);
                } else {
                    PortfolioStock psNew = new PortfolioStock();
                    psNew.setPortfolio(portfolio);
                    psNew.setStock(s);
                    psNew.setQuantity(qty);
                    portfolio.getPortfolioStocks().add(psNew);
                }
            }

            // remove any existing PortfolioStock entries that are not present in incomingMap
            portfolio.getPortfolioStocks().removeIf(ps -> ps.getStock() == null || !incomingMap.containsKey(ps.getStock().getStock_id()));
        } else {
            // If the client did not include stock_entries in the DTO, do not modify the existing portfolioStocks collection.
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
    public List<PortfolioDTO> getAllPortfolios() {
        List<Portfolio> portfolios = portfolioRepo.findAll();
        return portfolios.stream().map(this::getPortfolioDTO).collect(Collectors.toList());
    }

    @Override
    public List<PortfolioDTO> getPortfoliosByUser(Integer user_id) {
        List<Portfolio> portfolios = portfolioRepo.findByUserId(user_id);
        return portfolios.stream().map(this::getPortfolioDTO).collect(Collectors.toList());
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
    public PortfolioDTO addStockToPortfolio(Integer portfolio_id, Integer stock_id, Integer user_id, Integer qty) {
        Portfolio portfolio = portfolioRepo.findById(portfolio_id).orElse(null);
        if (portfolio == null) return null;
        // validate stock exists
        Stocks stock = stocksRepo.findById(stock_id).orElse(null);
        if (stock == null) return null;
        int addedQty = qty != null && qty > 0 ? qty : 1;
        // see if an entry exists
        PortfolioStock existing = portfolioStockRepo.findByPortfolioIdAndStockId(portfolio_id, stock_id);
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + addedQty);
            portfolioStockRepo.save(existing);
        } else {
            PortfolioStock ps = new PortfolioStock();
            ps.setPortfolio(portfolio);
            ps.setStock(stock);
            ps.setQuantity(addedQty);
            portfolioStockRepo.save(ps);
        }
        // recompute total_value = sum(stock.current_price * qty)
        List<PortfolioStock> entries = portfolioStockRepo.findByPortfolioId(portfolio_id);
        long total = entries.stream().mapToLong(e -> (e.getStock().getCurrent_price() != null ? e.getStock().getCurrent_price() : 0) * (e.getQuantity() != null ? e.getQuantity() : 0)).sum();
        portfolio.setTotal_value(total);
        Portfolio saved = portfolioRepo.save(portfolio);
        // record transaction
        Transaction tx = new Transaction();
        tx.setStock_id(stock_id);
        tx.setPortfolio_id(saved.getPortfolio_id());
        tx.setUser_id(user_id);
        tx.setType("ADD");
        tx.setQty(addedQty);
        tx.setPrice(stock.getCurrent_price() != null ? stock.getCurrent_price().longValue() : 0L);
        tx.setTimestamp_t(new java.sql.Timestamp(System.currentTimeMillis()));
        transactionRepo.save(tx);
        return getPortfolioDTO(saved);
    }

    @Override
    public PortfolioDTO removeStockFromPortfolio(Integer portfolio_id, Integer stock_id, Integer user_id, Integer qty) {
        Portfolio portfolio = portfolioRepo.findById(portfolio_id).orElse(null);
        if (portfolio == null) return null;
        PortfolioStock existing = portfolioStockRepo.findByPortfolioIdAndStockId(portfolio_id, stock_id);
        if (existing == null) return null;
        int removeQty = qty != null && qty > 0 ? qty : 1;
        if (existing.getQuantity() > removeQty) {
            existing.setQuantity(existing.getQuantity() - removeQty);
            portfolioStockRepo.save(existing);
        } else {
            // remove the entry
            portfolioStockRepo.delete(existing);
        }
        List<PortfolioStock> entries = portfolioStockRepo.findByPortfolioId(portfolio_id);
        long total = entries.stream().mapToLong(e -> (e.getStock().getCurrent_price() != null ? e.getStock().getCurrent_price() : 0) * (e.getQuantity() != null ? e.getQuantity() : 0)).sum();
        portfolio.setTotal_value(total);
        Portfolio saved = portfolioRepo.save(portfolio);
        Transaction tx = new Transaction();
        tx.setStock_id(stock_id);
        tx.setPortfolio_id(saved.getPortfolio_id());
        tx.setUser_id(user_id);
        tx.setType("REMOVE");
        tx.setQty(removeQty);
        tx.setPrice(existing.getStock().getCurrent_price() != null ? existing.getStock().getCurrent_price().longValue() : 0L);
        tx.setTimestamp_t(new java.sql.Timestamp(System.currentTimeMillis()));
        transactionRepo.save(tx);
        return getPortfolioDTO(saved);
    }
}