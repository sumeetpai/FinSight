package com.FinSight_Backend.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {
    private Integer t_id;
    private Integer stock_id;
    private Integer portfolio_id;
    private Integer user_id;
    private String type;
    private Integer qty;
    private Double price;
    private Timestamp timestamp_t;
}