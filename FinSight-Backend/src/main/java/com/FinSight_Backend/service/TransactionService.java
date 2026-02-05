package com.FinSight_Backend.service;

import com.FinSight_Backend.dto.TransactionDTO;
import java.util.List;

public interface TransactionService {
    TransactionDTO addTransaction(TransactionDTO transactionDTO);
    TransactionDTO getTransaction(Integer t_id);
    TransactionDTO updateTransaction(Integer t_id, TransactionDTO transactionDTO);
    String deleteTransaction(Integer t_id);
    List<TransactionDTO> getTransactionsByPortfolioId(Integer portfolio_id);
}