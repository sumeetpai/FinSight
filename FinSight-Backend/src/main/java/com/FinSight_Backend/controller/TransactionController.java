package com.FinSight_Backend.controller;
import com.FinSight_Backend.dto.TransactionDTO;
import com.FinSight_Backend.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/transaction/")
@AllArgsConstructor
public class TransactionController {
    private TransactionService transactionService;

    @PostMapping()
    public ResponseEntity<TransactionDTO> addTransaction(@RequestBody TransactionDTO transactionDTO) {
        // For creation, require stock_id, portfolio_id, user_id, and type
        if (transactionDTO == null || transactionDTO.getStock_id() == null || transactionDTO.getPortfolio_id() == null
                || transactionDTO.getUser_id() == null || transactionDTO.getType() == null || transactionDTO.getType().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        TransactionDTO savedTransaction = transactionService.addTransaction(transactionDTO);
        return new ResponseEntity<>(savedTransaction, HttpStatus.CREATED);
    }

    @GetMapping("{t_id}")
    public ResponseEntity<TransactionDTO> getTransaction(@PathVariable Integer t_id) {
        TransactionDTO transactionDTO = transactionService.getTransaction(t_id);
        if (transactionDTO == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(transactionDTO, HttpStatus.OK);
    }

    @PutMapping("{t_id}")
    public ResponseEntity<TransactionDTO> updateTransaction(@PathVariable Integer t_id, @RequestBody TransactionDTO transactionDTO) {
        TransactionDTO updatedTransaction = transactionService.updateTransaction(t_id, transactionDTO);
        if (updatedTransaction == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updatedTransaction, HttpStatus.OK);
    }

    @DeleteMapping("{t_id}")
    public ResponseEntity<String> deleteTransaction(@PathVariable Integer t_id) {
        String msg = transactionService.deleteTransaction(t_id);
        if (msg == null) {
            return new ResponseEntity<>("Transaction not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }

}