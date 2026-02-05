package com.FinSight_Backend.dto;

import lombok.Data;

@Data
public class MarketPriceDTO {
    private String symbol;
    private Double price;
    private String currency;
    private Long timestamp;
}
