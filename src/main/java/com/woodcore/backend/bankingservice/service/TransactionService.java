package com.woodcore.backend.bankingservice.service;

import com.woodcore.backend.bankingservice.dto.request.TransferRequest;
import com.woodcore.backend.bankingservice.model.Transaction;
import com.woodcore.backend.bankingservice.model.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;

public interface TransactionService {

    Transaction deposit(TransferRequest transferRequest);

    Transaction withdraw(TransferRequest transferRequest);

    Transaction transfer(TransferRequest transferRequest);

    Page<Transaction> getAllTransactions(String accountNumber, Pageable pageable);

    Page<Transaction> getAllTransactionsByTransactionType(String accountNumber, TransactionType type, Pageable pageable);
}
