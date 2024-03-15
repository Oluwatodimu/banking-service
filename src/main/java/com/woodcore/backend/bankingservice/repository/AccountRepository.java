package com.woodcore.backend.bankingservice.repository;

import com.woodcore.backend.bankingservice.model.Account;
import com.woodcore.backend.bankingservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findAllByUser(User user);
}
