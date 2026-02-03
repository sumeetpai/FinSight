package com.FinSight_Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RemoveStockRequestDTO {
    private Integer user_id;
    private Integer qty; // number of shares to remove
}
