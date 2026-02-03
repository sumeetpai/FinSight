package com.FinSight_Backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "portfolio_stock")
@Data
public class PortfolioStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id")
    private Stocks stock;

    private Integer quantity;
}
