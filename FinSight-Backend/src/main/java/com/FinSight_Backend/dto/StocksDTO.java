package com.FinSight_Backend.dto;
import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StocksDTO {
    private Integer stock_id;
    private String stock_sym;
    private String name;
    private Double day_before_price;
    private Double market_cap;
    private Double current_price;
    private List<Integer> portfolio_ids;
}