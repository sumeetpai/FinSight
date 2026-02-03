package com.FinSight_Backend.service;

import com.FinSight_Backend.dto.TransactionDTO;
import com.FinSight_Backend.repo.TransactionRepo;
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
        return getTransactionDTO(transactionDTO, transaction);

    }

    private TransactionDTO getTransactionDTO(TransactionDTO transactionDTO, Transaction transaction) {
        transaction.setPrice(transactionDTO.getPrice());
        transaction.setQty(transactionDTO.getQty());
        transaction.setStock_sym(transactionDTO.getStock_sym());
        transaction.setTimestamp_t(transactionDTO.getTimestamp_t());
        transaction.setType(transactionDTO.getType());
        transaction.setUsers(transactionDTO.getUsers());
        Transaction savedTransaction = transactionRepo.save(transaction);
        return getTransactionDTO(savedTransaction);
    }

    private TransactionDTO getTransactionDTO(Transaction savedTransaction) {
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setPrice(savedTransaction.getPrice());
        transactionDTO.setQty(savedTransaction.getQty());
        transactionDTO.setStock_sym(savedTransaction.getStock_sym());
        transactionDTO.setTimestamp_t(savedTransaction.getTimestamp_t());
        transactionDTO.setType(savedTransaction.getType());
        transactionDTO.setUsers(savedTransaction.getUsers());
        return transactionDTO;
    }

    @Override
    public TransactionDTO getTransaction(Integer t_id) {
        Transaction transaction = transactionRepo.findById(t_id).isPresent() ? transactionRepo.findById(t_id).get() : null;
        assert transaction != null;
        return getTransactionDTO(transaction);
    }

    @Override
    public TransactionDTO updateTransaction(Integer t_id, TransactionDTO transactionDTO) {
        Transaction transaction = transactionRepo.findById(t_id).isPresent() ? transactionRepo.findById(t_id).get() : null;
        assert transaction != null;
        return getTransactionDTO(transactionDTO, transaction);
    }

    @Override
    public String deleteTransaction(Integer t_id) {
        Transaction transaction = transactionRepo.findById(t_id).isPresent() ? transactionRepo.findById(t_id).get() : null;
        assert transaction != null;
        transactionRepo.delete(transaction);
        return "Transaction deleted";
    }
}