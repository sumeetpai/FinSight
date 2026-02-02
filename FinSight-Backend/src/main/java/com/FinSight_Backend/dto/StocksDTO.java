package com.stport.spm.DTOClasses;

import com.FinSight_Backend.model.Portfolio;
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
    private Integer day_before_price;
    private Long market_cap;
    private Integer current_price;
    private List<Portfolio> portfolios;
}