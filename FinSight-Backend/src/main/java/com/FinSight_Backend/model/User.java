package com.FinSight_Backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "user")
@Data
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer user_id;
    private String username;
    private String email;
    private String password;
    private Timestamp createdAt;
    @OneToMany(mappedBy = "user")
    private List<Portfolio> portfolios;
    @ManyToOne
    private Transaction transaction;
}