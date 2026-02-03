package com.FinSight_Backend.model;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "portfolio")
@Data
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer portfolio_id;
    private Integer shares;
    private String stock_id;
    private Long current_price;
    private Long cost_basis;
    private Integer yield;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToMany
    @JoinTable(
            name = "portfolio_stocks",
            joinColumns = @JoinColumn(name = "portfolio_id"),
            inverseJoinColumns = @JoinColumn(name = "stock_id")
    )
    private List<Stocks> stocks;

}