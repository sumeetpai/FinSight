package com.FinSight_Backend.model;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "stocks")
@Data
public class Stocks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer stock_id;
    private String stock_sym;
    private String name;
    private Integer day_before_price;
    private Long market_cap;
    private Integer current_price;

}