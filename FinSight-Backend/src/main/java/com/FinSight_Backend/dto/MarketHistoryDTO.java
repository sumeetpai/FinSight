package com.FinSight_Backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class MarketHistoryDTO {
    private String symbol;
    private List<MarketCandleDTO> candles;
}
