package com.FinSight_Backend.dto;
//import com.FinSight_Backend.model.Portfolio;
import com.FinSight_Backend.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Integer user_id;
    private String username;
    private String email;
    private String password;
    private Timestamp createdAt;
    private Portfolio portfolio;
    private Transaction transaction;
}