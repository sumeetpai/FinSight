package com.FinSight_Backend.service;

import com.FinSight_Backend.dto.UserDTO;
import com.FinSight_Backend.model.User;
import com.FinSight_Backend.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService{

    private UserRepo userRepo;
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
        user.setPortfolio(userDTO.getPortfolio());
        user.setTransaction(userDTO.getTransaction());
        User savedUser = userRepo.save(user);
        return getUserDTO(savedUser);
    }

    private UserDTO getUserDTO(User savedUser) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(savedUser.getUsername());
        userDTO.setEmail(savedUser.getEmail());
        userDTO.setPassword(savedUser.getPassword());
        userDTO.setCreatedAt(savedUser.getCreatedAt());
        userDTO.setPortfolio(savedUser.getPortfolio());
        userDTO.setTransaction(savedUser.getTransaction());
        return userDTO;
    }

    @Override
    public UserDTO getUser(Integer user_id) {
        User user = userRepo.findById(user_id).isPresent() ? userRepo.findById(user_id).get() : null;
        assert user != null;
        return getUserDTO(user);
    }

    @Override
    public UserDTO updateUser(Integer user_id, UserDTO userDTO) {
        User user = userRepo.findById(user_id).isPresent() ? userRepo.findById(user_id).get() : null;
        assert user != null;
        return getUserDTO(userDTO, user);
    }

    @Override
    public String deleteUser(Integer user_id) {
        User user = userRepo.findById(user_id).isPresent() ? userRepo.findById(user_id).get() : null;
        assert user != null;
        userRepo.delete(user);
        return "User deleted successfully";
    }
}