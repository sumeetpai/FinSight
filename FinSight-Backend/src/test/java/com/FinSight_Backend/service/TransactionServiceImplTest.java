package com.FinSight_Backend.service;

import com.FinSight_Backend.dto.TransactionDTO;
import com.FinSight_Backend.model.Transaction;
import com.FinSight_Backend.repository.TransactionRepo;
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
=======
>>>>>>> 323f0136fc4e141b74bb75a0c52ada2cc2f11153
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
<<<<<<< HEAD
=======
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
>>>>>>> 323f0136fc4e141b74bb75a0c52ada2cc2f11153

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepo transactionRepo;

    @InjectMocks
    private TransactionServiceImpl transactionService;

<<<<<<< HEAD
    @Test
    void addTransaction_mapsFields() {
        TransactionDTO dto = new TransactionDTO();
        dto.setStock_id(1);
        dto.setPortfolio_id(2);
        dto.setUser_id(3);
        dto.setType("ADD");
        dto.setQty(4);
        dto.setPrice(100.0);
        dto.setTimestamp_t(new Timestamp(1000));

        Transaction saved = new Transaction();
        saved.setT_id(10);
        saved.setStock_id(1);
        saved.setPortfolio_id(2);
        saved.setUser_id(3);
        saved.setType("ADD");
        saved.setQty(4);
        saved.setPrice(100.0);
        saved.setTimestamp_t(dto.getTimestamp_t());

        Mockito.when(transactionRepo.save(Mockito.any())).thenReturn(saved);

        TransactionDTO out = transactionService.addTransaction(dto);
        assertNotNull(out);
        assertEquals(10, out.getT_id());
        assertEquals(100.0, out.getPrice());
    }

    @Test
    void getTransaction_returnsNullWhenMissing() {
        Mockito.when(transactionRepo.findById(1)).thenReturn(Optional.empty());
        assertNull(transactionService.getTransaction(1));
    }

    @Test
    void updateTransaction_returnsNullWhenMissing() {
        Mockito.when(transactionRepo.findById(2)).thenReturn(Optional.empty());
        assertNull(transactionService.updateTransaction(2, new TransactionDTO()));
    }

    @Test
    void deleteTransaction_returnsNullWhenMissing() {
        Mockito.when(transactionRepo.findById(3)).thenReturn(Optional.empty());
        assertNull(transactionService.deleteTransaction(3));
=======
    private TransactionDTO transactionDTO;
    private Transaction transaction;
    private Timestamp timestamp;

    @BeforeEach
    void setUp() {
        timestamp = new Timestamp(System.currentTimeMillis());

        transactionDTO = new TransactionDTO();
        transactionDTO.setStock_id(1);
        transactionDTO.setPortfolio_id(1);
        transactionDTO.setUser_id(1);
        transactionDTO.setType("BUY");
        transactionDTO.setQty(10);
        transactionDTO.setPrice(150.0);
        transactionDTO.setTimestamp_t(timestamp);

        transaction = new Transaction();
        transaction.setT_id(1);
        transaction.setStock_id(1);
        transaction.setPortfolio_id(1);
        transaction.setUser_id(1);
        transaction.setType("BUY");
        transaction.setQty(10);
        transaction.setPrice(150.0);
        transaction.setTimestamp_t(timestamp);
    }

    @Test
    void testAddTransaction_Success() {
        // Arrange
        when(transactionRepo.save(any(Transaction.class))).thenReturn(transaction);

        // Act
        TransactionDTO result = transactionService.addTransaction(transactionDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getT_id());
        assertEquals(1, result.getStock_id());
        assertEquals(1, result.getPortfolio_id());
        assertEquals(1, result.getUser_id());
        assertEquals("BUY", result.getType());
        assertEquals(10, result.getQty());
        assertEquals(150.0, result.getPrice());
        verify(transactionRepo, times(1)).save(any(Transaction.class));
    }

    @Test
    void testAddTransaction_WithSellType() {
        // Arrange
        TransactionDTO sellDTO = new TransactionDTO();
        sellDTO.setStock_id(2);
        sellDTO.setPortfolio_id(1);
        sellDTO.setUser_id(1);
        sellDTO.setType("SELL");
        sellDTO.setQty(5);
        sellDTO.setPrice(160.0);
        sellDTO.setTimestamp_t(timestamp);

        Transaction sellTransaction = new Transaction();
        sellTransaction.setT_id(2);
        sellTransaction.setStock_id(2);
        sellTransaction.setPortfolio_id(1);
        sellTransaction.setUser_id(1);
        sellTransaction.setType("SELL");
        sellTransaction.setQty(5);
        sellTransaction.setPrice(160.0);
        sellTransaction.setTimestamp_t(timestamp);

        when(transactionRepo.save(any(Transaction.class))).thenReturn(sellTransaction);

        // Act
        TransactionDTO result = transactionService.addTransaction(sellDTO);

        // Assert
        assertNotNull(result);
        assertEquals("SELL", result.getType());
        assertEquals(5, result.getQty());
        verify(transactionRepo, times(1)).save(any(Transaction.class));
    }

    @Test
    void testGetTransaction_Success() {
        // Arrange
        when(transactionRepo.findById(1)).thenReturn(Optional.of(transaction));

        // Act
        TransactionDTO result = transactionService.getTransaction(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getT_id());
        assertEquals("BUY", result.getType());
        assertEquals(10, result.getQty());
        verify(transactionRepo, times(1)).findById(1);
    }

    @Test
    void testGetTransaction_NotFound() {
        // Arrange
        when(transactionRepo.findById(999)).thenReturn(Optional.empty());

        // Act
        TransactionDTO result = transactionService.getTransaction(999);

        // Assert
        assertNull(result);
        verify(transactionRepo, times(1)).findById(999);
    }

    @Test
    void testUpdateTransaction_Success() {
        // Arrange
        TransactionDTO updateDTO = new TransactionDTO();
        updateDTO.setStock_id(1);
        updateDTO.setPortfolio_id(1);
        updateDTO.setUser_id(1);
        updateDTO.setType("SELL");
        updateDTO.setQty(8);
        updateDTO.setPrice(155.0);
        updateDTO.setTimestamp_t(timestamp);

        Transaction updatedTransaction = new Transaction();
        updatedTransaction.setT_id(1);
        updatedTransaction.setStock_id(1);
        updatedTransaction.setPortfolio_id(1);
        updatedTransaction.setUser_id(1);
        updatedTransaction.setType("SELL");
        updatedTransaction.setQty(8);
        updatedTransaction.setPrice(155.0);
        updatedTransaction.setTimestamp_t(timestamp);

        when(transactionRepo.findById(1)).thenReturn(Optional.of(transaction));
        when(transactionRepo.save(any(Transaction.class))).thenReturn(updatedTransaction);

        // Act
        TransactionDTO result = transactionService.updateTransaction(1, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals("SELL", result.getType());
        assertEquals(8, result.getQty());
        assertEquals(155.0, result.getPrice());
        verify(transactionRepo, times(1)).findById(1);
        verify(transactionRepo, times(1)).save(any(Transaction.class));
    }

    @Test
    void testUpdateTransaction_NotFound() {
        // Arrange
        when(transactionRepo.findById(999)).thenReturn(Optional.empty());

        // Act
        TransactionDTO result = transactionService.updateTransaction(999, transactionDTO);

        // Assert
        assertNull(result);
        verify(transactionRepo, times(1)).findById(999);
        verify(transactionRepo, never()).save(any(Transaction.class));
    }

    @Test
    void testDeleteTransaction_Success() {
        // Arrange
        when(transactionRepo.findById(1)).thenReturn(Optional.of(transaction));
        doNothing().when(transactionRepo).delete(any(Transaction.class));

        // Act
        String result = transactionService.deleteTransaction(1);

        // Assert
        assertNotNull(result);
        assertEquals("Transaction deleted", result);
        verify(transactionRepo, times(1)).findById(1);
        verify(transactionRepo, times(1)).delete(transaction);
    }

    @Test
    void testDeleteTransaction_NotFound() {
        // Arrange
        when(transactionRepo.findById(999)).thenReturn(Optional.empty());

        // Act
        String result = transactionService.deleteTransaction(999);

        // Assert
        assertNull(result);
        verify(transactionRepo, times(1)).findById(999);
        verify(transactionRepo, never()).delete(any(Transaction.class));
    }

    @Test
    void testAddTransaction_WithDifferentStockAndPortfolio() {
        // Arrange
        TransactionDTO multiDTO = new TransactionDTO();
        multiDTO.setStock_id(5);
        multiDTO.setPortfolio_id(3);
        multiDTO.setUser_id(2);
        multiDTO.setType("BUY");
        multiDTO.setQty(20);
        multiDTO.setPrice(200.0);
        multiDTO.setTimestamp_t(timestamp);

        Transaction multiTransaction = new Transaction();
        multiTransaction.setT_id(3);
        multiTransaction.setStock_id(5);
        multiTransaction.setPortfolio_id(3);
        multiTransaction.setUser_id(2);
        multiTransaction.setType("BUY");
        multiTransaction.setQty(20);
        multiTransaction.setPrice(200.0);
        multiTransaction.setTimestamp_t(timestamp);

        when(transactionRepo.save(any(Transaction.class))).thenReturn(multiTransaction);

        // Act
        TransactionDTO result = transactionService.addTransaction(multiDTO);

        // Assert
        assertNotNull(result);
        assertEquals(5, result.getStock_id());
        assertEquals(3, result.getPortfolio_id());
        assertEquals(2, result.getUser_id());
        assertEquals(20, result.getQty());
        verify(transactionRepo, times(1)).save(any(Transaction.class));
    }

    @Test
    void testUpdateTransaction_ChangeQtyAndPrice() {
        // Arrange
        TransactionDTO updateDTO = new TransactionDTO();
        updateDTO.setStock_id(1);
        updateDTO.setPortfolio_id(1);
        updateDTO.setUser_id(1);
        updateDTO.setType("BUY");
        updateDTO.setQty(15);
        updateDTO.setPrice(145.0);
        updateDTO.setTimestamp_t(timestamp);

        Transaction updatedTransaction = new Transaction();
        updatedTransaction.setT_id(1);
        updatedTransaction.setStock_id(1);
        updatedTransaction.setPortfolio_id(1);
        updatedTransaction.setUser_id(1);
        updatedTransaction.setType("BUY");
        updatedTransaction.setQty(15);
        updatedTransaction.setPrice(145.0);
        updatedTransaction.setTimestamp_t(timestamp);

        when(transactionRepo.findById(1)).thenReturn(Optional.of(transaction));
        when(transactionRepo.save(any(Transaction.class))).thenReturn(updatedTransaction);

        // Act
        TransactionDTO result = transactionService.updateTransaction(1, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(15, result.getQty());
        assertEquals(145.0, result.getPrice());
        verify(transactionRepo, times(1)).save(any(Transaction.class));
>>>>>>> 323f0136fc4e141b74bb75a0c52ada2cc2f11153
    }
}
