package com.FinSight_Backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.FinSight_Backend.model.User;
public interface UserRepo extends JpaRepository<User, Integer> {
}