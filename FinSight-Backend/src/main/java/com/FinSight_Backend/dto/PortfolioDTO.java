package com.FinSight_Backend.dto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PortfolioDTO {

    private Integer portfolio_id;
    private String name;
    private String description;
    private Boolean active;
    private Double total_value;
    private Double cost_basis;
    private Double yield;
    private Integer user_id;
    private List<StockEntryDTO> stock_entries;

}