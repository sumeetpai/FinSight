package com.FinSight_Backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "transaction")
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer t_id;
    private Integer stock_id;
    private Integer portfolio_id;
    private Integer user_id;
    private String type; // ADD or REMOVE
    private Integer qty;
    private Double price;
    private Timestamp timestamp_t;
}