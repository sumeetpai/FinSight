package com.FinSight_Backend.service;

import com.FinSight_Backend.dto.TransactionDTO;
import com.FinSight_Backend.model.Transaction;
import com.FinSight_Backend.repository.TransactionRepo;
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
class TransactionServiceImplTest {

    @Mock
    private TransactionRepo transactionRepo;

    @InjectMocks
    private TransactionServiceImpl transactionService;

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
    }
}
