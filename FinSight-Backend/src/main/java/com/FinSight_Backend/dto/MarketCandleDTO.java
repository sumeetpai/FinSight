package com.FinSight_Backend.dto;

import lombok.Data;

@Data
public class MarketCandleDTO {
    private Long time;
    private Double open;
    private Double high;
    private Double low;
    private Double close;
    private Long volume;
}
