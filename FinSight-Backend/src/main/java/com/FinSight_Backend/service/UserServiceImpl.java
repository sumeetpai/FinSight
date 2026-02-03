package com.FinSight_Backend.service;

import com.FinSight_Backend.dto.UserDTO;
import com.FinSight_Backend.model.User;
import com.FinSight_Backend.repository.UserRepo;
import com.FinSight_Backend.repository.PortfolioRepo;
import com.FinSight_Backend.repository.TransactionRepo;
import com.FinSight_Backend.model.Portfolio;
import com.FinSight_Backend.model.Transaction;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService{

    private UserRepo userRepo;
    private PortfolioRepo portfolioRepo;
    private TransactionRepo transactionRepo;
    @Override
    public UserDTO addUser(UserDTO userDTO) {
        User user = new User();
        return getUserDTO(userDTO, user);
    }

    private UserDTO getUserDTO(UserDTO userDTO, User user) {
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setCreatedAt(userDTO.getCreatedAt());
        if (userDTO.getPortfolio_ids() != null) {
            List<Portfolio> portfolios = userDTO.getPortfolio_ids().stream()
                    .map(id -> portfolioRepo.findById(id).orElse(null))
                    .filter(p -> p != null)
                    .collect(Collectors.toList());
            // ensure owning side is set
            portfolios.forEach(p -> p.setUser(user));
            user.setPortfolios(portfolios);
        } else {
            user.setPortfolios(null);
        }
        if (userDTO.getTransaction_id() != null) {
            Transaction t = transactionRepo.findById(userDTO.getTransaction_id()).orElse(null);
            user.setTransaction(t);
        } else {
            user.setTransaction(null);
        }
        User savedUser = userRepo.save(user);
        return getUserDTO(savedUser);
    }

    private UserDTO getUserDTO(User savedUser) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(savedUser.getUsername());
        userDTO.setEmail(savedUser.getEmail());
        userDTO.setPassword(savedUser.getPassword());
        userDTO.setCreatedAt(savedUser.getCreatedAt());
        userDTO.setUser_id(savedUser.getUser_id());
        userDTO.setPortfolio_ids(savedUser.getPortfolios() != null ?
                savedUser.getPortfolios().stream().map(Portfolio::getPortfolio_id).collect(Collectors.toList()) : null);
        userDTO.setTransaction_id(savedUser.getTransaction() != null ? savedUser.getTransaction().getT_id() : null);
        return userDTO;
    }

    @Override
    public UserDTO getUser(Integer user_id) {
        return userRepo.findById(user_id).map(this::getUserDTO).orElse(null);
    }

    @Override
    public UserDTO updateUser(Integer user_id, UserDTO userDTO) {
        return userRepo.findById(user_id).map(user -> getUserDTO(userDTO, user)).orElse(null);
    }

    @Override
    public String deleteUser(Integer user_id) {
        return userRepo.findById(user_id).map(user -> {
            userRepo.delete(user);
            return "User deleted successfully";
        }).orElse(null);
    }
}