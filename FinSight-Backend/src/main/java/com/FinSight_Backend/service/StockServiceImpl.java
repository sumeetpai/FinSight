package com.FinSight_Backend.service;

import com.FinSight_Backend.dto.StocksDTO;
import com.FinSight_Backend.model.Stocks;
import com.FinSight_Backend.repository.StocksRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StockServiceImpl implements StockService {

    private StocksRepo stocksRepo;

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
        stocksDTO.setPortfolios(stocks.getPortfolios());
        return stocksDTO;
    }

    @Override
    public StocksDTO getStocks(Integer stock_id) {
        Stocks stocks = stocksRepo.findById(stock_id).isPresent() ? stocksRepo.findById(stock_id).get() : null;
        assert stocks != null;
        return getStocksDTO(stocks);
    }

    @Override
    public StocksDTO updateStocks(Integer stock_id, StocksDTO stocksDTO) {
        Stocks stocks = stocksRepo.findById(stock_id).isPresent() ? stocksRepo.findById(stock_id).get() : null;
        assert stocks != null;
        return getStocksDTO(stocksDTO, stocks);
    }

    private StocksDTO getStocksDTO(StocksDTO stocksDTO, Stocks stocks) {
        stocks.setName(stocksDTO.getName());
        stocks.setStock_sym(stocksDTO.getStock_sym());
        stocks.setDay_before_price(stocksDTO.getDay_before_price());
        stocks.setMarket_cap(stocksDTO.getMarket_cap());
        stocks.setCurrent_price(stocksDTO.getCurrent_price());
        stocks.setPortfolios(stocksDTO.getPortfolios());
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

}