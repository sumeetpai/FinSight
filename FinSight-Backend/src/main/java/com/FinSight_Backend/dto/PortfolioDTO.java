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
    private Long total_value;
    private Long cost_basis;
    private Integer yield;
    private Integer user_id;
    private List<StockEntryDTO> stock_entries;

}