package com.FinSight_Backend.service;

import com.FinSight_Backend.dto.UserDTO;
<<<<<<< HEAD
=======
import com.FinSight_Backend.model.Portfolio;
import com.FinSight_Backend.model.Transaction;
>>>>>>> 323f0136fc4e141b74bb75a0c52ada2cc2f11153
import com.FinSight_Backend.model.User;
import com.FinSight_Backend.repository.PortfolioRepo;
import com.FinSight_Backend.repository.TransactionRepo;
import com.FinSight_Backend.repository.UserRepo;
<<<<<<< HEAD
=======
import org.junit.jupiter.api.BeforeEach;
>>>>>>> 323f0136fc4e141b74bb75a0c52ada2cc2f11153
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
<<<<<<< HEAD
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
=======
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
>>>>>>> 323f0136fc4e141b74bb75a0c52ada2cc2f11153

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

<<<<<<< HEAD
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
=======
    private UserDTO userDTO;
    private User user;
    private Timestamp createdAt;

    @BeforeEach
    void setUp() {
        createdAt = new Timestamp(System.currentTimeMillis());

        userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setEmail("test@example.com");
        userDTO.setPassword("password123");
        userDTO.setCreatedAt(createdAt);
        userDTO.setPortfolio_ids(null);
        userDTO.setTransaction_id(null);

        user = new User();
        user.setUser_id(1);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setCreatedAt(createdAt);
        user.setPortfolios(null);
        user.setTransaction(null);
    }

    @Test
    void testAddUser_Success() {
        // Arrange
        when(userRepo.save(any(User.class))).thenReturn(user);

        // Act
        UserDTO result = userService.addUser(userDTO);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("password123", result.getPassword());
        verify(userRepo, times(1)).save(any(User.class));
    }

    @Test
    void testAddUser_WithPortfolios() {
        // Arrange
        List<Integer> portfolioIds = new ArrayList<>();
        portfolioIds.add(1);
        portfolioIds.add(2);
        userDTO.setPortfolio_ids(portfolioIds);

        Portfolio portfolio1 = new Portfolio();
        portfolio1.setPortfolio_id(1);
        portfolio1.setName("Portfolio 1");

        Portfolio portfolio2 = new Portfolio();
        portfolio2.setPortfolio_id(2);
        portfolio2.setName("Portfolio 2");

        when(portfolioRepo.findById(1)).thenReturn(Optional.of(portfolio1));
        when(portfolioRepo.findById(2)).thenReturn(Optional.of(portfolio2));
        when(userRepo.save(any(User.class))).thenReturn(user);

        // Act
        UserDTO result = userService.addUser(userDTO);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(portfolioRepo, times(2)).findById(anyInt());
        verify(userRepo, times(1)).save(any(User.class));
    }

    @Test
    void testAddUser_WithTransaction() {
        // Arrange
        Transaction transaction = new Transaction();
        transaction.setT_id(1);
        userDTO.setTransaction_id(1);

        when(transactionRepo.findById(1)).thenReturn(Optional.of(transaction));
        when(userRepo.save(any(User.class))).thenReturn(user);

        // Act
        UserDTO result = userService.addUser(userDTO);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(transactionRepo, times(1)).findById(1);
        verify(userRepo, times(1)).save(any(User.class));
    }

    @Test
    void testGetUser_Success() {
        // Arrange
        when(userRepo.findById(1)).thenReturn(Optional.of(user));

        // Act
        UserDTO result = userService.getUser(1);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepo, times(1)).findById(1);
    }

    @Test
    void testGetUser_NotFound() {
        // Arrange
        when(userRepo.findById(999)).thenReturn(Optional.empty());

        // Act
        UserDTO result = userService.getUser(999);

        // Assert
        assertNull(result);
        verify(userRepo, times(1)).findById(999);
    }

    @Test
    void testUpdateUser_Success() {
        // Arrange
        UserDTO updateDTO = new UserDTO();
        updateDTO.setUsername("updateduser");
        updateDTO.setEmail("updated@example.com");
        updateDTO.setPassword("newpassword");
        updateDTO.setCreatedAt(createdAt);

        User updatedUser = new User();
        updatedUser.setUser_id(1);
        updatedUser.setUsername("updateduser");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setPassword("newpassword");
        updatedUser.setCreatedAt(createdAt);

        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(userRepo.save(any(User.class))).thenReturn(updatedUser);

        // Act
        UserDTO result = userService.updateUser(1, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals("updateduser", result.getUsername());
        assertEquals("updated@example.com", result.getEmail());
        verify(userRepo, times(1)).findById(1);
        verify(userRepo, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUser_NotFound() {
        // Arrange
        when(userRepo.findById(999)).thenReturn(Optional.empty());

        // Act
        UserDTO result = userService.updateUser(999, userDTO);

        // Assert
        assertNull(result);
        verify(userRepo, times(1)).findById(999);
    }

    @Test
    void testDeleteUser_Success() {
        // Arrange
        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        doNothing().when(userRepo).delete(any(User.class));

        // Act
        String result = userService.deleteUser(1);

        // Assert
        assertNotNull(result);
        assertEquals("User deleted successfully", result);
        verify(userRepo, times(1)).findById(1);
        verify(userRepo, times(1)).delete(user);
    }

    @Test
    void testDeleteUser_NotFound() {
        // Arrange
        when(userRepo.findById(999)).thenReturn(Optional.empty());

        // Act
        String result = userService.deleteUser(999);

        // Assert
        assertNull(result);
        verify(userRepo, times(1)).findById(999);
        verify(userRepo, never()).delete(any(User.class));
    }

    @Test
    void testAddUser_WithInvalidPortfolio() {
        // Arrange
        List<Integer> portfolioIds = new ArrayList<>();
        portfolioIds.add(1);
        portfolioIds.add(999); // Non-existent portfolio
        userDTO.setPortfolio_ids(portfolioIds);

        Portfolio portfolio1 = new Portfolio();
        portfolio1.setPortfolio_id(1);

        when(portfolioRepo.findById(1)).thenReturn(Optional.of(portfolio1));
        when(portfolioRepo.findById(999)).thenReturn(Optional.empty());
        when(userRepo.save(any(User.class))).thenReturn(user);

        // Act
        UserDTO result = userService.addUser(userDTO);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepo, times(1)).save(any(User.class));
>>>>>>> 323f0136fc4e141b74bb75a0c52ada2cc2f11153
    }
}
