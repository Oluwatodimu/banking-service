package com.woodcore.backend.bankingservice.repository;

import com.woodcore.backend.bankingservice.model.Account;
import com.woodcore.backend.bankingservice.model.Transaction;
import com.woodcore.backend.bankingservice.model.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    Page<Transaction> findAllByAccount(Account account, Pageable pageable);

    Page<Transaction> findAllByAccountAndType(Account account, TransactionType type, Pageable pageable);
}
