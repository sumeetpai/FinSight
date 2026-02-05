package com.FinSight_Backend.service;

import com.FinSight_Backend.dto.UserDTO;
import com.FinSight_Backend.model.User;
import com.FinSight_Backend.repository.PortfolioRepo;
import com.FinSight_Backend.repository.TransactionRepo;
import com.FinSight_Backend.repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private PortfolioRepo portfolioRepo;

    @Mock
    private TransactionRepo transactionRepo;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void addUser_mapsFields() {
        UserDTO dto = new UserDTO();
        dto.setUsername("demo");
        dto.setEmail("demo@example.com");
        dto.setPassword("pw");
        dto.setCreatedAt(new Timestamp(1000));

        User saved = new User();
        saved.setUser_id(1);
        saved.setUsername("demo");
        saved.setEmail("demo@example.com");
        saved.setPassword("pw");
        saved.setCreatedAt(dto.getCreatedAt());

        Mockito.when(userRepo.save(Mockito.any())).thenReturn(saved);

        UserDTO out = userService.addUser(dto);
        assertNotNull(out);
        assertEquals(1, out.getUser_id());
        assertEquals("demo", out.getUsername());
    }

    @Test
    void getUser_returnsNullWhenMissing() {
        Mockito.when(userRepo.findById(1)).thenReturn(Optional.empty());
        assertNull(userService.getUser(1));
    }

    @Test
    void updateUser_returnsNullWhenMissing() {
        Mockito.when(userRepo.findById(2)).thenReturn(Optional.empty());
        assertNull(userService.updateUser(2, new UserDTO()));
    }

    @Test
    void deleteUser_returnsNullWhenMissing() {
        Mockito.when(userRepo.findById(3)).thenReturn(Optional.empty());
        assertNull(userService.deleteUser(3));
    }
}
