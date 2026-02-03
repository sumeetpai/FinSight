package com.FinSight_Backend.service;

import com.FinSight_Backend.dto.StocksDTO;

public interface StockService {
    StocksDTO addStocks(StocksDTO stocksDTO);
    StocksDTO getStocks(Integer stock_id);
    StocksDTO updateStocks(Integer stock_id, StocksDTO stocksDTO);
    String deleteStocks(Integer stock_id);
}