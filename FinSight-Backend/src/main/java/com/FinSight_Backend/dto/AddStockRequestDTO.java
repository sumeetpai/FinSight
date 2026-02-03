package com.FinSight_Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddStockRequestDTO {
    private Integer stock_id;
    private Integer user_id;
    private Integer qty; // optional, default 1 if null
}
