package com.FinSight_Backend.service;

import com.FinSight_Backend.dto.StocksDTO;
import com.FinSight_Backend.model.Stocks;
import com.FinSight_Backend.repository.StocksRepo;
import com.FinSight_Backend.repository.PortfolioRepo;
import com.FinSight_Backend.repository.PortfolioStockRepo;
import com.FinSight_Backend.model.Portfolio;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StockServiceImpl implements StockService {

    private StocksRepo stocksRepo;
    private PortfolioRepo portfolioRepo;
    private PortfolioStockRepo portfolioStockRepo;
    private MarketDataClient marketDataClient;
    @Override
    public StocksDTO addStocks(StocksDTO stocksDTO) {
        Stocks stocks = new Stocks();
        return getStocksDTO(stocksDTO, stocks);

    }

    private StocksDTO getStocksDTO(Stocks stocks) {
        StocksDTO stocksDTO = new StocksDTO();
        stocksDTO.setStock_id(stocks.getStock_id());
        stocksDTO.setName(stocks.getName());
        stocksDTO.setStock_sym(stocks.getStock_sym());
        stocksDTO.setDay_before_price(stocks.getDay_before_price());
        stocksDTO.setMarket_cap(stocks.getMarket_cap());
        stocksDTO.setCurrent_price(stocks.getCurrent_price());
        // find portfolios that include this stock via PortfolioStock
        List<com.FinSight_Backend.model.PortfolioStock> psList = portfolioStockRepo.findByStockId(stocks.getStock_id());
        stocksDTO.setPortfolio_ids(psList != null ? psList.stream().map(ps -> ps.getPortfolio().getPortfolio_id()).collect(Collectors.toList()) : null);
        return stocksDTO;
    }

    @Override
    public StocksDTO getStocks(Integer stock_id) {
        Stocks stocks = stocksRepo.findById(stock_id).isPresent() ? stocksRepo.findById(stock_id).get() : null;
        assert stocks != null;
        StocksDTO dto = getStocksDTO(stocks);
        return applyLivePrice(dto);
    }

    @Override
    public StocksDTO updateStocks(Integer stock_id, StocksDTO stocksDTO) {
        Stocks stocks = stocksRepo.findById(stock_id).isPresent() ? stocksRepo.findById(stock_id).get() : null;
        assert stocks != null;
        return getStocksDTO(stocksDTO, stocks);
    }

    @Override
    public Optional<StocksDTO> getStocksBySymbol(String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) return Optional.empty();
        Optional<Stocks> opt = stocksRepo.findBySymbolIgnoreCase(symbol.trim());
        return opt.map(this::getStocksDTO).map(this::applyLivePrice);
    }

    private StocksDTO getStocksDTO(StocksDTO stocksDTO, Stocks stocks) {
        stocks.setName(stocksDTO.getName());
        stocks.setStock_sym(stocksDTO.getStock_sym());
        stocks.setDay_before_price(stocksDTO.getDay_before_price());
        stocks.setMarket_cap(stocksDTO.getMarket_cap());
        stocks.setCurrent_price(stocksDTO.getCurrent_price());
        if (stocksDTO.getPortfolio_ids() != null) {
            // create portfolio-stock entries via PortfolioStockRepo handled by PortfolioService; here we just ignore direct setting
            // ensure portfolios exist
            List<Portfolio> portfolios = stocksDTO.getPortfolio_ids().stream()
                    .map(id -> portfolioRepo.findById(id).orElse(null))
                    .filter(p -> p != null)
                    .collect(Collectors.toList());
            // no direct mapping on Stocks entity; portfolio membership is managed via PortfolioStock
        } else {
            // nothing to set
        }
        Stocks savedStock = stocksRepo.save(stocks);
        return getStocksDTO(savedStock);
    }

    @Override
    public String deleteStocks(Integer stock_id) {
        Stocks stocks = stocksRepo.findById(stock_id).isPresent() ? stocksRepo.findById(stock_id).get() : null;
        assert stocks != null;
        String msg = getStocksDTO(stocks).toString();
        stocksRepo.delete(stocks);
        return msg;
    }

    private StocksDTO applyLivePrice(StocksDTO dto) {
        if (dto == null) return null;
        String symbol = dto.getStock_sym();
        if (symbol == null || symbol.trim().isEmpty()) return dto;
        com.FinSight_Backend.dto.MarketPriceDTO live = marketDataClient.getPrice(symbol.trim());
        if (live != null && live.getPrice() != null) {
            dto.setCurrent_price(live.getPrice());
        }
        return dto;
    }

}
