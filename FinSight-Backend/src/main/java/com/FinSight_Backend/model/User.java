package com.FinSight_Backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

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
    @ManyToOne
    private Portfolio portfolio;
    @ManyToOne
    private Transaction transaction;
}