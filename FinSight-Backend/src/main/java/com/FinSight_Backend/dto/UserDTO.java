package com.FinSight_Backend.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

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
    private List<Integer> portfolio_ids;
    private Integer transaction_id;
}