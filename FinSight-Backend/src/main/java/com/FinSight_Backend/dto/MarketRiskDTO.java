package com.FinSight_Backend.dto;

import lombok.Data;

@Data
public class MarketRiskDTO {
    private String symbol;
    private Integer risk_score;
    private String risk_level;
    private String meaning;
}
