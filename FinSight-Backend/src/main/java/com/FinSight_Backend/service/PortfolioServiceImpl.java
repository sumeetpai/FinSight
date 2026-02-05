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
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

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
    private MarketDataClient marketDataClient;
    @PersistenceContext
    private EntityManager em;

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
        portfolioDTO.setActive(portfolio.getActive());
        return portfolioDTO;
    }

    private PortfolioDTO getPortfolioDTO(PortfolioDTO portfolioDTO, Portfolio portfolio) {
        // only update fields if provided (non-null) to avoid wiping existing values
        if (portfolioDTO.getName() != null) portfolio.setName(portfolioDTO.getName());
        if (portfolioDTO.getDescription() != null) portfolio.setDescription(portfolioDTO.getDescription());
        if (portfolioDTO.getTotal_value() != null) portfolio.setTotal_value(portfolioDTO.getTotal_value());
        if (portfolioDTO.getCost_basis() != null) portfolio.setCost_basis(portfolioDTO.getCost_basis());
        if (portfolioDTO.getYield() != null) portfolio.setYield(portfolioDTO.getYield());
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
    public java.util.Map<String, Long> getChildRowCounts(Integer portfolio_id) {
        java.util.Map<String, Long> map = new java.util.HashMap<>();
        try { Object o = em.createNativeQuery("SELECT COUNT(*) FROM portfolio_stock WHERE portfolio_id = :id").setParameter("id", portfolio_id).getSingleResult(); map.put("portfolio_stock_portfolio_id", ((Number)o).longValue()); } catch (Exception ignored) { map.put("portfolio_stock_portfolio_id", -1L); }
        try { Object o = em.createNativeQuery("SELECT COUNT(*) FROM portfolio_stock WHERE portfolio_portfolio_id = :id").setParameter("id", portfolio_id).getSingleResult(); map.put("portfolio_stock_portfolio_portfolio_id", ((Number)o).longValue()); } catch (Exception ignored) { map.put("portfolio_stock_portfolio_portfolio_id", -1L); }
        try { Object o = em.createNativeQuery("SELECT COUNT(*) FROM portfolio_stocks WHERE portfolio_id = :id").setParameter("id", portfolio_id).getSingleResult(); map.put("portfolio_stocks_portfolio_id", ((Number)o).longValue()); } catch (Exception ignored) { map.put("portfolio_stocks_portfolio_id", -1L); }
        try { Object o = em.createNativeQuery("SELECT COUNT(*) FROM portfolio_stocks WHERE portfolio_portfolio_id = :id").setParameter("id", portfolio_id).getSingleResult(); map.put("portfolio_stocks_portfolio_portfolio_id", ((Number)o).longValue()); } catch (Exception ignored) { map.put("portfolio_stocks_portfolio_portfolio_id", -1L); }
        try { Object o = em.createNativeQuery("SELECT COUNT(*) FROM portfolio_stock WHERE portfolio_id = :id OR portfolio_portfolio_id = :id").setParameter("id", portfolio_id).getSingleResult(); map.put("portfolio_stock_any", ((Number)o).longValue()); } catch (Exception ignored) { map.put("portfolio_stock_any", -1L); }
        try { Object o = em.createNativeQuery("SELECT COUNT(*) FROM transaction WHERE portfolio_id = :id").setParameter("id", portfolio_id).getSingleResult(); map.put("transactions", ((Number)o).longValue()); } catch (Exception ignored) { map.put("transactions", -1L); }
        return map;
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
    @Transactional
    public String deletePortfolio(Integer portfolio_id) {
        // Remove any rows in legacy/non-mapped join tables first (so DB won't block deletion)
        try { em.createNativeQuery("DELETE FROM portfolio_stock WHERE portfolio_id = :id").setParameter("id", portfolio_id).executeUpdate(); } catch (Exception ignored) {}
        try { em.createNativeQuery("DELETE FROM portfolio_stock WHERE portfolio_portfolio_id = :id").setParameter("id", portfolio_id).executeUpdate(); } catch (Exception ignored) {}
        try { em.createNativeQuery("DELETE FROM portfolio_stocks WHERE portfolio_id = :id").setParameter("id", portfolio_id).executeUpdate(); } catch (Exception ignored) {}
        try { em.createNativeQuery("DELETE FROM portfolio_stocks WHERE portfolio_portfolio_id = :id").setParameter("id", portfolio_id).executeUpdate(); } catch (Exception ignored) {}
        // Also try repository-level native helpers
        try { portfolioStockRepo.deleteNativePortfolioStockByPortfolioIdCombined(portfolio_id); } catch (Exception ignored) {}
        try { portfolioStockRepo.deleteLegacyByPortfolioIdCombined(portfolio_id); } catch (Exception ignored) {}
        try { portfolioStockRepo.deleteLegacyByPortfolioId(portfolio_id); } catch (Exception ignored) {}

        // delete transactions linked to this portfolio to avoid FK constraint
        transactionRepo.deleteByPortfolioId(portfolio_id);
        transactionRepo.flush();

        // Attempt to delete the parent. If DB still blocks due to FK, try extra cleanup and retry once.
        try {
            Portfolio portfolio = portfolioRepo.findById(portfolio_id).orElse(null);
            if (portfolio == null) return null;
            portfolioRepo.delete(portfolio);
            portfolioRepo.flush();
            return "Portfolio deleted";
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            // Some child rows may still exist in legacy tables with unexpected column names. Try additional aggressive native deletes and retry.
            try {
                // aggressive native cleanups
                em.createNativeQuery("DELETE FROM `portfolio_stocks` WHERE portfolio_id = :id OR portfolio_portfolio_id = :id").setParameter("id", portfolio_id).executeUpdate();
                em.createNativeQuery("DELETE FROM `portfolio_stock` WHERE portfolio_id = :id OR portfolio_portfolio_id = :id").setParameter("id", portfolio_id).executeUpdate();
            } catch (Exception ignored) {}

            // re-delete transactions and flush
            try { transactionRepo.deleteByPortfolioId(portfolio_id); } catch (Exception ignored) {}
            try { transactionRepo.flush(); } catch (Exception ignored) {}

            // final retry
            try {
                Portfolio portfolio2 = portfolioRepo.findById(portfolio_id).orElse(null);
                if (portfolio2 == null) return null;
                portfolioRepo.delete(portfolio2);
                portfolioRepo.flush();
                return "Portfolio deleted";
            } catch (Exception ex2) {
                // give a clear error so caller can inspect DB manually
                throw new org.springframework.dao.DataIntegrityViolationException("Unable to delete portfolio " + portfolio_id + ". Child rows remain in join tables or other tables referencing portfolio. Manual DB cleanup required.", ex2);
            }
        }
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
        // update cost_basis by adding the cost of the added shares (current_price * qty)
        Double addedCost = (stock.getCurrent_price() != null ? stock.getCurrent_price().doubleValue() : 0D) * addedQty;
        Double existingCost = portfolio.getCost_basis() != null ? portfolio.getCost_basis() : 0D;
        portfolio.setCost_basis(existingCost + addedCost);
        Portfolio saved = portfolioRepo.save(portfolio);
        // record transaction
        Transaction tx = new Transaction();
        tx.setStock_id(stock_id);
        tx.setPortfolio_id(saved.getPortfolio_id());
        tx.setUser_id(user_id);
        tx.setType("ADD");
        tx.setQty(addedQty);
        tx.setPrice(stock.getCurrent_price() != null ? stock.getCurrent_price().doubleValue() : 0D);
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
        Double total = entries.stream().mapToDouble(e -> {
            Stocks s = e.getStock();
            Double live = getLivePriceOrFallback(s);
            return (live != null ? live : 0) * (e.getQuantity() != null ? e.getQuantity() : 0);
        }).sum();
        portfolio.setTotal_value(total);
        Portfolio saved = portfolioRepo.save(portfolio);
        Transaction tx = new Transaction();
        tx.setStock_id(stock_id);
        tx.setPortfolio_id(saved.getPortfolio_id());
        tx.setUser_id(user_id);
        tx.setType("REMOVE");
        tx.setQty(removeQty);
        Double livePrice = getLivePriceOrFallback(existing.getStock());
        tx.setPrice(livePrice != null ? livePrice.doubleValue() : 0F);
        tx.setTimestamp_t(new java.sql.Timestamp(System.currentTimeMillis()));
        transactionRepo.save(tx);
        return getPortfolioDTO(saved);
    }

    private Double getLivePriceOrFallback(Stocks stock) {
        if (stock == null) return null;
        String symbol = stock.getStock_sym();
        if (symbol != null && !symbol.trim().isEmpty()) {
            com.FinSight_Backend.dto.MarketPriceDTO live = marketDataClient.getPrice(symbol.trim());
            if (live != null && live.getPrice() != null) {
                return live.getPrice();
            }
        }
        return stock.getCurrent_price() != null ? stock.getCurrent_price().doubleValue() : null;
    }

    @Override
    public PortfolioDTO setPortfolioActiveStatus(Integer portfolio_id, Boolean active) {
        Portfolio portfolio = portfolioRepo.findById(portfolio_id).orElse(null);
        if (portfolio == null) return null;
        portfolio.setActive(active != null ? active : Boolean.FALSE);
        Portfolio saved = portfolioRepo.save(portfolio);
        return getPortfolioDTO(saved);
    }
}
