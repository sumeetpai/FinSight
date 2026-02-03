package com.FinSight_Backend.service;

import com.FinSight_Backend.dto.TransactionDTO;
import com.FinSight_Backend.repository.TransactionRepo;
import com.FinSight_Backend.model.Transaction;
import com.FinSight_Backend.repository.UserRepo;
import com.FinSight_Backend.model.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService{

    private TransactionRepo transactionRepo;
    private UserRepo userRepo;

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
        if (transactionDTO.getUser_ids() != null) {
            List<User> users = transactionDTO.getUser_ids().stream()
                    .map(id -> userRepo.findById(id).orElse(null))
                    .filter(u -> u != null)
                    .collect(Collectors.toList());
            transaction.setUsers(users);
        } else {
            transaction.setUsers(null);
        }
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
        transactionDTO.setUser_ids(savedTransaction.getUsers() != null ?
                savedTransaction.getUsers().stream().map(User::getUser_id).collect(Collectors.toList()) : null);
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