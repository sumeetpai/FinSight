package com.FinSight_Backend.service;

import com.FinSight_Backend.dto.TransactionDTO;
import com.FinSight_Backend.repository.TransactionRepo;
import com.FinSight_Backend.model.Transaction;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService{

    private TransactionRepo transactionRepo;

    @Override
    public TransactionDTO addTransaction(TransactionDTO transactionDTO) {
        Transaction transaction = new Transaction();
        return mapAndSave(transactionDTO, transaction);

    }

    private TransactionDTO mapAndSave(TransactionDTO transactionDTO, Transaction transaction) {
        transaction.setStock_id(transactionDTO.getStock_id());
        transaction.setPortfolio_id(transactionDTO.getPortfolio_id());
        transaction.setUser_id(transactionDTO.getUser_id());
        transaction.setType(transactionDTO.getType());
        transaction.setQty(transactionDTO.getQty());
        transaction.setPrice(transactionDTO.getPrice());
        transaction.setTimestamp_t(transactionDTO.getTimestamp_t());
        Transaction savedTransaction = transactionRepo.save(transaction);
        return toDTO(savedTransaction);
    }

    private TransactionDTO toDTO(Transaction savedTransaction) {
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setT_id(savedTransaction.getT_id());
        transactionDTO.setStock_id(savedTransaction.getStock_id());
        transactionDTO.setPortfolio_id(savedTransaction.getPortfolio_id());
        transactionDTO.setUser_id(savedTransaction.getUser_id());
        transactionDTO.setType(savedTransaction.getType());
        transactionDTO.setQty(savedTransaction.getQty());
        transactionDTO.setPrice(savedTransaction.getPrice());
        transactionDTO.setTimestamp_t(savedTransaction.getTimestamp_t());
        return transactionDTO;
    }

    @Override
    public TransactionDTO getTransaction(Integer t_id) {
        return transactionRepo.findById(t_id).map(this::toDTO).orElse(null);
    }

    @Override
    public TransactionDTO updateTransaction(Integer t_id, TransactionDTO transactionDTO) {
        return transactionRepo.findById(t_id).map(tx -> mapAndSave(transactionDTO, tx)).orElse(null);
    }

    @Override
    public String deleteTransaction(Integer t_id) {
        return transactionRepo.findById(t_id).map(tx -> { transactionRepo.delete(tx); return "Transaction deleted"; }).orElse(null);
    }
}