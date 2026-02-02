package com.FinSight_Backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;
public interface UserRepo extends JPARepository<User, Integer> {
}